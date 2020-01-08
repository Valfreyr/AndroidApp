/**
 * Class Description    : Class which get details for new password and change the old password using server function
 * Contributors         : Elise Hosu, Eko Manggaprouw, Katarzyna Nozka
 */
package com.ncl.csc2022.team10;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.os.CountDownTimer;

import static com.ncl.csc2022.team10.MainActivity.themeChanger;

/**
 * Method to reset password
 */
public class PasswordReset extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "PasswordReset";
    private Context context;

    private EditText email, currentPassword, newPassword;
    private String mEmail, mCurrentPassword, mNewPassword;

    static ServiceCaller _serviceCaller = null;
    private static final String PREF_NAME = "Team10";
    public SharedPreferences mPrefs;

    /**
     * Getting the cached variables
     * Setting the ui
     * @param savedInstanceState
     */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null)
        {
            themeChanger = bundle.getInt("ThemeChanger");
        }
        ThemeChanger.onActivityCreateSetTheme(this, themeChanger);

        setContentView(R.layout.password_reset);
        context = this;
        mPrefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        _serviceCaller = new ServiceCaller(mPrefs.getString("sessionToken", null));

        bindLayout();

        getTexts();

        Button reset = findViewById(R.id.reset_button);
        reset.setOnClickListener(this);

    }

    /**
     * Calling the server function to change password when submit button has been pressed
     * @param v
     */
    public void onClick(View v) {
        //Server code here
        getTexts();
        new Thread(new Runnable() {
            @Override
            public void run() {
                _serviceCaller.getObjects("ChangePassword", new ChangePasswordModel(mEmail, mCurrentPassword, mNewPassword));
            }
        }).start();
        Toast.makeText(PasswordReset.this, "Your password change request has been send. An admin will now review your request", Toast.LENGTH_SHORT).show();
    }

    private void bindLayout(){
        email = findViewById(R.id.email);
        currentPassword = findViewById(R.id.current_password);
        newPassword  = findViewById(R.id.new_password);
    }

    private void getTexts(){
        mEmail = email.getText().toString();
        mCurrentPassword = currentPassword.getText().toString();
        mNewPassword = newPassword.getText().toString();
    }
}