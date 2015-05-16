package sdis.twitterclient;


import android.util.Log;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import twitter4j.Twitter;
import twitter4j.auth.AccessToken;

public class User {

    private String name;
    private String screen_name;
    private long id;

    private AccessToken accessToken;


    private String email;

    private ArrayList<User> friendsList;
    private ArrayList<Tweet> tweetsPublished;
    private ArrayList<Tweet> homeTimeLineTweets;

    public User(long id, String name, String screen_name){
        this.id = id;
        this.name = name;
        this.screen_name = screen_name;
        this.friendsList = new ArrayList<User>();
        this.tweetsPublished = new ArrayList<Tweet>();
        this.homeTimeLineTweets = new ArrayList<Tweet>();
        this.accessToken = null;
    }


    public User(long id, AccessToken accessToken){
        this.id = id;
        this.accessToken = accessToken;
        this.name = null;
        this.screen_name = null;
        this.friendsList = new ArrayList<User>();
        this.tweetsPublished = new ArrayList<Tweet>();
        this.homeTimeLineTweets = new ArrayList<Tweet>();
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

    public ArrayList<Tweet> getHomeTimeLineTweets() {
        return homeTimeLineTweets;
    }

    public void setHomeTimeLineTweets(ArrayList<Tweet> homeTimeLineTweets) {
        this.homeTimeLineTweets = homeTimeLineTweets;
    }

    public ArrayList<User> getFriendsList() {
        return friendsList;
    }

    public void setFriendsList(ArrayList<User> friendsList) {
        this.friendsList = friendsList;
    }

    public AccessToken getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(AccessToken accessToken) {
        this.accessToken = accessToken;
    }

    public void loadTweets(){
        TwitterApiRequest apiRequest = new TwitterApiRequest(TwitterApiRequest.GET_USER_TWEETS, LoginActivity.TWITTER_CONSUMER_KEY, LoginActivity.TWITTER_CONSUMER_SECRET, accessToken.getToken(), accessToken.getTokenSecret());

        try {
            this.tweetsPublished = (ArrayList<Tweet>) apiRequest.execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
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
        TwitterApiRequest apiRequest = new TwitterApiRequest(TwitterApiRequest.GET_FRIENDS_LIST, LoginActivity.TWITTER_CONSUMER_KEY, LoginActivity.TWITTER_CONSUMER_SECRET, accessToken.getToken(), accessToken.getTokenSecret());

        try {
            this.friendsList = (ArrayList<User>) apiRequest.execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    public void loadFriends(){
        TwitterApiRequest apiRequest = new TwitterApiRequest(TwitterApiRequest.GET_FRIENDS_LIST, LoginActivity.TWITTER_CONSUMER_KEY, LoginActivity.TWITTER_CONSUMER_SECRET, accessToken.getToken(), accessToken.getTokenSecret());

        try {
            this.friendsList = (ArrayList<User>) apiRequest.execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    public void loadTimeline(){
        TwitterApiRequest apiRequest = new TwitterApiRequest(TwitterApiRequest.GET_TIMELINE_TWEETS, LoginActivity.TWITTER_CONSUMER_KEY, LoginActivity.TWITTER_CONSUMER_SECRET, accessToken.getToken(), accessToken.getTokenSecret());

        try {
            this.tweetsPublished = (ArrayList<Tweet>) apiRequest.execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }


}
