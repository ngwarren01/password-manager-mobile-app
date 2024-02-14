package my.edu.utar.pwmanager;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import my.edu.utar.pwmanager.classFramework.PwClass;
import my.edu.utar.pwmanager.Fragments.mail.MailViewModel;
import com.github.javiersantos.appupdater.AppUpdater;
import com.github.javiersantos.appupdater.enums.Display;
import com.google.android.material.navigation.NavigationView;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Random;

public class Home extends AppCompatActivity {


    final String PREFS_NAME = "appEssentials";
    SharedPreferences sharedPreferences;
    String PREF_KEY_SECURE_CORE_MODE = "SECURE_CORE";
    MasterKey masterKey = null;

    public static final String NO_DATA = "NO DATA";
    private static final int ADD_RECORD = 1;
    private static final String TAG = "HOME";
    private static final String PROVIDER = "mail";

    String PASSWORD = "";

    private static final String collection = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*_=+-";

    AlertDialog.Builder builder;
    boolean secureCodeModeState;
    private AppBarConfiguration mAppBarConfiguration;
    AppUpdater appUpdater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getIntent().setAction("1");
        setContentView(R.layout.activity_home);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Retrieve ID
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        View view = navigationView.getHeaderView(0);
        ImageButton imageButton = view.findViewById(R.id.refresh);
        final TextView textView1 = view.findViewById(R.id.generate_password);
        builder = new AlertDialog.Builder(this);

        // Encrypted SharedPrefs
        try {
            //MK.security
            masterKey = new MasterKey.Builder(getApplicationContext(), MasterKey.DEFAULT_MASTER_KEY_ALIAS)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();

            //initialize sharedPref
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

        //Checks for SECURE CORE mode
        secureCodeModeState = sharedPreferences.getBoolean(PREF_KEY_SECURE_CORE_MODE, false);

        if (secureCodeModeState) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        }

        //Passing mail/social ID
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_password,
                R.id.nav_social)
                .setDrawerLayout(drawer)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        //Initial random password generator
        String Password = generatePassword();
        textView1.setText(Password);

        //Listen Activity for password refresh button
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nav_refresh();
            }
        });

        //Display notification
        appUpdater = new AppUpdater(this)
                .showEvery(3)
                .setDisplay(Display.NOTIFICATION)
                .setDisplay(Display.DIALOG);
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (sharedPreferences.getBoolean("FIRSTNOTICE", true)) {

            //Build ALert Dialog
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle("Notice");
            alertDialogBuilder.setMessage("SECURE+ is still in beta, you might face some bugs while using the app.");

            //OK Button
            alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("FIRSTNOTICE", false).apply();
                }
            });
            //Negative button
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();

            if (!secureCodeModeState)
                Log.d("Update", String.valueOf(secureCodeModeState));
            appUpdater.start();
        }
    }


    // Inflates the menu; this adds items to the action bar if it is present.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }


    //Intent linking to other activity
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent(getApplicationContext(), Settings.class));
                return true;
            case R.id.action_help:
                startActivity(new Intent(getApplicationContext(), Help.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //Functions: copy random password
    public void copy(View view) {

        if (sharedPreferences.getBoolean(PREF_KEY_SECURE_CORE_MODE, false)) {

            ImageButton copyImage = findViewById(R.id.copy);

            //Checks for SECURE CORE MODE, if enabled, not allowed to copy
            copyImage.setEnabled(false);
            Toast.makeText(this, "Secure code mode is Enabled. Copying is not allowed  ", Toast.LENGTH_SHORT).show();

        } else {
            TextView textView = findViewById(R.id.generate_password);
            final String gn_password = textView.getText().toString().trim();

            //Clipboard manager to copy strings to clipboard
            ClipboardManager clipboard = (ClipboardManager)
                    getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Password", gn_password);

            if (clipboard != null) {
                //Copies strings to clipboard
                clipboard.setPrimaryClip(clip);
                Toast.makeText(getApplicationContext(), "Copied!", Toast.LENGTH_SHORT).show();
            }

            builder.setMessage("Do you want to add this password to the database?")
                    .setTitle("Alert")
                    .setCancelable(true)
                    //Set listener, if yes, copy to clipboard
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //If yes, Calls to Add Activity
                            Intent intent = new Intent(getApplicationContext(), AddEntry.class);

                            //cast the copied strings into PASSWORD field
                            intent.putExtra(PASSWORD, gn_password);
                            startActivityForResult(intent, ADD_RECORD);
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        //"No" button to decline
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
        }
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == ADD_RECORD && resultCode == RESULT_OK) {

            //Attributes
            String providerName = data.getStringExtra(AddEntry.EXTRA_PROVIDER_NAME);
            String enc_passwd = data.getStringExtra(AddEntry.EXTRA_ENCRYPT);
            String enc_email = data.getStringExtra(AddEntry.EXTRA_EMAIL);

            //Create new PwClass that store credential
            PwClass pwClass = new PwClass(PROVIDER, providerName, enc_email, enc_passwd);
            Log.d(TAG, "Provider: " + PROVIDER + " EMAIL: " + enc_email + " ENC_DATA: " + enc_passwd);

            SharedPreferences sharedPreferences = this.getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

            MailViewModel passwordViewModel = new ViewModelProvider(this).get(MailViewModel.class);

            //insert data into viewmodel
            passwordViewModel.insert(pwClass);
            Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_SHORT).show();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private String generatePassword() {

        //Creating Random object
        Random random = new Random();

        //Limits  length of the generated password
        int limit = (int) (Math.random() * 10 + 5);

        //Build String using string builder
        StringBuilder password = new StringBuilder();
        for (int itr = 0; itr < limit; itr++) {
            password.append(collection.charAt(random.nextInt(collection.length())));
        }
        return password.toString();
    }

    public void nav_refresh() {
        // refresh/generate new password on textview
        TextView textView = findViewById(R.id.generate_password);
        String Password = generatePassword();
        textView.setText(Password);
    }


}
