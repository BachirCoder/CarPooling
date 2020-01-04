package com.bachir.owner.blayes;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import com.bachir.owner.blayes.login.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Starter extends AppCompatActivity implements View.OnClickListener {

    private static final String LOG_TAG = Starter.class.getSimpleName();
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private Button driver, passenger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.i(LOG_TAG, "onAuthStateChanged:signed_in");

                } else {
                    // User is signed out
                    Log.i(LOG_TAG, "onAuthStateChanged:signed_out");
                    //FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(Starter.this, LoginActivity.class);
                    startActivity(intent);
                }
                // ...
            }
        };

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_starter);
        driver = (Button) findViewById(R.id.btn1);
        passenger = (Button) findViewById(R.id.btn2);

        driver.setOnClickListener(this);
        passenger.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        if (v == driver) {
            final Animation myAnim = AnimationUtils.loadAnimation(this, R.anim.bounce);
            v.startAnimation(myAnim);
            Intent intent = new Intent(Starter.this, AddTrip.class);
            startActivity(intent);
        }

        if (v == passenger) {
            final Animation myAnim = AnimationUtils.loadAnimation(this, R.anim.bounce);
            v.startAnimation(myAnim);
            Intent intent = new Intent(Starter.this, TripList.class);
            startActivity(intent);
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
}
