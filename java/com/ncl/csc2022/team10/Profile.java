/**
 * Class Description    : Display User's information based on userType of the one who view it
 * Contributors         : Elise Hosu, Eko Manggaprouw, Joseph Heath
 */
package com.ncl.csc2022.team10;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.ncl.csc2022.team10.adapters.Document_RecyclerViewAdapter;

import net.karthikraj.shapesimage.ShapesImage;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.ncl.csc2022.team10.MainActivity.themeChanger;
public class Profile extends AppCompatActivity implements SubmissionDialog_DeleteDocument.SubmissionDialogListener{

    // Initialise all necessary variables
    private static final String TAG = "Profile";
    private Context context;
    static ServiceCaller _serviceCaller;
    private TextView employeeName;
    private TextView employeeTitle;
    private ShapesImage profilePicture;
    private Button select;
    private Document_RecyclerViewAdapter adapter;
    private String filePath;
    private static final int REQUEST_CODE = 43; //Can keep any request code
    private static final String PREF_NAME = "Team10";
    private User u;
    private List<Document> documents = new ArrayList<Document>();
    public SharedPreferences mPrefs;

    /**
     * Setting up methods called when class first created
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
        context = this;
        setContentView(R.layout.splash);
        Intent i = getIntent();
        mPrefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        _serviceCaller = new ServiceCaller(mPrefs.getString("sessionToken", null));
        this.populateUser(this, i.getStringExtra("id"));
    }

    /**
     * Delete documents
     * @param fileName Name of the documents
     */
    @Override
    public void onDeleteUserClicked(String fileName){
        for(Document d : documents){
            if(d.getName().toLowerCase().equals(fileName.toLowerCase())){
                documents.remove(d);
                break;
            }
        }
    }

    /**
     * Initialise Admin View
     * @param u Current User viewed
     */
    public void finishUpdateProfileAdmin(final User u){
        this.u = u;
        final Activity a = this;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setContentView(R.layout.tab4user_profile_full);
                Date test;
                SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                SimpleDateFormat output = new SimpleDateFormat("dd/MM/yyyy");
                String s = null;
                if(u.dateOfBirth != null){
                    try{
                        test = input.parse(u.dateOfBirth);
                        s = output.format(test);
                    }
                    catch(ParseException pe){
                        Log.e("profileParseException", pe.toString());
                    }
                }

                if(u.profilePicture != null){
                    ((ImageView)findViewById(R.id.Picture_Holder) ).setImageBitmap(u.getBitmapProfilePicture(u.profilePicture));
                }
                ((TextView)findViewById(R.id.EmployeeName) ).setText(u.name);
                ((TextView)findViewById(R.id.EmployeeTitle) ).setText(u.role);
                ((TextView)findViewById(R.id.Mobile) ).setText(u.mobile);
                ((TextView)findViewById(R.id.Birthday) ).setText(s);
                ((TextView)findViewById(R.id.Email) ).setText(u.email);
                ((TextView)findViewById(R.id.Address) ).setText(u.address);
                ((TextView)findViewById(R.id.VisaStatus) ).setText(u.visaStatus);
                ((TextView)findViewById(R.id.NextOfKin) ).setText(u.nextOfKin1);
                ((TextView)findViewById(R.id.Nationality) ).setText(u.nationality);
                ((TextView)findViewById(R.id.Gender) ).setText(u.gender);
                ((TextView)findViewById(R.id.MedicalStatus) ).setText(u.medicalStatus);
                ((TextView)findViewById(R.id.NextOfKin2) ).setText(u.nextOfKin2);
                ((TextView)findViewById(R.id.UserLanguage) ).setText(u.getLanguagesString());
                ((TextView)findViewById(R.id.MaritalStatus) ).setText(u.maritalStatus);
                if(u.getSkills() == null){
                    ((TextView)findViewById(R.id.Skills) ).setText("");
                }else{
                    ((TextView)findViewById(R.id.Skills) ).setText(u.getSkillsString());
                }

                for(String doc : u.documents) {
                    documents.add(new Document(doc, null));
                }
                RecyclerView recyclerView = findViewById(R.id.documentRecyclerView);
                adapter = new Document_RecyclerViewAdapter(context, documents, _serviceCaller, u.userID.toString(), a);

                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(context));

