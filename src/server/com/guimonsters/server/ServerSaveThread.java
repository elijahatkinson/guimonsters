package com.guimonsters.server;

import java.util.HashMap;

import com.guimonsters.server.game.GameWorld;

/**
 * Thread that saves the server game state every 30 seconds. (currently a stub class)
 * Serializes all playing account files account files and the world file.
 * @author Elijah Atkinson 2013-05-08
 *
 */
public class ServerSaveThread extends Thread {
	
	//Data fields
	private GameWorld world;
	private HashMap<Long, MudServerThread> clientThreads;
	
	public ServerSaveThread() {
		
	}
	
	public void run() {
		
	}

}
