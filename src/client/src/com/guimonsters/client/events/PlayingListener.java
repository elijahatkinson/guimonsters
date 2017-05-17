package com.guimonsters.client.events;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JTextField;

import com.guimonsters.client.Account;
import com.guimonsters.client.GameCommands;
import com.guimonsters.client.GameLog;
import com.guimonsters.client.MudClient;

/**
 * Key listener for the PLAYING state.
 * Handles user game commands.
 * 
 * @author Elijah Atkinson
 * @version 1.00, 2013-04-18
 */
public class PlayingListener extends ConsoleListener implements KeyListener {

	//Listener data fields
	private MudClient client;
    private GameCommands gameCommands;
    private GameLog gameLog;
    private Account userAccount;
    private String input;
	
	public PlayingListener(MudClient client) {
		super(client.getConsole());
		
		this.gameLog = client.getGameLog();
		this.gameCommands = client.getGameCommands();
		this.userAccount = client.getUserAccount();
		
		this.client = client;
	}
	
	/**
	 * Enter Key Event Handler for the LOGGED_IN state.
	 * Triggers if the console is focused and the
	 * Enter Key is pressed.
	 * 
	 * @param e The KeyEvent object passed by the activated JComponent.
	 */
	public void keyPressed(KeyEvent e) {
		//If enter button pressed, get the text in the console and save it as a command.
		if(e.getKeyCode() == KeyEvent.VK_ENTER) {
			
			input = getUserInput();
			//Add the user input to the log history.
			gameLog.addUserInput(input);
			//Parse the input command and save the results.
			String results = gameCommands.parseCommand(input);
			//If the results are not null and not empty, add them to the gameLog.
			if(results != null && !results.isEmpty()) {
				gameLog.add(results);
			}
		}
	}
	/**
	 * Not currently used.
	 */
	public void keyTyped(KeyEvent e) {
		//No keyTyped listener.
	}
	/**
	 * Not currently used.
	 */
	public void keyReleased(KeyEvent e)	{
		//No keyReleased listener.
	}
}