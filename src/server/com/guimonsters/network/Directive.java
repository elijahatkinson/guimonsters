package com.guimonsters.network;

/**
 * Enum to be passed as part of a packet in a server
 * or client socket message.  These directives
 * correspond to an action by either the client
 * or the server.
 * 
 * DISCONNECT     - Indicates the client should disconnect
 * 			        its end of the connection to the server.
 * 
 * CLEAR_LOG      - Indicates the client should clear its game log
 *                  and then display the message string from the packet.
 * 
 * MESSAGE        - Indicates the client should display the message
 *                  string from the packet. This is the default directive
 *                  passed by packets when no other directive is specified. 
 * 
 * PROMPT         - Indicates that this packet is part of a multi-part prompt
 *                  that requests information from the client.
 *              
 * SERV_CREATE_ACCOUNT - Indicates that the server successfully responded to a client
 *                  account creation request.
 *                  
 * CHAT            - Indicates that the string received by the client should
 *                   be displayed with the chat format.
 *                   
 * LOGIN_FALSE     - Sent by the server when the user failed to log in.
 * LOGIN_TRUE      - Sent by the server when the user logged in successfully.
 * 
 * @author Elijah Atkinson
 * @version 2.00, 2013-05-06
 *
 */
public enum Directive {
	DISCONNECT,
	MESSAGE,
	CLEAR_LOG,
	PROMPT,
	SERV_CREATE_ACCOUNT,
	CHAT,
	LOGIN_FALSE,
	LOGIN_TRUE
}