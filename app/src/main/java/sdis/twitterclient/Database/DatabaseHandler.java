package sdis.twitterclient.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.lang.reflect.Array;
import java.util.ArrayList;

import sdis.twitterclient.Models.Tweet;
import sdis.twitterclient.Models.User;

public class DatabaseHandler extends SQLiteOpenHelper {

    // All Static variables
    // UserFriendsDatabaseHandler Version
    private static final int DATABASE_VERSION = 8;

    // UserFriendsDatabaseHandler Name
    private static final String DATABASE_NAME = "twitter_client";

    // Contacts table name
    private static final String TABLE_USER_FRIENDS = "user_friends";

    // Contacts Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_SCREEN_NAME = "screen_name";
    private static final String KEY_PROFILE_IMAGE_LOCATION = "profile_image";


    private static final String TABLE_TIMELINE_TWEETS = "timeline_tweets";
    private static final String KEY_CREATED_AT = "created_at";
    private static final String KEY_TEXT = "text";
    private static final String KEY_PUBLISHER_ID = "publisher_id";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USER_FRIENDS_TABLE = "CREATE TABLE " + TABLE_USER_FRIENDS + "("
                + KEY_ID + " INTEGER," + KEY_NAME + " TEXT,"
                + KEY_SCREEN_NAME + " TEXT, " + KEY_PROFILE_IMAGE_LOCATION + " TEXT )";


        String CREATE_TIMELINE_TABLE = "CREATE TABLE " + TABLE_TIMELINE_TWEETS + "("
                + KEY_ID + " INTEGER," + KEY_PUBLISHER_ID + " TEXT,"
                + KEY_TEXT + " TEXT, " + KEY_CREATED_AT + " TEXT )";

        db.execSQL(CREATE_USER_FRIENDS_TABLE);
        db.execSQL(CREATE_TIMELINE_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER_FRIENDS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TIMELINE_TWEETS);

        // Create tables again
        onCreate(db);
    }

    // Adding new friend
    public void addUserFriend(User user) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        User existingFriend = getFriend(user.getId());

        if(existingFriend == null){
            values.put(KEY_ID, user.getId());
            values.put(KEY_NAME, user.getName());
            values.put(KEY_SCREEN_NAME, user.getScreen_name());
            values.put(KEY_PROFILE_IMAGE_LOCATION, user.getProfileImage());

            // Inserting Row
            db.insert(TABLE_USER_FRIENDS, null, values);
            db.close(); // Closing database connection
        }else{
            updateUser(user);
        }

    }

    public int updateUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_ID, user.getId());
        values.put(KEY_NAME, user.getName());
        values.put(KEY_SCREEN_NAME, user.getScreen_name());
        values.put(KEY_PROFILE_IMAGE_LOCATION, user.getProfileImage());


        // updating row
        return db.update(TABLE_USER_FRIENDS, values, KEY_ID + " = ?",
                new String[] { String.valueOf(user.getId()) });
    }

    public User getFriend(long id){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_USER_FRIENDS, new String[] { KEY_ID,
                        KEY_NAME, KEY_SCREEN_NAME, KEY_PROFILE_IMAGE_LOCATION }, KEY_ID + "=?",
                new String[] { Long.toString(id) }, null, null, null, null);


        if (cursor.getCount() == 0)
            return null;

        cursor.moveToFirst();

        User friend = new User(null, Long.parseLong(cursor.getString(0)),
                cursor.getString(1), cursor.getString(2));
        friend.setProfileImage(cursor.getString(3));
        // return contact
        return friend;
    }


    public User getFriend(String screen_name) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_USER_FRIENDS, new String[] { KEY_ID,
                        KEY_NAME, KEY_SCREEN_NAME, KEY_PROFILE_IMAGE_LOCATION }, KEY_SCREEN_NAME + "=?",
                new String[] { screen_name }, null, null, null, null);
        if (cursor.getCount() == 0)
            return null;

        cursor.moveToFirst();

        User friend = new User(null, Long.parseLong(cursor.getString(0)),
                cursor.getString(1), cursor.getString(2));
        friend.setProfileImage(cursor.getString(3));
        // return contact
        return friend;
    }

    // Getting All Contacts
    public ArrayList<User> getAllFriends() {
        ArrayList<User> friendsList = new ArrayList<User>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_USER_FRIENDS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.getCount() == 0)
            return null;

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                User friend = new User(null, Long.parseLong(cursor.getString(0)), cursor.getString(1), cursor.getString(2));
                friend.setProfileImage(cursor.getString(3));
                // Adding contact to list
                friendsList.add(friend);
            } while (cursor.moveToNext());
        }

        // return friends list
        return friendsList;
    }



    // Adding new friend
    public void addTimelineTweet(Tweet tweet) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        Tweet existingTweet = getTimelineTweet(tweet.getId());

        if(existingTweet == null){
            values.put(KEY_ID, tweet.getId());
            values.put(KEY_PUBLISHER_ID, tweet.getPublisher().getId());
            values.put(KEY_TEXT, tweet.getText());
            values.put(KEY_CREATED_AT, tweet.getCreated_at());

            // Inserting Row
            db.insert(TABLE_TIMELINE_TWEETS, null, values);
            db.close(); // Closing database connection
        }else{
            updateTimelineTweet(tweet);
        }

    }

    public int updateTimelineTweet(Tweet tweet) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, tweet.getId());
        values.put(KEY_PUBLISHER_ID, tweet.getPublisher().getId());
        values.put(KEY_TEXT, tweet.getText());
        values.put(KEY_CREATED_AT, tweet.getCreated_at());


        // updating row
        return db.update(TABLE_TIMELINE_TWEETS, values, KEY_ID + " = ?",
                new String[] { String.valueOf(tweet.getId()) });
    }

    public Tweet getTimelineTweet(long id) {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_TIMELINE_TWEETS, new String[] { KEY_ID,
                        KEY_PUBLISHER_ID, KEY_TEXT, KEY_CREATED_AT }, KEY_ID + "=?",
                new String[] { Long.toString(id) }, null, null, null, null);

        if (cursor.getCount() == 0)
            return null;

        cursor.moveToFirst();

        long publisherId = Long.parseLong(cursor.getString(1));

        User publisher = getFriend(publisherId);

        Tweet tweet = new Tweet(publisher.getScreen_name(), Long.parseLong(cursor.getString(0)),
                cursor.getString(3), cursor.getString(2));

        tweet.setPublisher(publisher);
        // return contact
        return tweet;
    }


    // Getting All Contacts
    public ArrayList<Tweet> getAllTimelineTweets() {
        ArrayList<Tweet> timelineList = new ArrayList<Tweet>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_TIMELINE_TWEETS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.getCount() == 0)
            return null;

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                int publisherId = Integer.parseInt(cursor.getString(1));

                User publisher = getFriend(publisherId);

                Log.d("publisher name", publisher.getName());

                Tweet tweet = new Tweet(publisher.getScreen_name(), Long.parseLong(cursor.getString(0)),
                        cursor.getString(3), cursor.getString(2));

                tweet.setPublisher(publisher);

                // Adding contact to list
                timelineList.add(tweet);
            } while (cursor.moveToNext());
        }

        // return timelist list
        return timelineList;
    }


}