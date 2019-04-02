package com.pole.krono.model;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import com.pole.krono.MillisecondChronometer;

import java.util.Calendar;
import java.util.List;

public class MyViewModel extends AndroidViewModel {

    private Dao dao;

    private MutableLiveData<Profile> selectedProfile;

    private LiveData<List<Profile>> profiles;

    private LiveData<List<Sport>> sports;

    private MutableLiveData<Sport> selectedSport;
    private MutableLiveData<List<ActivityType>> activityTypes;

    private MutableLiveData<Long> trackingSessionId;
    private MutableLiveData<List<Lap>> laps;

    public MyViewModel(Application application) {
        super(application);

        dao = DB.getDatabase(application).dao();

        sports = dao.getSports();
        profiles = dao.getProfiles();

        selectedSport = new MutableLiveData<>();

        trackingSessionId = new MutableLiveData<>();

        laps = new MutableLiveData<>();

        Log.v("Pole", "MyViewModel.onCreate");
    }

    public LiveData<List<Profile>> getProfiles() {
        return profiles;
    }

    public LiveData<List<Sport>> getSports() {
        return sports;
    }

    public void setSelectedProfile(Profile profile) {
        SharedPreferences settings = getApplication().getSharedPreferences("krono_pref", 0);
        settings.edit().putString("profile_name", profile.getName()).putString("profile_surname", profile.getSurname()).apply();

        if(selectedProfile == null)
            selectedProfile = new MutableLiveData<>();

        selectedProfile.setValue(profile);
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

    public void addLap(long lapTime, int lapCounter) {
        if(trackingSessionId.getValue() != null) {
            new addLap(dao).execute(new Lap(trackingSessionId.getValue(), lapCounter, lapTime));
            new updateLaps(dao, laps).execute(trackingSessionId.getValue());
        }
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
                Log.v("Pole", "MyViewModel: selected profile assigned: " + profile.getFullName());
            else
                Log.v("Pole", "MyViewModel: selected profile assigned: null");

            asyncTaskSelectedProfile.postValue(profile);

            return null;
        }
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

    public LiveData<List<ActivityType>> getActivityTypes() {
        if(activityTypes == null) {
            activityTypes = new MutableLiveData<>();
            if(selectedSport.getValue() != null) {
                updateActivityTypes();
            }
        }

        return activityTypes;
    }

    private static class GetActivityTypes extends AsyncTask<Sport, Void, Void> {

        private Dao mAsyncTaskDao;
        private MutableLiveData<List<ActivityType>> asyncTaskActivityTypes;

        GetActivityTypes(Dao dao, MutableLiveData<List<ActivityType>> activityTypes) {
            mAsyncTaskDao = dao;
            asyncTaskActivityTypes = activityTypes;
        }

        @Override
        protected Void doInBackground(final Sport... sport) {
            asyncTaskActivityTypes.postValue(mAsyncTaskDao.getActivityTypes(sport[0].name));
            return null;
        }
    }

    public void updateActivityTypes() {
        new GetActivityTypes(dao, activityTypes).execute(selectedSport.getValue());
    }

    public MutableLiveData<Sport> getSelectedSport() {
        return selectedSport;
    }

    public void insertTrackingSession(TrackingSession session) {
        new insertTrackingSession(dao, trackingSessionId).execute(session);
    }

    private static class insertTrackingSession extends AsyncTask<TrackingSession, Void, Void> {

        private Dao mAsyncTaskDao;
        private MutableLiveData<Long> asyncTaskId;

        insertTrackingSession(Dao dao, MutableLiveData<Long> id) {
            mAsyncTaskDao = dao;
            asyncTaskId = id;
        }

        @Override
        protected Void doInBackground(final TrackingSession... sessions) {
            long id = mAsyncTaskDao.insertTrackingSession(sessions[0]);
            asyncTaskId.postValue(id);
            Log.v("Pole", "MyViewModel: got tracking session id: " + id);
            return null;
        }
    }

    public void stopTracking() {
        if(trackingSessionId.getValue() != null) {
            new stopTrackingSession(dao).execute(trackingSessionId.getValue());
        } else {
            Log.v("Pole", "MyViewModel: could not stop sessionSession, id == null");
        }
    }

    private static class stopTrackingSession extends AsyncTask<Long, Void, Void> {

        private Dao mAsyncTaskDao;

        stopTrackingSession(Dao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Long... ids) {
            mAsyncTaskDao.stopTrackingSession(ids[0], Calendar.getInstance().getTime());
            Log.v("Pole", "MyViewModel: stopping tracking session id: " + ids[0]);
            return null;
        }
    }

    private static class addLap extends AsyncTask<Lap, Void, Void> {

        private Dao mAsyncTaskDao;

        addLap(Dao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Lap... laps) {
            mAsyncTaskDao.insertLap(laps);
            Log.v("Pole", "MyViewModel: added lap: " + MillisecondChronometer.getTimeString(laps[0].time) + " to track session " + laps[0].trackingSessionId);
            return null;
        }
    }

    public MutableLiveData<List<Lap>> getLaps() {
        if(laps != null) {
            laps = new MutableLiveData<>();
            new updateLaps(dao, laps).execute(trackingSessionId.getValue());
        }
        return laps;
    }

    private static class updateLaps extends AsyncTask<Long, Void, Void> {

        private Dao mAsyncTaskDao;
        private MutableLiveData<List<Lap>> asyncLaps;

        updateLaps(Dao dao, MutableLiveData<List<Lap>> laps) {
            mAsyncTaskDao = dao;
            asyncLaps = laps;
        }

        @Override
        protected Void doInBackground(Long... sessionId) {
            asyncLaps.postValue(mAsyncTaskDao.getLaps(sessionId[0]));
            return null;
        }
    }

}
