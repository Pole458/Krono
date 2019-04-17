package com.pole.krono.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity
public class Sport {

    @PrimaryKey
    @NonNull
    public String name;

    public Sport(@NonNull String name) {
        this.name = name;
    }

    static Sport[] populate() {
        return new Sport[]{
                new Sport("Run"),
                new Sport("Cycling"),
                new Sport("Swim"),
        };
    }

    @NonNull
    @Override
    public String toString() {
        return name;
    }
}
