package com.pole.krono.model;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteConstraintException;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import com.pole.krono.MyChronometer;

import java.util.Calendar;
import java.util.List;

class Repository {

    @SuppressWarnings("FieldCanBeLocal")
    private static String TAG = "POLE: Repository";

    private static volatile Repository INSTANCE;

    private final Dao dao;

    private MutableLiveData<Profile> selectedProfile;

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

    static void deleteSport(Repository repo, Sport sport) {
        new AsyncTask<Sport, Void, Void>(){

            @Override
            protected Void doInBackground(Sport... sports) {
                repo.dao.deleteSport(sport);
                return null;
            }
        }.execute(sport);
    }

    static void insertActivityType(Repository repo, ActivityType activityType) {
        new AsyncTask<ActivityType, Void, Void>() {

            @Override
            protected Void doInBackground(ActivityType... activityTypes) {
                repo.dao.insertActivities(activityTypes);
                return null;
            }
        }.execute(activityType);
    }

    static void insertSport(Repository repo, Sport sport) {
        new AsyncTask<Sport, Void, Void>() {

            @Override
            protected Void doInBackground(Sport... sports) {
                repo.dao.insertSports(sports);
                return null;
            }
        }.execute(sport);

    }

    static LiveData<Profile> getSelectedProfile(Context context, Repository repo) {
        if(repo.selectedProfile == null) {
            repo.selectedProfile = new MutableLiveData<>();
            SharedPreferences settings = context.getSharedPreferences("krono_pref", 0);
            String name = settings.getString("profile_name", null);
            String surname = settings.getString("profile_surname", null);

            new AsyncTask<String, Void, Void>() {
                @Override
                protected Void doInBackground(String... strings) {
                    Profile profile = repo.dao.getProfile(strings[0], strings[1]);

                    if(profile != null)
                        Log.v(TAG, "selected profile assigned: " + profile.getFullName());
                    else
                        Log.v(TAG, "selected profile assigned: null");

                    repo.selectedProfile.postValue(profile);
                    return null;
                }
            }.execute(name, surname);

            Log.v(TAG, "selectedProfile in sharedPref: " + name + " " + surname);
        }
        return repo.selectedProfile;
    }

    void setSelectedProfile(Context context, Profile profile) {

        SharedPreferences settings = context.getSharedPreferences("krono_pref", 0);
        settings.edit().putString("profile_name", profile.getName()).putString("profile_surname", profile.getSurname()).apply();
        Log.v(TAG, "SelectedProfile set in sharedPref: " + profile.getFullName());

        selectedProfile.setValue(profile);
    }

    LiveData<List<Profile>> getProfiles() {
        return dao.getProfiles();
    }

    LiveData<List<ActivityType>> getActivityTypes(Sport selectedSport) {
        return dao.getActivityTypes(selectedSport.name);
    }

    LiveData<TrackingSession> getTrackingSession(Long id) {
        return dao.getTrackingSession(id);
    }

    static boolean deleteProfile(Repository repo, Profile profile) {

        Log.v(TAG, "Deleting profile " + profile.getFullName());

        Log.v(TAG, "Selected profile is null: " + (repo.selectedProfile.getValue() == null));

        if(repo.selectedProfile.getValue() != null && profile.getFullName().equals(repo.selectedProfile.getValue().getFullName())) {
            Log.v(TAG, "Returning false");
            return false;
        }

        Log.v(TAG, "Starting async task");

        new AsyncTask<Profile, Void, Void>() {

            @Override
            protected Void doInBackground(Profile... profiles) {
                Log.v(TAG, "async task do in background");
                repo.dao.deleteProfiles(profiles);
                return null;
            }
        }.execute(profile);

        return true;
    }

    static void deleteActivityType(Repository repo, ActivityType activityType) {

        new AsyncTask<ActivityType, Void, Void>() {
            @Override
            protected Void doInBackground(ActivityType... activityTypes) {
                repo.dao.deleteActivityTypes(activityTypes);
                return null;
            }
        }.execute(activityType);
    }

    static void insertProfile(Context context, Repository repo, Profile profile) {
        new AsyncTask<Profile, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Profile... profiles) {
                try {
                    repo.dao.insertProfiles(profiles);
                } catch (SQLiteConstraintException ignored) {
                   return false;
                }
                return true;
            }
            @Override
            protected void onPostExecute(Boolean success) {
                if(!success)
                    Toast.makeText(context, "There is already a Profile with this name", Toast.LENGTH_LONG).show();
            }
        }.execute(profile);
    }

    LiveData<List<Sport>> getSports() {
        return dao.getSports();
    }

    static void insertTrackingSession(Repository repo, TrackingSession session, MutableLiveData<Long> trackingSessionId) {
        new AsyncTask<TrackingSession, Void, Void>() {
            @Override
            protected Void doInBackground(TrackingSession... trackingSessions) {
                long id = repo.dao.insertTrackingSession(trackingSessions[0]);
                trackingSessionId.postValue(id);
                Log.v(TAG, "got tracking session id: " + id);
                return null;
            }
        }.execute(session);
    }

    LiveData<List<Lap>> getLaps(Long trackingSessionId) {
        return dao.getLaps(trackingSessionId);
    }

    static void insertLap(Repository repo, long lapTime, int lapCounter, Long trackingSessionId) {
        new AsyncTask<Lap, Void, Void>() {
            @Override
            protected Void doInBackground(Lap... laps) {
                repo.dao.insertLaps(laps);
                Log.v(TAG, "added lap: " + MyChronometer.getTimeString(laps[0].time) + " to track session " + laps[0].trackingSessionId);
                return null;
            }
        }.execute(new Lap(trackingSessionId, lapCounter, lapTime));
    }

    static void stopTracking(Repository repo, long trackingSessionId) {
        new AsyncTask<Long, Void, Void>() {
            @Override
            protected Void doInBackground(Long... ids) {
                repo.dao.stopTrackingSession(ids[0], Calendar.getInstance().getTimeInMillis());
                Log.v(TAG, "stopping tracking session id: " + ids[0]);
                return null;
            }
        }.execute(trackingSessionId);
    }

    LiveData<List<TrackingSession>> getAllTrackingSession(Profile profile) {
        return dao.getTrackingSession(profile.getName(), profile.getSurname());
    }

    LiveData<List<TrackingSession>> getTrackingSession(Profile profile, long startTime) {
        return dao.getTrackingSession(profile.getName(), profile.getSurname(), startTime, startTime + 24 * 60 * 60 * 1000);
    }

    static void deleteTrackingSession(Repository repo, TrackingSession session) {
        new AsyncTask<TrackingSession, Void, Void>() {
            @Override
            protected Void doInBackground(TrackingSession... trackingSessions) {
                repo.dao.deleteTrackingSession(trackingSessions);
                return null;
            }
        }.execute(session);
    }
}
