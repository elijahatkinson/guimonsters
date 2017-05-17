package com.guimonsters.server;

import java.net.*;
import java.util.HashMap;
import java.util.Map;
import java.io.*;

import com.guimonsters.network.ClientConnection;
import com.guimonsters.network.Directive;
import com.guimonsters.network.Packet;
import com.guimonsters.server.ServerState;
import com.guimonsters.server.commands.LoggedInCommands;
import com.guimonsters.server.commands.PlayingCommands;
import com.guimonsters.server.commands.ServerCommands;
import com.guimonsters.server.game.Account;
import com.guimonsters.server.game.PlayerCharacter;
import com.guimonsters.server.game.Room;

/**
 * A basic server thread used to communicate
 * to a MudClient instance using a ClientConnection.
 * 
 * Each instance of MudServerThread will be started
 * up by the main MudServer ServerSocketThread when a client connects
 * to it.  Each MudServerThread instance is tied to
 * the character and account currently in use by the client
 * application.
 * 
 * @author Elijah Atkinson
 * @author Stephen Butler
 * @version 2.00, 2013-05-06
 */
public class MudServerThread extends Thread {
	
	//Data fields
	private ServerSocketThread parentThread;
	private Socket socket = null;
	private Long threadId;
	private String clientAddress;
	private ClientConnection clientConnection;
	private ServerCommands serverCommands;
	private ServerState state;
	private Account user;
	private PlayerCharacter player;

	//Construct a new MudServerThread instance and assign
	//a socket to it.
	public MudServerThread(ServerSocketThread parentThread, Socket socket) {
		super("MudServerThread");
		this.parentThread = parentThread;
		this.socket = socket;
		this.threadId = this.getId();
		this.clientAddress = socket.getInetAddress().getHostAddress();
		this.serverCommands = new ServerCommands(this);
		this.state = ServerState.LOGGED_OUT;
	}
	
	/**
	 * Main thread execution method.
	 */
	public void run() {
		try {
			//Add this thread to the parent thread's hash map for client threads.
			//This allows the parent thread and each instance of MudServerThread to communicate.
			this.parentThread.getClientThreads().put(this.threadId, this);
			
			//Display new connection message.
			this.println("Client connected from: "+clientAddress+
					".  New server thread created with ID: "+threadId+".");
			
			
			//Manage the connection to the client.
			clientConnection = new ClientConnection(socket);
	 
	        //Main server thread loop. Keep serving as long as the client is connected.
	        while (clientConnection.isConnected()) {
	        	try {
	        		//Get client input.
	        		Packet clientPacket = clientConnection.read();
	        		String clientCommand = clientPacket.getMessage().trim();
	        		Directive clientDirective = clientPacket.getDirective();
	        		
	        		//Parse the client input.
	        		String serverResponse = serverCommands.parseCommand(clientCommand);
	        		
	        		//Save the account file after every user command is executed
	        		//once the player is playing a character.
	        		if(this.isPlaying()) {
	        			this.user.save();
	        		}
	        		
	        		//Respond to the client if the results of command execution are
	        		//not empty and not null.
	        		if(serverResponse != null && !serverResponse.isEmpty()) {
		        		//Send results of command execution back to client.
		        		clientConnection.write(serverResponse);
			        	
		        		//If the response from the command matches the 
		        		//response from the logout function, or the terminate string then
		        		//terminate this thread.
				        if (serverResponse.equals("Goodbye!") || serverResponse.equals("disconnect")) {
				        	clientConnection.disconnect();
				        }
	        		}
	        	}
	        	catch(SocketException e) {
	        		this.println("The socket from "+clientAddress+" was unexpectedly closed.");
	        		clientConnection.disconnect();
	        	}
	        }
	        //Display thread termination message.
	        this.println("Connection from "+clientAddress+" was terminated. Stopping thread "+threadId+".");
	        
	        //Save the account before shutdown and print account save notification.
	        if(this.user != null) {
		        if(this.user.save()) {
		        	this.println("Save succeeded for account: "+this.user.getAccountName());
		        }
		        else {
		        	this.println("Save failed for account: "+this.user.getAccountName());
		        }
	        }
	        
	        //Before the thread ends, remove this player from the room they were in last.
	        if(this.player != null) {
	        	this.player.getCurrentRoom().removeCharacter(this.player);
	        }
	        
	        //Before the thread ends, remove this thread from the server's clientThread hash map.
	        this.parentThread.getClientThreads().remove(this.threadId);
	 
	    } catch (Exception e) {
	    	//Display any exceptions if they occur.
	        e.printStackTrace();
	    }
	}
	
	/**
	 * Send server shut down message to the client and terminate the thread.
	 */
	public void terminate() {
		this.logOut("The server has been shut down.");
	}
	
