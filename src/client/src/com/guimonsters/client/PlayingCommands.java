package com.guimonsters.client;

/**
 * Child of GameCommands. Used to define Commands
 * available to the PLAYING game state.
 * 
 * @author Elijah Atkinson
 * @author Stephen Butler
 * @author Curran Hamilton
 * @version 1.00, 2013-04-18
 */
public class PlayingCommands extends GameCommands {
	
	//Data Field
	
	private String promptString = "Welcome to the main game area! There is not currently much to do here... "+
			" Type 'help' to view a list of commands.";
	
	//Command Description strings
	private static final String DESCRIPTION_STRONG_SILENT = "For when you want to look tough.";
	private static final String DESCRIPTION_SAY = "Say something out loud.";
	
	//Command error strings
	private static final String ERROR_STRONG_SILENT = "Proper usage is '...'.";
	private static final String ERROR_SAY = "What do you want to say?";
	
	/**
	 * Create a PlayingCommands instance that can be used
	 * to parse user input and dispatch the appropriate
	 * event from the PLAYING state of the game.
	 * Assumes user input has been previously sanitized.
	 */
	public PlayingCommands(MudClient client) {
		super(client);
		
		//Override parent strings here.
		super.promptString = this.promptString;
		
		//Main State commands
		Command strongSilentType = new Command(this, "strongSilentType",
				DESCRIPTION_STRONG_SILENT, ERROR_STRONG_SILENT,
				methodMap.get("strongSilentType"));
		
		Command say = new Command(this, "say", DESCRIPTION_SAY, ERROR_SAY,
				methodMap.get("say"));
		
		//Add the state command objects to the command hash map.
		this.commandMap.put("...", strongSilentType);
		this.commandMap.put("/s", say);
		this.commandMap.put("say", say);
		
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
	 * Maps to the user command '...'.
	 * Makes the game recognize your studlyness.
	 * @return results String The response string to print to the game log.
	 */
	public String strongSilentType() {
		String results = "You're looking pretty tough there.  Let me see those muscles!";
		return results;
	}
	
	/**
	 * Maps to the user commands '/s <message>' or 'say <message>'.
	 * Prints:
	 * You say 'user message here'
	 * To the game log.  Eventually output of this command
	 * will be visible to the whole game room.
	 * @param message The String to "say".
	 * @return results The String of command results to print to the game log.
	 */
	public String say(String message) {
		//TODO Modify say command so the message string is visible to anyone in the room with the player.
		String results = "You say '"+message+"'";
		return results;
	}
}