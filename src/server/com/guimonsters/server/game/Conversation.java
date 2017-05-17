package com.guimonsters.server.game;

import java.io.Serializable;
import java.util.*;

/**
 * Conversation class to contain conversation texts for Actors.
 * @author Stephen Butler
 * @version 2.2 (Since I actually deleted the first version) 05/07/13
 *
 */
public class Conversation implements Serializable 
{
	
	
	//Data Fields
	private static final long serialVersionUID = -4498572260859138791L;
	//Map each String of text to a conversation keyword.
	protected HashMap<String, String> convoMap;

	
	//Create an instance of a conversation map, 0 arguments for default/testing
	
	public Conversation()
	{
		convoMap = new HashMap<String, String>();
		
		convoMap.put("default","Hello! Would you like some Advice?" +
				"Or perhaps you'd like to hear a Joke?");
		convoMap.put("advice","Don't feed the Beasties!");
		convoMap.put("joke","What did Steve Jobs order at the Drive-Through?" +
				"A BigMac!");
		convoMap.put("beasties","If you meet Beastie, " +
				"you do like we do: you run.");
	}
	
	//Add Conversation line to Map, ensuring case.
	public void convoAdd(String convoKey,String convoText)
	{
		try
		{
			convoMap.put(convoKey.toLowerCase(), convoText);
			
			
		}
		catch (Exception e)
		{
			System.out.println(e.toString());
		}
	}
	public void setDefault(String defaultReply){
		convoMap.put("default", defaultReply);
	}
	
	/**
	 * Responds to a given conversation keyword. If the
	 * keyword is unknown, responds with default text.
	 * @param key The keyword to look for a response to.
	 * @return convoText The conversation String from the Actor.
	 */
	public String getResponse(String key)
	{
		String text = convoMap.get(key);
		if(text == null) {
			text = convoMap.get("default");
		}
		return text;
	}
	
	//Returns true if conversation key exists in HashMap
	public boolean knowsTopic(String key)
	{
		return convoMap.containsKey(key);
	}
	
	

}