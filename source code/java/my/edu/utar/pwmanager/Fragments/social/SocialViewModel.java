package my.edu.utar.pwmanager.Fragments.social;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import my.edu.utar.pwmanager.classFramework.PwClass;
import my.edu.utar.pwmanager.database.PwRepos;

import java.util.List;

//Social Fragments
public class SocialViewModel extends AndroidViewModel {
    private PwRepos repository;
    private LiveData<List<PwClass>> allCreds, mailCreds;

    public SocialViewModel(@NonNull Application application) {
        super(application);
        repository = new PwRepos(application);
        mailCreds = repository.getAllSocial();

    }

    public void insert(PwClass pwClass) {
        repository.insert(pwClass);
    }

    public void update(PwClass pwClass) {
        repository.update(pwClass);
    }

    public void delete(PwClass pwClass) {
        repository.delete(pwClass);
    }

    public void deleteAllNotes() {
        repository.deleteAllNotes();
    }

    public LiveData<List<PwClass>> getAllCreds() {
        return allCreds;
    }

    public LiveData<List<PwClass>> getAllSocial() {
        return mailCreds;
    }
}