package io.recite.audiorecognitiontest.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import io.recite.audiorecognitiontest.R;
import io.recite.audiorecognitiontest.model.QuranModel;

/**
 * Created by johan on 27/7/2017.
 */

public class rvAdapter extends
        RecyclerView.Adapter<rvAdapter.ViewHolder> {

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView ayatTv, surahtv, verseTv;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            ayatTv = (TextView) itemView.findViewById(R.id.tv_ayatText);
            surahtv = (TextView) itemView.findViewById(R.id.tv_surahId);
            verseTv = (TextView) itemView.findViewById(R.id.tv_verseId);
        }
    }


    // Store a member variable for the contacts
    private ArrayList<QuranModel> mList;
    // Store the context for easy access
    private Context mContext;

    // Pass in the contact array into the constructor
    public rvAdapter(Context context, ArrayList<QuranModel> contacts) {
        mList = contacts;
        mContext = context;
    }

    // Easy access to the context object in the recyclerview
    private Context getContext() {
        return mContext;
    }

    // Usually involves inflating a layout from XML and returning the holder
    @Override
    public rvAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.list_item, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(rvAdapter.ViewHolder viewHolder, int position) {
        // Get the data model based on position
        QuranModel data = mList.get(position);

        // Set item views based on your views and data model
        TextView ayatTv = viewHolder.ayatTv;
        ayatTv.setText(data.getayah());
        TextView surahTv = viewHolder.surahtv;
        surahTv.setText("Surah " + String.valueOf(data.getsuraID()));
        TextView verseTv = viewHolder.verseTv;
        verseTv.setText("Verse ");
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        if (mList != null) {
            return mList.size();
        }
        return 0;
    }
}