package com.guimonsters.client;

import java.util.ArrayList;

/**
 * Master Class for all game characters.
 * @author Elijah Atkinson
 * @author Stephen Butler
 * @version 1.00, 2013-04-18
 */
public class Actor extends GameObject {
	
	//Data fields

	//Actor class could potential be an object.
	private String profession;
	//Race could potentially be an object in the future.
	private String race;
	private String sex;

	//Contains all objects in an Actor's inventory.
	private Inventory inventory;

	//Contains the total experience the actor has gained.
	private int experience;
	//Contains the total available wealth of the actor.
	private int money;
	//The level of the actor for combat purposes.
	private int level;
	
	/**
	 * Create a new actor instance. Actor is the master class for NPCs and player Characters.
	 * 
	 * @param name The name of the actor.
	 * @param description A longer description of the actor. 
	 * @param location The location in the game world where the actor resides.
	 * @param profession The "Class" of the actor, i.e. Mage, rogue, etc.
	 * @param race The race of the actor, "Human", "Elf", "Kobold", etc.
	 * @param sex The sex of the actor: Male, Female.
	 * @param level The level of the actor for combat uses.
	 * @param visibilityLevel How visible the actor is.
	 */
	public Actor(String name, String description, String profession,
			String race, String sex, int level,  int visibilityLevel) {
		
		//Call the GameObject constructor with valid parameters.
		super(name, description, visibilityLevel);
		
		//Create a new blank inventory for the actor.
		this.inventory = new Inventory();
		
		this.profession = profession;
		this.race = race;
		this.sex = sex;
		this.level = level;
		this.experience = 0;
		this.money = 0;
	}
	
	/**
	 * Overloaded actor class for actors with normal visibility by default.
	 * 
	 * @param name The name of the actor.
	 * @param description A longer description of the actor. 
	 * @param profession The "Class" of the actor, i.e. Mage, rogue, etc.
	 * @param race The race of the actor, "Human", "Elf", "Kobold", etc.
	 * @param sex The sex of the actor: Male, Female.
	 * @param level The level of the actor for combat uses.
	 */
	public Actor(String name, String description, String profession,
			String race, String sex, int level) {
	
		//Call the GameObject constructor with valid parameters.
		super(name, description, VIS_VISIBLE);
		
		//Create a new blank inventory for the actor.
		this.inventory = new Inventory();
		
		this.profession = profession;
		this.race = race;
		this.sex = sex;
		this.level = level;
		this.experience = 0;
		this.money = 0;
	}

	//Getters and Setters
	//-------------------------------------------------
	public String getProfession() {
		return profession;
	}

	public void setProfession(String profession) {
		this.profession = profession;
	}

	public String getRace() {
		return race;
	}

	public void setRace(String race) {
		this.race = race;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public Inventory getInventory() {
		return inventory;
	}

	public int getExperience() {
		return experience;
	}

	public void setExperience(int experience) {
		this.experience = experience;
	}

	public int getMoney() {
		return money;
	}

	public void setMoney(int money) {
		this.money = money;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}
}
