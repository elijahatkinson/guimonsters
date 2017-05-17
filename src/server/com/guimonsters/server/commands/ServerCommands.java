package com.guimonsters.server.commands;

import java.lang.reflect.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.guimonsters.network.Directive;
import com.guimonsters.network.Packet;
import com.guimonsters.server.MudServerThread;
import com.guimonsters.server.ServerState;
import com.guimonsters.server.game.Account;

/**
 * Master Class for Commands available to the MudServer.
 * Collects and handles parsing and execution of commands
 * sent to the server.
 * 
 * @author Elijah Atkinson
 * @version 1.00, 2013-04-28
 */
public class ServerCommands {
	
	//Data Fields
	
	//Defines syntax for expected areas of arguments in output strings.
	protected final String ARG0 = "<<{{arg00}}>>";
	protected final String ARG1 = "<<{{arg01}}>>";
	protected final String ARG2 = "<<{{arg02}}>>";
	protected final String ARG3 = "<<{{arg03}}>>";
	
	//Message returned when the given command is not in the HashMap.
	protected String parseFailString = "I don't know what "+ARG0+" means.";
	//Message returned when the given command is empty.
	protected String emptyCommandString = "You'll have to speak up!";
	
	//The Default master prompt string. Child classes will override this.
	protected String promptString;
	
	//Command description strings
	protected String DESCRIPTION_HELP = "Displays this help message.";
	protected String DESCRIPTION_CLEAR = "Clears the game history.";
	protected String DESCRIPTION_LOGOUT = "Log out of the game.";
	protected String DESCRIPTION_LOGIN = "Logs a user into the game.";
	protected String DESCRIPTION_CREATE_ACCOUNT = "Creates a new server account.";
	
	//Command error strings
	protected String ERROR_HELP = "Proper usage is 'help'.";
	protected String ERROR_CLEAR = "Proper usage is 'clear'.";
	protected String ERROR_LOGOUT = "Proper usage is 'logout'.";
	protected String ERROR_CREATE_ACCOUNT = "Proper usage is 'create_account [account name] [password]'.";
	protected String ERROR_LOGIN = "Proper usage is 'login [account name] [password]'.";
	protected String ERROR_LOGIN_ACCOUNT_LOGGED_IN = "That account is already logged in.";
	protected String ERROR_LOGIN_FAILED = "Login failed. Please check your account name and/or password.";
	protected String LOGIN_ACCOUNT_SUCCESS = "Logging in.";
	
	//Stores all public methods of this class.
	protected Method[] commandMethods;
	//Maps each method in the commandMethods array to its method name.
	protected HashMap<String, Method> methodMap;
	//Maps each valid user input string to a command object.
	protected HashMap<String, Command> commandMap;
	//The master MudClient object.
	protected MudServerThread serverThread;
	
	//If any commands are added to the commandMap
	//HashMap past 200, it will be rehashed.
	protected int commandMapInitialCapacity = 200;
	//This load factor has a good trade off between size and speed.
	protected float commandMapLoadFactor = 0.75f;
	
	
	/**
	 * Create a ServerCommands instance that can be used
	 * to parse client input and execute the appropriate
	 * function.
	 * @param thread The server thread managing the connection to the client. 
	 */
	public ServerCommands(MudServerThread thread) {
		
		//Set the server thread instance that these game commands are associated with.
		this.serverThread = thread;
		
		//Create a new HashMap to contain all game commands.
		commandMap = new HashMap<String, Command>(commandMapInitialCapacity,
								 				  commandMapLoadFactor);
		
		//Create a new HashMap to contain all public command methods that are
		//defined in this class. Index command methods by method name.
		methodMap = new HashMap<String, Method>();

		//Get an array of all public methods available to GameCommands.
		//These methods will be mapped to command strings.
		commandMethods = this.getClass().getMethods();
		
		//Walk through all command methods and add them to the method map.
		for(int i=0; i<commandMethods.length; i++) {
			methodMap.put(commandMethods[i].getName(), commandMethods[i]);
		}
		
		//Create server commands for each method that we want to map to.
		//------------------------------------------------------------
		Command printHelp = new Command(this, "printHelp",
				DESCRIPTION_HELP, ERROR_HELP, methodMap.get("printHelp"));
		
		Command clearGameLog = new Command(this, "clearGameLog",
				DESCRIPTION_CLEAR, ERROR_CLEAR, methodMap.get("clearGameLog"));
		
		Command logOut = new Command(this, "logOut", DESCRIPTION_LOGOUT, 
				ERROR_LOGOUT, methodMap.get("logOut"));
		
		Command createAccount = new Command(this, "createAccount",
				DESCRIPTION_CREATE_ACCOUNT, ERROR_CREATE_ACCOUNT,
				methodMap.get("createAccount"));
		
		Command logIn = new Command(this, "logIn", DESCRIPTION_LOGIN, 
				ERROR_LOGIN, methodMap.get("logIn"));
		
		//Map command strings to server command methods.
		//-------------------------------------------------------------
		commandMap.put("help", printHelp);
		commandMap.put("clear", clearGameLog);
		commandMap.put("logout", logOut);
		commandMap.put("create_account", createAccount);
		commandMap.put("login", logIn);
	}
	
