package com.guimonsters.network;

import java.net.*;
import java.io.*;

import com.guimonsters.network.Packet;

public class ClientConnection {

	//Data fields
	private boolean connected;
	private Socket socket;
	private ObjectOutputStream outToClient;
	private ObjectInputStream inFromClient;
	
	
	/**
	 * Construct a ClientConnection with the supplied active socket.
	 * @param soc The Socket to use for the connection to the client.
	 */
	public ClientConnection(Socket soc) throws IOException {
		this.connected = true;
		this.socket = soc;
		
		//Create an input object to read signals from the client.
		this.inFromClient = new ObjectInputStream(soc.getInputStream());
		
		//Create an output object to send signals to the client.
		this.outToClient = new ObjectOutputStream(soc.getOutputStream());
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
	 * Read a packet from the client.
	 * @return clientPacket The Packet sent from the client.
	 */
	public Packet read() throws IOException, ClassNotFoundException {
		Packet clientPacket = (Packet)inFromClient.readObject();
		return clientPacket;
	}
	
	/**
	 * Send a packet object containting a message to the client.
	 * @param command The command to send to the client inside a packet.
	 */
	public void write(String command) throws IOException {
		Packet packet = new Packet(command);
		outToClient.writeObject(packet);
	}
	
	/**
	 * Send a packet object to the client.
	 * @param p The Package object to write to the client.
	 */
	public void write(Packet p) throws IOException {
		outToClient.writeObject(p);
	}
	
	
	/**
	 * Return the connected status of this ClientConnection instance.
	 * @return connected The boolean indicating connection status.
	 */
	public boolean isConnected() {
		return this.connected;
	}
}