	/**
	 * Switch to a new server state.
	 * Updates the command set used by the server.
	 * @param state The ServerState to switch to.
	 */
	public void switchServerState(ServerState state) {
		//Update the game state to the new state.
		switch (state) {
			case LOGGED_OUT:
				this.state = state;
				this.serverCommands = new ServerCommands(this);
				break;
				
			case LOGGED_IN:
				this.state = state;
				this.serverCommands = new LoggedInCommands(this);
				break;
				
			case PLAYING:
				this.state = state;
				this.serverCommands = new PlayingCommands(this);
				break;
				
			default:
				//Don't do anything if any other state is given.
				break;
		}
	}
	
	/**
	 * Writes a user logging out message to the server log,
	 * writes a string to the client to notify them of disconnect,
	 * and logs the user out of the game.  This thread will then be allowed
	 * to terminate.
	 * @param message The String to write to the client to notify
	 * 				  them of the disconnection.
	 */
	public void logOut(String message) {
		this.println(this.user.getAccountName()+"@"+this.clientAddress+" is logging out.");
		//Create a packet with logout message and disconnect directive to send to the client.
		Packet logOutPacket = new Packet(message, Directive.DISCONNECT);
		//Send the packet to the client.
		this.write(logOutPacket);
		
		//Terminate the connection managed by the server thread.
		this.clientConnection.disconnect();
	}
	
	/**
	 * Broadcasts a message string to MudServerThreads that are in the PLAYING state
	 * and have threadIds different from this one.
	 * @param message The message String to send to other playing clients.
	 */
	public void broadcastPlaying(String message) {
		//Wrap the message in a packet with the message directive.
		Packet p = new Packet(message, Directive.MESSAGE);
		//Broadcast our message packet.
		this.broadcastPlaying(p);
	}
	
	/**
	 * Broadcasts a packet to MudServerThreads that are in the PLAYING state
	 * and have threadIds different from this one.
	 * @param message The Packet object to send to other playing clients.
	 */
	public void broadcastPlaying(Packet p) {
		
		//TODO write a version of this that only talks to the room.
		
		HashMap<Long, MudServerThread> playerThreads = getPlayerThreads();
		
		//If there are other players, send the message to them.
		if(playerThreads.size() > 0) {
			
			//Loop through every entry in the playerThreads map.
			for(Map.Entry<Long, MudServerThread> entry : playerThreads.entrySet()) {
				//Grab the thread from the hash map entry so we can broadcast to it.
				MudServerThread thread = entry.getValue();
				
				//Only broadcast to other running threads that are in the playing state.
				//Don't broadcast to this thread.
				if(thread.isAlive() && thread.isPlaying() && thread.getId() != this.threadId) {
					thread.write(p);
				}
			}
		}
	}
	
	/**
	 * Broadcasts a message string to all players in a room that have a threadID
	 * different from this one. (I.E. Don't broadcast to the calling player.)
	 * @param message The message String to send to other playing clients.
	 */
	public void broadcastRoom(Room r, String message) {
		//Wrap the message in a packet with the message directive.
		Packet p = new Packet(message, Directive.MESSAGE);
		//Broadcast our message packet.
		this.broadcastRoom(r, p);
	}
	
	/**
	 * Broadcasts a packet to all players in a room that have a threadID
	 * different from this one. (I.E. Don't broadcast to the calling player.)
	 * @param message The Room object to broadcast too.
	 * @param message The Packet object to send to other playing clients.
	 */
	public void broadcastRoom(Room r, Packet p) {
		
		HashMap<String, PlayerCharacter> roomCharacters = r.getCharacters();
		
		//If there are other players in the room, send the message to them.
		if(roomCharacters.size() > 1) {
			
			//Loop through every entry in the roomCharacters map.
			for(Map.Entry<String, PlayerCharacter> entry : roomCharacters.entrySet()) {
				
				PlayerCharacter player = entry.getValue();
				MudServerThread thread = player.getPlayerThread();
				
				//Only broadcast to other running threads that are in the playing state.
				//Don't broadcast to this thread.
				if(thread.isAlive() && thread.isPlaying() && thread.getId() != this.threadId) {
					thread.write(p);
				}
			}
		}
	}
	
	/**
	 * Write a message out to the client that is managed by this thread.
	 * @param message The string to write to the client.
	 * @return success The success status of the write.
	 */
	public boolean write(String message) {
		boolean success;
		try {
			this.clientConnection.write(message);
			success = true;
		} catch (IOException e) {
			success = false;
			e.printStackTrace();
		}
		return success;
	}
	
	/**
	 * Write a packet out to the client that is managed by this thread.
	 * @param p The packet to write to the client.
	 * @return success The success status of the write.
	 */
	public boolean write(Packet p) {
		boolean success;
		try {
			this.clientConnection.write(p);
			success = true;
		} catch (IOException e) {
			success = false;
			e.printStackTrace();
		}
		return success;
	}
	
