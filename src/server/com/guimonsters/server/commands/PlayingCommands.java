package com.guimonsters.server.commands;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.guimonsters.network.Directive;
import com.guimonsters.network.Packet;
import com.guimonsters.server.MudServerThread;
import com.guimonsters.server.game.Actor;
import com.guimonsters.server.game.PlayerCharacter;
import com.guimonsters.server.game.Room;

/**
 * Child of GameCommands. Used to define Commands
 * available to the PLAYING game state.
 * 
 * @author Elijah Atkinson
 * @version 2.00, 2013-05-01
 */
public class PlayingCommands extends ServerCommands {
	
	//Data Field
	
	private String promptString = "Your vision begins to fade to black and you "+
			"feel like you're being sucked down a drain and torn into pieces at "+
			"the same time. A roaring sound fills your ears and suddenly you feel "+
			"a sense of weightlessness. Maybe you should ask for some 'help'.";
	
	//Player chat separator string.
	private static final String CHAT_DELIMTER = " >> ";
	
	//Command Description strings
	private static final String DESCRIPTION_STRONG_SILENT = "For when you want to look tough.";
	private static final String DESCRIPTION_SAY = "Say something out loud.";
	private static final String DESCRIPTION_GO = "Move you through an exit.";
	private static final String DESCRIPTION_LOOK = "Look closely at something.";
	private static final String DESCRIPTION_TALK = "Talk to someone or something.";
	
	//Command error strings
	private static final String ERROR_STRONG_SILENT = "...";
	private static final String ERROR_SAY = "What do you want to say? Proper usage is 'say [message]'.";
	private static final String ERROR_GO = "Where do you want to go? Proper usage is 'go [exit]'.";
	private static final String ERROR_LOOK = "What do you want to look at? Proper usage is 'look [something]'.";
	private static final String ERROR_TALK = "Who or what do you want to talk to? Proper usage is 'talk [something]' or 'talk [something] [topic]'.";
	
	//Store a reference to the thread's player object.
	private PlayerCharacter player;
	
