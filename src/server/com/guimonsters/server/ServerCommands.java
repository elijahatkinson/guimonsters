package com.guimonsters.server;

import java.lang.reflect.*;
import java.util.HashMap;

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
	protected String parseFailString = "Unrecognized command: "+ARG0+" received from client.";
	
	//Command description strings
	protected String DESCRIPTION_LOGOUT = "Log out of the game.";
	protected String DESCRIPTION_LOGIN = "Logs a user into the game.";
	
	//Command error strings
	protected String ERROR_LOGOUT = "Proper usage is 'logout'.";
	protected String ERROR_LOGIN = "Proper usage is 'login [account name] [password]'.";
	
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
	 * @param client The master server thread object used by commands. 
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
		Command logOut = new Command(this, "logOut", DESCRIPTION_LOGOUT, 
				ERROR_LOGOUT, methodMap.get("logOut"));
		
		Command logIn = new Command(this, "logIn", DESCRIPTION_LOGIN, 
				ERROR_LOGIN, methodMap.get("logIn"));
		
		//Map command strings to server command methods.
		//-------------------------------------------------------------
		commandMap.put("logout", logOut);
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
				if(paramString.isEmpty()) {
					//Execute the command without parameter string.
					//Results will be null if the command fails
					//or if the return type of the command is void.
					results = userCommand.execute();
				}
				else {
					//Execute the command with parameter string.
					//Results will be null if the command fails
					//or if the return type of the command is void.
					results = userCommand.execute(paramString);
				}
			}
		}
		return results;
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
	 * Logs the user out of the game and results in termination of the server thread.
	 */
	public String logOut() {
		serverThread.println("Client@"+serverThread.getClientAddress()+" is logging out.");
		return "Goodbye!";
	}
	
	/**
	 * Logs the user into the game
	 * @param args The argument parameter to parse.
	 * @return results The success flag of the attempted log in.
	 */
	public String logIn(String args) {
		//Parse argument string for expected parameters.
		String params[] = args.split(" ");
		String username = params[0];
		String password = params[1];
		String response = "";
		
		//Print attempted connection method.
		serverThread.println("User "+username+"@"+serverThread.getClientAddress()+" is attempting to connect...");

		//TODO implement a way to store valid accounts and check given username/password against them.
		if(username.equalsIgnoreCase("team1") && password.equals("*unicorn99")) {
			
			//Print connection success message.
			serverThread.println(username+" has connected.");
			
			response = "username="+username+"&login=true";
		}
		else {
			//Print connection failed message.
			serverThread.println(username+" failed to connect.");
			
			//If the user information is not valid, return the terminate string
			//end the connection.
			response = "disconnect";
		}
		
		return response;
	}
}
