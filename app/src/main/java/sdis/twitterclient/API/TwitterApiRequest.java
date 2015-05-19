package sdis.twitterclient.API;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import sdis.twitterclient.Models.Tweet;
import sdis.twitterclient.Models.User;

import android.os.AsyncTask;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class TwitterApiRequest extends AsyncTask{
    public static final String POST = "POST";
    public static final String GET = "GET";
    public static final String home_timeline = "https://api.twitter.com/1.1/statuses/home_timeline.json";
    public static final String user_tweets = "https://api.twitter.com/1.1/statuses/user_timeline.json";
    public static final String friends_list = "https://api.twitter.com/1.1/friends/list.json";
    public static final String followers_list = "https://api.twitter.com/1.1/followers/list.json";

    public static final String post_new_tweet = "https://api.twitter.com/1.1/statuses/update.json";
    public static final String post_retweet = "https://api.twitter.com/1.1/statuses/retweet/";



    public static final String GET_USER_TWEETS = "GET_USER_TWEETS";
    public static final String GET_TIMELINE_TWEETS = "GET_TIMELINE_TWEETS";
    public static final String GET_FRIENDS_LIST = "GET_FRIENDS_LIST";
    public static final String GET_FOLLOWERS_LIST = "GET_FOLLOWERS_LIST";

    public static final String POST_NEW_TWEET = "POST_NEW_TWEET";
    public static final String POST_RETWEET = "POST_RETWEET";

    private static final int REQUEST_TWEETS_NUMBER = 200;

    private String request;

    private OAuthConsumer consumer;

    private String consumerKey, consumerSecret;
    private String token, secretToken;

    private List<NameValuePair> postParams;

    public TwitterApiRequest(String request, String consumerKey, String consumerSecret, String token, String secretToken){
        this.consumerKey = consumerKey;
        this.consumerSecret = consumerSecret;
        this.token = token;
        this.secretToken = secretToken;
        this.request = request;

        this.consumer = new CommonsHttpOAuthConsumer(this.consumerKey, this.consumerSecret);
        this.consumer.setTokenWithSecret(this.token, this.secretToken);

        this.postParams = null;
    }

    public TwitterApiRequest(String request, List<NameValuePair> postParams ,String consumerKey, String consumerSecret, String token, String secretToken){
        this.consumerKey = consumerKey;
        this.consumerSecret = consumerSecret;
        this.token = token;
        this.secretToken = secretToken;
        this.request = request;

        this.consumer = new CommonsHttpOAuthConsumer(this.consumerKey, this.consumerSecret);
        this.consumer.setTokenWithSecret(this.token, this.secretToken);

        this.postParams = postParams;
    }

    public void setPostParams(List<NameValuePair> postParams)
    {
        this.postParams = postParams;
    }

    public List<NameValuePair> getPostParams()
    {
        return this.postParams;
    }


    public String POST(String url, List<NameValuePair> params) throws IOException, OAuthCommunicationException, OAuthExpectationFailedException, OAuthMessageSignerException {

        String contentType = "application/json";

        DefaultHttpClient httpclient = new DefaultHttpClient();

        HttpPost postRequest = new HttpPost(url);

        postRequest.setEntity(new UrlEncodedFormEntity(params));

        this.consumer.sign(postRequest);

        try {
            // Execute HTTP Post Request
            HttpResponse response = httpclient.execute(postRequest);

            InputStream data = response.getEntity().getContent();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(data));
            String responseLine;
            StringBuilder responseBuilder = new StringBuilder();

            while ((responseLine = bufferedReader.readLine()) != null) {
                responseBuilder.append(responseLine);
            }

            return responseBuilder.toString();

        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
        } catch (IOException e) {
            // TODO Auto-generated catch block
        }

        return null;
    }

    public String GET(String url, List<NameValuePair> params) throws OAuthCommunicationException, OAuthExpectationFailedException, OAuthMessageSignerException, IOException {
        DefaultHttpClient httpclient = new DefaultHttpClient();
        HttpGet request;

        if(params != null){
            String paramsString = URLEncodedUtils.format(params, "UTF-8");

            request = new HttpGet(url + "?" + paramsString);
        }else{
            request = new HttpGet(url);
        }

        consumer.sign(request);

        HttpResponse response = httpclient.execute(request);

        InputStream data = response.getEntity().getContent();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(data));
        String responseLine;
        StringBuilder responseBuilder = new StringBuilder();

        while ((responseLine = bufferedReader.readLine()) != null) {
            responseBuilder.append(responseLine);

        }

        return responseBuilder.toString();
    }

    @Override
    protected Object doInBackground(Object[] params) {

            if(this.request.equals(GET_USER_TWEETS)){
                return getUserTweets();
            }else if(this.request.equals(GET_TIMELINE_TWEETS)){
                return getHomeTimelineTweets();
            }else if(this.request.equals(GET_FRIENDS_LIST)){
                return getFriendsList();
            }else if(this.request.equals(GET_FOLLOWERS_LIST)){
                return getFollowersList();
            }else if(this.request.equals(POST_NEW_TWEET)){
                if(postParams == null){
                    Log.e("POST PARAMETERS", "Post parameters required not found.");
                    return null;
                }
                return postNewTweet();
            }else if(this.request.equals(POST_RETWEET)){
                if(postParams == null){
                    Log.e("POST PARAMETERS", "Post parameters required not found.");
                    return null;
                }
                return postRetweet();
            }

        return null;
    }


    private long getIdFromParams(List<NameValuePair> paramsList){
        for(NameValuePair param : paramsList){
            if(param.getName().equals("id")){
                return Long.valueOf(param.getValue());
            }
        }
        return -1;
    }

    public String postRetweet(){
        long tweet_id = this.getIdFromParams(this.postParams);

        String postResponse = null;

        try {
            postResponse = this.POST(post_retweet + tweet_id + ".json", this.postParams);
            return postResponse;

        } catch (IOException e) {
            e.printStackTrace();
        } catch (OAuthCommunicationException e) {
            e.printStackTrace();
        } catch (OAuthExpectationFailedException e) {
            e.printStackTrace();
        } catch (OAuthMessageSignerException e) {
            e.printStackTrace();
        }
        return postResponse;
    }
    /** Post new tweet **/
    public String postNewTweet(){
        String postResponse = null;
        try {
            postResponse = this.POST(post_new_tweet, this.postParams);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (OAuthCommunicationException e) {
            e.printStackTrace();
        } catch (OAuthExpectationFailedException e) {
            e.printStackTrace();
        } catch (OAuthMessageSignerException e) {
            e.printStackTrace();
        }
        return postResponse;
    }

    /** get user object from JSON **/
    private String getUserFromTweetsJSON(JSONObject tweetsJsonObject) throws JSONException {
        JSONObject userObject = tweetsJsonObject.getJSONObject("user");
        return userObject.getString("screen_name");

    }

    /** get users that are following the user **/
    public ArrayList<User> getFollowersList()
    {
        ArrayList<User> followersList = new ArrayList<User>();

        String resultString = null;
        try {

            long cursor = -1;
            // while not in last page
            while(cursor != 0 || cursor == -1) {
                List<NameValuePair> params = null;
                if(cursor > 0) {
                    params = new ArrayList<NameValuePair>();
                    params.add(new BasicNameValuePair("cursor", Long.toString(cursor)));
                }

                resultString = this.GET(followers_list, params);

                JSONObject resultObject = new JSONObject(resultString);

                cursor = resultObject.getLong("next_cursor");

                JSONArray friendsListArray = resultObject.getJSONArray("users");


                for (int i = 0; i < friendsListArray.length(); i++) {
                    JSONObject userObject = friendsListArray.getJSONObject(i);

                    User user = new User(userObject.getLong("id"), userObject.getString("name"), userObject.getString("screen_name"));
                    user.setProfileImage(userObject.getString("profile_image_url"));

                    followersList.add(user);
                }
            }
        } catch (OAuthCommunicationException e) {
            e.printStackTrace();
        } catch (OAuthExpectationFailedException e) {
            e.printStackTrace();
        } catch (OAuthMessageSignerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return followersList;
    }

    /** get list of users the app user is following **/
    public ArrayList<User> getFriendsList()
    {
        ArrayList<User> friendsList = new ArrayList<User>();

        String resultString = null;
        try {

            long cursor = -1;
            // while not in last page
            while(cursor != 0 || cursor == -1) {
                List<NameValuePair> params = null;
                if(cursor > 0) {
                    params = new ArrayList<NameValuePair>();
                    params.add(new BasicNameValuePair("cursor", Long.toString(cursor)));
                }

                resultString = this.GET(friends_list, params);

                JSONObject resultObject = new JSONObject(resultString);

                cursor = resultObject.getLong("next_cursor");

                JSONArray friendsListArray = resultObject.getJSONArray("users");


                for (int i = 0; i < friendsListArray.length(); i++) {
                    JSONObject userObject = friendsListArray.getJSONObject(i);

                    User user = new User(userObject.getLong("id"), userObject.getString("name"), userObject.getString("screen_name"));
                    user.setProfileImage(userObject.getString("profile_image_url"));
                    Log.d("Asd", "profile image");

                    friendsList.add(user);
                }
            }
        } catch (OAuthCommunicationException e) {
            e.printStackTrace();
        } catch (OAuthExpectationFailedException e) {
            e.printStackTrace();
        } catch (OAuthMessageSignerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return friendsList;
    }

    /** get tweets posted by the user **/
    public ArrayList<Tweet> getUserTweets()
    {

        ArrayList<Tweet> tweets = new ArrayList<>();
        try {

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("count", Integer.toString(TwitterApiRequest.REQUEST_TWEETS_NUMBER)));

            String resultString = this.GET(user_tweets, params);

            JSONArray tweetsArray = new JSONArray(resultString);

            for (int i = 0; i < tweetsArray.length(); i++) {
                JSONObject tweetsObject = tweetsArray.getJSONObject(i);

                String username = getUserFromTweetsJSON(tweetsObject);

                Tweet tweet = new Tweet(username, tweetsObject.getLong("id"), tweetsObject.getString("created_at"), tweetsObject.getString("text"));
                tweets.add(tweet);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (OAuthExpectationFailedException e) {
            e.printStackTrace();
        } catch (OAuthCommunicationException e) {
            e.printStackTrace();
        } catch (OAuthMessageSignerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return tweets;
    }

    /** get user home timeline **/
    public ArrayList<Tweet> getHomeTimelineTweets()
    {

        ArrayList<Tweet> tweets = new ArrayList<>();
        try {

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("count", Integer.toString(TwitterApiRequest.REQUEST_TWEETS_NUMBER)));

            String resultString = this.GET(home_timeline, params);

            JSONArray tweetsArray = new JSONArray(resultString);

            for(int i = 0; i < tweetsArray.length(); i++){
                JSONObject tweetsObject = tweetsArray.getJSONObject(i);

                String userScreenName = getUserFromTweetsJSON(tweetsObject);

                Tweet tweet = new Tweet(userScreenName, tweetsObject.getLong("id"), tweetsObject.getString("created_at"), tweetsObject.getString("text"));
                tweets.add(tweet);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (OAuthExpectationFailedException e) {
            e.printStackTrace();
        } catch (OAuthCommunicationException e) {
            e.printStackTrace();
        } catch (OAuthMessageSignerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return tweets;
    }
}