                ImageButton edit = findViewById(R.id.editProfile);
                edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, EditableProfile.class);
                        intent.putExtra("UserId", u.getUserID());
                        intent.putExtra("Name", u.getName());
                        intent.putExtra("Role", u.role);
                        intent.putExtra("Mobile", u.getMobile());
                        intent.putExtra("Birthday", u.dateOfBirth);
                        intent.putExtra("Email", u.email);
                        intent.putExtra("Address", u.getAddress());
                        intent.putExtra("Visa", u.getVisaStatus());
                        intent.putExtra("NextOfKin1", u.getNextOfKin1());
                        intent.putExtra("Nationality", u.getNationality());
                        intent.putExtra("Gender", u.getGender());
                        intent.putExtra("MedicalStatus", u.getMedicalStatus());
                        intent.putExtra("NextOfKin2", u.getNextOfKin2());
                        intent.putExtra("Languages", u.getLanguagesString());
                        intent.putExtra("MaritalStatus", u.getMaritalStatus());
                        intent.putExtra("Skills", u.getSkillsString());

                        context.startActivity(intent);
                    }
                });

                select = findViewById(R.id.getFile);
                select.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        selectFile();
                    }
                });
            }
        });
    }

    /**
     * Initialise Management View
     * @param u Current User viewed
     */
    public void finishUpdateProfileManager(final User u){
        this.u = u;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                setContentView(R.layout.tab4user_profile_partial);
                Date test;
                SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                SimpleDateFormat output = new SimpleDateFormat("dd/MM/yyyy");
                String s = null;
                if(u.dateOfBirth != null){
                    try{
                        test = input.parse(u.dateOfBirth);
                        s = output.format(test);
                    }
                    catch(ParseException pe){
                        Log.e("profileParseException", pe.toString());
                    }
                }
                ((TextView)findViewById(R.id.EmployeeName) ).setText(u.name);
                ((TextView)findViewById(R.id.EmployeeTitle) ).setText(u.role);
                ((TextView)findViewById(R.id.Mobile) ).setText(u.mobile);
                ((TextView)findViewById(R.id.Birthday) ).setText(s);
                ((TextView)findViewById(R.id.Email) ).setText(u.email);
                ((TextView)findViewById(R.id.Address) ).setText(u.address);
                ((TextView)findViewById(R.id.NextOfKin) ).setText(u.nextOfKin1);
                ((TextView)findViewById(R.id.NextOfKin2) ).setText(u.nextOfKin2);
                if(u.getSkills() == null){
                    ((TextView)findViewById(R.id.Skills) ).setText("");
                }else{
                    ((TextView)findViewById(R.id.Skills) ).setText(u.getSkillsString());
                }
            }
        });
    }

    /**
     * Initialise Basic User View
     * @param u Current User viewed
     */
    public void finishUpdateProfileBasic(final User u){
        this.u = u;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                setContentView(R.layout.tab4user_profile_basic);
                Date test;
                SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                SimpleDateFormat output = new SimpleDateFormat("dd/MM/yyyy");
                String s = null;
                if(u.dateOfBirth != null){
                    try{
                        test = input.parse(u.dateOfBirth);
                        s = output.format(test);
                    }
                    catch(ParseException pe){
                        Log.e("profileParseException", pe.toString());
                    }
                }

                if(u.profilePicture != null){
                    ((ImageView)findViewById(R.id.Picture_Holder) ).setImageBitmap(u.getBitmapProfilePicture(u.profilePicture));
                }
                ((TextView)findViewById(R.id.EmployeeName) ).setText(u.name);
                ((TextView)findViewById(R.id.EmployeeTitle) ).setText(u.role);
                ((TextView)findViewById(R.id.Mobile) ).setText(u.mobile);
                String birthday = s;
                if(s != null){
                    ((TextView)findViewById(R.id.Birthday) ).setText(birthday.substring(0,birthday.length()-5));
                }
                else{
                    ((TextView)findViewById(R.id.Birthday) ).setText("");
                }
                if(u.getSkills() == null){
                    ((TextView)findViewById(R.id.Skills) ).setText("");
                }else{
                    ((TextView)findViewById(R.id.Skills) ).setText(u.getSkillsString());
                }
            }
        });
    }

    /**
     * Populate all users information
     * @param p User's profile
     * @param userId User's ID
     */
    public void populateUser(final Profile p,final String userId){
        new Thread(new Runnable() {
            @Override
            public void run() {
                ObjectMapper objectMapper = new ObjectMapper();
                User u = null;

                try {
                    Log.d("UserCallerProfile", "Attempting to read " + userId);
                    String s = _serviceCaller.getObjects("ViewUser", new UserIDModel(userId));

                    Log.d("UserCallerProfile", "Attempting to read " + s);
                    if(s != null){
                        u = objectMapper.readValue(s, User.class);
                        JsonNode languages = objectMapper.readTree(s).get("languages");
                        JsonNode skills = objectMapper.readTree(s).get("skills");
                        ObjectReader reader = objectMapper.readerFor(new TypeReference<List<String>>() {
                        });
                        u.languages = reader.readValue(languages);
                        u.skills = reader.readValue(skills);
                        Log.d("Year", "Attempting to read " + u.languages);
                    }
                }
                catch (Exception ioe){
                    Log.e("UserCallerProfile","GetProfile didn't work: " + ioe);
                }


                Log.d("currentUserTeam", "currentUserTeamID: " +  mPrefs.getString("teamID", null));
                String currentUserTeamID = mPrefs.getString("teamID", null);
                if(MainActivity.getUserType().equals("Admin")){
                    Log.d("UserTypeCalled", "Admin");
                    p.finishUpdateProfileAdmin(u);
                }else if(MainActivity.getUserType().equals("Manager") && u.teamID != null && u.teamID.toString().equals(currentUserTeamID)){
                    Log.d("UserTypeCalled", "Manager");
                    p.finishUpdateProfileManager(u);
                }else{
                    Log.d("UserTypeCalled", "User " + u.teamID);
                    p.finishUpdateProfileBasic(u);
                }
            }
        }).start();

    }

    /**
     * Selecting document type from device storage
     */
    private void selectFile(){
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("application/pdf"); //PDF Files
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, REQUEST_CODE);
    }

    /**
     * Handles the result from selecting document file
     * @param requestCode Request code passed in
     * @param resultCode Result code passed in
     * @param data Intent's data passed in
     */
    @Override //Whatever selected after this method will be called and result is stored in parameter of onActivityResult having datatype Intent
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_CODE && resultCode== Activity.RESULT_OK){
            if(data != null){
                Uri uri = data.getData();
                //s((MainActivity)getActivity())._serviceCaller.getObjects()
                filePath = uri.getPath().toString();

                Calendar c= Calendar.getInstance();
                String currentDate = DateFormat.getDateInstance().format(c.getTime());


                final ServiceCaller sc = _serviceCaller;
                try {
                    ContentResolver cr = getContentResolver();
                    final String fileName = getFileName(uri, cr);
                    final InputStream is = cr.openInputStream(uri);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {

                            sc.UploadDocument(u.userID,is,fileName);
                        }
                    }).start();
                } catch(Exception e){
                    Log.e("Tab4Profile",e.toString());
                }

                documents.add(new Document(getDocumentTitle(uri), currentDate));

                Toast.makeText(context,"Selecting file: "+filePath,Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Get file name
     * @param uri Uri path
     * @param cr Content Resolver
     * @return new String
     */
    public static String getFileName(Uri uri, ContentResolver cr) {
        String result;

        //if uri is content
        if (uri.getScheme() != null && uri.getScheme().equals("content")) {
            Cursor cursor = cr.query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    //local filesystem
                    int index = cursor.getColumnIndex("_data");
                    if(index == -1)
                        //google drive
                        index = cursor.getColumnIndex("_display_name");
                    result = cursor.getString(index);
                    if(result != null)
                        uri = Uri.parse(result);
                    else
                        return null;
                }
            } finally {
                cursor.close();
            }
        }

        result = uri.getPath();

        //get filename + ext of path
        int cut = result.lastIndexOf('/');
        if (cut != -1)
            result = result.substring(cut + 1);
        return result;
    }

    /**
     * Get document file name as shown in the device storage
     * @param uri URi path
     * @return Document string name
     */
    public String getDocumentTitle(Uri uri) {
        String documentTitle = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    documentTitle = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (documentTitle == null) {
            documentTitle = uri.getPath();
            int cut = documentTitle.lastIndexOf('/');
            if (cut != -1) {
                documentTitle = documentTitle.substring(cut + 1);
            }
        }
        return documentTitle;
    }
}
