package com.guimonsters.server;

import java.net.*;
import java.io.*;

public class ClientConnection {

	//Data fields
	private boolean connected;
	private Socket socket;
	private DataOutputStream outToClient;
	private BufferedReader inFromClient;
	
	
	/**
	 * Construct a ClientConnection with the supplied active socket.
	 * @param soc The Socket to use for the connection to the client.
	 */
	public ClientConnection(Socket soc) throws IOException{
		this.connected = true;
		this.socket = soc;
		
		//Create an input object to read signals from the client.
		this.inFromClient = new BufferedReader(
				new InputStreamReader(soc.getInputStream())
		);
		
		//Create an output object to send signals to the client.
		this.outToClient = new DataOutputStream(soc.getOutputStream());
	}
	
	
	/**
	 * Close the socket connection with the client.
	 * Sets this ClientConnection's connected status to false.
	 * @return success The boolean that indicates if the socket was closed.
	 */
	public boolean disconnect() {
		boolean success;
		
		try {
			outToClient.close();
			inFromClient.close();
			socket.close();
			
			success = true;
			connected = false;
		}
		catch (IOException e) {
			success = false;
			e.printStackTrace();
		}
		
		return success;
	}
	
	/**
	 * Read a line from the client as a string.
	 * @return results The String sent from the server.
	 */
	public String read() throws IOException {
		String results = inFromClient.readLine();
		return results;
	}
	
	/**
	 * Send a string to the client.
	 * @param command The String to send to the client.
	 */
	public void write(String command) throws IOException {
		outToClient.writeBytes(command+"\n");
	}
	
	/**
	 * Return the connected status of this ClientConnection instance.
	 * @return connected The boolean indicating connection status.
	 */
	public boolean isConnected() {
		return this.connected;
	}
}