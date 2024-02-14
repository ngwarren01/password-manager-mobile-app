package my.edu.utar.pwmanager;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class SplashArt extends AppCompatActivity {

    final String PREFS_NAME = "appEssentials";
    SharedPreferences sharedPreferences = null;
    MasterKey masterKey = null;

    String PREF_KEY = "MASTER_PASSWORD";
    String PREF_DARK = "DARK_THEME";
    String PREF_KEY_FRUN = "FIRST RUN";

    //timeout timer for splash art
    private static int timeout = 2000;


    // Sets gradient on statusbar
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void setStatusBarGradiant(Activity activity) {
        Window window = activity.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(activity, R.color.bg_color_splash));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Checks whether Light/Dark MODE
        SharedPreferences UIPref = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean UI = UIPref.getBoolean(PREF_DARK, false);

        if (UIPref.getBoolean(PREF_DARK, false)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        setStatusBarGradiant(this);

        setContentView(R.layout.activity_splash);

        //Retrieve ID
        TextView password_manager = findViewById(R.id.password_manager);
        password_manager.setText("Secure+");

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Encrypted SharedPrefs
                try {
                    //MK.security
                    masterKey = new MasterKey.Builder(getApplicationContext(), MasterKey.DEFAULT_MASTER_KEY_ALIAS)
                            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                            .build();

                    //initialize sharedPef
                    sharedPreferences = EncryptedSharedPreferences.create(
                            getApplicationContext(),
                            PREFS_NAME,
                            masterKey,
                            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
                    );
                } catch (GeneralSecurityException | IOException e) {
                    e.printStackTrace();
                }
                //Checks for firstTime and PasswordOnLaunch conditions
                final boolean askPasswordLaunchState = sharedPreferences.getBoolean(PREF_KEY, true);
                final boolean firstRun = sharedPreferences.getBoolean(PREF_KEY_FRUN, true);
                if (firstRun) {
                    //if first time using the app
                    //play welcome slides
                    startActivity(new Intent(SplashArt.this, WelcomeScreen.class));
                } else {
                    //!FirstTime
                    if (askPasswordLaunchState) {
                        //If Password required, proceeds to MLock
                        startActivity(new Intent(SplashArt.this, MasterLock.class));
                    } else {
                        //If Password not required, proceeds to home
                        startActivity(new Intent(SplashArt.this, Home.class));
                        Toast.makeText(getApplicationContext(), "Consider using password", Toast.LENGTH_SHORT).show();
                    }
                }
                finish();
            }
            //Timeout timer set for 2seconds
        }, timeout);
    }
}