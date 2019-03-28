package com.pole.krono.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;

@Entity(primaryKeys = {"trackingSession", "lapNumber"},
        foreignKeys = {
                @ForeignKey(
                        entity = TrackingSession.class,
                        parentColumns = "id",
                        childColumns = "trackingSession"
                )},
        indices = @Index("trackingSession"))
public class Lap {

    public int trackingSession;

    public int lapNumber;

    public long time;
}
