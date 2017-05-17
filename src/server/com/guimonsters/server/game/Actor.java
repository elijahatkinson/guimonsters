package com.guimonsters.server.game;

import java.io.Serializable;

/**
 * Master Class for all game characters.
 * 
 * @author Elijah Atkinson
 * @author Stephen Butler
 * @version 2.00, 2013-05-05
 */
public class Actor extends GameObject implements Serializable {
	
	private static final long serialVersionUID = -4936695806396646355L;
	
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
	//The conversation object for the actor.
	private Conversation convo;
	
	/**
	 * Create a new actor instance. Actor is the master class for NPCs and player Characters.
	 * 
	 * @param name The name of the actor.
	 * @param description A longer description of the actor. 
	 * @param profession The "Class" of the actor, i.e. Mage, rogue, etc.
	 * @param race The race of the actor, "Human", "Elf", "Kobold", etc.
	 * @param sex The sex of the actor: Male, Female.
	 * @param level The level of the actor for combat uses.
	 * @param visibilityLevel How visible the actor is.
	 */
	public Actor(String name, String description, String profession,
			String race, String sex, int level,  Visibility visibilityLevel) {
		
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
		this.convo = new Conversation();
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
		super(name, description, Visibility.VISIBLE);
		
		//Create a new blank inventory for the actor.
		this.inventory = new Inventory();
		
		this.profession = profession;
		this.race = race;
		this.sex = sex;
		this.level = level;
		this.experience = 0;
		this.money = 0;
		this.convo = new Conversation();
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
		
	public void addTalkOption(String topic, String sentence){
		this.convo.convoAdd(topic, sentence);
	}
	public void setDefaultResponse(String response){
		this.convo.setDefault(response);
	}
	
	public Conversation getConversation(){
		return convo;
	}
	
	/**
	 * Gives the Actor's response to a keyword. Responds using default response
	 * if the command is not found.
	 * @param keyword The keyword to talk to the actor about.
	 * @return response The response from the actor about the keyword, or the default response.
	 */
	public String respond(String keyword){
		//Get the response to the keyword or the default conversation string.
		return convo.getResponse(keyword.toLowerCase());
	}
}
