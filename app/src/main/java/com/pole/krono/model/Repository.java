package com.pole.krono.model;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;
import com.pole.krono.MillisecondChronometer;

import java.util.Calendar;
import java.util.List;

public class Repository {

    @SuppressWarnings("FieldCanBeLocal")
    private static String TAG = "POLE: Repository";

    private static volatile Repository INSTANCE;

    private final Dao dao;

    @NonNull
    private final MutableLiveData<Profile> selectedProfile = new MutableLiveData<>();

    private Repository(Context context) {
        dao = DB.getDatabase(context).dao();
    }

    static Repository getRepository(final Context context) {
        if (INSTANCE == null) {
            synchronized (Repository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new Repository(context);
                }
            }
        }
        return INSTANCE;
    }

    LiveData<Profile> getSelectedProfile(Context context) {
        if(selectedProfile.getValue() == null) {
            Log.v(TAG, "loading selectedProfile");
            SharedPreferences settings = context.getSharedPreferences("krono_pref", 0);
            String name = settings.getString("profile_name", null);
            String surname = settings.getString("profile_surname", null);

            Log.v(TAG, "selectedProfile in sharedPref: " + name + " " + surname);

            new LoadProfileAsyncTask(dao, selectedProfile).execute(name, surname);
        }
        Log.v(TAG, "returning selected profile");
        return selectedProfile;
    }

    void setSelectedProfile(Context context, Profile profile) {

        SharedPreferences settings = context.getSharedPreferences("krono_pref", 0);
        settings.edit().putString("profile_name", profile.getName()).putString("profile_surname", profile.getSurname()).apply();

        selectedProfile.setValue(profile);
    }


    public LiveData<List<Profile>> getProfiles() {
        return dao.getProfiles();
    }

    LiveData<List<ActivityType>> getActivityTypes(Sport selectedSport) {
        return dao.getActivityTypes(selectedSport.name);
    }

    private static class LoadProfileAsyncTask extends AsyncTask<String, Void, Void> {

        MutableLiveData<Profile> asyncTaskSelectedProfile;
        Dao asyncTaskDao;

        LoadProfileAsyncTask(Dao dao, MutableLiveData<Profile> profileLiveData) {
            asyncTaskDao = dao;
            asyncTaskSelectedProfile = profileLiveData;
        }

        @Override
        protected Void doInBackground(String... strings) {

            Profile profile = asyncTaskDao.getProfile(strings[0], strings[1]);

            if(profile != null)
                Log.v(TAG, "selected profile assigned: " + profile.getFullName());
            else
                Log.v(TAG, "selected profile assigned: null");

            asyncTaskSelectedProfile.postValue(profile);

            return null;
        }
    }

    void insertProfile(Profile profile) {
        new InsertProfileTask(dao).execute(profile);
    }

    private static class InsertProfileTask extends AsyncTask<Profile, Void, Void> {

        private Dao mAsyncTaskDao;

        InsertProfileTask(Dao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Profile... profiles) {
            mAsyncTaskDao.insertProfiles(profiles);
            return null;
        }
    }

    LiveData<List<Sport>> getSports() {
        return dao.getSports();
    }

    void insertTrackingSession(TrackingSession session, MutableLiveData<Long> trackingSessionId) {
        new InsertTrackingSessionTask(dao, trackingSessionId).execute(session);
    }

    private static class InsertTrackingSessionTask extends AsyncTask<TrackingSession, Void, Void> {

        private Dao mAsyncTaskDao;
        private MutableLiveData<Long> asyncTaskId;
//        private long id;

        InsertTrackingSessionTask(Dao dao, MutableLiveData<Long> id) {
            mAsyncTaskDao = dao;
            asyncTaskId = id;
        }

        @Override
        protected Void doInBackground(final TrackingSession... sessions) {
            long id = mAsyncTaskDao.insertTrackingSession(sessions[0]);
            asyncTaskId.postValue(id);
            Log.v(TAG, "got tracking session id: " + id);
            return null;
        }

//        @Override
//        protected void onPostExecute(Void aVoid) {
//            new UpdateLaps(mAsyncTaskDao, asyncLaps).execute(id);
//        }
    }

//    private static class UpdateLaps extends AsyncTask<Long, Void, Void> {
//
//        private Dao mAsyncTaskDao;
//        private MutableLiveData<List<Lap>> asyncLaps;
//
//        UpdateLaps(Dao dao, MutableLiveData<List<Lap>> laps) {
//            mAsyncTaskDao = dao;
//            asyncLaps = laps;
//        }
//
//        @Override
//        protected Void doInBackground(Long... sessionId) {
//            asyncLaps.postValue(mAsyncTaskDao.getLaps(sessionId[0]));
//            return null;
//        }
//    }

    LiveData<List<Lap>> getLaps(Long trackingSessionId) {
//        if(laps != null) {
//            laps = new MutableLiveData<>();
//            new UpdateLaps(dao, laps).execute(trackingSessionId.getValue());
//        }
        return dao.getLaps(trackingSessionId);
    }

    void insertLap(long lapTime, int lapCounter, MutableLiveData<Long> trackingSessionId) {
        if(trackingSessionId.getValue() != null) {
            new InsertLapTask(dao).execute(new Lap(trackingSessionId.getValue(), lapCounter, lapTime));
//            new UpdateLaps(dao, laps).execute(trackingSessionId.getValue());
        }
    }

    private static class InsertLapTask extends AsyncTask<Lap, Void, Void> {

        private Dao mAsyncTaskDao;

        InsertLapTask(Dao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Lap... laps) {
            mAsyncTaskDao.insertLaps(laps);
            Log.v(TAG, "added lap: " + MillisecondChronometer.getTimeString(laps[0].time) + " to track session " + laps[0].trackingSessionId);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

    void stopTracking(long trackingSessionId) {
        new StopTrackingSessionTask(dao).execute(trackingSessionId);
    }

    private static class StopTrackingSessionTask extends AsyncTask<Long, Void, Void> {

        private Dao mAsyncTaskDao;

        StopTrackingSessionTask(Dao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Long... ids) {
            mAsyncTaskDao.stopTrackingSession(ids[0], Calendar.getInstance().getTimeInMillis());
            Log.v(TAG, "stopping tracking session id: " + ids[0]);
            return null;
        }
    }

    LiveData<List<TrackingSession>> getAllTrackingSession(Profile profile) {
        return dao.getTrackingSession(profile.getName(), profile.getSurname());
    }

    LiveData<List<TrackingSession>> getTrackingSession(Profile profile, long startTime) {
        return dao.getTrackingSession(profile.getName(), profile.getSurname(), startTime, startTime + 24 * 60 * 60 * 1000);
    }
}
