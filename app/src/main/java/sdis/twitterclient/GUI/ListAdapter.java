package sdis.twitterclient.GUI;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

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

        Tweet tweet = tweetArrayList.get(position);

        image.setImageBitmap(tweet.getPublisher().getProfileBitmapImage());
        fromView.setText(tweet.getPublisher().getName());
        descView.setText(tweet.getText());
        timeView.setText(tweet.getCreated_at());

        return v;
    }
}

