package com.bachir.owner.blayes;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.firebase.client.Firebase;

/**
 * Created by Owner on 11/30/2016.
 */
public class RemoveTripDialogFragment extends DialogFragment {

    private static Firebase child;
    private static Context conn;
    /**
     * Public static constructor that creates fragment and passes a bundle with data into it when adapter is created
     */
    public static RemoveTripDialogFragment newInstance(Context con, Firebase ref) {
        child = ref;
        conn = con;
        RemoveTripDialogFragment removeListDialogFragment = new RemoveTripDialogFragment();
        Bundle bundle = new Bundle();
        removeListDialogFragment.setArguments(bundle);
        return removeListDialogFragment;
    }

    /**
     * Initialize instance variables with data from bundle
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.Base_Theme_AppCompat_Dialog_Alert)
                .setTitle("Confirmation")
                .setMessage("Êtes-vous certain de vouloir supprimer cette annonce?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        removeTrip();
                        Intent intent = new Intent(getActivity(), TripList.class);
                        startActivity(intent);
                        /* Dismiss the dialog */
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        /* Dismiss the dialog */
                        dialog.dismiss();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert);

        return builder.create();
    }

    private void removeTrip() {
    child.removeValue();
        Toast.makeText(conn, "Vous avez supprimé votre annonce avec succès.",
                Toast.LENGTH_LONG).show();
    }

}
