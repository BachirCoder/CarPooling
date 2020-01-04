package com.bachir.owner.blayes;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bachir.owner.blayes.login.LoginActivity;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Contact extends AppCompatActivity {

    private static final String LOG_TAG = Contact.class.getSimpleName();
    private String email, mEncodedEmail, subject, text, uid, name;
    EditText sub, txt;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Firebase.setAndroidContext(this);
        final Firebase ref = new Firebase(Constants.FIREBASE_URL_MESSAGES);
        setContentView(R.layout.activity_contact);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        final Context varCon = this;

        if (!Utils.isConnectedToInternet(this)) {
            Toast.makeText(Contact.this, R.string.connection_error,
                    Toast.LENGTH_LONG).show();
        }

        sub = (EditText) findViewById(R.id.sujet);
        txt = (EditText) findViewById(R.id.message);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    uid = user.getUid();
                    Log.d(LOG_TAG, "onAuthStateChanged:signed_in:" + uid);
                    email = user.getEmail().toLowerCase();
                    mEncodedEmail = Utils.encodeEmail(email);
                    final Firebase userLocation = new Firebase(Constants.FIREBASE_URL_USERS).child(mEncodedEmail);

                    userLocation.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            try {
                                final User myuser = dataSnapshot.getValue(User.class);
                            if (myuser != null) {
                                name = myuser.getName();
                                //creator = myuser.getEmail();
                            }

                        }catch (Exception e) {
                            Toast.makeText(Contact.this, R.string.error_account, Toast.LENGTH_LONG).show();
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
        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    newMsg(ref, sub, txt, uid, name, email);
                }
                catch (Exception e) {
                    Snackbar.make(view, "Votre message ne peut pas passer! Contactez-nous sur: blayes.dev@gmail.com", Snackbar.LENGTH_LONG).
                            setAction("Action", null).show();
                }

                if (!Utils.isConnectedToInternet(Contact.this)) {
                    Toast.makeText(Contact.this, R.string.connection_error,
                            Toast.LENGTH_LONG).show();
                }

            }
        });

    }

    public void newMsg(Firebase ref, EditText subject, EditText text, String uid, String name, String email){

        String sub = subject.getText().toString();
        String txt = text.getText().toString();
        Message msg = new Message(sub, txt, uid, name, email);
        ref.push().setValue(msg, new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                if (firebaseError != null) {
                    System.out.println("Data could not be saved. " + firebaseError.getMessage());

                    Toast.makeText(Contact.this, "Le message n'a pas été envoyé: " + firebaseError.getMessage(),
                            Toast.LENGTH_SHORT).show();

                } else {
                    System.out.println("Data saved successfully.");

                    Toast.makeText(Contact.this, "Message envoyé.",
                            Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Contact.this, TripList.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(intent);
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_contact, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_home) {
            Intent intent = new Intent(Contact.this, TripList.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
            return true;
        }

        if (id == R.id.action_add) {
            Intent intent = new Intent(Contact.this, AddTrip.class);
            startActivity(intent);
            return true;
        }

        if (id == R.id.action_about) {
            Intent intent = new Intent(Contact.this, About.class);
            startActivity(intent);
            return true;
        }

        if (id == R.id.action_logout) {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(Contact.this, LoginActivity.class);
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

}
