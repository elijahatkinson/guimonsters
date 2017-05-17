package com.guimonsters.client;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.guimonsters.network.Directive;
import com.guimonsters.network.Packet;
import com.guimonsters.network.ServerConnection;

/**
 * Client side accounts collect user input for the login prompt
 * and account creation prompt and manage the server login process.
 * This class also sha256 hashes user passwords before they are sent to the server.
 * 
 * @author Curran Hamilton
 * @author Kendall Lewis
 * @author Elijah Atkinson
 * @author Stephen Butler
 * @version 2.00, 2013-05-05
 */
public class Account {
	
	//Data Fields
	private ServerConnection sc;
	private String accountName;
	private String password;
	
	//Static Fields
	private static final String SALT = "@#$%^&*78q96874302189387%&%43663420951#$84325827^&*$^394870(&^$%$8709uioghjfgw$#&^*(";
	
	/**
	 * Create an account class that requires an account name and a password.
	 * Account names should be valid email addresses.
	 * @param connection The ServerConnection object that
	 * 		             manages the connection to the server.
	 * @param name The account name String.
	 * @param password The account password string.
	 * @throws UnsupportedEncodingException 
	 * @throws NoSuchAlgorithmException 
	 */
	public Account(ServerConnection connection, String name, String password) {
		this.sc = connection;
		this.accountName = name;
		this.password = getHash(password, SALT);
	}
	
	/**
	 * Attempt to create an account on the server.
	 * 
	 * @return results The boolean describing whether the account creation
	 * 				   succeeded or not.
	 */
	public boolean create() {
		boolean success = false;
		
		//Attempt to connect to the server.
		boolean connected = sc.connect();
		if(connected) {
			//Write the login request out to the server.
			sc.write("create_account "+this.accountName+" "+this.password);
			
			//Read the response from the server.
			Packet responsePacket = sc.read();
			
			//If we received a null packet from server, the creation failed.
			if(responsePacket == null) {
				success = false;
				sc.close();
			}
			//Otherwise, check server response.
			else {
				Directive responseDirective = responsePacket.getDirective();
				
				if(responseDirective == Directive.SERV_CREATE_ACCOUNT) {
					success = true;
				}
				else {
					success = false;
				}
			}
		}
		else {
			success = false;
		}
		
		return success;
	}
	

	/**
	 * Attempt to log the account into the server.
	 * 
	 * @return results The packet describing the status of the login.
	 * 		If the login failed, package contains Directive.LOGIN_FALSE,
	 *      if the login succeeded, package contains Directive.LOGIN_TRUE.
	 */
	public Packet logIn() {
		Packet results;
		
		//Attempt to connect to the server.
		boolean connected = sc.connect();
		if(connected) {
			//Write the login request out to the server.
			sc.write("login "+this.accountName+" "+this.password);
			
			//Read the response from the server.
			results = sc.read();
			
			if(results == null) {
				results = new Packet("Connection to server failed.", Directive.LOGIN_FALSE);
			}
			else {
				Directive responseDirective = results.getDirective();
				
				//If the directive from the server matches the login failure directive,
				//the login failed. Close the connection to the server.
				//Otherwise, the user logged in successfully.
				if(responseDirective.equals(Directive.LOGIN_FALSE)) {
					sc.close();
				}
			}
		}
		else {
			results = new Packet("Connection to server failed.", Directive.LOGIN_FALSE);
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
}
