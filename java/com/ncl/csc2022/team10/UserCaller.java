/**
 * Class Description    : Class that specifies functions for a particular user
 * Contributors         : Elise Hosu
 */
package com.ncl.csc2022.team10;
import android.app.Activity;
import android.content.SharedPreferences;
import android.util.Log;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class UserCaller {
    private ServiceCaller _serviceCaller;
    private MainActivity _activity;

    /**
     * Constructor for user caller
     * @param serviceCaller Service caller (see service caller class)
     * @param activity Main Activity
     */
    public UserCaller(ServiceCaller serviceCaller, MainActivity activity) {
        _serviceCaller = serviceCaller;
        _activity = activity;
    }

    /**
     * Method getting all the users
     */
    public void GetUsers() {
        ObjectMapper objectMapper = new ObjectMapper();
        List<User> users = null;
        try {
            String s = _serviceCaller.getObjects("ReturnAllUserScreen?code=1GezBXkc3BdHa7RF28JarOaSBWsk9RlxDfJz7zZFsBQD2jRKNSeZag==", null);
            Log.d("UserCaller", "Attempting to read " + s);
            ObjectMapper mapper = new ObjectMapper();
            ArrayNode node = (ArrayNode)mapper.readTree(s);
            users = new ArrayList<>();
            for(JsonNode jn : node){
                User u = mapper.convertValue(jn, User.class);
                JsonNode skills = jn.get("skills");
                ObjectReader reader = objectMapper.readerFor(new TypeReference<List<String>>() {
                });
                u.skills = reader.readValue(skills);
                users.add(u);
            }
        }
        catch (IOException ioe){
            Log.e("UserCaller","GetUsers return all users screen didn't work: " + ioe);
        }
        _activity.finishUpdateUsers(users);
    }

    /**
     * Method getting all notifications
     */
    public void GetNotifications(){
        ObjectMapper objectMapper = new ObjectMapper();
        List<Notification> notifications = null;
        try {
            String s = _serviceCaller.getObjects("ViewNotifications", null);
            Log.d("UserCaller", "Attempting to read " + s);
            notifications = Arrays.asList(objectMapper.readValue(s, Notification[].class));
        }
        catch (IOException ioe){
            Log.e("UserCaller","GetNotifications didn't work: " + ioe);
        }
        _activity.finishUpdateNotifications(notifications);
    }

    /**
     * Method getting all teams
     */
    public void GetTeams(){
        ObjectMapper objectMapper = new ObjectMapper();
        List<Team> teams = null;
        try {
            String s = _serviceCaller.getObjects("ReturnAllTeams", null);
            Log.d("UserCaller", "Attempting to read " + s);
            teams = Arrays.asList(objectMapper.readValue(s, Team[].class));
        }
        catch (IOException ioe){
            Log.e("UserCaller","GetTeams didn't work: " + ioe);
        }
        _activity.finishUpdateTeam(teams);
    }

    /**
     * Method getting the user profile
     * @param userID User's ID
     */
    public void GetProfile(String userID){
        ObjectMapper objectMapper = new ObjectMapper();
        User u = null;

        try {
            String s = _serviceCaller.getObjects("ViewUser", new UserIDModel(userID));
            Log.d("UserCallerProfile", "Attempting to read " + s);
            if(s != null)
                u = objectMapper.readValue(s, User.class);
            JsonNode languages = objectMapper.readTree(s).get("languages");
            JsonNode skills = objectMapper.readTree(s).get("skills");
            ObjectReader reader = objectMapper.readerFor(new TypeReference<List<String>>() {
            });
            u.languages = reader.readValue(languages);
            u.skills = reader.readValue(skills);
            Log.d("Year", "Attempting to read " + u.skills);
        }
        catch (IOException ioe){
            Log.e("UserCallerProfile","GetProfile didn't work: " + ioe);
        }
        _activity.finishUpdateProfile(u);
    }

    //Dependencies
    /*compile 'com.fasterxml.jackson.core:jackson-core:2.7.3'
compile 'com.fasterxml.jackson.core:jackson-annotations:2.7.3'
compile 'com.fasterxml.jackson.core:jackson-databind:2.7.3'\
*/
}