	/**
	 * Check the command HashMap for the command string matching client input.
	 * If found, execute the function mapped to the input string and
	 * return the result.
	 * @param c The String input to attempt to parse and execute.
	 * @return results The String returned by the function mapped to the given
	 *     input string.  Results is null if no function was found for the
	 *     input string or if the function failed to execute.
	 */
	public String parseCommand(String c) {
		String results = "";
		String commandString = c;
		String paramString = "";
		
		if(!c.isEmpty()) {
			//If the string contains a space, then we need to parse user arguments.
			if(c.contains(" ")) {
				//Everything before the first space is the command.
				commandString = c.substring(0, c.indexOf(' '));
				//Everything after the first space is the parameter string.
				paramString = c.substring(c.indexOf(' ')+1);
				//Trim leading and trailing spaces from param string.
				paramString = paramString.trim();
			}
			
			//Cast the command string to lower case before 
			//checking the commandMap for it.
			commandString = commandString.toLowerCase();
			
			//Search the hashMap for a function matching the command.
			Command userCommand = commandMap.get(commandString);
		
			//If the command did not match a function, return the error string.
			if(userCommand == null) {
				results = this.parseFailString.replace(ARG0, "'"+commandString+"'");
			}
			//Otherwise, attempt execute the function mapped to the command
			else {
				results = userCommand.execute(paramString);
			}
		}
		return results;
	}
	
	/**
	 * Send the default prompt for the current game state
	 * to the client.
	 */
	public void prompt() {
		
		boolean success = serverThread.write(this.promptString);
		
		//If write fails, print error to server screen
		if(!success) {
			serverThread.println("Write to client@"+serverThread.getClientAddress()+" failed.");
		}
	}

	//========================================================================
	//Server Command Public Methods.
	//--------------------------------------
	//Each of these methods should map to a valid server command!
	//Start your Javadoc for each method by listing which command string
	//the method maps to.
	//Ex: Responds to the user command 'help'.
	//========================================================================
	
	/**
	 * Responds to the user command 'help'.
	 * Iterate through the HashMap of game commands and print
	 * description field of every game command.
	 * @return helpStr The String containing the help message.
	 */
	public String printHelp() {
		String results = "These are all of the available Game Commands:";
		
		//Create an iterator to walk through the entire commandMap.
		Iterator<Map.Entry<String, Command>> entries = commandMap.entrySet().iterator();
		
		//Loop through every command in the command map and build up the
		//help string from the command descriptions.
		while(entries.hasNext()) {
			Map.Entry<String, Command> entry = entries.next();
			//Build up the help string in the format \n command => command description.
			results += "\n    "+entry.getKey()+" => "+entry.getValue().getDescription();
		}
		
		return results;
	}
	
