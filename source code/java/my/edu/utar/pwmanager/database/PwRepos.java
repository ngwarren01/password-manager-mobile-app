package my.edu.utar.pwmanager.database;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import my.edu.utar.pwmanager.classFramework.PwClass;

import java.util.List;

public class PwRepos {
    private PwDAO pwDAO;
    private LiveData<List<PwClass>> allEntries, mailEntries, socialEntries;

    //Repository class for getting the entries
    public PwRepos(Application application) {
        PwDB database = PwDB.getInstance(application);
        pwDAO = database.pwDao();
        allEntries = pwDAO.getAllCreds();
        mailEntries = pwDAO.getAllMails();
        socialEntries = pwDAO.getAllSocial();
    }

    //insert function
    public void insert(PwClass pwClass) {
        new InsertEntry(pwDAO).execute(pwClass);
    }

    //update function
    public void update(PwClass pwClass) {
        new UpdateEntry(pwDAO).execute(pwClass);
    }

    //delete function
    public void delete(PwClass pwClass) {
        new DeleteEntry(pwDAO).execute(pwClass);
    }

    //delete all function
    public void deleteAllNotes() {
        new DeleteAllEntry(pwDAO).execute();
    }

    public LiveData<List<PwClass>> getAllNotes() {
        return allEntries;
    }

    public LiveData<List<PwClass>> getAllMails() {
        return mailEntries;
    }

    public LiveData<List<PwClass>> getAllSocial() {
        return socialEntries;
    }

    //Insert set of entry into database
    private static class InsertEntry extends AsyncTask<PwClass, Void, Void> {
        private PwDAO pwDAO;

        private InsertEntry(PwDAO pwDAO) {
            this.pwDAO = pwDAO;
        }

        @Override
        protected Void doInBackground(PwClass... pwClass) {
            pwDAO.insert(pwClass[0]);
            return null;
        }
    }

    //Update the data of set of entry in the database
    private static class UpdateEntry extends AsyncTask<PwClass, Void, Void> {
        private PwDAO pwDAO;

        private UpdateEntry(PwDAO pwDAO) {
            this.pwDAO = pwDAO;
        }

        @Override
        protected Void doInBackground(PwClass... pwClass) {
            pwDAO.update(pwClass[0]);
            return null;
        }
    }

    //Delete the data of selected set of entry in the database
    private static class DeleteEntry extends AsyncTask<PwClass, Void, Void> {
        private PwDAO pwDAO;

        private DeleteEntry(PwDAO pwCredDao) {
            this.pwDAO = pwCredDao;
        }

        @Override
        protected Void doInBackground(PwClass... pwClass) {
            pwDAO.delete(pwClass[0]);
            return null;
        }
    }

    //Delete all data in the database
    private static class DeleteAllEntry extends AsyncTask<Void, Void, Void> {
        private PwDAO pwDAO;

        private DeleteAllEntry(PwDAO pwDAO) {
            this.pwDAO = pwDAO;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            pwDAO.deleteAllNotes();
            return null;
        }
    }
}
