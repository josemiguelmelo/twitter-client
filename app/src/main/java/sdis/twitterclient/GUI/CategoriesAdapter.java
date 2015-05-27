package sdis.twitterclient.GUI;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import sdis.twitterclient.Models.Category;
import sdis.twitterclient.Models.Tweet;
import sdis.twitterclient.Models.User;
import sdis.twitterclient.R;

public class CategoriesAdapter  extends RecyclerView.Adapter<CategoriesAdapter.ViewHolder> {

    ArrayList<Category> categories;
    User user;

    CategoriesAdapter(ArrayList<Category> categories, User user){ // MyAdapter Constructor with titles and icons parameter
        // titles, icons, name, email, profile pic are passed from the main activity as we
        this.categories = categories;
        this.user = user;

    }


    public void changeList(ArrayList<Category> categories){
        this.categories = categories;
    }

    public void appendItems(ArrayList<Category> tweetsToAppend){
        this.categories.addAll(tweetsToAppend);
    }

    public void addItemToList(Category category){
        this.categories.add(category);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView categoryName;
        Button open;


        public ViewHolder(View itemView,int ViewType) {                 // Creating ViewHolder Constructor with View and viewType As a parameter
            super(itemView);

            this.categoryName = (TextView) itemView.findViewById(R.id.categoryName);
            this.open = (Button) itemView.findViewById(R.id.openCategory);
        }



    }


    @Override
    public CategoriesAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_category,viewGroup,false); //Inflating the layout

        ViewHolder vhItem = new ViewHolder(v,i); //Creating ViewHolder and passing the object of type view

        return vhItem; // Returning the created object
    }


    @Override
    public void onBindViewHolder(CategoriesAdapter.ViewHolder viewHolder, int i) {
        final Category category = categories.get(i);
        viewHolder.categoryName.setText(category.getName());
        viewHolder.open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }




}
