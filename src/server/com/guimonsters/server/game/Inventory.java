package com.guimonsters.server.game;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Class for Inventories of GameObjects.
 * @author Stephen Butler
 * @author Kendall Lewis
 * @version 1.00, 2013-04-30
 */
public class Inventory implements Serializable {
	
	private static final long serialVersionUID = -1288408251632244399L;

	//Fields of Data (If you Construct it, they will return) . . . Kevin Costner? No?
	private ArrayList<GameObject> itemList;
	protected int inventorySize = 20;

	/**
	 * Create an inventory item to be used by Characters, NPC's, container items, etc.
	 */	
	public Inventory() {
		this.itemList = new ArrayList<GameObject>(inventorySize);
	}
	
	/**
	 * Add an item to the inventory.
	 * @param item The GameObject to add to the inventory
	 */
	public void addItem(GameObject item) {
		this.itemList.add(item);
	}
	
	/**
	 * Remove an item from the inventory.
	 * @param item The GameObject to remove from the inventory
	 */
	public void removeItem(GameObject item) {
		for(int i = 0; i < inventorySize; i++) {
			if(this.itemList.get(i) == item) {
				this.itemList.remove(i);
			}
		}
	}
	
	/**
	 * Checks to see how many free spaces are available in
	 * the inventory.
	 * @return free The number of free spaces in the inventory
	 */
	public int freeSpace() {
		int free = inventorySize - this.itemList.size();
		return free;
	}
	
	/**
	 * Deletes the contents of the inventory.
	 */
	public void deleteInventory() {
		for(int i = 0; i < this.itemList.size(); i++) {
			this.itemList.remove(this.itemList.get(i));
		}
	}
	
	/**
	 * Merges the items from two inventories, moving items from
	 * the second into the first.
	 * @param i1 The inventory all items will end up in
	 * @param i2 The inventory items are being taken from
	 * @return complete Whether or not the merge was completed
	 */
	public Boolean merge(Inventory i1, Inventory i2) {
		Boolean complete = false;
		
		ArrayList<GameObject> toInvent = new ArrayList<GameObject>();
		ArrayList<GameObject> fromInvent = new ArrayList<GameObject>();
		toInvent = i1.getItemList();
		fromInvent = i2.getItemList();
		
		int free = i1.freeSpace();
		int toMove = fromInvent.size();
		
		if(toMove <= free) {
			for(int i = 0; i < toMove; i++) {
				toInvent.add(fromInvent.get(i));
			}
			i1.setItemList(toInvent);
			complete = true;
		}
		return complete;
	}
	
	/**
	 * Return a comma separated value of names of GameObjects
	 * in this inventory.
	 * @return description The description string.
	 */
	public String describe() {
		String results = "";
		for(GameObject i : this.itemList) {
			//Only describe visible objects.
			if(i.getVisibilityLevel().compareTo(Visibility.INVISIBILE) > 0) {
				results += i.getName()+", ";
			}
		}
		
		//Strip trailing comma and whitespace.
		results = results.substring(0, results.length()-2);
		
		return results;
	}
	
	//Setter and Getter
	//------------------------------------------------------------
	public int size() {
		return this.itemList.size();
	}
	
	/**
	 * Returns an item from the inventory based on item name. 
	 * @param name The name of the item to search for.
	 * @return result The item that was found, or null if item is not found.
	 */
	public GameObject getItem(String name) {
		GameObject result;
		int indexOfItem = this.itemList.indexOf(name);
		if(indexOfItem != -1) {
			result = this.itemList.get(indexOfItem);
		}
		else {
			result = null;
		}
		return result;
	}
	
	public ArrayList<GameObject> getItemList() {
		return itemList;
	}

	public void setItemList(ArrayList<GameObject> itemList) {
		this.itemList = itemList;
	}	
}
