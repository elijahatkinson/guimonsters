package com.guimonsters.server.game;

//import com.guimonsters.server.*;
import java.io.Serializable;
import com.guimonsters.server.MudServerThread;

/**
 * Character stub class.
 * @author Elijah Atkinson
 * @author Stephen Butler
 * @author Curran Hamilton
 * @author Kendall Lewis
 * @version 2.00, 2013-05-05
 */
public class PlayerCharacter extends Actor implements Serializable {
	
	private static final long serialVersionUID = 8924688873557984933L;
	
	//Data fields
	
	private transient MudServerThread playerThread;
	private Room currentRoom;

	public PlayerCharacter(String name, String description, String profession,
			String race, String sex, int level,  Visibility vLevel) {
		
		super(name, description, profession, race, sex, level, vLevel);
		
	}
	
	public PlayerCharacter(String name, String description, String profession,
			String race, String sex) {
		
		super(name, description, profession, race, sex, 1, Visibility.VISIBLE);
	}
	
	/**
	 * Describes this character using name, level, race, sex,
	 * class, and description fields.
	 * @return description The compound description of the character.
	 */
	public String describe() {
		String message = this.getName()+" is a level "+this.getLevel()+" "+
				this.getRace()+" "+this.getProfession()+". "+this.getSexPronoun()+
				" looks "+this.getDescription()+".";
		
		return message;
	}
	
	/**
	 * Describes this character using the second person with name, level, race, sex,
	 * class, and description fields.
	 * @return description The compound description of the character.
	 */
	public String describeSelf() {
		String message = "You are a level "+this.getLevel()+" "+
				this.getRace()+" "+this.getProfession()+". You "+
				"look "+swapPronounsToSecondPerson(this.getDescription())+".";
		
		return message;
	}
	
	/**
	 * Replace pronouns in a given string. Used to fix
	 * player input descriptions for second person descriptions.
	 * Change instances of Her/Him to Your, her/him to your,
	 * His/Hers to Yours, and his/hers to yours.
	 * @param message The message String to edit.
	 * @return results The edited String.
	 */
	private String swapPronounsToSecondPerson(String message) {
		String results = message;
		results = results.replaceAll(" He | She ", " You ");
		results = results.replaceAll(" he | she ", " you ");
		results = results.replaceAll(" Him | Her ", " Your ");
		results = results.replaceAll(" him | her ", " your ");
		results = results.replaceAll(" His | Hers ", " Yours ");
		results = results.replaceAll(" his | hers ", " yours ");
		
		return results;
	}
	
	/**
	 * Gets the proper pronoun for this character based on its sex.
	 * The pronoun is capitalized by default.
	 * @return pronoun The pronoun string for this character.
	 */
	private String getSexPronoun() {
		String pronoun;
		if(this.getSex().equalsIgnoreCase("male")) {
			pronoun = "He";
		}
		else {
			pronoun = "She";
		}
		
		return pronoun;
	}
	
	//Get the MudServerThread associated with this character
	public MudServerThread getPlayerThread(){
		return this.playerThread;
	}
	//Set the MudServerThread associated with this character
	public void setPlayerThread(MudServerThread thread){
		this.playerThread = thread;
	}

	public Room getCurrentRoom() {
		return currentRoom;
	}

	/**
	 * Set the current room this player is in.
	 * If current room is null, the player has never entered a GameWorld before.
	 * So move to a destination room without a start room.
	 * Otherwise, move from the player's current room to a new room.
	 * @param currentRoom
	 */
	public void setCurrentRoom(Room currentRoom) {
		if(this.currentRoom == null) {
			//Move the player into the destination room because they are not currently in a room.
			this.playerThread.getParentThread().getWorld().move(this, currentRoom);
		}
		else {
			//Move from the previous room to the new room.
			this.playerThread.getParentThread().getWorld().move(this, this.currentRoom, currentRoom);
		}
		//Update the current room pointer to the new room.
		this.currentRoom = currentRoom;
	}	
	
}