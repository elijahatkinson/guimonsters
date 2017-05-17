package com.guimonsters.server.game;

import java.io.Serializable;

/**
 * Describes the visibility level of game objects and actors.
 * 
 * @author Elijah Atkinson
 * @version 2013-05-05
 */
public enum Visibility implements Serializable {
	INVISIBILE,
	VERY_WELL_HIDDEN,
	WELL_HIDDEN,
	HIDDEN,
	SLIGHTLY_HIDDEN,
	VISIBLE,
	OBVIOUS
}
