package my.edu.utar.pwmanager.Fragments.mail;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import my.edu.utar.pwmanager.classFramework.PwClass;
import my.edu.utar.pwmanager.database.PwRepos;

import java.util.List;

//Mail Fragments
public class MailViewModel extends AndroidViewModel {
    private PwRepos repository;
    private LiveData<List<PwClass>> allCreds, mailCreds;

    public MailViewModel(@NonNull Application application) {
        super(application);
        repository = new PwRepos(application);
        allCreds = repository.getAllNotes();
        mailCreds = repository.getAllMails();

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

    public LiveData<List<PwClass>> getAllMails() {
        return mailCreds;
    }
}