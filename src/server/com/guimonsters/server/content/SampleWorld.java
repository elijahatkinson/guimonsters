package com.guimonsters.server.content;


import com.guimonsters.server.game.*;

/**
 * Generate the Sample GameWorld if a world file isn't loaded.
 * @author Stephen Butler
 * @author Curran Hamilton
 * @author Kendall Lewis
 * @author Elijah Atkinson
 * @version 2.00, 2013-05-08
 */
public class SampleWorld {
	
	
	//Data Fields
	Room abyss, hallway, entryRoom, garden;

	
	/**
	 * Creates a default instance of a GameWorld object.
	 * @return GameWorld A new instance of a gameworld.
	 */
	public GameWorld create(String name){
		GameWorld sampleWorld = new GameWorld(name);
		
		//----Garden----(Exits: hallway, abyss)
		garden = new Room("Garden", "A lush and fragrant garden....");

		GameObject appleTree = new GameObject("Apple Tree", "A tree covered with the ripest, most delicious " +
		           "apples in the land.");
		GameObject fountain = new GameObject("Fountain", "Drink from it and you may find that you never grow old...maybe.");
		GameObject roseBush = new GameObject("Rose Bush", "Overflowing with the most beautiful blossoms you have ever beheld." +
		           " But there appears to be a strange flickering light coming from within.");

		Actor snake = new Actor("Snake", "Smooth and slithery...and not the best influence.", "Guardian of the Apple Tree",
		                        "Serpent", "Unknown", 10);
		snake.setDefaultResponse("Wouldn't you like to eat one of these delicious apples?");

		Actor nicodemus = new Actor("Nicodemus", "Old and wise rat who lives beneath the Rose Bush.", "Wizard", "Rat", "Male", 8);
		nicodemus.setDefaultResponse("Courage of the heart is very rare.");
		nicodemus.addTalkOption("Rose Bush", "It is where I and those others who escaped NIHM have come to live.");

		Actor whiteRabbit = new Actor("WhiteRabbit", "Jittery, twitchy litte bunny who's always in a hurry.", "Footman", "Rabbit", "Male", 5);
		whiteRabbit.setDefaultResponse("I'm late! I'm late! For a very important date!");
		whiteRabbit.addTalkOption("Rabbit Hole", "Be careful! You don't know what lies at the bottom of the rabbit hole...");

		garden.addRoomItem(appleTree);
		garden.addRoomItem(fountain);
		garden.addRoomItem(roseBush);
		garden.addActor(snake);
		garden.addActor(nicodemus);
		garden.addActor(whiteRabbit);

		
		//----Hallway----(Exits: garden, entry room)
		String hallwayName = "Hallway";
		String hallwayDesc = "A barren hallway that looks as though it is well traveled..." +
							" though for now there are no exits!";	
		hallway = new Room(hallwayName, hallwayDesc);
		
		
		//----entryRoom----(Exits: 	hallway)
		String entryName = "Siemens Hall 120";
		String entryDescription = "A class room in shambles, that smells of burning projector";
		entryRoom = new Room(entryName, entryDescription);
		GameObject desks = new GameObject("Set of Desks", "A collection of desks arranged in a crazy manner, as if some ritual was preformed here.");

		//GameObjects
		GameObject projector = new GameObject("Projector", "A burning projector that gives off a dangerous odor, while still projecting the screen of the nearby computer.");
		GameObject whiteboard = new GameObject("Whiteboard", "A whiteboard that has 'Save This' as its only text written in the top right corner.");
		GameObject overheadProjector = new GameObject("Overhead Projector", "A shabby overhead projector that sits dejectedly in the corner. I has a note on it that says, 'Return to Chip Dixon'.");
		GameObject backpack = new GameObject("Backpack", "This appears to be an abandoned backpack, with nothing in it");
		GameObject computer = new GameObject("Computer", "A computer that is connected to the burning projector");
		entryRoom.addRoomItem(desks);
		entryRoom.addRoomItem(projector);
		entryRoom.addRoomItem(whiteboard);
		entryRoom.addRoomItem(overheadProjector);
		entryRoom.addRoomItem(backpack);
		entryRoom.addRoomItem(computer);

		//Actors
		String sharonDesc = "Sharon is a Computer Science Professor, who wears a red hat, and carries with her a dozen jumbled bags of varying size";
		Actor sharon = new Actor("Sharon", sharonDesc, "Professor", "Human", "Female", 9001);
		String sharonResponse = "Welcome to CS 435! Lets learn about software! Or get some Advice!";
		String sharonAdvice = "You should go explore out the door, and learn what exists in the world through experience and learning, and you should share your knowledge with those around you!";
			sharon.setDefaultResponse(sharonResponse);
			sharon.addTalkOption("Advice", sharonAdvice);
			entryRoom.addActor(sharon);
		
		String teacherDesc = "A teacher who is packing up and taking her time leaving the class room, even though her class is over and her students have left.";
		Actor teacher = new Actor("Slow Teacher", teacherDesc, "Teacher", "Human", "Female", 1);
		String teacherResponse = "I'm leaving, i'm leaving!";
		teacher.setDefaultResponse(teacherResponse);
		
		//TODO add another person to climb over

		//----The Abyss----(exits: entryRoom, abyss)
		abyss = new Room("The Abyss", "You seem to be floating in a dark cloudy region of the Void.");
		
		//GameObjects
		GameObject bird = new GameObject("Bird", "A sad looking bird.");
		GameObject mist = new GameObject("spooky Mist", "Yup, there's lots of mist here.");
		
		abyss.addRoomItem(bird);	
		abyss.addRoomItem(mist);
			
		//Actors
		Actor spirit = new Actor("Spirit", "A spooky but uninteresting poltergeist.",
								"Guide of the Damned", "Ghost", "Unknown", 1);
		
			spirit.setDefaultResponse("You will never escape the Abyss! There is no way out! Ignore that Door over there.");
		
			spirit.addTalkOption("door", "Door? I don't know what you're talking about.");
			
		abyss.addActor(spirit);


		//--------------------------------------------------------------
		//Add all game exits after game rooms have been created.
		//--------------------------------------------------------------
		//Garden exits
		Exit gate = new Exit("Gate", "Though intricately inlaid with beautiful precious metals, it towers over you in a " +
		           "daunting manner.", hallway);
		garden.addExit(gate);
		Exit rabbitHole = new Exit("Rabbit Hole", "Down and down and down you fall...", abyss);
		garden.addExit(rabbitHole);
		
		//Abyss exits
		Exit abyssDoor = new Exit("A door", "A door whirls in the void. You hear noise on the other side.", entryRoom);
		Exit vortex = new Exit ("A vortex", "Peering through the dark vortex, you think you can see a person . . .", abyss);
		abyss.addExit(abyssDoor);
		abyss.addExit(vortex);
		
		//Hallway exits
		Exit hallwayDoor = new Exit("Door", "The hallway side of the door to the classroom. You see 'SH 120' written above the door.", entryRoom);
		hallway.addExit(hallwayDoor);
		
		//EntryRoom exits
		Exit frontDoor = new Exit("Door", "The front door to the classroom, and is unremarkable in every way, except the door closes when the classroom loses power.", hallway);
		entryRoom.addExit(frontDoor);
		Exit window = new Exit("Window", "A window that leads out into a beautiful garden... maybe.", garden);
		entryRoom.addExit(window);
		
		
	
		//Add all rooms to the world.
		sampleWorld.addRoom(abyss);
		sampleWorld.addRoom(entryRoom);
		sampleWorld.addRoom(garden);
		sampleWorld.addRoom(hallway);
		
		sampleWorld.setStartingRoom(abyss);
		
		return sampleWorld;
	}
}
