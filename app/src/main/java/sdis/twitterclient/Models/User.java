package sdis.twitterclient.Models;


import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import sdis.twitterclient.API.DownloadImageTask;
import sdis.twitterclient.API.TwitterApiRequest;
import sdis.twitterclient.Database.DatabaseHandler;
import sdis.twitterclient.GUI.LoginActivity;
import twitter4j.auth.AccessToken;

public class User {
    private String name;
    private String screen_name;
    private long id;
    private AccessToken accessToken;

    private String email;
    private String profileImage;

    private ArrayList<User> friendsList;

    private ArrayList<User> followersList;
    private ArrayList<Tweet> tweetsPublished;
    private ArrayList<Tweet> homeTimeLineTweets;

    private Bitmap profileBitmapImage;
    private Context context;
    public DatabaseHandler databaseHandler;


    public User(Context context, long id, String name, String screen_name){
        this.id = id;
        this.name = name;
        this.screen_name = screen_name;
        this.friendsList = new ArrayList<User>();
        this.followersList = new ArrayList<User>();
        this.tweetsPublished = new ArrayList<Tweet>();
        this.homeTimeLineTweets = new ArrayList<Tweet>();
        this.accessToken = null;
        this.context = context;
        this.databaseHandler = new DatabaseHandler(context);
    }


    public User(Context context, long id, AccessToken accessToken){
        this.id = id;
        this.accessToken = accessToken;
        this.name = null;
        this.screen_name = null;
        this.friendsList = new ArrayList<User>();
        this.followersList = new ArrayList<User>();
        this.tweetsPublished = new ArrayList<Tweet>();
        this.homeTimeLineTweets = new ArrayList<Tweet>();
        this.context = context;
        this.databaseHandler = new DatabaseHandler(context);
    }

    /**
     * Loads friends, followers and tweets
     */
    public void init(){
        this.loadFriends();
        this.loadFollowers();
        this.loadTweets();
        this.loadTimeline();
    }


    public Bitmap getProfileBitmapImage() {
        return profileBitmapImage;
    }

    public void setProfileBitmapImage() {
        Log.d("url image", getProfileImage());
        try {
            DownloadImageTask getImage = new DownloadImageTask(getProfileImage());
            this.profileBitmapImage = (Bitmap) getImage.execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }


    public ArrayList<User> getFollowersList() {
        return followersList;
    }

    public void setFollowersList(ArrayList<User> followersList) {
        this.followersList = followersList;
    }

    public void appendFollowersList(ArrayList<User> followersList) {
        this.followersList.addAll(followersList);
    }



    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public User getFriendByUsername(String username){
        for(User friend : friendsList){
            if(friend.getScreen_name().equals(username)){
                return friend;
            }
        }
        return null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getScreen_name() {
        return screen_name;
    }

    public void setScreen_name(String screen_name) {
        this.screen_name = screen_name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public ArrayList<Tweet> getTweetsPublished() {
        return tweetsPublished;
    }

    public void setTweetsPublished(ArrayList<Tweet> tweetsPublished) {
        this.tweetsPublished = tweetsPublished;
    }

    public void appendTweetsPublished(ArrayList<Tweet> tweetsPublished) {
        this.tweetsPublished.addAll(tweetsPublished);
    }


    public ArrayList<Tweet> getHomeTimeLineTweets() {
        return homeTimeLineTweets;
    }

    public void setHomeTimeLineTweets(ArrayList<Tweet> homeTimeLineTweets) {
        this.homeTimeLineTweets = homeTimeLineTweets;
    }

    public void appendHomeTimeLineTweets(ArrayList<Tweet> homeTimeLineTweets) {
        this.homeTimeLineTweets.addAll( homeTimeLineTweets);
    }


    public ArrayList<User> getFriendsList() {
        return friendsList;
    }

    public void setFriendsList(ArrayList<User> friendsList) {
        this.friendsList = friendsList;
    }

    public void appendFriendsList(ArrayList<User> friendsList) {
        this.friendsList.addAll(friendsList);
    }

    public AccessToken getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(AccessToken accessToken) {
        this.accessToken = accessToken;
    }

    public void postReTweet(String tweet_id){
        TwitterApiRequest apiRequest = new TwitterApiRequest(TwitterApiRequest.POST_RETWEET, LoginActivity.TWITTER_CONSUMER_KEY, LoginActivity.TWITTER_CONSUMER_SECRET, accessToken.getToken(), accessToken.getTokenSecret());

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("id", tweet_id));

        apiRequest.setPostParams(params);

        String result;
        try {
            result = (String) apiRequest.execute().get();
            Log.d("post result", result);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }


    public void postTweet(String tweetMessage)
    {
        TwitterApiRequest apiRequest = new TwitterApiRequest(TwitterApiRequest.POST_NEW_TWEET, LoginActivity.TWITTER_CONSUMER_KEY, LoginActivity.TWITTER_CONSUMER_SECRET, accessToken.getToken(), accessToken.getTokenSecret());

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("status", tweetMessage));

        apiRequest.setPostParams(params);

        String result;
        try {
            result = (String) apiRequest.execute().get();
            Log.d("post result", result);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }




    public void loadFollowers(){
        TwitterApiRequest apiRequest = new TwitterApiRequest(TwitterApiRequest.GET_FOLLOWERS_LIST, LoginActivity.TWITTER_CONSUMER_KEY, LoginActivity.TWITTER_CONSUMER_SECRET, getAccessToken().getToken(), getAccessToken().getTokenSecret());

        try {
            this.followersList = (ArrayList<User>) apiRequest.execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    public void loadFriends(){
        TwitterApiRequest apiRequest = new TwitterApiRequest(TwitterApiRequest.GET_FRIENDS_LIST, LoginActivity.TWITTER_CONSUMER_KEY, LoginActivity.TWITTER_CONSUMER_SECRET, getAccessToken().getToken(), getAccessToken().getTokenSecret());

        try {
            this.friendsList = (ArrayList<User>) apiRequest.execute().get();
            Log.d("friends list", ""+friendsList.size());
            for(User user: this.getFriendsList()){
                user.setProfileBitmapImage();
                this.databaseHandler.addUserFriend(user);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    public void loadTweets(){
        TwitterApiRequest apiRequest = new TwitterApiRequest(TwitterApiRequest.GET_USER_TWEETS, LoginActivity.TWITTER_CONSUMER_KEY, LoginActivity.TWITTER_CONSUMER_SECRET, getAccessToken().getToken(), getAccessToken().getTokenSecret());

        try {
            this.tweetsPublished = (ArrayList<Tweet>) apiRequest.execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    public void loadTimeline(){
        TwitterApiRequest apiRequest = new TwitterApiRequest(TwitterApiRequest.GET_TIMELINE_TWEETS, LoginActivity.TWITTER_CONSUMER_KEY, LoginActivity.TWITTER_CONSUMER_SECRET, getAccessToken().getToken(), getAccessToken().getTokenSecret());

        try {
            this.homeTimeLineTweets = (ArrayList<Tweet>) apiRequest.execute().get();

            for(Tweet tweet: this.getHomeTimeLineTweets()){
                tweet.setPublisher(getFriendByUsername(tweet.getPublisherUsername()));
                this.databaseHandler.addTimelineTweet(tweet);
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }


}
