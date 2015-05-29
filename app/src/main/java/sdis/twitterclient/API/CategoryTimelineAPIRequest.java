package sdis.twitterclient.API;


import android.app.Activity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
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
    RecyclerView timelineView;

    public CategoryTimelineAPIRequest(Category category, User user, RecyclerView timelineView, CategoryTimelineAdapter adapter, SwipeRefreshLayout refreshLayout, Activity activity){
        this.user=user;
        this.adapter = adapter;
        this.activity = activity;
        this.refreshLayout = refreshLayout;
        this.category = category;
        this.timelineView = timelineView;
    }


    @Override
    public void run() {
        ArrayList<String> requests = new ArrayList<>();
        requests.add(TwitterApiRequest.GET_TIMELINE_TWEETS);

        TwitterApiRequest apiRequest = new TwitterApiRequest(requests, LoginActivity.TWITTER_CONSUMER_KEY, LoginActivity.TWITTER_CONSUMER_SECRET, this.user.getAccessToken().getToken(), this.user.getAccessToken().getTokenSecret());

        try {
            HashMap<String, Object> apiResult = (HashMap<String, Object>) apiRequest.execute().get();

            ArrayList<Tweet> timelineAPI = (ArrayList<Tweet>) apiResult.get(TwitterApiRequest.GET_TIMELINE_TWEETS);


            for(Tweet tweet : timelineAPI){
                if(user.isTweetAlreadyLoaded(tweet)){
                    tweet.setRead(true);
                }else{
                    tweet.setRead(false);
                }
            }

            user.homeTimeLineTweets = user.invertTweetList(timelineAPI);



            /** Add tweets to database **/
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

            user.homeTimeLineTweets = user.invertTweetList(user.databaseHandler.getAllTimelineTweets());

            /** Load user images **/
            for(Tweet tweet : user.homeTimeLineTweets){
                User user = this.user.getFriendByUsername(tweet.getPublisherUsername());
                if(user == null){
                    if(tweet.getPublisherUsername().equals(this.user.getScreen_name())){
                        user = this.user;
                    }
                }


                tweet.setPublisher(user);
            }


            final ArrayList<Tweet> categoryTweets = new ArrayList<>();
            int numUnread = 0;
            for(Tweet tweet : this.user.getHomeTimeLineTweets()){
                if(this.category.existsUser(tweet.getPublisher())){
                    categoryTweets.add(tweet);
                    if(tweet.getRead() == false){
                        numUnread++;
                    }
                }
            }

            final int unreadFinal = numUnread;



            activity.runOnUiThread(new Thread(){
                public void run(){
                    refreshLayout.setRefreshing(false);
                    adapter.changeList(user.homeTimeLineTweets);
                    adapter.notifyDataSetChanged();
                    timelineView.scrollToPosition(unreadFinal + 2);
                }
            });

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
