/**
 * Class Description    : Class which add new team details to the database
 * Contributors         : Elise Hosu, Eko Manggaprouw
 */
package com.ncl.csc2022.team10;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.ncl.csc2022.team10.MainActivity.themeChanger;

public class AddTeam extends AppCompatActivity {

    //Initialise all variables needed
    private static final String TAG = "AddTeam";
    static ServiceCaller _serviceCaller = null;
    private static final String PREF_NAME = "Team10";
    public SharedPreferences mPrefs;
    private EditText teamName, projectName, leaderName;
    private String mTeamName, mProjectName, mLeaderName;


    /**
     * Function executed when AddTeam class is created
     * @param savedInstanceState Any Bundles passed in or the current saved instance state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null)
        {
            themeChanger = bundle.getInt("ThemeChanger");
        }
        ThemeChanger.onActivityCreateSetTheme(this, themeChanger);

        setContentView(R.layout.add_team);

        // Getting current service caller for server function
        mPrefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        _serviceCaller = new ServiceCaller(mPrefs.getString("sessionToken", null));
        mLeaderName = mTeamName = mProjectName = null;

        // Setting up layout aspects
        bindLayout();
        setTexts();
        getTexts();
    }


    /**
     * Binds all layout properties to their listeners
     */
    private void bindLayout() {
        // Bind team name properties and its listener
        teamName = findViewById(R.id.teamName);
        teamName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    mTeamName = teamName.getText().toString();
                }

            }
        });

        // Bind project name properties and its listener
        projectName = findViewById(R.id.projectName);
        projectName.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    mProjectName = projectName.getText().toString();
                }

            }
        });

        // Bind leader name properties and its listener
        leaderName = findViewById(R.id.leaderID);
        leaderName.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    mLeaderName = leaderName.getText().toString();
                }

            }
        });

    }

    /**
     * Set texts passed in to Add Team class into the appropriate layout properties
     */
    private void setTexts(){
        teamName.setText(mTeamName);
        projectName.setText(mProjectName);
        leaderName.setText(mLeaderName);
    }


    /**
     * Getting all texts from layout text fields
     */
    private void getTexts(){
        mTeamName = teamName.getText().toString();
        mProjectName = projectName.getText().toString();
        mLeaderName = leaderName.getText().toString();
    }

    /**
     * Perform server function once a new team properties is about to be added
     * @param view View called by the layout
     */
    public void submitClicked(View view){
        //Create new team using server function
        //Server function
        getTexts();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    _serviceCaller.getObjects("CreateTeam", new AddTeamModel(mTeamName, mProjectName, mLeaderName));
                }catch (Exception e){
                    Log.e("AddTeam", e.toString());
                }
            }
        }).start();
        this.finish();
        Toast.makeText(this, "Team added!", Toast.LENGTH_SHORT).show();
    }

    /**
     * Finish the activity once cancel button is clicked
     * @param view View called by the layout
     */
    public void cancelClickedTeam(View view){
        this.finish();
    }
}
