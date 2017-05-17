package com.guimonsters.client;

import java.net.*;
import java.io.*;

public class ServerConnection {

	//Data fields
	private static final int DEFAULT_PORT = 7777;
	private boolean connected;
	private String ipAddress;
	private int port;
	private Socket socket;
	private DataOutputStream outToServer;
	private BufferedReader inFromServer;
	
	
	/**
	 * Construct a ServerConnection with the default port of 7777.
	 * @param ipAddress The String containing the IP Address to connect to.
	 */
	public ServerConnection(String ipAddress) {
		this.connected = false;
		this.ipAddress = ipAddress;
		this.port = DEFAULT_PORT;
		this.socket = null;
		this.outToServer = null;
		this.inFromServer = null;
	}
	
	/**
	 * Construct a ServerConnection with custom IP Address and port.
	 * @param ipAddress The String containing the IP Address to connect to.
	 * @param port An integer representing the port to connect on.
	 */
	public ServerConnection(String ipAddress, int port) {
		this.connected = false;
		this.ipAddress = ipAddress;
		this.port = port;
		this.socket = null;
		this.outToServer = null;
		this.inFromServer = null;
	}
	
	/**
	 * Attempt to connect to the server. Sets this ServerConnection's connected
	 * status to true if successful and opens a connection with the server.
	 * @return success The boolean that indicates if the connection succeeded.
	 */
	public boolean connect() {
		boolean success = false;
		//Attempt to connect to the server.
		try {
			socket = new Socket(ipAddress, port);

			//Set up the object that lets us send commands to the server.
			outToServer = new DataOutputStream(socket.getOutputStream());
			
			//Set up the object that lets us read responses from the server.
			inFromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
						
			success = true;
			connected = true;
		}
		catch (IOException exception) {
			success = false;
			exception.printStackTrace();
		}
		
		return success;
	}
	
	/**
	 * Close the socket connection with the server.
	 * Sets this ServerConnection's connected status to false.
	 * @return success The boolean that indicates if the socket was closed.
	 */
	public boolean close() {
		boolean success = false;
		
		try {
			outToServer.close();
			inFromServer.close();
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
	 * Read the next line from the server as a String.
	 * @return results The String sent from the server.
	 */
	public String read() {
		String results = null;
		try {
			results = inFromServer.readLine();
		}
		catch (IOException e) {
			results = null;
			e.printStackTrace();
		}
		return results;
	}
	
	/**
	 * Send a string to the server.
	 * @param command The String to send to the server.
	 * @return success The success flag for the write. True if the write succeeded. False otherwise.
	 */
	public boolean write(String command) {
		boolean success = false;
		
		try {
			outToServer.writeBytes(command+"\n");
			success = true;
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		
		return success;
	}
	
	/**
	 * Return weather or not this ServerConnection instance
	 * is currently connected to a server.
	 * @return connected The boolean indicating connection status.
	 */
	public boolean isConnected() {
		return this.connected;
	}
	
	//Getters and setters
	//------------------------------
	public int getPort() {
		return this.port;
	}
	
	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}
	
	public Socket getSocket() {
		return this.socket;
	}
}
