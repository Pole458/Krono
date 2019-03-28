package com.pole.krono.View;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.pole.krono.MillisecondChronometer;
import com.pole.krono.R;
import com.pole.krono.model.*;

import java.util.Calendar;

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
}
