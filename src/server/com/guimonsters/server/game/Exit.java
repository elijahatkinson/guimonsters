package com.guimonsters.server.game;

import java.io.Serializable;

/**
 * Exit objects link two GameWorld rooms together.
 * Actors can use the go command on an exit object to
 * move from an exits start to its destination.
 * Exits are one way! If you want to create a two way
 * link between rooms, you need to make an exit in both rooms.
 * 
 * @author Elijah Atkinson
 * @version 2.00, 2013-05-07
 */
public class Exit extends GameObject implements Serializable {

	//Data fields
	private static final long serialVersionUID = -6672896583523995917L;
	private Room destination;

	public Exit(String name, String description, Room destination, Visibility vLevel) {
		super(name, description, vLevel);
		this.destination = destination;
	}
	
	public Exit(String name, String description, Room destination) {
		super(name, description, Visibility.VISIBLE);
		this.destination = destination;
	}

	public Room getDestination() {
		return destination;
	}

	public void setDestination(Room destination) {
		this.destination = destination;
	}
}
