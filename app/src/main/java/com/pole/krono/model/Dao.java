package com.pole.krono.model;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Insert;
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

    @Insert
    void insertProfiles(Profile... profiles);

    @Query("DELETE FROM sport")
    void deleteAll();

    @Insert
    void insertActivities(ActivityType... activityTypes);
}
