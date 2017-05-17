package com.guimonsters.client.events;

import javax.swing.JTextField;

/**
 * Master listener class for the main game console.
 * Provides functionality to read from the console.
 * 
 * @author Elijah Atkinson
 * @version 1.00, 2013-04-18
 */
public class ConsoleListener {
	
	//Data fields
	private JTextField console;
	
	//Constructor
	public ConsoleListener(JTextField console) {
		this.console = console;
	}
	
	/**
	 * Get the user input and sanitize it:
	 * Trim leading and trailing space and
	 * set it to empty string if it is all spaces.
	 * Clear the input box after input is sanitized.
	 * @return input String The user input after sanitization.
	 */
	protected String getUserInput() {
		String input = console.getText();
		console.setText("");

		//Sanitize user input.
		//------------------------
		//Trim trailing and leading whitespace.
		input = input.trim();

		return input;
	}
	
	/**
	 * Validation function for user input strings.
	 * @param input The string to validate.
	 * @return valid Whether or not the string is valid.
	 */
	protected Boolean validate(String input) {
		Boolean valid = true;
		if (input.isEmpty()){
			valid = false;
		}
		return valid;
			
	}
}
