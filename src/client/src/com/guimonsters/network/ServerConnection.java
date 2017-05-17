package com.guimonsters.network;

import java.net.*;
import java.io.*;

import com.guimonsters.network.Packet;

public class ServerConnection {

	//Data fields
	private static final int DEFAULT_PORT = 7777;
	private boolean connected;
	private String ipAddress;
	private int port;
	private Socket socket;
	private ObjectOutputStream outToServer;
	private ObjectInputStream inFromServer;
	
	
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
			outToServer = new ObjectOutputStream(socket.getOutputStream());
			
			//Set up the object that lets us read responses from the server.
			inFromServer = new ObjectInputStream(socket.getInputStream());
						
			success = true;
			connected = true;
		}
		catch (IOException exception) {
			success = false;
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
		}
		
		return success;
	}
	
	/**
	 * Read the next packet from the server if one exists.
	 * @return serverPacket The Packet sent from the server.
	 * 						Returns null if no packet exists in stream.
	 */
	public Packet read() {
		Packet serverPacket;
		try {
			serverPacket = (Packet) inFromServer.readObject();
		} catch (IOException | ClassNotFoundException e) {
			serverPacket = null;
		}
		return serverPacket;
	}
	
	/**
	 * Send a packet object containing a string to the server.
	 * @param command The String to send to the server.
	 */
	public boolean write(String command) {
		boolean success;
		Packet packet = new Packet(command);
		
		try {
			outToServer.writeObject(packet);
			success = true;
		} catch (IOException e) {
			success = false;
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
	
	public void setPort(int port) {
		this.port = port;
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
