/**
 * 	DatabaseHandler.java
 * 
 *  @author Gresham, Ryan, Everett, Pierce
 */
package com.grep.database;

import java.util.ArrayList;
import java.util.List;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * {@code public class DatabaseHandler extends SQLiteOpenHelper}
 * <br><br>
 * Handles all SQLite database interaction and functionality.
 * <br><br><blockquote>
 * Instantiating database handler within activity: <br><blockquote>
 * {@code DatabaseHandler dh = new DatabaseHandler(this)}
 */
public class DatabaseHandler extends SQLiteOpenHelper {
	
	//Static variables
	//Database version
	private static final int DATABASE_VERSION = 2;
	
	// Database name
	private static final String DATABASE_NAME = "SMS_DB";
	
	// Table names
	private static final String AUTH_TABLE = "credentials";
	private static final String TOPIC_TABLE = "topics";
	private static final String KEYWORD_TABLE = "keywords";
	private static final String SESSION_TABLE = "sessions";
	
	// Authentication Table column names
	private static final String USER_KEY_ID = "id";
	private static final String USER_KEY = "user_key";
	private static final String USER_SECRET = "user_secret";
	
	// Topic Table column names
	private static final String TOPIC_KEY_ID = "id";
	private static final String TOPIC_NAME = "topic";
	
	// Keyword Table column names
	private static final String KEYWORD_KEY_ID = "id";	
	private static final String KEYWORD_TEXT = "keyword";	
	private static final String KEYWORD_TOPIC_ID = "topic_id";
	
	// Session Table column names
	private static final String	SESSION_KEY_ID = "id";
	private static final String SESSION_TOPIC_ID = "topic_id";
	private static final String	SESSION_START_TIME = "start_time";
	private static final String	SESSION_DURATION = "duration";
	private static final String	SESSION_TWEETS_PROCESSED = "tweets_processed";
	private static final String	SESSION_AVG_POSITIVE = "pos_sentament";
	private static final String SESSION_AVG_NEGATIVE = "neg_sentament";
	
	// Database object
	SQLiteDatabase db;
	
	/**
	 * Constructor
	 * @param context	(Context) - the application context that contains the database
	 */
	public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
	
	/**
	 * */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create Authentication Table in SQLite DB
    	String CREATE_AUTH_TABLE = "CREATE TABLE " + AUTH_TABLE + " ("
                + USER_KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + USER_KEY + " TEXT, "
                + USER_SECRET + " TEXT" + ")";
        db.execSQL(CREATE_AUTH_TABLE);
        
        // Create Topic Table in SQLite DB
        String CREATE_TOPIC_TABLE = "CREATE TABLE " + TOPIC_TABLE + " ("
        		+ TOPIC_KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + TOPIC_NAME + " TEXT "  + ")";
        db.execSQL(CREATE_TOPIC_TABLE);
        
        // Create Keyword Table in SQLite DB
        String CREATE_KEYWORD_TABLE = "CREATE TABLE " + KEYWORD_TABLE + " ("
        		+ KEYWORD_KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + KEYWORD_TEXT + " TEXT,"
        		+ KEYWORD_TOPIC_ID + " INTEGER, FOREIGN KEY (" + KEYWORD_TOPIC_ID + ") REFERENCES "
        		+ TOPIC_TABLE + " (" + TOPIC_KEY_ID + ") ON DELETE CASCADE)";
        db.execSQL(CREATE_KEYWORD_TABLE);
        
