/**
 * Class Description    : Adapter class to display list of notifications
 * Contributors         : Elise Hosu, Eko Manggaprouw
 */
package com.ncl.csc2022.team10.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ncl.csc2022.team10.R;

import java.util.List;

public class Notification_RecyclerViewAdapter extends RecyclerView.Adapter<Notification_RecyclerViewAdapter.ViewHolder>{

    //Variables needed for Notification adapter class
    private static final String TAG = "ListView_Adapter";
    private List<String> Messages;
    private List<String> Time;
    private Context context;

    /**
     * Constructor for the ListView Adapter class
     * @param context Caller class's context
     * @param messages List of notification messages
     * @param time List of messages time
     */
    public Notification_RecyclerViewAdapter(Context context,List<String> messages,List<String> time) {
        Messages = messages;
        Time = time;
        this.context = context;
    }

    /**
     * View creator and layout inflater for notification adapter class
     * @param viewGroup Parent view group to inflate notification layout
     * @param i View type of the new view
     * @return New ViewHolder for notification layout
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.notification_layout_list, viewGroup, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    /**
     * Bind and display the data to the correct position
     * @param viewHolder ViewHolder which holds the content
     * @param position Current item's position
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int position) {
        Log.d(TAG, "onBindViewHolder:called.");
        viewHolder.mainContent.setText(Messages.get(position));
        viewHolder.time.setText(Time.get(position));
    }

    /**
     * Getting total number of elements currently stored in the data set
     * @return Total number elements from list of messages passed to adapter
     */
    @Override
    public int getItemCount() {
        return Messages.size();
    }

    /**
     * Inner class for information hold inside one notification list
     */
    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView mainContent;
        TextView time;
        ConstraintLayout parentLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mainContent = itemView.findViewById(R.id.notification_content);
            time = itemView.findViewById(R.id.notification_time);
            parentLayout = itemView.findViewById(R.id.notification_parent_layout);

        }
    }
}
