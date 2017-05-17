package com.guimonsters.client;

import java.util.ArrayList;

/**
 * This class is for room objects.
 * It contains room descriptions and contents.
 * @author Stephen Butler
 * @version 1.00, 2013-04-18
 */

public class Room { 

	//Data fields
	private String name;
	private String description;
	//The description of the room itself, not including it's GameObject contents.
	private Inventory roomItems ;
	
	/**
	 * Create a new room object.
	 * @param name The name string of the room.
	 * @param description The description string of the room.
	 * @param roomItems An inventory of objects and actors in the room.
	 */
	public Room(String name, String description, Inventory roomItems) {
		this.name = name;
		this.description = description;
		this.roomItems = roomItems;
	}

}
