package sdis.twitterclient.API;

import android.util.Base64;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Formatter;
import java.util.HashMap;
import java.util.List;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import sdis.twitterclient.Models.Tweet;
import sdis.twitterclient.Models.User;

import android.os.AsyncTask;
import android.util.Pair;

import org.apache.http.Header;
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

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;


public class TwitterApiRequest extends AsyncTask {
    public static final String POST = "POST";
    public static final String GET = "GET";
    public static final String home_timeline = "https://api.twitter.com/1.1/statuses/home_timeline.json";
    public static final String user_tweets = "https://api.twitter.com/1.1/statuses/user_timeline.json";
    public static final String friends_list = "https://api.twitter.com/1.1/friends/list.json";
    public static final String followers_list = "https://api.twitter.com/1.1/followers/list.json";

    public static final String post_new_tweet = "https://api.twitter.com/1.1/statuses/update.json";
    public static final String post_retweet = "https://api.twitter.com/1.1/statuses/retweet/";
    public static final String verify_credentials = "https://api.twitter.com/1.1/account/verify_credentials.json";


    public static final String GET_USER_TWEETS = "GET_USER_TWEETS";
    public static final String GET_TIMELINE_TWEETS = "GET_TIMELINE_TWEETS";
    public static final String GET_FRIENDS_LIST = "GET_FRIENDS_LIST";
    public static final String GET_FOLLOWERS_LIST = "GET_FOLLOWERS_LIST";

    public static final String POST_NEW_TWEET = "POST_NEW_TWEET";
    public static final String POST_RETWEET = "POST_RETWEET";

    private static final int REQUEST_TWEETS_NUMBER = 200;

    private ArrayList<String> requests;

    private OAuthConsumer consumer;

    private String consumerKey, consumerSecret;
    private String token, secretToken;

    private List<NameValuePair> postParams;

    public TwitterApiRequest(ArrayList<String> requests, String consumerKey, String consumerSecret, String token, String secretToken) {
        this.consumerKey = consumerKey;
        this.consumerSecret = consumerSecret;
        this.token = token;
        this.secretToken = secretToken;
        this.requests = requests;

        this.consumer = new CommonsHttpOAuthConsumer(this.consumerKey, this.consumerSecret);
        this.consumer.setTokenWithSecret(this.token, this.secretToken);

        this.postParams = null;
    }

    public TwitterApiRequest(String consumerKey, String consumerSecret, String token, String secret) {
        this.consumerKey = consumerKey;
        this.consumerSecret = consumerSecret;
        this.token = token;
        this.secretToken = secret;

        this.consumer = new CommonsHttpOAuthConsumer(this.consumerKey, this.consumerSecret);
        this.consumer.setTokenWithSecret(this.token, this.secretToken);
    }


    public TwitterApiRequest(ArrayList<String> requests, List<NameValuePair> postParams, String consumerKey, String consumerSecret, String token, String secretToken) {
        this.consumerKey = consumerKey;
        this.consumerSecret = consumerSecret;
        this.token = token;
        this.secretToken = secretToken;
        this.requests = requests;

        this.consumer = new CommonsHttpOAuthConsumer(this.consumerKey, this.consumerSecret);
        this.consumer.setTokenWithSecret(this.token, this.secretToken);

        this.postParams = postParams;
    }


    public void setPostParams(List<NameValuePair> postParams) {
        this.postParams = postParams;
    }

    public List<NameValuePair> getPostParams() {
        return this.postParams;
    }


