package com.guimonsters.client;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import javax.swing.JTextArea;

/**
 * A customized JTextArea that keeps track of user input and server responses.
 * @author Elijah Atkinson
 * @version 2.00, 2013-05-02
 */
public class GameLog extends JTextArea {
	
	//Private class strings
	private static final long serialVersionUID = 7344196085622309407L;
	private static final String HISTORY_ENTRY_OPEN = "[ ";
	private static final String HISTORY_ENTRY_CLOSE = " ]";
	private static final String USER_ACTION_CARET = "> ";
	
	//Text and component colors.
    private final static Color BG_COLOR = Color.BLACK;
    //private final static Color SYSTEM_COLOR = Color.GRAY;
    private final static Color SYSTEM_COLOR = new Color(192, 192, 192);
    private final static Color SELECTED_TEXT = Color.WHITE;
    private final static Color SELECTED_BG = Color.BLUE;
    //Font styles
    //private final static Font MONOSPACED_FONT = Font.decode("Courier New");
    private final static Font MONOSPACED_FONT = Font.decode("Courier");
    
    
    //Stores all commands and responses from the game server.
  	//One index per line will be printed to the gameLog text area.
  	private ArrayList<String> gameHistory;
  	
	//The number of past entries to display in the log.
	private int historySize = 100;

	/**
	 * Create an instance of GameLog.
	 * GameLog keeps track of user input and responses from the MUD Server.
	 */
	public GameLog() {
		super();
		
		//Set the initial rows of the game log to match the
		//length of the gameHistory array. We want 1 row for
		//every entry in the gameHistory.
		this.setRows(historySize);
		this.setEditable(false);
		this.setAlignmentX(LEFT_ALIGNMENT);
		this.setAlignmentY(CENTER_ALIGNMENT);
		this.setFont(MONOSPACED_FONT);
		this.setBackground(BG_COLOR);
		this.setForeground(SYSTEM_COLOR);
		this.setSelectedTextColor(SELECTED_TEXT);
		this.setSelectionColor(SELECTED_BG);
		this.setLineWrap(true);
		this.setWrapStyleWord(true);
		
		//Create an ArrayList of size HISTORY_SIZE to store GameLog entries.
		gameHistory = new ArrayList<String>(historySize);
		
		//Fill all but one of the GameLog rows with blank strings to start.
		for(int i=0; i<historySize-1; i++) {
			gameHistory.add("");
		}
	}
	
	/**
	 * Add each entry in the gameHistory ArrayList to to the gameLog
	 * and scroll to the bottom of the log.
	 */
	public void draw() {
		//Clear the textArea before we redraw.
		this.setText("");
		
		//Only draw the game history if it is not empty.
		if(!gameHistory.isEmpty()) {
			
			//Add the first entry from gameHistory to the GameLog instance.
			this.append(" "+gameHistory.get(0));
			//Add every entry after the first to the GameLog prepended by a new line.
			for(int i=1; i<gameHistory.size(); i++) {
				this.append("\n"+gameHistory.get(i));
			}
		}
		
		this.scrollToBottom();
	}
	
	/**
	 * Add a line to the GameLog and re-draw it.
	 * Lines are wrapped with brackets by default.
	 * @param line The String to add to the GameLog.
	 */
	public void add(String line) {
		gameHistory.add(HISTORY_ENTRY_OPEN+line+HISTORY_ENTRY_CLOSE);
		if(gameHistory.size() >= historySize) {
			gameHistory.remove(0);
		}
		this.draw();
	}
	
	/**
	 * Add a line to the GameLog and re-draw it.
	 * If brackets is false, line will be drawn without brackets.
	 * @param line The String to add to the GameLog.
	 * @param brackets Boolean flag to set if brackets are added to the line.
	 */
	public void add(String line, Boolean brackets) {
		if(brackets == false) {
			//Draw the line without brackets.
			gameHistory.add(line);
			if(gameHistory.size() >= historySize) {
				gameHistory.remove(0);
			}
			this.draw();
		}
		else {
			//Draw the line with brackets.
			this.add(line);
		}
	}
	
	/**
	 * Add a line of user input to the GameLog and re-draw it.
	 * User input lines are prefaced with a prompt character
	 * and are not wrapped by brackets.
	 * @param line The user string to add to the GameLog.
	 */
	public void addUserInput(String line) {
		this.add(USER_ACTION_CARET+line, false);
	}
	
	/**
	 * Initialize a new, clean gameHistory and draw it.
	 */
	public void clear() {
		gameHistory = new ArrayList<String>(historySize);
		//Fill all of the GameLog rows with blank strings to start.
		for(int i=0; i<historySize; i++) {
			gameHistory.add("");
		}
		this.draw();
	}
	
	/**
	 * Scroll the GameLog to the bottom of the History.
	 */
	public void scrollToBottom() {
		//
		this.selectAll();
		int x = this.getSelectionEnd();
		this.select(x, x);
	}
	
	/**
	 * Scroll the GameLog to the top of the History.
	 */
	public void scrollToTop() {
		this.select(0, 0);
	}
	
	//Getters and Setters
	//---------------------------------------
	public int getHistorySize() {
		return this.historySize;
	}
	
	public String getHistoryEntryOpen() {
		return HISTORY_ENTRY_OPEN;
	}

	public String getHistoryEntryClose() {
		return HISTORY_ENTRY_CLOSE;
	}

	public String getUserActionCaret() {
		return USER_ACTION_CARET;
	}

	public ArrayList<String> getGameHistory() {
		return gameHistory;
	}
}
