package com.example.foodwastemanagement;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionHelper {


    private final String KEY_USER_NAME = "user_name";
    private final String KEY_USER_ID = "user_id";
    private final String KEY_FIREBASE_TOKEN = "firebase_token";
    private final String KEY_USER_TYPE = "user_type";
    private final String KEY_FIRST_TIME = "first_time";

    public static boolean JustLoggedIn = false;



    private String KEY_LAST_KNOWN_LON = "last_known_lon";
    private String KEY_LAST_KNOWN_LAT = "last_known_lat";


    private String PREF_NAME = "FoodWasteManagement";

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    private Context context;

    public SessionHelper(Context context) {
        this.context = context;
        preferences = context.getSharedPreferences (PREF_NAME, Context.MODE_PRIVATE);
        editor = preferences.edit ();
    }

    public String getUserName() {
        return preferences.getString (KEY_USER_NAME, "");
    }

    public String getUserId() {
        return preferences.getString (KEY_USER_ID, "");
    }

    public String getFirebaseToken() {
        return preferences.getString (KEY_FIREBASE_TOKEN, "");
    }


    public void setFirebaseToken(String token) {
        editor.putString (KEY_FIREBASE_TOKEN, token);
        editor.commit ();
    }

    public boolean isFirstLogin() {
        return preferences.getBoolean (KEY_FIRST_TIME, true);
    }


    public void setFirstLogin(boolean firstLogin) {
        editor.putBoolean (KEY_FIREBASE_TOKEN, firstLogin);
        editor.commit ();
    }

    public String getUserType() {
        return preferences.getString (KEY_USER_TYPE, "");
    }


    public void setUserType(String userType) {
        editor.putString (KEY_USER_TYPE, userType);
        editor.commit ();
    }
    public void setUserName(String userName) {
        editor.putString (KEY_USER_NAME, userName);
        editor.commit ();
    }
    public void setUserId(String userId) {
        editor.putString (KEY_USER_ID, userId);
        editor.commit ();
    }

    public String getLastKnownLON() {
        return preferences.getString (KEY_LAST_KNOWN_LON, "");
    }

    public void setLastKnownLON(String lastKnownLon) {
        editor.putString (KEY_LAST_KNOWN_LON, lastKnownLon);
        editor.commit ();
    }

    public String getLastKnownLAT() {
        return preferences.getString (KEY_LAST_KNOWN_LAT, "");
    }

    public void setLastKnownLAT(String lastKnownLAT) {
        editor.putString (KEY_LAST_KNOWN_LAT, lastKnownLAT);
        editor.commit ();
    }
}
