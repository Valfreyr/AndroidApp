/**
 * Class Description    : Dialog class which shows up before deleting a team
 * Contributors         : Elise Hosu, Eko Manggaprouw
 */
package com.ncl.csc2022.team10;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.util.Log;

import com.ncl.csc2022.team10.EditableProfile;
import com.ncl.csc2022.team10.EditableTeam;
import com.ncl.csc2022.team10.ServiceCaller;
import com.ncl.csc2022.team10.Team;
import com.ncl.csc2022.team10.TeamIDModel;
import com.ncl.csc2022.team10.UserIDModel;

public class SubmissionDialog_DeleteTeam extends AppCompatDialogFragment {

    private static final String TAG = "SubDialog_Profile";
    private SubmissionDialogListener listener;
    static ServiceCaller _serviceCaller = null;

    /**
     * Server call to actually delete the team
     * Displays a dialog message
     * @param savedInstanceState
     * @return
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        _serviceCaller = EditableTeam._serviceCaller;
        builder.setTitle("Warning!")
                .setMessage("Deleting this team will also permanently delete all of this team's information from our server. This action is NOT reversible. ")
                .setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.d(TAG, "Cancel clicked");
                    }
                }).setNegativeButton("Delete Team", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, EditableTeam.teamID);
                        _serviceCaller.getObjects("DeleteTeam", new TeamIDModel(EditableTeam.teamID, null));
                    }
                }).start();
                Log.d(TAG, "Team deleted");
                listener.onDeleteTeamClicked();
            }
        });

        return builder.create();
    }

    /**
     * Listener for submission dialog
     */
    public interface SubmissionDialogListener{
        void onDeleteTeamClicked();
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
