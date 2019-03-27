package com.pole.krono.model;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import java.util.List;

public class MyViewModel extends AndroidViewModel {

    private Dao dao;

    private MutableLiveData<Profile> selectedProfile;

    private LiveData<List<Profile>> profiles;

    private LiveData<List<Sport>> sports;

    private LiveData<List<ActivityType>> activityTypes;

    public MyViewModel(Application application) {
        super(application);

        dao = DB.getDatabase(application).dao();

        sports = dao.getSports();
        profiles = dao.getProfiles();

        Log.d("MyViewModel", "onCreate");

    }

    public LiveData<List<Profile>> getProfiles() {
        return profiles;
    }

    public LiveData<List<Sport>> getSports() {
        return sports;
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

    public LiveData<List<ActivityType>> getActivityTypes(String sport) {
        //todo get actiivty
        return activityTypes;
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
                Log.d("MyViewModel", "selected profile assigned: " + profile.getFullName());
            else
                Log.d("MyViewModel", "selected profile assigned: null");

            asyncTaskSelectedProfile.postValue(profile);

            return null;
        }
    }

    public void setSelectedProfile(Profile profile) {
        SharedPreferences settings = getApplication().getSharedPreferences("krono_pref", 0);
        settings.edit().putString("profile_name", profile.getName()).putString("profile_surname", profile.getSurname()).apply();
        selectedProfile.setValue(profile);
    }

    public void addProfile(Profile profile) {
        new insertAsyncTask(dao).execute(profile);
        setSelectedProfile(profile);
    }

    private static class insertAsyncTask extends AsyncTask<Profile, Void, Void> {

        private Dao mAsyncTaskDao;

        insertAsyncTask(Dao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Profile... profiles) {
            mAsyncTaskDao.insertProfiles(profiles);
            return null;
        }
    }
}
