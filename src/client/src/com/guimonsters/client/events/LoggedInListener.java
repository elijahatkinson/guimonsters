package com.guimonsters.client.events;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import com.guimonsters.client.GameLog;
import com.guimonsters.client.GameState;
import com.guimonsters.client.MudClient;
import com.guimonsters.network.ServerConnection;

/**
 * Key listener for the LOGGED_IN state.
 * Sends all user input to the server.
 * Server responses must be handled by an instance of
 * SocketScannerThread.
 * 
 * @author Elijah Atkinson
 * @version 2.00, 2013-05-04
 */
public class LoggedInListener extends ConsoleListener implements KeyListener {

	//Listener data fields
	private static final String ERROR_CONNECTION_CLOSED = "Your connection to the server has been closed.";
	private static final String[] CHAT_COMMANDS = { "/s", "say" };
	private MudClient client;
	private ServerConnection sc;
    private GameLog gameLog;
    private String input;
	
	public LoggedInListener(MudClient client) {
		super(client.getConsole());
		this.gameLog = client.getGameLog();
		this.client = client;
	}
	
	/**
	 * Enter Key Event Handler for the LOGGED_IN state.
	 * Sends all user input strings to the server.
	 * Server responses should be listened for and handled by
	 * a SocketScannerThread instance.
	 * 
	 * Triggers if the console is focused, the
	 * Enter Key is pressed, and the client is in the LOGGED_IN state.
	 * 
	 * @param e The KeyEvent object passed by the activated JComponent.
	 */
	public void keyPressed(KeyEvent e) {
		//If enter button pressed, get the text in the console and save it as a command.
		if(e.getKeyCode() == KeyEvent.VK_ENTER) {
			//Get the current server connection object for the client.
			this.sc = this.client.getServerConnection();
			
			//Get user input from the client input field.
			input = getUserInput();
			
			//Only process non empty input.
			if(!input.isEmpty()) {
				
				//Only add user input command to the log
				//if it is NOT a chat command. This prevents double
				//logging chat commands.
				if(!isChatCommand(input)) {
					//Add the user input to the log history.
					gameLog.addUserInput(input);
				}
				
				//write user input to the server.
				//The server response will be handled by the SocketScannerThread.
				boolean writeSuccess = this.sc.write(input);
				
				//If the write failed display connection closed error message
				//and switch to logged out state.
				if(!writeSuccess) {
					gameLog.add(ERROR_CONNECTION_CLOSED);
					client.switchGameState(GameState.LOGGED_OUT);
				}
			}
		}
	}
	
	/**
	 * Determine if the user entered a chat command.
	 * If they did, don't add input to the log
	 * (this will prevent double logging of chat)
	 * @return results boolean that is true if the command matches
	 *                 a valid chat command, false otherwise.
	 */
	private boolean isChatCommand(String c) {
		String commandString = c.trim().toLowerCase();
		
		if(!commandString.isEmpty()) {
			if(c.contains(" ")) {
				//Everything before the first space is the command.
				String command = c.substring(0, c.indexOf(' '));
				
				//Loop through all valid chat commands.
				//If a match is found
				for(String chatCommand : CHAT_COMMANDS) {
					if(command.equals(chatCommand)) {
						return true;
					}
				}
			}
		}

		return false;
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