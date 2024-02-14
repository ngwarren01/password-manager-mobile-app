package my.edu.utar.pwmanager.classFramework;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "entry_table")
public class PwClass {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String provider;
    private String providerName;
    private String email;
    private String cat;

    public PwClass(String provider, String providerName, String email, String cat) {
        this.provider = provider;
        this.providerName = providerName;
        this.email = email;
        this.cat = cat;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProvider() {
        return provider;
    }

    public String getProviderName() {
        return providerName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCat() {
        return cat;
    }
}