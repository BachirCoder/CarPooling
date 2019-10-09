package com.bachir.owner.blayes;

import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


/**
 * Created by Owner on 4/8/2017.
 */

public class NotificationService extends FirebaseMessagingService {

    String var = "Nothing";

    public NotificationService(){}

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        var = remoteMessage.getData().get("post");
        if (var != null) {
            SharedPreferences pref = this.getSharedPreferences("MyPref", MODE_PRIVATE);
            final SharedPreferences.Editor editor = pref.edit();
            editor.putString("id", var);
            editor.commit();
            for (int i = 0; i < 10; ++i) {
                Log.d("Post ID is: ", var);
            }
        }

    }
}
