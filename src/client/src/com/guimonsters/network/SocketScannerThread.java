package com.guimonsters.network;

import com.guimonsters.client.GameLog;
import com.guimonsters.client.GameState;
import com.guimonsters.client.MudClient;

/**
 * Continuously scans the server socket for input and responds accordingly.
 * Blocks if no input from the server is available.
 * 
 * @author Elijah Atkinson
 * @version 2.00, 2013-05-04
 */
public class SocketScannerThread extends Thread {

	//Data fields
	private MudClient client;
	private ServerConnection sc;
    private GameLog gameLog;
    private Packet response;
    private String serverMessage;
    private Directive serverDirective;
    private boolean scanning;
    
    /**
     * Create a new socket scanner thread and begin listening
     * for input from the server.
     * 
     * @param client The MudClient object used by the thread to perform actions.
     */
	public SocketScannerThread(MudClient client) {
		this.gameLog = client.getGameLog();
		this.client = client;
		this.sc = client.getServerConnection();
		this.scanning = true;
	}
	
	/**
	 * Scan for server input. Will block until input comes in.
	 * Displays messages from server and acts on directives from server.
	 */
	public void run() {
		//Scan for input until terminated.
		while(scanning) {
			//Request a response packet from the server.
			//This request Will block the thread if no input is available.
			response = this.sc.read();
			
			//Only attempt to process non null results from server.
			if(response != null) {
				//Split the response packet into message and directive.
				serverMessage = response.getMessage();
				serverDirective = response.getDirective();
				
				//Check server directives and respond accordingly.
				switch(serverDirective) {
					//Server sent the disconnect directive:
					//display the disconnect message, terminate connection
					//and switch to logged_out state.
					case DISCONNECT:
						//Write the message from the server.
						this.displayMessage(serverMessage);
						//Server has closed its connection,
						//so close the client connection.
						this.logOut();
						this.scanning = false;
						break;
						
					//Server sent the clear log directive:
					//clear the game log and display the prompt
					//string from the server.
					case CLEAR_LOG:
						//clear the game log
						gameLog.clear();
						//Display the prompt message of the server state.
						this.displayMessage(serverMessage);
						break;
					
					//Server has issued a multi-part prompt to the client.
					case PROMPT:
						//Display the prompt message as a prompt string.
						this.displayPrompt(serverMessage);
						break;
					
					//Server has sent a chat string. Display it in the proper format.
					case CHAT:
						this.displayChat(serverMessage);
						break;
					//For any other case, attempt to display the server
					//response message if it exists.
					default:
						this.displayMessage(serverMessage);
						break;
				}
			}
		}
	}
	
	/**
	 * Stop scanning the server socket for input and allow this thread to end.
	 */
	public void terminate() {
		this.scanning = false;
	}
	
	/**
	 * Write a non-empty, non-null string to the game log.
	 * @param message The String to write to the game log.
	 */
	private void displayMessage(String message) {
		if(message != null && !message.isEmpty()) {
			gameLog.add(message);
		}
	}
	
	/**
	 * Write a non-empty, non-null string to the game log
	 * as a prompt.
	 * @param message The prompt String to write to the game log.
	 */
	private void displayPrompt(String prompt) {
		if(prompt != null && !prompt.isEmpty()) {
			//Add the prompt string to the gamelog without brackets.
			gameLog.add(prompt, false);
		}
	}
	
	/**
	 * Write a non-empty, non-null string to the game log
	 * as a chat string.
	 * @param message The chat String to write to the game log.
	 */
	private void displayChat(String message) {
		if(message != null && !message.isEmpty()) {
			//Add the chat message to the gameLog without brackets.
			gameLog.add(message, false);
		}
	}
	
	/**
	 * Close the connection with the server
	 * and return to the LOGGED_OUT state.
	 */
	private void logOut() {
		//Close the client's current connection with the server.
		client.getServerConnection().close();
		//Switch back to the logged out state.
		client.switchGameState(GameState.LOGGED_OUT);
	}
}
