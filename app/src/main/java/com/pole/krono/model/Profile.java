package com.pole.krono.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.support.annotation.NonNull;

@Entity(primaryKeys = {"name","surname"},
        foreignKeys = {
                @ForeignKey(
                        entity = Sport.class,
                        parentColumns = "name",
                        childColumns = "sport"
                )},
        indices = @Index("sport"))
public class Profile {

    @NonNull
    private String name;

    @NonNull
    private String surname;

    private String sport;


    public Profile(@NonNull String name, @NonNull String surname, String sport) {
        this.name = name;
        this.surname = surname;
        this.sport = sport;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public String getFullName(){
        return String.format("%s %s", name, surname);
    }

    public String getSport() {
        return sport;
    }

    @NonNull
    public String getSurname() {
        return surname;
    }
}