	/**
	 * Responds to the user command 'clear'.
	 * Sends the current thread state prompt to the client with the
	 * directive to clear the game log.
	 */
	public void clearGameLog() {
		Packet clearPacket = new Packet(this.promptString, Directive.CLEAR_LOG);
		serverThread.write(clearPacket);
	}
	
	/**
	 * Logs the user out of the game and results in termination of the server thread.
	 */
	public void logOut() {
		serverThread.logOut("Goodbye!");
	}
	
	/**
	 * Maps to the client command 'create_account [account name] [password]'.
	 * Creates an account file if it doesn't already exist.
	 * If account is created successfully, the user is logged in with it.
	 * Otherwise, a failure packet (null packet) is written to the client.
	 * @param args The arguments from the client.
	 */
	public void createAccount(String args) {
		//Parse argument string for expected account parameters.
		String params[] = args.split(" ");
		//Create an account object with with given parameters.
		Account user = new Account(params[0], params[1]);
		
		if(user.create()) {
			//Log account creation.
			serverThread.println("Account file created for "+user.getAccountName()+". New account logging in.");
			
			//Set the server's user account object to this new account.
			serverThread.setUser(user);
			
			//Write an account creation success packet to client. 
			Packet successPacket = new Packet(null, Directive.SERV_CREATE_ACCOUNT);
			serverThread.write(successPacket);
			
			//Switch the server state to logged in.
			//This writes the lobby welcome message to the client.
			serverThread.switchServerState(ServerState.LOGGED_IN);
		}
		else {
			//Print account creation failed message.
			serverThread.println("Failed to create account '"+user.getAccountName()+"'. Account name already in use.");
			
			//Write a null packet to client to indicate failure
			Packet response = new Packet(null);
			serverThread.write(response);
			
			//Close the connection because account creation failed.
			serverThread.getClientConnection().disconnect();
		}
	}
	
	/**
	 * Maps to the client command 'login [account name] [password]'.
	 * Logs the user into the game. Returns error strings to the client
	 * if the user is already logged in, or if the login credentials are invalid.
	 * @param args The arguments from the client.
	 */
	public void logIn(String args) {
		
		//Parse argument string for expected account parameters.
		String params[] = args.split(" ");
		//Create an account object with with given parameters.
		Account user = new Account(params[0], params[1]);
		
		//Print attempted connection method.
		serverThread.println(user.getAccountName()+" is connecting from "+serverThread.getClientAddress());
		
		//Get all accounts currently logged into the server.
		HashMap<String, Account> loggedInAccounts = serverThread.getLoggedInAccounts();
		
		//If the user supplied account is in loggedInAccounts, then the user supplied account
		//is already logged in.
		if(loggedInAccounts.containsKey(user.getAccountName().toLowerCase())) {
			
			serverThread.println(user.getAccountName()+" failed to connect. Account is already connected.");
			
			//Write a login failed packet to the client.
			Packet response = new Packet(ERROR_LOGIN_ACCOUNT_LOGGED_IN, Directive.LOGIN_FALSE);
			serverThread.write(response);
			
			//Close this connection because the login attempt failed.
			serverThread.getClientConnection().disconnect();
		}
		else {
			
			//Attempt to log the user in.
			if(user.logIn()) {
				
				//Print connection success message.
				serverThread.println(user.getAccountName()+" has connected.");
				
				//Set the server's user account object.
				serverThread.setUser(user);
				
				//Write login success packet to the client
				Packet response = new Packet(LOGIN_ACCOUNT_SUCCESS, Directive.LOGIN_TRUE);
				serverThread.write(response);
				
				//Switch the server state to logged in.
				//This writes the lobby welcome message to the client.
				serverThread.switchServerState(ServerState.LOGGED_IN);
			}
			//User login failed.
			else {
				//Print connection failed message.
				serverThread.println(user.getAccountName()+" failed to connect.");
				
				//Write a login failed packet to the client to indicate failed login.
				Packet response = new Packet(ERROR_LOGIN_FAILED, Directive.LOGIN_FALSE);
				serverThread.write(response);
				
				//Close the connection because the login attempt failed.
				serverThread.getClientConnection().disconnect();
			}
		}
	}
}
