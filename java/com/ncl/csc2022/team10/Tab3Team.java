/**
 * Class Description    : Tab 3, List of all teams (Card Version)
 * Contributors         : Elise Hosu, Eko Manggaprouw, Joseph Heath
 */
package com.ncl.csc2022.team10;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ncl.csc2022.team10.adapters.TeamRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Tab 3 : Team section
 */
public class Tab3Team extends android.support.v4.app.Fragment {

    //variables needed
    private static final String TAG = "Tab3Team";
    private List<Team> teams;
    private Employee teamLeader;
    private List<TeamUsers> teamUsers = new ArrayList<>();
    private FloatingActionButton addTeamButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab3team, container, false);
        Log.d(TAG, "onCreateView: started");

        addTeamButton = rootView.findViewById(R.id.addTeamButton);
        // Only display add team button if the logged in user is an Admin
        if(!MainActivity.getUserType().equals("Admin")){
            addTeamButton.setVisibility(View.INVISIBLE);
        }

        // Get list of teams from MainActivity
        teams = ((MainActivity)this.getActivity()).teams;
        String debug = "null";
        if(teams!=null){
            debug = teams.toString();
        }
        Log.d("Tab3Team", "Team = " + debug);

        // Initialising adapter and layout manager
        if(teams != null){
            getMembers(teams);
            if(teamUsers != null) {
                Log.d("Tab3Team", "Members = " + teamUsers.toString());

                RecyclerView teamRV = rootView.findViewById(R.id.team_cardview_recyclerview);
                TeamRecyclerViewAdapter teamAdapter = new TeamRecyclerViewAdapter(this.getContext(), teamUsers, teamLeader);

                teamRV.setLayoutManager(new GridLayoutManager(this.getContext(), 2));
                teamRV.setAdapter(teamAdapter);
            }
        }
        return rootView;
    }

    /**
     * Get all members of a team
     * @param teams List of all teams
     */
    private void getMembers(List<Team> teams) {
        List<User> users = ((MainActivity)this.getActivity()).users;
        String debug = "null";
        if(users!=null){
            debug = users.toString();
        }
        Log.d("Tab3Team", "User = " + debug);

        if(users !=null && teams != null){
            for(Team team : teams){
                List<Employee> employees = new ArrayList<>();
                teamLeader = null;
                for(User u : users){
                    if(u.teamID != null && u.teamID.equals(team.teamID) && u.userID != team.leaderID){
                        //Log.d("Tab3Team", "test = " + u.name + " userID = " + u.teamID + " teamID = " +team.teamID+ " do team matches? = " + u.teamID.equals(team.teamID));
                        employees.add(new Employee(u.name, u.role, u.getSkillsString(), u.profilePicture,u.userID.toString(), u.teamID));
                    }

                    if(u.userID.equals(team.leaderID)){
                        teamLeader = new Employee(u.name, u.role, u.getSkillsString(), u.profilePicture,u.userID.toString(), u.teamID);
                    }
                }

                teamUsers.add(new TeamUsers(team, employees, teamLeader));
            }
            for(TeamUsers tu : teamUsers)
                for(Employee e : tu.employeeList)
                    Log.d("Tab3Team", "teamUsers = " + tu + " employee = " +e.teamID);
        }
    }
}