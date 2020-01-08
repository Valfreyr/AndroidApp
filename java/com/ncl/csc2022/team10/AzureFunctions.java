/**
 * Class Description    : Class which holds initial Azure function used for connecting to the server
 * Contributors         : Elise Hosu
 */
package com.ncl.csc2022.team10;

import android.app.Service;
import android.util.Log;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 Class used to log in by making a https request to the server
 */

public class AzureFunctions {
    ServiceCaller _serviceCaller;
    public AzureFunctions(){
        _serviceCaller = new ServiceCaller("");
    }

    /**
     * Method that passes the email and password to the server
     * @param email User's email
     * @param password User's password
     * @return User object, containing all the user details; including a session token
     */
    public LoginResponse attemptLogin(String email, String password){
        final String e = email;
        final String p = password;

        ObjectMapper objectMapper = new ObjectMapper();
        LoginResponse u;
        try{
            u = objectMapper.readValue(_serviceCaller.getObjects("AttemptLogin?code=Nl96bXfJLmQYQF34eQCuEAzW2iOTyu3U9bOHoqRaQsHC7vmoMcwj/g==", new LoginRequest(e,p)), LoginResponse.class); //?code=Nl96bXfJLmQYQF34eQCuEAzW2iOTyu3U9bOHoqRaQsHC7vmoMcwj/g==
            if(u!=null){
                return u;
            }
        }
        catch(Exception ioe){
            Log.d("AzureFunctions",ioe.toString());
        }
        return null;
    }
}
