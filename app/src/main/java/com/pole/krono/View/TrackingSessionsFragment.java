package com.pole.krono.View;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
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

import java.text.DateFormat;
import java.util.List;

public class TrackingSessionsFragment extends Fragment {

    private AppCompatActivity activity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.activity = (AppCompatActivity) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.layout_recycler, container, false);

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

        ProfileViewModel viewModel = ViewModelProviders.of(activity).get(ProfileViewModel.class);

        // update UI
        viewModel.getTrackingSession().observe(activity, trackingSessions -> {
            Log.v("Pole", "TSFrag: new tracking session: " + trackingSessions.size());
            adapter.setTrackingSession(trackingSessions);
        });

        Log.v("Pole", "TSFrag: onCreateView");

        return view;
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
}
