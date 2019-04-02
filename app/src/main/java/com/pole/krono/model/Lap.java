package com.pole.krono.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;

@Entity(primaryKeys = {"trackingSessionId", "lapNumber"},
        foreignKeys = {
                @ForeignKey(
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
}
