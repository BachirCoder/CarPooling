package com.bachir.owner.blayes;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import static android.content.ContentValues.TAG;

/**
 * Created by Owner on 4/7/2017.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {


    public MyFirebaseInstanceIDService(){
        onTokenRefresh();
    }

    String token = "";

    public String getToken(){
        return token;
    }

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        token = refreshedToken;
        Log.d(TAG, "Refreshed token: " + refreshedToken);
        //return refreshedToken;

    }

}
