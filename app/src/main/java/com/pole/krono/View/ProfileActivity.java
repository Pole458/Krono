package com.pole.krono.View;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;
import com.pole.krono.R;
import com.pole.krono.model.Profile;
import com.pole.krono.model.ProfileViewModel;
import com.pole.krono.model.TrackingSession;

import java.util.List;

public class ProfileActivity extends AppCompatActivity {

    private ProfileViewModel viewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Profile Page");

        viewModel = ViewModelProviders.of(this).get(ProfileViewModel.class);

        if(getIntent().hasExtra("profile_name")) {
            viewModel.setProfile(new Profile(getIntent().getStringExtra("profile_name"),
                    getIntent().getStringExtra("profile_surname"),
                    getIntent().getStringExtra("profile_sport")));
        }


        ((TextView)findViewById(R.id.fullnameTextView)).setText(viewModel.getProfile().getFullName());

        RecyclerView profilesRecyclerView = findViewById(R.id.trackingSessionsRecyclerView);
        // use a linear layout manager
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mLayoutManager.scrollToPosition(0);
        profilesRecyclerView.setLayoutManager(mLayoutManager);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        profilesRecyclerView.setHasFixedSize(true);

        // specify an adapter (see also next example)
        final TrackingSessionAdapter adapter = new TrackingSessionAdapter(this);
        profilesRecyclerView.setAdapter(adapter);
        profilesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // update UI
        viewModel.getTrackingSession().observe(this, adapter::setTrackingSession);

    }

    private class TrackingSessionAdapter extends RecyclerView.Adapter<TrackingSessionAdapter.ViewHolder> {

        private LayoutInflater layoutInflater;
        private List<TrackingSession> trackingSessions; // cached copy of profiles

        TrackingSessionAdapter(Context context) {
            layoutInflater = LayoutInflater.from(context);
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            // create a new view
            // set the view's size, margins, paddings and layout parameters
            return new ViewHolder(layoutInflater.inflate(R.layout.recycler_view_tracking_session_item, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
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
            private TextView idTextView;

            ViewHolder(View itemView) {
                super(itemView);

                sportTextView = itemView.findViewById(R.id.sportTextView);
                activityTypeTextView = itemView.findViewById(R.id.activityTypeTextView);
                startDateTextView = itemView.findViewById(R.id.startDateTextView);
                endDateTextView = itemView.findViewById(R.id.endDateTextView);
                idTextView = itemView.findViewById(R.id.sessionIDTextView);

            }

            void setTrackingSession(TrackingSession trackingSession) {
                if(trackingSession != null) {
                    sportTextView.setText(trackingSession.sport);
                    activityTypeTextView.setText(trackingSession.activityType);
                    startDateTextView.setText(trackingSession.startTime.toString());
                    if(trackingSession.endTime != null)
                        endDateTextView.setText(trackingSession.endTime.toString());
                    idTextView.setText(String.valueOf(trackingSession.id));

                } else {
                    sportTextView.setText("Loading");

                }

            }
        }
    }

}
