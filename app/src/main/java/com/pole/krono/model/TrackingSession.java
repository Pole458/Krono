package com.pole.krono.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import static android.arch.persistence.room.ForeignKey.CASCADE;
import static android.arch.persistence.room.ForeignKey.SET_NULL;

@Entity(foreignKeys = {
        @ForeignKey(
                onDelete = CASCADE,
                entity = Profile.class,
                parentColumns = {"name", "surname"},
                childColumns = {"profileName", "profileSurname"}
        ),
        @ForeignKey(
                onDelete = CASCADE,
                entity = Sport.class,
                parentColumns = "name",
                childColumns = "sport"
        ),
        @ForeignKey(
                onDelete = SET_NULL,
                entity = ActivityType.class,
                parentColumns = "name",
                childColumns = "activityType"
        ),},
        indices = {@Index({"profileName", "profileSurname"}),
                @Index("sport"),
                @Index("activityType")})
public class TrackingSession {

    @PrimaryKey(autoGenerate = true)
    public long id;

    public String profileName;

    public String profileSurname;

    public String sport;

    public String activityType;

    public long startTime;

    public long endTime;

    public float distance;

    public String[] toCSV() {
        return new String[]{String.valueOf(id), profileName, profileSurname, sport, activityType,
                String.valueOf(startTime), String.valueOf(endTime), String.valueOf(distance)};
    }

}
