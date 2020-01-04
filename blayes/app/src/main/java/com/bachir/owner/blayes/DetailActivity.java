package com.bachir.owner.blayes;

import android.app.DialogFragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bachir.owner.blayes.login.LoginActivity;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class DetailActivity extends AppCompatActivity {

    private static final String LOG_TAG = DetailActivity.class.getSimpleName();
    private TextView departue, destination, date, time, driver, rem, total, booked, price, contact, details, penTitle, bookTitle, status;
    private Button book, cancel, callT, smsT, call, sms;//accept, decline, delete;
    private LinearLayout penList, bookList, remRow, totalRow, bookedRow, buttons, enableCall, disableCall;
    private HashMap<String, HashMap<String, String>> book_hash, pen_hash;
    private List<HashMap<String, HashMap<String, String>>> book_list, pen_list;
    private String id, phone, smsBody = "السلام عليكم. أريد حجز مقعد معكم في رحلتكم المعلنة على تطبيق بلايص. وشكرا";
    private Firebase refListName, refNot;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    //private String creator, email, mEncodedEmail, var;
    private boolean owner = false, wanted = false, confirmed = false;
    private Integer penNum, bookNum, totalNum, remNum, requestedN=0, bookedN=0;
    private Trip post;
    private FirebaseAnalytics mFirebaseAnalytics;
    String token = "", var, driverEmail;
    //private User myuser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Firebase.setAndroidContext(this);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        final RecyclerView rv1 = (RecyclerView) findViewById(R.id.rv1);
        final RecyclerView rv2 = (RecyclerView) findViewById(R.id.rv2);
        final SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        final String mUserEmail = pref.getString("user", "");
        final String useremail = Utils.encodeEmail(mUserEmail);
        final String username = pref.getString("name", "");

        if (!Utils.isConnectedToInternet(this)) {
            Toast.makeText(DetailActivity.this, R.string.connection_error,
                    Toast.LENGTH_LONG).show();
        }

        id = pref.getString("id", "");
        final SharedPreferences.Editor editor = pref.edit();
        //final String userkey = pref.getString("userkey", null);
        status = (TextView) findViewById(R.id.status);
        departue = (TextView) findViewById(R.id.departure);
        destination = (TextView) findViewById(R.id.destination);
        date = (TextView) findViewById(R.id.date);
        time = (TextView) findViewById(R.id.time);
        driver = (TextView) findViewById(R.id.driver);
        rem = (TextView) findViewById(R.id.rem);
        total = (TextView) findViewById(R.id.total);
        booked = (TextView) findViewById(R.id.booked);
        price = (TextView) findViewById(R.id.price);
        contact = (TextView) findViewById(R.id.contact);
        details = (TextView) findViewById(R.id.details);
        book = (Button) findViewById(R.id.btnBook);
        cancel = (Button) findViewById(R.id.btnCancel);
        call = (Button) findViewById(R.id.btnCall2);
        sms = (Button) findViewById(R.id.btnSms2);
        callT = (Button) findViewById(R.id.btnCall);
        smsT = (Button) findViewById(R.id.btnSms);
        //accept = (Button) findViewById(R.id.btnAccept);
        //decline = (Button) findViewById(R.id.btnDecline);
        //delete = (Button) findViewById(R.id.btnDelete);
        penList = (LinearLayout) findViewById(R.id.penList);
        bookList = (LinearLayout) findViewById(R.id.bookList);
        buttons = (LinearLayout) findViewById(R.id.buttons);
        penTitle = (TextView) findViewById(R.id.penTitle);
        bookTitle = (TextView) findViewById(R.id.bookTitle);

        totalRow = (LinearLayout) findViewById(R.id.totalRow);
        bookedRow = (LinearLayout) findViewById(R.id.bookedRow);
        remRow = (LinearLayout) findViewById(R.id.remRow);
        enableCall = (LinearLayout) findViewById(R.id.enableCall);
        disableCall = (LinearLayout) findViewById(R.id.disableCall);
        //Intent intent = getIntent();
        //id = intent.getExtras().getString("trip");

        refListName = new Firebase(Constants.FIREBASE_URL_TRIPS).child(id);
        refNot = new Firebase(Constants.FIREBASE_URL_NOT);
        refListName.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // You can use getValue to deserialize the data at dataSnapshot
                // into a ShoppingList.
                post = dataSnapshot.getValue(Trip.class);
                // If there was no data at the location we added the listener, then
                // shoppingList will be null.
                if (post != null) {
                    // If there was data, take the TextViews and set the appropriate values.
                    departue.setText(post.getDeparture());
                    destination.setText(post.getDestination());
                    date.setText(Trip.gToString(post.getDatetime(), 2));
                    time.setText(Trip.gToString(post.getDatetime(), 3));
                    token = post.getToken();
                    driverEmail = post.getUserid();
                    driver.setText(post.getDriver());
                    price.setText(post.getPrice().toString()+".00 DZD");
                    phone = post.getContact();
                    contact.setText(phone);
                    details.setText(post.getDetails());
                    totalNum = post.getSeat();
                    total.setText(totalNum.toString());
                    bookNum = post.getBooked();
                    penNum = post.getPending();
                    remNum = totalNum - bookNum;
                    rem.setText(remNum.toString());
                    editor.putInt("pending", penNum);
                    editor.putInt("booked", bookNum);
                    editor.putInt("remaining", remNum);
                    editor.putString("token", token);
                    editor.putString("driverEmail", driverEmail);
                    //editor.putInt("total", totalNum);
                    booked.setText(bookNum.toString());
                    pen_hash = post.getPen_list();
                    Trip.pen_l = pen_hash;
                    book_hash = post.getBook_list();

                    //if (pen_hash.containsKey(useremail)) {try {requestedN = Integer.parseInt(pen_hash.get(useremail).get("seats"));} catch (Exception e) { requestedN = 1;}}
                    //if (book_hash.containsKey(useremail)) {try {bookedN = Integer.parseInt(book_hash.get(useremail).get("seats"));} catch (Exception e) { bookedN = 1;}}

                    var = post.getUserid();
                    if (pen_hash != null && pen_hash.containsKey(useremail)) {
                        wanted = true;
                        try {requestedN = Integer.parseInt(pen_hash.get(useremail).get("seats"));}
                        catch (Exception e) { requestedN = 1;}
                    }
                    else if (book_hash != null && book_hash.containsKey(useremail)) {
                        wanted = true; confirmed = true;
                        try {bookedN = Integer.parseInt(book_hash.get(useremail).get("seats"));}
                        catch (Exception e) { bookedN = 1;}
                    }
                    else {
                        wanted = false; confirmed = false;
                    }


                    Log.d(LOG_TAG, "Driver post key name is: " + var);
                    Log.d(LOG_TAG, "user current key name is: " + username);
                    if (var.equalsIgnoreCase(mUserEmail)) {
                        owner = true;
                        contact.setVisibility(LinearLayout.VISIBLE);
                        penTitle.setVisibility(LinearLayout.VISIBLE);
                        bookTitle.setVisibility(LinearLayout.VISIBLE);
                        remRow.setVisibility(LinearLayout.GONE);
                        if (pen_hash != null) upPenList(pen_hash, rv1);
                        else {
                            Log.d(LOG_TAG, "Point 001.");
                            penTitle.setText("Aucune demande.");
                            penList.setVisibility(LinearLayout.GONE);
                        }
                        if (book_hash != null) upBookList(book_hash, rv2);
                        else {
                            Log.d(LOG_TAG, "Point 002.");
                            bookTitle.setText("Aucune réservation.");
                            bookList.setVisibility(LinearLayout.GONE);
                        }
                        buttons.setVisibility(LinearLayout.GONE);

                    }
                    else {  owner = false;
                        totalRow.setVisibility(LinearLayout.GONE);
                        bookedRow.setVisibility(LinearLayout.GONE);

                        if (wanted) {
                            book.setVisibility(Button.GONE);
                            cancel.setVisibility(Button.VISIBLE);
                            status.setVisibility(TextView.VISIBLE);
                            //call.setBackgroundColor(getResources().getColor(deepskyblue));
                            //sms.setBackgroundColor(getResources().getColor(deepskyblue));
                            disableCall.setVisibility(View.GONE);
                            enableCall.setVisibility(View.VISIBLE);
                            //sms.setTextAppearance(R.style.Buttons);
                            if (confirmed) {
                                status.setText("Votre réservation a été confirmée.");
                                cancel.setText("Annuler votre réservation");
                            }
                        }
                        else {
                            //editor.putBoolean("visited", false);
                            book.setVisibility(Button.VISIBLE);
                            cancel.setVisibility(Button.GONE);
                            status.setVisibility(TextView.GONE);
                        }

                    };
                }

                //BLAdapter adapter = new BLAdapter(pen_list);
                //rv.setAdapter(adapter);
                editor.commit();

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });

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
                    //FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(DetailActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
                // ...
            }
        };


        book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (bookNum < totalNum) {
                    log_event("01", "Click", "Go reserve");
                    Intent intent = new Intent(DetailActivity.this, Book.class);
                    startActivity(intent);
                }
                else {
                    Snackbar.make(view, "Il n'y a aucune place disponible.", Snackbar.LENGTH_LONG).
                            setAction("Action", null).show();
                }

            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                unBook(refListName, useremail, requestedN, bookedN);

            }
        });

        callT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                     Snackbar.make(view, "Réserver d'abord puis faire votre appel.", Snackbar.LENGTH_LONG).
                            setAction("Action", null).show();
                } catch (Exception e) {
                    Snackbar.make(view, "Vous ne pouvez pas faire l'appel.", Snackbar.LENGTH_LONG).
                            setAction("Action", null).show();            }
            }
        });

        smsT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Snackbar.make(view, "Réserver d'abord puis envoyer vos sms.", Snackbar.LENGTH_LONG).
                            setAction("Action", null).show();
                } catch (Exception e) {
                    Snackbar.make(view, "Vous ne pouvez pas faire l'appel.", Snackbar.LENGTH_LONG).
                            setAction("Action", null).show();            }

            }
        });

        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                        if (phone != null && phone != "") {
                            log_event("02", "Click", "Call");
                            Intent intent = new Intent(Intent.ACTION_DIAL);
                            intent.setData(Uri.parse("tel:"+phone));
                            startActivity(intent);
                        }
                        else
                            Snackbar.make(view, "Le chauffeur n'a pas fourni son numéro de téléphone.", Snackbar.LENGTH_LONG).
                                    setAction("Action", null).show();


                } catch (Exception e) {
                    Snackbar.make(view, "Vous ne pouvez pas envoyer de SMS.", Snackbar.LENGTH_LONG).
                            setAction("Action", null).show();            }

            }
        });

        sms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (phone != null && phone != "") {
                        log_event("03", "Click", "SMS");
                        Intent smsIntent = new Intent(Intent.ACTION_VIEW);
                        // Invokes only SMS/MMS clients
                        smsIntent.setType("vnd.android-dir/mms-sms");
                        // Specify the Phone Number
                        smsIntent.putExtra("address", phone);
                        // Specify the Message
                        smsIntent.putExtra("sms_body", smsBody);
                        // Shoot!
                        startActivity(smsIntent);
                    } else
                        Snackbar.make(view, "Le chauffeur n'a pas fourni son numéro de téléphone.", Snackbar.LENGTH_LONG).
                                setAction("Action", null).show();

                } catch (Exception e) {
                    Snackbar.make(view, "Vous ne pouvez pas envoyer de SMS.", Snackbar.LENGTH_LONG).
                            setAction("Action", null).show();            }

            }
        });

    }

    public boolean booked() {
        if (wanted == true) return true;
        else return false;
    }

    public void log_event(String id, String name, String event) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, id);
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, name);
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, event);
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }


    public void upPenList(HashMap<String, HashMap<String, String>> trips, RecyclerView rv) {

        //trips = new ArrayList<String>();
        //trips.add("One");         trips.add("two");        trips.add("Three");trips.add("Four");trips.add("Five");
        //for(int i=0; i<3; i++) Log.i("Items: ", trips.get(i));
        List<HashMap<String, HashMap<String, String>>> list = hashToList(trips);
        LinearLayoutManager llm = new LinearLayoutManager(DetailActivity.this);
        if (rv != null) {
            rv.setLayoutManager(llm);

            rv.setHasFixedSize(false);
            PLAdapter adapter = new PLAdapter(list);
            Log.i("Num of pen found: ", Integer.toString(adapter.getItemCount()));
            rv.setAdapter(adapter);
        }

        penTitle.setText("Demandes de réservation:");
        penList.setVisibility(LinearLayout.VISIBLE);
    }

    public void upBookList(HashMap<String, HashMap<String, String>> trips, RecyclerView rv) {

        //trips = new ArrayList<String>();
        //trips.add("One");         trips.add("two");        trips.add("Three");trips.add("Four");trips.add("Five");
        //for(int i=0; i<3; i++) Log.i("Items: ", trips.get(i));
        List<HashMap<String, HashMap<String, String>>> list = hashToList(trips);
        LinearLayoutManager llm = new LinearLayoutManager(DetailActivity.this);
        if (rv != null) {
            rv.setLayoutManager(llm);

            rv.setHasFixedSize(false);
            BLAdapter adapter = new BLAdapter(list);
            Log.i("Num of books found: ", Integer.toString(adapter.getItemCount()));
            rv.setAdapter(adapter);
        }

        bookTitle.setText("Réservations:");
        bookList.setVisibility(LinearLayout.VISIBLE);
    }

    public List<HashMap<String, HashMap<String, String>>> hashToList(HashMap<String, HashMap<String, String>> var){

        List<HashMap<String, HashMap<String, String>>> list = new ArrayList<>();
        Integer s = 0;

        for ( String key : var.keySet() ) {
            Log.i("Pair: ", key + " " + var.get(key));
            HashMap<String, HashMap<String, String>> var1 = new HashMap<>();
            var1.put(key, var.get(key));
            list.add(s, var1);
            s += 1;
        }

      return list;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail, menu);

        /**
         * Get menu items
         */
        MenuItem remove = menu.findItem(R.id.action_remove_list);
        MenuItem edit = menu.findItem(R.id.action_edit_list_name);
        MenuItem share = menu.findItem(R.id.action_share_list);
        MenuItem archive = menu.findItem(R.id.action_archive);

        if (owner) {
            remove.setVisible(true);
            edit.setVisible(true);

        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_home) {
            Intent intent = new Intent(DetailActivity.this, TripList.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
            return true;
        }

        if (id == R.id.action_add) {
            Intent intent = new Intent(DetailActivity.this, AddTrip.class);
            startActivity(intent);
            return true;
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(DetailActivity.this, LoginActivity.class);
            startActivity(intent);
            return true;
        }

        if (id == R.id.action_about) {
            Intent intent = new Intent(DetailActivity.this, About.class);
            startActivity(intent);
            return true;
        }

        if (id == R.id.action_contact) {
            Intent intent = new Intent(DetailActivity.this, Contact.class);
            startActivity(intent);
            return true;
        }

        if (id == R.id.action_remove_list) {
            removeTrip();
            return true;
        }

        if (id == R.id.action_edit_list_name) {
            editTrip();
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

    public void removeTrip() {
        /* Create an instance of the dialog fragment and show it */
        DialogFragment dialog = RemoveTripDialogFragment.newInstance(DetailActivity.this, refListName);
        dialog.show(getFragmentManager(), "RemoveTripDialogFragment");
    }

    public void editTrip(){
        Intent intent = new Intent(this, EditTrip.class);
        intent.putExtra("trip",id);
        startActivity(intent);
    }



    public void unBook(Firebase ref, String useremail, Integer requestedN, Integer bookedN) {
        Log.d(LOG_TAG, useremail);

        if (confirmed == false) {
            post.pen_append(-requestedN);
            Log.d(LOG_TAG, post.getPen_list().toString());
            post.remPen_list(useremail);

            ref.child("pending").setValue(post.getPending());
            ref.child("pen_list").setValue(post.getPen_list());


            HashMap<String, String> cancell = new HashMap<String, String>();
            cancell.put("post", id);
            cancell.put("token", token);
            refNot.child("cancel").setValue(null);
            refNot.child("cancel").setValue(cancell);
            cancel.setVisibility(Button.GONE);
            book.setVisibility(Button.VISIBLE);

            Toast.makeText(DetailActivity.this, "Vous avez annulé votre demande avec succès.",
                    Toast.LENGTH_LONG).show();
        }
        else if (confirmed == true) {
            DialogFragment dialog = CancelDialogFragment.newInstance(DetailActivity.this, refListName, post, useremail, id, book, cancel, bookedN);
            dialog.show(getFragmentManager(), "CancelDialogFragment");

        }

    }

    @Override
    public void onStart() {
        super.onStart();
            mAuth.addAuthStateListener(mAuthListener);

    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(LOG_TAG, "Activity pausing ...");
        //mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(LOG_TAG, "Activity resuming ...");
        //mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
    /*
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, TripList.class);
        startActivity(intent);
    }
    */
}
