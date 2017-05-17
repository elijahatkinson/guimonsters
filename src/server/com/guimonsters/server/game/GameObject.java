package com.guimonsters.server.game;

import java.io.Serializable;

import com.guimonsters.server.game.Visibility;

/**
 * Master class for every game object.
 * This class defines the global fields for every game object:
 * Actors, Players, NPCS, Monsters, Items, etc.
 * @author Elijah Atkinson
 * @author Stephen Butler
 * @author Curran Hamilton
 * @author Kendall Lewis
 * @version 2.00, 2013-05-05
 */
public class GameObject implements Serializable {

	private static final long serialVersionUID = -392117041398869247L;
	
	//Data fields
	private String name;
	private String description;
	//Location might eventually change to a room object or a coordinate
	//in a dungeon object (or something like that).
	
	private Visibility vLevel;
	
	/**
	 * Create a new instance of a GameObject.
	 * @param name The name string associated with the GameObject.
	 * @param description The long description of the GameObject (used by 'look' command).
	 *     (Possibly the room object in which the GameObject resides)
	 * @param vLevel The Visibility enum flag for the GameObject.
	 * 		         Determines how visible this object is to actors.
	 */
	public GameObject(String name, String description, Visibility vLevel) {
		this.name = name;
		this.description = description;
		this.vLevel = vLevel;
	}
	
	/**
	 * Overloaded for GameObjects that are visible by default.
	 * @param name The name string associated with the GameObject.
	 * @param description The long description of the GameObject (used by 'look' command).
	 */
	public GameObject(String name, String description) {
		this.name = name;
		this.description = description;
		this.vLevel = Visibility.VISIBLE;
	}

	//Getters and setters
	//------------------------------------------------
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Visibility getVisibilityLevel() {
		return vLevel;
	}

	public void setVisibilityLevel(Visibility vLevel) {
		this.vLevel = vLevel;
	}
}
