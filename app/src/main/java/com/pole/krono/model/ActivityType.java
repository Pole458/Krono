package com.pole.krono.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import static android.arch.persistence.room.ForeignKey.CASCADE;

@Entity(foreignKeys = {
        @ForeignKey(
                onDelete = CASCADE,
                entity = Sport.class,
                parentColumns = "name",
                childColumns = "sport"
        )},
        indices = @Index("sport"))
public class ActivityType {

    @NonNull
    @PrimaryKey
    public String name;

    public String sport;

    ActivityType(@NonNull String name, String sport){
        this.name = name;
        this.sport = sport;
    }

    static ActivityType[] populate() {
        return new ActivityType[] {
                new ActivityType("Freestyle", "Swim"),
                new ActivityType("Butterfly", "Swim"),
                new ActivityType("Backstroke", "Swim"),
                new ActivityType("Breaststroke", "Swim")
        };
    }

    @NonNull
    @Override
    public String toString() {
        return name;
    }
}
