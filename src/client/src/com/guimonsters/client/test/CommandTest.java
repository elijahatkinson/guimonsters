package com.guimonsters.client.test;

import static org.junit.Assert.*;
import java.lang.reflect.*;
import org.junit.Before;
import org.junit.Test;

import com.guimonsters.client.Command;

/**
 * Tests the Command class.
 * @author Elijah Atkinson
 * @version 1.00, 2013-04-18
 */
public class CommandTest {
	Object parent;
	Command c1;
	Command c2;
	Command c3;
	Command c4;
	Method m1;
	Method m2;
	Method m3;
	Method m4;
	int a;
	int b;
	int c;
	String alpha;
	String beta;
	String cappa;
	
	/**
	 * This function runs before every test.
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		try {
			
			//Get methods from this test class to attach to commands.
			m1 = this.getClass().getMethod("m1");
			m2 = this.getClass().getMethod("m2", String.class);
			m3 = this.getClass().getMethod("m3");
			m4 = this.getClass().getMethod("m4", String.class);
			
			//These values will be used to test runs of a void method without parameters.
			a = 3; b = 4; c = 0;
			//These values will be used to test runs of a void method with parameters.
			alpha = "alpha";
			beta = "beta";
			cappa = "";
		}
		catch (Exception e) {
			System.out.println("Setup failed!");
			e.printStackTrace();
		}
	}

	/**
	 * Test method for {@link com.guimonsters.client.Command#Command(java.lang.Object, java.lang.String, java.lang.String, java.lang.String, java.lang.reflect.Method)}.
	 */
	@Test
	public void testCommand() {
		//Create a command object.
		String descr = "This is command 1.";
		String error = "Command 1 error message";
		c1 = new Command(parent, "c1", descr, error, m2);
		
		//Test that command data members were initialized properly.
		assertEquals(parent, c1.getParent());
		assertEquals("c1", c1.getName());
		assertEquals(descr, c1.getDescription());
		assertEquals(error, c1.getErrorMessage());
		assertEquals(m2, c1.getMethod());
		assertEquals(String.class, c1.getParamTypes()[0]);
		assertEquals(void.class, c1.getReturnType());
		
		//Test setters
		Object newParent = new Object();
		c1.setParent(newParent);
		assertEquals(newParent, c1.getParent());
		
		c1.setName("Teddy tumblebum");
		assertEquals("Teddy tumblebum", c1.getName());
		
		c1.setDescription("This command has a long nose.");
		assertEquals("This command has a long nose.", c1.getDescription());
		
		c1.setErrorMessage("New error message has been set");
		assertEquals("New error message has been set", c1.getErrorMessage());
		
		c1.setMethod(m3);
		assertEquals(m3, c1.getMethod());
	}

	/**
	 * Test method for {@link com.guimonsters.client.Command#execute(java.lang.String)}.
	 */
	@Test
	public void testExecute1() {
		//Create commands for each test method.
		c2 = new Command(this, "c2", "This is command 2.", "Error 2", m2);
		c4 = new Command(this, "c4", "This is command 4.", "Error 4", m4);
		
		//Store string results of command execution.
		String result = "";
		
		//Test conditions of method m2 before execution.
		assertEquals("", cappa);
		//Executes method m2 with string parameter and no return type.
		c2.execute("gamma");
		assertEquals("alphabetagamma", cappa);
		
		//Execute method m4 with string parameter and string return type.
		result = c4.execute("I love testing");
		assertEquals("m4 success I love testing", result);
	}
	/**
	 * Test method for {@link com.guimonsters.client.Command#execute()}.
	 */
	@Test
	public void testExecute2() {
		//Create commands for each test method.
		c1 = new Command(this, "c1", "This is command 1.", "Error 1", m1);
		c3 = new Command(this, "c3", "This is command 3.", "Error 3", m3);
		
		//Store string results of command execution.
		String result = "";
		
		//Test conditions of method m1 before execution.
		assertEquals(0, c);
		//Executes method m1 with no parameters and no return type.
		c1.execute();
		//Test results of m1's execution.
		assertEquals(7, c);
		
		//Execute method m3 with no parameters but string return type.
		result = c3.execute();
		assertEquals("m3 success", result);
	}
	
	/**
	 * Used to test execution of commands linked to void functions with no parameters.
	 */
	public void m1() {
		c = a + b;
	}
	
	/**
	 * Used to test execution of commands linked to void functions with parameters.
	 * @param arg A test string to append to a static test string.
	 */
	public void m2(String arg) {
		cappa = alpha+beta+arg;
	}
	
	/**
	 * Used to test execution of commands with no parameters that return a string.
	 * @return results A static test string.
	 */
	public String m3() {
		return "m3 success";
	}
	
	/**
	 * Used to test execution of commands with parameters that return a string.
	 * @param arg A string argument to append to the static test string.
	 * @return results A static test string with method input appended.
	 */
	public String m4(String arg) {
		return "m4 success "+arg;
	}
}
