package com.guimonsters.server.commands;

import java.lang.reflect.*;

/**
 * A class that represents a server command.
 * Commands contain a parent object, a name,
 * a description (used to populate the help menu),
 * and Method object that they invoke when executed.
 * 
 * @author Elijah Atkinson
 * @version 1.00, 2013-04-28
 */
public class Command extends Object {
	
	//Data fields
	private static final String ARG0 = "<<{{arg00}}>>";
	//private static final String ARG1 = "<<{{arg01}}>>";
	//private static final String ARG2 = "<<{{arg02}}>>";
	//private static final String ARG3 = "<<{{arg03}}>>";
	private static final String METHOD_NOT_FOUND_STRING = "Error attempting "+
			 "to look for command '"+ARG0+"' method parameters or return type. Command "+
			 "method may not be defined.";
	
	private Object parent;
	private String name;
	private String description;
	private String errorMessage;
	private Method commandMethod;
	
	//private Class<?> commandClass;
	private Class<?>[] commandParams;
	private Class<?> commandReturnType;
	
	/**
	 * Create a new Command instance.
	 * @param o The Object this command belongs to.
	 * @param n The String name of this command.
	 * @param d The String description of this command.
	 *     Used to populate help message.
	 * @param e The String error message to display when the
	 *     command is used wrong.
	 * @param m The Method object that this command executes.
	 */
	public Command(Object o, String n, String d, String e, Method m) {

		//Store basic Command data.
		this.parent = o;
		this.name = n;
		this.description = d;
		this.errorMessage = e;
		this.commandMethod = m;
			
		//Store information about this command's method.
		//this.commandClass = Object.class;
		//Attempt to get the commandMethod's parameter types and return type.
		//Handle errors if they occur.
		try {
			this.commandParams = commandMethod.getParameterTypes();
			this.commandReturnType = commandMethod.getReturnType();
		}
		catch (NullPointerException exception) {
			System.out.println(METHOD_NOT_FOUND_STRING.replace(ARG0, this.name));
			exception.printStackTrace();
		}
	}
	
	/**
	 * Attempt to execute the command and return a string.
	 * @param args The String parameter to pass to the command method.
	 *     Empty or null strings will be ignored and the command will
	 *     be executed without arguments.
	 * @return results The String containing results of command execution.
	 *     Results will be null if the command executes a method with void
	 *     return type.
	 */
	public String execute(String args) {
		String results = null;
		
		//Invoke the command with parameters, and record the results.
		results = this.invoke(args);

		return results;
	}
	
	/**
	 * Attempt to execute the command without parameters and return a string.
	 * @return results The String containing results of command execution.
	 *     Results will be null if the command executes a method with void
	 *     return type or if the command execution failed.
	 */
	public String execute() {
		String results = null;
		results = this.invokeNoParams();
		return results;
	}
	
	
	/**
	 * Attempt to execute the command with a String parameter.
	 * If the command executes a void method, results will be null.
	 * If invocation fails due to illegal arguments, attempts to invoke
	 * the command without parameters.
	 * @param args The String parameter to pass to the command method.
	 * @return results The String containing results of command execution.
	 *     Results is null if the command failed to execute or if the command
	 *     executes a void method.
	 */
	private String invoke(String args) {
		String results;
		try {
			results = (String)this.commandMethod.invoke(parent, args);
		}
		catch (IllegalArgumentException e) {
			
			//Try to execute the command with no arguments
			results = this.invokeNoParams();
		}
		catch (NullPointerException | IllegalAccessException |
			   InvocationTargetException e) {
			e.printStackTrace();
			results = null;
		}
		return results;
	}
	
	/**
	 * Attempt to execute the command with no parameters.
	 * If the command executes a void method, results will be null.
	 * @return results The String containing results of command execution.
	 *     Results is null if command failed to execute or if the command
	 *     executes a void method.
	 */
	private String invokeNoParams() {
		String results;
		try {
			results = (String)this.commandMethod.invoke(parent);
		}
		catch (IllegalArgumentException e) {
			results = this.errorMessage;
		}
		catch (NullPointerException | IllegalAccessException |
			   InvocationTargetException e) {
			results = null;
		}
		return results;
	}
	
	
	//Getters
	//----------------------------------------------
	public Object getParent() {
		return this.parent;
	}
	public String getName() {
		return this.name;
	}
	public String getDescription() {
		return this.description;
	}
	public String getErrorMessage() {
		return this.errorMessage;
	}
	public Class<?> getReturnType() {
		return this.commandReturnType;
	}
	public Class<?>[] getParamTypes() {
		return this.commandParams;
	}
	public Method getMethod() {
		return this.commandMethod;
	}
	//Setters
	//----------------------------------------------
	public void setParent(Object o) {
		this.parent = o;
	}
	public void setName(String n) {
		this.name = n;
	}
	public void setDescription(String d) {
		this.description = d;
	}
	public void setErrorMessage(String e) {
		this.errorMessage = e;
	}
	public void setMethod(Method m) {
		this.commandMethod = m;
	}
}
