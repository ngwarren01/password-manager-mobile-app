package my.edu.utar.pwmanager;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;
import my.edu.utar.pwmanager.database.PwDB;
import my.edu.utar.pwmanager.Fragments.mail.MailViewModel;
import com.google.android.material.switchmaterial.SwitchMaterial;
import java.io.IOException;
import java.security.GeneralSecurityException;
import ir.androidexception.roomdatabasebackupandrestore.Backup;
import ir.androidexception.roomdatabasebackupandrestore.OnWorkFinishListener;
import ir.androidexception.roomdatabasebackupandrestore.Restore;

public class Settings extends AppCompatActivity {

    final String PREFS_NAME = "appEssentials";
    SharedPreferences sharedPreferences = null;
    MasterKey masterKey = null;

    SharedPreferences UIPref;
    String PREF_DARK = "DARK_THEME";

    String PREF_KEY_SECURE_CORE_MODE = "SECURE_CORE";
    boolean secureCodeModeState;
    String PREF_KEY_SCM_COPY = "SCM_COPY";
    String PREF_KEY_SCM_SCREENSHOTS = "SCM_SCREENSHOTS";
    String NO_DATA = "NO DATA";
    String TYPE_PASS_1 = "PIN";
    String TYPE_PASS_2 = "PASSWORD";

    String PREF_KEY = "MASTER_PASSWORD";
    String PACKAGE_NAME;
    TextView change_password, delete_data, about_app;
    ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        //retrieve package name
        PACKAGE_NAME = getApplicationContext().getPackageName();

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

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

        //Retrieve ID
        change_password = findViewById(R.id.change_master_password);
        delete_data = findViewById(R.id.delete_all_data);
        about_app = findViewById(R.id.about_app);
        progressBar = findViewById(R.id.progress_bar);
        final SwitchMaterial secureCoreModeSwitch = findViewById(R.id.secure_core_mode);
        final SwitchMaterial dark_theme = findViewById(R.id.ask_dark_theme);

        //Retrieve conditions
        secureCodeModeState = sharedPreferences.getBoolean(PREF_KEY_SECURE_CORE_MODE, false);
        final boolean askPasswordLaunchState = sharedPreferences.getBoolean(PREF_KEY, true);
        secureCoreModeSwitch.setChecked(secureCodeModeState);

        //Set theme mode
        UIPref = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean onDarkTheme = UIPref.getBoolean(PREF_DARK, false);
        if (onDarkTheme) {
            dark_theme.setChecked(onDarkTheme);
        }

        final SharedPreferences.Editor editor = sharedPreferences.edit();


        final SharedPreferences.Editor UIEditor = UIPref.edit();

        //Button to set dark/light theme
        dark_theme.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // Enable Dark theme
                    AppCompatDelegate.setDefaultNightMode(
                            AppCompatDelegate.MODE_NIGHT_YES
                    );
                    UIEditor.putBoolean(PREF_DARK, true).apply();
                } else {
                    // Disable Dark theme
                    AppCompatDelegate.setDefaultNightMode(
                            AppCompatDelegate.MODE_NIGHT_NO
                    );
                    UIEditor.putBoolean(PREF_DARK, false).apply();
                }
            }
        });

        //Creates listener for Secure Mode button
        secureCoreModeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // Removing
                    secureCodeMode(true);
                    editor.putBoolean(PREF_KEY_SECURE_CORE_MODE, true).apply();
                } else {
                    secureCodeMode(false);
                    editor.putBoolean(PREF_KEY_SECURE_CORE_MODE, false).apply();
                }
            }
        });
    }

    //SecureCodeMode functions
    private void secureCodeMode(boolean state) {
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        if (state) {

            //remove copy to clipboard and screenshot ability
            editor.putBoolean(PREF_KEY_SCM_COPY, false).apply();
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
            Toast.makeText(getApplicationContext(), "Success. Restart app to apply changes", Toast.LENGTH_LONG).show();
        } else {

            //set copy to clipboard and screenshot ability
            editor.putBoolean(PREF_KEY_SCM_SCREENSHOTS, true).apply();
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_SECURE);
            Toast.makeText(getApplicationContext(), "Secure code mode is inactive", Toast.LENGTH_LONG).show();
        }
    }


    //Change password/PIN
    public void changePassword(View view) {
        TextView PIN = findViewById(R.id.change_master_password_option_1);
        PIN.setVisibility(View.VISIBLE);

    }

    public void changePasswordToPIN(View view) {
        Intent intent = new Intent(getApplicationContext(), ChangePassword.class);
        intent.putExtra(ChangePassword.EXTRA_TYPE_PASS, TYPE_PASS_1);
        startActivity(intent);
    }


    public void deleteAllData(View view) {

        //Build Alert Dialog
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Delete All Data");
        alertDialogBuilder.setMessage("Do you want to delete everything?");
        alertDialogBuilder.setCancelable(false);

        //YES buton
        alertDialogBuilder.setPositiveButton("yes", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                MailViewModel passwordViewModel = new MailViewModel(getApplication());
                progressBar.setVisibility(View.VISIBLE);
                //deletes all data
                passwordViewModel.deleteAllNotes();
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(NO_DATA, false).apply();
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), "Deleted", Toast.LENGTH_SHORT).show();
            }
        });
        //NO button
        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }

    //Intent linking current activity to next activity
    public void aboutApp(View view) {
        startActivity(new Intent(this, About.class));
    }

    //IF app icon in action bar clicked, go to parent activity.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}