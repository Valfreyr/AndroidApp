/**
 * Class Description    : Dialog class which shows up before deleting a user
 * Contributors         : Elise Hosu, Eko Manggaprouw
 */
package com.ncl.csc2022.team10;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.util.Log;

import java.util.Arrays;
import java.util.List;

public class SubmissionDialog_DeleteUser extends AppCompatDialogFragment {

    private static final String TAG = "SubDialog_Profile";
    private SubmissionDialogListener listener;

    static ServiceCaller _serviceCaller = null;

    /**
     * Server call to actually delete the user
     * Displays a dialog message
     * @param savedInstanceState
     * @return
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        _serviceCaller = EditableProfile._serviceCaller;
        builder.setTitle("Warning!")
                .setMessage("Deleting this account will also permanently delete all of this account's data from our server. This action is NOT reversible. ")
                .setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.d(TAG, "Cancel clicked");
                    }
                }).setNegativeButton("Delete User", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, EditableProfile.mUserId.toString());
                        _serviceCaller.getObjects("DeleteUser", new UserIDModel(EditableProfile.mUserId.toString()));
                    }
                }).start();
                Log.d(TAG, "Account deleted");
                listener.onDeleteUserClicked();
            }
        });

        return builder.create();
    }

    /**
     * Listener for submission dialog
     */
    public interface SubmissionDialogListener{
        void onDeleteUserClicked();
    }

    /**
     * Checks if the class using submission dialogs implements SubmissionDialogListener
     * @param context
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (SubmissionDialogListener) context;

        } catch(ClassCastException e){
            throw new ClassCastException(context.toString() + "must implement SubmissionDialogListener");
        }

    }
}
