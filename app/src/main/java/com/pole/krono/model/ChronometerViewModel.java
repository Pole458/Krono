package com.pole.krono.model;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.support.annotation.NonNull;

import java.util.List;

public class ChronometerViewModel extends AndroidViewModel {

    private Repository repo;

    private LiveData<List<Sport>> sports;

    private MutableLiveData<Sport> selectedSport = new MutableLiveData<>();
    private LiveData<List<ActivityType>> activityTypes;

    private MutableLiveData<Long> trackingSessionId = new MutableLiveData<>();
    private LiveData<List<Lap>> laps;

    public ChronometerViewModel(@NonNull Application application) {
        super(application);

        repo = Repository.getRepository(application);
        sports = repo.getSports();

        activityTypes = Transformations.switchMap(selectedSport, selectedSport -> repo.getActivityTypes(selectedSport));

        laps = Transformations.switchMap(trackingSessionId, id -> repo.getLaps(id));

    }

    public MutableLiveData<Sport> getSelectedSport() {
        return selectedSport;
    }

    public LiveData<List<ActivityType>> getActivityTypes() {
        return activityTypes;
    }

    public LiveData<List<Sport>> getSports() {
        return sports;
    }

    public void insertLap(long lapTime, int lapCounter) {
        if(trackingSessionId.getValue() != null)
            Repository.insertLap(repo, lapTime, lapCounter, trackingSessionId.getValue());
    }

    public void insertTrackingSession(TrackingSession session) {
        Repository.insertTrackingSession(repo, session, trackingSessionId);
    }

    public void stopTracking() {
        if(trackingSessionId.getValue() != null)
            Repository.stopTracking(repo, trackingSessionId.getValue());
    }

    public LiveData<List<Lap>> getLaps() {
        return laps;
    }
}
