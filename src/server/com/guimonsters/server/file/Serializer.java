/**
 * 
 */
package com.guimonsters.server.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * @author Curran Hamilton
 * @author Modified by Elijah Atkinson
 * @version 2.00, 2013-05-05
 */
public class Serializer {
	//Data Fields
	File file;
	Object serialize_object;
	
	/**
	 * 
	 * @param file
	 * @param serial_object
	 */
	public Serializer(File file, Object serial_object) {
		this.file = file;
		this.serialize_object = serial_object;
	}
	
	/**
	 * 
	 * @param file
	 */
	public Serializer(File file) {
		this.file = file;
	}
	
	/**
	 * 
	 * @throws IOException 
	 */
	public boolean serialize() {
		boolean success = false;
		try {
			FileOutputStream fout = new FileOutputStream(this.file.getAbsolutePath());
			ObjectOutputStream oos = new ObjectOutputStream(fout);   
			oos.writeObject(this.serialize_object);
			oos.close();
			success = true;
		}
		catch(IOException e) {
			success = false;
			e.printStackTrace();
		}
		return success;
	}
	
	/**
	 * 
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public Object deserialize() throws IOException, ClassNotFoundException{
		FileInputStream fin = new FileInputStream(this.file.getAbsolutePath());
		ObjectInputStream ois = new ObjectInputStream(fin);
		this.serialize_object = (Object) ois.readObject();
		ois.close();
		return this.serialize_object;
	}

	//Getters
	
	/**
	 * @return the file
	 */
	public File getFile() {
		return file;
	}

	/**
	 * @return the serialize_object
	 */
	public Object getSerialize_object() {
		return serialize_object;
	}
	
	//Setters

	/**
	 * @param file the file to set
	 */
	public void setFile(File file) {
		this.file = file;
	}

	/**
	 * @param serialize_object the serialize_object to set
	 */
	public void setSerialize_object(Object serialize_object) {
		this.serialize_object = serialize_object;
	}
	
	
}
