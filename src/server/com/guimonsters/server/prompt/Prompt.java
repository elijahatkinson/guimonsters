package com.guimonsters.server.prompt;

/**
 * Define the basic attributes of the prompt type.
 */
public interface Prompt {
	
	boolean run();
	String promptUser(String message);
	void advance();
	boolean backUpPrompt(String input);
	boolean isPrompting();
	boolean exitPrompt(String input);
	void terminate();
	Object getResults();
}
