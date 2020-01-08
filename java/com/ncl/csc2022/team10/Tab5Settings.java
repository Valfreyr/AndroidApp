/**
 * Class Description    : Tab 5, Application settings page
 * Contributors         : Elise Hosu, Eko Manggaprouw, Katarzyna Nozka
 */
package com.ncl.csc2022.team10;

import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import 	android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;
import android.content.Intent;
import android.widget.Switch;

//Class handling all Settings options
//Contributors: Kat
public class Tab5Settings extends Fragment  implements View.OnClickListener  {

    Switch viewSwitch;
    Switch themeSwitch;
    Boolean viewSwitchIsON;
    Boolean themeSwitchIsON;

    private static final String TAG = "AddTeam";
    static ServiceCaller _serviceCaller = null;
    private static final String PREF_NAME = "Team10";
    public SharedPreferences mPrefs;


    @Override
    //On create set up layout and initialise onClickListeners for each settings option
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.tab5settings, container, false);

        Bundle b = getArguments();
        viewSwitchIsON = b.getBoolean("ViewChangerIsON");
        themeSwitchIsON = b.getBoolean("ThemeChangerIsON");

        viewSwitch = rootView.findViewById(R.id.view_switch);
        viewSwitch.setChecked(viewSwitchIsON);
        viewSwitch.setOnClickListener(this);

        themeSwitch = rootView.findViewById(R.id.theme_switch);
        themeSwitch.setChecked(themeSwitchIsON);
        themeSwitch.setOnClickListener(this);


        TextView passReset = rootView.findViewById(R.id.change_password);
        passReset.setOnClickListener(this);

        TextView faq = rootView.findViewById(R.id.help);
        faq.setOnClickListener(this);

        Button signOut = rootView.findViewById(R.id.sign_out_button);
        signOut.setOnClickListener(this);

        return rootView;
    }

    // onClick methods for each Settings option
    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.change_password:
                Intent intent = new Intent(getActivity(), PasswordReset.class);
                startActivity(intent);
                this.getActivity().finish();
                break;

            case R.id.theme_switch:
                if(themeSwitch.isChecked()){
                    ThemeChanger.changeToTheme(this.getActivity(), ThemeChanger.THEME_DARK);
                    Toast.makeText(getActivity(),"Changed to Dark Theme",Toast.LENGTH_SHORT).show();
                }
                else{
                    ThemeChanger.changeToTheme(this.getActivity(), ThemeChanger.THEME_LIGHT);
                    Toast.makeText(getActivity(),"Changed to Light Theme",Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.view_switch:
                if(viewSwitch.isChecked()){
                    Intent i = new Intent(this.getActivity(), MainActivity.class);
                    i.putExtra("ViewChanger",1);
                    this.startActivity(i);
                    this.getActivity().finish();
                    Toast.makeText(getActivity(),"Changed to Card View",Toast.LENGTH_SHORT).show();
                }
                else{
                    Intent i = new Intent(this.getActivity(), MainActivity.class);
                    i.putExtra("ViewChanger", 0);
                    this.startActivity(i);
                    this.getActivity().finish();
                    Toast.makeText(getActivity(),"Changed to List View",Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.help:
                Intent goToHelpPage = new Intent(Intent.ACTION_VIEW);
                goToHelpPage.setData(Uri.parse("http://homepages.cs.ncl.ac.uk/2018-19/CSC2022/Team10/website/index.html#faq"));
                startActivity(goToHelpPage);
                break;


            case R.id.sign_out_button:

                _serviceCaller = MainActivity._serviceCaller;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, MainActivity.userID);
                        _serviceCaller.getObjects("Logout", new UserIDModel(MainActivity.userID));
                    }
                }).start();
                Intent signingOut = new Intent(getActivity(), LoginActivity.class);
                startActivity(signingOut);
                this.getActivity().finish();
                Toast.makeText(getActivity(),"You have been signed out",Toast.LENGTH_SHORT).show();
                break;

            default:
                break;
        }
    }


}
