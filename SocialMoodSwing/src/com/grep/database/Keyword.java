/**
 * Keyword.java
 * 
 * @author Gresham, Ryan, Everett, Pierce
 */

package com.grep.database;

/**
 * public class Keyword
 * <br><br>
 * Utility class containing keywords used in SQLite database
 */
public class Keyword {
	
	// Local private variables
	// Table key id
	/**
	 * private int id
	 */
	private int id;
	
	/**
	 * private String keyword
	 */
	private String keyword;
	
	// Foreign topic id for keyword
	/**
	 * private int t_id
	 * <br><br>
	 * Topic ID, foreign key reference
	 */
	private int t_id;
	
	/**
	 * Constructor
	 */
	public Keyword() {
		//empty
	}
	
	/**
	 * Constructor
	 * @param topic_id
	 * @param keyword
	 */
	public Keyword(int topic_id, String keyword) {
		this.t_id = topic_id;
		this.keyword = keyword;
	}
	
	/**
	 * Constructor
	 * @param id
	 * @param topic_id
	 * @param keyword
	 */
	public Keyword(int id, int topic_id, String keyword) {
		this.id = id;
		this.t_id = topic_id;		
		this.keyword = keyword;
	}
	
	// Get and set methods
	
	/**
	 * public int getId
	 * @return id
	 */
	public int getId() {
		return this.id;
	}
	
	/**
	 * public void setId
	 * @param id (int)
	 */
	public void setId(int id) {
		this.id = id;
	}
	
	/**
	 * public String getKeyword
	 * @return keyword
	 */
	public String getKeyword() {
		return this.keyword;
	}
	
	/**
	 * public void setKeyword
	 * @param keyword (String)
	 */
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	
	/**
	 * public int getKeywordTopicId
	 * @return t_id
	 */
	public int getKeywordTopicId() {
		return this.t_id;
	}
	
	/**
	 * public void setKeywordTopicId
	 * @param id (int)
	 */
	public void setKeywordTopicId(int id) {
		this.t_id = id;
	}
}
