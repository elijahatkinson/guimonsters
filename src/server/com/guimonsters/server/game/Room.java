package com.guimonsters.server.game;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.guimonsters.server.MudServerThread;

/**
 * @author Modified by Elijah Atkinson
 * @author Modified by Kendall Lewis
 * @version 2.00, 2013-05-05
 */
public class Room implements Serializable { 
	
	private static final long serialVersionUID = 5459956620026614953L;
	
	private static final String LOOK_FAILED_STRING = "You don't see anything like that.";
	private static final String ERROR_EXIT_NOT_FOUND = "That is not an exit.";
	private static final String ERROR_EXIT_BROKEN = "That exit appears to be broken.";
	
	private String name;
	private String description;
	//The description of the room itself, not including its GameObject contents.
/*	private Inventory roomItems;
	private ArrayList<PlayerCharacter> characters;
	private ArrayList<Actor> actors;
	//Will change Exits array list to "GameExitObects" once they're created.
	private ArrayList<Exit> exits;*/
	
	private HashMap<String, GameObject> roomItems;
	private HashMap<String, PlayerCharacter> characters;
	private HashMap<String, Actor> actors;
	private HashMap<String, Exit> exits;
	
	/**
	 * Create a new room object with no items within.
	 * @param name The string name of the room.
	 * @param description The string description of the room.
	 */
	public Room(String name, String description) {
		this.name = name;
		this.description = description;	
	/*	this.roomItems = new Inventory();
		this.characters = new ArrayList<PlayerCharacter>();
		this.actors = new ArrayList<Actor>();
		this.exits = new ArrayList<Exit>();*/
		this.roomItems = new HashMap<String, GameObject>();
		this.characters = new HashMap<String, PlayerCharacter>();
		this.actors = new HashMap<String, Actor>();
		this.exits = new HashMap<String, Exit>();
	}

	/**
	 * Create a new room object with items already within.
	 * @param name The string name of the room.
	 * @param description The string description of the room.
	 * @param items The HashMap of items within the room.
	 * @param chars The HashMap of player-characters within the room.
	 * @param actors The HashMap of non-player actors within the room.
	 *@param exits The HashMap of exits within the room.
	 */
	public Room(String name, String description,
			HashMap<String, GameObject> items, HashMap<String, PlayerCharacter> chars,
			HashMap<String, Actor> actors, HashMap<String, Exit> exits) {
		this.name = name;
		this.description = description;	
		this.roomItems = items;
		this.characters = chars;
		this.actors = actors;
		this.exits = exits;
	}
	
	/**
	 * Move the player object into this room. Broadcast a [playername] appears
	 * message to other players in the room.  Update the player object's
	 * currentRoom pointer to match the room they are now in. Write the description
	 * of the new room to the player.
	 * @return The description of the room the player just entered.
	 */
	public String enter(PlayerCharacter player) {
		this.addCharacter(player);
		
		player.setCurrentRoom(this);
		
		player.getPlayerThread().broadcastRoom(this, player.getName()+" appears.");
		
		return "You have entered "+this.name+".";
	}
	
	/**
	 * Move the player object into this room, do NOT broadcast any messages
	 * to other players in the room.
	 * @param player
	 * @return
	 */
	public String enterSilent(PlayerCharacter player) {
		this.addCharacter(player);
		
		player.setCurrentRoom(this);
		
		return "You have entered "+this.name+".";
	}
	
	/**
	 * Move the player out of their current room into another room
	 * through an exit object. Writes a [playername] has left message
	 * to all remaining players in the room. The player then enters
	 * the destination room of the exit object they traveled through.
	 * @param player The PlayerCharacter to move.
	 * @param exit The exit to travel through.
	 */
	public String exit(PlayerCharacter player, String exitName) {
		String message;
		//Make sure the exit exists in this room before the player is moved.
		Exit exit = this.getExit(exitName);
		
		//If the exit exists and leads somewhere, proceed with exiting.
		if(exit != null) {
			//Make sure the exit is linked to a room.
			if(exit.getDestination() != null) {
				
				//Broadcast zone message to all players in the room. IF and only if
				//player didn't go through a loop back exit (an exit that points to its own room).
				boolean exitedToADifferentZone = !exit.getDestination().getName().equalsIgnoreCase(this.getName());
				if(exitedToADifferentZone) {
					player.getPlayerThread().broadcastRoom(this, player.getName()+" has left.");
					
					//Remove the given player from this room.
					this.removeCharacter(player);
					
					//Enter the room attached to the exit object that the player went through.
					message = exit.getDestination().enter(player);
				}
				//Otherwise player went through a looping exit. Don't move them, but
				//return the room description.
				else {
					message = this.descRoom(player.getName());
				}
			}
			else {
				message = ERROR_EXIT_BROKEN;
			}
		}
		//Otherwise, print exit not found message.
		else {
			message = ERROR_EXIT_NOT_FOUND;
		}
		
		
		return message;
	}
	