    public String POST(String url, List<NameValuePair> params) throws IOException {

        String contentType = "application/json";

        DefaultHttpClient httpclient = new DefaultHttpClient();

        HttpPost postRequest = new HttpPost(url);

        postRequest.setEntity(new UrlEncodedFormEntity(params));

        Long tsLong = System.currentTimeMillis() / 1000;
        String timestamp = tsLong.toString();

        String[][] authData = {
                {"oauth_consumer_key", "38SSotcpNaprOstWA6jctfmG5"},
                {"oauth_nonce", TwitterApiRequest.generateNonce()},
                {"oauth_signature", ""},
                {"oauth_signature_method", "HMAC-SHA1"},
                {"oauth_timestamp", String.valueOf(timestamp)},
                {"oauth_token", this.token},
                {"oauth_version", "1.0"}
        };

        ArrayList<Pair<String, String>> authDataList = new ArrayList<Pair<String, String>>();

        for(int i = 0; i<authData.length; i++)
        {
            authDataList.add(new Pair<String, String>(authData[i][0], authData[i][1]));
        }

        for(int i = 0; i<postParams.size(); i++)
        {
            Log.d("params", postParams.get(i).getName());
            authDataList.add(new Pair<String, String>(oauth.signpost.OAuth.percentEncode(postParams.get(i).getName()), oauth.signpost.OAuth.percentEncode((postParams.get(i).getValue()))));
        }

        Collections.sort(authDataList, new Comparator<Pair<String, String>>() {
            @Override
            public int compare(Pair<String, String> elem1, Pair<String, String> elem2) {

                return elem1.first.compareTo(elem2.first);
            }
        });

        String[][] authDataWithParams = new String[7+postParams.size()][2];

        for(int i = 0; i<authDataList.size(); i++)
        {
            authDataWithParams[i][0] = authDataList.get(i).first;
            authDataWithParams[i][1] = authDataList.get(i).second;
        }

        postRequest.setHeader("Content-Type", "application/x-www-form-urlencoded");
        postRequest.setHeader("Authorization", generateSignature("POST", url, authDataWithParams, this.secretToken));

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

    public String GET(String url, List<NameValuePair> params) throws IOException {
        DefaultHttpClient httpclient = new DefaultHttpClient();
        HttpGet request;

        if (params != null) {
            String paramsString = URLEncodedUtils.format(params, "UTF-8");

            request = new HttpGet(url + "?" + paramsString);
        } else {
            request = new HttpGet(url);
        }

        Long tsLong = System.currentTimeMillis() / 1000;
        String timestamp = tsLong.toString();

        String[][] authData = {
                {"oauth_consumer_key", "38SSotcpNaprOstWA6jctfmG5"},
                {"oauth_nonce", TwitterApiRequest.generateNonce()},
                {"oauth_signature", ""},
                {"oauth_signature_method", "HMAC-SHA1"},
                {"oauth_timestamp", String.valueOf(timestamp)},
                {"oauth_token", this.token},
                {"oauth_version", "1.0"}
        };

        ArrayList<Pair<String, String>> authDataList = new ArrayList<Pair<String, String>>();

        for(int i = 0; i<authData.length; i++)
        {
            authDataList.add(new Pair<String, String>(authData[i][0], authData[i][1]));
        }

        for(int i = 0; i<params.size(); i++)
        {
            Log.d("params", params.get(i).getName());
            authDataList.add(new Pair<String, String>(params.get(i).getName(), params.get(i).getValue()));
        }

        Collections.sort(authDataList, new Comparator<Pair<String, String>>() {
            @Override
            public int compare(Pair<String, String> elem1, Pair<String, String> elem2) {

                return elem1.first.compareTo(elem2.first);
            }
        });

        String[][] authDataWithParams = new String[7+params.size()][2];

        for(int i = 0; i<authDataList.size(); i++)
        {
            authDataWithParams[i][0] = authDataList.get(i).first;
            authDataWithParams[i][1] = authDataList.get(i).second;
        }

        request.setHeader("Content-Type", "application/x-www-form-urlencoded");
        request.setHeader("Authorization", generateSignature("GET", url, authDataWithParams, this.secretToken));

        Log.d("request URL", request.getURI().toString());
        for(Header h : request.getAllHeaders())
            Log.d("request headers", h.getName() + " " + h.getValue());

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
        HashMap<String, Object> requestResult = new HashMap<>();

        for (String request : this.requests) {
            if (request.equals(GET_USER_TWEETS)) {
                requestResult.put(GET_USER_TWEETS, getUserTweets());
            } else if (request.equals(GET_TIMELINE_TWEETS)) {
                requestResult.put(GET_TIMELINE_TWEETS, getHomeTimelineTweets());
            } else if (request.equals(GET_FRIENDS_LIST)) {
                requestResult.put(GET_FRIENDS_LIST, getFriendsList());
            } else if (request.equals(GET_FOLLOWERS_LIST)) {
                requestResult.put(GET_FOLLOWERS_LIST, getFollowersList());
            } else if (request.equals(POST_NEW_TWEET)) {
                if (postParams == null) {
                    Log.e("POST PARAMETERS", "Post parameters required not found.");

                } else {
                    requestResult.put(POST_NEW_TWEET, postNewTweet());
                }

            } else if (request.equals(POST_RETWEET)) {
                Log.d("Post ret", "ete");
                if (postParams == null) {
                    Log.e("POST PARAMETERS", "Post parameters required not found.");
                } else {
                    requestResult.put(POST_RETWEET, postRetweet());
                }
            }
        }

        if (requestResult.size() == 0)
            return null;
        return requestResult;
    }


    private long getIdFromParams(List<NameValuePair> paramsList) {
        for (NameValuePair param : paramsList) {
            if (param.getName().equals("id")) {
                return Long.valueOf(param.getValue());
            }
        }
        return -1;
    }

    public String postRetweet() {
        long tweet_id = this.getIdFromParams(this.postParams);

        Log.d("tweet_id", "" + tweet_id);
        String postResponse = null;

        try {
            postResponse = this.POST(post_retweet + tweet_id + ".json", this.postParams);
            return postResponse;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return postResponse;
    }

    /**
     * Post new tweet *
     */
    public String postNewTweet() {
        String postResponse = null;
        try {
            postResponse = this.POST(post_new_tweet, this.postParams);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return postResponse;
    }

    /**
     * get user object from JSON *
     */
    private String getUserFromTweetsJSON(JSONObject tweetsJsonObject) throws JSONException {
        JSONObject userObject = tweetsJsonObject.getJSONObject("user");
        return userObject.getString("screen_name");

    }

    /**
     * get users that are following the user *
     */
    public ArrayList<User> getFollowersList() {
        ArrayList<User> followersList = new ArrayList<User>();

        String resultString = null;
        try {

            long cursor = -1;
            // while not in last page
            while (cursor != 0 || cursor == -1) {
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                if (cursor > 0) {

                    params.add(new BasicNameValuePair("cursor", Long.toString(cursor)));
                }

                resultString = this.GET(followers_list, params);

                JSONObject resultObject = new JSONObject(resultString);

                cursor = resultObject.getLong("next_cursor");

                JSONArray friendsListArray = resultObject.getJSONArray("users");


                for (int i = 0; i < friendsListArray.length(); i++) {
                    JSONObject userObject = friendsListArray.getJSONObject(i);

                    User user = new User(null, userObject.getLong("id"), userObject.getString("name"), userObject.getString("screen_name"));
                    user.setProfileImage(userObject.getString("profile_image_url"));

                    followersList.add(user);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return followersList;
    }

    /**
     * get list of users the app user is following *
     */
    public ArrayList<User> getFriendsList() {
        ArrayList<User> friendsList = new ArrayList<User>();

        String resultString = null;

        try {

            long cursor = -1;
            // while not in last page
            while (cursor != 0 || cursor == -1) {
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                if (cursor > 0) {

                    params.add(new BasicNameValuePair("cursor", Long.toString(cursor)));
                }


                resultString = this.GET(friends_list, params);

                JSONObject resultObject = new JSONObject(resultString);

                cursor = resultObject.getLong("next_cursor");

                JSONArray friendsListArray = resultObject.getJSONArray("users");


                for (int i = 0; i < friendsListArray.length(); i++) {
                    JSONObject userObject = friendsListArray.getJSONObject(i);

                    User user = new User(null, userObject.getLong("id"), userObject.getString("name"), userObject.getString("screen_name"));
                    user.setProfileImage(userObject.getString("profile_image_url"));

                    friendsList.add(user);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return friendsList;
    }

    /**
     * get tweets posted by the user *
     */
    public ArrayList<Tweet> getUserTweets() {

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
        } catch (IOException e) {
            e.printStackTrace();
        }

        return tweets;
    }

    /**
     * get user home timeline *
     */
    public ArrayList<Tweet> getHomeTimelineTweets() {

        ArrayList<Tweet> tweets = new ArrayList<>();
        try {

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("count", Integer.toString(TwitterApiRequest.REQUEST_TWEETS_NUMBER)));

            String resultString = this.GET(home_timeline, params);

            JSONArray tweetsArray = new JSONArray(resultString);

            for (int i = 0; i < tweetsArray.length(); i++) {
                JSONObject tweetsObject = tweetsArray.getJSONObject(i);

                String userScreenName = getUserFromTweetsJSON(tweetsObject);

                Tweet tweet = new Tweet(userScreenName, tweetsObject.getLong("id"), tweetsObject.getString("created_at"), tweetsObject.getString("text"));
                tweets.add(tweet);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return tweets;
    }

    public static String generateNonce() {
        // Pick from some letters that won't be easily mistaken for each
        // other. So, for example, omit o O and 0, 1 l and L.
        String letters = "abcdefghjkmnpqrstuvwxyzABCDEFGHJKMNPQRSTUVWXYZ23456789";

        String pw = "";
        for (int i = 0; i < 32; i++) {
            int index = (int) (new SecureRandom().nextDouble() * letters.length());
            pw += letters.substring(index, index + 1);
        }
        return pw;
    }

    private static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";

    private static String toHexString(byte[] bytes) {
        Formatter formatter = new Formatter();

        for (byte b : bytes) {
            formatter.format("%02x", b);
        }

        return formatter.toString();
    }

    public static String calculateRFC2104HMAC(String data, String key)
            throws SignatureException, NoSuchAlgorithmException, InvalidKeyException {
        SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(), HMAC_SHA1_ALGORITHM);
        Mac mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
        mac.init(signingKey);
        return Base64.encodeToString(mac.doFinal(data.getBytes()), Base64.URL_SAFE);
    }

    public static String generateSignature(String method, String url, String[][] data, String secretToken) {
        int signatureIndex = 0;
        try {
            for (int i = 0; i < data.length; i++) {
                if (data[i][0].equals("oauth_signature")) {
                    signatureIndex = i;
                }
            }

            /**
             * Generation of the signature base string
             */

            String signature_base_string =
                    method + "&" + oauth.signpost.OAuth.percentEncode(url) + "&";
            for (int i = 0; i < data.length; i++) {
                // ignore the empty oauth_signature field
                if (i != signatureIndex) {
                    signature_base_string +=
                            oauth.signpost.OAuth.percentEncode(data[i][0]) + "%3D" +
                                    oauth.signpost.OAuth.percentEncode(data[i][1]) + "%26";
                }
            }
            // cut the last appended %26
            signature_base_string = signature_base_string.substring(0,
                    signature_base_string.length() - 3);

            Log.d("Sig base string", signature_base_string);
            Log.d("Secret token", secretToken);
            Log.d("Encode token", URLEncoder.encode(secretToken, "UTF-8"));

            /**
             * Sign the request
             */
            String sig = calculateRFC2104HMAC(signature_base_string, oauth.signpost.OAuth.percentEncode("ytFGceSKDQnc2HE61EYLRuotLeym09WKXzg0h3FE4OnOWahvyk") + "&" + oauth.signpost.OAuth.percentEncode(secretToken));

            sig = oauth.signpost.OAuth.percentEncode(sig.trim());
            data[signatureIndex][1] = sig;

            Log.d("Signature", sig);

            /**
             * Create the header for the request
             */
            String header = "OAuth ";
            for (String[] item : data) {
                header += item[0] + "=\"" + item[1] + "\", ";
            }
            // cut off last appended comma
            header = header.substring(0, header.length() - 2);
            Log.d("Header", header);
            return header;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";

    }

    public long getUserId() {
        String resultString = "";
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        try {
            resultString = this.GET(verify_credentials, params);
        } catch (IOException e) {
            e.printStackTrace();
        }

        JSONObject resultObject = new JSONObject();
        try {
            resultObject = new JSONObject(resultString);
            return resultObject.getLong("id");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return 0;

    }


    public static HashMap<String, String> testRead(String url, String[][] data) {
        StringBuffer buffer = new StringBuffer();
        try {

            String header = generateSignature("POST", url, data, "");
            /**
             * Listing of all parameters necessary to retrieve a token
             * (sorted lexicographically as demanded)
             */

            System.out.println("Header: " + header);

            String charset = "UTF-8";
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Accept-Charset", charset);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("Authorization", header);
            connection.setRequestProperty("User-Agent", "XXXX");

            OutputStream output = connection.getOutputStream();
            output.write(header.getBytes(charset));

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            String read;
            while ((read = reader.readLine()) != null) {
                buffer.append(read);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        String response = buffer.toString();

        String[] stringParams = response.split("&");
        HashMap<String, String> params = new HashMap<String, String>();

        for (String param : stringParams) {
            String[] separatedParams = param.split("=");
            params.put(separatedParams[0], separatedParams[1]);
        }

        return params;
    }
}