        // Create Session Table in SQLite DB
        String CREATE_SESSION_TABLE = "CREATE TABLE " + SESSION_TABLE + " ("
        		+ SESSION_KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + SESSION_TOPIC_ID 
        		+ " INTEGER, " + SESSION_START_TIME + " TEXT, " + SESSION_DURATION 
        		+ " TEXT, " + SESSION_TWEETS_PROCESSED + " INTEGER, " + SESSION_AVG_POSITIVE
        		+ " INTEGER, " + SESSION_AVG_NEGATIVE + " INTEGER, FOREIGN KEY (" + SESSION_TOPIC_ID + ") REFERENCES " + TOPIC_TABLE 
        		+ "(" + TOPIC_KEY_ID + ") ON DELETE CASCADE)";
        db.execSQL(CREATE_SESSION_TABLE);
        
    }
 
    /**
     * Enables foreign keys for writable databases when opened
     */
    @Override
    public void onOpen(SQLiteDatabase db) {
    	if (!db.isReadOnly()) {
    	    // Enable foreign key constraints
    	    db.execSQL("PRAGMA foreign_keys=ON;");
    	  }
    	super.onOpen(db);
    }
    
    /**
     * */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        
    	// Drop older tables if existed
        db.execSQL("DROP TABLE IF EXISTS " + AUTH_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + TOPIC_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + KEYWORD_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + SESSION_TABLE);
	
        // Create tables again
        onCreate(db);
    }
    
    /**
     * {@code public void open}
     * <br><br>
     * Open SQLite database as writable
     */
    public void open() {
    	this.db = this.getWritableDatabase();
    }
    
    /**
     * {@code public void close}
     * <br><br>
     * Close SQLite database
     */
    public void close() {
    	this.db.close();
    }
    
    /**
     * {@code public void clean}
     * <br><br>
     * Clean the database, clear out all table's rows
     */
    public void clean() {
    	List<Topic> tList = this.getAllTopics();
    	
    	if(tList.size() > 0) {
    		for (Topic t: tList) {
    			this.deleteTopic(t.getId());
    		}
    	}
    }
    
    /**
     * {@code public void delete}
     * <br><br>
     * Delete the database, context is the activity calling the method.
     * @param context	(Context)
     */
    public void delete(Context context) {
    	context.deleteDatabase(DATABASE_NAME);
    }

    
    /*
     *  CRUD operations ( Create, Read, Update, Delete )
     *  Operations are listed in order for each table.
     */
    
// Authentication table CRUD
    /**
     * {@code public void addCredentials}
     * <br><br>
     * Insert credentials into authentication table
     * @param credentials	(Credentials)
     * @return Row id of inserted credential, -1 if error occurred
     */
    public int addCredentials(Credentials credentials) {   	
    	ContentValues values = new ContentValues();
    	values.put(USER_KEY, credentials.getUserKey());
    	values.put(USER_SECRET, credentials.getUserSecret());
    	
    	return (int)this.db.insert(AUTH_TABLE, null, values);
    }
    
    /**
     * {@code public Credentials getCredentials}
     * <br><br>
     * Retrieve first credential from authentication table
     * 
     * @return credentials
     */
    public Credentials getCredentials() {
    	Credentials credentials = null;
    	
    	// SQLite command for select first row
    	String selectQuery = "SELECT * FROM " + AUTH_TABLE + " LIMIT 1";
    	Cursor cursor = this.db.rawQuery(selectQuery, null);
    	/*
    	Cursor cursor = this.db.query(AUTH_TABLE, new String[] { USER_KEY_ID,
                USER_KEY, USER_SECRET }, USER_KEY_ID + " =?",
                new String[] { String.valueOf(id) }, null, null, null, null);
    	*/
    	if (cursor.getCount() > 0) {
            cursor.moveToFirst();
    	
            credentials = new Credentials(Integer.parseInt(cursor.getString(0)),
            		cursor.getString(1), cursor.getString(2));
    	
            cursor.close();
    	}
    	
    	return credentials;
    }
    
    /**
     * {@code public boolean update}
     * <br><br>
     * Update existing credentials in credentials table
     * @param credentials
     * @return updateSuccess
     */
    public boolean updateCredentials(Credentials credentials) {  	
    	ContentValues values = new ContentValues();
    	values.put(USER_KEY, credentials.getUserKey());
    	values.put(USER_SECRET, credentials.getUserSecret());
    	
    	// update credentials row
    	boolean updateSuccess = this.db.update(AUTH_TABLE, values, USER_KEY_ID + " = ?",
                new String[] { String.valueOf(credentials.getId()) }) > 0;
        
    	return updateSuccess;
    }
    
    /**
     * {@code public void deleteCredentials}
     * <br><br>
     * Delete credentials from authentication table by id
     * @param credentials	(int)
     */
    public void deleteCredentials(int credentials_id) {       
    	this.db.delete(AUTH_TABLE, USER_KEY_ID + " =?",
                new String[] { String.valueOf(credentials_id) });
    }
    
