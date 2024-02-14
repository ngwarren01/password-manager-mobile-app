package my.edu.utar.pwmanager.Utilities;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefManager {

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;

    // Shared preferences file name
    private static final String SharedPref_filename = "appEssentials";

    private static final String firstTime = "IsFirstTimeLaunch";

    public SharedPrefManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(SharedPref_filename, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public void setFirstTimeLaunch(boolean isFirstTime) {
        editor.putBoolean(firstTime, isFirstTime);
        editor.apply();
    }

    public boolean isFirstTimeLaunch() {
        return pref.getBoolean(firstTime, true);
    }

}