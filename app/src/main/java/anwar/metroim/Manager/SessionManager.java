package anwar.metroim.Manager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.provider.ContactsContract;

import java.util.HashMap;

import anwar.metroim.CustomImage.getCustomImage;
import anwar.metroim.LoginActivity;

/**
 * Created by anwar on 2/17/2017.
 */

public class SessionManager {
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;
    int PRIVATE_MODE = 0;
    private static final String PREF_NAME = "MetroIMPref";
    private static final String IS_LOGIN ="IsLoggedIn";
    public static final String KEY_PHONE = "phone";
    public static final String KEY_STATUS = "status";
    public static final String KEY_TYPE = "ac_type";
    public static final String KEY_PHOTO = "photo";
    public static final String KEY_NAME = "name";
    public static final String KEY_PASSWORD = "passward";
    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }
    public void createLoginSession(String phone, String password){
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(KEY_PHONE,phone );
        editor.putString(KEY_PASSWORD, password);
        editor.commit();
    }
    public void createinfoSession(String nam,String st, String ph,String ac_type){
        try{
            editor.putString(KEY_NAME,nam );
            editor.putString(KEY_STATUS,st );
            editor.putString(KEY_PHOTO, ph);
            editor.putString(KEY_TYPE, ac_type);
            editor.commit();
        }catch (NullPointerException ex){}
    }
    public HashMap<String, String> getUserDetails(){
        HashMap<String, String> user = new HashMap<String, String>();
        user.put(KEY_PHONE, pref.getString(KEY_PHONE, null));
        user.put(KEY_PASSWORD, pref.getString(KEY_PASSWORD, null));
        user.put(KEY_STATUS, pref.getString(KEY_STATUS, null));
        user.put(KEY_PHOTO, pref.getString(KEY_PHOTO, null));
        user.put(KEY_NAME, pref.getString(KEY_NAME, null));
        user.put(KEY_TYPE,pref.getString(KEY_TYPE,null));
        return user;
    }
    public void checkLogin(){
        if(!this.isLoggedIn()){

        }

    }
    public void logoutUser(){
        editor.clear();
        editor.commit();
    }
    public boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGIN, false);
    }
}
