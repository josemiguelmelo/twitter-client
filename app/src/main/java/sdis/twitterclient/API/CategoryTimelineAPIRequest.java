package sdis.twitterclient.API;


import android.app.Activity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import sdis.twitterclient.API.TwitterApiRequest;
import sdis.twitterclient.GUI.CategoryTimelineAdapter;
import sdis.twitterclient.GUI.LoginActivity;
import sdis.twitterclient.GUI.TimelineAdapter;
import sdis.twitterclient.Models.Category;
import sdis.twitterclient.Models.Tweet;
import sdis.twitterclient.Models.User;
import sdis.twitterclient.Util.BoolReference;

public class CategoryTimelineAPIRequest extends Thread{
    User user;
    CategoryTimelineAdapter adapter;
    Activity activity;
    SwipeRefreshLayout refreshLayout;
    Category category;

    public CategoryTimelineAPIRequest(Category category, User user, CategoryTimelineAdapter adapter, SwipeRefreshLayout refreshLayout, Activity activity){
        this.user=user;
        this.adapter = adapter;
        this.activity = activity;
        this.refreshLayout = refreshLayout;
        this.category = category;
    }


    @Override
    public void run() {
        ArrayList<String> requests = new ArrayList<>();
        requests.add(TwitterApiRequest.GET_TIMELINE_TWEETS);

        TwitterApiRequest apiRequest = new TwitterApiRequest(requests, LoginActivity.TWITTER_CONSUMER_KEY, LoginActivity.TWITTER_CONSUMER_SECRET, this.user.getAccessToken().getToken(), this.user.getAccessToken().getTokenSecret());

        try {
            HashMap<String, Object> apiResult = (HashMap<String, Object>) apiRequest.execute().get();

            user.homeTimeLineTweets = (ArrayList<Tweet>) apiResult.get(TwitterApiRequest.GET_TIMELINE_TWEETS);

            user.homeTimeLineTweets = user.invertTweetList(user.homeTimeLineTweets);

            for(Tweet tweet : user.homeTimeLineTweets){
                User user = this.user.getFriendByUsername(tweet.getPublisherUsername());

                if(user == null){
                    if(tweet.getPublisherUsername().equals(this.user.getScreen_name())){
                        user = this.user;
                    }
                }

                tweet.setPublisher(user);

                this.user.databaseHandler.addTimelineTweet(tweet);
            }

            user.initFromDatabase();


            final ArrayList<Tweet> categoryTweets = new ArrayList<>();

            for(Tweet tweet : this.user.getHomeTimeLineTweets()){
                if(this.category.existsUser(tweet.getPublisher())){
                    categoryTweets.add(tweet);
                }
            }

            activity.runOnUiThread(new Thread(){
                public void run(){
                    refreshLayout.setRefreshing(false);
                    adapter.changeList(categoryTweets);
                    adapter.notifyDataSetChanged();
                }
            });

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

    }

}
