package com.bachir.owner.blayes;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationListener;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
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
import java.util.Date;
import java.util.List;

import static com.bachir.owner.blayes.Constants.wilaya0;
import static com.bachir.owner.blayes.Constants.wilaya1;
import static com.bachir.owner.blayes.Utils.wilaya;

//import com.google.android.gms.appindexing.AppIndex;
//import com.google.firebase.appindexing.Action;

//import com.google.android.gms.appindexing.Action;
//import com.google.android.gms.appindexing.AppIndex;


public class TripList extends BaseActivity implements LaunchFragment.OnLaunchSelectedListener, StarterFragment.OnRoleSelectedListener, MainActivityFragment.MainSelectedListener {

    private FirebaseAnalytics mFirebaseAnalytics;
    private static final String LOG_TAG = TripList.class.getSimpleName();
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    //private GoogleApiClient client;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private Firebase ref, userLocation;
    private String creator, phone, mEncodedEmail, currName;

    private Trip post;
    //private boolean launch = true;
    private User myuser;
    private String depSearch, desSearch; //wilaya = Utils.wilaya;
    private ProgressDialog mAuthProgressDialog;
    boolean allowPost = true, locationRetrieved = false, dataRetrieved = false;
    private Button driver, passenger;
    final List<Trip> trips = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Check that the activity is using the layout version with
        // the fragment_container FrameLayout
        if (findViewById(R.id.fragment_container) != null) {
            Log.i(LOG_TAG, "This is the Activity's initial launch ...");
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }

            // Create a new Fragment to be placed in the activity layout
            LaunchFragment firstFragment = new LaunchFragment();

            // In case this activity was started with special instructions from an
            // Intent, pass the Intent's extras to the fragment as arguments
            firstFragment.setArguments(getIntent().getExtras());

            // Add the fragment to the 'fragment_container' FrameLayout
            transaction
                    .add(R.id.fragment_container, firstFragment).commit();

        }




