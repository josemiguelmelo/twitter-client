package sdis.twitterclient.Models;


import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.util.Log;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import sdis.twitterclient.API.DownloadImageTask;
import sdis.twitterclient.API.TwitterApiRequest;
import sdis.twitterclient.Database.DatabaseHandler;
import sdis.twitterclient.GUI.LoginActivity;
import sdis.twitterclient.GUI.TimelineAdapter;
import twitter4j.Twitter;
import twitter4j.auth.AccessToken;

public class User implements Serializable{
    private String name;
    private String screen_name;
    private long id;
    private transient AccessToken accessToken;

    private String email;
    private String profileImage;

    private ArrayList<User> friendsList;

    private ArrayList<User> followersList;
    private transient ArrayList<Tweet> tweetsPublished;
    private transient ArrayList<Tweet> homeTimeLineTweets;
    private transient ArrayList<Category> categories;

    public transient Context context;
    private transient Bitmap profileBitmapImage;
    public transient DatabaseHandler databaseHandler;



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
        this.categories = new ArrayList<>();
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
        this.categories = new ArrayList<>();
    }

    private ArrayList<Tweet> invertTweetList(ArrayList<Tweet> list){

        ArrayList<Tweet> tweetsInverted = new ArrayList<>();

        for(int i = list.size()-1; i >= 0; i--){
            tweetsInverted.add(list.get(i));
        }

        return tweetsInverted;
    }
    public void initFromDatabase(){
        this.friendsList = databaseHandler.getAllFriends();
        this.homeTimeLineTweets = databaseHandler.getAllTimelineTweets();


        loadCategories();

        if(friendsList == null || homeTimeLineTweets == null){
            this.friendsList = new ArrayList<>();
            this.homeTimeLineTweets = new ArrayList<>();
            loadAllFromAPI();
        }
        this.homeTimeLineTweets = invertTweetList(homeTimeLineTweets);


        for(User friend : this.friendsList){
            friend.setProfileBitmapImage();
        }

        for(Tweet tweet: this.getHomeTimeLineTweets()){
            tweet.setPublisher(getFriendByUsername(tweet.getPublisherUsername()));
        }
    }


    public void loadAllFromAPI(){
        Thread th = new Thread() {
            @Override
            public void run() {
                ArrayList<String> requests = new ArrayList<>();
                requests.add(TwitterApiRequest.GET_FRIENDS_LIST);
                requests.add(TwitterApiRequest.GET_FOLLOWERS_LIST);
                requests.add(TwitterApiRequest.GET_USER_TWEETS);
                requests.add(TwitterApiRequest.GET_TIMELINE_TWEETS);

                TwitterApiRequest apiRequest = new TwitterApiRequest(requests, LoginActivity.TWITTER_CONSUMER_KEY, LoginActivity.TWITTER_CONSUMER_SECRET, getAccessToken().getToken(), getAccessToken().getTokenSecret());

                try {
                    HashMap<String, Object> apiResult = (HashMap<String, Object>) apiRequest.execute().get();

                    friendsList = (ArrayList<User>) apiResult.get(TwitterApiRequest.GET_FRIENDS_LIST);
                    followersList = (ArrayList<User>) apiResult.get(TwitterApiRequest.GET_FOLLOWERS_LIST);
                    tweetsPublished = (ArrayList<Tweet>) apiResult.get(TwitterApiRequest.GET_USER_TWEETS);
                    homeTimeLineTweets = (ArrayList<Tweet>) apiResult.get(TwitterApiRequest.GET_TIMELINE_TWEETS);

                    for(User friend : friendsList){
                        friend.setProfileBitmapImage();
                        databaseHandler.addUserFriend(friend);
                    }

                    for(User follower : followersList){
                        // TODO: followers database table and methods
                    }

                    for(Tweet tweetPublished : tweetsPublished){
                        // TODO: tweets published database table and methods
                    }

                    homeTimeLineTweets = invertTweetList(homeTimeLineTweets);
                    for(Tweet tweet : homeTimeLineTweets){
                        User user = getFriendByUsername(tweet.getPublisherUsername());
                        tweet.setPublisher(user);
                        databaseHandler.addTimelineTweet(tweet);
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

            }
        };
        th.start();
        try {
            th.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public Bitmap getProfileBitmapImage() {
        return profileBitmapImage;
    }

    public void setProfileBitmapImage() {
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

    public ArrayList<Category> getCategories(){ return this.categories; }
    public void setCategories(ArrayList<Category> categories){ this.categories = categories; }

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
        ArrayList<String> request = new ArrayList<>();
        request.add(TwitterApiRequest.POST_RETWEET);
        TwitterApiRequest apiRequest = new TwitterApiRequest(request, LoginActivity.TWITTER_CONSUMER_KEY, LoginActivity.TWITTER_CONSUMER_SECRET, accessToken.getToken(), accessToken.getTokenSecret());

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("id", tweet_id));

        apiRequest.setPostParams(params);

        HashMap<String, Object> result;
        try {
            result = (HashMap<String, Object>) apiRequest.execute().get();
            Log.d("post result", (String) result.get(TwitterApiRequest.POST_RETWEET));
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    public void postReplyTweet(String tweetMessage, long answerTweedId){

        ArrayList<String> request = new ArrayList<>();
        request.add(TwitterApiRequest.POST_NEW_TWEET);

        TwitterApiRequest apiRequest = new TwitterApiRequest(request, LoginActivity.TWITTER_CONSUMER_KEY, LoginActivity.TWITTER_CONSUMER_SECRET, accessToken.getToken(), accessToken.getTokenSecret());

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("status", tweetMessage));
        params.add(new BasicNameValuePair("in_reply_to_status_id", Long.toString(answerTweedId)));

        apiRequest.setPostParams(params);

        HashMap<String, Object> result;
        try {
            result = (HashMap<String, Object>) apiRequest.execute().get();
            Log.d("post result", (String) result.get(TwitterApiRequest.POST_NEW_TWEET));
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    public void postTweet(String tweetMessage)
    {

        ArrayList<String> request = new ArrayList<>();
        request.add(TwitterApiRequest.POST_NEW_TWEET);
        TwitterApiRequest apiRequest = new TwitterApiRequest(request, LoginActivity.TWITTER_CONSUMER_KEY, LoginActivity.TWITTER_CONSUMER_SECRET, accessToken.getToken(), accessToken.getTokenSecret());

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("status", tweetMessage));

        apiRequest.setPostParams(params);

        HashMap<String, Object> result;
        try {
            result = (HashMap<String, Object>) apiRequest.execute().get();
            Log.d("post result", (String) result.get(TwitterApiRequest.POST_NEW_TWEET));
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    public void loadFollowers(){

    }

    public void loadFriends(){

    }

    public void loadTweets(){

    }

    public void loadTimeline(){
        Thread th = new Thread() {
            @Override
            public void run() {
                ArrayList<String> requests = new ArrayList<>();
                requests.add(TwitterApiRequest.GET_TIMELINE_TWEETS);

                TwitterApiRequest apiRequest = new TwitterApiRequest(requests, LoginActivity.TWITTER_CONSUMER_KEY, LoginActivity.TWITTER_CONSUMER_SECRET, getAccessToken().getToken(), getAccessToken().getTokenSecret());

                try {
                    HashMap<String, Object> apiResult = (HashMap<String, Object>) apiRequest.execute().get();

                    homeTimeLineTweets = (ArrayList<Tweet>) apiResult.get(TwitterApiRequest.GET_TIMELINE_TWEETS);

                    for(Tweet tweet : homeTimeLineTweets){
                        User user = getFriendByUsername(tweet.getPublisherUsername());
                        tweet.setPublisher(user);
                        databaseHandler.addTimelineTweet(tweet);
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

            }
        };
        th.start();
        try {
            th.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public void loadCategories() {
        this.categories = databaseHandler.getAllCategories();

        if(this.categories == null)
            this.categories = new ArrayList<>();
    }
}
