/**
 * Topic.java
 * 
 * @author Gresham, Ryan, Everett, Pierce
 */

package com.grep.database;

/**
 *	{@code public class Topic}
 *	<br><br>
 *  Utility class containing topics used in SQLite database
 */
public class Topic {
	
	// Local private variables
	// Table key id
	private int id;
	
	private String topic_name;
	
	/**
	 * Constructor
	 */
	public Topic() {
		//empty
	}
	
	/**
	 * Constructor
	 * @param name	(String)
	 */
	public Topic(String name) {
		this.topic_name = name;
	}
	
	/**
	 * Constructor
	 * @param id	(int)
	 * @param name	(String)
	 */
	public Topic(int id, String name) {
		this.id = id;
		this.topic_name = name;
	}
	
	// Get and set methods
	
	/**
	 * {@code public int getId}
	 * @return id
	 */
	public int getId() {
		return this.id;
	}
	
	/**
	 * {@code public void setId}
	 * @param id	(int)
	 */
	public void setId(int id) {
		this.id = id;
	}
	
	/**
	 * {@code public String getTopicName}
	 * @return topic_name
	 */
	public String getTopicName() {
		return this.topic_name;
	}
	
	/**
	 * {@code public void setTopicName}
	 * @param name	(String)
	 */
	public void setTopicName(String name) {
		this.topic_name = name;
	}
}