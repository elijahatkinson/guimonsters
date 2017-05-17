package com.guimonsters.client;

import javax.swing.text.*;
import java.awt.*;

/**
 * Defines all of the text styles used by the client.
 * Currently a stub class until we decide on how to
 * manage text colors.
 * 
 * @author Elijah Atkinson
 * @version 0.04, 2013-04-05
 */
public class ClientStyles {
	
	//Data Fields
	private StyleContext sc;
	private Style defaultStyle;
	public Style mainStyle;
	public Style commandStyle;
	
	public static Style BACKGROUND_STYLE;
	public static Style SYSTEM_STYLE;
	public static Style SELECTED_STYLE;
	public static Style SELECTED_BACKGROUND_STYLE;
	
	/**
	 * Construct a ClientStyles instance object.
	 */
	public ClientStyles() {
		sc = new StyleContext();
		
		//Get the default style to use as an initial parent for all other styles.
		this.defaultStyle = sc.getStyle(StyleContext.DEFAULT_STYLE);
		StyleConstants.setFontFamily(this.defaultStyle, "serif");
		StyleConstants.setFontSize(this.defaultStyle, 12);
		
		this.mainStyle = sc.addStyle("MainStyle", defaultStyle);
		StyleConstants.setBackground(mainStyle, Color.BLACK);
		StyleConstants.setForeground(mainStyle, Color.GRAY);
		
	}
}
