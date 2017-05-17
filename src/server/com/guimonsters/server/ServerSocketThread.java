package com.guimonsters.server;

import java.net.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.io.*;

import com.guimonsters.server.game.GameWorld;

/**
 * This thread listens for client connections on the main server socket
 * and creates a new MudServerThread for each new connection.
 * 
 * @author Elijah Atkinson
 * @version 1.02, 2013-04-29
 */
public class ServerSocketThread extends Thread {
	
	//Data fields
	private int port;
	private ServerSocket socket;
	private GameWorld world;
	private boolean listenForConnections;
	private HashMap<Long, MudServerThread> clientThreads;
	
	/**
	 * Construct a new ServerSocketThread instance and assign a port to listen on.
	 * @param port The integer matching the port number for the server to listen on.
	 * @param ar The AccountReader instance to use to keep track of user accounts.
	 */
	public ServerSocketThread(int port) {
		this.port = port;
		this.socket = null;
		this.listenForConnections = true;
		this.clientThreads = new HashMap<Long, MudServerThread>();
	}
	
	/**
	 * ServerSocketThread main method.
	 */
	public void run() {
		try {
			//Create the main server socket.
			socket = new ServerSocket(port);
		}
		catch (IOException e) {
			System.err.println("Could not listen on port: "+port+". Check that port is not in use.\n");
			System.exit(-1);
		}
		
		//Listen for connections while the server socket thread is running.
		//Spin up a new server thread for each MudClient that connects.
		while(this.listenForConnections) {
			Socket clientSocket;
			try {
				//Listen for incoming client connections. (Blocks until a connection comes in).
				clientSocket = socket.accept();
				
				//Create a new thread to manage the connection to the client.
				MudServerThread clientThread = new MudServerThread(this, clientSocket);
				
				//Game on!!
				clientThread.start();
			}
			catch (IOException e) {
				System.out.println("Shutting down server socket.");
				this.listenForConnections = false;
			}
		}
	}
	
	/**
	 * Loop through all server threads and terminate them, then
	 * shut down this thread.
	 */
	public void terminate() {
		//Check if we have client threads.
		if(this.clientThreads.size() > 0) {
		
			//Set up an iterator for the clientThreads hash map.			
			Iterator<Map.Entry<Long, MudServerThread>> it = this.clientThreads.entrySet().iterator();
			
			//Loop through each entry in clientThreads.
			while(it.hasNext()) {
				//Get the entry object from the hash map.
				Map.Entry<Long, MudServerThread> entry = it.next();
				
				//Grab the thread from the hash map entry so we can terminate it.
				MudServerThread thread = entry.getValue();
				
				//Remove the thread from the iterator to prevent 
				//ConcurrentModificationException.
				it.remove();
				
				//If the thread is alive, kill it.
				if(thread.isAlive()) {
					thread.terminate();
					try {
						thread.join();
					}
					catch (InterruptedException e) {
						System.err.println("Could not shut down thread "+thread.getId()+".");
					}
				}
			}
		}
		
		//Save the game world file before we shut down the server.
		System.out.println("Saving game world.");
		this.world.save();
		
		try {
			this.socket.close();
		} catch (IOException e) {
			System.err.println("Could not shut down the server socket thread.");
		}
		this.listenForConnections = false;
	}

	public HashMap<Long, MudServerThread> getClientThreads() {
		return this.clientThreads;
	}

	public GameWorld getWorld() {
		return world;
	}

	public void setWorld(GameWorld world) {
		this.world = world;
	}
}