// Topic table CRUD
    /**
     * {@code public void addTopic}
     * <br><br>
     * Insert new topic into topic table
     * @param topic	(Topic)
     * @return the topic id of the inserted topic, -1 if error occurred 
     */
    public int addTopic(Topic topic) {
    	ContentValues values = new ContentValues();
    	values.put(TOPIC_NAME, topic.getTopicName());
    	
    	return (int) this.db.insert(TOPIC_TABLE, null, values);
    }
    
    /**
     * {@code public Topic getTopic}
     * <br><br>
     * Retrieve a topic from topic table by id
     * @param id	(int)
     * @return topic
     */
    public Topic getTopic(int id) {
    	Topic topic = null;
    	
    	Cursor cursor = this.db.query(TOPIC_TABLE, new String[] { TOPIC_KEY_ID, 
    			TOPIC_NAME }, TOPIC_KEY_ID + " =?",
    			new String[] { String.valueOf(id) }, null, null, null, null);
    	if( cursor.getCount() > 0 ) {
    		cursor.moveToFirst();
    	
    		topic = new Topic(Integer.parseInt(cursor.getString(0)), 
    				cursor.getString(1));
    		
    		cursor.close();
    	}
    	
    	return topic;
    }
  
    /**
     * {@code public List<Topic> getAllTopics}
     * <br><br>
     * Retrieve all topics in topic table as a list
     * @return topicList
     */
    public List<Topic> getAllTopics() { 	
    	List<Topic> topicList = new ArrayList<Topic>();
    	
    	// SQLite command for select all
    	String selectQuery = "SELECT * FROM " + TOPIC_TABLE;
    	Cursor cursor = this.db.rawQuery(selectQuery, null);

    	// add all topics in topic table to topic list
    	if(cursor.moveToFirst()) {
    		do {
    			Topic topic = new Topic(Integer.parseInt(cursor.getString(0)),
    					cursor.getString(1));
    			
    			// add topic to list
    			topicList.add(topic);
    		} while(cursor.moveToNext());
    	}
    	
    	return topicList;
    }
    
    /**
     * {@code public boolean updateTopic}
     * <br><br>
     * Update existing topic in topic table
     * @param topic	(Topic)
     * @return updateSuccess
     */
    public boolean updateTopic(Topic topic) {
    	ContentValues values = new ContentValues();
    	values.put(TOPIC_NAME, topic.getTopicName());
    	
    	// update topic row
    	boolean updateSuccess = this.db.update(TOPIC_TABLE, values, TOPIC_KEY_ID + " =?", 
    			new String[] { String.valueOf(topic.getId()) }) > 0;
    	
    	return updateSuccess;
    }
    
    /**
     * {@code public void Topic}
     * <br><br>
     * Delete topic from topic table by id
     * @param topic	(int)
     */
    public void deleteTopic(int topic_id) {        
    	this.db.delete(TOPIC_TABLE, TOPIC_KEY_ID + " =?",
                new String[] { String.valueOf(topic_id) });
    }
    
