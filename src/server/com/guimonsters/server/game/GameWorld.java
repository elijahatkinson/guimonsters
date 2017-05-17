package com.guimonsters.server.game;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import com.guimonsters.server.file.Serializer;

/**
 * The class GameWorld loads a character specific world when
 * that character is selected at login, and saves the world 
 * every time the user logs off.
 * Rooms can be added or removed from the world.
 * @author Kendall Lewis
 * @author Elijah Atkinson
 * @author Curran Hamilton
 * @author Stephen Butler
 * @version 2.00, 2013-05-07
 */
public class GameWorld implements Serializable {
	
	// Data fields
	private static final long serialVersionUID = -8221865884947706117L;
	//The main world file path
	private static final String GAME_WORLD_PATH = "./worlds";
	private File worldFile;
	private Room startingRoom;
	/*private ArrayList<Room> roomList;*/
	private HashMap<String, Room> roomList;
	
	/**
	 * Create a GameWorld.
	 * @param name The name string to use for saving to file.
	 */
	public GameWorld(String name) {
		/*this.roomList = new ArrayList<Room>();*/
		this.roomList = new HashMap<String, Room>();
		
		String noSpaceName = name.replaceAll(" ", "_");
		this.worldFile = new File(GAME_WORLD_PATH+"/"+noSpaceName+".world");
	}

	/**
	 * Create a new GameWorld without a file association.
	 * The load function must be called before this game world can be saved.
	 */
	public GameWorld() {
		/*this.roomList = new ArrayList<Room>();*/
		this.roomList = new HashMap<String, Room>();
	}
	
	/**
	 * Get a room that matches a given name string.
	 * @param name The name of the room to look for.
	 * @return results The room object if found, or null if not found.
	 */
	public Room getRoom(String name) {
		Room results;
		results = this.roomList.get(name.toLowerCase());
		return results;
	}

	/**
	 * Check if a room with a specified name exists in the 
	 * GameWorld.
	 * @param name The name of the room to look for.
	 * @return success True if the room was found in the GameWorld, false otherwise.
	 */
	public boolean roomExists(String name) {
		return this.roomList.containsKey(name.toLowerCase());
	}
	
	/**
	 * Check if a room object exists in the GameWorld.
	 * @param r The Room object to check for.
	 * @return success True if the Room object exists in the GameWorld, false otherwise.
	 */
	public boolean roomExists(Room r) {
		return this.roomList.containsValue(r);
	}
	
	
	/**
	 * Add a pre-constructed room object to the GameWorld.
	 * @param r The room to add.
	 * @return success The success of the add.
	 */
	public boolean addRoom(Room r) {
		boolean success = false;
		
		//If the room is not already in the map, add it.
		String roomNameKey = r.getName().toLowerCase();
		if(this.roomList.containsKey(roomNameKey) == false) {
			this.roomList.put(roomNameKey, r);
			success = true;
		}
		
	/*	//If the room is not contained in the GameWorld, add it.
		if(this.roomList.contains(r.getName()) == false) {
			this.roomList.add(r);
			success = true;
		}*/
		return success;
	}
	
	/**
	 * Adds a room to the list of rooms within the GameWorld.
	 * @param name The name of the room to be added to the GameWorld.
	 * @param description A description of the room being added.
	 * @return success Whether the room was successfully added or not.
	 */
	public boolean addRoom(String name, String description) {
		boolean success = false;
		Room r = new Room(name, description);

		//Add the new room if it doesn't already exist.
		success = addRoom(r);
		
		return success;
	}
	
	/**
	 * Adds a room to the list of rooms within the GameWorld.
	 * @param name The name of the room to be added to the GameWorld.
	 * @param description A description of the room being added.
	 * @param roomItems The inventory of items within the room being added.
	 * @param characters The inventory of characters within the room being added.
	 * @param actors The inventory of actors within the room being added.
	 * @param exits The inventory of exits within the room being added.
	 * @return success Whether the room was successfully added or not.
	 */
	public boolean Room(String name, String description,
			HashMap<String, GameObject> roomItems, HashMap<String, PlayerCharacter> characters,
			HashMap<String, Actor> actors, HashMap<String, Exit> exits) {
		boolean success = false;
		Room r = new Room(name, description, roomItems, characters, actors, exits);
		
		//Add the new room if it doesn't already exist.
		success = addRoom(r);
		
		return success;
	}
	
