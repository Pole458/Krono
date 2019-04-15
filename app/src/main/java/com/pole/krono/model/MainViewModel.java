package com.pole.krono.model;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.util.Log;

import java.util.List;

public class MainViewModel extends AndroidViewModel {

    @SuppressWarnings("FieldCanBeLocal")
    private static String TAG = "POLE: MainVM";

    private Repository repo;

    private LiveData<Profile> selectedProfile;

    private LiveData<List<Profile>> profiles;
    private LiveData<List<Sport>> sports;

    public MainViewModel(Application application) {
        super(application);

        repo = Repository.getRepository(application);

        selectedProfile = repo.getSelectedProfile(application);

        profiles = repo.getProfiles();
        sports = repo.getSports();

        Log.v(TAG, "onCreate");
    }

    public void setSelectedProfile(Context context, Profile profile) {
        repo.setSelectedProfile(context, profile);
    }

    public LiveData<Profile> getSelectedProfile() {
        return selectedProfile;
    }

    public LiveData<List<Profile>> getProfiles() {
        return profiles;
    }

    public LiveData<List<Sport>> getSports() {
        return sports;
    }

    public void insertProfile(Context context, Profile profile) {
        repo.insertProfile(profile);
        setSelectedProfile(context, profile);
    }

}
