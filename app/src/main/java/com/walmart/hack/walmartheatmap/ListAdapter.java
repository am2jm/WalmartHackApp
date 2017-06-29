package com.walmart.hack.walmartheatmap;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by a0m019z on 6/29/17.
 */

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<Notification> mDataset;



    // Provide a suitable constructor (depends on the kind of dataset)
    public ListAdapter(Context context, ArrayList<Notification> myDataset) {

        mDataset = myDataset;
        mContext = context;
    }

    // Create new views (invoked by the layout manager)
//    @Override
    public ListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.notification, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView);


        return viewHolder;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView mTitle;
        public TextView mBody;



        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            mTitle = (TextView) itemView.findViewById(R.id.title);
            mBody = (TextView) itemView.findViewById(R.id.body);
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(final ListAdapter.ViewHolder viewHolder, final int position) {


        TextView mBody = viewHolder.mBody;
        mBody.setText(mDataset.get(position).body);
//        Log.i("Setting Date picker: ", "boop!");

        TextView mTitle = viewHolder.mTitle;
        mTitle.setText(mDataset.get(position).title);

//        TextView emailView = viewHolder.emailAddr;
//        String e = mDataset.get(position).getEmail();
//        emailView.setText(e);


    }

    public void remove(int pos){
        mDataset.remove(pos);
        notifyDataSetChanged();
    }

}
