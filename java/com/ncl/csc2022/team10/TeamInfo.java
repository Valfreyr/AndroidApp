/**
 * Class Description    : Class which holds all information about a team
 * Contributors         : Elise Hosu, Eko Manggaprouw
 */
package com.ncl.csc2022.team10;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ncl.csc2022.team10.adapters.CardView_RecyclerViewAdapter;
import com.ncl.csc2022.team10.adapters.TeamRecyclerViewAdapter;

import net.karthikraj.shapesimage.ShapesImage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class TeamInfo extends AppCompatActivity {

    private static final String TAG = "TeamInfo";
    private Context context;

    private TextView TEAM_NAME;
    private TextView PROJECT_NAME;

    private TextView LEADER_NAME;
    private TextView LEADER_TITLE;
    private TextView LEADERS_SKILLS;
    private ShapesImage LEADER_IMAGE;

    private ImageButton editTeam;

    private List<Employee> members;

    String teamName;
    String projectName;
    String leaderName;
    String leaderTitle;
    String leaderSkills;
    String leaderId;
    static String teamID;
    private Employee teamLeader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.team_info);
        context = this;

        //Bind layouts
        TEAM_NAME = findViewById(R.id.teamName);
        PROJECT_NAME = findViewById(R.id.projectName);

        LEADER_NAME = findViewById(R.id.cardview_leader_name);
        LEADER_TITLE = findViewById(R.id.cardview_leader_title);
        LEADERS_SKILLS = findViewById(R.id.cardview_leader_skills);
        LEADER_IMAGE = findViewById(R.id.cardview_profile_picture);
        members = getMembers();

        editTeam = findViewById(R.id.editTeam);
        if(MainActivity.getUserType().equals("User") ){
            editTeam.setVisibility(View.INVISIBLE);
        }

        members = getMembers();


        //Receive data
        Intent intent = getIntent();
        teamID = intent.getExtras().getString("TeamID");
        teamName = intent.getExtras().getString("TeamName");
        projectName = intent.getExtras().getString("ProjectName");
        teamLeader = TeamRecyclerViewAdapter.mData.get(intent.getExtras().getInt("Position")).teamLeader;
        if(teamLeader!=null) {
            leaderName = intent.getExtras().getString("LeaderName");
            leaderTitle = intent.getExtras().getString("LeaderTitle");
            leaderSkills = intent.getExtras().getString("LeaderSkills");
            leaderId = intent.getExtras().getString("LeaderId");
            if(teamLeader.getProfile_pic()!=null){
                LEADER_IMAGE.setImageBitmap(teamLeader.getProfile_pic());
            }
        }

        //Setting values
        TEAM_NAME.setText(teamName);
        PROJECT_NAME.setText(projectName);
        if(teamLeader!=null) {
            LEADER_NAME.setText(leaderName);
            LEADER_TITLE.setText(leaderTitle);
            LEADERS_SKILLS.setText(leaderSkills);
        } else {
            LEADER_NAME.setText("No leader");
            LEADER_TITLE.setText("");
            LEADERS_SKILLS.setText("");
            LEADER_IMAGE.setImageResource(android.R.color.transparent);
        }
        StringBuilder mems = new StringBuilder();
        for(int i = 0; i < members.size(); i++){
            mems.append(members.get(i).getName());
            if(i != members.size() - 1){
                mems.append(", ");
            }
        }

        final String memString = mems.toString();
        editTeam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, EditableTeam.class);
                intent.putExtra("TeamID", teamID);
                intent.putExtra("TeamName", teamName);
                intent.putExtra("ProjectName", projectName);
                intent.putExtra("LeaderName", leaderName);

                intent.putExtra("MemberNames", memString);
                context.startActivity(intent);

            }
        });



        RecyclerView mRV = findViewById(R.id.teamMembers_recyclerView);
        CardView_RecyclerViewAdapter mCV_RVA = new CardView_RecyclerViewAdapter(this, members);


        mRV.setLayoutManager(new GridLayoutManager(this, 2));
//        mRV.setNestedScrollingEnabled(false);
        mRV.setAdapter(mCV_RVA);
    }

    /**
     * Get the members from a team
     * @return List of employees
     */
    private List<Employee> getMembers() {
        List<Employee> m =  TeamRecyclerViewAdapter.employees;
        Log.d("TeamInfo", "employees = " + Arrays.toString(m.toArray()));
        return m;
    }

    /**
     * Display the leader separately
     * @param view
     */
    public void openProfile(View view){
        if(teamLeader!=null) {
            Intent intent = new Intent(context, Profile.class);

            intent.putExtra("id", leaderId);
//        intent.putExtra("Image", mData.get(i).getProfile_pic());

            context.startActivity(intent);
        } else {
            Toast.makeText(context, "There is no team leader for the current team.", Toast.LENGTH_SHORT);
        }
    }
}
