/**
 * Class Description    : Editable team info that holds editable fields before submitting changes to database
 * Contributors         : Elise Hosu, Eko Manggaprouw
 */
package com.ncl.csc2022.team10;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;

import static com.ncl.csc2022.team10.MainActivity.themeChanger;

public class EditableTeam extends AppCompatActivity implements SubmissionDialog_Team.SubmissionDialogListener, SubmissionDialog_DeleteTeam.SubmissionDialogListener {

    // Declaring all necessary variables
    private static final String TAG = "EditableTeam";
    private Context context;
    public static ServiceCaller _serviceCaller = null;
    private static final String PREF_NAME = "Team10";
    public static String teamID;
    public SharedPreferences mPrefs;
    private EditText teamName, projectName, leaderName, teamMembers;
    private String mTeamName, mProjectName, mLeaderName, mTeamMembers;
    private TextView deleteTeam;


    /**
     * Class building or setting up once it was created
     * @param savedInstanceState Any Bundle passed in
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editable_team);
        context = this;

        // Getting current service caller for server function
        mPrefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        _serviceCaller = new ServiceCaller(mPrefs.getString("sessionToken", null));

        // Only Admin can delete a team
        deleteTeam = findViewById(R.id.deleteTeam);
        if(!MainActivity.getUserType().equals("Admin")){
            deleteTeam.setVisibility(View.INVISIBLE);
        }

        //Bind all layout aspects
        bindLayout();

        //Get passed data
        Intent intent = getIntent();
        getData(intent);

        //Display current data
        setData();

        //After changes have been made
        getTexts(); //Values will not change if not edited

    }

    /**
     * Initialise Submission dialog once update button is clicked
     * @param view View called by the layout
     */
    public void updateClicked(View view){
        SubmissionDialog_Team dialog = new SubmissionDialog_Team();
        dialog.show(getSupportFragmentManager(), TAG);
    }

    /**
     * Finish current activity once canceled is clicked
     * @param view View called by the layout
     */
    public void cancelClickedTeamUpdate(View view){
        this.finish();
    }

    /**
     * Binds all team properties layout
     */
    private void bindLayout(){
        teamName = findViewById(R.id.teamName);
        teamName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    mTeamName = teamName.getText().toString();
                }
            }
        });

        projectName = findViewById(R.id.projectName);
        projectName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    mProjectName = projectName.getText().toString();
                }
            }
        });

        leaderName = findViewById(R.id.leaderName);
        leaderName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    mLeaderName = leaderName.getText().toString();
                }
            }
        });

        teamMembers = findViewById(R.id.teamMembersUsers);
        teamMembers.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    mTeamMembers = teamMembers.getText().toString();
                }
            }
        });
    }

    /**
     * Getting all the team's data
     * @param intent Intent information passed in
     */
    private void getData(Intent intent) {
        teamID = intent.getExtras().getString("TeamID");
        mTeamName = intent.getExtras().getString("TeamName");
        mProjectName = intent.getExtras().getString("ProjectName");
        mLeaderName = intent.getExtras().getString("LeaderName");
        mTeamMembers = intent.getExtras().getString("MemberNames");
    }

    /**
     * Setting the data string to the layout
     */
    private void setData(){
        teamName.setText(mTeamName);
        projectName.setText(mProjectName);
        leaderName.setText(mLeaderName);
        teamMembers.setText(mTeamMembers);
    }

    /**
     * Getting all the text fields string from the layout
     */
    private void getTexts(){
        mTeamName = teamName.getText().toString();
        mProjectName = projectName.getText().toString();
        mLeaderName = leaderName.getText().toString();
        mTeamMembers = teamMembers.getText().toString();
    }

    /**
     * Perform Amend team server function once the submit button is clicked
     */
    @Override
    public void onSubmitClicked() {
        this.finish();
        getTexts();
        new Thread(new Runnable() {
            @Override
            public void run() {
                _serviceCaller.getObjects("AmmendTeam", new AddTeamModel(mTeamName, mProjectName, "", teamID));
                _serviceCaller.getObjects("AddTeamLeader", new TeamIDModel(teamID, mLeaderName));
                List<String> members = Arrays.asList(mTeamMembers.split(","));
                for(String s : members){
                    _serviceCaller.getObjects("RemoveUserFromTeam", new TeamIDModel(teamID,s));
                }
                for(String s : members){
                    _serviceCaller.getObjects("AddUserToTeam", new TeamIDModel(teamID, s));
                }
            }
        }).start();
        Toast.makeText(context, "Team changes have been submitted "+ mTeamName, Toast.LENGTH_SHORT).show();
    }

    /**
     * Admin function to delete team, and calls submission dialog
     * @param view View called by the layout
     */
    public void deleteTeamClicked(View view){
        SubmissionDialog_DeleteTeam dialog = new SubmissionDialog_DeleteTeam();
        dialog.show(getSupportFragmentManager(), TAG);
    }

    /**
     * Start new activity once the delete button is clicked
     */
    @Override
    public void onDeleteTeamClicked() {
        Intent i = new Intent(this, MainActivity.class);
        this.startActivity(i);
        Toast.makeText(this, "Team has been deleted ", Toast.LENGTH_SHORT).show();
    }
}
