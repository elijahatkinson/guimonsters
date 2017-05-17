package com.guimonsters.server.prompt;

import java.io.File;
import java.util.HashMap;

import com.guimonsters.network.Directive;
import com.guimonsters.network.Packet;
import com.guimonsters.server.MudServerThread;
import com.guimonsters.server.game.Account;
import com.guimonsters.server.game.PlayerCharacter;

public class CreateCharacterPrompt implements Prompt {
	
	//Account path name
	private static final String ACCOUNT_PATH = "./accounts";
	//Character creation prompt strings
	private static final String CHARACTER_CREATION_WELCOME = "Welcome to the character creation prompt. You can return to a previous field with the 'back' command. You can exit this prompt at any time by typing 'exit'.";
	private static final String CHARACTER_CREATE_NAME = "<< Please enter your desired character name. >>";
	private static final String CHARACTER_CREATE_RACE = "<< What is your character's race? (Human, Elf, Dwarf, Orc, etc) >>";
	private static final String CHARACTER_CREATE_SEX = "<< Is your character male or female? >>";
	private static final String CHARACTER_CREATE_CLASS = "<< Please enter your character's class. (Warrior, Wizard, Paladin, Thief, etc) >>";
	private static final String CHARACTER_CREATE_DESCRIPTION = "<< Please enter a short description of your character." +
			" Your description should complete this sentence: 'Your character looks [description].' This description can be changed later. >>";
	//Character creation error strings
	//name strings
	private static final String ERROR_NAME_TAKEN = "That character name is taken.";
	private static final String ERROR_NAME_EMPTY = "Character name can not be empty.";
	private static final String ERROR_NAME_FORMAT = "Character names must contain only letters.";
	private static final String ERROR_NAME_LENGTH = "Character names must between 3 and 20 letters long.";
	//race strings
	private static final String ERROR_RACE_EMPTY = "Character race can not be empty.";
	private static final String ERROR_RACE_FORMAT = "Character races must contain only letters.";
	private static final String ERROR_RACE_LENGTH = "Character races must between 3 and 20 letters long.";
	//sex strings
	private static final String ERROR_SEX_EMPTY = "Character sex can not be empty.";
	private static final String ERROR_SEX_FORMAT = "Character sex must be 'male' or 'female'.";
	//class strings
	private static final String ERROR_CLASS_EMPTY = "Character class can not be empty.";
	private static final String ERROR_CLASS_FORMAT = "Character classes must contain only letters.";
	private static final String ERROR_CLASS_LENGTH = "Character classes can not be longer than 20 letters.";
	//description strings
	private static final String ERROR_DESCRIPTION_LENGTH = "Character descriptions can not be longer than 200 letters.";
	
	//Prompt related strings
	private static final String PROMPT_EXIT_STRING = "exit";
	private static final String PROMPT_BACKUP_STRING = "back";
	private static final String PROMPT_EXIT_MESSAGE = "Exiting character creation prompt. Character was not created.";
	private static final String YES_NO_PROMPT_MESSAGE = "Please enter 'yes' or 'no'.";
	private static final String CONFIRM_NO_MESSAGE = "Returning to previous prompt field. Use the 'back' command to back up further to make more changes.";
	
	//Data fields
	private MudServerThread serverThread;
	private boolean prompting;
	private int promptCount;
	private PlayerCharacter userCharacter;
	private String name;
	private String race;
	private String sex;
	private String charClass;
	private String description;
	
	/**
	 * Create a new CreateCharacterPrompt and prepare it to begin prompting the user.
	 * @param serverThread The serverThread this prompt is executing from.
	 */
	public CreateCharacterPrompt(MudServerThread serverThread) {
		this.serverThread = serverThread;
		this.prompting = false;
		this.promptCount = 0;
	}
	
