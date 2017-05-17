/**
 * 
 */
package com.guimonsters.client.test;

import static org.junit.Assert.*;

import java.awt.Color;
import java.awt.Font;

import org.junit.Before;
import org.junit.Test;

import com.guimonsters.client.GameLog;

/**
 * Tests the GameLog class.
 * (Needs to be finished later. :( )
 * @author Elijah Atkinson
 * @version 1.00, 2013-04-18
 */
public class GameLogTest {
	
	GameLog g1;
	
	/**
	 * This function runs before every test.
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		try {
			g1 = new GameLog();
		}
		catch (Exception e) {
			System.out.println("Setup failed!");
			e.printStackTrace();
		}
	}

	/**
	 * Test method for {@link com.guimonsters.client.GameLog#GameLog()}.
	 */
	@Test
	public void testGameLog() {
		int gameLogHistorySize = g1.getHistorySize();
		
		//Test initialization properties.
		assertEquals(gameLogHistorySize, g1.getRows());
		assertEquals(false, g1.isEditable());
		assertEquals(GameLog.LEFT_ALIGNMENT, g1.getAlignmentX(), 0);
		assertEquals(GameLog.CENTER_ALIGNMENT, g1.getAlignmentY(), 0);
		assertEquals(Color.BLACK, g1.getBackground());
		assertEquals(Color.GRAY, g1.getForeground());
		assertEquals(Font.decode("Courier"), g1.getFont());
		assertEquals(Color.WHITE, g1.getSelectedTextColor());
		assertEquals(Color.BLUE, g1.getSelectionColor());
		assertEquals(true, g1.getLineWrap());
		assertEquals(true, g1.getWrapStyleWord());
		
		//Check the 0 based size of the game history object.
		assertEquals(gameLogHistorySize-1, g1.getGameHistory().size());
		
		//Loop through each line in the game history and make sure they're all empty.
		for (String line : g1.getGameHistory()) {
			assertEquals("", line);
		}
		
	}

	/**
	 * Test method for {@link com.guimonsters.client.GameLog#draw()}.
	 */
	@Test
	public void testDraw() {
		
		g1.add("testing line 1");
		g1.add("testing line 2");
		g1.add("testingLine3");
		
		//Draw the GameHistory to the GameLog object.
		g1.draw();
		//Grab all of the lines from the GameLog object for comparison.
		String[] lines = g1.getText().split("\n");
		
		assertEquals(" [ testingLine3 ]", lines[98]);
		assertEquals(" [ testing line 2 ]", lines[97]);
		assertEquals(" [ testing line 1 ]", lines[96]);
	}
	
	//TODO Implement the rest of the testing for this class at a later date.

	/**
	 * Test method for {@link com.guimonsters.client.GameLog#add(java.lang.String)}.
	 */
	@Test
	public void testAddString() {
		//fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link com.guimonsters.client.GameLog#add(java.lang.String, java.lang.Boolean)}.
	 */
	@Test
	public void testAddStringBoolean() {
		//fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link com.guimonsters.client.GameLog#addUserInput(java.lang.String)}.
	 */
	@Test
	public void testAddUserInput() {
		//fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link com.guimonsters.client.GameLog#clear()}.
	 */
	@Test
	public void testClear() {
		//fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link com.guimonsters.client.GameLog#scrollToBottom()}.
	 */
	@Test
	public void testScrollToBottom() {
		//fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link com.guimonsters.client.GameLog#scrollToTop()}.
	 */
	@Test
	public void testScrollToTop() {
		//fail("Not yet implemented"); // TODO
	}

}
