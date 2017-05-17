package com.guimonsters.client;

/**
 * Child of GameCommands. Used to define Commands
 * available to the LOGGED_OUT game state.
 * 
 * @author Elijah Atkinson
 * @author Stephen Butler
 * @author Curran Hamilton
 * @version 1.00, 2013-04-18
 */
public class LoggedOutCommands extends GameCommands {
	
	//Data Fields
	
	private String promptString = "You are currently logged out. Type 'login' to log in, or type "+
			"'help' for a list of commands.";
	
	//Command Description strings
	private static final String DESCRIPTION_LOG_IN = "Attempt to log in to a server.";
	private static final String DESCRIPTION_CREATE_ACCOUNT = "Create a new account.";
	
	//Command error strings
	private static final String ERROR_LOG_IN = "Proper usage is 'login'.";
	private static final String ERROR_CREATE_ACCOUNT = "";
	
	//Prompt strings
	private static final String PROMPT_LOGIN = "<< Welcome to the login menu. Please enter your account name / email address. >>";
	private static final String PROMPT_CREATE = "<< Welcome to the account creation Menu. " +
			"Please enter desired account name or email address. >>";
	
	/**
	 * Create a LoggedOutCommands instance that can be used
	 * to parse user input and dispatch the appropriate
	 * event from the PLAYING state of the game.
	 * Assumes user input has been previously sanitized.
	 */
	public LoggedOutCommands(MudClient client) {
		super(client);
		
		//Override parent strings here.
		super.promptString = this.promptString;
		
		//Main State commands
		Command createAccount = new Command(this, "createAccount",
				DESCRIPTION_CREATE_ACCOUNT, ERROR_CREATE_ACCOUNT,
				methodMap.get("createAccount"));
		
		Command logIn = new Command(this, "logIn",
				DESCRIPTION_LOG_IN, ERROR_LOG_IN,
				methodMap.get("logIn"));
		
		//Add the state command objects to the command hash map.
		this.commandMap.put("create", createAccount);
		this.commandMap.put("login", logIn);
		
		//Print this command object's prompt to the main game log.
		this.prompt();
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
	 * Maps to the 'create' user command.
	 * Prompt user for first part of account creation information and
	 * switch game state to the CREATING_ACCOUNT state.
	 */
	public void createAccount() {
		this.client.getGameLog().add(PROMPT_CREATE, false);
		this.client.switchGameState(GameState.CREATING_ACCOUNT);
	}
	
	/**
	 * Maps to the 'login' user command.
	 * Print the login prompt and user name prompt to the screen.
	 * Then switch the client into the LOGGING_IN state.
	 */
	public void logIn() {
		this.client.getGameLog().add(PROMPT_LOGIN, false);
		this.client.switchGameState(GameState.LOGGING_IN);
	}
}