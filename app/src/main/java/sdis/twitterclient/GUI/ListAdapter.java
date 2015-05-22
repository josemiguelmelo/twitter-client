package sdis.twitterclient.GUI;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

import sdis.twitterclient.API.TwitterApiRequest;
import sdis.twitterclient.Models.Tweet;
import sdis.twitterclient.R;

public class ListAdapter extends BaseAdapter {

    private ArrayList<Tweet> tweetArrayList;
    Context context;

    ListAdapter (ArrayList<Tweet> tweetArrayList, Context context){
        this.tweetArrayList = tweetArrayList;
        this.context = context;
    }

    public int getCount() {
        // TODO Auto-generated method stub
        return tweetArrayList.size();
    }

    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return tweetArrayList.get(position);
    }

    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        View v = convertView;
        if (v == null)
        {
            LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.list_item_tweet, null);
        }

        ImageView image = (ImageView) v.findViewById(R.id.icon);
        TextView fromView = (TextView)v.findViewById(R.id.From);
        TextView descView = (TextView)v.findViewById(R.id.description);
        TextView timeView = (TextView)v.findViewById(R.id.time);
        Button retweetButton = (Button) v.findViewById(R.id.retweet);

        final Tweet tweet = tweetArrayList.get(position);

        image.setImageBitmap(tweet.getPublisher().getProfileBitmapImage());
        fromView.setText(tweet.getPublisher().getName());
        descView.setText(tweet.getText());
        timeView.setText(tweet.getCreated_at());

        retweetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("here", "clicker");
                List<NameValuePair> postParams = new ArrayList<NameValuePair>();
                postParams.add(new BasicNameValuePair("id", Long.toString(tweet.getId())));

                ArrayList<String> requests= new ArrayList<>();
                requests.add(TwitterApiRequest.POST_RETWEET);

                new TwitterApiRequest(requests, postParams, LoginActivity.TWITTER_CONSUMER_KEY, LoginActivity.TWITTER_CONSUMER_SECRET, LoginActivity.accessToken.getToken(), LoginActivity.accessToken.getTokenSecret()).execute();

            }
        });

        return v;
    }
}

