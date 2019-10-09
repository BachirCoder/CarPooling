package com.bachir.owner.blayes.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bachir.owner.blayes.R;
import com.bachir.owner.blayes.Utils;
import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class PassReset extends AppCompatActivity {

    private static final String LOG_TAG = CreateAccountActivity.class.getSimpleName();
    private ProgressDialog mAuthProgressDialog;
    private String error;
    private EditText mEditTextUsernameCreate, mEditTextEmailCreate, mEditTextPasswordCreate;
    private String mUserName, mUserEmail, mPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Firebase.setAndroidContext(this);
        setContentView(R.layout.activity_pass_reset);
        final EditText email = (EditText) findViewById(R.id.reset_pass_email);
        final String emailAddress = email.getText().toString();
        final Button post = (Button) findViewById(R.id.PassReset);
        initializeDialog();
        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailAddress = email.getText().toString();
                if (!emailAddress.equals("") && Utils.isConnectedToInternet(PassReset.this)) {
                    mAuthProgressDialog.show();
                    Log.i(LOG_TAG, emailAddress);
                    FirebaseAuth auth = FirebaseAuth.getInstance();


                    try {
                        auth.sendPasswordResetEmail(emailAddress)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            // do something when mail was sent successfully.
                                            mAuthProgressDialog.dismiss();
                                            Toast.makeText(PassReset.this, "A password reset email is successfully sent. Check your mail box.",
                                                    Toast.LENGTH_LONG).show();
                                            Intent intent = new Intent(PassReset.this, LoginActivity.class);
                                            startActivity(intent);
                                        } else {
                                            // ...
                                            mAuthProgressDialog.dismiss();
                                            Toast.makeText(PassReset.this, "Invalid email!",
                                                    Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                    } catch (Exception e) {
                        mAuthProgressDialog.dismiss();
                        Snackbar.make(view, "Undefined Error. Contact our facebook page for help.", Snackbar.LENGTH_LONG).
                                setAction("Action", null).show();
                    }


                }
                else if (emailAddress.equals("")) email.setError(getString(R.string.error_cannot_be_empty));

                else if (!Utils.isConnectedToInternet(PassReset.this)) {
                    Toast.makeText(PassReset.this, R.string.connection_error,
                            Toast.LENGTH_LONG).show();
                }

            }
        });

    }


    public void initializeDialog() {

        mAuthProgressDialog = new ProgressDialog(this);
        mAuthProgressDialog.setTitle("Email verification");
        mAuthProgressDialog.setMessage("Checking user database ...");
        mAuthProgressDialog.setCancelable(false);

    }

    public void onSignInPressed(View view) {
        Intent intent = new Intent(PassReset.this, LoginActivity.class);
        startActivity(intent);
    }
}
