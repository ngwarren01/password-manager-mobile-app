package my.edu.utar.pwmanager;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class About extends AppCompatActivity {

    TextView tv;

    String version;
    //What's new feature String
    String[] whatsnew = {
            "Added Night Mode",
            "Added Secure Mode",
            "Added FingerPrint Authentication",
            "Added Random Password Generator",};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        //Set version text
        version = "Version 1.0" ;

        tv = findViewById(R.id.version);
        tv.setText(version);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }

        //List whatsnew array in the features listview
        ArrayAdapter adapter = new ArrayAdapter(this, R.layout.list_whats_new, whatsnew);
        ListView listView = findViewById(R.id.listView);
        listView.setAdapter(adapter);
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