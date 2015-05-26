package sdis.twitterclient.GUI;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

import sdis.twitterclient.Models.Category;
import sdis.twitterclient.Models.Tweet;
import sdis.twitterclient.Models.User;
import sdis.twitterclient.R;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

public class ClientActivity extends ActionBarActivity {

//First We Declare Titles And Icons For Our Navigation Drawer List View
    //This Icons And Titles Are holded in an Array as you can see

    ArrayList<String> TITLES = new ArrayList<>();
    int ICONS[] = {R.drawable.ic_drawer,R.drawable.ic_action_new,R.drawable.ic_action_cancel,R.drawable.ic_drawer,R.drawable.ic_drawer,R.drawable.ic_drawer };

    //Similarly we Create a String Resource for the name and email in the header view
    //And we also create a int resource for profile picture in the header view

    int PROFILE = R.drawable.ic_drawer;

    private Toolbar toolbar;                              // Declaring the Toolbar Object

    RecyclerView navBarView;                           // Declaring RecyclerView
    RecyclerView.Adapter navbarAdapter;                        // Declaring Adapter For Recycler View
    DrawerLayout Drawer;                                  // Declaring DrawerLayout

    ActionBarDrawerToggle mDrawerToggle;                  // Declaring Action Bar Drawer Toggle
    public SwipeRefreshLayout refreshLayout;

    private Twitter twitter;
    private AccessToken accessToken;

    private User user;

    public static SharedPreferences mSharedPreferences;


    RecyclerView timelineView;
    public TimelineAdapter timelineAdapter;                  // Declaring Adapter For Recycler View
    RecyclerView.LayoutManager mLayoutManager;



    private void initUser(){
        Thread th = new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                    twitter4j.User userTask = twitter.showUser(accessToken.getUserId());
                    user.setName(userTask.getName());
                    user.setScreen_name(userTask.getScreenName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        th.start();
        try {
            th.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    void refreshItems() {
        // Load items
        // ...

        // Load complete
        onItemsLoadComplete();
    }

    void onItemsLoadComplete() {

        user.loadTimeline();
        timelineAdapter.tweets = user.getHomeTimeLineTweets();
        timelineAdapter.notifyDataSetChanged();

        refreshLayout.setRefreshing(false);
    }

    private void setRefreshLayoutListener(){

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshItems();
            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_client);

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        toolbar.setTitle("Twitter Client");
        setSupportActionBar(toolbar);


        mSharedPreferences = this.getSharedPreferences(
                "sdis.twitterclient", Context.MODE_PRIVATE);

        this.accessToken = LoginActivity.accessToken;

        ConfigurationBuilder builder = new ConfigurationBuilder();
        builder.setOAuthConsumerKey(LoginActivity.TWITTER_CONSUMER_KEY);
        builder.setOAuthConsumerSecret(LoginActivity.TWITTER_CONSUMER_SECRET);
        Configuration configuration = builder.build();
        TwitterFactory factory = new TwitterFactory(configuration);
        this.twitter = factory.getInstance(this.accessToken);

        this.user = new User(this.getApplicationContext(), this.accessToken.getUserId(), this.accessToken);
        initUser();

        this.user.initFromDatabase();

        this.TITLES.add("Home");
        this.TITLES.add("Add Category");

        for(Category c : this.user.getCategories()){
            this.TITLES.add(c.getName());
        }

        this.TITLES.add("Logout");


        timelineView = (RecyclerView) findViewById(R.id.timelineView);

        timelineView.setHasFixedSize(true);                            // Letting the system know that the list objects are of fixed size

        this.timelineAdapter = new TimelineAdapter(user.getHomeTimeLineTweets());       // Creating the Adapter of MyAdapter class(which we are going to see in a bit)
        // And passing the titles,icons,header view name, header view email,
        // and header view profile picture

        timelineView.setAdapter(timelineAdapter);                              // Setting the adapter to RecyclerView

        mLayoutManager = new LinearLayoutManager(this);                 // Creating a layout Manager

        timelineView.setLayoutManager(mLayoutManager);


        navBarView = (RecyclerView) findViewById(R.id.navbarView); // Assigning the RecyclerView Object to the xml View

        navBarView.setHasFixedSize(true);                            // Letting the system know that the list objects are of fixed size


        String[] titlesArray = new String[TITLES.size()];
        titlesArray = TITLES.toArray(titlesArray);
        navbarAdapter = new NavbarAdapter(this, user, titlesArray ,ICONS, user.getName(),"@"+user.getScreen_name());       // Creating the Adapter of MyAdapter class(which we are going to see in a bit)
        // And passing the titles,icons,header view name, header view email,
        // and header view profile picture

        navBarView.setAdapter(navbarAdapter);                              // Setting the adapter to RecyclerView

        mLayoutManager = new LinearLayoutManager(this);                 // Creating a layout Manager

        navBarView.setLayoutManager(mLayoutManager);                 // Setting the layout Manager

        Drawer = (DrawerLayout) findViewById(R.id.DrawerLayout);        // Drawer object Assigned to the view

        mDrawerToggle = new ActionBarDrawerToggle(this,Drawer,toolbar,R.string.openDrawer,R.string.closeDrawer){

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                // code here will execute once the drawer is opened( As I dont want anything happened whe drawer is
                // open I am not going to put anything here)
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                // Code here will execute once drawer is closed
            }



        }; // Drawer Toggle Object Made
        Drawer.setDrawerListener(mDrawerToggle); // Drawer Listener set to the Drawer toggle
        mDrawerToggle.syncState();               // Finally we set the drawer toggle sync State



        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        setRefreshLayoutListener();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.


                return super.onOptionsItemSelected(item);


    }


}
