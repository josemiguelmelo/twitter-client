package sdis.twitterclient.GUI;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import sdis.twitterclient.Models.Tweet;
import sdis.twitterclient.R;

public class TimelineAdapter  extends RecyclerView.Adapter<TimelineAdapter.ViewHolder> {

    ArrayList<Tweet> tweets;

    TimelineAdapter(ArrayList<Tweet> tweets){ // MyAdapter Constructor with titles and icons parameter
        // titles, icons, name, email, profile pic are passed from the main activity as we
       this.tweets = tweets;

    }

    public void changeList(ArrayList<Tweet> tweets){
        this.tweets = tweets;
    }

    public void appendItems(ArrayList<Tweet> tweetsToAppend){
        this.tweets.addAll(tweetsToAppend);
    }

    public void addItemToList(Tweet tweet){
        this.tweets.add(tweet);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView from;
        ImageView icon;
        TextView description;
        TextView time;
        Button retweet;


        public ViewHolder(View itemView,int ViewType) {                 // Creating ViewHolder Constructor with View and viewType As a parameter
            super(itemView);

            this.from = (TextView) itemView.findViewById(R.id.From);
            this.icon = (ImageView) itemView.findViewById(R.id.icon);

            this.description = (TextView) itemView.findViewById(R.id.description);

            this.time = (TextView) itemView.findViewById(R.id.time);

            this.retweet = (Button) itemView.findViewById(R.id.retweet);



        }


    }


    @Override
    public TimelineAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_tweet,viewGroup,false); //Inflating the layout

        ViewHolder vhItem = new ViewHolder(v,i); //Creating ViewHolder and passing the object of type view

        return vhItem; // Returning the created object
    }

    @Override
    public void onBindViewHolder(TimelineAdapter.ViewHolder viewHolder, int i) {
        viewHolder.from.setText(tweets.get(i).getPublisherUsername());
        viewHolder.description.setText(tweets.get(i).getText());
        viewHolder.time.setText(tweets.get(i).getCreated_at());
    }

    @Override
    public int getItemCount() {
        return tweets.size();
    }


}
