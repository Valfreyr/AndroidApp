/**
 * Class Description    : Adapter class to display team in cards view
 * Contributors         : Elise Hosu, Eko Manggaprouw, Joseph Heath
 */
package com.ncl.csc2022.team10.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ncl.csc2022.team10.Employee;
import com.ncl.csc2022.team10.R;
import com.ncl.csc2022.team10.TeamUsers;
import com.ncl.csc2022.team10.TeamInfo;

import java.util.List;

public class TeamRecyclerViewAdapter extends RecyclerView.Adapter<TeamRecyclerViewAdapter.TeamViewHolder>{

    //Variables needed for Team adapter class
    private static final String TAG = "TeamAdapter";
    private Context mContext;
    public static List<TeamUsers> mData;
    public static Employee teamLeader;
    public static List<Employee> employees;

    /**
     * Constructor for the Team Adapter class
     * @param mContext Caller class's context
     * @param mData List of teams
     * @param teamLeader Current team leader
     */
    public TeamRecyclerViewAdapter(Context mContext, List<TeamUsers> mData, Employee teamLeader) {
        this.mContext = mContext;
        this.mData = mData;
        this.teamLeader = teamLeader;
    }

    /**
     * View creator and layout inflater for Team adapter class
     * @param parent Parent view group to inflate Team layout
     * @param viewType View type of the new view
     * @return New ViewHolder for Team layout
     */
    @NonNull
    @Override
    public TeamViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        view = mInflater.inflate(R.layout.team_layout_card, parent, false);

        return new TeamViewHolder(view);
    }

    /**
     * Bind and display the data to the correct position
     * @param holder TeamViewHolder which holds the content
     * @param position Current item's position
     */
    @Override
    public void onBindViewHolder(@NonNull TeamViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: called");
        holder.teamName.setText(mData.get(position).team.getTeamName());
        holder.projectName.setText(mData.get(position).team.getProjectName());
        holder.leaderName.setText(String.valueOf(mData.get(position).team.getLeaderID()));

        // Setting up listener to go to team's info screen
        holder.cardLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                teamLeader = null;
                employees = mData.get(position).employeeList;
                Intent intent = new Intent(mContext, TeamInfo.class);

                for(Employee tu : employees)
                    Log.d("TeamRecyclerViewAdapter", "employee = " + tu.getName());

                intent.putExtra("TeamID", String.valueOf( mData.get(position).team.getTeamID() ));
                intent.putExtra("TeamName", mData.get(position).team.getTeamName());
                intent.putExtra("ProjectName", mData.get(position).team.getProjectName());
                intent.putExtra("Position", position);
                teamLeader = mData.get(position).teamLeader;
                Log.d("TeamAdapter", Integer.toString(position));
                if(teamLeader != null){
                    intent.putExtra("LeaderName", mData.get(position).teamLeader.getName());
                    intent.putExtra("LeaderTitle", mData.get(position).teamLeader.getTitle());
                    intent.putExtra("LeaderSkills", mData.get(position).teamLeader.getSkills());
                    intent.putExtra("LeaderId", mData.get(position).teamLeader.getId());
                }

                mContext.startActivity(intent);
            }
        });

    }

    /**
     * Getting total number of elements currently stored in the data set
     * @return Total number elements from list of teams passed to adapter
     */
    @Override
    public int getItemCount() {
        return mData.size();
    }

    /**
     * Inner class for information hold inside one team card
     */
    public static class TeamViewHolder extends RecyclerView.ViewHolder{

        TextView teamName;
        TextView projectName;
        TextView leaderName;
        CardView cardLayout;

        public TeamViewHolder(View itemView) {
            super(itemView);

            teamName = itemView.findViewById(R.id.cardview_teamName);
            projectName = itemView.findViewById(R.id.cardview_projectName);
            leaderName = itemView.findViewById(R.id.cardview_leaderName);
            cardLayout = itemView.findViewById(R.id.cardview_team_parent);
        }
    }
}