package sdis.twitterclient.GUI;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.Serializable;

import sdis.twitterclient.Models.User;
import sdis.twitterclient.R;


public class NavbarAdapter extends RecyclerView.Adapter<NavbarAdapter.ViewHolder> {

    private static final int TYPE_HEADER = 0;  // Declaring Variable to Understand which View is being worked on
    // IF the view under inflation and population is header or Item
    private static final int TYPE_ITEM = 1;
    private Activity activity;

    public String mNavTitles[]; // String Array to store the passed titles Value from MainActivity.java
    private int mIcons[];       // Int Array to store the passed icons resource value from MainActivity.java

    private String name;        //String Resource for header View Name
    private String email;       //String Resource for header view email

    private User user;

    // Creating a ViewHolder which extends the RecyclerView View Holder
    // ViewHolder are used to to store the inflated views in order to recycle them

    public static class ViewHolder extends RecyclerView.ViewHolder {
        int Holderid;

        TextView button;
        ImageView imageView;
        ImageView profile;
        TextView Name;
        TextView email;


        public ViewHolder(View itemView,int ViewType) {                 // Creating ViewHolder Constructor with View and viewType As a parameter
            super(itemView);


            // Here we set the appropriate view in accordance with the the view type as passed when the holder object is created

            if(ViewType == TYPE_ITEM) {
                button = (TextView) itemView.findViewById(R.id.rowText); // Creating TextView object with the id of textView from item_row.xml
                imageView = (ImageView) itemView.findViewById(R.id.rowIcon);// Creating ImageView object with the id of ImageView from item_row.xml
                Holderid = 1;                                               // setting holder id as 1 as the object being populated are of type item row
            }
            else{


                Name = (TextView) itemView.findViewById(R.id.name);         // Creating Text View object from header.xml for name
                email = (TextView) itemView.findViewById(R.id.email);       // Creating Text View object from header.xml for email

                Holderid = 0;                                                // Setting holder id = 0 as the object being populated are of type header view
            }
        }


    }



    NavbarAdapter(Activity activity, User user, String Titles[], int Icons[], String Name, String Email){ // MyAdapter Constructor with titles and icons parameter
        // titles, icons, name, email, profile pic are passed from the main activity as we
        mNavTitles = Titles;                //have seen earlier
        mIcons = Icons;
        name = Name;
        email = Email;
        this.activity = activity;
        this.user = user;

    }



    //Below first we ovverride the method onCreateViewHolder which is called when the ViewHolder is
    //Created, In this method we inflate the item_row.xml layout if the viewType is Type_ITEM or else we inflate header.xml
    // if the viewType is TYPE_HEADER
    // and pass it to the view holder

    @Override
    public NavbarAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == TYPE_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row,parent,false); //Inflating the layout

            ViewHolder vhItem = new ViewHolder(v,viewType); //Creating ViewHolder and passing the object of type view

            return vhItem; // Returning the created object

            //inflate your layout and pass it to view holder

        } else if (viewType == TYPE_HEADER) {

            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.header,parent,false); //Inflating the layout

            ViewHolder vhHeader = new ViewHolder(v,viewType); //Creating ViewHolder and passing the object of type view

            return vhHeader; //returning the object created


        }
        return null;

    }

    //Next we override a method which is called when the item in a row is needed to be displayed, here the int position
    // Tells us item at which position is being constructed to be displayed and the holder id of the holder object tell us
    // which view type is being created 1 for item row
    @Override
    public void onBindViewHolder(NavbarAdapter.ViewHolder holder, int position) {
        if(holder.Holderid ==1) {                              // as the list view is going to be called after the header view so we decrement the
            // position by 1 and pass it to the holder while setting the text and image
            holder.button.setText(mNavTitles[position - 1]); // Setting the Text with the array of our Titles

            if(mNavTitles[position-1].equals("Home")){
                holder.button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(activity.getLocalClassName().equals("GUI.ClientActivity") == false){
                            activity.finish();
                        }
                    }
                });
            }
            if(mNavTitles[position-1].equals("Logout")){
                holder.button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SharedPreferences.Editor e = ClientActivity.mSharedPreferences.edit();
                        e.putBoolean(LoginActivity.PREF_KEY_TWITTER_LOGIN, false);
                        e.commit();
                        activity.finish();
                    }
                });
            }
            if(mNavTitles[position-1].equals("Categories")){
                holder.button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(activity.getLocalClassName().equals("GUI.CategoriesActivity") == false){
                            Intent categoriesIntent = new Intent(activity, CategoriesActivity.class);
                            Bundle mBundle = new Bundle();

                            mBundle.putSerializable("user" ,user);
                            categoriesIntent.putExtras(mBundle);
                            categoriesIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            activity.startActivity(categoriesIntent);

                            if(activity.getLocalClassName().equals("GUI.ClientActivity") == false){
                                activity.finish();
                            }else{
                                ((ClientActivity)activity).Drawer.closeDrawer(Gravity.LEFT);
                            }
                        }else{
                            ((CategoriesActivity)activity).Drawer.closeDrawer(Gravity.LEFT);
                        }

                    }
                });
            }
            if(mNavTitles[position-1].equals("Search user")){
                holder.button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(activity.getLocalClassName().equals("GUI.SearchActivity") == false){
                            Intent searchIntent = new Intent(activity, SearchActivity.class);
                            Bundle mBundle = new Bundle();

                            mBundle.putSerializable("user" ,user);
                            searchIntent.putExtras(mBundle);
                            searchIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            activity.startActivity(searchIntent);

                            if(activity.getLocalClassName().equals("GUI.ClientActivity") == false){
                                activity.finish();
                            }else{
                                ((ClientActivity)activity).Drawer.closeDrawer(Gravity.LEFT);
                            }
                        }else{
                            ((CategoriesActivity)activity).Drawer.closeDrawer(Gravity.LEFT);
                        }

                    }
                });
            }
            if(mNavTitles[position-1].equals("Add Category")){
                holder.button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(activity.getLocalClassName().equals("GUI.CreateCategoryActivity") == false){
                            Intent newCategoryIntent = new Intent(activity, CreateCategoryActivity.class);
                            Bundle mBundle = new Bundle();

                            mBundle.putSerializable("user",user);
                            newCategoryIntent.putExtras(mBundle);
                            newCategoryIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            activity.startActivity(newCategoryIntent);
                            if(activity.getLocalClassName().equals("GUI.ClientActivity") == false){
                                activity.finish();
                            }else{
                                ((ClientActivity)activity).Drawer.closeDrawer(Gravity.LEFT);
                            }
                        }else{
                            ((CreateCategoryActivity)activity).Drawer.closeDrawer(Gravity.LEFT);
                        }

                    }
                });
            }


            holder.imageView.setImageResource(mIcons[position -1]);// Settimg the image with array of our icons
        }
        else{          // Similarly we set the resources for header view
            holder.Name.setText(name);
            holder.email.setText(email);
        }
    }

    // This method returns the number of items present in the list
    @Override
    public int getItemCount() {
        return mNavTitles.length+1; // the number of items in the list will be +1 the titles including the header view.
    }


    // Witht the following method we check what type of view is being passed
    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position))
            return TYPE_HEADER;

        return TYPE_ITEM;
    }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }

}