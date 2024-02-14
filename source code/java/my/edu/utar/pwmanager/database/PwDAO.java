//Declaring abstract for functions within Database

package my.edu.utar.pwmanager.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import my.edu.utar.pwmanager.classFramework.PwClass;

import java.util.List;

@Dao
public interface PwDAO {

    @Insert
    void insert(PwClass pwClass);

    @Update
    void update(PwClass pwClass);

    @Delete
    void delete(PwClass pwClass);

    @Query("DELETE FROM entry_table")
    void deleteAllNotes();


    @Query("SELECT * FROM entry_table")
    LiveData<List<PwClass>> getAllCreds();

    @Query("SELECT * FROM entry_table WHERE provider = 'mail'")
    LiveData<List<PwClass>> getAllMails();

    @Query("SELECT * FROM entry_table WHERE provider = 'social'")
    LiveData<List<PwClass>> getAllSocial();
}