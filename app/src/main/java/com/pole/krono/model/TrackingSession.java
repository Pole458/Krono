package com.pole.krono.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import java.util.Date;

@Entity(foreignKeys = {
        @ForeignKey(
                entity = Profile.class,
                parentColumns = {"name", "surname"},
                childColumns = {"profileName", "profileSurname"}
        ),
        @ForeignKey(
                entity = Sport.class,
                parentColumns = "name",
                childColumns = "sport"
        ),
        @ForeignKey(
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

    public Date startTime;

    public Date endTime;

}
