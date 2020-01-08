/**
 * Class Description    : Main class that holds information on its child tab fragments, and initialise & load all data after successful log in
 * Contributors         : Changda Pan, Elise Hosu, Eko Manggaprouw, Joseph Heath, Katarzyna Nozka
 */
package com.ncl.csc2022.team10;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.ncl.csc2022.team10.adapters.CardView_RecyclerViewAdapter;
import com.ncl.csc2022.team10.adapters.ListView_RecyclerViewAdapter;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SubmissionDialog_DeleteDocument.SubmissionDialogListener{

    // Initialise all necessary fields
    static ServiceCaller _serviceCaller = null;
    static String userID;
    public static String userType;
    List<User> users;
    List<Notification> notifications;
    List<Team> teams;
    User user;
    private static final String PREF_NAME = "Team10";
    private static final String TAG = "MainActivity";
    public ListView_RecyclerViewAdapter adapterList;
    public CardView_RecyclerViewAdapter adapterCard;
    public static int viewChanger; //0 for list view, any other number for view
    private static boolean viewChangerSwitchON;
    public static int themeChanger; //0 for list view, any other number for view
    private static boolean themeChangerSwitchON;

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    ProgressBar progressBar;
    SearchView searchView;

    public SharedPreferences mPrefs;


    /**
     * Method to receive notifications
     */
    public void testFirebase (){
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        FirebaseNotification fms = new FirebaseNotification();
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();
                        // Log and toast
                        String msg = getString(R.string.msg_token_fmt, token);
                        Log.d(TAG, msg);

                    }
                });
    }

    /**
     * This method will make calls to the core server functions such as:
     * - Main screen (with all the users)
     * - Notifications tab
     * - Team tab
     * - Profile tab
     * Https requests must be on a different thread
     * @param savedInstanceState
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
        setContentView(R.layout.splash);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        /**
         * Getting the cached variables
         * Creating a serviceCaller object to store our session token
         *
         * Session token is used as a security measure, it is generated when the user logs in and it gets destroyed when the user logs out
         * Without a valid session token the server will reply with a 403 code (Forbidden)
         */
        Log.d(TAG, "onCreate: Attempt Login");
        mPrefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        _serviceCaller = new ServiceCaller(mPrefs.getString("sessionToken", null));
        userID = mPrefs.getString("userID",null);
        Log.d(TAG, "UserID = " + userID);
        //Getting User Type
        userType = mPrefs.getString("userType",null);
        Log.d(TAG, "UserType = "+ userType);

        FirebaseNotification fms = new FirebaseNotification();
        fms.onNewToken(FirebaseInstanceId.getInstance().getToken(),_serviceCaller);
        testFirebase();
        final UserCaller us = new UserCaller(_serviceCaller, this);
        new Thread(new Runnable() {
            @Override
            public void run() {

                us.GetUsers();
            }

        }).start();

        final UserCaller notification = new UserCaller(_serviceCaller, this);
        new Thread(new Runnable() {
            @Override
            public void run() {

                notification.GetNotifications();
            }
        }).start();

        final UserCaller team = new UserCaller(_serviceCaller, this);
        new Thread(new Runnable() {
            @Override
            public void run() {

                team.GetTeams();
            }
        }).start();

        final UserCaller profile = new UserCaller(_serviceCaller, this);
        new Thread(new Runnable() {
            @Override
            public void run() {

                profile.GetProfile(userID);
            }
        }).start();
    }


    /**
     * Method to populate the ui with the information from the server (all users) and the right fragment
     * A loading screen will be triggered to prevent the user to access the app before it is fully loaded
     * @param users A list with all the user objects
     */
    public void finishUpdateUsers(List<User> users){
        Log.d("MainActivity","Finishing user update: " + Arrays.toString(users.toArray()));
        this.users = users;
        Fragment f = mSectionsPagerAdapter.getExistingFragment(0);
        if(f!=null) {
            getSupportFragmentManager().beginTransaction().detach(f).attach(f).commit();
        }

        // loading screen
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setContentView(R.layout.activity_main);
                //        Intent intent = getIntent();
                Bundle bundle = getIntent().getExtras();
                if (bundle != null)
                {
//            viewChanger = (int) intent.getExtras().get("ViewChanger");
                    viewChanger = bundle.getInt("ViewChanger");
                }
                progressBar = (ProgressBar) findViewById(R.id.progress_bar2);
                progressBar.setVisibility(View.VISIBLE);

                searchView =(SearchView)findViewById(R.id.search_bar);
                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        if(viewChanger != 0){
                            adapterCard.getFilter().filter(newText);
                        }else{
                            adapterList.getFilter().filter(newText);
                        }
                        return false;
                    }
                });


//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
                // Create the adapter that will return a fragment for each of the three
                // primary sections of the activity.

//        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportChildFragmentManager());

                // Set up the ViewPager with the sections adapter.
                mViewPager = (ViewPager) findViewById(R.id.container);
                mViewPager.setAdapter(mSectionsPagerAdapter);

                TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

                tabLayout.getTabAt(0).setIcon(R.drawable.list_icon);
                tabLayout.getTabAt(1).setIcon(R.drawable.notification_icon);
                tabLayout.getTabAt(2).setIcon(R.drawable.team_icon);
                tabLayout.getTabAt(3).setIcon(R.drawable.profile_icon);
                tabLayout.getTabAt(4).setIcon(R.drawable.menu_icon);

