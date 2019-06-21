package com.pole.krono.view;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.pole.krono.R;
import com.pole.krono.view.adapter.TrackingSessionAdapter;
import com.pole.krono.viewmodel.ProfileViewModel;
import com.pole.krono.model.TrackingSession;
import com.skyhope.eventcalenderlibrary.CalenderEvent;
import com.skyhope.eventcalenderlibrary.model.Event;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class TrackingSessionCalendarFragment extends Fragment {

    private static final String TAG = "Pole: TSCalendarFrag";

    private ProfileViewModel viewModel;

    private AppCompatActivity activity;

    private CalenderEvent calendar;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.activity = (AppCompatActivity) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Log.v(TAG, "onCreateView");

        View view = inflater.inflate(R.layout.layout_tracking_session_calendar, container, false);

        RecyclerView profilesRecyclerView = view.findViewById(R.id.recyclerView);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(activity);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mLayoutManager.scrollToPosition(0);
        profilesRecyclerView.setLayoutManager(mLayoutManager);
        profilesRecyclerView.setHasFixedSize(true);
        final TrackingSessionAdapter adapter = new TrackingSessionAdapter(activity);
        profilesRecyclerView.setAdapter(adapter);
        profilesRecyclerView.setLayoutManager(new LinearLayoutManager(activity));

        viewModel = ViewModelProviders.of(activity).get(ProfileViewModel.class);

        // update UI
        viewModel.getTodayTrackingSession().observe(activity, adapter::setTrackingSession);

        calendar = view.findViewById(R.id.calendar);
        calendar.setOnCalendarDayClickListener(time -> viewModel.getStartTime().setValue(time));

        // Get tracking sessions for today
        Calendar today = Calendar.getInstance();
        today.set(Calendar.MILLISECOND, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.HOUR_OF_DAY, 0);
        Log.v(TAG, "new startTime set to today: " + today.getTime());
        viewModel.getStartTime().setValue(today.getTimeInMillis());

        viewModel.getAllTrackingSession().observe(this, trackingSessions -> {
           if(trackingSessions != null) {
               setEvents(trackingSessions, calendar);
               Log.v(TAG, "Found " + trackingSessions.size() + " session for " + viewModel.getProfile().getFullName());
           }
        });

        return view;
    }

    private static void setEvents(List<TrackingSession> sessions, CalenderEvent calenderEvent) {
        new AsyncTask<CalenderEvent, Void, CalenderEvent>() {

            private List<Event> events;

            @Override
            protected CalenderEvent doInBackground(CalenderEvent... calenderEvents) {
                events = new ArrayList<>();

                long lastTime = 0;

                for (TrackingSession session : sessions) {
                    Event e = new Event(session.startTime, session.sport);
                    if(e.getTime() != lastTime) {
                        lastTime = e.getTime();
                        events.add(e);
                        Log.v(TAG, "Added new event with time " + e.getTime());
                    } else {
                        Log.v(TAG, "Found an other event at time " + lastTime);
                    }
                }
                return calenderEvents[0];
            }

            @Override
            protected void onPostExecute(CalenderEvent calenderEvent) {
                calenderEvent.setEvents(events);
            }
        }.execute(calenderEvent);
    }
}