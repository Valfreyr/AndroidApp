/**
 * Class Description    : Tab 1, List of all employees (Card Version)
 * Contributors         : Elise Hosu, Eko Manggaprouw
 */
package com.ncl.csc2022.team10;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ncl.csc2022.team10.adapters.CardView_RecyclerViewAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Tab 1 : List of all employees (Card View version)
 */
public class Tab1List_CardView extends Fragment {

    // Variables needed
    private static final String TAG = "Tab1List_CardView";
    private List<Employee> employees;
    private FloatingActionButton addUserButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab1list_cardview, container, false);
        Log.d(TAG, "onCreateView: started");

        addUserButton = rootView.findViewById(R.id.addUserButton);
        // Only display add user button if the logged in user is an Admin
        if(!MainActivity.getUserType().equals("Admin")){
            addUserButton.setVisibility(View.INVISIBLE);
        }

        // Get list of users from MainActivity
        List<User> users =  ((MainActivity)this.getActivity()).users;
        String debug = "null";
        if(users!=null){
            debug = users.toString();
        }
        Log.d("Tab1List_CardView", "Users = " + debug);

        // Initialising layout manager and adapter
        if(users!=null) {
            employees = new ArrayList<>();
            for (User u : users) {
                employees.add(new Employee(u.name, u.role, u.getSkillsString(), u.profilePicture, u.userID.toString(), u.teamID));
            }

            Collections.sort(employees);
            Log.d(TAG, "onCreateView: Users sorted");

            RecyclerView recyclerView = rootView.findViewById(R.id.recyclerview_cardview);
            CardView_RecyclerViewAdapter adapter = new CardView_RecyclerViewAdapter(this.getContext(), employees);

            recyclerView.setLayoutManager(new GridLayoutManager(this.getContext(), 2)); // Two cards per line
            recyclerView.setAdapter(adapter);

            ((MainActivity) getActivity()).adapterCard = adapter;
        }
        return rootView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
