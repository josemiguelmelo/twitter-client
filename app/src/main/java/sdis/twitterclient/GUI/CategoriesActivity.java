package sdis.twitterclient.GUI;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.ArrayList;

import sdis.twitterclient.Database.DatabaseHandler;
import sdis.twitterclient.Models.Category;
import sdis.twitterclient.Models.User;
import sdis.twitterclient.R;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

public class CategoriesActivity extends ActionBarActivity {


    //First We Declare Titles And Icons For Our Navigation Drawer List View
    //This Icons And Titles Are holded in an Array as you can see

    String TITLES[] = {"Home","Search user","Add Category", "Categories", "Logout"};
    int ICONS[] = {R.drawable.home,R.drawable.search, R.drawable.plus,R.drawable.categories,R.drawable.logout};


    //Similarly we Create a String Resource for the name and email in the header view
    //And we also create a int resource for profile picture in the header view

    int PROFILE = R.drawable.ic_drawer;

    private Toolbar toolbar;                              // Declaring the Toolbar Object

    RecyclerView navBarView;                           // Declaring RecyclerView
    RecyclerView.Adapter navbarAdapter;                        // Declaring Adapter For Recycler View
    DrawerLayout Drawer;                                  // Declaring DrawerLayout

    ActionBarDrawerToggle mDrawerToggle;                  // Declaring Action Bar Drawer Toggle


    private Twitter twitter;
    private AccessToken accessToken;

    private User user;

    public static SharedPreferences mSharedPreferences;

    ArrayList<User> categoryUser = new ArrayList<>();



    RecyclerView categoriesView;
    public CategoriesAdapter categoriesAdapter;                  // Declaring Adapter For Recycler View
    RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        toolbar.setTitle("Twitter Client");
        setSupportActionBar(toolbar);


        this.user = (User) getIntent().getSerializableExtra("user");
        this.user.databaseHandler = new DatabaseHandler(getApplication());
        this.user.loadCategories();


        categoriesView = (RecyclerView) findViewById(R.id.categoriesView);

        categoriesView.setHasFixedSize(true);                            // Letting the system know that the list objects are of fixed size

        this.categoriesAdapter = new CategoriesAdapter(user.getCategories(), user, this);       // Creating the Adapter of MyAdapter class(which we are going to see in a bit)
        // And passing the titles,icons,header view name, header view email,
        // and header view profile picture

        categoriesView.setAdapter(categoriesAdapter);                              // Setting the adapter to RecyclerView

        mLayoutManager = new LinearLayoutManager(this);                 // Creating a layout Manager

        categoriesView.setLayoutManager(mLayoutManager);


        navBarView = (RecyclerView) findViewById(R.id.navbarView); // Assigning the RecyclerView Object to the xml View

        navBarView.setHasFixedSize(true);                            // Letting the system know that the list objects are of fixed size

        navbarAdapter = new NavbarAdapter(this, user, TITLES ,ICONS, user.getName(),"@"+user.getScreen_name());       // Creating the Adapter of MyAdapter class(which we are going to see in a bit)
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

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_create_category, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
