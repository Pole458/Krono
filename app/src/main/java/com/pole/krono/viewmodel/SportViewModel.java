package com.pole.krono.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.support.annotation.NonNull;
import com.pole.krono.model.ActivityType;
import com.pole.krono.model.Repository;
import com.pole.krono.model.Sport;

import java.util.List;

public class SportViewModel extends AndroidViewModel {

    private static Repository repo;

    private MutableLiveData<Sport> sport = new MutableLiveData<>();

    private LiveData<List<ActivityType>> activityTypes;

    public SportViewModel(@NonNull Application application) {
        super(application);
        repo = Repository.getRepository(application);

        activityTypes = Transformations.switchMap(sport, sport -> repo.getActivityTypes(sport));
    }

    public MutableLiveData<Sport> getSport() {
        return sport;
    }

    public LiveData<List<ActivityType>> getActivityTypes() {
        return activityTypes;
    }

    public void deleteActivityType(ActivityType activityType) {
        Repository.deleteActivityType(repo, activityType);
    }

    public void deleteSport() {
        Repository.deleteSport(repo, sport.getValue());
    }

    public void insertActivityType(String activityTypeName) {
        if(sport.getValue() != null)
            Repository.insertActivityType(repo, new ActivityType(activityTypeName, sport.getValue().name));
    }
}
