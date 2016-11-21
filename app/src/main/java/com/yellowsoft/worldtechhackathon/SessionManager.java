package com.yellowsoft.worldtechhackathon;

/**
 * Created by subhankar on 11/21/2016.
 */
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class SessionManager {
    // LogCat tag
    private static String TAG = SessionManager.class.getSimpleName();

    // Shared Preferences
    SharedPreferences pref;

    SharedPreferences.Editor editor;
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String PREF_NAME = "Yellow";

    private static final String KEY_IS_LOGGEDIN = "isLoggedIn";
    private static final String KEY_NAME   = "name";
    private static final String KEY_ID   = "id";
    private static final String KEY_EMAIL   = "email";

    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setLogin(boolean isLoggedIn, String id, String email, String name) {

        editor.putBoolean(KEY_IS_LOGGEDIN, isLoggedIn);
        editor.putString(KEY_ID, id);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_NAME, name);

        // commit changes
        editor.commit();

        Log.d(TAG, "User login session modified!");
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGEDIN, false);
    }

    public String getId() { return pref.getString(KEY_ID, null); }

    public String getName() { return pref.getString(KEY_NAME, null); }

    public String getEmail() { return pref.getString(KEY_EMAIL, null); }
}
