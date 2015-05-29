package sdis.twitterclient.GUI;

import android.app.Activity;
import android.content.Intent;
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

import java.util.ArrayList;

import sdis.twitterclient.Models.Category;
import sdis.twitterclient.Models.Tweet;
import sdis.twitterclient.Models.User;
import sdis.twitterclient.R;

public class CategoriesAdapter  extends RecyclerView.Adapter<CategoriesAdapter.ViewHolder> {

    ArrayList<Category> categories;
    User user;
    private Activity activity;


    CategoriesAdapter(ArrayList<Category> categories, User user, Activity activity){ // MyAdapter Constructor with titles and icons parameter
        // titles, icons, name, email, profile pic are passed from the main activity as we
        this.categories = categories;
        this.user = user;
        this.activity = activity;

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

    public void removeItemFromList(Category category){
        for(int i = 0; i < this.categories.size(); i++){
            if(categories.get(i).getName().equals(category.getName())){
                categories.remove(i);
                return;
            }
        }
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView categoryName;
        Button open;

        Button delete;

        public ViewHolder(View itemView,int ViewType) {                 // Creating ViewHolder Constructor with View and viewType As a parameter
            super(itemView);

            this.categoryName = (TextView) itemView.findViewById(R.id.categoryName);
            this.open = (Button) itemView.findViewById(R.id.openCategory);
            this.delete = (Button) itemView.findViewById(R.id.deleteCategory);
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
                Intent categoriesIntent = new Intent(activity, SpecificCategoryActivity.class);
                Bundle mBundle = new Bundle();

                mBundle.putSerializable("user" ,user);
                mBundle.putSerializable("category", category);
                categoriesIntent.putExtras(mBundle);
                categoriesIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                activity.startActivity(categoriesIntent);
                activity.finish();
            }
        });

        viewHolder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user.databaseHandler.removeCategory(category);
                removeItemFromList(category);
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }




}
