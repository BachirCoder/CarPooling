package com.bachir.owner.blayes;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.HashMap;

import static com.bachir.owner.blayes.Utils.ValidatePhoneNumber;

public class Book extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAnalytics mFirebaseAnalytics;
    private static final String LOG_TAG = Book.class.getSimpleName();
    private Button book, cancel, seats;
    private Firebase refListName, refNot;
    private MyFirebaseInstanceIDService tokenVar;
    private String error;
    private int seatNum = 1;
    private boolean visited = true;
    final Firebase refReq = new Firebase(Constants.FIREBASE_URL_REQ);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        //visited = pref.getBoolean("visited", false);
        visited = false;
        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Log.i(LOG_TAG, "Visited: " + visited);



            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_book);
            Firebase.setAndroidContext(this);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            //ActionBar actionBar = getSupportActionBar();
            //actionBar.setTitle("Réservez votre place");
            if (!Utils.isConnectedToInternet(this)) {
                Toast.makeText(Book.this, R.string.connection_error,
                        Toast.LENGTH_LONG).show();
            }
            //Log.d(TAG, "Toke supposed to be: " + tokenVar.getToken());
            final String id = pref.getString("id", "");
            final String token = pref.getString("token", "");
            final String mUserEmail = pref.getString("user", "");
            final String useremail = Utils.encodeEmail(mUserEmail);
            final String username = pref.getString("name", "");
            final Integer remNum = pref.getInt("remaining", 0);
            final String driverEmail = pref.getString("driverEmail", "unknown");
            final String driveremail = Utils.encodeEmail(driverEmail);


            //final String departure = pref.getString("desPlace", "");
            //final String destination = pref.getString("depPlace", "");
            final String contact = pref.getString("resContact", "");
            refListName = new Firebase(Constants.FIREBASE_URL_TRIPS).child(id);
            refNot = new Firebase(Constants.FIREBASE_URL_NOT);
            tokenVar = new MyFirebaseInstanceIDService();

            book = (Button) findViewById(R.id.res);
            cancel = (Button) findViewById(R.id.notRes);
            seats = (Button) findViewById(R.id.seat);
            final EditText dep = (EditText) findViewById(R.id.dep);
            final EditText des = (EditText) findViewById(R.id.des);
            final EditText con = (EditText) findViewById(R.id.contact);
            //dep.setText(destination); des.setText(departure);
            if (contact != "") con.setText(contact);

            seats.setOnClickListener(this);
            book.setOnClickListener(this);
            cancel.setOnClickListener(this);

            book.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String departure = dep.getText().toString();
                    String destination = des.getText().toString();
                    String contact = con.getText().toString();


                    if (validate(contact, remNum)) {
                        log_event("04", "Click", "Reservation request");
                        String seats = Integer.toString(seatNum);
                        if (departure.equals("")) departure = "Non défini";
                        if (destination.equals("")) destination = "Non défini";
                        bookTrip(refListName, useremail, driveremail, username, departure, destination, seats, contact, token, id);
                        Toast.makeText(Book.this, "Votre demande à été envoyé.", Toast.LENGTH_LONG).show();
                        final SharedPreferences.Editor editor = pref.edit();
                        //editor.putBoolean("visited", true);
                        Intent intent = new Intent(Book.this, DetailActivity.class);
                        startActivity(intent);
                    } else Toast.makeText(Book.this, error, Toast.LENGTH_LONG).show();

                }
            });

            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Snackbar.make(view, "Réservation annulée.", Snackbar.LENGTH_LONG).
                            setAction("Action", null).show();
                    Intent intent = new Intent(Book.this, DetailActivity.class);
                    startActivity(intent);

                }
            });


    }

    public void log_event(String id, String name, String type) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, id);
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, name);
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, type);
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }

    public boolean validate(String contact, Integer rem) {
        error = "";
        if (contact.equals("")) error = "Entrez votre numéro de téléphone.";
        else if (!ValidatePhoneNumber(contact)) error = "Le format du numéro de téléphone est incorrect!";

        if (seatNum == 0) error = "Combien de places voulez-vous réserver?";
        if (seatNum > rem) error = "Pas assez de places disponibles!";

        if (error != "") return false;
        else return true;
    }

    public void bookTrip(Firebase ref, String useremail, String driveremail, String username ,String departure, String destination, String seats, String contact, String token, String id) {
        // notifying post owner ..
        HashMap<String, String> bookk = new HashMap<String, String>();
        bookk.put("post", id);
        bookk.put("token", token);
        refNot.child("book").setValue(null);
        refNot.child("book").setValue(bookk);
        final SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        // pushing booker info into the post
        int penNum = pref.getInt("pending", 0);
        //int booked = pref.getInt("booked", 0);
        final SharedPreferences.Editor editor = pref.edit();
        //editor.putString("resContact", contact);
        //editor.putString("depPlace", departure);
        editor.putString("desPlace", destination);

        penNum = penNum + Integer.parseInt(seats);
        HashMap<String, String> pen2 = new HashMap<>();
        pen2.put("name",username);
        pen2.put("departure", departure);
        pen2.put("destination", destination);
        pen2.put("seats", seats);
        pen2.put("contact", contact);
        pen2.put("token",tokenVar.getToken());

        if(Trip.pen_l!=null) Trip.pen_l.put(useremail, pen2);
        else {
            HashMap<String, HashMap<String, String>> pen = new HashMap<>();
            pen.put(useremail, pen2);
            Trip.pen_l = pen;
        }
        ref.child("pending").setValue(penNum);
        ref.child("pen_list").setValue(Trip.pen_l);

        String month, stamp;
        GregorianCalendar date = new GregorianCalendar();

        SimpleDateFormat monthformat = new SimpleDateFormat("MM-yyyy");
        SimpleDateFormat stampformat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

        monthformat.setCalendar(date);
        month = monthformat.format(date.getTime());

        stampformat.setCalendar(date);
        stamp = stampformat.format(date.getTime());
        String mUserEmail = Utils.decodeEmail(useremail);
        refReq.child(month).child(driveremail).child(id).child(stamp).setValue(mUserEmail);
    }

    @Override
    public void onClick(View v) {
        if (v == seats) {
            final CharSequence dest[] = new CharSequence[] {"01", "02", "03", "04", "05", "06", "07", "08"};
            final int seat[] = new int[] {1, 2, 3, 4, 5, 6, 7, 8};

            AlertDialog.Builder builder = new AlertDialog.Builder(this, android.R.style.Theme_Holo_Wallpaper);
            builder.setTitle("Nombre de places");
            builder.setItems(dest, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // the user clicked on colors[which]
                    seatNum = seat[which];
                    seats.setText(dest[which]);
                }
            });
            builder.show();
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        Log.e(LOG_TAG, "Activity starting ...");
        if (visited == true) {
            Intent intent = new Intent(Book.this, TripList.class);
            startActivity(intent);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e(LOG_TAG, "Activity pausing ...");
        //mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e(LOG_TAG, "Activity resuming ...");
        //mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        visited = true;
        Log.e(LOG_TAG, "Activity stopping ...");

    }
}
