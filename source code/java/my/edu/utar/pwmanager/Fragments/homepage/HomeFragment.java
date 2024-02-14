package my.edu.utar.pwmanager.Fragments.homepage;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import my.edu.utar.pwmanager.R;
import my.edu.utar.pwmanager.Utilities.RecyclerViewAdapter;

public class HomeFragment extends Fragment {

    String PREF_NAME = "appEssentials";
    SharedPreferences sharedPreferences;
    String PREF_KEY_SECURE_CORE_MODE = "SECURE_CORE";

    private HomeViewModel homeViewModel;
    private RecyclerViewAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);

        View root = inflater.inflate(R.layout.fragment_home, container, false);

        final TextView textView = root.findViewById(R.id.text_home);

        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        sharedPreferences = this.getActivity().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        if (sharedPreferences.getBoolean(PREF_KEY_SECURE_CORE_MODE, false)) {
            getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        }

        return root;
    }
}
