package com.pole.krono.model;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import android.util.Log;

public class SelectedProfileViewModel extends AndroidViewModel {

    @SuppressWarnings("FieldCanBeLocal")
    private static String TAG = "POLE: SelectedProfileVM";
    @SuppressWarnings("FieldCanBeLocal")
    private Repository repo;

    private LiveData<Profile> selectedProfile;

    public SelectedProfileViewModel(@NonNull Application application) {
        super(application);

        Log.d(TAG, "onCreate");

        repo = Repository.getRepository(application);

        selectedProfile = Repository.getSelectedProfile(application, repo);

    }

    public LiveData<Profile> getSelectedProfile() {
        return selectedProfile;
    }

}
