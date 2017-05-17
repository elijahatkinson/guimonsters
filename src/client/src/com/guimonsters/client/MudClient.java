package com.guimonsters.client;

import java.awt.event.*;
import javax.swing.*;
import javax.swing.GroupLayout.*;

import com.guimonsters.client.events.*;
import com.guimonsters.network.ServerConnection;
import com.guimonsters.network.SocketScannerThread;

/**
 * A Swing GUI Mud Client.
 * (Iteration 02)
 * 
 * @author Elijah Atkinson
 * @author Stephen Butler
 * @author Curran Hamilton
 * @author Kendall Lewis
 * @version 2.00, 2013-05-01
 */
public class MudClient extends JFrame {
	
	//Main Client Data Fields
	private static final long serialVersionUID = -1959232992078433666L;
	private static final  int DEFAULT_WIDTH = 800;
    private static final  int DEFAULT_HEIGHT = 200;
    private static final  String APPLICATION_TITLE = "GUI Monsters Client";
	//Label for the user input field.
    private static final String STRING_CONSOLE_LABEL = "Command:";
    private static final String WELCOME_MESSAGE = "Welcome to the GUI_Monsters MUD client.";
    
    //Component objects
	private JLabel prompt;
	private JTextField console;
	private JScrollPane gameLogPane;
	
	//Handles display of all game text.
	private GameLog gameLog;
	
	//Handles parsing of all game commands.
	private GameCommands gameCommands;
	
	//Tracks user account information.
	private Account userAccount;
	
	//Client instance of the game state ENum.
	private GameState gameState;
	
	//Handle this Client instance's connection to the server.
	private ServerConnection sc;
    private String serverIpAddress = "127.0.0.1";
    private int serverPort = 7777;
    private SocketScannerThread socketScanner;
	
	/**
	 * Main MudClient Constructor. 
	 * Initializes GUI Components and attaches event listeners.
	 */
	public MudClient() {
		//Set main application window settings.
		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		this.setTitle(APPLICATION_TITLE);
		this.setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
		
		//Configure and initialize GUI components.
		initComponents();
		
		//Adjust the gameLog to display the last line when the client
		//application is resized.
		this.addComponentListener(new ClientResizeListener(this.gameLog));
		
		//Focus on the input box
		console.requestFocusInWindow();
		
		//Create a ServerConnection object, but don't attempt to connect yet.
		this.sc = new ServerConnection(serverIpAddress, serverPort);
		
		//Display the welcome method.
		this.gameLog.add(WELCOME_MESSAGE);
		
		//Start the client in logged out state.
		//Use the logged out listener for console input.
		//Use the logged out commands.
		this.switchGameState(GameState.LOGGED_OUT);
	}
	
	/**
	 * Set up and add GUI components to the application.
	 */
	private void initComponents() {
		prompt = new JLabel();
		console = new JTextField();
		gameLog = new GameLog();
		
		//Set the prompt label text.
		prompt.setText(STRING_CONSOLE_LABEL);
		
		//Allow the console to be focused on.
		console.setFocusable(true);
		
		//Build the scroll-able gameLogPane using the gameLog object.
		gameLogPane = new JScrollPane(gameLog);
		gameLog.draw();
		
		//Create a layout object for the client GUI.
		GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
		
		//Create a parallel group for the horizontal axis
	    ParallelGroup hGroup = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
	     
	    //Create a sequential group to hold h2 followed by a container gap.
	    SequentialGroup h1 = layout.createSequentialGroup();
	    //Create the parallel group to hold the prompt label and the console input field.
	    ParallelGroup h2 = layout.createParallelGroup(GroupLayout.Alignment.TRAILING);
	    
	    //Add a container gap to the sequential group h1
	    h1.addContainerGap();
	     
	    //Add a scroll pane to the parallel group h2
	    h2.addComponent(gameLogPane, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, DEFAULT_WIDTH, Short.MAX_VALUE);
	     
	    //Create a sequential group h3
	    SequentialGroup h3 = layout.createSequentialGroup();
	    h3.addComponent(prompt);
	    h3.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED);
	    h3.addComponent(console, GroupLayout.DEFAULT_SIZE, 440, Short.MAX_VALUE);
	      
	    //Add the group h3 to the group h2
	    h2.addGroup(h3);
	    //Add the group h2 to the group h1
	    h1.addGroup(h2);
	 
	    h1.addContainerGap();
	     
	    //Add the group h1 to the hGroup
	    hGroup.addGroup(GroupLayout.Alignment.TRAILING, h1);
	    //Create the horizontal group
	    layout.setHorizontalGroup(hGroup);
	     
