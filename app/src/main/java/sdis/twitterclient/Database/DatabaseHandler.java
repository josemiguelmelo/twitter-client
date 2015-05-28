package sdis.twitterclient.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.lang.reflect.Array;
import java.util.ArrayList;

import sdis.twitterclient.Models.Category;
import sdis.twitterclient.Models.Tweet;
import sdis.twitterclient.Models.User;

public class DatabaseHandler extends SQLiteOpenHelper {

    // All Static variables
    // UserFriendsDatabaseHandler Version
    private static final int DATABASE_VERSION = 20;

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




    private static final String TABLE_CATEGORIES = "categories";
    private static final String TABLE_CATEGORIES_USERS = "categories_users";

    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_CATEGORY_NAME = "category_name";


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


        String CREATE_CATEGORIES_TABLE = "CREATE TABLE " + TABLE_CATEGORIES + "("
                + KEY_NAME + " TEXT)";


        String CREATE_CATEGORIES_USERS_TABLE = "CREATE TABLE " + TABLE_CATEGORIES_USERS + "("
                + KEY_USER_ID + " INTEGER," + KEY_CATEGORY_NAME + " TEXT)";


        db.execSQL(CREATE_USER_FRIENDS_TABLE);
        db.execSQL(CREATE_TIMELINE_TABLE);
        db.execSQL(CREATE_CATEGORIES_TABLE);
        db.execSQL(CREATE_CATEGORIES_USERS_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER_FRIENDS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TIMELINE_TWEETS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORIES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORIES_USERS);

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

    // Adding new timeline tweet
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
                long publisherId = Long.parseLong(cursor.getString(1));

                User publisher = getFriend(publisherId);

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


    // Adding new timeline tweet
    public void addCategory(Category category) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        Category existingCategory = getCategory(category.getName());

        if(existingCategory == null){
            values.put(KEY_NAME, category.getName());
            // Inserting Row
            db.insert(TABLE_CATEGORIES, null, values);

            for(User user: category.getUsers()){
                addUserToCategory(user, category);
            }

            db.close(); // Closing database connection
        }
    }

    private boolean existsUser(User user, ArrayList<User> userList){
        for(User userInList: userList){
            if(userInList.getId() == user.getId())
                return true;
        }
        return false;
    }

    public void addUserToCategory(User user, Category category){
        ArrayList<User> categoryUserList = getCategoryUsers(category);
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        if(categoryUserList == null){
            values.put(KEY_CATEGORY_NAME, category.getName());
            values.put(KEY_USER_ID, user.getId());
            // Inserting Row
            db.insert(TABLE_CATEGORIES_USERS, null, values);
            db.close(); // Closing database connection
        }
        else if(!existsUser(user, categoryUserList)){
            values.put(KEY_CATEGORY_NAME, category.getName());
            values.put(KEY_USER_ID, user.getId());
            // Inserting Row
            db.insert(TABLE_CATEGORIES_USERS, null, values);
            db.close(); // Closing database connection
        }
    }

    public int updateCategory(Category oldCategory, Category newCategory) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, newCategory.getName());

        ContentValues valuesCategoryUsers = new ContentValues();
        valuesCategoryUsers.put(KEY_CATEGORY_NAME, newCategory.getName());

        // updating row
            db.update(TABLE_CATEGORIES, values, KEY_NAME + " = ?",
                new String[] { String.valueOf(oldCategory.getName()) });

        return db.update(TABLE_CATEGORIES_USERS, valuesCategoryUsers, KEY_CATEGORY_NAME + " = ?",
                new String[] { String.valueOf(oldCategory.getName()) });
    }

    public ArrayList<User> getCategoryUsers(Category category){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_CATEGORIES_USERS, new String[] { KEY_USER_ID, KEY_CATEGORY_NAME}, KEY_CATEGORY_NAME + "=?",
                new String[] { category.getName() }, null, null, null, null);

        if (cursor.getCount() == 0)
            return null;

        cursor.moveToFirst();

        ArrayList<User> users = new ArrayList<User>();

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                long userId = Long.parseLong(cursor.getString(0));

                User user = getFriend(userId);
                users.add(user);
            } while (cursor.moveToNext());
        }
        return users;
    }

    public Category getCategory(String name) {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_CATEGORIES, new String[] { KEY_NAME}, KEY_NAME + "=?",
                new String[] { name }, null, null, null, null);

        if (cursor.getCount() == 0)
            return null;

        cursor.moveToFirst();


        Category category = new Category(cursor.getString(0));

        category.setUsers(getCategoryUsers(category));

        return category;
    }

    public ArrayList<Category> getAllCategories() {
        ArrayList<Category> categoriesList = new ArrayList<Category>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_CATEGORIES;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.getCount() == 0)
            return null;

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                String categoryName = cursor.getString(0);

                Category category = new Category(categoryName);
                category.setUsers(getCategoryUsers(category));

                categoriesList.add(category);
            } while (cursor.moveToNext());
        }

        return categoriesList;
    }



}