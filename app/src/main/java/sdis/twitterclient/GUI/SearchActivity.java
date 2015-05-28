package sdis.twitterclient.GUI;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
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
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

import sdis.twitterclient.Database.DatabaseHandler;
import sdis.twitterclient.Models.Category;
import sdis.twitterclient.Models.Tweet;
import sdis.twitterclient.Models.User;
import sdis.twitterclient.R;
import sdis.twitterclient.Util.BoolReference;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

public class SearchActivity extends ActionBarActivity {

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


    private Twitter twitter;
    private AccessToken accessToken;

    private User user;

    public static SharedPreferences mSharedPreferences;


    RecyclerView resultsView;
    public SearchResultsAdapter resultsAdapter;                  // Declaring Adapter For Recycler View
    RecyclerView.LayoutManager resultsLayoutManager;
    RecyclerView.LayoutManager navLayoutManager;

    EditText usernameEdittext;
    ImageButton searchButton;

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



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        toolbar.setTitle("Twitter Client");

        mSharedPreferences = this.getSharedPreferences(
                "sdis.twitterclient", Context.MODE_PRIVATE);

        this.accessToken = LoginActivity.accessToken;

        ConfigurationBuilder builder = new ConfigurationBuilder();
        builder.setOAuthConsumerKey(LoginActivity.TWITTER_CONSUMER_KEY);
        builder.setOAuthConsumerSecret(LoginActivity.TWITTER_CONSUMER_SECRET);
        Configuration configuration = builder.build();
        TwitterFactory factory = new TwitterFactory(configuration);
        this.twitter = factory.getInstance(this.accessToken);


        this.user = (User) getIntent().getSerializableExtra("user");
        this.user.databaseHandler = new DatabaseHandler(getApplication());
        this.user.setAccessToken(this.accessToken);
        initUser();

        usernameEdittext = (EditText) findViewById(R.id.searchUsername);
        searchButton = (ImageButton) findViewById(R.id.searchButton);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<User> usersFound = user.searchUser(usernameEdittext.getText().toString());
                resultsAdapter.changeList(usersFound);
                resultsAdapter.notifyDataSetChanged();
            }
        });


        setSupportActionBar(toolbar);




        resultsView = (RecyclerView) findViewById(R.id.timelineView);

        resultsView.setHasFixedSize(true);                            // Letting the system know that the list objects are of fixed size

        this.resultsAdapter = new SearchResultsAdapter(new ArrayList<User>(), user);


        resultsView.setAdapter(resultsAdapter);                              // Setting the adapter to RecyclerView

        resultsLayoutManager = new LinearLayoutManager(this);                 // Creating a layout Manager

        resultsView.setLayoutManager(resultsLayoutManager);

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



        initialized = true;

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
