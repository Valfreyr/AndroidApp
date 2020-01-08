/**
 * Class Description    : Class which add new user details to the database
 * Contributors         : Elise Hosu, Eko Manggaprouw
 */
package com.ncl.csc2022.team10;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import net.karthikraj.shapesimage.ShapesImage;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static com.ncl.csc2022.team10.MainActivity.themeChanger;

public class AddUser extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{

    // Initialising all variables needed for AddUser class
    private static final String TAG = "AddUser";
    private Context context;
    private String filePath;
    private static final int PICK_IMAGE = 43; //Can keep any request code
    static ServiceCaller _serviceCaller = null;
    private static final String PREF_NAME = "Team10";
    public SharedPreferences mPrefs;
    private ShapesImage profilePicture;
    private EditText name, role, phoneNumber, birthday, email, address, visa, nok, nationality, gender, medStat, nok2, languages, maritalStatus;
    private String mName, mRole, mPhoneNumber, mBirthday, mEmail, mAddress, mVisa, mNok, mNationality, mGender, mMedStat, mNok2, mLanguages, mMaritalStatus, mImage;


    /**
     * Function called when AddUser is first created
     * @param savedInstanceState Any Bundle passed in or current saved Instance state
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
        setContentView(R.layout.add_user);

        // Getting current service caller for server function
        mPrefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        _serviceCaller = new ServiceCaller(mPrefs.getString("sessionToken", null));

        // Setting up layout aspects
        bindLayout();
        setTexts();
        //After changes have been made
        getTexts(); //Values will not change if not edited
    }

    /**
     * Binds all layout properties to their listener
     */
    private void bindLayout() {
        // Bind user name and its listener
        name = findViewById(R.id.EmployeeName);
        name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    mName = name.getText().toString();
                }

            }
        });

        // Bind user role and its listener
        role = findViewById(R.id.EmployeeTitle);
        role.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    mRole = role.getText().toString();
                }

            }
        });

        // Binds user phone number and its listener
        phoneNumber = findViewById(R.id.Mobile);
        phoneNumber.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    mPhoneNumber = phoneNumber.getText().toString();
                }

            }
        });

        // Binds user date of birth and its listener
        birthday = findViewById(R.id.Birthday);
        birthday.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    mBirthday = birthday.getText().toString();
                }else{
                    DialogFragment datePicker = new DatePickerFragment();
                    datePicker.show(getSupportFragmentManager(), "date picker");
                }

            }
        });

        // Binds user email and its listener
        email = findViewById(R.id.Email);
        email.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    mEmail = email.getText().toString();
                }

            }
        });

        // Binds user address and its listener
        address = findViewById(R.id.Address);
        address.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    mAddress = address.getText().toString();
                }

            }
        });

        // Binds user visa to its listener
        visa = findViewById(R.id.VisaStatus);
        visa.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    mVisa = visa.getText().toString();
                }

            }
        });

        // Binds user first  next of kin and its listener
        nok = findViewById(R.id.NextOfKin);
        nok.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    mNok = nok.getText().toString();
                }

            }
        });

        // Binds user nationality and its listener
        nationality = findViewById(R.id.Nationality);
        nationality.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    mNationality = nationality.getText().toString();
                }

            }
        });

        // Binds user gender and its listener
        gender = findViewById(R.id.Gender);
        gender.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    mGender = gender.getText().toString();
                }

            }
        });

        // Binds user medical status and its listener
        medStat = findViewById(R.id.MedicalStatus);
        medStat.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    mMedStat = medStat.getText().toString();
                }

            }
        });

        // Binds user second next of kin and its listener
        nok2 = findViewById(R.id.NextOfKin2);
        nok2.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    mNok2 = nok2.getText().toString();
                }

            }
        });

        // Binds user language and its listener
        languages = findViewById(R.id.UserLanguage);
        languages.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    mLanguages = languages.getText().toString();
                }

            }
        });

//        maritalStatus = findViewById(R.id.MaritalStatus);
//        maritalStatus.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//
//            public void onFocusChange(View v, boolean hasFocus) {
//                if(!hasFocus) {
//                    mMaritalStatus = maritalStatus.getText().toString();
//                }
//
//            }
//        });

        // Binds user profile picture and its listener
        profilePicture = findViewById(R.id.Picture_Holder);
        profilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectFile();
            }
        });
    }

    /**
     * setting up current texts to be displayed on the layout
     */
    private void setTexts(){
        name.setText(mName);
        role.setText(mRole);
        phoneNumber.setText(mPhoneNumber);
        birthday.setText(mBirthday);
        email.setText(mEmail);
        address.setText(mAddress);
        visa.setText(mVisa);
        nok.setText(mNok);
        nationality.setText(mNationality);
        gender.setText(mGender);
        medStat.setText(mMedStat);
        nok2.setText(mNok2);
        languages.setText(mLanguages);
//        maritalStatus.setText(mMaritalStatus);
    }

    /**
     * Getting current texts from all user fields
     */
    private void getTexts(){
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
//        mMaritalStatus = maritalStatus.getText().toString();
    }

    /**
     * Perform server function once a new user is about to be added
     * @param view View called by the layout
     */
    public void saveClicked(View view){
        //Create new user using server function
        //Server function
        getTexts();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String bd = null;
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

                    if(mBirthday != null && !mBirthday.isEmpty()){
                        Date date = new Date(mBirthday);
                        bd = sdf.format(date);
                    }

                    _serviceCaller.getObjects("AddUser", new AddUserModel(mName, mRole, mPhoneNumber, bd, "User",
                            mAddress, mEmail, mNok, mNok2, mMaritalStatus, mNationality, mVisa, mGender, mMedStat, null, null, null, "1234"));
                }catch (IllegalArgumentException e){
                    Log.e("AddUserE", e.toString());
                }
            }
        }).start();
        this.finish();
        Toast.makeText(this, "User added!", Toast.LENGTH_SHORT).show();
    }

    /**
     * Finish the activity once cancel button is clicked
     * @param view View called by the layout
     */
    public void cancelClicked(View view){
        this.finish();
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
        String format2 = day+"/"+(month+1)+"/"+year;
        birthday.setText(format2);
    }

    /**
     * Select image file from device storage for user profile picture
     */
    private void selectFile(){
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
    @Override //Whatever selected after this method will be called and result is stored in parameter of onActivityResult having datatype Intent
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE && resultCode== Activity.RESULT_OK){
            if(data != null){
                Uri uri = data.getData();
                //s((MainActivity)getActivity())._serviceCaller.getObjects()
                filePath = uri.getPath();
                profilePicture.setImageURI(uri);
                mImage = uri.toString();
                Toast.makeText(context,"Selecting file: "+uri.toString(),Toast.LENGTH_SHORT).show();
            }
        }
    }
}