// Keyword table CRUD
    /**
     * {@code public void addKeyword}
     * <br><br>
     * Insert new keyword into keyword table
     * @param keyword
     * @return Row id of inserted keyword, -1 if error occurred
     */
    public int addKeyword(Keyword keyword) {    	
    	ContentValues values = new ContentValues();
    	values.put(KEYWORD_TOPIC_ID, keyword.getKeywordTopicId());
    	values.put(KEYWORD_TEXT, keyword.getKeyword());
    	
    	return (int)this.db.insert(KEYWORD_TABLE, null, values);
    }
    
    /**
     * {@code public Keyword getKeyword}
     * <br><br>
     * Retrieve keyword from keyword table
     * @param id	(int)
     * @return keyword
     */
    public Keyword getKeyword(int id) {
    	Keyword keyword = null;
    	
    	Cursor cursor = this.db.query(KEYWORD_TABLE, new String[] { KEYWORD_KEY_ID,
                KEYWORD_TEXT, KEYWORD_TOPIC_ID }, KEYWORD_KEY_ID + " =?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
        
            keyword = new Keyword(Integer.parseInt(cursor.getString(0)),
            		Integer.parseInt(cursor.getString(2)), cursor.getString(1));
        
        	cursor.close();
        }
        
        return keyword;
    }
    
    /**
     * {@code public List<Keyword> getAllKeywords}
     * <br><br>
     * Retrieve a list of keywords with the same topic id
     * @param t_keyword_id	(int)
     * @return keywordList
     */
    public List<Keyword> getAllKeywords(int t_keyword_id) {    	
    	List<Keyword> keywordList = new ArrayList<Keyword>();
    	
    	// SQLite command to select all rows with t_keyword_id
    	String selectQuery = "SELECT * FROM " + KEYWORD_TABLE + " WHERE " 
    	+ KEYWORD_TOPIC_ID + "=" + t_keyword_id;
    	
    	Cursor cursor = this.db.rawQuery(selectQuery, null);
    	
    	// add all keywords with selected topic id's in keyword table to the keyword list
    	if(cursor.moveToFirst()) {
    		do {
    			Keyword keyword = new Keyword(Integer.parseInt(cursor.getString(0)),
    					Integer.parseInt(cursor.getString(2)), cursor.getString(1));
    			
    			// add topic to list
    			keywordList.add(keyword);
    		} while(cursor.moveToNext());
    	}
    	
    	return keywordList;
    }
    
    /**
     * {@code public boolean updateKeyword}
     * <br><br>
     * Update existing keyword in keyword table
     * @param keyword	(Keyword)
     * @return updateSuccess
     */
    public boolean updateKeyword(Keyword keyword) {    	
    	ContentValues values = new ContentValues();
    	values.put(KEYWORD_TEXT, keyword.getKeyword());
    	values.put(KEYWORD_TOPIC_ID, keyword.getKeywordTopicId());
    	
    	boolean updateSuccess = this.db.update(KEYWORD_TABLE, values, KEYWORD_KEY_ID + " =?", 
    			new String[] { String.valueOf(keyword.getId()) }) > 0;
    	    	
    	return updateSuccess;
    }
    
    /**
     * {@code public void deleteKeyword}
     * <br><br>
     * Delete keyword from keyword table by id
     * @param keyword_id	(int)
     */
    public void deleteKeyword(int keyword_id) {        
    	this.db.delete(KEYWORD_TABLE, KEYWORD_KEY_ID + " =?",
                new String[] { String.valueOf(keyword_id) });
    }
    