	/**
	 * Create a PlayingCommands instance that can be used
	 * to parse user input and dispatch the appropriate
	 * event from the PLAYING state of the game.
	 * Assumes user input has been previously sanitized.
	 */
	public PlayingCommands(MudServerThread thread) {
		super(thread);
		
		//Override parent strings here.
		super.promptString = this.promptString;
		
		//Main State commands
		Command strongSilentType = new Command(this, "strongSilentType",
				DESCRIPTION_STRONG_SILENT, ERROR_STRONG_SILENT,
				methodMap.get("strongSilentType"));
		
		Command say = new Command(this, "say", DESCRIPTION_SAY, ERROR_SAY,
				methodMap.get("say"));
		
		Command go = new Command(this, "go", DESCRIPTION_GO, ERROR_GO,
				methodMap.get("go"));
		
		Command look = new Command(this, "look", DESCRIPTION_LOOK, ERROR_LOOK,
				methodMap.get("look"));
		
		Command talk = new Command(this, "talk", DESCRIPTION_TALK, ERROR_TALK,
				methodMap.get("talk"));
		
		//Remove the login command from the map since we're already logged in.
		this.commandMap.remove("login");
		//Remove create account from command map since we're already logged in.
		this.commandMap.remove("create_account");
		
		//Add the state command objects to the command hash map.
		this.commandMap.put("...", strongSilentType);
		this.commandMap.put("/s", say);
		this.commandMap.put("say", say);
		this.commandMap.put("go", go);
		this.commandMap.put("move", go);
		this.commandMap.put("look", look);
		this.commandMap.put("inspect", look);
		this.commandMap.put("talk", talk);
		
		//Print this command object's prompt to the main game log.
		this.prompt();
		
		
		player = serverThread.getPlayer();
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
	 * Maps to the user commands '/s [message]' or 'say [message]'.
	 * Prints:
	 * You say 'user message here'
	 * To the game log.  Eventually output of this command
	 * will be visible to the whole game room.
	 * @param message The String to "say".
	 */
	public void say(String message) {
		//Get the player name so we can prepend it to the broadcast.
		String playerName = player.getName();

		//Build the chat packet to send to the client and all other players.
		Packet chatPacket = new Packet(playerName+CHAT_DELIMTER+message, Directive.CHAT);
		
		//Broadcast what this player said to other players.
		this.serverThread.broadcastRoom(player.getCurrentRoom(), chatPacket);
		
		this.serverThread.write(chatPacket);
	}
	
	/**
	 * Moves a character between GameWorld rooms. Broadcasts enter and exit
	 * methods to other players in the start and end rooms.
	 * @param exitName The name of the room exit object to exit through.
	 * @return description The description entered room.
	 */
	public String go(String exitName) {
		//Pass the exit name entered by the user to the player's current room.
		//Return results to the client.
		return player.getCurrentRoom().exit(player, exitName);
	}
	
	/**
	 * Maps to 'look' command.
	 * Return a description of the room if called without arguments or if called
	 * with the room name.
	 * Return a description of an GameObject, Actor, or Player if called with arguments.
	 * @return description The description String of the zone, object, or actor.
	 */
	public String look(String something) {
		String description;
		
		Room currentRoom = player.getCurrentRoom();
		String roomName = currentRoom.getName();
		
		//If look command was executed with no arguments,
		//return the description for the room.
		if(something.isEmpty() || something == null ||
		   something.equalsIgnoreCase("room") || something.equalsIgnoreCase("around") ||
		   something.equalsIgnoreCase(roomName)) {
			description = currentRoom.descRoom(player.getName());
		}
		else {
			//Describe the current player.
			if(something.equalsIgnoreCase("self") || something.equalsIgnoreCase("me")) {
				description = player.describeSelf();
			}
			else {
				//Attempt to match user string against any object in the room: GameObjects, Players, Actors, and Exits.
				//If no object is found, returns the default look error string.
				description = currentRoom.describeObject(something);
			}
		}
		
		return description;
	}
	
	//Need case for "Talk Actor" and "Talk Actor Topic"
	
	/**
	 * Maps to 'talk' command.
	 * Return dialogue from an Actor.
	 * @return results The reply from the Actor, or a goofy message from yourself.
	 */
	public String talk(String message) {
		//TODO Stephen, this currently works now for actors with no spaces in their names.
		//TODO need to expand this stub command to initiate conversation with an actor. Conversations should take place as a prompt.
		
		String[] selfMessages = {
			"I wish I had a tar pit full of werewolves right now.",
			"I wonder how long I've been here.",
			"I knew I shouldn't have taken directions from those mudkips.",
			"I'm starting to get hungry. I wish I had some food left in my backpack.",
			"What was that?  It sounded like squealing.",
			"Why am I talking to myself? Get a grip on yourself!",
			"Sure could go for pint of mead right about now..."
		};
		
		Random rand = new Random();
		int i = rand.nextInt(7);
		
		String results;
		
		//Talk to yourself, choose a random index from self messages.
		if(message.equals("self") || message.equals("yourself") || message.equals("myself") || message.equals("me")) {
			results = selfMessages[i];
		}
		//If message contains a space, attempt to break the message into actor name and keyword.
		else if(message.contains(" ")) {
			String actorName = message.substring(0, message.indexOf(' '));
			//Everything after the first space is the parameter string.
			String keyword = message.substring(message.indexOf(' ')+1);
			
			//Attempt Get the npc object to talk to.
			Actor npc = serverThread.getPlayer().getCurrentRoom().getActor(actorName);
			//If the npc exists, talk to them.
			if(npc != null){
				results = npc.respond(keyword);
			}
			else {
				results = "You can't find anyone named "+actorName+" to talk to.";
			}
		}
		//Otherwise, just interpret the message as the actor name.
		else {
			//Attempt Get the npc object to talk to.
			Actor npc = serverThread.getPlayer().getCurrentRoom().getActor(message);
			//If the npc exists, talk to them using the default command.
			if(npc != null){
				results = npc.respond("default");
			}
			else {
				results = "You can't find anyone named "+message+" to talk to.";
			}
		}
		return results;
	}
}