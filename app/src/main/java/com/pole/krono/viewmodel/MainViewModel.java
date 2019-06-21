package com.pole.krono.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.util.Log;
import com.pole.krono.model.Profile;
import com.pole.krono.model.Repository;
import com.pole.krono.model.Sport;

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

        Log.v(TAG, "onCreate");

        repo = Repository.getRepository(application);

        selectedProfile = Repository.getSelectedProfile(application, repo);

        profiles = repo.getProfiles();
        sports = repo.getSports();

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
        Repository.insertProfile(context, repo, profile);
        setSelectedProfile(context, profile);
    }

    public void insertSport(String sportName) {
        Repository.insertSport(repo, new Sport(sportName));
    }
}
