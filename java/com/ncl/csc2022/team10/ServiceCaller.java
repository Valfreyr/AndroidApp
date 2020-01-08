/**
 * Class Description    : Class which call server services specified by methods
 * Contributors         : Elise Hosu
 */
package com.ncl.csc2022.team10;

import android.net.Uri;
import android.util.Log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

public class ServiceCaller {
    private User user;
    private String _sessionToken;

    // Base URL to the server
    private static final String functionsBase = "https://team10project.azurewebsites.net/api/";

    public ServiceCaller(String sessionToken) {
        _sessionToken = sessionToken;
    }

    public String getSessionToken() {
        return _sessionToken;
    }

    public void setSessionToken(String _sessionToken) {
        this._sessionToken = _sessionToken;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Method which calls the appropriate server function
     * @param s Name of the server functions
     * @param obj Object send to the server (usually one of the model classes)
     * @param <T> Type of the object
     * @return Either a 200 code which means the call was successful or the information requested as a JSON String
     */
    public <T> String getObjects(String s, T obj) {
        //make a new http request
        //call the service and add the session token
        if(obj!=null) {
            Log.d("ServiceCaller", "Beginning Get Objects With " + obj.toString());
        }
        String inputLine;
        URL u = null;
        HttpURLConnection con = null;
        try {
            u = new URL(functionsBase + s);
        } catch (MalformedURLException mue) {
            Log.e("ServiceCaller", mue.toString());
        }

        try {
            con = (HttpURLConnection) u.openConnection();
        } catch (IOException ioe) {
            Log.e("ServiceCallerIOE", ioe.toString());
        }

        if (con != null) {
            try {
                con.setRequestProperty("Content-Type", "application/json");
                con.setRequestMethod("POST");
                con.setRequestProperty("sessionToken", _sessionToken);

                Log.d("ServiceCaller", "Making Request with session token" + _sessionToken);
                if(obj!=null) {
                    con.setDoOutput(true);
                    OutputStream os = con.getOutputStream();
                    OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");
                    String json = toJSON(obj);

                    osw.write(json);
                    osw.flush();
                    osw.close();
                    Log.d("ServiceCaller", "Making Request with json: " + json);
                }
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(con.getInputStream()));
                StringBuffer content = new StringBuffer();
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
                in.close();

                Log.d("ServiceCaller", "Request Successful (" + s + ")" + content.toString());
                if (con.getResponseCode() == 200) {
                    con.disconnect();

                    return content.toString();
                }
            } catch (Exception pe) {
                Log.e("ServiceCaller", pe.toString());
            }
        }
        if(con!=null)con.disconnect();
        return null;
    }

    /**
     * Method to convert different objects to JSON
     * @param obj Object
     * @return A JSON string
     */
    public static String toJSON(Object obj) {
        ObjectMapper mapper = new ObjectMapper();
        String jsonInString = null;
        try {
            jsonInString = mapper.writeValueAsString(obj);
        } catch (JsonProcessingException jpe) {
            Log.e("ServiceCaller",jpe.toString());
        } catch (NullPointerException npe){

            Log.e("ServiceCaller",npe.toString());
        }
        return jsonInString;
    }

    /**
     * Method to upload document
     * @param id User's ID
     * @param is Input stream
     * @param fileName File name
     */
    public void UploadDocument(Integer id, InputStream is, String fileName){
        UploadDocument(id, is, fileName, false);
    }

    /**
     * Method to upload the document to the blob storage
     * @param id User's ID
     * @param is Input Stream
     * @param fileName File name
     * @param isProfilePicture true = file is profile picture, false = file is normal document
     */
    public void UploadDocument(Integer id, InputStream is, String fileName, boolean isProfilePicture){
        Log.d("ServiceCaller", "Attempting to upload file...");
        try {
            CloseableHttpClient httpClient = HttpClients.createDefault();

            Log.d("ServiceCaller", "Made my client");
            HttpPost uploadFile = new HttpPost(functionsBase + "UploadDocument");
            uploadFile.addHeader("sessionToken", _sessionToken);
            Log.d("ServiceCaller", "Made my post");
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            Log.d("ServiceCaller", "Made my builder");
            builder.addTextBody("id", id.toString(), ContentType.TEXT_PLAIN);
            builder.addTextBody("isProfilePicture", Boolean.toString(isProfilePicture), ContentType.TEXT_PLAIN);
            Log.d("ServiceCaller", "Attempting to add file..." + " is is null? " + Boolean.toString(is==null));
            builder.addBinaryBody(
                    "file",
                    is,
                    ContentType.APPLICATION_OCTET_STREAM,
                    fileName
            );

            Log.d("ServiceCaller", "Attempting to build...");
            HttpEntity multipart = builder.build();
            uploadFile.setEntity(multipart);
            CloseableHttpResponse response = httpClient.execute(uploadFile);
            HttpEntity responseEntity = response.getEntity();
            Log.d("ServiceCaller", "Response status: " +
                    Integer.toString((response.getStatusLine().getStatusCode())));
        } catch (Exception e) {
            Log.e("ServiceCaller", e.toString());
        }
    }

}