	/**
	 * Starts up the prompt loop. Loops until all prompt fields
	 * have been filled in properly, or until user enters the
	 * prompt exit string.
	 * @return success True indicates that the prompt succeeded
	 *                 and it is now safe to call getResults().
	 *                 False indicates that the prompt failed or was
	 *                 interrupted.
	 */
	public boolean run() {
		boolean success = false;
		this.prompting = true;
		
		//Write initial character creation message to the client.
		serverThread.write(CHARACTER_CREATION_WELCOME);
		
		//Loop until prompting is done.
		while(this.prompting) {
			switch(this.promptCount) {
				//Character name
				case 0:
					//Prompt user for character name.
					name = promptUser(CHARACTER_CREATE_NAME);
					
					//Continue if user did not enter the exit string or back string.
					if(!exitPrompt(name) && !backUpPrompt(name)) {
						//If name is valid, continue to the next prompt item.
						if(validateCharacterName(name)) {
							this.advance();
						}
					}
					break;
					
				//Character race
				case 1:
					//Prompt user for character race.
					race = promptUser(CHARACTER_CREATE_RACE);
					
					//Continue if user did not enter the exit string or back string.
					if(!exitPrompt(race) && !backUpPrompt(race)) {
						//If race is valid, continue to the next prompt item.
						if(validateCharacterRace(race)) {
							this.advance();
						}
					}
					break;
				
				//Character sex
				case 2:
					//Prompt user for character sex.
					sex = promptUser(CHARACTER_CREATE_SEX);
					
					//Continue if user did not enter the exit string or back string.
					if(!exitPrompt(sex) && !backUpPrompt(sex)) {
						//If sex is valid, continue to the next prompt item.
						if(validateCharacterSex(sex)) {
							this.advance();
						}
					}
					break;
				
				//Character class
				case 3:
					//Prompt user for character class.
					charClass = promptUser(CHARACTER_CREATE_CLASS);
					
					//Continue if user did not enter the exit string or back string.
					if(!exitPrompt(charClass) && !backUpPrompt(charClass)) {
						//If character class is valid, continue to the next prompt item.
						if(validateCharacterClass(charClass)) {
							this.advance();
						}
					}
					break;
				
				//Character description
				case 4:
					//Prompt user for character description.
					description = promptUser(CHARACTER_CREATE_DESCRIPTION);
					
					//Continue if user did not enter the exit string or back string.
					if(!exitPrompt(description) && !backUpPrompt(description)) {
						//If character description is valid we're done prompting
						if(validateCharacterDescription(description)) {
							this.advance();
						}
					}
					break;
				
				//Display user input and confirm.
				case 5:
					//Create character object from user input.
					this.userCharacter = new PlayerCharacter(this.name,
							this.description, this.charClass, this.race, this.sex);
					
					String response = this.promptUser("Creating:\n    "+this.userCharacter.describe()+"\nis this correct (yes/no)?");
					
					if(response.equalsIgnoreCase("yes")) {
						this.advance();
					}
					else if(response.equalsIgnoreCase("no")){
						serverThread.write(CONFIRM_NO_MESSAGE);
						this.back();
					}
					else {
						serverThread.write(YES_NO_PROMPT_MESSAGE);
					}
					
					break;
				
				//If we reached this state, the prompt succeeded.
				default:
					if(this.userCharacter != null) {
						success = true;
					}
					//Terminate prompt.
					this.terminate();
					break;
			}
		}
		
		//Return if the prompt was successful in creating a PlayerCharacter
		//object.
		return success;
	}
	
	/**
	 * Return the results of the prompt.
	 */
	public Object getResults() {
		return this.userCharacter;
	}
	
	/**
	 * Sets the prompt loop's flag so that the loop will be allowed to exit.
	 */
	public void terminate() {
		this.prompting = false;
	}
	
	/**
	 * Return the current status of the prompt.
	 * 
	 * @return promptStatus True if the prompt is still prompting, false otherwise.
	 */
	public boolean isPrompting() {
		return this.prompting;
	}
	
	/**
	 * Advance the prompt to the next item.
	 */
	public void advance() {
		this.promptCount++;
	}
	
	/**
	 * Check input string against the prompt backup string.
	 * If they match, decrement the prompt counter so the previous
	 * field will be prompted for next.
	 * 
	 * @param input The user input to check against the backup string.
	 * @return backUp Indicates if the prompt was backed up or not.
	 */
	public boolean backUpPrompt(String input) {
		boolean backUp = false;
		
		if(input.equalsIgnoreCase(PROMPT_BACKUP_STRING)) {
			backUp = true;
			this.back();
		}
		else {
			backUp = false;
		}
		
		return backUp;
	}
	
	/**
	 * Step the prompt back by one field.
	 * Can not back up beyond the first field.
	 */
	private void back() {
		if(this.promptCount > 0) {
			this.promptCount--;
		}
	}
	
	/**
	 * Check input string against the prompt exit string.
	 * If they match, write prompt exit message to the client and
	 * stop the current prompt.
	 * 
	 * @param input The user input to check against the exit string.
	 * @return exitPrompt Indicates if the prompt should be exited or not.
	 */
	public boolean exitPrompt(String input) {
		boolean exitPrompt = false;
		
		if(input.equalsIgnoreCase(PROMPT_EXIT_STRING)) {
			exitPrompt = true;
			serverThread.write(PROMPT_EXIT_MESSAGE);
			this.terminate();
		}
		else {
			exitPrompt = false;
		}
		
		return exitPrompt;
	}
	
	/**
	 * Send a prompt message to the client and returns user input
	 * when it is obtained.
	 * This method blocks until user input has been received.
	 * 
	 * @param message The message String to send to the client as a prompt.
	 * @return userInput The user input string response to the prompt. 
	 */
	public String promptUser(String message) {
		//Send the message string to the user as a prompt.
		serverThread.write(new Packet(message, Directive.PROMPT));
		
		//Read response from user and store it as a class.
		Packet results = serverThread.read();
		
		return results.getMessage().trim();
	}
	
