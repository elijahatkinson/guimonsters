package com.guimonsters.network;

import java.io.Serializable;
import com.guimonsters.network.Directive;

/**
 * A serializable object used to communicate between the client
 * and the server applications.
 * This class must be present in the same package in both the
 * client and server applications.
 * Each packet consists of a message string and a directive enum.
 * 
 * Message strings are displayed by the client and directives
 * are Enum flags used to indicate that a specific action needs to be
 * taken by the server or the client.
 * 
 * @author Elijah Atkinson
 * @version 2.00, 2013-05-07
 *
 */
public class Packet implements Serializable {
	
	//Data fields
	private static final long serialVersionUID = -2973870376791680987L;
	private Directive directive;
	private String message;
	
	public Packet(String message) {
		this.message = message;
		this.directive = Directive.MESSAGE;
	}
	
	public Packet(String message, Directive directive) {
		this.message = message;
		this.directive = directive;
	}
	
	public String getMessage() {
		return this.message;
	}
	
	public Directive getDirective() {
		return this.directive;
	}
}