	/**
	 * Removes a room from within the GameWorld.
	 * @param name The name of the room to be removed.
	 * @return success Whether the room was successfully removed or not.
	 */
	public boolean removeRoom(String name) {
		boolean success = false;
		
		Room oldRoom = this.roomList.remove(name.toLowerCase());
		//If a room was returned, it was removed. Return true.
		if(oldRoom != null) {
			success= true;
		}
		
		return success;
	}
	
	/**
	 * Loads a GameWorld from a serialized file in the worlds directory.
	 * @param filename The name of the file to attempt to open (must include extension).
	 * @return The success of the load.
	 */
	public boolean load(String filename) {
		boolean success = false;
		
		this.worldFile = new File(GAME_WORLD_PATH+"/"+filename);
		Serializer serial = new Serializer(this.worldFile);
		
		try {
			//Load world data from the serialized file associated
			//with this world object.
			GameWorld worldFromFile = (GameWorld) serial.deserialize();
			
			this.startingRoom = worldFromFile.startingRoom;
			this.roomList = worldFromFile.roomList;
			
			success = true;
		}
		catch (IOException | ClassNotFoundException e) {
			success = false;
		}
		
		return success;
	}
	
	
	/**
	 * Saves the current GameWorld. Rewrites the file for the
	 * previous GameWorld (if it exists) to reflect recent changes.
	 * @return success The success of the save.
	 */
	public boolean save() {
		//Write this world object into the worldFile.
		
		Serializer serial = new Serializer(this.worldFile, this);
		boolean success = serial.serialize();
		
		return success;
	}
	
	/**
	 * Move a GameObject item from one room to another.
	 * @param item The item to be moved.
	 * @param start The room the item is leaving.
	 * @param destination The room the item is going to. 
	 */
	public void move(GameObject item, Room start, Room destination) {
		destination.addRoomItem(item);
		start.removeRoomItem(item);
	}
	
	/**
	 * Move a character from one room to another.
	 * @param ch The character to be moved.
	 * @param start The room the character is leaving.
	 * @param destination The room the character is going to.
	 */
	public void move(PlayerCharacter ch, Room start, Room destination) {
		destination.addCharacter(ch);
		start.removeCharacter(ch);
	}
	
	/**
	 * Move an actor (NPC, monster, etc.) from one room to another.
	 * @param a The actor to be moved.
	 * @param start The room the actor is leaving.
	 * @param destination The room the actor is going to.
	 */
	public void move(Actor a, Room start, Room destination) {
		destination.addActor(a);
		start.removeActor(a);
	}
	
	/**
	 * Move an exit from one room to another.
	 * @param e The exit to be moved.
	 * @param start The room the exit is leaving.
	 * @param destination The room the exit is going to.
	 */
	public void move(Exit e, Room start, Room destination) {
		destination.addExit(e);
		start.removeExit(e);
	}
	
	/**
	 * Move a new character into a GameWorld room for the first time.
	 * @param ch The player character to move.
	 * @param destination The destination room to move too.
	 */
	public void move(PlayerCharacter ch, Room destination) {
		destination.addCharacter(ch);
	}

	//Setter and Getter
	//----------------------------------------------------
	public String getFileName() {
		return this.worldFile.getName();
	}
	public HashMap<String, Room> getRoomList() {
		return this.roomList;
	}
	
	public void setRoomList(HashMap<String, Room> roomList) {
		this.roomList = roomList;
	}

	public Room getStartingRoom() {
		return startingRoom;
	}

	/**
	 * Set the starting room for the GameWorld.
	 * This is the room new characters will be placed in
	 * the first time they log in.
	 * @param startingRoom The room for new characters to be placed in when
	 *                     they log in the first time.
	 */
	public void setStartingRoom(Room startingRoom) {
		this.startingRoom = startingRoom;
		this.roomList.put(this.startingRoom.getName().toLowerCase(), this.startingRoom);
	}
}
