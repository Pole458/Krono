package com.pole.krono.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(foreignKeys = {
        @ForeignKey(
                entity = Sport.class,
                parentColumns = "name",
                childColumns = "sport"
        )})
public class ActivityType {

    @NonNull
    @PrimaryKey
    public String name;

    public String sport;

    public ActivityType(@NonNull String name, String sport){
        this.name = name;
        this.sport = sport;
    }

    public static ActivityType[] populate() {
        return new ActivityType[] {
                new ActivityType("Freestyle", "Swim"),
                new ActivityType("Butterfly", "Swim"),
                new ActivityType("Backstroke", "Swim"),
                new ActivityType("Breaststroke", "Swim")
        };
    }

    @Override
    public String toString() {
        return name;
    }
}
