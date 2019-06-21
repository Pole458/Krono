package com.pole.krono.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.support.annotation.NonNull;
import com.pole.krono.model.Lap;
import com.pole.krono.model.Repository;
import com.pole.krono.model.TrackingSession;

import java.util.List;

public class TrackingSessionViewModel extends AndroidViewModel {

    private Repository repo;

    private LiveData<TrackingSession> session;

    private MutableLiveData<Long> id = new MutableLiveData<>();

    private LiveData<List<Lap>> laps;

    public TrackingSessionViewModel(@NonNull Application application) {
        super(application);

        repo = Repository.getRepository(application);

        session = Transformations.switchMap(id, id -> repo.getTrackingSession(id));

        laps = Transformations.switchMap(id, id -> repo.getLaps(id));

    }

    public LiveData<TrackingSession> getSession() {
        return session;
    }

    public LiveData<List<Lap>> getLaps() {
        return laps;
    }

    public MutableLiveData<Long> getId() {
        return id;
    }

    public void deleteTrackingSession() {
        Repository.deleteTrackingSession(repo, session.getValue());
    }
}
