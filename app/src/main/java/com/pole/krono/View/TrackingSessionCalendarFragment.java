package com.pole.krono.View;

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
import android.widget.TextView;
import com.pole.krono.R;
import com.pole.krono.model.ProfileViewModel;
import com.pole.krono.model.TrackingSession;
import com.skyhope.eventcalenderlibrary.CalenderEvent;
import com.skyhope.eventcalenderlibrary.model.Event;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class TrackingSessionCalendarFragment extends Fragment {

    private static final String TAG = "Pole: TSCalendarFrag";

    private ProfileViewModel viewModel;

    private AppCompatActivity activity;

    private CalenderEvent calenderEvent;

    private TextView dayTextView;

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

        dayTextView = view.findViewById(R.id.dayTextView);

        RecyclerView profilesRecyclerView = view.findViewById(R.id.recyclerView);
        // use a linear layout manager
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(activity);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mLayoutManager.scrollToPosition(0);
        profilesRecyclerView.setLayoutManager(mLayoutManager);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        profilesRecyclerView.setHasFixedSize(true);

        // specify an adapter (see also next example)
        final TrackingSessionAdapter adapter = new TrackingSessionAdapter(activity);
        profilesRecyclerView.setAdapter(adapter);
        profilesRecyclerView.setLayoutManager(new LinearLayoutManager(activity));

        viewModel = ViewModelProviders.of(activity).get(ProfileViewModel.class);

        // update UI
        viewModel.getTodayTrackingSession().observe(activity, adapter::setTrackingSession);

        calenderEvent = view.findViewById(R.id.calender_event);
        calenderEvent.initCalderItemClickCallback(dayContainerModel -> {
            viewModel.getStartTime().setValue(dayContainerModel.getTimeInMillisecond());
            dayTextView.setText(dayContainerModel.getDate());
        });

        // Get tracking sessions for today
        Calendar today = Calendar.getInstance();
        today.set(Calendar.MILLISECOND, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.HOUR_OF_DAY, 0);
        Log.v(TAG, "new startTime set to today");
        viewModel.getStartTime().setValue(today.getTimeInMillis());

        viewModel.getAllTrackingSession().observe(this, trackingSessions -> {
           if(trackingSessions != null)
                new SetEventsAsyncTask(trackingSessions).execute(calenderEvent);
        });

        return view;
    }

    private class TrackingSessionAdapter extends RecyclerView.Adapter<TrackingSessionAdapter.ViewHolder> {

        private LayoutInflater layoutInflater;
        private List<TrackingSession> trackingSessions;

        TrackingSessionAdapter(Context context) {
            layoutInflater = LayoutInflater.from(context);
        }

        @NonNull
        @Override
        public TrackingSessionAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            // create a new view
            // set the view's size, margins, paddings and layout parameters
            return new TrackingSessionAdapter.ViewHolder(layoutInflater.inflate(R.layout.recycler_view_tracking_session_item, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull TrackingSessionAdapter.ViewHolder holder, int position) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
            holder.setTrackingSession(trackingSessions.get(position));
        }

        void setTrackingSession(List<TrackingSession> trackingSession){
            this.trackingSessions = trackingSession;
            notifyDataSetChanged();
        }

        @Override
        public int getItemCount() {
            if (trackingSessions != null)
                return trackingSessions.size();
            else return 0;
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            private TextView sportTextView;
            private TextView activityTypeTextView;
            private TextView startDateTextView;
            private TextView endDateTextView;

            ViewHolder(View itemView) {
                super(itemView);

                sportTextView = itemView.findViewById(R.id.sportTextView);
                activityTypeTextView = itemView.findViewById(R.id.activityTypeTextView);
                startDateTextView = itemView.findViewById(R.id.startDateTextView);
                endDateTextView = itemView.findViewById(R.id.endDateTextView);

            }

            void setTrackingSession(TrackingSession trackingSession) {
                if(trackingSession != null) {
                    sportTextView.setText(trackingSession.sport);
                    activityTypeTextView.setText(trackingSession.activityType);
                    startDateTextView.setText(DateFormat.getDateInstance().format(trackingSession.startTime));
                    endDateTextView.setText(DateFormat.getDateInstance().format(trackingSession.endTime));
                } else {
                    sportTextView.setText(R.string.loading);

                }
            }
        }
    }


    private static class SetEventsAsyncTask extends AsyncTask<CalenderEvent, Void, CalenderEvent> {

        private List<Event> events;
        private List<TrackingSession> trackingSessions;

        SetEventsAsyncTask(List<TrackingSession> trackingSessions) {
            this.trackingSessions = trackingSessions;
        }


        @Override
        protected CalenderEvent doInBackground(CalenderEvent... calenderEvents) {

            events = new ArrayList<>();

            /*Calendar month = Calendar.getInstance();
            month.set(Calendar.MILLISECOND, 0);
            month.set(Calendar.SECOND, 0);
            month.set(Calendar.MINUTE, 0);
            month.set(Calendar.HOUR_OF_DAY, 0);
            month.set(Calendar.DAY_OF_MONTH, 0);

            Calendar nextMonth = Calendar.getInstance();
            nextMonth.set(Calendar.MILLISECOND, 0);
            nextMonth.set(Calendar.SECOND, 0);
            nextMonth.set(Calendar.MINUTE, 0);
            nextMonth.set(Calendar.HOUR_OF_DAY, 0);
            nextMonth.set(Calendar.DAY_OF_MONTH, 0);
            nextMonth.set(Calendar.MONTH, nextMonth.get(Calendar.MONTH) + 1);*/

            for (TrackingSession session : trackingSessions) {
                //if(month.getTimeInMillis() < session.startTime && session.startTime < nextMonth.getTimeInMillis())
                    events.add(new Event(session.startTime, session.sport));
            }

            return calenderEvents[0];
        }

        @Override
        protected void onPostExecute(CalenderEvent calenderEvent) {
            super.onPostExecute(calenderEvent);

            for(Event event : events) {
                calenderEvent.addEvent(event);
            }
        }
    }

}
