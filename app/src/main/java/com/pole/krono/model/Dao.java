package com.pole.krono.model;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.Date;
import java.util.List;

@android.arch.persistence.room.Dao
public interface Dao {

    @Query("SELECT * FROM sport")
    LiveData<List<Sport>> getSports();

    @Insert
    void insertSports(Sport... sports);

    @Query("SELECT * FROM profile")
    LiveData<List<Profile>> getProfiles();

    @Query("SELECT * FROM profile WHERE name == :name AND surname == :surname")
    Profile getProfile(String name, String surname);

    @Insert
    void insertProfiles(Profile... profiles);

    @Insert
    void insertActivities(ActivityType... activityTypes);

    @Query("SELECT * FROM activitytype WHERE sport == :sport")
    List<ActivityType> getActivityTypes(String sport);

    @Insert
    long insertTrackingSession(TrackingSession trackingSession);

    @Query("UPDATE trackingsession SET endTime = :endTime WHERE id == :id")
    void stopTrackingSession(long id, Date endTime);

    @Query("SELECT * FROM trackingsession WHERE profileName == :name AND profileSurname == :surname ORDER BY startTime DESC")
    List<TrackingSession> getTrackingSession(String name, String surname);

    @Insert
    void insertLap(Lap... laps);

    @Query("SELECT * FROM lap WHERE trackingSessionId == :sessionId ORDER BY lapNumber DESC")
    List<Lap> getLaps(Long sessionId);
}
