package com.pole.krono.model;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

public class SelectedProfileViewModel extends AndroidViewModel {

    private Dao dao;

    private MutableLiveData<Profile> selectedProfile;

    public SelectedProfileViewModel(@NonNull Application application) {
        super(application);

        dao = DB.getDatabase(application).dao();

        Log.d("SelectedProfileVM", "onCreate");

    }

    public MutableLiveData<Profile> getSelectedProfile() {
        if(selectedProfile == null) {
            selectedProfile = new MutableLiveData<>();
            loadSelectedProfile();
        }

        return selectedProfile;
    }

    private void loadSelectedProfile() {

        SharedPreferences settings = getApplication().getSharedPreferences("krono_pref", 0);
        String name = settings.getString("profile_name", null);
        String surname = settings.getString("profile_surname", null);

        new GetProfileAsyncTask(dao, selectedProfile).execute(name, surname);

    }

    private static class GetProfileAsyncTask extends AsyncTask<String, Void, Void> {

        MutableLiveData<Profile> asyncTaskSelectedProfile;
        Dao asyncTaskDao;

        GetProfileAsyncTask(Dao dao, MutableLiveData<Profile> profileLiveData) {
            asyncTaskDao = dao;
            asyncTaskSelectedProfile = profileLiveData;
        }

        @Override
        protected Void doInBackground(String... strings) {

            Profile profile = asyncTaskDao.getProfile(strings[0], strings[1]);

            if(profile != null)
                Log.d("SelectedProfileVM", "selected profile assigned: " + profile.getFullName());
            else
                Log.d("SelectedProfileVM", "selected profile assigned: null");

            asyncTaskSelectedProfile.postValue(profile);

            return null;
        }
    }
}
