package com.guimonsters.client;

/**
 * Child of GameCommands. Used to define Commands
 * available to the LOGGED_IN game state.
 * 
 * @author Elijah Atkinson
 * @author Stephen Butler
 * @author Curran Hamilton
 * @version 1.00, 2013-04-18
 */
public class LoggedInCommands extends GameCommands {
	
	//Data Field
	
	private String promptString = "Welcome to the Main Account Lobby! "+
			"From here you will eventually be able to create, select, and delete characters. "+
			"Type 'help' to view a list of commands.";
	
	//Command Description strings
	private static final String DESCRIPTION_PLAY = "Move to the playing gamestate. This is currently a stub command.";
	
	//Command error strings
	private static final String ERROR_PLAY = "Proper usage is 'play'.";
	
	/**
	 * Create a LoggedInCommands instance that can be used
	 * to parse user input and dispatch the appropriate
	 * event from the PLAYING state of the game.
	 * Assumes user input has been previously sanitized.
	 */
	public LoggedInCommands(MudClient client) {
		super(client);
		
		//Override parent strings here.
		super.promptString = this.promptString;
				
		//Main State commands
		Command play = new Command(this, "play",
				DESCRIPTION_PLAY, ERROR_PLAY,
				methodMap.get("play"));
		
		//Add the state command objects to the command hash map.
		this.commandMap.put("play", play);
		
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
	 * Maps to the 'play' user command.
	 * Currently a stub just to have some kind of functionality in the logged in state.
	 * Directs the user to the playing state. Eventually we want this done when the
	 * user selects a character.
	 */
	public void play() {
		this.client.getGameLog().add("Now Entering Game...");
		this.client.switchGameState(GameState.PLAYING);
	}
}