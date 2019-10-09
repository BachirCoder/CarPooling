package com.bachir.owner.blayes;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ParseException;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bachir.owner.blayes.login.LoginActivity;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import static com.bachir.owner.blayes.Constants.commune;
import static com.bachir.owner.blayes.Constants.wilaya;
import static com.bachir.owner.blayes.Constants.wilaya0;
import static com.bachir.owner.blayes.Utils.ValidatePhoneNumber;

//import static com.bachir.owner.tawattrip.Constants.commune;


//import android.support.annotation.NonNull;

//import android.support.annotation.NonNull;

/**
 * Created by Owner on 5/24/2016.
 */
public class AddTrip extends AppCompatActivity implements View.OnClickListener {

    private static final String LOG_TAG = AddTrip.class.getSimpleName();
    Button departure, destination, dateSelector, timeSelector, seatNum;
    EditText desc, price, contact;
    boolean allowPost;
    private int mYear, mMonth, mDay, mHour, mMinute, yy, mo, dd, hh, mm, seats = 0;
    private String driver, email, mEncodedEmail, creator, error;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private MyFirebaseInstanceIDService tokenVar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Firebase.setAndroidContext(this);
        final Firebase ref = new Firebase(Constants.FIREBASE_URL_TRIPS);
        tokenVar = new MyFirebaseInstanceIDService();
        //Firebase ref = new Firebase(com.example.bachir.bachir.tawattrip.Constants.FIREBASE_URL);
        //ref.child("trip").setValue("My firt value!");
        setContentView(com.bachir.owner.blayes.R.layout.add_trip);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        if (!Utils.isConnectedToInternet(this)) {
            Toast.makeText(AddTrip.this, R.string.connection_error,
                    Toast.LENGTH_LONG).show();
        }

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(LOG_TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    email = user.getEmail().toLowerCase();
                    mEncodedEmail = Utils.encodeEmail(email);
                    final Firebase userLocation = new Firebase(Constants.FIREBASE_URL_USERS).child(mEncodedEmail);

                    userLocation.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            try {
                                final User myuser = dataSnapshot.getValue(User.class);
                                if (myuser != null) {
                                    driver = myuser.getName();
                                    allowPost = myuser.getAllowPost();
                                    if (allowPost == false)
                                        Toast.makeText(AddTrip.this, R.string.error_cannot_post, Toast.LENGTH_LONG).show();

                                    creator = email;
                                }
                            }catch (Exception e) {
                                Toast.makeText(AddTrip.this, R.string.error_account, Toast.LENGTH_LONG).show();
                            }

                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {
                            Log.d(LOG_TAG, getString(R.string.log_error_occurred) + firebaseError.getMessage());
                        }
                    });

                } else {
                    // User is signed out
                    Log.d(LOG_TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };

        final Button post = (Button) findViewById(R.id.btnPost);
        departure = (Button) findViewById(R.id.buttonSelectDeparture);
        destination = (Button) findViewById(R.id.buttonSelectDestination);
        dateSelector = (Button) findViewById(R.id.buttonSelectDate);
        timeSelector = (Button) findViewById(R.id.buttonSelectTime);
        seatNum = (Button) findViewById(R.id.seat);
        price = (EditText) findViewById(R.id.price);
        contact = (EditText) findViewById(R.id.contact);
        desc = (EditText) findViewById(R.id.inputDesc);

        departure.setOnClickListener(this);
        destination.setOnClickListener(this);
        dateSelector.setOnClickListener(this);
        timeSelector.setOnClickListener(this);
        seatNum.setOnClickListener(this);
        final SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        final String cont = pref.getString("contact", "Contact No");
        final String dep = pref.getString("departure", "Départ");
        final String des = pref.getString("destination", "Destination");
        final String inf = pref.getString("description", "");
        Integer pri = 0;
        pri = pref.getInt("price", 0);
        if (cont != "Contact No") contact.setText(cont);
        if (dep != "Départ") destination.setText(dep);
        if (des != "Destination") departure.setText(des);
        if (inf != "") desc.setText(inf);
        if (pri != 0) price.setText(pri.toString());

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (validate(seats, contact, departure, destination, dateSelector, timeSelector) == true && allowPost == true) {
                    newTrip(ref, departure, destination, seats, price, contact, desc);
                    Toast.makeText(AddTrip.this, "Votre annonce a été créée.",
                            Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(AddTrip.this, TripList.class);
                    startActivity(intent);
                } else if (allowPost == false)
                    Toast.makeText(AddTrip.this, R.string.error_cannot_post, Toast.LENGTH_LONG).show();
                    else Toast.makeText(AddTrip.this, error, Toast.LENGTH_LONG).show();
            }
        });

    }

    public boolean validate(int seats, EditText cc, Button departure, Button destination, Button date, Button time) {
        String contact = cc.getText().toString();
        String dep = departure.getText().toString();
        String des = destination.getText().toString();
        String dd = date.getText().toString();
        String tt = time.getText().toString();
        error = "";
        Log.i(LOG_TAG, "dep is: " + dep);
        Log.i(LOG_TAG, "des is: " + des);
        Log.i(LOG_TAG, "contact is: " + contact);

        if (contact.equals("")) error = "Entrez votre numéro de téléphone.";
        else if (!ValidatePhoneNumber(contact)) error = "Le format du numéro de téléphone est incorrect!";

        if (seats == 0) error = "Combien de places sont disponibles?";
        if (dd.equals("Date")) error = "Entrez une date.";
        if (tt.equals("Horaire")) error = "Choisissez votre temps.";
        if (dep.equals("Départ")) error = "Entrez votre départ.";
        if (des.equals("Destination")) error = "Entrez votre destination.";

        if (error != "") return false;
        else return true;
    }

    public void newTrip(Firebase ref, Button dep, Button des, Integer seat, EditText price, EditText cc, EditText desc) {
        String departure = dep.getText().toString();
        String destination = des.getText().toString();
        String contact = cc.getText().toString();
        Integer prix = 0;
        if (contact == null) contact="No contact.";
        final SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        final SharedPreferences.Editor editor = pref.edit();
        editor.putString("contact", contact);

        String datetime, token = "No token!";

        GregorianCalendar date = new GregorianCalendar();
        SimpleDateFormat dateformat = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        try {
            date = new GregorianCalendar(yy, mo, dd, hh+1, mm);
            Log.i(LOG_TAG, "Hour: " + hh);

            dateformat.setCalendar(date);
            datetime = dateformat.format(date.getTime());
            System.out.println("Current Date Time : " + datetime);
            //date = new GregorianCalendar(yy, mo, dd, hh+8, mm);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        try {
            token = tokenVar.getToken();
            Log.i(LOG_TAG, "GCM Registration Token: " + token);

        }catch (Exception e) {
            Log.d(LOG_TAG, "Failed to complete token refresh", e);
        }

        String description = desc.getText().toString();
        try {
            prix = Integer.parseInt(price.getText().toString());
        } catch(Exception var){
            prix = 0;
        }
        editor.putInt("price", prix);
        editor.putString("departure", departure);
        editor.putString("destination", destination);
        editor.putString("description", description);
        editor.commit();
        //int id = Trip.id += 1;
        //String idkey = Integer.toString(id);
        Log.d(LOG_TAG, "Driver added is: " + driver);
        Trip obj = new Trip(creator, driver, departure, destination, date, seat, prix, contact, description, token);
        ref.push().setValue(obj);
    }



    @Override
    public void onClick(View v) {

        if (v == departure) {
            //final CharSequence dest[] = new CharSequence[] {"Alger", "Oran", "Annaba", "Ghardaïa", "Blida", "Batna", "Sétif", "Laghouat", "Tlemcen", "Tiaret", "Constantine"};

            final AlertDialog.Builder builder = new AlertDialog.Builder(this, android.R.style.Theme_Holo_Wallpaper);
            builder.setTitle("Choisissez votre départ (Wilaya)");
            final AlertDialog.Builder builder2 = new AlertDialog.Builder(this, android.R.style.Theme_Holo_Wallpaper);
            builder2.setTitle("Choisissez votre départ (Commune)");
            builder.setItems(wilaya, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // the user clicked on colors[which]

                    final int var = which;
                    builder2.setItems(commune[var], new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (which == 0) departure.setText(wilaya0[var]);
                            else departure.setText(wilaya0[var] + " (" + commune[var][which] + ")");
                        }
                    });
                    builder2.show();

                }
            });
            builder.show();

        }

        if (v == destination) {
            //final CharSequence dest[] = new CharSequence[] {"Alger", "Oran", "Annaba", "Ghardaïa", "Blida", "Batna", "Sétif", "Laghouat", "Tlemcen", "Tiaret", "Constantine"};

            final AlertDialog.Builder builder = new AlertDialog.Builder(this, android.R.style.Theme_Holo_Wallpaper);
            builder.setTitle("Choisissez votre destination (Wilaya)");
            final AlertDialog.Builder builder2 = new AlertDialog.Builder(this, android.R.style.Theme_Holo_Wallpaper);
            builder2.setTitle("Choisissez votre destination (Commune)");
            builder.setItems(wilaya, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // the user clicked on colors[which]

                    final int var = which;
                    builder2.setItems(commune[var], new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (which == 0) destination.setText(wilaya0[var]);
                            else destination.setText(wilaya0[var] + " (" + commune[var][which] + ")");
                        }
                    });
                    builder2.show();

                }
            });
            builder.show();
        }

        if (v == dateSelector) {

            // Get Current Date
            final Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);


            DatePickerDialog datePickerDialog = new DatePickerDialog(this, AlertDialog.THEME_DEVICE_DEFAULT_DARK,
                    new DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {

                            dateSelector.setText(dayOfMonth + " - " + (monthOfYear + 1) + " - " + year);
                            yy = year; mo = monthOfYear; dd = dayOfMonth;

                        }
                    }, mYear, mMonth, mDay);
            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis()-1000);
            datePickerDialog.show();
        }
        if (v == timeSelector) {

            // Get Current Time
            final Calendar c = Calendar.getInstance();
            mHour = c.get(Calendar.HOUR_OF_DAY);
            mMinute = c.get(Calendar.MINUTE);

            // Launch Time Picker Dialog
            TimePickerDialog timePickerDialog = new TimePickerDialog(this, AlertDialog.THEME_DEVICE_DEFAULT_DARK,
                    new TimePickerDialog.OnTimeSetListener() {

                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay,
                                              int minute) {

                            timeSelector.setText(hourOfDay + " : " + String.format("%02d", minute));
                            hh = hourOfDay; mm = minute;
                        }
                    }, mHour, mMinute, false);
            timePickerDialog.show();
        }
        if (v == seatNum) {
            final CharSequence dest[] = new CharSequence[] {"01", "02", "03", "04", "05", "06", "07", "08"};
            final int seat[] = new int[] {1, 2, 3, 4, 5, 6, 7, 8};

            AlertDialog.Builder builder = new AlertDialog.Builder(this, android.R.style.Theme_Holo_Wallpaper);
            builder.setTitle("Nombre de places");
            builder.setItems(dest, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // the user clicked on colors[which]
                    seats = seat[which];
                    seatNum.setText(dest[which]);
                }
            });
            builder.show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_trip, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_home) {
            Intent intent = new Intent(AddTrip.this, TripList.class);
            startActivity(intent);
            return true;
        }

        if (id == R.id.action_contact) {
            Intent intent = new Intent(AddTrip.this, Contact.class);
            startActivity(intent);
            return true;
        }

        if (id == R.id.action_about) {
            Intent intent = new Intent(AddTrip.this, About.class);
            startActivity(intent);
            return true;
        }

        if (id == R.id.action_logout) {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(AddTrip.this, LoginActivity.class);
            startActivity(intent);
            return true;
        }

        if (id == R.id.action_exit) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

}
