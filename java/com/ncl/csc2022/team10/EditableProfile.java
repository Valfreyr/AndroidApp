/**
 * Class Description    : Editable user profile that holds editable fields before submitting changes to database
 * Contributors         : Elise Hosu, Eko Manggaprouw, Joseph Heath
 */
package com.ncl.csc2022.team10;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import net.karthikraj.shapesimage.ShapesImage;

import java.io.File;
import java.io.InputStream;
import java.sql.Array;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.*;

import static com.ncl.csc2022.team10.MainActivity.themeChanger;

public class EditableProfile extends AppCompatActivity implements SubmissionDialog_Profile.SubmissionDialogListener, SubmissionDialog_DeleteUser.SubmissionDialogListener, DatePickerDialog.OnDateSetListener {
    // Server variables
    static ServiceCaller _serviceCaller = null;
    private static final String PREF_NAME = "Team10";
    private static final String TAG = "EditableProfile";

    // Activity variables
    private Context context;
    public SharedPreferences mPrefs;

    // Layout related variables
    private ShapesImage profilePicture;
    private TextView deleteUser;
    private EditText name, role, phoneNumber, birthday, email, address, visa, nok, nationality, gender, medStat, nok2, languages, maritalStatus, skills;
    static Integer mUserId;
    //Strings for changed values
    private String mName, mRole, mPhoneNumber, mBirthday, mEmail, mAddress, mVisa, mNok, mNationality, mGender, mMedStat, mNok2, mLanguages, mMaritalStatus, mImage, mSkills;
    DatePickerDialog datePickerDialog;

    // Profile picture variables
    private String filePath;
    private static final int PICK_IMAGE = 43; //Can keep any request code


