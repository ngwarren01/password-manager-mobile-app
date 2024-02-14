package my.edu.utar.pwmanager.database;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import my.edu.utar.pwmanager.classFramework.PwClass;

@Database(entities = {PwClass.class}, version = 5)
public abstract class PwDB extends RoomDatabase {

    private static PwDB instance;

    public abstract PwDAO pwDao();

    public static synchronized PwDB getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    PwDB.class, "PwDatabase")
                    .setJournalMode(JournalMode.TRUNCATE)
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallback)
                    .build();
        }
        return instance;
    }

    private static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new PopulateDbAsyncTask(instance).execute();
        }
    };

    private static class PopulateDbAsyncTask extends AsyncTask<Void, Void, Void> {
        private PwDAO pwDAO;

        private PopulateDbAsyncTask(PwDB db) {
            pwDAO = db.pwDao();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            return null;
        }
    }
}