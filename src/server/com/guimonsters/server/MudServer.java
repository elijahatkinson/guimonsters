package com.guimonsters.server;

import java.io.*;

import com.guimonsters.server.content.SampleWorld;
import com.guimonsters.server.game.GameWorld;

/**
 * A basic multi-threaded server application.
 * Supports connections to multiple clients.
 * Each client connection is handled by a new thread.
 * 
 * @author Elijah Atkinson
 * @version 1.01, 2013-04-23
 *
 */
public class MudServer {
	
	//The port to attempt to host all connections on.
	//This will be set by a server config.ini file.
	private static final int PORT = 7777;
	
	//The main world file path
	private static final String GAME_WORLD_PATH = "./worlds";
	
	public static void main(String[] args) throws IOException {
		
		//Data fields
		BufferedReader in;
		String userLine;
		String command;
		String params;
		boolean worldLoaded = false;
		boolean runServer = true;
		GameWorld world;
		
		//Stores all description strings for server commands.
		//Add a new entry to this HashMap for each new server command.
		HelpMap helpMap = new HelpMap();
		helpMap.put("help", "Display a list of server commands.");
		helpMap.put("load", "Load an existing game world file. Proper usage is 'load [filename]'.");
		helpMap.put("create", "Create a new game world file. Proper usage is 'create [worldname]'.");
		helpMap.put("list", "List all game world files.");
		helpMap.put("exit", "Save the server state and shutdown.");
		
		//Startup messages.
		System.out.println("Starting GuiMonsters Server.");
		
		//Create a server thread to manage incoming connections from the clients.
		//Start up the server thread after a world has been loaded.
		ServerSocketThread serverThread = new ServerSocketThread(PORT);
		
		//List the contents of world files in the worlds directory
		File worldDir = new File(GAME_WORLD_PATH);
		
		//Create the worlds directory if it doesn't exist.
		if(!worldDir.exists()) {
			worldDir.mkdirs();
		}
		
		//List all of current world files.
		listWorldFiles(worldDir);
		
	
		//Listen for server commands
		try {
			
			//Create our main input reader
			in = new BufferedReader(new InputStreamReader(System.in));
			
			//Prompt user for input until 'exit' is entered.
			while(runServer) {
				//Display the server command prompt
				System.out.print(": ");
				
				//Block until user enters input.
				userLine = in.readLine().toLowerCase().trim();
				
				//Break user input up into command and parameters.
				if(userLine.contains(" ")) {
					//Everything before the first space is the command.
					command = userLine.substring(0, userLine.indexOf(' '));
					//Everything after the first space is the parameter string.
					params = userLine.substring(userLine.indexOf(' ')+1);
					//Trim leading and trailing spaces from param string.
					params = params.trim();
				}
				else {
					command = userLine;
					params = "";
				}
				
				//Execute user command.
				switch (command) {
					case "help":
						System.out.println(helpMap.print());
						break;
					case "load":
						if(!params.isEmpty()) {
							if(!worldLoaded) {
								System.out.println("Loading world file: "+params);
								world = new GameWorld();
								boolean success = world.load(params);
								if(success) {
									//Assign the loaded world object to the server.
									serverThread.setWorld(world);
									//Start the thread to listen on specified port and dispatch
									//new threads for each incoming client connection.
									serverThread.start();
									//Display server socket startup message.
									System.out.println("Server now listening on port: "+PORT+".");
									System.out.println("Type 'help' to see a list of server commands.");
									
									worldLoaded = true;
								}
								else {
									System.out.println("Failed to load '"+params+"'. Please try again, or use the 'create [world name]' command to make a new world.");
								}
							}
							else {
								System.out.println("A world is already loaded.");
							}
						}
						else {
							System.out.println("Please specify a filename to load.");
						}
						break;
						
					case "create":
						if(!params.isEmpty()) {
							
							String filename = params+".world"; 
							
							//If the file name is not taken, continue.
							if(!fileNameTaken(worldDir, filename)) {
								//Create the sample world
								System.out.println("Creating new world: "+params);
								SampleWorld sampleWorldFactory = new SampleWorld();
								GameWorld sample = sampleWorldFactory.create(params);
								
								if(sample.save()) {
									System.out.println("GameWorld file: "+sample.getFileName()+" created.");
									System.out.println("Please type 'load "+sample.getFileName()+"' to load the GameWorld.");
								}
								else {
									System.out.println("World creation failed.");
								}
							}
							else {
								System.out.println("A world with that name exists already.");
							}
							
						}
						else {
							System.out.println("Please specify the name of the world to create.");
						}
						break;
					case "list":
						//List all world files in the world directory.
						listWorldFiles(worldDir);
						break;
					case "exit":
						runServer = false;
						break;
					default:
						//If we're here, user input an invalid command.
						System.out.println("Command not recognized.");
						break;
				}
			}
			
			try {
				//Shutdown the server socket thread if the server is alive.
				if(serverThread.isAlive()) {
					serverThread.terminate();
					serverThread.join();
				}
			}
			catch (InterruptedException e) {
				System.out.println("Could not shut down the server socket thread.");
			}

			//Exit after main server loop terminates.
			System.out.println("Shutting down MudServer.");
			System.exit(0);
		}
		catch(IOException e) {
			System.out.println("Could not read standard input.");
			e.printStackTrace();
		}
	}
	
	/**
	 * Print a listing of GameWorld files to the console.
	 * @param worldDir The File directory to print files from.
	 */
	private static void listWorldFiles(File worldDir) {
		File[] worldFiles = worldDir.listFiles();
		
		//If we have world files, list them.
		if(worldFiles.length > 0) {
			
			System.out.println("Listing GameWorld files:");
			//Loop through all worldFiles
			for(File wFile : worldFiles) {
				System.out.println("    "+wFile.getName());
			}
			System.out.println("Please load a GameWorld file from the above list by typing 'load [filename]'.");
		}
		else {
			System.out.println("No GameWorld files found, you can create a new GameWorld with the 'create [world name]' command.");
		}
	}
	
	/**
	 * Check to see if a file name is taken already.
	 * Loop through the given directory and returns true if any filename matches the 
	 * supplied name.
	 * @param dir
	 * @param name
	 * @return
	 */
	private static boolean fileNameTaken(File dir, String name) {
		File[] files = dir.listFiles();
		
		if(files.length > 0) {
			for(File f : files) {
				if(f.getName().equalsIgnoreCase(name)) {
					return true;
				}
			}
		}
		
		return false;
	}
}
