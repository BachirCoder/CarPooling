package com.bachir.owner.blayes;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.client.Firebase;

import java.util.HashMap;

/**
 * Created by Owner on 5/3/2017.
 */

public class CancelDialogFragment extends DialogFragment {
    private static Firebase child;
    private static Firebase refNot = new Firebase(Constants.FIREBASE_URL_NOT);
    private static String id = "";
    private static Trip post;
    private static String useremail;
    private static Button book, cancel;
    private static Context conn;
    private static Integer booked;
    /**
     * Public static constructor that creates fragment and passes a bundle with data into it when adapter is created
     */
    public static CancelDialogFragment newInstance(Context con, Firebase ref, Trip var, String user, String idd, Button bb, Button cc, Integer bookedN) {
        child = ref;
        post = var;
        useremail = user;
        id = idd;
        book = bb; cancel = cc;
        conn = con;
        booked = bookedN;
        CancelDialogFragment cancelListDialogFragment = new CancelDialogFragment();
        Bundle bundle = new Bundle();
        cancelListDialogFragment.setArguments(bundle);
        return cancelListDialogFragment;
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
                .setMessage("Êtes-vous sûr de vouloir annuler la réservation?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        cancelTrip();
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

    private void cancelTrip() {

        post.booked_append(-booked);
        //Log.d(LOG_TAG, post.getBook_list().toString());
        post.remBook_list(useremail);
        child.child("booked").setValue(post.getBooked());
        child.child("book_list").setValue(post.getBook_list());

        Toast.makeText(conn, "Réservation annulée.",
                Toast.LENGTH_LONG).show();
        HashMap<String, String> cancell = new HashMap<String, String>();
        cancell.put("post", id);
        cancell.put("token", post.getToken());
        refNot.child("cancelBook").setValue(null);
        refNot.child("cancelBook").setValue(cancell);
        cancel.setVisibility(Button.GONE);
        book.setVisibility(Button.VISIBLE);

    }
}