	    //Create a parallel group for the vertical axis
	    ParallelGroup vGroup = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
	    //Create a sequential group v1
	    SequentialGroup v1 = layout.createSequentialGroup();
	    //Add a container gap to the sequential group v1
	    v1.addContainerGap();
	    //Create a parallel group v2
	    ParallelGroup v2 = layout.createParallelGroup(GroupLayout.Alignment.BASELINE);
	    v2.addComponent(prompt);
	    v2.addComponent(console, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE);
	   
	    v1.addComponent(gameLogPane, GroupLayout.DEFAULT_SIZE, DEFAULT_HEIGHT, Short.MAX_VALUE);
	    v1.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED);
	    
	    v1.addGroup(v2);
	    v1.addContainerGap();
	     
	    //Add the group v1 to the group vGroup
	    vGroup.addGroup(v1);
	    //Create the vertical group
	    layout.setVerticalGroup(vGroup);

	    pack();
	}
	
	/**
	 * Switch to a new game state.
	 * Updates the console key listener to match the new state.
	 * @param state The GameState to switch to.
	 */
	public void switchGameState(GameState state) {
		//Update the game state to the new state.
		switch (state) {
			case LOGGED_OUT:
				this.gameState = state;
				//Terminate the socket scanner thread instance if it exists
				//and is still running.
				terminateSocketScanner(this.socketScanner);
				this.gameCommands = new LoggedOutCommands(this);
				this.switchConsoleKeyListener(new LoggedOutListener(this));
				break;
				
			case LOGGING_IN:
				this.gameState = state;
				this.switchConsoleKeyListener(new LoggingInListener(this));
				break;
				
			case CREATING_ACCOUNT:
				this.gameState = state;
				this.switchConsoleKeyListener(new CreateAccountListener(this));
				break;
				
			case LOGGED_IN:
				this.gameState = state;
				//Begin scanning for input from the server.
				this.socketScanner = new SocketScannerThread(this);
				this.socketScanner.start();
				//Sends user input to the server.
				this.switchConsoleKeyListener(new LoggedInListener(this));
				break;
				
			default:
				//Don't do anything if any other state is given.
				break;
		}
	}
	
	/**
	 * Terminates a socket scanner thread if it exists and is currently running.
	 * @param scanner The SocketScannerThread to terminate.
	 */
	private void terminateSocketScanner(SocketScannerThread scanner) {
		if(scanner != null && scanner.isAlive()) {
			scanner.terminate();
		}
	}
	
	/**
	 * Remove all of the old console key listeners and add a new one.
	 * @param newListener The new KeyListener to attach to the console.
	 */
	private void switchConsoleKeyListener(KeyListener newListener) {
		this.removeConsoleKeyListeners();
		this.console.addKeyListener(newListener);
	}
	
	/**
	 * Remove all key listeners attached to the game console.
	 */
	private void removeConsoleKeyListeners() {
		for(KeyListener kl : this.console.getKeyListeners()) {
			this.console.removeKeyListener(kl);
		}
	}

	//Getters and Setters
	//--------------------------------------------------------------------
	public JLabel getPrompt() {
		return prompt;
	}

	public void setPrompt(JLabel prompt) {
		this.prompt = prompt;
	}

	public JTextField getConsole() {
		return console;
	}

	public void setConsole(JTextField console) {
		this.console = console;
	}

	public GameLog getGameLog() {
		return gameLog;
	}

	public GameCommands getGameCommands() {
		return gameCommands;
	}

	public void setGameCommands(GameCommands gameCommands) {
		this.gameCommands = gameCommands;
	}

	public Account getUserAccount() {
		return userAccount;
	}

	public void setUserAccount(Account userAccount) {
		this.userAccount = userAccount;
	}

	public GameState getGameState() {
		return gameState;
	}
	
	public ServerConnection getServerConnection() {
		return this.sc;
	}

	public String getServerIpAddress() {
		return serverIpAddress;
	}

	public void setServerIpAddress(String serverIpAddress) {
		this.serverIpAddress = serverIpAddress;
		this.sc.setIpAddress(serverIpAddress);
	}

	public int getServerPort() {
		return serverPort;
	}

	public void setServerPort(int serverPort) {
		this.serverPort = serverPort;
		this.sc.setPort(serverPort);
	}

	/**
	 * Create and show the client GUI.
	 * @param args The main application arguments.
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
	        public void run() {
	            UIManager.put("swing.boldMetal", Boolean.FALSE);
	            new MudClient().setVisible(true);
	        }
	    });
	}
}