//        GetData task = new GetData(this);
//        task.execute("1");

                mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));


                tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

                progressBar.setVisibility(View.GONE);
            }
        });
    }

    /**
     * Method to populate the ui with the information from the server (notifications) and the right fragment
     * @param notifications A list with all the notifications objects
     */
    public void finishUpdateNotifications (List<Notification> notifications){
        Log.d("MainActivity","Finishing notification update: " + Arrays.toString(notifications.toArray()));
        this.notifications = notifications;

        Fragment f = mSectionsPagerAdapter.getExistingFragment(1);
        if(f!=null) {
            getSupportFragmentManager().beginTransaction().detach(f).attach(f).commit();
        }
    }

    /**
     * Method to populate the ui with the information from the server (teams) and the right fragment
     * @param teams A list with all the team objects
     */
    public void finishUpdateTeam (List<Team> teams){
        Log.d("MainActivity","Finishing team update: " + Arrays.toString(teams.toArray()));
        this.teams = teams;
        Fragment f = mSectionsPagerAdapter.getExistingFragment(2);
        if(f!=null) {
            getSupportFragmentManager().beginTransaction().detach(f).attach(f).commit();
        }
    }

    /**
     * Method to populate the ui with the information from the server (user) and the right fragment
     * @param user User object
     */
    public void finishUpdateProfile(User user){
        Log.d("MainActivityProfile","Finishing profile update: " + user.teamID);
        Log.d("MainActivityProfile","Finishing language update: " + user.skills);
        this.user = user;
        Fragment f = mSectionsPagerAdapter.getExistingFragment(3);
        if(f!=null) {
            getSupportFragmentManager().beginTransaction().detach(f).attach(f).commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.app_menu, menu);

//        MenuItem searchItem = menu.findItem(R.id.action_search);
//        android.support.v7.widget.SearchView searchView = (android.support.v7.widget.SearchView) searchItem.getActionView();
//
//        searchView.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                adapter.getFilter().filter(newText);
//                return false;
//            }
//        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

//    public void editProfile(View view) {
//        Intent edit = new Intent(getApplicationContext(), EditableProfile.class);
//        startActivity(edit);
//    }

    /**
     *
     * @param view
     */
    public void addUser(View view){
        Intent addUserScreen = new Intent(getApplicationContext(), AddUser.class);
        startActivity(addUserScreen);
    }

    public void addTeam(View view){
        Intent addTeamScreen = new Intent(getApplicationContext(), AddTeam.class);
        startActivity(addTeamScreen);
    }

    public static String getUserType() {
        return userType;
    }

    /**
     * Method to delete the documents on the user's profile
     * @param fileName Name of the documents
     */
    @Override
    public void onDeleteUserClicked(String fileName){
        List<Document> documents = ((Tab4Profile)mSectionsPagerAdapter.getExistingFragment(3)).documents;
        for(Document d : documents){
            if(d.getName().toLowerCase().equals(fileName.toLowerCase())){
                documents.remove(d);
                break;
            }
        }
    }
    //deleted PlaceHolderFragment class from here

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends android.support.v4.app.FragmentStatePagerAdapter {
        public HashMap<Integer,Fragment> curFrags = new HashMap<Integer, Fragment>();
        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Tab1List tab1List = new Tab1List();
            Tab1List_CardView tab1List_cardView = new Tab1List_CardView();
            Tab2Notification tab2Notification = new Tab2Notification();
            Tab3Team tab3Team = new Tab3Team();
            Tab4Profile tab4Profile = new Tab4Profile();
            Tab5Settings tab5Settings = new Tab5Settings();
            //Returning the current tab
            Fragment f = null;
            switch(position){
                case 0:
                    if(viewChanger!=0){
                        viewChangerSwitchON = true;
                        f = tab1List_cardView;
                    }else{
                        viewChangerSwitchON = false;
                        f = tab1List;
                    }
                    break;
                case 1:
                    f = tab2Notification;
                    break;
                case 2:
                    f = tab3Team;
                    break;
                case 3:
                    f = tab4Profile;
                    break;
                case 4:
                    f = tab5Settings;
                    Bundle b = new Bundle();
                    b.putBoolean("ViewChangerIsON", viewChangerSwitchON);
                    if(themeChanger!=0){
                        themeChangerSwitchON = true;
                    }else{
                        themeChangerSwitchON = false;
                    }
                    b.putBoolean("ThemeChangerIsON", themeChangerSwitchON);
                    f.setArguments(b);
                    break;
            }
            curFrags.put(position,f);
            return f;
        }

        public Fragment getExistingFragment(int pos){
            return curFrags.get(pos);
        }

        @Override
        public int getCount() {
            return 5;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            super.destroyItem(container, position, object);
        }

        @Override
        public void startUpdate(ViewGroup container) {
            super.startUpdate(container);
        }
    }

    public static int getViewChanger() {
        return viewChanger;
    }

    public static void setViewChanger(int viewChanger) {
        MainActivity.viewChanger = viewChanger;
    }
}
