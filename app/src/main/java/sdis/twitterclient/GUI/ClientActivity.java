package sdis.twitterclient.GUI;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import sdis.twitterclient.Models.User;
import sdis.twitterclient.R;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

public class ClientActivity extends ActionBarActivity {

//First We Declare Titles And Icons For Our Navigation Drawer List View
    //This Icons And Titles Are holded in an Array as you can see

    String TITLES[] = {"Home","Search user","Add Category", "Categories", "Logout"};
    int ICONS[] = {R.drawable.home,R.drawable.search, R.drawable.plus,R.drawable.categories,R.drawable.logout};

    //Similarly we Create a String Resource for the name and email in the header view
    //And we also create a int resource for profile picture in the header view


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
    RecyclerView.LayoutManager timelineLayoutManager;
    RecyclerView.LayoutManager navLayoutManager;

    public boolean initialized = false;


    private void initUser(){
        Thread th = new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                    twitter4j.User userTask = twitter.showUser(accessToken.getUserId());
                    user.setName(userTask.getName());
                    user.setScreen_name(userTask.getScreenName());
                    user.setId(accessToken.getUserId());
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
        onItemsLoadComplete();
    }

    void onItemsLoadComplete() {
        user.loadTimeline(timelineView, timelineAdapter, refreshLayout, this);

    }

    private void setRefreshLayoutListener(){

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshItems();
            }
        });

    }

    public void showToast(String text, int duration){
        Toast toast = Toast.makeText(this, text, duration);
        toast.show();
    }

    private void createTweetAlertDialog(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);

        // set title
        alertDialogBuilder.setTitle("Your Title");

        // Set up the input
        final EditText tweetText = new EditText(this);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        tweetText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        alertDialogBuilder.setView(tweetText);


        // set dialog message
        alertDialogBuilder
                .setTitle("Post new tweet")
                .setCancelable(false)
                .setPositiveButton("Post",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // if this button is clicked, close
                        // current activity
                        user.postTweet(tweetText.getText().toString());
                        showToast("Tweet posted", 1000);
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("Client", "oncreate");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_client);

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        toolbar.setTitle("Twitter Client");
        ImageButton postTweet = (ImageButton) toolbar.findViewById(R.id.postTweet);

        postTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createTweetAlertDialog();
            }
        });
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

        this.user = new User(this, this.accessToken.getUserId(), this.accessToken);
        initUser();

        this.user.initFromDatabase();


        timelineView = (RecyclerView) findViewById(R.id.timelineView);

        timelineView.setHasFixedSize(true);                            // Letting the system know that the list objects are of fixed size

        this.timelineAdapter = new TimelineAdapter(this, user.getHomeTimeLineTweets(), user);       // Creating the Adapter of MyAdapter class(which we are going to see in a bit)
        // And passing the titles,icons,header view name, header view email,
        // and header view profile picture

        timelineView.setAdapter(timelineAdapter);                              // Setting the adapter to RecyclerView

        timelineLayoutManager = new LinearLayoutManager(this);                 // Creating a layout Manager

        timelineView.setLayoutManager(timelineLayoutManager);

        navBarView = (RecyclerView) findViewById(R.id.navbarView); // Assigning the RecyclerView Object to the xml View

        navBarView.setHasFixedSize(true);                            // Letting the system know that the list objects are of fixed size

        navbarAdapter = new NavbarAdapter(this, user, TITLES ,ICONS, user.getName(),"@"+user.getScreen_name());       // Creating the Adapter of MyAdapter class(which we are going to see in a bit)
        // And passing the titles,icons,header view name, header view email,
        // and header view profile picture

        navBarView.setAdapter(navbarAdapter);                              // Setting the adapter to RecyclerView

        navLayoutManager = new LinearLayoutManager(this);                 // Creating a layout Manager

        navBarView.setLayoutManager(navLayoutManager);                 // Setting the layout Manager

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

        initialized = true;
        timelineView.scrollToPosition(user.unreadTweets());

        timelineView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                int unread = user.unreadTweets();
                int first = ((LinearLayoutManager) timelineLayoutManager).findFirstCompletelyVisibleItemPosition();

                if(first < unread)
                {
                    user.homeTimeLineTweets.get(first).setRead(true);
                    user.databaseHandler.updateTimelineTweet(user.homeTimeLineTweets.get(first));
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

            }
        });
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
