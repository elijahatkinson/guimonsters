package com.guimonsters.client.events;

import com.guimonsters.client.GameLog;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

/**
 * Listen for resize of the main application GUI window.
 * 
 * @author Elijah Atkinson
 * @version 1.00, 2013-04-18
 */
public class ClientResizeListener implements ComponentListener {
	
	//Listener Data fields.
	private GameLog log;
	
	//Listener Constructor
	public ClientResizeListener(GameLog log) {
		this.log = log;
	}
	
	/**
	 * Push the gameLog scroll bar to the bottom 
	 * when the client is resized.
	 */
	public void componentResized(ComponentEvent evt) {
		this.log.scrollToBottom();
	}
	
	public void componentHidden(ComponentEvent evt) {
		//Not used
	}

	//@Override
	public void componentMoved(ComponentEvent evt) {
		//Not used
		
	}

	//@Override
	public void componentShown(ComponentEvent evt) {
		//Not used
	}
}