// Session table CRUD
    /**
     * {@code public void addSession}
     * <br><br>
     * Insert new session into session table
     * @param session	(Session)
     * @return Row id of inserted session, -1 if error occurred
     */
    public int addSession(Session session) {    	
    	ContentValues values = new ContentValues();
    	values.put(SESSION_TOPIC_ID, session.getSessionTopicId());
    	values.put(SESSION_START_TIME, session.getStartTime());
    	values.put(SESSION_DURATION, session.getDuration());
    	values.put(SESSION_TWEETS_PROCESSED, session.getNumTweetsProcessed());
    	values.put(SESSION_AVG_POSITIVE, session.getAvgPosSentiment());
    	values.put(SESSION_AVG_NEGATIVE, session.getAvgNegSentiment());
    	
    	return (int)this.db.insert(SESSION_TABLE, null, values);
    }
    
    /**
     * {@code public Session getSession}
     * <br><br>
     * Retrieve a session from the session table
     * @param id	(int)
     * @return session
     */
    public Session getSession(int id) {
    	Session session = null;
    	
    	Cursor cursor = this.db.query(SESSION_TABLE, new String[] { SESSION_KEY_ID,
                SESSION_TOPIC_ID, SESSION_START_TIME, SESSION_DURATION, 
                SESSION_TWEETS_PROCESSED, SESSION_AVG_POSITIVE, SESSION_AVG_NEGATIVE },
                SESSION_KEY_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
        
            session = new Session(Integer.parseInt(cursor.getString(0)),
        		Integer.parseInt(cursor.getString(1)), cursor.getString(2), Integer.parseInt(cursor.getString(3)), 
        		Integer.parseInt(cursor.getString(4)), Integer.parseInt(cursor.getString(5)), 
        		Integer.parseInt(cursor.getString(6)));
        
        	cursor.close();
        }
        
        return session;
    }
    
    /**
     * {@code public List<Session> getAllSessions}
     * <br><br>
     * Retrieve all sessions with the same topic id
     * @param t_session_id	(int)
     * @return	sessionList
     */
    public List<Session> getAllSessions(int t_session_id) {    	
    	List<Session> sessionList = new ArrayList<Session>();
    	
    	// SQLite command to select all rows with t_keyword_id
    	String selectQuery = "SELECT * FROM " + SESSION_TABLE + " WHERE " 
    	+ SESSION_TOPIC_ID + "=" + t_session_id;
    	Cursor cursor = this.db.rawQuery(selectQuery, null);
    	// add all sessions with selected topic id's in session table to the session list
    	if(cursor.moveToFirst()) {
    		do {
    			Session session = new Session(Integer.parseInt(cursor.getString(0)),
    	        		Integer.parseInt(cursor.getString(1)), cursor.getString(2), Integer.parseInt(cursor.getString(3)), 
    	        		Integer.parseInt(cursor.getString(4)), Integer.parseInt(cursor.getString(5)), 
    	        		Integer.parseInt(cursor.getString(6)));
    			
    			// add topic to list
    			sessionList.add(session);
    		} while(cursor.moveToNext());
    	}
    	return sessionList;
    }
    
    /**
     * {@code public boolean updateSession}
     * <br><br>
     * Update existing session in session table
     * @param session	(Session)
     * @return
     */
    public boolean updateSession(Session session) {
    	ContentValues values = new ContentValues();
    	values.put(SESSION_TOPIC_ID, session.getSessionTopicId());
    	values.put(SESSION_START_TIME, session.getStartTime());
    	values.put(SESSION_DURATION, session.getDuration());
    	values.put(SESSION_TWEETS_PROCESSED, session.getNumTweetsProcessed());
    	values.put(SESSION_AVG_POSITIVE, session.getAvgPosSentiment());
    	values.put(SESSION_AVG_NEGATIVE, session.getAvgNegSentiment());
    	
    	boolean updateSession = this.db.update(SESSION_TABLE, values, SESSION_KEY_ID + " =?",
    			new String[] { String.valueOf(session.getId()) }) > 0;
    	
    	return updateSession;
    }
    
    /** 
     * {@code public void deleteSession}
     * <br><br>
     * Delete a session from session table by id
     * @param session_id	(int)
     */
    public void deleteSession(int session_id) {
    	this.db.delete(SESSION_TABLE, SESSION_KEY_ID + " =?", 
    			new String[] { String.valueOf(session_id) });
    }

}
