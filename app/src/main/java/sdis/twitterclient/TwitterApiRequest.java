package sdis.twitterclient;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import oauth.signpost.OAuth;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.basic.DefaultOAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.http.HttpParameters;
import twitter4j.Twitter;

import android.os.AsyncTask;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;


public class TwitterApiRequest extends AsyncTask{
    public static final String POST = "POST";
    public static final String GET = "GET";
    public static final String home_timeline = "https://api.twitter.com/1.1/statuses/home_timeline.json";
    public static final String post_tweet = "https://api.twitter.com/1.1/statuses/update.json";

    private String requestUrl;

    private OAuthConsumer consumer;

    private String consumerKey, consumerSecret;
    private String token, secretToken;

    public TwitterApiRequest(String requestUrl, String consumerKey, String consumerSecret, String token, String secretToken){
        this.requestUrl = requestUrl;
        this.consumerKey = consumerKey;
        this.consumerSecret = consumerSecret;
        this.token = token;
        this.secretToken = secretToken;

        this.consumer = new CommonsHttpOAuthConsumer(this.consumerKey, this.consumerSecret);
        this.consumer.setTokenWithSecret(this.token, this.secretToken);

    }


    public void POST(String url, List<NameValuePair> params) throws IOException, OAuthCommunicationException, OAuthExpectationFailedException, OAuthMessageSignerException {

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

            Log.d("post result", responseBuilder.toString());

        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
        } catch (IOException e) {
            // TODO Auto-generated catch block
        }
    }

    public String GET(String url) throws OAuthCommunicationException, OAuthExpectationFailedException, OAuthMessageSignerException, IOException {
        DefaultHttpClient httpclient = new DefaultHttpClient();
        HttpGet request = new HttpGet(url);

        consumer.sign(request);

        HttpResponse response = httpclient.execute(request);

        InputStream data = response.getEntity().getContent();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(data));
        String responseLine;
        StringBuilder responseBuilder = new StringBuilder();

        while ((responseLine = bufferedReader.readLine()) != null) {
            responseBuilder.append(responseLine);

        }


        Log.d("get result", responseBuilder.toString());
        return responseBuilder.toString();
    }

    @Override
    protected Object doInBackground(Object[] params) {
        try {
            // get timeline tweets example
            this.GET(TwitterApiRequest.home_timeline);

            //post tweet example
            List<NameValuePair> postParams = new ArrayList<NameValuePair>(1);
            postParams.add(new BasicNameValuePair("status", "new tweet"));

            this.POST(TwitterApiRequest.post_tweet,postParams);
            
        } catch (OAuthCommunicationException e) {
            e.printStackTrace();
        } catch (OAuthExpectationFailedException e) {
            e.printStackTrace();
        } catch (OAuthMessageSignerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