	/**
	 * Validates input character name. If name is not valid,
	 * writes error to the client and returns false. Otherwise,
	 * returns true.
	 * @param name The character name string to validate.
	 * @return valid The boolean indicating if the name is valid or not.
	 */
	private boolean validateCharacterName(String name) {
		boolean valid = true;
		
		//Make sure character name is available.
		if(!characterNameAvailable(name)) {
			valid = false;
			serverThread.write(ERROR_NAME_TAKEN);
		}
		//Make sure name string is not empty.
		if(name.isEmpty()) {
			valid = false;
			serverThread.write(ERROR_NAME_EMPTY);
		}
		//Check if name contains only a-z,A-Z, and space characters.
		if(!name.matches("^[a-z A-Z]+$")) {
			valid = false;
			serverThread.write(ERROR_NAME_FORMAT);
		}
		//Check if name is between 3 and 20 characters
		if(name.length() < 3 || name.length() > 20) {
			valid = false;
			serverThread.write(ERROR_NAME_LENGTH);
		}
		
		return valid;
	}
	
	/**
	 * Checks the character map of every account file for
	 * the given name. Returns true the first time a name match
	 * is found.
	 * 
	 * @param name The name string to search for.
	 * @return success boolean, True if the character name is available.
	 *                          False if the name is taken.
	 */
	private boolean characterNameAvailable(String name) {
		 //First make sure we're ignoring case.
	     name = name.toLowerCase();
	     
	     //Create a file for the accounts directory.
		 File dir = new File(ACCOUNT_PATH);
		 
		 //If the accounts directory exists, continue.
		 if(dir.isDirectory()) {
			 //Loop through every account file in the account directory.
			 for(File accountFile : dir.listFiles()) {
				 //Load the account file
				 Account a = new Account(accountFile);
				 boolean loadSuccess = a.load();
				 
				 if(loadSuccess) {
					 //Get the character map from the account.
					 HashMap<String, PlayerCharacter> accountCharacters = a.getCharacters();
					 //If the name is in the account character map, then the name is taken
					 //and we can end the search. Otherwise, check the next account file.
					 if(accountCharacters.containsKey(name)) {
						 return false;
					 }
				 }
			 }
			 //If we went through every account and character name,
			 //then the character name is available.
			 return true;
		 }
		 //If the account directory does not exist,
		 //then we have no accounts, and all character names are available.
		 else {
			 return true;
		 }
	}
	
	/**
	 * Validates input character race. If name is not valid,
	 * writes error to the client and returns false. Otherwise,
	 * returns true.
	 * 
	 * @param race The character race string to validate.
	 * @return valid The boolean indicating if the name is valid or not.
	 */
	private boolean validateCharacterRace(String race) {
		boolean valid = true;
		
		//Make sure race string is not empty.
		if(race.isEmpty()) {
			valid = false;
			serverThread.write(ERROR_RACE_EMPTY);
		}
		//Check if race contains only a-z,A-Z, and space characters.
		if(!race.matches("^[a-z A-Z]+$")) {
			valid = false;
			serverThread.write(ERROR_RACE_FORMAT);
		}
		//Check if race is between 3 and 20 characters.
		if(race.length() < 3 || race.length() > 20) {
			valid = false;
			serverThread.write(ERROR_RACE_LENGTH);
		}
		
		return valid;
	}
	
	/**
	 * Validates input character sex. Sex can be 'male' or 'female'.
	 * Writes error message to the client if sex is invalid.
	 * 
	 * @param sex The character sex string to validate.
	 * @return valid The boolean indicating if the sex is valid or not.
	 */
	private boolean validateCharacterSex(String sex) {
		boolean valid = true;
		
		//Make sure sex string is not empty.
		if(sex.isEmpty()) {
			valid = false;
			serverThread.write(ERROR_SEX_EMPTY);
		}
		//If sex is not male and not female, invalidate.
		if(!sex.equalsIgnoreCase("male") && !sex.equalsIgnoreCase("female")) {
			valid = false;
			serverThread.write(ERROR_SEX_FORMAT);
		}
		
		return valid;
	}
	
	/**
	 * Validates input character class. If class is not valid,
	 * writes error to the client and returns false. Otherwise,
	 * returns true.
	 * 
	 * @param class The character class string to validate.
	 * @return valid The boolean indicating if the class is valid or not.
	 */
	private boolean validateCharacterClass(String charClass) {
		boolean valid = true;
		
		//Make sure class string is not empty.
		if(charClass.isEmpty()) {
			valid = false;
			serverThread.write(ERROR_CLASS_EMPTY);
		}
		//Check if class contains only a-z,A-Z, and space characters.
		if(!charClass.matches("^[a-z A-Z]+$")) {
			valid = false;
			serverThread.write(ERROR_CLASS_FORMAT);
		}
		//Check if class is between 3 and 20 characters.
		if(charClass.length() < 3 || charClass.length() > 20) {
			valid = false;
			serverThread.write(ERROR_CLASS_LENGTH);
		}
		
		return valid;
	}
	
	/**
	 * Validates input character description.
	 * Character descriptions can be any string with 200 or fewer characters.
	 * If description is not valid, writes error to the client and returns false.
	 * Otherwise, returns true.
	 * 
	 * @param description The character description string to validate.
	 * @return valid The boolean indicating if the description is valid or not.
	 */
	private boolean validateCharacterDescription(String description) {
		boolean valid = true;
		
		if(description.length() > 200) {
			valid = false;
			serverThread.write(ERROR_DESCRIPTION_LENGTH);
		}
		
		return valid;
	}
}