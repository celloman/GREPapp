package com.grep.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


/**
 * This classes handles all SQLite database functionality. 
 * 
 * It contains CRUD operations for each table in the 
 * database.
 * 
 * @author Everett
 *
 */
public class DatabaseHandler extends SQLiteOpenHelper {
	
	//Static variables
	//Database version
	private static final int DATABASE_VERSION = 1;
	
	// Database name
	private static final String DATABASE_NAME = "SMS_DB";
	
	// Table names
	private static final String AUTH_TABLE = "credentials";
	private static final String TOPIC_TABLE = "topics";
	private static final String KEYWORD_TABLE = "keywords";
	private static final String SESSION_TABLE = "sessions";
	
	// Authentication Table column names
	private static final String USER_KEY_ID = "id";
	private static final String CONSUMER_KEY = "c_key";
	private static final String CONSUMER_SECRET = "c_secret";
	
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
	
	// Constructor
	public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
	
	// Create Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create Authentication Table in SQLite DB
    	String CREATE_AUTH_TABLE = "CREATE TABLE " + AUTH_TABLE + " ("
                + USER_KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + CONSUMER_KEY + " TEXT, "
                + CONSUMER_SECRET + " TEXT" + ")";
        db.execSQL(CREATE_AUTH_TABLE);
        
        // Create Topic Table in SQLite DB
        String CREATE_TOPIC_TABLE = "CREATE TABLE " + TOPIC_TABLE + " ("
        		+ TOPIC_KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + TOPIC_NAME + " TEXT "  + ")";
        db.execSQL(CREATE_TOPIC_TABLE);
        
        // Create Topic Table in SQLite DB
        String CREATE_KEYWORD_TABLE = "CREATE TABLE " + KEYWORD_TABLE + " ("
        		+ KEYWORD_KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + KEYWORD_TEXT + " TEXT,"
        		+ KEYWORD_TOPIC_ID + " INTEGER, FOREIGN KEY (" + KEYWORD_TOPIC_ID + ") REFERENCES "
        		+ TOPIC_TABLE + " (" + TOPIC_KEY_ID + ")" + ")";
        db.execSQL(CREATE_KEYWORD_TABLE);
        
        // Create Topic Table in SQLite DB
        String CREATE_SESSION_TABLE = "CREATE TABLE " + SESSION_TABLE + " ("
        		+ SESSION_KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + SESSION_TOPIC_ID 
        		+ " INTEGER, FOREIGN KEY (" + SESSION_TOPIC_ID + ") REFERENCES " + TOPIC_TABLE 
        		+ " (" + TOPIC_KEY_ID + "), " + SESSION_START_TIME + " TEXT, " + SESSION_DURATION 
        		+ " TEXT, " + SESSION_TWEETS_PROCESSED + " INTEGER, " + SESSION_AVG_POSITIVE
        		+ " INTEGER, " + SESSION_AVG_NEGATIVE + " INTEGER" + ")";
        db.execSQL(CREATE_SESSION_TABLE);
        
    }
 
    // Upgrade database
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
    
   
    /*
     *  CRUD operations ( Create, Read, Update, Delete )
     *  Operations are listed in order for each table.
     */
    
// Authentication table CRUD
    // Insert credentials into authentication table
    public void addCredentials(Credentials credentials) {
    	SQLiteDatabase db = this.getWritableDatabase();
    	
    	ContentValues values = new ContentValues();
    	values.put(CONSUMER_KEY, credentials.getConsumerKey());
    	values.put(CONSUMER_SECRET, credentials.getConsumerSecret());
    	
    	db.insert(AUTH_TABLE, null, values);
    	db.close();
    }
    
    // Retrieve credentials from authentication table
    public Credentials getCredentials(int id) {
    	SQLiteDatabase db = this.getReadableDatabase();
    	
    	Cursor cursor = db.query(AUTH_TABLE, new String[] { USER_KEY_ID,
                CONSUMER_KEY, CONSUMER_SECRET }, USER_KEY_ID + " =?",
                new String[] { String.valueOf(id) }, null, null, null, null);
    	if (cursor != null)
            cursor.moveToFirst();
    	
    	Credentials credentials = new Credentials(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1), cursor.getString(2));
    	
    	cursor.close();
    	db.close();
    	
    	return credentials;
    }
    
    // Update existing credentials
    public int updateCredentials(Credentials credentials) {
    	SQLiteDatabase db = this.getWritableDatabase();
    	
    	ContentValues values = new ContentValues();
    	values.put(CONSUMER_KEY, credentials.getConsumerKey());
    	values.put(CONSUMER_SECRET, credentials.getConsumerSecret());
    	
    	// update credentials row
    	int numRowsUpdated = db.update(AUTH_TABLE, values, USER_KEY_ID + " = ?",
                new String[] { String.valueOf(credentials.getId()) });
        
    	db.close();
        
    	return numRowsUpdated;
    }
    
    // Delete credentials from authentication table
    public void deleteCredentials(Credentials credentials) {
    	SQLiteDatabase db = this.getWritableDatabase();
        
    	db.delete(AUTH_TABLE, USER_KEY_ID + " = ?",
                new String[] { String.valueOf(credentials.getId()) });
        db.close();
    }
    
// Topic table CRUD
    // Insert new topic into topic table
    public void addTopic(Topic topic) {
    	SQLiteDatabase db = this.getWritableDatabase();
    	
    	ContentValues values = new ContentValues();
    	values.put(TOPIC_NAME, topic.getTopicName());
    	
    	db.insert(TOPIC_TABLE, null, values);
    	db.close();
    }
    
    // Retrieve a topic from topic table
    public Topic getTopic(int id) {
    	SQLiteDatabase db = this.getReadableDatabase();
    	
    	Cursor cursor = db.query(TOPIC_TABLE, new String[] { TOPIC_KEY_ID, 
    			TOPIC_NAME }, TOPIC_KEY_ID + " =?",
    			new String[] { String.valueOf(id) }, null, null, null, null);
    	if( cursor != null )
    		cursor.moveToFirst();
    	
    	Topic topic = new Topic(Integer.parseInt(cursor.getString(0)), 
    			cursor.getString(1));
    	
    	cursor.close();
    	db.close();
    	
    	return topic;
    }
  
    // Retrieve all topics in topic table
    public List<Topic> getAllTopics() {
    	SQLiteDatabase db = this.getReadableDatabase();
    	
    	List<Topic> topicList = new ArrayList<Topic>();
    	
    	// SQLite command for select all
    	String selectQuery = "SELECT * FROM " + TOPIC_TABLE;
    	Cursor cursor = db.rawQuery(selectQuery, null);
    	
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
    
    // Update existing topic
    public int updateTopic(Topic topic) {
    	SQLiteDatabase db = this.getReadableDatabase();
    	
    	ContentValues values = new ContentValues();
    	values.put(TOPIC_NAME, topic.getTopicName());
    	
    	// update topic row
    	int numRowsUpdated = db.update(TOPIC_TABLE, values, TOPIC_KEY_ID + " =?", 
    			new String[] { String.valueOf(topic.getId()) });
    	db.close();
    	
    	return numRowsUpdated;
    }
    
    // Delete topic from topic table
    public void deleteTopic(Topic topic) {
    	SQLiteDatabase db = this.getWritableDatabase();
        
    	db.delete(TOPIC_TABLE, TOPIC_KEY_ID + " = ?",
                new String[] { String.valueOf(topic.getId()) });
        db.close();
    }
    
// Keyword table CRUD
    // Insert new keyword into keyword table
    public void addKeyword
}
