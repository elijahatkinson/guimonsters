package com.guimonsters.server.test;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.guimonsters.server.game.GameObject;
import com.guimonsters.server.game.Visibility;

/**
 * Tests the GameObject class.
 * (Needs to be finished later. :( )
 * @author Kendall Lewis
 * @author Modified by Elijah Atkinson
 * @version 2.00, 2013-05-05
 */
public class GameObjectTest {

	GameObject go1;
	GameObject go2;
	
	/**
	 * This function runs before every test.
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		try {
			go1 = new GameObject("go1", "This is GameObject 1.", Visibility.INVISIBILE);
			go2 = new GameObject("go2", "This is GameObject 2.");
		}
		catch (Exception e) {
			System.out.println("Setup failed!");
			e.printStackTrace();
		}
	}
	
	/**
	 * Test method for ....
	 */
	@Test
	public void testGameObject1() {
		//Test initialization properties
		assertEquals("go1", go1.getName());
		assertEquals("This is GameObject 1.", go1.getDescription());
		assertEquals(Visibility.INVISIBILE, go1.getVisibilityLevel());
		
		//Test setters
		go1.setName("Cow");
		assertEquals("Cow", go1.getName());
		
		go1.setDescription("Moo moo");
		assertEquals("Moo moo", go1.getDescription());
		
		go1.setVisibilityLevel(Visibility.SLIGHTLY_HIDDEN);
		assertEquals(Visibility.SLIGHTLY_HIDDEN, go1.getVisibilityLevel());
	}
	
	/**
	 * Test method for ....
	 */
	@Test
	public void testGameObject2() {
		//Test initialization properties
		assertEquals("go2", go2.getName());
		assertEquals("This is GameObject 2.", go2.getDescription());
		assertEquals(Visibility.VISIBLE, go2.getVisibilityLevel());
	}
}
