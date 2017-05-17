package com.guimonsters.server;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * A custom hash map used to store help messages for each server command.
 * @author Elijah Atkinson
 * @version 1.00, 2013-04-29
 */
public class HelpMap extends HashMap<String, String> {

	private static final long serialVersionUID = 1L;
	
	/**
	 * Construct a string containing the server help message.
	 * Builds a string of all key/value pairs from this HelpMap.
	 * Each key/value pair will be on a new line.
	 *  
	 * @return results The string representation of all key/value
	 *                 pairs in this HelpMap.
	 */
	public String print() {
		String results = "Valid server commands are:";
		
		//Create an iterator to walk through the entire hash map.
		Iterator<Map.Entry<String, String>> entries = this.entrySet().iterator();
		
		//Loop through every key in the hash map and build up the
		//results string from the command descriptions.
		while(entries.hasNext()) {
			Map.Entry<String, String> entry = entries.next();
			//Build up the help string in the format \n command => command description.
			results += "\n    "+entry.getKey()+":\t "+entry.getValue();
		}
		
		return results;
	}
	
}
