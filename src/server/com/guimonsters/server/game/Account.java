package com.guimonsters.server.game;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.guimonsters.server.file.Serializer;

/**
 * An account contains the player information that the game uses for login,
 * password reset, and keeps track of characters associated with the account.
 * 
 * @author Curran Hamilton
 * @author Kendall Lewis
 * @author Elijah Atkinson
 * @author Stephen Butler
 * @version 2.00, 2013-05-05
 */
public class Account implements Serializable {
	
	private static final long serialVersionUID = 8620369887789374381L;
	
	private static final String ACCOUNT_DIRECTORY = "./accounts";
	private static final String MESSAGE_NO_CHARACTERS = "You have no characters.";
	private static final String MESSAGE_HAVE_CHARACTERS = "Your characters are: ";
	
	//Data Fields
	private String accountName;
	private String password;
	private File accountFile;
	private HashMap<String, PlayerCharacter> characters;
	
	//Static Constants
	public static final String SALT = "sdfghdsljui783289703264!#@#$%^(^*hjfgftkyo2448697*%^%(*^()itykfhg564($$(";
	
	/**
	 * Create an account object with the given account name and password.
	 * @param a The account name. Used as email as well as account name.
	 * @param p The client's account password String.
	 * @throws UnsupportedEncodingException 
	 * @throws NoSuchAlgorithmException 
	 */
	public Account(String a, String p) {
		this.accountName = a;
		this.password = getHash(p, SALT);
		this.characters = new HashMap<String, PlayerCharacter>();
		this.accountFile = new File(ACCOUNT_DIRECTORY+"/"+this.accountName + ".ser");
	}
	
	/**
	 * Create an account object with the given account name and password.
	 * @param a The account name. Used as email as well as account name.
	 * @param p The servers's account password string.
	 * @param cs The server's character hash map
	 * @throws UnsupportedEncodingException 
	 * @throws NoSuchAlgorithmException
	 */
	public Account(String a, String p, HashMap<String, PlayerCharacter> cs) {
		this.accountName = a;
		this.password = getHash(p, SALT);
		this.characters = cs;
		this.accountFile = new File(ACCOUNT_DIRECTORY+"/"+this.accountName + ".ser");
	}
	
	/**
	 * Create an account object from a file.
	 * Call load() method to finish loading this account file.
	 * 
	 * @param accountFile The account file to load.
	 */
	public Account(File accountFile) {
		this.accountFile = accountFile;
	}
	
	/**
	 * Load an account file and overwrite this accounts data fields
	 * from the file.
	 * @return success boolean, true is load succeeded, false otherwise.
	 */
	public boolean load() {
		boolean success = false;
		Serializer serial = new Serializer(this.accountFile);
		
		try {
			//Load account data from the serialized file associated
			//with this account object.
			Account accountFromFile = (Account) serial.deserialize();
			
			this.accountName = accountFromFile.accountName;
			this.password = accountFromFile.password;
			this.characters = accountFromFile.characters;
			
			success = true;
		}
		catch (IOException | ClassNotFoundException e) {
			success = false;
		}
		
		return success;
	}
	
	/**
	 * Creates an account file if it doesn't already exist.
	 * If file already exists, returns false.
	 * @return success The flag indicating if an account file was created or not.
	 */
	public boolean create() {
		boolean success = false;
		
		try {
			//Create a new file object in the account path
			//(files are named after the accountName)
			if(this.accountFile.createNewFile()) {
				//Save the new account.
				success = this.save();
			}
			else {
				success = false;
			}
		}
		catch (IOException e) {
			success = false;
			e.printStackTrace();
		}
		
		return success;
	}
	
	/**
	 * Writes serialized data of the current account object
	 * into the account file associated with the account.
	 * 
	 * @return success The boolean indicating if the save was successful.
	 */
	public boolean save() {
		//Write this account object into the accountFile.
		Serializer serial = new Serializer(this.accountFile, this);
		boolean success = serial.serialize();
		
		return success;
	}
	
