package sdis.twitterclient.GUI;

import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import sdis.twitterclient.Database.DatabaseHandler;
import sdis.twitterclient.Models.Category;
import sdis.twitterclient.Models.User;
import sdis.twitterclient.R;
import twitter4j.Twitter;
import twitter4j.auth.AccessToken;


public class ClientActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    private ListView tweetsListView;
    public DatabaseHandler databaseHandler;


    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    private Twitter twitter;
    private AccessToken accessToken;

    private User user;

    private static SharedPreferences mSharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);

        tweetsListView = (ListView) findViewById(R.id.TweetsList);
        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));


        mSharedPreferences = this.getSharedPreferences(
                "sdis.twitterclient", Context.MODE_PRIVATE);



        this.twitter = LoginActivity.twitter;
        this.accessToken = LoginActivity.accessToken;

        this.user = new User(this.getApplicationContext(), this.accessToken.getUserId(), this.accessToken);

        Log.d("Access token secret", this.accessToken.getTokenSecret());
        Log.d("Access token", this.accessToken.getToken());
        Log.d("Access User ID", "" + this.accessToken.getUserId());
        this.user.init();

        tweetsListView.setAdapter(new ListAdapter(user.getHomeTimeLineTweets() , this));

        this.databaseHandler = new DatabaseHandler(this.getApplicationContext());
        Category category = new Category("Category 1");
        category.setUsers(user.getFriendsList());
        this.databaseHandler.addCategory(category);

        ArrayList<Category> categories = this.databaseHandler.getAllCategories();
        Log.d("Number categories", "" + categories.size());
        Log.d("First category name", "" + categories.get(0).getName());
        Log.d("First category users", "" + categories.get(0).getUsers().size());

    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, new TimelineFragment())
                .commit();
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_logout:
                SharedPreferences.Editor e = mSharedPreferences.edit();
                e.putBoolean(LoginActivity.PREF_KEY_TWITTER_LOGIN, false);
                e.commit();
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.client, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_client, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((ClientActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

}
