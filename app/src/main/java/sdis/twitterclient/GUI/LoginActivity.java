package sdis.twitterclient.GUI;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.net.URLEncoder;
import java.util.HashMap;

import sdis.twitterclient.API.TwitterApiRequest;
import sdis.twitterclient.R;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;


public class LoginActivity extends ActionBarActivity {

    public static String TWITTER_CONSUMER_KEY = "38SSotcpNaprOstWA6jctfmG5"; // place your consumer key here
    public static String TWITTER_CONSUMER_SECRET = "ytFGceSKDQnc2HE61EYLRuotLeym09WKXzg0h3FE4OnOWahvyk"; // place your consumer secret here

    // Preference Constants
    static String PREFERENCE_NAME = "twitter_oauth";
    static final String PREF_KEY_OAUTH_TOKEN = "oauth_token";
    static final String PREF_KEY_OAUTH_SECRET = "oauth_token_secret";
    static final String TWITTER_USER_ID = "twitter_user_id";
    static final String PREF_KEY_TWITTER_LOGIN = "isTwitterLoggedIn";

    static final String TWITTER_CALLBACK_URL = "oauth://sdis-twitter-client";

    // Twitter oauth urls
    static final String URL_TWITTER_AUTH = "auth_url";
    static final String URL_TWITTER_OAUTH_VERIFIER = "oauth_verifier";
    static final String URL_TWITTER_OAUTH_TOKEN = "oauth_token";

    // Twitter
    public static Twitter twitter;
    public static RequestToken requestToken;
    public static AccessToken accessToken;
    public static Configuration configuration;
    public static String accessTokenString;
    public static String accessTokenSecretString;
    public static long twitterUserId;
    public static HashMap<String, String> token;

    // Shared Preferences
    private static SharedPreferences mSharedPreferences;