	/**
	 * Read a packet from the client that is managed by this thread.
	 * @return results The packet read in from the client.
	 */
	public Packet read() {
		Packet results;
		try {
			results = this.clientConnection.read();
		} catch (IOException | ClassNotFoundException e) {
			results = null;
		}
		return results;
	}
	
	/**
	 * Output a string to the server console.
	 * Removes previous server prompt before printing and
	 * prints a new server prompt after printing.
	 * @param str The String to output to the server console.
	 */
	public void println(String str) {
		//Remove the previous server prompt characters before write.
		System.out.print("\b\b");
		//Write to the console.
		System.out.println(str);
		//Display a new server prompt.
		System.out.print(": ");
	}
	
	/**
	 * Returns whether or not the user connected to this thread
	 * has entered the game.
	 * @return The boolean flag representing whether the user connected
	 *         to this thread is 'playing' or not.
	 */
	public boolean isPlaying() {
		if(this.state.equals(ServerState.PLAYING)) {
			return true;
		}
		else {
			return false;
		}
	}
	
	/**
	 * Returns true if this thread is in the logged in state.
	 * Returns false otherwise.
	 * @return The boolean flag representing whether the user connected
	 *         to this thread is 'logged in' or not.
	 */
	public boolean isLoggedIn() {
		if(this.state.equals(ServerState.LOGGED_IN)) {
			return true;
		}
		else {
			return false;
		}
	}
	
	/**
	 * Returns true if this thread is in the logged out state.
	 * Returns false otherwise.
	 * @return The boolean flag representing whether the user connected
	 *         to this thread is 'logged out' or not.
	 */
	public boolean isLoggedOut() {
		if(this.state.equals(ServerState.LOGGED_OUT)) {
			return true;
		}
		else {
			return false;
		}
	}

	//Getters and setters
	//------------------------------
	
	/**
	 * Return a hash map containing all MudServerThreads that are currently on the server.
	 * @return playerThreads The hash map containing every other MudServerThread
	 *     currently running on the server mapped to their individual thread id.
	 */
	public HashMap<Long, MudServerThread> getPlayerThreads() {
		//Get a map of all client threads from this thread's parent thread.
		HashMap<Long, MudServerThread> playerThreads = this.parentThread.getClientThreads();
		
		return playerThreads;
	}
	
	/**
	 * Return a hash map containing all Account objects currently
	 * logged in to the server mapped to their account name string.
	 * @return playerAccounts The hash map containing every logged in player Account.
	 */
	public HashMap<String, Account> getLoggedInAccounts() {
				
		HashMap<Long, MudServerThread> playerThreads = this.parentThread.getClientThreads();
		HashMap<String, Account> accountMap = new HashMap<String, Account>(playerThreads.size());
		
		//If we have at least 1 logged in user, continue.
		if(playerThreads.size() > 0) {
					
			//Walk through all player threads and get the account object from each one.
			for(Map.Entry<Long, MudServerThread> entry : playerThreads.entrySet()) {
				
				MudServerThread thread = entry.getValue();
				
				//Only continue if the thread is not in the logged out state.
				if(!thread.isLoggedOut()) {
				
					//Get the account object from the player thread and add it to the account map.
					Account account = thread.getUser();
					accountMap.put(account.getAccountName().toLowerCase(), account);
				}
			}
		}
		
		return accountMap;
	}
	
	/**
	 * Returns a hash map of PlayerCharacter objects for all OTHER players that are currently playing.
	 * @return playerMap The hash map containing all OTHER PlayerCharacter objects besides
	 * 					 the one associated with this thread.
	 */
	public HashMap<String, PlayerCharacter> getPlayerCharacters() {
		HashMap<Long, MudServerThread> playerThreads = this.parentThread.getClientThreads();
		HashMap<String, PlayerCharacter> playerMap = new HashMap<String, PlayerCharacter>(playerThreads.size());
		
		//Walk through all player threads and get the player object from each one.
		for(Map.Entry<Long, MudServerThread> entry : playerThreads.entrySet()) {
			
			MudServerThread thread = entry.getValue();
			
			//Player objects only exist when the thread is in the playing state.
			if(thread.isPlaying()) {
			
				PlayerCharacter player = entry.getValue().getPlayer();
				
				//If the player name from the map doesn't match this thread's player name,
				//add it to player map.
				if(!this.player.getName().equals(player.getName())) {
					playerMap.put(player.getName().toLowerCase(), player);
				}
			}
		}
		
		return playerMap;
	}

	public String getClientAddress() {
		return clientAddress;
	}

	public ClientConnection getClientConnection() {
		return clientConnection;
	}

	public Account getUser() {
		return user;
	}

	public void setUser(Account user) {
		this.user = user;
	}
	
	public ServerSocketThread getParentThread() {
		return this.parentThread;
	}

	public PlayerCharacter getPlayer() {
		return player;
	}
	
	public void setPlayer(PlayerCharacter player) {
		this.player = player;
	}
	
}
