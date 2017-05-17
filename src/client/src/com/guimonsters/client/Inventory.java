package com.guimonsters.client;

import java.util.ArrayList;

/**
 * Class for Inventories of GameObjects.
 * @author Stephen Butler
 * @version 1.00, 2013-04-18
 * ok I'm timed of Inventories so I'm gonna go look at networking. 04/11/13
 */
public class Inventory {
	
	//Fields of Data
	
//	private String name;
	private ArrayList<GameObject> itemList;
	
	protected int inventorySize = 20;

	/**
	 * Create an inventory item to be used by Characters, NPC's, container items, etc.
	 */
	
//	Default constructor will create an empty ArrayList of default (inventorySize) size.	
//	public Inventory(String name) {
	public Inventory() {
	
//		this.name = name;
		this.itemList = new ArrayList<GameObject>(inventorySize);
	}
	//Constructor
	public Inventory(int size){
		this.itemList = new ArrayList<GameObject>(size);
	}

//	public String getName() {
//		return name;
//	}
//
//	public void setName(String name) {
//		this.name = name;
//	}

	public ArrayList<GameObject> getItemList() {
		return itemList;
	}

	public void setItemList(ArrayList<GameObject> itemList) {
		this.itemList = itemList;
	}
	
	// Check of free available spaces in the inventory (stub)
	public int getFreeSpace(){
		return 1;
	}
	/**
	 * Add an item to the inventory.  Check if the inventory
	 * is full before adding.  If it is full, return false.
	 * @param item The GameObject to add to the inventory
	 * @return success Whether or not the add was successful.
	 */
	public boolean addItem(GameObject item) {
		if(this.itemList.size() < this.inventorySize) {
			this.itemList.add(item);
			return true;
		}
		else {
			return false;
		}
	}
	
	//Remove item to inventory (stub)
	public boolean removeItem (GameObject item) {
		
		return true;
	}
	
	//Transfer all items from one inventory to another. (stub)
	public boolean merge(Inventory otherInventory){
		return true;
	}
	
}
