package com.pole.krono.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;

import static android.arch.persistence.room.ForeignKey.CASCADE;

@Entity(primaryKeys = {"trackingSessionId", "lapNumber"},
        foreignKeys = {
                @ForeignKey(
                        onDelete = CASCADE,
                        entity = TrackingSession.class,
                        parentColumns = "id",
                        childColumns = "trackingSessionId"
                )},
        indices = @Index("trackingSessionId"))

public class Lap {

    public long trackingSessionId;

    public int lapNumber;

    public long time;

    Lap(long trackingSessionId, int lapNumber, long time) {
        this.trackingSessionId = trackingSessionId;
        this.lapNumber = lapNumber;
        this.time = time;
    }

    public String[] toCSV() {
        return new String[]{String.valueOf(trackingSessionId), String.valueOf(lapNumber), String.valueOf(time)};
    }
}
