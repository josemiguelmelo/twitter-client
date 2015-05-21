package sdis.twitterclient.GUI;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import sdis.twitterclient.Models.Tweet;
import sdis.twitterclient.R;

public class TimelineFragment extends Fragment {

    RecyclerView timelineView;
    RecyclerView.Adapter timelineAdapter;                  // Declaring Adapter For Recycler View
    RecyclerView.LayoutManager mLayoutManager;

    Context context;

    public void setContext(Context context){ this.context = context; }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.timeline, container, false);

        timelineView = (RecyclerView) view.findViewById(R.id.timelineView);

        timelineView.setHasFixedSize(true);                            // Letting the system know that the list objects are of fixed size

        ArrayList<Tweet> tweets = new ArrayList<>();

        tweets.add(new Tweet("creator", 10202, "created_at", "text"));
        tweets.add(new Tweet("creator 2", 10202, "created_at", "text"));
        tweets.add(new Tweet("creator 3", 10202, "created_at", "text"));
        tweets.add(new Tweet("creator 4", 10202, "created_at", "text"));
        tweets.add(new Tweet("creator 5", 10202, "created_at", "text"));
        tweets.add(new Tweet("creator 6", 10202, "created_at", "text"));
        tweets.add(new Tweet("creator 7", 10202, "created_at", "text"));
        timelineAdapter = new TimelineAdapter(tweets);       // Creating the Adapter of MyAdapter class(which we are going to see in a bit)
        // And passing the titles,icons,header view name, header view email,
        // and header view profile picture

        timelineView.setAdapter(timelineAdapter);                              // Setting the adapter to RecyclerView

        mLayoutManager = new LinearLayoutManager(context);                 // Creating a layout Manager

        timelineView.setLayoutManager(mLayoutManager);


        return view;
    }

}