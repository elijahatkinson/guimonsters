package com.guimonsters.server.commands;

import java.util.HashMap;

import com.guimonsters.server.MudServerThread;
import com.guimonsters.server.ServerState;
import com.guimonsters.server.game.GameWorld;
import com.guimonsters.server.game.PlayerCharacter;
import com.guimonsters.server.game.Room;
import com.guimonsters.server.prompt.CreateCharacterPrompt;
import com.guimonsters.server.prompt.Prompt;

/**
 * Child of GameCommands. Used to define Commands
 * available to the LOGGED_IN game state.
 * 
 * @author Elijah Atkinson
 * @author Stephen Butler
 * @author Curran Hamilton
 * @version 1.00, 2013-04-18
 */
public class LoggedInCommands extends ServerCommands {
	
	//Data Field
	
	private String promptString = "Welcome to the account lobby! "+
			"From here you can create, view, and play characters. " +
			"Type 'help' to view a list of commands.";
	
	//Command Description strings
	private static final String DESCRIPTION_PLAY = "Play a character.";
	private static final String DESCRIPTION_CREATE = "Create a new character.";
	private static final String DESCRIPTION_VIEW_CHARACTERS = "View a listing of your characters.";
	
	//Command error strings
	private static final String ERROR_PLAY = "You must specify a character to play. Proper usage is 'play [character name]'.";
	private static final String ERROR_CREATE = "Proper usage is 'create'.";
	private static final String ERROR_VIEW_CHARACTERS = "Proper usage is 'characters'.";
	private static final String ERROR_PLAYER_CHARACTER_NOT_FOUND = "You do not have a character with that name.";
	
	private GameWorld world;

	/**
	 * Create a LoggedInCommands instance that can be used
	 * to parse user input and dispatch the appropriate
	 * event from the PLAYING state of the game.
	 * Assumes user input has been previously sanitized.
	 */
	public LoggedInCommands(MudServerThread thread) {
		super(thread);
		
		//Override parent strings here.
		super.promptString = this.promptString;
				
		//Main State commands
		Command create = new Command(this, "create",
				DESCRIPTION_CREATE, ERROR_CREATE, methodMap.get("create"));
		
		Command viewCharacters = new Command(this, "viewCharacters",
				DESCRIPTION_VIEW_CHARACTERS, ERROR_VIEW_CHARACTERS,
				methodMap.get("viewCharacters"));
		
		Command play = new Command(this, "play",
				DESCRIPTION_PLAY, ERROR_PLAY, methodMap.get("play"));
		
		//Remove the login command from the map since user is already logged in.
		this.commandMap.remove("login");
		//Remove create account from command map since we're already logged in.
		this.commandMap.remove("create_account");
		
		//Add the state command objects to the command hash map.
		this.commandMap.put("create", create);
		this.commandMap.put("characters", viewCharacters);
		this.commandMap.put("play", play);
		
		//Print this command object's prompt to the main game log.
		this.prompt();
		
		//Get a pointer to the game world.
		world = serverThread.getParentThread().getWorld();
	}
	
	//========================================================================
	//Public Command Methods.
	//--------------------------------------
	//Each of these methods should map to a valid game command!
	//Start your Javadoc for each method by listing which command string
	//method maps to.
	//Ex: Responds to the user command 'help'.
	//========================================================================
	
	/**
	 * Maps to the 'create' command.
	 * Prompts the user for multi-part character information.
	 * When all information has been entered, creates a new character
	 * and adds it to the user's account.
	 * @return results The string describing the results of character creation.
	 */
	public String create() {
		
		//Create a character creation prompt.
		Prompt charPrompt = new CreateCharacterPrompt(serverThread);
		//Begin executing the prompt loop.
		boolean success = charPrompt.run();
		
		//If the prompt finished successfully, handle results.
		if(success) {
			//Get the new character object from the prompt and add it to the user account
			//of the logged in player.
			PlayerCharacter userCharacter = (PlayerCharacter)charPrompt.getResults();
			serverThread.getUser().addCharacter(userCharacter);
			
			return userCharacter.getName()+" was created successfully.";
		}
		else {
			return null;
		}
	}
	
	/**
	 * Maps to the 'characters' command.
	 * Returns a list of character names and descriptions for each
	 * character associated with this user account.
	 * @return characterList The String description of characters to write to the client.
	 */
	public String viewCharacters() {
		return serverThread.getUser().listCharacters();
	}
	
	/**
	 * Maps to the 'play' user command.
	 * Attempt to play a character tied to the user account.
	 * If a character is found that matches the user string,
	 * Sets the character to the active character for the server thread,
	 * and moves the server thread to the playing state.
	 */
	public void play(String characterName) {
		//If no character name is provided, write error to client.
		if(characterName.isEmpty()) {
			serverThread.write(ERROR_PLAY);
		}
		else {
			//Get the map of characters associated with this account.
			HashMap<String, PlayerCharacter> accountCharacters = serverThread.getUser().getCharacters();
			
			PlayerCharacter player = accountCharacters.get(characterName.toLowerCase());
			
			//If player character is found, play it!
			if(player != null) {
				//Set this thread's player object.
				serverThread.setPlayer(player);
				//Set the player's owner thread to this thread.
				player.setPlayerThread(serverThread);
				
				//Move this thread into the playing state.
				serverThread.switchServerState(ServerState.PLAYING);
				
				//Get the room object that was saved to the player during their last game session.
				Room playerLastRoomFromFile = player.getCurrentRoom();
				
				//Move the player object into the starting room if they are not currently in one.
				if(playerLastRoomFromFile == null) {
					player.setCurrentRoom(world.getStartingRoom());
				}
				//Otherwise, move the player object into the room they were in last.
				else {
					//Update the pointer to the player's last room based on the room name from the account file.
					Room playerStartRoom = world.getRoom(playerLastRoomFromFile.getName());
					//If the room name from the file was not found, place the player into the default starting room.
					if(playerStartRoom == null) {
						player.setCurrentRoom(world.getStartingRoom());
					}
					//Otherwise, the room from the file was found in the GameWorld
					//and the pointer has been updated. Set the player's room.
					else {
						player.setCurrentRoom(playerStartRoom);
					}
				}
				
				//Enter either the starting room for the GameWorld
				//or the last room the player was in last time they played.
				//Broadcast the enter message to the client.
				world.move(player, player.getCurrentRoom());
				
				//Broadcast player login message to everyone in the room the player appears in.
				serverThread.broadcastRoom(player.getCurrentRoom(), player.getName()+" has appeared.");
				
				//Display the enter message to the client.
				serverThread.write("You have entered "+player.getCurrentRoom().getName()+".");
			}
			//Otherwise display error message.
			else {
				serverThread.write(ERROR_PLAYER_CHARACTER_NOT_FOUND);
			}
		}
	}
}