    /**
     * Setting up variables, called when EditableProfile class created
     * @param savedInstanceState Any Bundle passed in or the current saved instance state
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
        setContentView(R.layout.editable_profile);
        context = this;

        // Getting current service caller for server function
        mPrefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        _serviceCaller = new ServiceCaller(mPrefs.getString("sessionToken", null));

        // Delete user only available for Admin
        deleteUser = findViewById(R.id.deleteUser);
        if (!MainActivity.getUserType().equals("Admin")) {
            deleteUser.setVisibility(View.INVISIBLE);
        }

        // Binding all layout aspects
        bindLayout();

        // Getting all passed in user information for editing
        Intent intent = getIntent();
        mUserId = intent.getExtras().getInt("UserId");
        mName = intent.getExtras().getString("Name");
        mRole = intent.getExtras().getString("Role");
        mPhoneNumber = intent.getExtras().getString("Mobile");
        mBirthday = intent.getExtras().getString("Birthday");
        mEmail = intent.getExtras().getString("Email");
        mAddress = intent.getExtras().getString("Address");
        mVisa = intent.getExtras().getString("Visa");
        mNok = intent.getExtras().getString("NextOfKin1");
        mNationality = intent.getExtras().getString("Nationality");
        mGender = intent.getExtras().getString("Gender");
        mMedStat = intent.getExtras().getString("MedicalStatus");
        mNok2 = intent.getExtras().getString("NextOfKin2");
        mLanguages = intent.getExtras().getString("Languages");
        mMaritalStatus = intent.getExtras().getString("MaritalStatus");
        mImage = intent.getExtras().getString("ProfilePicture");
        mSkills = intent.getExtras().getString("Skills");

        // Setting up layout aspects
        setTexts();
        //After changes have been made
        getTexts(); //Values will not change if not edited

    }

    /**
     * Setting date texts from Date Picker pop up
     * @param datePicker Date picker object pop up fragment
     * @param year Year picked
     * @param month Month picked
     * @param day Day picked
     */
    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, day);
        String currentDateString = DateFormat.getDateInstance().format(c.getTime());
        String format2 = day + "/" + (month + 1) + "/" + year;
        birthday.setText(format2);
    }

    /**
     * Initialise Submission dialog when edited profile is about to be submitted
     * @param view View called by the current layout
     */
    public void saveClicked(View view) {
        SubmissionDialog_Profile dialog = new SubmissionDialog_Profile();
        dialog.show(getSupportFragmentManager(), TAG);
    }

    /**
     * Finish the EditableProfile activity once cancel button is clicked
     * @param view View called by the current layout
     */
    public void cancelClicked(View view) {
        this.finish();
    }

    /**
     * Admin function to delete the user viewed, and initialise Submission dialog
     * @param view View called by the current layout
     */
    public void deleteClicked(View view) {
        SubmissionDialog_DeleteUser dialog = new SubmissionDialog_DeleteUser();
        dialog.show(getSupportFragmentManager(), TAG);
    }

    /**
     * Binds all layout aspects for all user fields
     */
    private void bindLayout() {
        // Binds name fields and its listener
        name = findViewById(R.id.EmployeeName);
        name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    mName = name.getText().toString();
                }

            }
        });

        // Binds role field and its listener
        role = findViewById(R.id.EmployeeTitle);
        role.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    mRole = role.getText().toString();
                }

            }
        });

        // Binds phone number field and its listener
        phoneNumber = findViewById(R.id.Mobile);
        phoneNumber.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    mPhoneNumber = phoneNumber.getText().toString();
                }

            }
        });

        // Binds date of birth field and its listener
        birthday = findViewById(R.id.Birthday);
        birthday.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    mBirthday = birthday.getText().toString();
                } else {
                    DialogFragment datePicker = new DatePickerFragment();
                    datePicker.show(getSupportFragmentManager(), "date picker");
                }

            }
        });

        // Binds email field and its listener
        email = findViewById(R.id.Email);
        email.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    mEmail = email.getText().toString();
                }

            }
        });

        // Binds address field and its listener
        address = findViewById(R.id.Address);
        address.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    mAddress = address.getText().toString();
                }

            }
        });

        // Binds visa field and its listener
        visa = findViewById(R.id.VisaStatus);
        visa.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    mVisa = visa.getText().toString();
                }

            }
        });

        // Binds first next of kin and its listener
        nok = findViewById(R.id.NextOfKin);
        nok.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    mNok = nok.getText().toString();
                }

            }
        });

        // Binds nationality field and its listener
        nationality = findViewById(R.id.Nationality);
        nationality.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    mNationality = nationality.getText().toString();
                }

            }
        });

        // Binds gender fields and its listener
        gender = findViewById(R.id.Gender);
        gender.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    mGender = gender.getText().toString();
                }

            }
        });

        // Binds medical status field and its listener
        medStat = findViewById(R.id.MedicalStatus);
        medStat.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    mMedStat = medStat.getText().toString();
                }

            }
        });

        // Binds second next of kin field and its listener
        nok2 = findViewById(R.id.NextOfKin2);
        nok2.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    mNok2 = nok2.getText().toString();
                }

            }
        });

        // Binds language field and its listener
        languages = findViewById(R.id.UserLanguage);
        languages.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    mLanguages = languages.getText().toString();
                }

            }
        });

        // Binds marital status field and its listener
        maritalStatus = findViewById(R.id.MaritalStatus);
        maritalStatus.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    mMaritalStatus = maritalStatus.getText().toString();
                }

            }
        });

        // Binds profile picture field and its listener
        profilePicture = findViewById(R.id.Picture_Holder);
        profilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectFile();
            }
        });

        // Binds skills field and its listener
        skills = findViewById(R.id.Skills);
        skills.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    mSkills = skills.getText().toString();
                }

            }
        });
    }

    /**
     * setting up current texts to be displayed on the layout
     */
    private void setTexts() {
        name.setText(mName);
        role.setText(mRole);
        skills.setText(mSkills);
        phoneNumber.setText(mPhoneNumber);
        email.setText(mEmail);
        address.setText(mAddress);
        visa.setText(mVisa);
        nok.setText(mNok);
        nationality.setText(mNationality);
        gender.setText(mGender);
        medStat.setText(mMedStat);
        nok2.setText(mNok2);
        maritalStatus.setText(mMaritalStatus);
        languages.setText(mLanguages);
        Date test;
        SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        SimpleDateFormat output = new SimpleDateFormat("dd/MM/yyyy");
        String s = null;
        if(mBirthday != null){
            try{
                test = input.parse(mBirthday);
                s = output.format(test);
            }
            catch(ParseException pe){
                Log.e("profileParseException", pe.toString());
            }
        }

        birthday.setText(s);
    }

    /**
     * Getting current texts from all user fields
     */
    private void getTexts() {
        mName = name.getText().toString();
        mRole = role.getText().toString();
        mPhoneNumber = phoneNumber.getText().toString();
        mBirthday = birthday.getText().toString();
        mEmail = email.getText().toString();
        mAddress = address.getText().toString();
        mVisa = visa.getText().toString();
        mNok = nok.getText().toString();
        mNationality = nationality.getText().toString();
        mGender = gender.getText().toString();
        mMedStat = medStat.getText().toString();
        mNok2 = nok2.getText().toString();
        mLanguages = languages.getText().toString();
        mMaritalStatus = maritalStatus.getText().toString();
        mSkills = skills.getText().toString();
    }

    /**
     * Select image file from device storage for user profile picture
     */
    private void selectFile() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*"); //MIME data type files
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
    }

    /**
     * Handles the result from selecting image file
     * @param requestCode Request code passed in
     * @param resultCode Result code passed in
     * @param data Intent's data passed in
     */
    @Override
    //Whatever selected after this method will be called and result is stored in parameter of onActivityResult having datatype Intent
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE && resultCode== Activity.RESULT_OK){
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
                    // Run upload to server function
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            sc.UploadDocument(mUserId,is,fileName, true);
                        }
                    }).start();
                } catch(Exception e){
                    Log.e("Tab4Profile",e.toString());
                }

                // Set the selected image to the current profile image
                profilePicture.setImageURI(uri);
                mImage = uri.toString();
                Toast.makeText(context,"Selecting file: "+uri.toString(),Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Submit edited data to the server once submit button is clicked
     */
    @Override
    public void onSubmitClicked() {
        this.finish();
        getTexts();
        // Server function
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<String> languages = toList(mLanguages);
                List<String> skills = toList(mSkills);
                try {
                    String bd = null;
                    SimpleDateFormat output = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                    SimpleDateFormat input = new SimpleDateFormat("dd/MM/yyyy");
                    if (mBirthday != null && !mBirthday.isEmpty()) {
                        Date date = input.parse(mBirthday);
                        bd = output.format(date);
                    }
                    Log.e("testLanguages", languages.toString());
                    _serviceCaller.getObjects("AmendUser", new User(mUserId, mName, null, mRole, mPhoneNumber, bd, null, mAddress, mEmail, mNok, mNok2, mMaritalStatus, mNationality, mVisa, mGender, mMedStat, languages, skills, null, null, null,null));
                } catch (Exception e) {
                    Log.e("testBirthday", e.toString());
                }
            }
        }).start();
        Toast.makeText(this, "Changes have been submitted ", Toast.LENGTH_SHORT).show();
        //Amend user data function
    }

    /**
     * Change activity once the delete user button is clicked
     */
    @Override
    public void onDeleteUserClicked() {
        Intent i = new Intent(this, MainActivity.class);
        this.startActivity(i);
        Toast.makeText(this, "Account has been deleted ", Toast.LENGTH_SHORT).show();
    }

    /**
     * Change strings to list
     * @param s String to be turned into list of strings
     * @return
     */
    public List<String> toList(String s){
        List<String> sList = Arrays.asList(s.split(","));
        Log.e("toList", sList.toString());
        return sList;
    }

    /**
     * Get the profile picture's Bitmap from strings
     * @param profilePicture String representation of an image
     * @return Bitmap of the image passed in
     */
    public Bitmap getBitmapProfilePicture(String profilePicture) {
        Bitmap pf = null;
        if (profilePicture != null) {
            byte[] decodedString = Base64.decode(profilePicture, Base64.DEFAULT);
            pf = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length, null);
            Log.d("User", "setting pp " + pf.getByteCount());
        }
        return pf;
    }

    /**
     * Get file name
     * @param uri Uri path
     * @param cr Content Resolver
     * @return File name
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

}
