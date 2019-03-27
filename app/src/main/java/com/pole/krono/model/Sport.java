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

    public static Sport[] populate() {
        return new Sport[]{
                new Sport("Run"),
                new Sport("Cycling"),
                new Sport("Swim"),
        };
        //                            db.execSQL("insert into sport values('Run')");
//                            db.execSQL("insert into sport values('Cycling')");
//                            db.execSQL("insert into sport values('Swim - Freestyle')");
//                            db.execSQL("insert into sport values('Swim - Butterfly')");
//                            db.execSQL("insert into sport values('Swim - Breaststroke')");
//                            db.execSQL("insert into sport values('Swim - Backstroke')");
    }

    @Override
    public String toString() {
        return name;
    }
}
