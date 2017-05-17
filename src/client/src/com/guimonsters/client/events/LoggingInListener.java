package com.guimonsters.client.events;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import com.guimonsters.client.MudClient;
import com.guimonsters.client.Account;
import com.guimonsters.client.GameState;
import com.guimonsters.client.GameLog;
import com.guimonsters.network.Directive;
import com.guimonsters.network.Packet;

/**
 * Key listener for the LOGGING_IN states.
 * Handles log in prompts.
 * 
 * @author Elijah Atkinson
 * @version 1.00, 2013-04-18
 */
public class LoggingInListener extends ConsoleListener implements KeyListener {
	
    //Prompt strings for the LOGGING_IN state.
    private static final String PROMPT_LOGIN_PASSWORD = "<< Please enter your password. >>";
    private static final String PROMPT_LOGIN_IP = "<< Please enter your server's IP address. >>";
	
    //Listener data fields
    private MudClient client;
    private GameLog gameLog;
    private Account userAccount;
    private String input;
	private int logInFieldsSubmitted = 0;
	private String accountName;
	private String password;
	private String serverIp;
	
	/**
	 * Create a listener to handle enter key pressed events
	 * for the game console component.
	 * @param client The master MudClient application object.
	 */
	public LoggingInListener(MudClient client) {
		super(client.getConsole());
		
		this.gameLog = client.getGameLog();
		this.userAccount = client.getUserAccount();
		
		this.client = client;
	}
	
	/**
	 * Enter Key Event Handler for the LOGGING_IN state.
	 * Triggers if the console is focused and the
	 * Enter Key is pressed while the client is in the LOGGING_IN state.
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
				logInFieldsSubmitted++;
				
				//If this is the first entry in the state, it must be the user name.
				//(The prompt for the user name is printed to the screen by the LOG_IN command
				//in the LoggedOutCommands class.)
				if(logInFieldsSubmitted == 1) {
					accountName = input;
					
					//Add the input user name to the log history.
	    			gameLog.addUserInput(input);
					
					//TODO Make the console interpret the next input as a password.
	
					gameLog.add(PROMPT_LOGIN_PASSWORD, false);
				}
				//if this is the second entry in the state, it must be the password.
				else if(logInFieldsSubmitted == 2) {
					password = input;
					
					//Add the input password to the log history using *s for each character.
	    			gameLog.addUserInput(input.replaceAll(".", "*"));
					
					//TODO Switch the console back to normal input mode here.
					
					gameLog.add(PROMPT_LOGIN_IP, false);
				}
				//If this is the third and final entry in the state, it must be the IP address.
				else if(logInFieldsSubmitted == 3) {
					serverIp = input;
					
					//Add the input server IP address to the log history.
	    			gameLog.addUserInput(input);
					
	    			//Use the ip address provided by the user.
					client.setServerIpAddress(serverIp);
	    			
					gameLog.add("Attempting to connect to '"+serverIp+"' with account '"+accountName+"'.");
					
					//Make an account object from user input.
					userAccount = new Account(client.getServerConnection(), accountName, password);
					
					//Send login request to the server and get response.
					Packet resultsPacket = userAccount.logIn();
					
					String resultsMessage = resultsPacket.getMessage();
					Directive resultsDirective = resultsPacket.getDirective();
					
					//Display the login results message.
					gameLog.add(resultsMessage);
					
					//Login failed, go back to logged out state.
					if(resultsDirective.equals(Directive.LOGIN_FALSE)) {
						client.switchGameState(GameState.LOGGED_OUT);
					}
					//Login succeeded.
					else {
						//Set the new account as the active account.
						client.setUserAccount(userAccount);
						client.switchGameState(GameState.LOGGED_IN);
					}
				}
			}	
		}
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