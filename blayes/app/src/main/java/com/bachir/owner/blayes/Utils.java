package com.bachir.owner.blayes;

/**
 * Created by Owner on 1/25/2017.
 */

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.bachir.owner.blayes.Constants.wilaya;

/**
 * Utility class
 */
public class Utils {
    /**
     * Format the timestamp with SimpleDateFormat
     */
    public static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private Context mContext = null;
    public static String wilaya = "Not assigned!";
    //public static Boolean firstLaunch = true;

    public static String encodeEmail(String userEmail) {
        return userEmail.replace(".", ",");
    }
    public static String decodeEmail(String userEmail) {
        return userEmail.replace(",", ".");
    }

    public static boolean isConnectedToInternet(Context var){
        ConnectivityManager connectivity = (ConnectivityManager)var.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null)
        {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED)
                    {
                        return true;
                    }

        }
        return false;
    }

    public static boolean ValidatePhoneNumber(String num){
        num = num.trim();
        Pattern pattern = Pattern.compile("0\\d{9}"); // "\\d{3}-\\d{7}"
        Matcher matcher = pattern.matcher(num);

        if (matcher.matches()) {
            return true;
        }
        else
        {
            return false;
        }
    }

    public static void exitApp() {

    }

    public static void call(String contact) {

    }
    /**
     * Public constructor that takes mContext for later use
     */
    public Utils(Context con) {
        mContext = con;
    }

    /**
     * Encode user email to use it as a Firebase key (Firebase does not allow "." in the key name)
     * Encoded email is also used as "userEmail", list and item "owner" value
     */


}