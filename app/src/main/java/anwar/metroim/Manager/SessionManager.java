package anwar.metroim.Manager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.ContactsContract;

import java.util.HashMap;

import anwar.metroim.LoginActivity;

/**
 * Created by anwar on 2/17/2017.
 */

public class SessionManager {
    SharedPreferences pref;

    // Editor for Shared preferences
    SharedPreferences.Editor editor;

    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREF_NAME = "MetroIMPref";

    // All Shared Preferences Keys
    private static final String IS_LOGIN ="IsLoggedIn";

    // User name (make variable public to access from outside)
    public static final String KEY_PHONE = "phone";
    public static final String KEY_STATUS = "status";
    public static final String KEY_PHOTO = "photo";
    public static final String KEY_NAME = "name";

    // Email address (make variable public to access from outside)
    public static final String KEY_PASSWORD = "passward";

    // Constructor
    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }
    public void createLoginSession(String phone, String password){
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);

        // Storing name in pref
        editor.putString(KEY_PHONE,phone );

        // Storing email in pref
        editor.putString(KEY_PASSWORD, password);

        // commit changes
        editor.commit();
    }
    public void createinfoSession(String nam,String st, String ph){
        // Storing login value as TRUE
        editor.putString(KEY_NAME,nam );
        // Storing name in pref
        editor.putString(KEY_STATUS,st );

        // Storing email in pref
        editor.putString(KEY_PHOTO, ph);

        // commit changes
        editor.commit();
    }
    public HashMap<String, String> getUserDetails(){
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(KEY_PHONE, pref.getString(KEY_PHONE, null));

        // user email id
        user.put(KEY_PASSWORD, pref.getString(KEY_PASSWORD, null));
        user.put(KEY_STATUS, pref.getString(KEY_STATUS, null));
        user.put(KEY_PHOTO, pref.getString(KEY_PHOTO, null));
        user.put(KEY_NAME, pref.getString(KEY_NAME, null));

        return user;
    }
    public void checkLogin(){
        // Check login status
        if(!this.isLoggedIn()){

        }

    }
    public void logoutUser(){
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();
    }
    public boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGIN, false);
    }
}
