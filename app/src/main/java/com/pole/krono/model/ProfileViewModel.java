package com.pole.krono.model;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.List;

public class ProfileViewModel extends AndroidViewModel {

    private static final String TAG = "ProfileViewModel";

    private Profile profile;

    private Repository repo;

    private LiveData<List<TrackingSession>> trackingSession;

    private LiveData<List<TrackingSession>> todayTrackingSession;

    private MutableLiveData<Long> startTime = new MutableLiveData<>();

    public ProfileViewModel(@NonNull Application application) {
        super(application);
        Log.v(TAG, "onCreate");

        repo = Repository.getRepository(application);

    }

    public void setProfile(Profile profile) {
        this.profile = profile;
        trackingSession = repo.getAllTrackingSession(profile);
        todayTrackingSession = Transformations.switchMap(startTime, startTime -> repo.getTrackingSession(profile, startTime));
    }

    public Profile getProfile() {
        return profile;
    }

    public LiveData<List<TrackingSession>> getAllTrackingSession() {
        return trackingSession;
    }

    public MutableLiveData<Long> getStartTime() {
        return startTime;
    }

    public LiveData<List<TrackingSession>> getTodayTrackingSession() {
        return todayTrackingSession;
    }
}
