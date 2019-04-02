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
import android.widget.*;
import com.pole.krono.MillisecondChronometer;
import com.pole.krono.R;
import com.pole.krono.model.*;

import java.util.Calendar;
import java.util.List;

public class ChronometerFragment extends Fragment {

    private MyViewModel viewModel;

    private MillisecondChronometer milliChronometer;

    private TextView fullnameTextView;

    private Button startButton;
    private Button pauseResumeButton;
    private Button lapButton;

    private Spinner sportSpinner;
    private Spinner activityTypeSpinner;

    private ArrayAdapter<Sport> sportArrayAdapter;
    private ArrayAdapter<ActivityType> activityTypeArrayAdapter;

    private AppCompatActivity activity;

    private RecyclerView lapsRecyclerView;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.activity = (AppCompatActivity) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_chronometer, container, false);

        milliChronometer = view.findViewById(R.id.chronometer);

        startButton = view.findViewById(R.id.startTrackingButton);
        startButton.setOnClickListener(v -> {

            if (startButton.getText().equals("Start Tracking")) {

                startTracking();

            } else {

                stopTracking();

            }
        });

        fullnameTextView = view.findViewById(R.id.fullnameTextView);

        pauseResumeButton = view.findViewById(R.id.pauseResumeTrackingButton);

        pauseResumeButton.setOnClickListener(v -> {

            if(pauseResumeButton.getText().equals("Pause")) {

                pauseResumeButton.setText("Resume");
                milliChronometer.pause();

            } else {

                pauseResumeButton.setText("Pause");
                milliChronometer.resume();

            }
        });

        lapButton = view.findViewById(R.id.lapTrackingButton);

        lapButton.setOnClickListener(v -> viewModel.addLap(milliChronometer.lap(), milliChronometer.getLaps()));

        sportSpinner = view.findViewById(R.id.sportSpinner);
        sportArrayAdapter = new ArrayAdapter<>(activity, R.layout.support_simple_spinner_dropdown_item);
        sportSpinner.setAdapter(sportArrayAdapter);

        activityTypeSpinner = view.findViewById(R.id.activityTypeSpinner);
        activityTypeArrayAdapter = new ArrayAdapter<>(activity, R.layout.support_simple_spinner_dropdown_item);
        activityTypeSpinner.setAdapter(activityTypeArrayAdapter);

        viewModel = ViewModelProviders.of(activity).get(MyViewModel.class);

        viewModel.getSelectedProfile().observe(activity, selectedProfile -> {
            if(selectedProfile != null) {
                fullnameTextView.setText(selectedProfile.getFullName());
                viewModel.getSelectedSport().setValue(new Sport(selectedProfile.getSport()));
            }
        });

        viewModel.getSports().observe(this, sports -> {
            if(sports != null) {
                sportArrayAdapter.addAll(sports);
                sportArrayAdapter.notifyDataSetChanged();
                Log.v("Pole", "Sport spinner updated");
                if(viewModel.getSelectedSport().getValue() != null)
                    selectSpinnerItemByValue(sportSpinner, viewModel.getSelectedSport().getValue().name);
            }
        });

        sportSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            boolean init = true;

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(init) {
                    init = false;
                } else
                    viewModel.getSelectedSport().setValue(sportArrayAdapter.getItem(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        viewModel.getSelectedSport().observe(activity, sport -> {
            if(sport != null) {
                Log.v("Pole", "Selected sport: " + sport.name);
                viewModel.updateActivityTypes();
                selectSpinnerItemByValue(sportSpinner, sport.name);
            }
        });

        viewModel.getActivityTypes().observe(activity, activityTypes -> {
            activityTypeArrayAdapter.clear();
            if (activityTypes != null) {
                activityTypeArrayAdapter.addAll(activityTypes);
                Log.v("Pole", "Found " + activityTypes.size() + " activity types");
            }
            activityTypeArrayAdapter.notifyDataSetChanged();
        });

        lapsRecyclerView = view.findViewById(R.id.lapsRecyclerView);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(activity);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mLayoutManager.scrollToPosition(0);
        lapsRecyclerView.setLayoutManager(mLayoutManager);
        lapsRecyclerView.setHasFixedSize(true);
        final LapAdapter adapter = new LapAdapter(activity);
        lapsRecyclerView.setAdapter(adapter);
        lapsRecyclerView.setLayoutManager(new LinearLayoutManager(activity));
        viewModel = ViewModelProviders.of(activity).get(MyViewModel.class);
        viewModel.getLaps().observe(this, adapter::setLaps);


        return view;
    }

    private static void selectSpinnerItemByValue(Spinner spinner, String value) {
        Adapter adapter = spinner.getAdapter();
        for (int position = 0; position < adapter.getCount(); position++) {
            if(adapter.getItem(position).toString().equals(value)) {
                spinner.setSelection(position);
                return;
            }
        }
    }

    private void startTracking() {

        startButton.setText("Stop Tracking");
        milliChronometer.restart();

        pauseResumeButton.setVisibility(View.VISIBLE);
        lapButton.setVisibility(View.VISIBLE);

        Profile profile = viewModel.getSelectedProfile().getValue();
        Sport sport = viewModel.getSelectedSport().getValue();
        ActivityType activityType = null;
        if(activityTypeArrayAdapter.getCount() > 0)
            activityType = activityTypeArrayAdapter.getItem(activityTypeSpinner.getSelectedItemPosition());

        if(profile != null && sport != null) {
            TrackingSession session = new TrackingSession();

            session.profileName = profile.getName();
            session.profileSurname = profile.getSurname();
            session.sport = sport.name;
            if(activityType != null)
                session.activityType = activityType.name;
            session.startTime = Calendar.getInstance().getTime();

            viewModel.insertTrackingSession(session);
        }
    }

    private void stopTracking() {

        startButton.setText("Start Tracking");
        pauseResumeButton.setText("Pause");
        milliChronometer.stop();

        pauseResumeButton.setVisibility(View.GONE);
        lapButton.setVisibility(View.GONE);

        viewModel.stopTracking();
    }

    private class LapAdapter extends RecyclerView.Adapter<LapAdapter.ViewHolder> {

        private LayoutInflater layoutInflater;
        private List<Lap> laps;

        LapAdapter(Context context) {
            layoutInflater = LayoutInflater.from(context);
        }

        @NonNull
        @Override
        public LapAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            // create a new view
            // set the view's size, margins, paddings and layout parameters
            return new ViewHolder(layoutInflater.inflate(R.layout.recycler_item_lap, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull LapAdapter.ViewHolder holder, int position) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
            holder.setLap(laps.get(position));
        }

        void setLaps(List<Lap> laps){
            this.laps = laps;
            Log.v("Pole", "ChronoFragment: added " + laps.size() + " laps");
            notifyDataSetChanged();
        }

        @Override
        public int getItemCount() {
            if (laps != null)
                return laps.size();
            else return 0;
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            private TextView lapTimeTextView;
            private TextView gapTextView;
            private TextView lapNumberTextView;

            ViewHolder(View itemView) {
                super(itemView);

                lapTimeTextView = itemView.findViewById(R.id.lapTimeTextView);
                gapTextView = itemView.findViewById(R.id.gapTextView);
                lapNumberTextView = itemView.findViewById(R.id.lapNumberTextView);

            }

            void setLap(Lap lap) {
                if(lap != null) {
                    lapTimeTextView.setText(MillisecondChronometer.getTimeString(lap.time));
                    gapTextView.setText("+00:00");
                    lapNumberTextView.setText(String.valueOf(lap.lapNumber));
                } else {
                    lapTimeTextView.setText("Loading");
                    gapTextView.setText("Loading");
                    lapNumberTextView.setText("Loading");
                }
            }
        }
    }

}
