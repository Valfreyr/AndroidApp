/**
 * Class Description    : Tab 2, List of all notifications
 * Contributors         : Elise Hosu, Eko Manggaprouw
 */
package com.ncl.csc2022.team10;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ncl.csc2022.team10.adapters.Notification_RecyclerViewAdapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;

/**
 * Tab 2 : Notification section
 */
public class Tab2Notification extends Fragment {

    //Variables needed
    private static final String TAG = "Tab2Notification";
    private List<String> MESSAGES = new ArrayList<>();
    private List<String> TIMES = new ArrayList<>();
    private List<NotificationModel> nm;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab2notification, container, false);
        Log.d(TAG, "onCreateView: started");

        List<Notification> notifications =  ((MainActivity)this.getActivity()).notifications;
        String debug = "null";
        if(notifications!=null){
            debug = notifications.toString();
        }
        Log.d(TAG, "Notification = " + debug);

        if(notifications != null){
            for(Notification n : notifications){
                MESSAGES.add(n.body);
                Date test;
                SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                SimpleDateFormat output = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                String s = null;
                if(n.date != null){
                    try{
                        test = input.parse(n.date);
                        s = output.format(test);
                    }
                    catch(ParseException pe){
                        Log.e("profileParseException", pe.toString());
                    }
                }

                TIMES.add(s);
            }
        }


        Log.d(TAG, "intialiseRecyclerView: intialise RecyclerView");

        // Set up recycler view and adapter
        RecyclerView recyclerView = rootView.findViewById(R.id.notification_list_recycler_view);
        Notification_RecyclerViewAdapter adapter = new Notification_RecyclerViewAdapter(this.getContext(), MESSAGES, TIMES);
        recyclerView.setAdapter(adapter);

        // Set up layout manager
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getContext());
        linearLayoutManager.setReverseLayout(true); // Newest notification first
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        return rootView;
    }
}
