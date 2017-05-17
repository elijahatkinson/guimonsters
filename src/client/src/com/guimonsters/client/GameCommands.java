package com.guimonsters.client;

import java.lang.reflect.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Master Class for Commands available to specific states
 * of gameplay. Collects and handles parsing and event dispatching for
 * all game commands.
 * 
 * @author Elijah Atkinson
 * @author Stephen Butler
 * @author Curran Hamilton
 * @version 1.00, 2013-04-18
 */
public class GameCommands {
	
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
	protected String promptString = "Welcome to the GUI_Monsters MUD client. Type 'Help' for a list of commands.";
	
	//Command description strings
	protected String DESCRIPTION_HELP = "Displays this Help message.";
	protected String DESCRIPTION_CLEAR = "Clears the game History.";
	protected String DESCRIPTION_LOGOUT = "Log out of the game.";
	
	//Command error strings
	protected String ERROR_HELP = "Proper usage is 'help'.";
	protected String ERROR_CLEAR = "Proper usage is 'clear'.";
	protected String ERROR_LOGOUT = "Proper usage is 'logout'.";
	
	//Stores all public methods of this class.
	protected Method[] commandMethods;
	//Maps each method in the commandMethods array to its method name.
	protected HashMap<String, Method> methodMap;
	//Maps each valid user input string to a command object.
	protected HashMap<String, Command> commandMap;
	//The master MudClient object.
	protected MudClient client;
	
	//If any commands are added to the commandMap
	//HashMap past 200, it will be rehashed.
	protected int commandMapInitialCapacity = 200;
	//This load factor has a good trade off between size and speed.
	protected float commandMapLoadFactor = 0.75f;
	
	
	/**
	 * Create a GameCommands instance that can be used
	 * to parse user input and dispatch the appropriate
	 * event. GameCommands assumes user input has been previously sanitized.
	 * @param client The master game client object used by commands. 
	 */
	public GameCommands(MudClient client) {
		
		//Set the client instance that these game commands are associated with.
		this.client = client;
		
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
		
		//Create game commands for each method that we want to map to.
		//------------------------------------------------------------
		Command printHelp = new Command(this, "printHelp",
				DESCRIPTION_HELP, ERROR_HELP, methodMap.get("printHelp"));
		
		Command clearGameLog = new Command(this, "clearGameLog",
				DESCRIPTION_CLEAR, ERROR_CLEAR, methodMap.get("clearGameLog"));
		
		//Map command strings to game command methods.
		//-------------------------------------------------------------
		commandMap.put("help", printHelp);
		commandMap.put("clear", clearGameLog);
	}
	
	
	/**
	 * Check the command HashMap for the command string matching user input.
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
		
		if(c.isEmpty()) {
			results = this.emptyCommandString;
		}
		else {
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
	//Game Command Public Methods.
	//--------------------------------------
	//Each of these methods should map to a valid game command!
	//Start your Javadoc for each method by listing which command string
	//method maps to.
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
	 * Clears the main Game Log.
	 * Adds the prompt for the current state to the gameLog.
	 */
	public void clearGameLog() {
		this.client.getGameLog().clear();
		this.prompt();
	}
	
	/**
	 * Print the default prompt for the current game state
	 * to the game log.
	 */
	public void prompt() {
		this.client.getGameLog().add(this.promptString);
	}

	
	public HashMap<String, Method> getMethodMap() {
		return methodMap;
	}


	public HashMap<String, Command> getCommandMap() {
		return commandMap;
	}
	
	
}
