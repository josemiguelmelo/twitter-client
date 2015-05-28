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

public class SearchResultsAdapter  extends RecyclerView.Adapter<SearchResultsAdapter.ViewHolder> {

    public ArrayList<User> users;
    public User user;

    SearchResultsAdapter(ArrayList<User> users, User user){ // MyAdapter Constructor with titles and icons parameter
        // titles, icons, name, email, profile pic are passed from the main activity as we
        this.users = users;
        this.user = user;

    }


    public void changeList(ArrayList<User> users){
        this.users = users;
    }

    public void appendItems(ArrayList<User> usersToAppend){
        this.users.addAll(usersToAppend);
    }

    public void addItemToList(User user){
        this.users.add(user);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        ImageView icon;
        TextView screenName;
        ImageButton followButton;


        public ViewHolder(View itemView,int ViewType) {                 // Creating ViewHolder Constructor with View and viewType As a parameter
            super(itemView);

            this.name = (TextView) itemView.findViewById(R.id.name);
            this.icon = (ImageView) itemView.findViewById(R.id.icon);

            this.screenName = (TextView) itemView.findViewById(R.id.screen_name);

            this.followButton = (ImageButton) itemView.findViewById(R.id.followButton);

        }



    }


    @Override
    public SearchResultsAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_result,viewGroup,false); //Inflating the layout

        ViewHolder vhItem = new ViewHolder(v,i); //Creating ViewHolder and passing the object of type view

        return vhItem; // Returning the created object
    }


    @Override
    public void onBindViewHolder(SearchResultsAdapter.ViewHolder viewHolder, int i) {
        final User thisUser = users.get(i);
        viewHolder.name.setText(thisUser.getName());
        viewHolder.screenName.setText(thisUser.getScreen_name());
        viewHolder.icon.setImageBitmap(thisUser.getProfileBitmapImage());

        viewHolder.followButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user.addFriend(thisUser);
            }
        });


    }

    @Override
    public int getItemCount() {
        return users.size();
    }




}
