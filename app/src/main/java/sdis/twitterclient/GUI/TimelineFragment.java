package sdis.twitterclient.GUI;

import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import sdis.twitterclient.Models.Tweet;
import sdis.twitterclient.R;

public class TimelineFragment extends Fragment {

    RecyclerView timelineView;
    public TimelineAdapter timelineAdapter;                  // Declaring Adapter For Recycler View
    RecyclerView.LayoutManager mLayoutManager;

    Context context;

    ArrayList<Tweet> timelineTweets = new ArrayList<>();

    public void setTimelineTweets(ArrayList<Tweet> timelineTweets){
        this.timelineTweets = timelineTweets;
    }

    public void setContext(Context context){ this.context = context; }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.timeline, container, false);

        timelineView = (RecyclerView) view.findViewById(R.id.timelineView);

        timelineView.setHasFixedSize(true);                            // Letting the system know that the list objects are of fixed size



        this.timelineAdapter = new TimelineAdapter(null);       // Creating the Adapter of MyAdapter class(which we are going to see in a bit)
        // And passing the titles,icons,header view name, header view email,
        // and header view profile picture

        Log.d("timeline adapter", timelineAdapter.toString());
        timelineView.setAdapter(timelineAdapter);                              // Setting the adapter to RecyclerView

        mLayoutManager = new LinearLayoutManager(context);                 // Creating a layout Manager

        timelineView.setLayoutManager(mLayoutManager);

        return view;
    }

}