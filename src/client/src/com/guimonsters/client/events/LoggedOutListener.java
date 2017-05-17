package com.guimonsters.client.events;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import com.guimonsters.client.MudClient;
import com.guimonsters.client.Account;
import com.guimonsters.client.GameLog;

/**
 * Key listener for the LOGGED_OUT state.
 * Provides the user with the option to create an account or log in.
 * 
 * @author Elijah Atkinson
 * @version 1.00, 2013-04-18
 */
public class LoggedOutListener extends ConsoleListener implements KeyListener {
	
    //Listener data fields
    private MudClient client;
    private GameLog gameLog;
    private Account userAccount;
    private String input;
	
	/**
	 * Create a listener to handle enter key pressed events
	 * for the game console component.
	 * @param client The master MudClient application object.
	 */
	public LoggedOutListener(MudClient client) {
		super(client.getConsole());
		
		this.gameLog = client.getGameLog();
		this.userAccount = client.getUserAccount();
		
		this.client = client;
	}
	
	/**
	 * Enter Key Event Handler for the LOGGED_OUT and LOGGING_IN states.
	 * Triggers if the console is focused and the
	 * Enter Key is pressed while the client is in the LOGGED_OUT or LOGGING_IN state.
	 * 
	 * @param e The KeyEvent object passed by the activated JComponent.
	 */
	public void keyPressed(KeyEvent e) {
		//If enter button pressed, get the text in the console and save it as input.
		if(e.getKeyCode() == KeyEvent.VK_ENTER) {
			
			//Grab the user input.
			input = getUserInput();
			
			//Add the user input to the log history.
			gameLog.addUserInput(input);
			//Parse the input command and save the results.
			String results = client.getGameCommands().parseCommand(input);
			
			//If the results are not null and not empty, continue.
			if(results != null && !results.isEmpty()) {
				//Add the results to the game log.
				gameLog.add(results);
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
