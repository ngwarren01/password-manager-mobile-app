package my.edu.utar.pwmanager;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import com.andrognito.pinlockview.IndicatorDots;
import com.andrognito.pinlockview.PinLockListener;
import com.andrognito.pinlockview.PinLockView;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
//MLock Master Lock

public class MasterLock extends AppCompatActivity {

    final String PREFS_NAME = "appEssentials";
    SharedPreferences sharedPreferences = null;
    MasterKey masterKey = null;

    final String PREF_KEY = "firstRun";
    final String HASH = "HASH";
    final int DOESNT_EXIST = -1;

    TextView mlock_tv_greet, mlock_tv_pp;
    PinLockView mPinLockView;
    IndicatorDots mIndicatorDots;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarGradiant(this);
        setContentView(R.layout.activity_mlock);

        //Calls function for biometrics authentication
        biometricsAuth();

        //retrieve ID
        mlock_tv_greet = findViewById(R.id.mlock_l_tv_greet);
        mIndicatorDots = findViewById(R.id.indicator_dots);
        mPinLockView = findViewById(R.id.pin_lock_view);

        // Encrypted SharedPrefs
        try {
            //MK.security
            masterKey = new MasterKey.Builder(getApplicationContext(), MasterKey.DEFAULT_MASTER_KEY_ALIAS)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();

            //initialize sharedpPef
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

        //Checks whether is firstTime, if yes display "Create master password"
        if (sharedPreferences.getBoolean(PREF_KEY, true)) {

            mlock_tv_greet.setText(R.string.mlock_st_create_password);

            //Setting biometric auth button to invis, forcing user to set up master password
            findViewById(R.id.launchAuthentication).setVisibility(View.GONE);
        }

        //Create PinLock Listener
        PinLockListener mPinLockListener = new PinLockListener() {
            @Override
            public void onComplete(String pin) {

                //First time logging in, create new HASH, and stores pin
                if (sharedPreferences.getBoolean(PREF_KEY, true)) {

                    sharedPreferences.edit().putString(HASH, pin).apply();
//                  String HASH = new String(Hex.encodeHex(DigestUtils.sha(pin)));
                    sharedPreferences.edit().putBoolean(PREF_KEY, false).apply();

                    Toast.makeText(getApplicationContext(), "Welcome", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), Home.class));
                    finish();
                } else {//compares with HASH, if pin matches, proceeds to Home
                    String sp = sharedPreferences.getString(HASH, "0");
                    if (sp.equals(pin)) {
//                        Toast.makeText(getApplicationContext(), "Successful login", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), Home.class));
                        finish();
                    } else {//if pin does not matches, display "Wrong Password"
                        Toast.makeText(getApplicationContext(), "Wrong password", Toast.LENGTH_SHORT).show();
                    }
                }

            }

            //Checks PIN condition
            @Override
            public void onEmpty() {
//                Log.d(TAG, "Pin empty");
            }

            @Override
            public void onPinChange(int pinLength, String intermediatePin) {
                //Log.d(TAG, "Pin changed, new length " + pinLength + " with intermediate pin " + intermediatePin);
            }
        };

        //Create listener for pinlock using indicator dots
        mPinLockView.setPinLockListener(mPinLockListener);
        mPinLockView.attachIndicatorDots(mIndicatorDots);
    }

    //Functions: biometric authentication
    public void biometricsAuth() {

        //Create single thread
        Executor newExecutor = Executors.newSingleThreadExecutor();
        FragmentActivity activity = this;

        //Start listening for authentication events
        final BiometricPrompt myBiometricPrompt = new BiometricPrompt(activity, newExecutor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            //onAuthenticationError is called when a fatal error occur
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                if (errorCode == BiometricPrompt.ERROR_NEGATIVE_BUTTON) {
                } else {
                    //Print a message to Logcat//
//                    Log.d(TAG, "An unrecoverable error occurred");
                }
            }

            //onAuthenticationSucceeded is called when a fingerprint is matched successfully
            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                //Print a message to Logcat//
                startActivity(new Intent(MasterLock.this, Home.class));
                finish();
//                Log.d(TAG, "Fingerprint recognised successfully");
            }

            //onAuthenticationFailed is called when the fingerprint do not match
            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                //Print a message to Logcat//
//                Log.d(TAG, "Fingerprint not recognised");
            }
        });

        //Create the BiometricPrompt instance//
        final BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                //Add some text to the dialog//
                .setTitle("Fingerprint Authentication")
                .setDescription("Place your finger on the sensor to authenticate")
                .setNegativeButtonText("Cancel")
                //Build the dialog//
                .build();
        findViewById(R.id.launchAuthentication).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myBiometricPrompt.authenticate(promptInfo);
            }
        });
    }

    // Setting Gradient on statusbar
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void setStatusBarGradiant(Activity activity) {
        Window window = activity.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(activity, R.color.bg_color_primary));
    }
}