	/**
	 * Writes a description of the room to the player.
	 * Describes all visible items, actors, players, and exits
	 * in the room.
	 * @return 
	 */
	public String descRoom(String ignoredPlayer) {
		String results = this.description+descrItems()+descrActors()+descrPlayers(ignoredPlayer)+descrExits();
		return results;
	}
	
	/**
	 * Check every room container map for a string.
	 * Return the first matching object's description.
	 * Return the look failed string if no match found.
	 * @param something The object name to attempt to describe.
	 * @return results The detailed description of an object in the room if
	 *                 an object with name matching the user search string exists.
	 *                 Otherwise, returns the look failed string.
	 */
	public String describeObject(String something) {
		String key = something.toLowerCase();
		String results = "";
		
		GameObject item = roomItems.get(key);
		if(item == null) {
			PlayerCharacter character = characters.get(key);
			if(character == null) {
				Exit ex = exits.get(key);
				if(ex == null) {
					Actor actor = actors.get(key);
					if(actor == null) {
						results = LOOK_FAILED_STRING;
					}
					else {
						results = actor.getDescription();
					}
				}
				else {
					results = ex.getDescription();
				}
			}
			else {
				results = character.describe();
			}
		}
		else {
			results = item.getDescription();
		}
			
		
		return results;
	}
	
	
	/**
	 * Describe visible items in the room if there is at least one.
	 * @return results The description of all items in the room.
	 */
	private String descrItems() {
		String results = "";
		if(roomItems.size() > 0) {
			
			results = " It contains ";
			
			for(Map.Entry<String, GameObject> entry : roomItems.entrySet()) {
				GameObject item = entry.getValue();
				
				//Only describe visible items.
				if(item.getVisibilityLevel().compareTo(Visibility.INVISIBILE) > 0) {
					results += item.getName()+", ";
				}
			}
			
			//Strip trailing comma and whitespace.
			results = results.substring(0, results.length()-2);
			results += ".";
		}
		return results;
	}
	
	/**
	 * Describe all of the visible actors in the room.
	 * @return description The description of actor objects.
	 */
	private String descrActors() {
		String results = "";
		if(actors.size() > 0) {
			
			results = " Also here is ";
			
			for(Map.Entry<String, Actor> entry : actors.entrySet()) {
				Actor actor = entry.getValue();
				
				//Only describe visible actors.
				if(actor.getVisibilityLevel().compareTo(Visibility.INVISIBILE) > 0) {
					results += actor.getName()+", ";
				}
			}
			
			//Strip trailing comma and whitespace.
			results = results.substring(0, results.length()-2);
			results += ".";
		}
		return results;
	}
	
	/**
	 * Describe all players in the room but the player who issued
	 * the look command.
	 * @param ignoredPlayer The player name to not add to the description
	 * @return description The description of players in the room.
	 */
	private String descrPlayers(String ignoredPlayer) {
		String results = "";
		if(characters.size() > 1) {
			
			results = " You also see ";
			
			for(Map.Entry<String, PlayerCharacter> entry : characters.entrySet()) {
				PlayerCharacter pc = entry.getValue();
				
				//Only describe visible players.
				if(pc.getVisibilityLevel().compareTo(Visibility.INVISIBILE) > 0) {
					//Don't describe the ignored player.
					if(!pc.getName().equalsIgnoreCase(ignoredPlayer)) {
						results += pc.getName()+", ";
					}
				}
			}
			
			//Strip trailing comma and whitespace.
			results = results.substring(0, results.length()-2);
			results += ".";
		}
		return results;
	}
	
