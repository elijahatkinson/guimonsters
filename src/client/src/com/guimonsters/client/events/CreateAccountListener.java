package com.guimonsters.client.events;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import com.guimonsters.client.Account;
import com.guimonsters.client.GameLog;
import com.guimonsters.client.GameState;
import com.guimonsters.client.MudClient;

/**
 * Key listener for the CREATING_ACCOUNT state.
 * Allows the user to input information and create a new account.
 * 
 * Currently a stub, needs to have a menu handler added to it
 * (similar to loggingInListener).
 * 
 * @author Elijah Atkinson
 * @author Stephen Butler
 * @author Curran Hamilton
 * @version 2.00, 2013-05-05
 */
public class CreateAccountListener extends ConsoleListener implements KeyListener {
	
    //Message strings for the CREATING_ACCOUNT state.
	private static final String CREATE_FAIL = "Failed to create account. " +
			"Invalid server address or an account with that name exists already.";
	
	//Prompt strings for the CREATING_ACCOUNT state.
	private static final String PROMPT_CREATE_ACCOUNT = "<< Please enter desired account name or email address. >>";
    private static final String PROMPT_CREATE_PASSWORD1 = "<< Please enter your password. >>";
    private static final String PROMPT_CREATE_PASSWORD2 = "<< Please re-eneter your password. >>";
    private static final String PROMPT_CREATE_IP = "<< Please enter your server's IP address. >>";
    //Error strings for the CREATING_ACCOUNT state.
    private static final String ERROR_PASSWORD = "Your passwords do not match. Nice try.";
	
    //Listener data fields
    private MudClient client;
    private GameLog gameLog;
    private Account userAccount;
    private String input;
	private int fieldsSubmitted = 0;
	private String username;
	private String password1;
	private String password2;
	private String serverIp;
	
	/**
	 * Create a listener to handle enter key pressed events
	 * for the game console component.
	 * @param client The master MudClient application object.
	 */
	public CreateAccountListener(MudClient client) {
		super(client.getConsole());
		
		this.gameLog = client.getGameLog();
		this.userAccount = client.getUserAccount();
		
		this.client = client;
	}
	
	/**
	 * Enter Key Event Handler for the CREATING_ACCOUNT state.
	 * Triggers if the console is focused and the
	 * Enter Key is pressed while the client is in the CREATING_ACCOUNT state.
	 * 
	 * @param e The KeyEvent object passed by the activated JComponent.
	 */
	public void keyPressed(KeyEvent e) {
		//If enter button pressed, get the text in the console and save it as input.
		if(e.getKeyCode() == KeyEvent.VK_ENTER) {
			
			//Grab the user input.
			input = getUserInput();
			if (this.validate(input)) {
				
				//Count this input as one of the required fields for this state.
				fieldsSubmitted++;
				
				//If this is the first entry in the state, it must be the user name.
				//(The prompt for the user name is printed to the screen by the create() command
				//in the LoggedOutCommands class.)
				if(fieldsSubmitted == 1) {
					username = input;
					
					//Make sure username is a valid account name.
					if(validateAccountName(username)) {
						//Add the input user name to the log history.
		    			gameLog.addUserInput(input);
						
						//TODO Make the console interpret the next input as a password.
		
						gameLog.add(PROMPT_CREATE_PASSWORD1, false);
					}
					//Validation failed, display error and re-prompt for account name.
					else {
						gameLog.add("Account names can only contain letters, numbers, and @._- characters.");
						gameLog.add(PROMPT_CREATE_ACCOUNT, false);
						fieldsSubmitted--;
					}
				}
				//If this is the second entry in the state, it must be the first password.
				else if(fieldsSubmitted == 2) {
					password1 = input;
					
					//Add the input password to the log history using *s for each character.
	    			gameLog.addUserInput(input.replaceAll(".", "*"));
					
					gameLog.add(PROMPT_CREATE_PASSWORD2, false);
				}
				//If this is the third entry in the state, it must be the second password.
				else if(fieldsSubmitted == 3) {
					password2 = input;
					
					//Add the input password to the log history using *s for each character.
	    			gameLog.addUserInput(input.replaceAll(".", "*"));
					
					//TODO Switch the console back to normal input mode here.
	    			if (password1.equals(password2)) {
	    				gameLog.add(PROMPT_CREATE_IP, false);
	    			}
	    			else{
	    				fieldsSubmitted = 1;
	    				gameLog.add(ERROR_PASSWORD);
	    				gameLog.add(PROMPT_CREATE_PASSWORD1, false);
	    			}
				}
				//If this is the fifth and final entry in the state, it must be the ip address.
				else if(fieldsSubmitted == 4) {
					serverIp = input;
					
					//Add the input ip address to the log history.
	    			gameLog.addUserInput(input);
					
	    			//Use the ip address provided by the user for the connection to the server.
					client.setServerIpAddress(serverIp);
					
					//Create account object with provided user information.
					gameLog.add("Creating account with account name '" + username + "'.");
					userAccount = new Account(client.getServerConnection(), username, password1);
					
					//Attempt to create a new account on the server.
					if(userAccount.create()) {
						//Set the new account as the active client account.
						client.setUserAccount(userAccount);
						gameLog.add("Account '" + username + "' created successfully. Logging in.");
						
						//Log the user in with their new account now.
						client.switchGameState(GameState.LOGGED_IN);
					}
					else {
						//Creation failed. Go back to logged out state.
						gameLog.add(CREATE_FAIL);
						client.switchGameState(GameState.LOGGED_OUT);
					}
				}
			}
		}	
	}
	
	/**
	 * Validates input account name. Account names can only have
	 * letters, numbers, @._- characters in them.
	 * @param name The character name string to validate.
	 * @return valid The boolean indicating if the name is valid or not.
	 */
	private boolean validateAccountName(String name) {
		boolean valid = true;
		
		//TODO check if character name is taken by any other account.
		
		//Make sure name string is not empty.
		if(name.isEmpty()) {
			valid = false;
		}
		//Check if name contains valid characters
		//This is the regex for emails... not sure if we should use it
		//^([a-z0-9_\.-]+)@([\da-z\.-]+)\.([a-z\.]{2,6})$
		else if(!name.matches("^[a-zA-Z0-9@._-]+$")) {
			valid = false;
		}
		//Check if name is between 3 and 30 characters
		else if(name.length() < 3 || name.length() > 30) {
			valid = false;
		}
		else {
			valid = true;
		}
		
		return valid;
	}
	
	
	/**
	 * Currently not used.
	 */
	public void keyTyped(KeyEvent e) {
		//No keyTyped listener.
	}
	/**
	 * Currently not used.
	 */
	public void keyReleased(KeyEvent e)	{
		//No keyReleased listener.
	}
}