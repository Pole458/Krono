package com.pole.krono.model;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

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

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertProfiles(Profile... profiles);

    @Insert
    void insertActivities(ActivityType... activityTypes);

    @Query("SELECT * FROM activitytype WHERE sport == :sport")
    LiveData<List<ActivityType>> getActivityTypes(String sport);

    @Insert
    long insertTrackingSession(TrackingSession trackingSession);

    @Query("UPDATE trackingsession SET endTime = :endTime WHERE id == :id")
    void stopTrackingSession(long id, long endTime);

    @Query("SELECT * FROM trackingsession WHERE profileName == :name AND profileSurname == :surname ORDER BY startTime DESC")
    LiveData<List<TrackingSession>> getTrackingSession(String name, String surname);

    @Insert
    void insertLaps(Lap... laps);

    @Query("SELECT * FROM lap WHERE trackingSessionId == :sessionId ORDER BY lapNumber DESC")
    LiveData<List<Lap>>getLaps(Long sessionId);

    @Query("SELECT * FROM trackingsession WHERE profileName == :name AND profileSurname == :surname AND :startTime <= startTime AND startTime < :endTime ORDER BY startTime ASC")
    LiveData<List<TrackingSession>> getTrackingSession(String name, String surname, long startTime, long endTime);

    @Query("SELECT * FROM trackingsession WHERE id == :id")
    LiveData<TrackingSession> getTrackingSession(Long id);

    @Delete
    void deleteProfiles(Profile... profiles);

    @Delete
    void deleteActivityTypes(ActivityType... activityTypes);

    @Delete
    void deleteSport(Sport sport);

    @Delete
    void deleteTrackingSession(TrackingSession... trackingSessions);
}
