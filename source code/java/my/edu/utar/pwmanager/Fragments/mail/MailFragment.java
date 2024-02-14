package my.edu.utar.pwmanager.Fragments.mail;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import my.edu.utar.pwmanager.AddEntry;
import my.edu.utar.pwmanager.ModifyEntry;
import my.edu.utar.pwmanager.R;
import my.edu.utar.pwmanager.Utilities.RecyclerViewAdapter;
import my.edu.utar.pwmanager.classFramework.PwClass;
import my.edu.utar.pwmanager.database.PwRepos;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import static android.app.Activity.RESULT_OK;

// Mails
public class MailFragment extends Fragment {

    String PREF_NAME = "Settings";
    String PREF_KEY_SECURE_CORE_MODE = "SECURE_CORE";

    public static final String NO_DATA = "NO DATA";
    private static final int ADD_RECORD = 1;
    private static final int MODIFY_RECORD = 2;
    private static final int DELETE_RECORD = 3;

    private static final String PROVIDER = "mail";
    private static final String TAG = "P FRAG ";
    boolean status = false;

    private TextView empty;
    private MailViewModel passwordViewModel;
    private PwRepos repository;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_password, container, false);
        ProgressBar progressBar = root.findViewById(R.id.progress_bar);
        FloatingActionButton fab = root.findViewById(R.id.fab);

        empty = root.findViewById(R.id.empty);

        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences(PROVIDER, Context.MODE_PRIVATE);

        SharedPreferences sp = this.getActivity().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        if (sp.getBoolean(PREF_KEY_SECURE_CORE_MODE, false)) {
            try {
                ImageButton copyImage = root.findViewById(R.id.copy);
                copyImage.setEnabled(false);
            } catch (Exception e) {
                e.getStackTrace();
            }
            getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        }

        status = sharedPreferences.getBoolean(NO_DATA, false);
        if (status) {
            empty.setVisibility(View.GONE);
        } else {
            empty.setText(NO_DATA);
        }

        RecyclerView recyclerView = root.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerView.setHasFixedSize(true);

        //Declare viewAdapter
        final RecyclerViewAdapter viewAdapter = new RecyclerViewAdapter();
        recyclerView.setAdapter(viewAdapter);


        //Declare ViewModel for pw
        passwordViewModel = new ViewModelProvider(this).get(MailViewModel.class);

        //Retrieving email entries
        passwordViewModel.getAllMails().observe(getViewLifecycleOwner(), new Observer<List<PwClass>>() {
            @Override
            public void onChanged(List<PwClass> pwCreds) {
                viewAdapter.setCreds(pwCreds);
            }
        });


        //Defining the gestures action on screen
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                passwordViewModel.delete(viewAdapter.getCredAt(viewHolder.getAdapterPosition()));
                Toast.makeText(getContext(), "Entry deleted", Toast.LENGTH_SHORT).show();
            }
        };
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);


        //Declaring next activity using intent
        viewAdapter.setOnItemClickListener(new RecyclerViewAdapter.onItemClickListener() {
            @Override
            public void onItemClick(PwClass pwClass) {
                Intent intent = new Intent(getActivity(), ModifyEntry.class);
                intent.putExtra(ModifyEntry.EXTRA_ID, pwClass.getId());
                intent.putExtra(ModifyEntry.EXTRA_PROVIDER_NAME, pwClass.getProviderName());
                intent.putExtra(ModifyEntry.EXTRA_EMAIL, pwClass.getEmail());
                intent.putExtra(ModifyEntry.EXTRA_ENCRYPT, pwClass.getCat());
                startActivityForResult(intent, MODIFY_RECORD);
            }
        });


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AddEntry.class);
                intent.putExtra(AddEntry.EXTRA_PROVIDER, PROVIDER);
                startActivityForResult(intent, ADD_RECORD);

            }
        });
        return root;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == ADD_RECORD && resultCode == RESULT_OK) {

            String providerName = data.getStringExtra(AddEntry.EXTRA_PROVIDER_NAME);
            String enc_passwd = data.getStringExtra(AddEntry.EXTRA_ENCRYPT);
            String enc_email = data.getStringExtra(AddEntry.EXTRA_EMAIL);

            PwClass pwClass = new PwClass(PROVIDER, providerName, enc_email, enc_passwd);

            //Showing "No data" or not on activity if list is empty
            SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences(PROVIDER, Context.MODE_PRIVATE);
            sharedPreferences.edit().putBoolean(NO_DATA, true).apply();

            empty.setVisibility(View.GONE);
            passwordViewModel.insert(pwClass);
            //Shows status of the data after succesfully saved
            Toast.makeText(getContext(), "Saved", Toast.LENGTH_SHORT).show();

        } else if (requestCode == MODIFY_RECORD && resultCode == RESULT_OK) {
            int id = data.getIntExtra(ModifyEntry.EXTRA_ID, -1);

            //If cannot update data
            if (id == -1) {
                Toast.makeText(getContext(), "Cannot be updated!", Toast.LENGTH_LONG).show();
                return;
            }

            String providerName = data.getStringExtra(ModifyEntry.EXTRA_PROVIDER_NAME);
            String enc_passwd = data.getStringExtra(ModifyEntry.EXTRA_ENCRYPT);
            String enc_email = data.getStringExtra(ModifyEntry.EXTRA_EMAIL);

            PwClass pwClass = new PwClass(PROVIDER, providerName, enc_email, enc_passwd);

            //Shows status of the data after modify
            pwClass.setId(id);
            if (!data.getBooleanExtra(ModifyEntry.EXTRA_DELETE, false)) {
                // (TAG, "Provider: " + PROVIDER + " EMAIL: " + enc_email + " ENC_DATA: " + enc_passwd);
                passwordViewModel.update(pwClass);
                Toast.makeText(getContext(), "Updated", Toast.LENGTH_SHORT).show();
            } else {
                passwordViewModel.delete(pwClass);
                Toast.makeText(getContext(), "Deleted", Toast.LENGTH_SHORT).show();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