    public static String TWITTER_OBJECT = "TWITTER_OBJECT";
    public static String ACCESS_TOKEN_OBJECT = "ACCESS_TOKEN_OBJECT";
    public static String REQUEST_TOKEN_OBJECT = "REQUEST_TOKEN_OBJECT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSharedPreferences = this.getSharedPreferences(
                "sdis.twitterclient", Context.MODE_PRIVATE);

        if(mSharedPreferences.getBoolean(PREF_KEY_TWITTER_LOGIN, false))
        {
            accessTokenString = mSharedPreferences.getString(PREF_KEY_OAUTH_TOKEN, "");
            accessTokenSecretString = mSharedPreferences.getString(PREF_KEY_OAUTH_SECRET, "");
            twitterUserId = mSharedPreferences.getLong(TWITTER_USER_ID, 0);
            accessToken = new AccessToken(accessTokenString, accessTokenSecretString, twitterUserId);

            Intent intent = new Intent(LoginActivity.this, ClientActivity.class);
            startActivity(intent);
        }

        Button testButton = (Button) findViewById(R.id.testButton);
        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Thread th = new Thread() {
                    public void run()
                    {
                        Long tsLong = System.currentTimeMillis()/1000;
                        String timestamp = tsLong.toString();

                        String[][] data = {
                                {"oauth_callback", "oauth://sdis-twitter-client"},
                                {"oauth_consumer_key", "38SSotcpNaprOstWA6jctfmG5"},
                                {"oauth_nonce",  TwitterApiRequest.generateNonce()},
                                {"oauth_signature", ""},
                                {"oauth_signature_method", "HMAC-SHA1"},
                                {"oauth_timestamp", String.valueOf(timestamp)},
                                {"oauth_version", "1.0"}
                        };

                        token = TwitterApiRequest.testRead("https://api.twitter.com/oauth/request_token", data);
                        Log.d("Confirmed: ", token.get("oauth_callback_confirmed"));
                        Log.d("Token: ", token.get("oauth_token"));
                        Log.d("Secret: ", token.get("oauth_token_secret"));

                        LoginActivity.this.startActivity(new Intent(Intent.ACTION_VIEW, Uri
                                .parse("https://api.twitter.com/oauth/authenticate?oauth_token=" + token.get("oauth_token"))));
                    }
                };
                th.start();
            }
        });

        Button signInButton = (Button) findViewById(R.id.signInButton);
        signInButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                loginToTwitter();
            }
        });

        if (!isTwitterLoggedInAlready()) {
            Uri uri = getIntent().getData();
            if (uri != null && uri.toString().startsWith(TWITTER_CALLBACK_URL)) {
                new AfterLoginTask().execute(uri.toString());
            }
        }

    }

    private boolean isTwitterLoggedInAlready() {
        return false;
    }

    private void loginToTwitter() {
        if (!isTwitterLoggedInAlready()) {
            ConfigurationBuilder builder = new ConfigurationBuilder();
            builder.setOAuthConsumerKey(TWITTER_CONSUMER_KEY);
            builder.setOAuthConsumerSecret(TWITTER_CONSUMER_SECRET);
            configuration = builder.build();


            TwitterFactory factory = new TwitterFactory(configuration);
            twitter = factory.getInstance();


            Thread thread = new Thread(new Runnable(){
                @Override
                public void run() {
                    try {

                        requestToken = twitter
                                .getOAuthRequestToken(TWITTER_CALLBACK_URL);
                        LoginActivity.this.startActivity(new Intent(Intent.ACTION_VIEW, Uri
                                .parse(requestToken.getAuthenticationURL())));

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            thread.start();
        } else {
            // user already logged into twitter
            Toast.makeText(getApplicationContext(),
                    "Already Logged into twitter", Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    class AfterLoginTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Boolean doInBackground(String... params) {
            handleTwitterCallback(params[0]);
            return true;
        }

        private void handleTwitterCallback(String url) {
                Log.d("Handling twitter", "sim");
                Uri uri = Uri.parse(url);

                // oAuth verifier
                final String verifier = uri
                        .getQueryParameter(URL_TWITTER_OAUTH_VERIFIER);

                try {

                    Long tsLong = System.currentTimeMillis()/1000;
                    String timestamp = tsLong.toString();

                    String[][] data = {
                            {"oauth_consumer_key", "38SSotcpNaprOstWA6jctfmG5"},
                            {"oauth_nonce",  TwitterApiRequest.generateNonce()},
                            {"oauth_signature", ""},
                            {"oauth_signature_method", "HMAC-SHA1"},
                            {"oauth_timestamp", String.valueOf(timestamp)},
                            {"oauth_token", token.get("oauth_token")},
                            {"oauth_version", "1.0"}
                    };

                    token = TwitterApiRequest.testRead("https://api.twitter.com/oauth/access_token?oauth_verifier=" + verifier, data);
                    // Get the access token

                    Log.d("Token: ", token.get("oauth_token"));
                    Log.d("Secret: ", token.get("oauth_token_secret"));

                    long userId = new TwitterApiRequest(TWITTER_CONSUMER_KEY, TWITTER_CONSUMER_SECRET, token.get("oauth_token"), token.get("oauth_token_secret")).getUserId();

                    Log.d("userId: ", String.valueOf(userId));
                    // Shared Preferences
                    SharedPreferences.Editor e = mSharedPreferences.edit();

                    // After getting access token, access token secret
                    // store them in application preferences
                    e.putString(PREF_KEY_OAUTH_TOKEN, token.get("oauth_token"));
                    e.putString(PREF_KEY_OAUTH_SECRET, token.get("oauth_token_secret"));
                    e.putLong(TWITTER_USER_ID, userId);
                    // Store login status - true
                    e.putBoolean(PREF_KEY_TWITTER_LOGIN, true);
                    e.commit(); // save changes

                    ConfigurationBuilder builder = new ConfigurationBuilder();
                    builder.setOAuthConsumerKey(TWITTER_CONSUMER_KEY);
                    builder.setOAuthConsumerSecret(TWITTER_CONSUMER_SECRET);

                    // Access Token
                    String access_token = mSharedPreferences.getString(
                            PREF_KEY_OAUTH_TOKEN, "");
                    // Access Token Secret
                    String access_token_secret = mSharedPreferences.getString(
                            PREF_KEY_OAUTH_SECRET, "");


                    accessToken = new AccessToken(token.get("oauth_token"), token.get("oauth_token_secret"), userId);


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        @Override
        protected void onPostExecute(Boolean result) {
            // TODO Auto-generated method stub
            Toast.makeText(LoginActivity.this, "Login Successful",
                    Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(LoginActivity.this, ClientActivity.class);
            intent.putExtra(TWITTER_OBJECT, twitter);
            intent.putExtra(ACCESS_TOKEN_OBJECT, accessToken);
            startActivity(intent);

        }

    }
}