// Replace whatever is in the fragment_container view with this fragment,
// and add the transaction to the back stack so the user can navigate back





        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Log.d(LOG_TAG, "Main activity here ...");

        //toolbar.setPadding(0, 0, 0, 0);//for tab otherwise give space in tab
        //toolbar.setContentInsetsAbsolute(0, 0);

        if (!Utils.isConnectedToInternet(this)) {
            Toast.makeText(TripList.this, R.string.connection_error,
                    Toast.LENGTH_LONG).show();
        }

        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        final SharedPreferences.Editor editor = pref.edit();
        initializeDialog();
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(LOG_TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    phone = user.getPhoneNumber();
                    //mEncodedEmail = Utils.encodeEmail(email);
                    Log.d(LOG_TAG, "phone number is: " + phone);


                    userLocation = new Firebase(Constants.FIREBASE_URL_USERS).child(phone);

                    userLocation.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            try {
                                myuser = dataSnapshot.getValue(User.class);
                                Log.i(LOG_TAG, "No problem mapping user object ...");
                            if (myuser != null) {
                                creator = phone;
                                currName = myuser.getName();
                                Log.d(LOG_TAG, "Creator email is: " + creator);
                                allowPost = myuser.getAllowPost();
                                Log.i(LOG_TAG, "The value of (allowPost) has been retrieved ...");
                                if (allowPost == false)
                                    Toast.makeText(TripList.this, R.string.error_cannot_post, Toast.LENGTH_LONG).show();

                                editor.putString("user", creator);  // Saving string
                                editor.putString("name", currName);
                                editor.commit();
                            } else Log.d(LOG_TAG, "User object is null ...");

                        }catch (Exception e) {
                                 Toast.makeText(TripList.this, R.string.error_account, Toast.LENGTH_LONG).show();
                        }
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {
                            Log.d(LOG_TAG, getString(R.string.log_error_occurred) + firebaseError.getMessage());
                        }
                    });


                    //creator = user.getDisplayName();
                    Log.d(LOG_TAG, "Hello from here ...");




                } else {
                    // User is signed out
                    Log.d(LOG_TAG, "onAuthStateChanged:signed_out");
                    //FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(TripList.this, LoginActivity.class);
                    startActivity(intent);
                }
                // ...
            }
        };




        /*
        ActionBar actionBar = getSupportActionBar();
        actionBar.setLogo(R.drawable.ic_launcher);
        actionBar.setTitle("layes");
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        */
        Firebase.setAndroidContext(this);
        //List<Trip> trips1 = new ArrayList<>();
        //uplist(trips1, rv);
        Log.e("info: ", "Before reading.");
        long now = new Date().getTime();
        Log.e("Time now is: ", Long.toString(now));
        final Bundle extras = getIntent().getExtras();
        if (extras != null) {
            depSearch = extras.getString("departure");
            desSearch = extras.getString("destination");
        }
        ref = new Firebase(Constants.FIREBASE_URL_TRIPS);
        Log.e("info: ", "Not reading yet.");
        ref.orderByChild("datetime").startAt(now + 3600000).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Log.e("info: ", "It is reading.");
                System.out.println("There are " + snapshot.getChildrenCount() + " Trips");

                    int i = 0;
                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                        //HashMap<String, String> trip = new HashMap<String, String>();
                        post = postSnapshot.getValue(Trip.class);
                        post.setId(postSnapshot.getKey());
                                trips.add(post);
                                i += 1;
                        }
                dataRetrieved = true;
                Log.e("info: ", "All trips have been retrieved. Adapter not attached yet.");


                if (locationRetrieved == true) {
                    StarterFragment secondFragment = new StarterFragment();
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.fragment_container, secondFragment);
                    transaction.addToBackStack(null);
                    transaction.commitAllowingStateLoss();
                }
                //Log.e("info: ", "After attaching the adapter.");
                Log.i("Checking wilaya value: ", Utils.wilaya);
                if (!Utils.wilaya.equals("Not assigned!")) sortLocation(Utils.wilaya);

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });


        //final Intent intent = new Intent(this, AddTrip.class);

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        //client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    public void initializeDialog() {

        mAuthProgressDialog = new ProgressDialog(this);
        mAuthProgressDialog.setTitle("Signing out");
        mAuthProgressDialog.setMessage("De-authentication in progress ...");
        mAuthProgressDialog.setCancelable(false);

    }

    public void displayToolBar(Toolbar bar) {
        setSupportActionBar(bar);
        bar.setTitle("");
    }

    public void locationRetrieved(String wilaya) {
        //if (wilaya == null) wilaya = "Not assigned!";
        Utils.wilaya = wilaya;
        locationRetrieved = true;
        if (dataRetrieved == true) {
            sortLocation(wilaya);
            StarterFragment secondFragment = new StarterFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, secondFragment);
            transaction.addToBackStack(null);
            transaction.commitAllowingStateLoss();


        }
    }
    public void upList(RecyclerView rv) {

        Log.e("info: ", "We are in 101");

        if (trips.size() != 0) {
            Log.e("info: ", "We are in 102");

            LinearLayoutManager llm = new LinearLayoutManager(TripList.this);
            if (rv != null) {
                Log.e("info: ", "We are in 103");

                rv.setLayoutManager(llm);
                rv.setHasFixedSize(true);
                RVAdapter adapter = new RVAdapter(trips, this);
                Log.i("Num of Trips found: ", Integer.toString(adapter.getItemCount()));
                rv.setAdapter(adapter);
            }
        }
        else {
           // tripsNum.setVisibility(TextView.VISIBLE);
            //tripsNum.setText("Il n'y a pas de voyages disponibles pour le moment.");
        }

    }

    public void sortLocation(String province) {
        Log.i("Sorting: ", "Here. Province is: " + province);
        for (int i = 0; i < 48; i++) {
            Log.i("Sorting: ", "List of provinces: " + wilaya1[i]);
            if (province.equals(wilaya1[i].toString())) {
                province = wilaya0[i].toString(); break;
            }
        }
        Log.i("Sorting: ", "Province after modification: " + province);
        int toPos = 0;
        for (int i = 0; i < trips.size(); i++) {
            if (firstWord(trips.get(i).getDeparture()).equals(province)) {
                Log.i("Loop info: ", "Province is: " + province + " and array value is: " + firstWord(trips.get(i).getDeparture()) + " Match number: " + toPos);
                Trip match = trips.get(i);
                for (int j = i; j != toPos; j -= 1) {
                    trips.set(j, trips.get(j - 1));
                }
                trips.set(toPos, match);
                toPos ++;
            }
        }

        for (int i = 0; i < trips.size(); i++) {
            Log.i("Sorting: ", trips.get(i).getDeparture());
        }

    }

    public String firstWord(String str) {
        String[] split = str.split(" ");
        return split[0];
    }

    @Override
    public void onBackPressed() {
        final Bundle extras = getIntent().getExtras();
        if (extras != null) {
            Intent intent = new Intent(this, TripList.class);
            startActivity(intent);
        }
        else {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }

    }




    public void isDriver(View v) {
        final Animation myAnim = AnimationUtils.loadAnimation(this, R.anim.bounce);
        v.startAnimation(myAnim);
        Intent intent = new Intent(TripList.this, AddTrip.class);
        startActivity(intent);

    }

    public void isPassenger(View v) {
        // Create fragment and give it an argument specifying the article it should show
        MainActivityFragment thirdFragment = new MainActivityFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Log.i(LOG_TAG, "Step two ...");
        final Animation myAnim = AnimationUtils.loadAnimation(this, R.anim.bounce);
        v.startAnimation(myAnim);
        transaction.replace(R.id.fragment_container, thirdFragment);
        transaction.addToBackStack(null);
        transaction.commit();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        final Bundle extras = getIntent().getExtras();
        if (extras != null) {
            MenuItem all = menu.findItem(R.id.action_all);
            all.setVisible(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_all) {
            Intent intent = new Intent(TripList.this, TripList.class);
            startActivity(intent);
            return true;
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            mAuthProgressDialog.show();
            FirebaseAuth.getInstance().signOut();
            mAuthProgressDialog.dismiss();
            Intent intent = new Intent(TripList.this, LoginActivity.class);
            startActivity(intent);
            return true;
        }

        if (id == R.id.action_add) {
            Intent intent = new Intent(TripList.this, AddTrip.class);
            startActivity(intent);
            return true;
        }

        if (id == R.id.action_about) {
            Intent intent = new Intent(TripList.this, About.class);
            startActivity(intent);
            return true;
        }

        if (id == R.id.action_contact) {
            Intent intent = new Intent(TripList.this, Contact.class);
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

        if (id == R.id.action_search) {
            Intent intent = new Intent(TripList.this, Search.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mAuthListener != null) {
            mAuth.addAuthStateListener(mAuthListener);
        }

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
        Log.i(LOG_TAG, "This Activity is onStop state ...");
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

}
