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
import android.widget.TextView;
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

import static com.bachir.owner.blayes.Constants.wilaya;
import static com.bachir.owner.blayes.Constants.wilaya0;
import static com.bachir.owner.blayes.Utils.ValidatePhoneNumber;


public class EditTrip extends AppCompatActivity implements View.OnClickListener {

    private static final String LOG_TAG = LoginActivity.class.getSimpleName();
    TextView title;
    Button departure, destination, dateSelector, timeSelector, save, seatNum;
    EditText contact, desc, price;
    private int mYear, mMonth, mDay, mHour, mMinute, yy, mo, dd, hh, mm, seats = 0;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private MyFirebaseInstanceIDService tokenVar;
    String token = "", con;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_trip);
        tokenVar = new MyFirebaseInstanceIDService();
        //title = (TextView) findViewById(R.id.title);
        //title.setText("Edit Trip");

        if (!Utils.isConnectedToInternet(this)) {
            Toast.makeText(EditTrip.this, R.string.connection_error,
                    Toast.LENGTH_LONG).show();
        }

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(LOG_TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(LOG_TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };
        save = (Button) findViewById(R.id.btnPost);
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
        save.setText("SAVE");

        Intent intent = getIntent();
        String id = intent.getExtras().getString("trip");

        final Firebase refListName = new Firebase(Constants.FIREBASE_URL_TRIPS).child(id);

        refListName.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // You can use getValue to deserialize the data at dataSnapshot
                // into a ShoppingList.
                Trip post = dataSnapshot.getValue(Trip.class);
                // If there was no data at the location we added the listener, then
                // shoppingList will be null.
                if (post != null) {
                    // If there was data, take the TextViews and set the appropriate values.
                    departure.setText(post.getDeparture());
                    destination.setText(post.getDestination());
                    dateSelector.setText(Trip.gToString(post.getDatetime(),2));
                    timeSelector.setText(Trip.gToString(post.getDatetime(),3));
                    seats = post.getSeat();
                    seatNum.setText(post.getSeat().toString());
                    price.setText(post.getPrice().toString());
                    con = post.getContact();
                    contact.setText(con);
                    desc.setText(post.getDetails());
                    token = post.getToken();

                    //GregorianCalendar date = new GregorianCalendar();
                    GregorianCalendar date = post.getDatetime();
                    Calendar calendar = date;
                    //calendar.setTime(date);
                    yy = calendar.get(Calendar.YEAR);
                    mo = calendar.get(Calendar.MONTH);
                    dd = calendar.get(Calendar.DAY_OF_MONTH);
                    hh = calendar.get(Calendar.HOUR_OF_DAY); // gets hour in 24h format
                    mm = calendar.get(Calendar.MINUTE);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //con = contact.getText().toString();
                Log.i(LOG_TAG, "phone is: " + con);
                if (con != null && !con.equals("") && ValidatePhoneNumber(con)) {
                    edit(refListName, departure, destination, seats, price, con, desc);
                    Toast.makeText(EditTrip.this, "L'annonce a été éditée.",
                            Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(EditTrip.this, TripList.class);
                    startActivity(intent);
                }
                else Toast.makeText(EditTrip.this, "Entrez un numéro de téléphone valide.",
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onClick(View v) {

        if (v == departure) {
            //final CharSequence dest[] = new CharSequence[] {"Alger", "Oran", "Annaba", "Ghardaïa", "Blida", "Batna", "Sétif", "Laghouat", "Tlemcen", "Tiaret", "Constantine"};

            AlertDialog.Builder builder = new AlertDialog.Builder(this, android.R.style.Theme_Holo_Wallpaper);
            builder.setTitle("Choisissez un départ");
            builder.setItems(wilaya, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // the user clicked on colors[which]
                    departure.setText(wilaya0[which]);
                }
            });
            builder.show();
        }

        if (v == destination) {
            //final CharSequence dest[] = new CharSequence[] {"Alger", "Oran", "Annaba", "Ghardaïa", "Blida", "Batna", "Sétif", "Laghouat", "Tlemcen", "Tiaret", "Constantine"};

            AlertDialog.Builder builder = new AlertDialog.Builder(this, android.R.style.Theme_Holo_Wallpaper);
            builder.setTitle("Choisissez une destination");
            builder.setItems(wilaya, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    destination.setText(wilaya0[which]);
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


    public void edit(Firebase ref, Button dep, Button des, Integer seat, EditText price, String contact, EditText desc) {
        String departure = dep.getText().toString();
        String destination = des.getText().toString();
        if (contact == null) contact="No contact.";
        final SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        final SharedPreferences.Editor editor = pref.edit();
        editor.putString("contact", contact);
        String datetime;
        Integer prix = 0;
        GregorianCalendar date = new GregorianCalendar();
        SimpleDateFormat dateformat = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        final String mUserEmail = pref.getString("user", null);
        final String useremail = Utils.encodeEmail(mUserEmail);
        final String username = pref.getString("name", null);


        try {
            date = new GregorianCalendar(yy, mo, dd, hh+1, mm);
            dateformat.setCalendar(date);
            datetime = dateformat.format(date.getTime());
            System.out.println("Current Date Time : " + datetime);
            //date = new GregorianCalendar(yy, mo, dd, hh, mm);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String description = desc.getText().toString();

        try {
            prix = Integer.parseInt(price.getText().toString());
        } catch(Exception var){
            prix = 0;
        }

        try {
            token = tokenVar.getToken();
            Log.i(LOG_TAG, "GCM Registration Token: " + token);

        }catch (Exception e) {
            Log.d(LOG_TAG, "Failed to complete token refresh", e);
        }

        editor.putInt("price", prix);
        editor.putString("departure", departure);
        editor.putString("destination", destination);
        editor.putString("description", description);
        editor.commit();

        Trip a = new Trip(useremail, username, departure, destination, date, seat, prix, contact, description, token);


        ref.child("contact").setValue(a.getContact());
        ref.child("datetime").setValue(a.getDatetime());
        ref.child("departure").setValue(a.getDeparture());
        ref.child("destination").setValue(a.getDestination());
        ref.child("seat").setValue(a.getSeat());
        ref.child("price").setValue(a.getPrice());
        ref.child("contact").setValue(a.getContact());
        ref.child("details").setValue(a.getDetails());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_trip, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_home) {
            Intent intent = new Intent(EditTrip.this, TripList.class);
            startActivity(intent);
            return true;
        }

        if (id == R.id.action_contact) {
            Intent intent = new Intent(EditTrip.this, Contact.class);
            startActivity(intent);
            return true;
        }

        if (id == R.id.action_about) {
            Intent intent = new Intent(EditTrip.this, About.class);
            startActivity(intent);
            return true;
        }

        if (id == R.id.action_logout) {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(EditTrip.this, LoginActivity.class);
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
}
