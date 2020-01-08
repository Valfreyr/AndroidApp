/**
 * Class Description    : Dialog class which shows up before deleting user's document
 * Contributors         : Elise Hosu, Eko Manggaprouw
 */
package com.ncl.csc2022.team10;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.util.Log;

public class SubmissionDialog_DeleteDocument extends AppCompatDialogFragment {

    private static final String TAG = "SubDialog_Profile";
    private SubmissionDialogListener listener;

    static ServiceCaller _serviceCaller = null;
    private String userID;
    private String fileName;
    private ServiceCaller sc;

    /**
     * Method to populate the submission dialog variables
     * @param userID User's ID
     * @param fileName Document name
     * @param sc Service Caller(see service caller class)
     */
    public void populate(String userID, String fileName, ServiceCaller sc){
        this.userID = userID;
        this.fileName = fileName;
        this.sc = sc;
    }

    /**
     * Server calls to actually delete the document
     * Displays a dialog message
     * @param savedInstanceState
     * @return
     */
    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("Warning!")
                .setMessage("Your document will be deleted from our servers. This action is NOT reversible. ")
                .setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.d(TAG, "Cancel clicked");
                    }
                }).setNegativeButton("Delete Document", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        sc.getObjects("DeleteFile", new DownloadDocumentModel(userID, fileName));
                        listener.onDeleteUserClicked(fileName);
                    }
                }).start();
                Log.d(TAG, "Document deleted");
            }
        });

        return builder.create();
    }

    /**
     * Listener for submission dialog
     */
    public interface SubmissionDialogListener{
        void onDeleteUserClicked(String fileName);
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
            throw new ClassCastException(context.toString() + " must implement SubmissionDialogListener");
        }

    }
}
