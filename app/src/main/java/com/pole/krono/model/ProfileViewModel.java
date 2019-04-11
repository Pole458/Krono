package com.pole.krono.model;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.List;

public class ProfileViewModel extends AndroidViewModel {

    private Profile profile;

    private Dao dao;

    private MutableLiveData<List<TrackingSession>> trackingSession;

    public ProfileViewModel(@NonNull Application application) {
        super(application);

        dao = DB.getDatabase(application).dao();
        Log.v("Pole", "ProfileViewModel: onCreate");

    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public Profile getProfile() {
        return profile;
    }

    public LiveData<List<TrackingSession>> getTrackingSession() {
        if(trackingSession == null) {
            trackingSession = new MutableLiveData<>();
            new TrackingSessionAsyncTask(dao, trackingSession).execute(profile);
        }
        return trackingSession;
    }

    public void updateTrackingSession(long time) {
        if(trackingSession == null) {
            trackingSession = new MutableLiveData<>();
        }
        new TrackingSessionAsyncTask(dao, trackingSession, time).execute(profile);
    }

    private static class TrackingSessionAsyncTask extends AsyncTask<Profile, Void, Void> {

        private Dao mDao;
        private MutableLiveData<List<TrackingSession>> asyncSession;
        private long mTime;

        TrackingSessionAsyncTask(Dao dao, MutableLiveData<List<TrackingSession>> session) {
            mDao = dao;
            asyncSession = session;
            mTime = -1;
        }

        TrackingSessionAsyncTask(Dao dao, MutableLiveData<List<TrackingSession>> session, long time) {
            mDao = dao;
            asyncSession = session;
            mTime = time;
        }

        @Override
        protected Void doInBackground(Profile... profiles) {

            if(mTime == -1)
                asyncSession.postValue(mDao.getTrackingSession(profiles[0].getName(), profiles[0].getSurname()));
            else {
                long startTime = mTime;
                long endTime = mTime + 24 * 60 * 60 * 1000;
                asyncSession.postValue(mDao.getTrackingSession(profiles[0].getName(), profiles[0].getSurname(), startTime, endTime));
            }
            return null;
        }
    }
}
