/**
 * Class Description    : Dialog class which shows up before submitting changes on team
 * Contributors         : Eko Manggaprouw
 */
package com.ncl.csc2022.team10;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.util.Log;

public class SubmissionDialog_Team extends AppCompatDialogFragment {

    private static final String TAG = "SubDialog_Team";

    private SubmissionDialogListener listener;

    /**
     * Displays a dialog message
     * @param savedInstanceState
     * @return
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("Submit changes?")
                .setMessage("All changes submitted must be approved by Admin first, before the changes you made appear on the team")
                .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.d(TAG, "Submit clicked");
                        listener.onSubmitClicked();
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.d(TAG, "Cancel clicked");
            }
        });

        return builder.create();
    }

    /**
     * Listener for submission dialog
     */
    public interface SubmissionDialogListener{
        void onSubmitClicked();
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
