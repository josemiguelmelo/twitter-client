package sdis.twitterclient.GUI;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import sdis.twitterclient.Models.Tweet;
import sdis.twitterclient.Models.User;
import sdis.twitterclient.R;

public class TimelineAdapter  extends RecyclerView.Adapter<TimelineAdapter.ViewHolder> {

    public ArrayList<Tweet> tweets;
    public User user;

    TimelineAdapter(ArrayList<Tweet> tweets, User user){ // MyAdapter Constructor with titles and icons parameter
        // titles, icons, name, email, profile pic are passed from the main activity as we
        this.tweets = tweets;
        this.user = user;

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
        TextView date;
        ImageButton retweet;

        ImageButton reply;


        public ViewHolder(View itemView,int ViewType) {                 // Creating ViewHolder Constructor with View and viewType As a parameter
            super(itemView);

            this.from = (TextView) itemView.findViewById(R.id.From);
            this.icon = (ImageView) itemView.findViewById(R.id.icon);

            this.description = (TextView) itemView.findViewById(R.id.description);

            this.time = (TextView) itemView.findViewById(R.id.time);
            this.date = (TextView) itemView.findViewById(R.id.date);

            this.retweet = (ImageButton) itemView.findViewById(R.id.retweet);

            this.reply = (ImageButton) itemView.findViewById(R.id.reply);

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
        final Tweet tweet = tweets.get(i);
        viewHolder.from.setText(tweet.getPublisherUsername());
        viewHolder.description.setText(tweet.getText());
        String[] date = tweet.getCreated_at().split(" ");

        String dateString = date[2] + "/" + date[1] + "/" + date[5] ;
        String timeString = date[3];

        viewHolder.date.setText(dateString);
        viewHolder.time.setText(timeString);

        viewHolder.icon.setImageBitmap(tweet.getPublisher().getProfileBitmapImage());

        viewHolder.retweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user.postReTweet(Long.toString(tweet.getId()));
            }
        });


        viewHolder.reply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                            user.context);

                    // set title
                    alertDialogBuilder.setTitle("Your Title");

                    // Set up the input
                    final EditText tweetText = new EditText(user.context);

                    tweetText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                    tweetText.setText("@" + tweet.getPublisher().getScreen_name() + " ");
                    alertDialogBuilder.setView(tweetText);

                    // set dialog message
                    alertDialogBuilder
                            .setTitle("Reply to tweet")
                            .setCancelable(false)
                            .setPositiveButton("Reply",new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id) {
                                    // if this button is clicked, close
                                    // current activity
                                    user.postReplyTweet(tweetText.getText().toString(), tweet.getId());
                                }
                            })
                            .setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id) {
                                    // if this button is clicked, just close
                                    // the dialog box and do nothing
                                    dialog.cancel();
                                }
                            });

                    // create alert dialog
                    AlertDialog alertDialog = alertDialogBuilder.create();

                    // show it
                    alertDialog.show();
                }
        });
    }

    @Override
    public int getItemCount() {
        return tweets.size();
    }




}
