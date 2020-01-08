/**
 * Class Description    : Tab 4, All information on the current logged in user
 * Contributors         : Elise Hosu, Eko Manggaprouw, Joseph Heath
 */
package com.ncl.csc2022.team10;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ncl.csc2022.team10.adapters.Document_RecyclerViewAdapter;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Tab4Profile extends android.support.v4.app.Fragment {

    //Variables needed
    private static final String TAG = "Tab4Profile";
    private Context context;
    private TextView name, role, mobile, birthday, email, address, visa, nok, nationality, gender, medical, nok2, languages, maritalStatus;
    private Button select;
    public List<Document> documents;
    private Employee USER;
    private String filePath;
    private User u;
    private Document_RecyclerViewAdapter adapter;
    private static final int REQUEST_CODE = 43; //Can keep any request co

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab4user_profile_full, container, false);
        context = this.getContext();

        // Get current user
        u = ((MainActivity)this.getActivity()).user;

        // Display current user's info
        if(u != null){
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


            ((TextView)rootView.findViewById(R.id.EmployeeName) ).setText(u.name);
            ((TextView)rootView.findViewById(R.id.EmployeeTitle) ).setText(u.role);
            ((TextView)rootView.findViewById(R.id.Mobile) ).setText(u.mobile);
            ((TextView)rootView.findViewById(R.id.Birthday) ).setText(s);
            ((TextView)rootView.findViewById(R.id.Email) ).setText(u.email);
            ((TextView)rootView.findViewById(R.id.Address) ).setText(u.address);
            ((TextView)rootView.findViewById(R.id.VisaStatus) ).setText(u.visaStatus);
            ((TextView)rootView.findViewById(R.id.NextOfKin) ).setText(u.nextOfKin1);
            ((TextView)rootView.findViewById(R.id.Nationality) ).setText(u.nationality);
            ((TextView)rootView.findViewById(R.id.Gender) ).setText(u.gender);
            ((TextView)rootView.findViewById(R.id.MedicalStatus) ).setText(u.medicalStatus);
            ((TextView)rootView.findViewById(R.id.NextOfKin2) ).setText(u.nextOfKin2);
            ((TextView)rootView.findViewById(R.id.UserLanguage) ).setText(u.getLanguagesString());
            ((TextView)rootView.findViewById(R.id.MaritalStatus) ).setText(u.getMaritalStatus());
            // Setting default profile image if it was not set by user before
            if(u.profilePicture != null){
                ((ImageView)rootView.findViewById(R.id.Picture_Holder) ).setImageBitmap(u.getBitmapProfilePicture(u.profilePicture));
            }

            if(u.getSkills() == null){
                ((TextView)rootView.findViewById(R.id.Skills) ).setText("");
            }else{
                ((TextView)rootView.findViewById(R.id.Skills) ).setText(u.getSkillsString());
            }

        }

        // Initialise selecting file listener
        select = rootView.findViewById(R.id.getFile);
        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectFile();
            }
        });
        documents = new ArrayList<Document>();
        for(String doc : u.documents) {
            documents.add(new Document(doc, null));
        }
        initialiseRecyclerView(rootView, u.userID.toString());

        // Initialise and prepare variables to be sent to edit profile section
        ImageButton edit = rootView.findViewById(R.id.editProfile);
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
//                intent.putExtra("ProfilePicture", u.getBitmapProfilePicture(u.profilePicture));

                context.startActivity(intent);
            }
        });

        return rootView;
    }


    /**
     * Initialising document adapter and layout manager
     * @param view Parent view
     */
    private void initialiseRecyclerView(View view, String userID) {
        Log.d(TAG, "initialiseRecyclerView: init recyclerview");

        RecyclerView recyclerView = view.findViewById(R.id.documentRecyclerView);
        adapter = new Document_RecyclerViewAdapter(this.getContext(), documents, ((MainActivity)getActivity())._serviceCaller, userID, getActivity());

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
    }

    /**
     * Function to start opening and selecting file from device storage to upload
     */
    private void selectFile(){
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("application/pdf"); //PDF Files
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, REQUEST_CODE);
    }

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


                final ServiceCaller sc = ((MainActivity)this.getActivity())._serviceCaller;
                try {
                    ContentResolver cr = ((MainActivity)this.getActivity()).getContentResolver();
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

                adapter.notifyDataSetChanged();
                Toast.makeText(context,"Selecting file: "+filePath,Toast.LENGTH_SHORT).show();
            }
        }
    }

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
     * Getting a specif document title from Uri filepath
     * @param uri URI where the file was selected
     * @return The name of the file as it is named in user's storage
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

