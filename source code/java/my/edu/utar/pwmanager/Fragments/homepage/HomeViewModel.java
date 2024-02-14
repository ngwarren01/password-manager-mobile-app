package my.edu.utar.pwmanager.Fragments.homepage;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HomeViewModel extends ViewModel {

    private MutableLiveData<String> Text;

    public HomeViewModel() {
        Text = new MutableLiveData<>();
        Text.setValue("Home fragment");
    }

    public LiveData<String> getText() {
        return Text;
    }

}