	/**
	 * Describe all of the visible exits in the room.
	 * @return description The description of exit objects.
	 */
	private String descrExits() {
		String results = "";
		if(exits.size() > 0) {
			
			results = " Obvious exits are ";
			
			for(Map.Entry<String, Exit> entry : exits.entrySet()) {
				Exit ex = entry.getValue();
				
				//Only describe exits.
				if(ex.getVisibilityLevel().compareTo(Visibility.INVISIBILE) > 0) {
					results += ex.getName()+", ";
				}
			}
			
			//Strip trailing comma and whitespace.
			results = results.substring(0, results.length()-2);
			results += ".";
		}
		return results;
	}
	
	//Getters and setters
	//-------------------------------------------------
	public String getName() {
		return this.name;
	}
		
	public String getDescription() {
		return this.description;
	}
	
	public HashMap<String, GameObject> getRoomItems() {
		return roomItems;
	}

	public void setRoomItems(HashMap<String, GameObject> roomItems) {
		this.roomItems = roomItems;
	}

	public HashMap<String, PlayerCharacter> getCharacters() {
		return characters;
	}

	public void setCharacters(HashMap<String, PlayerCharacter> characters) {
		this.characters = characters;
	}

	public HashMap<String, Actor> getActors() {
		return actors;
	}

	public void setActors(HashMap<String, Actor> actors) {
		this.actors = actors;
	}

	public HashMap<String, Exit> getExits() {
		return exits;
	}

	public void setExits(HashMap<String, Exit> exits) {
		this.exits = exits;
	}

	/**
	 * Returns a GameObject from the roomItems map if it
	 * exists. Returns null if that GameObject is not found.
	 * @param name The name of the GameObject to look for.
	 * @return item The GameObject to return. Null if GameObject was not found.
	 */
	public GameObject getItem(String name) {
		return this.roomItems.get(name.toLowerCase());
	}
	
	/**
	 * Returns an actor from the room actor hash map
	 * if it exists. Returns null if that actor is not found.
	 * @param name The name of the actor to look for.
	 * @return actor The actor to return. Null if actor was not found.
	 */
	public Actor getActor(String name) {
		return this.actors.get(name.toLowerCase());
	}
	
	/**
	 * Returns a player from the room player hash map
	 * if it exists. Returns null if that player is not found.
	 * @param name The name of the player to look for.
	 * @return pc The player to return. Null if player was not found.
	 */
	public PlayerCharacter getPlayer(String name) {
		return this.characters.get(name.toLowerCase());
	}
	
	/**
	 * Returns a exit from the room exit hash map
	 * if it exists. Returns null if that exit is not found.
	 * @param name The name of the exit to look for.
	 * @return e The exit to return. Null if exit was not found.
	 */
	public Exit getExit(String name) {
		return this.exits.get(name.toLowerCase());
	}

	
	//Adders
	//-------------------------------------------------
	public void addRoomItem(GameObject item) {
		this.roomItems.put(item.getName().toLowerCase(), item);
	}
	
	public void addCharacter(PlayerCharacter ch) {
		this.characters.put(ch.getName().toLowerCase(), ch);
	}
	
	public void addActor(Actor a) {
		this.actors.put(a.getName().toLowerCase(), a);
	}
	
	public void addExit(Exit e) {
		this.exits.put(e.getName().toLowerCase(), e);
	}
	
	//Removers
	//-------------------------------------------------
	public void removeRoomItem(GameObject item) {
		this.roomItems.remove(item.getName().toLowerCase());
	}
	
	public void removeCharacter(PlayerCharacter character) {
		this.characters.remove(character.getName().toLowerCase());
	}
	
	public void removeActor(Actor actor) {
		this.actors.remove(actor.getName().toLowerCase());
	}
	
	public void removeExit(Exit exit) {
		this.exits.remove(exit.getName().toLowerCase());
	}
		
	/**
	 * Searches the room for the presence of an Actor.
	 * @param charName The name of the Actor to be found.
	 * @return Returns true if the acter is in the room.
	 */
	//Tests for presence of Actor (NPC or Player) and returns True if present.
	public boolean isPresentActor(String charName){
		return this.actors.containsKey(charName.toLowerCase());
	}

	/**
	 * Searches the room for the presence of a player character.
	 * @param charName The name of the player character to be found.
	 * @return Returns true if the player character is in the room.
	 */
	//Tests for presence of Player only, returns True if present.
	public boolean isPresentPlayer(String charName){
		return this.characters.containsKey(charName.toLowerCase());
	}
}