	/**
	 * Load the account file associated with this account
	 * and compare the file password to the user given password.
	 * If they match, the given account name and password are valid,
	 * log the user in and load character data from file.
	 * @return success The boolean indicating whether the user can log in or not.
	 */
	public boolean logIn() {
		boolean success = false;
		Account accountFromFile;
		
		//If the account file exists on the file system, continue.
		if(this.accountFile.exists()) {
			
			Serializer serial = new Serializer(this.accountFile);
			
			try {
				//Load account data from the serialized file associated
				//with this account object.
				accountFromFile = (Account) serial.deserialize();
				
				//If the user given password matches the password from the file,
				//then get character data from file and return true.
				if(this.password.equals(accountFromFile.password)) {
					success = true;
					this.characters = accountFromFile.characters;
			    }
				//Passwords do not match, user can't login.
				else {
					success = false;
				}
			}
			catch (IOException | ClassNotFoundException e) {
				success = false;
				e.printStackTrace();
			}
		}
		//If the account file does not exist on the file system
		//user can not log in, return false.
		else {
			return false;
		}
		
	    return success;
	}

	/**
	 * Adds a PlayerCharacter to the account and saves the account to file.
	 * Characters are mapped to their lower case name String.
	 * @param pc The PlayerCharacter to add to the account.
	 */
	public void addCharacter(PlayerCharacter pc) {
		this.characters.put(pc.getName().toLowerCase(), pc);
		this.save();
	}
	
	/**
	 * Returns a string that lists all PlayerCharacters for this account
	 * and their compound descriptions.
	 * @return results The String listing all account characters.
	 */
	public String listCharacters() {
		String results;
			
		if(this.characters.isEmpty()) {
			results = MESSAGE_NO_CHARACTERS;
		}
		else {
			results = MESSAGE_HAVE_CHARACTERS;
			
			//Create an iterator to walk through the entire characters hash map.
			Iterator<Map.Entry<String, PlayerCharacter>> entries = this.characters.entrySet().iterator();
			
			//Loop through every character in the command map and build up the
			//display string for them.
			while(entries.hasNext()) {
				Map.Entry<String, PlayerCharacter> entry = entries.next();
				
				results += "\n    "+entry.getValue().describe();
			}
		}
		
		return results;
	}
	
	/**
	 * Sha256 hash a string using a given salt.
	 * @param input The user input String to hash.
	 * @param salt Static salt 
	 * @return result The resulting hashed string.
	 */
	private String getHash(String input, String salt) {
		String result;
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			digest.reset();
			digest.update(salt.getBytes("UTF-8"));
			
			//Calculate the hash and store as a byte array.
			byte[] hash = digest.digest(input.getBytes("UTF-8"));
			
			//Convert the hash byte array into a hex string.
			result = this.convertToHexString(hash);
		}
		catch(NoSuchAlgorithmException | UnsupportedEncodingException e) {
			result = null;
			e.printStackTrace();
		}
		
		return result;
	}
	
	/**
	 * Converts a byte array into a hexadecimal string.
	 * @param bytes The array of bytes to convert to a hex String.
	 * @return result The converted string.
	 */
	private String convertToHexString(byte[] bytes) {
		StringBuffer hexString = new StringBuffer();
		String result;
		
		//Convert our input byte array back to a hexadecimal string.
		for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xff & bytes[i]);
            if(hex.length() == 1) {
            	hexString.append('0');
            }
            hexString.append(hex);
        }
		
		//Convert the byte array back to a string.
		result = hexString.toString();
		return result;
	}
	
	//Getters
	//-------------------------------------------------
	public String getAccountName() {
		return this.accountName;
	}
	
	public String getPassword() {
		return this.password;
	}
	
	//Setters
	//--------------------------------------------------
	public void setAccountName(String name) {
		this.accountName = name;
	}
	
	public void setPassword(String p) {
		this.password = getHash(p, SALT);
	}

	public HashMap<String, PlayerCharacter> getCharacters() {
		return characters;
	}
	
}
