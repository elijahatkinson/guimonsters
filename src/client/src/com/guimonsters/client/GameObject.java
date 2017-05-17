package com.guimonsters.client;

/**
 * Master class for every game object.
 * This class defines the global fields for every game object:
 * Actors, Players, NPCS, Monsters, Items, etc.
 * @author Elijah Atkinson
 * @author Stephen Butler
 * @author Curran Hamilton
 * @author Kendall Lewis
 * @version 1.00, 2013-04-18
 */
public class GameObject {

	//Data fields
	private String name;
	private String description;
	//Location might eventually change to a room object or a coordinate
	//in a dungeon object (or something like that).
	private String location;
	private int visibilityLevel;
	
	protected static final int VIS_COMPLETELY_HIDDEN = 0;
	protected static final int VIS_VERY_WELL_HIDDEN = 1;
	protected static final int VIS_WELL_HIDDEN = 2;
	protected static final int VIS_HIDDEN = 3;
	protected static final int VIS_SLIGHTLY_HIDDEN = 4;
	protected static final int VIS_VISIBLE = 5;
	protected static final int VIS_GLARING = 6;
	
	/**
	 * Create a new instance of a GameObject.
	 * @param name The name string associated with the GameObject.
	 * @param description The long description of the GameObject (used by 'look' command).
	 *     (Possibly the room object in which the GameObject resides)
	 * @param visibilityLevel Degrees of visibility of the GameObject.
	 *     0 = Completely hidden from all interaction.
	 *     1 = Very well hidden
	 *     2 = Well hidden
	 *     3 = Hidden
	 *     4 = Slightly hidden
	 *     5 = Completely visible
	 *     6 = Glaringly visible
	 */
	public GameObject(String name, String description, int visibilityLevel) {
		this.name = name;
		this.description = description;
		this.visibilityLevel = visibilityLevel;
	}
	
	/**
	 * Overloaded for GameObjects that are visible by default.
	 * @param name The name string associated with the GameObject.
	 * @param description The long description of the GameObject (used by 'look' command).
	 */
	public GameObject(String name, String description) {
		this.name = name;
		this.description = description;
		this.location = location;
		this.visibilityLevel = VIS_VISIBLE;
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

	public int getVisibilityLevel() {
		return visibilityLevel;
	}

	public void setVisibilityLevel(int visibilityLevel) {
		this.visibilityLevel = visibilityLevel;
	}
}
