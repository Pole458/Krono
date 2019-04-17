package com.pole.krono.view;

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

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Calendar;

public class ChronometerFragment extends Fragment {

    private final static String TAG = "Pole: ChronoFrag";

    private MainViewModel mainViewModel;
    private ChronometerViewModel chronoViewModel;

    private MillisecondChronometer milliChronometer;

    private TextView fullnameTextView;
    private TextView sportActivityTextView;

    private Button startButton;
    private Button pauseResumeButton;
    private Button lapButton;

    private Spinner sportSpinner;
    private Spinner activityTypeSpinner;
    private View activityTpeTextView;
    private View centralLayout;
    private EditText distanceEditText;

    private LapAdapter lapAdapter;

    private RecyclerView lapsRecyclerView;
    private View divider;

    private Button distanceButton;

    private ArrayAdapter<Sport> sportArrayAdapter;
    private ArrayAdapter<ActivityType> activityTypeArrayAdapter;

    private AppCompatActivity activity;

    private Listener listener;

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
            String text = startButton.getText().toString();

            if (text.equals(getString(R.string.start_tracking)))
                startTracking();
            else if(text.equals(getString(R.string.stop_tracking)))
                stopTracking();
            else if(text.equals(getString(R.string.restart_tracking)))
                restartTracking();
        });

        fullnameTextView = view.findViewById(R.id.fullnameTextView);
        sportActivityTextView = view.findViewById(R.id.sportActivityTextView);

        lapButton = view.findViewById(R.id.lapTrackingButton);
        pauseResumeButton = view.findViewById(R.id.pauseResumeTrackingButton);

        pauseResumeButton.setOnClickListener(v -> {

            if(pauseResumeButton.getText().equals("Pause")) {

                pauseResumeButton.setText(R.string.resume);
                milliChronometer.pause();

            } else {

                pauseResumeButton.setText(R.string.pause);
                milliChronometer.resume();

            }
        });

        lapButton.setOnClickListener(v -> chronoViewModel.insertLap(milliChronometer.lap(), milliChronometer.getLaps()));

        centralLayout = view.findViewById(R.id.centralLayout);

        sportSpinner = centralLayout.findViewById(R.id.sportSpinner);
        activityTypeSpinner = centralLayout.findViewById(R.id.activityTypeSpinner);
        activityTpeTextView = centralLayout.findViewById(R.id.activityTypeTextView);
        distanceEditText = centralLayout.findViewById(R.id.distanceEditText);
        distanceButton = centralLayout.findViewById(R.id.distanceButton);

        distanceButton.setOnClickListener(v -> {
            if (distanceButton.getText().equals(getString(R.string.kilometers))) {
                distanceButton.setText(R.string.meters);
                if(!distanceEditText.getText().toString().equals("")) {
                    float distance = Float.valueOf(distanceEditText.getText().toString());
                    DecimalFormat df = new DecimalFormat("#.###");
                    DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols();
                    decimalFormatSymbols.setDecimalSeparator('.');
                    df.setDecimalFormatSymbols(decimalFormatSymbols);
                    distanceEditText.setText(df.format(distance * 1000));
                }
            } else {
                distanceButton.setText(R.string.kilometers);
                if(!distanceEditText.getText().toString().equals("")) {
                    float distance = Float.valueOf(distanceEditText.getText().toString());
                    DecimalFormat df = new DecimalFormat("#.###");
                    DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols();
                    decimalFormatSymbols.setDecimalSeparator('.');
                    df.setDecimalFormatSymbols(decimalFormatSymbols);
                    distanceEditText.setText(df.format(distance / 1000));
                }
            }
        });

        sportArrayAdapter = new ArrayAdapter<>(activity, R.layout.support_simple_spinner_dropdown_item);
        sportSpinner.setAdapter(sportArrayAdapter);

        activityTypeArrayAdapter = new ArrayAdapter<>(activity, R.layout.support_simple_spinner_dropdown_item);
        activityTypeSpinner.setAdapter(activityTypeArrayAdapter);

        mainViewModel = ViewModelProviders.of(activity).get(MainViewModel.class);
        chronoViewModel = ViewModelProviders.of(this).get(ChronometerViewModel.class);

        mainViewModel.getSelectedProfile().observe(activity, selectedProfile -> {
            if(selectedProfile != null) {
                fullnameTextView.setText(selectedProfile.getFullName());
                chronoViewModel.getSelectedSport().setValue(new Sport(selectedProfile.getSport()));
//                selectSpinnerItemByValue(sportSpinner, selectedProfile.getSport());
            }
        });

        chronoViewModel.getSports().observe(this, sports -> {
            if(sports != null) {
                sportArrayAdapter.addAll(sports);
                sportArrayAdapter.notifyDataSetChanged();
                Log.v(TAG, "Sport spinner updated");
                if(chronoViewModel.getSelectedSport().getValue() != null)
                    selectSpinnerItemByValue(sportSpinner, chronoViewModel.getSelectedSport().getValue().name);
            }
        });

        sportSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            boolean init = true;

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(init) {
                    init = false;
                } else
                    chronoViewModel.getSelectedSport().setValue(sportArrayAdapter.getItem(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        chronoViewModel.getActivityTypes().observe(activity, activityTypes -> {
            activityTypeArrayAdapter.clear();
            if (activityTypes != null && activityTypes.size() > 0) {
                activityTypeArrayAdapter.addAll(activityTypes);
                Log.v(TAG, "Found " + activityTypes.size() + " activity types");
                activityTypeSpinner.setVisibility(View.VISIBLE);
                activityTpeTextView.setVisibility(View.VISIBLE);
            } else {
                Log.v(TAG, "Found 0 activity types");
                activityTypeSpinner.setVisibility(View.INVISIBLE);
                activityTpeTextView.setVisibility(View.INVISIBLE);
            }
            activityTypeArrayAdapter.notifyDataSetChanged();
        });

        lapsRecyclerView = view.findViewById(R.id.lapsRecyclerView);
        divider = view.findViewById(R.id.divider);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(activity);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mLayoutManager.scrollToPosition(0);
        lapsRecyclerView.setLayoutManager(mLayoutManager);
        lapsRecyclerView.setHasFixedSize(true);
        lapAdapter = new LapAdapter(activity);
        lapsRecyclerView.setAdapter(lapAdapter);
        lapsRecyclerView.setLayoutManager(new LinearLayoutManager(activity));
        mainViewModel = ViewModelProviders.of(activity).get(MainViewModel.class);
        chronoViewModel.getLaps().observe(this, laps -> {
            if (laps != null) {
                lapAdapter.setLaps(laps);
                lapsRecyclerView.scrollToPosition(0);
            }
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

        startButton.setText(R.string.stop_tracking);
        milliChronometer.restart();

        pauseResumeButton.setText(R.string.pause);  //in case it was set to resume

        Profile profile = mainViewModel.getSelectedProfile().getValue();
        Sport sport = chronoViewModel.getSelectedSport().getValue();
        ActivityType activityType = null;
        if(activityTypeArrayAdapter.getCount() > 0)
            activityType = activityTypeArrayAdapter.getItem(activityTypeSpinner.getSelectedItemPosition());

        if(profile != null && sport != null) {
            TrackingSession session = new TrackingSession();

            session.profileName = profile.getName();
            session.profileSurname = profile.getSurname();
            session.sport = sport.name;

            String summary = "";
            summary += sport.name;
            if(activityType != null) {
                summary += ", " + activityType.name;
                session.activityType = activityType.name;
            }
            if(!distanceEditText.getText().toString().equals(""))
                summary += ", " + distanceEditText.getText() + " " + distanceButton.getText();
            sportActivityTextView.setText(summary);

            session.startTime = Calendar.getInstance().getTimeInMillis();

            String distance = distanceEditText.getText().toString();
            if(!distance.equals("")) {
                if(distanceButton.getText().equals("Km")) {
                    session.distance = Float.parseFloat(distance) * 1000;
                } else {
                    session.distance = Float.parseFloat(distance);
                }
            }

            chronoViewModel.insertTrackingSession(session);
        }

        centralLayout.setVisibility(View.GONE);

        sportActivityTextView.setVisibility(View.VISIBLE);
        divider.setVisibility(View.VISIBLE);
        lapsRecyclerView.setVisibility(View.VISIBLE);
        pauseResumeButton.setVisibility(View.VISIBLE);
        lapButton.setVisibility(View.VISIBLE);

        listener.onStartTracking();

    }

    private void stopTracking() {

        startButton.setText(R.string.restart_tracking);
        milliChronometer.stop();

        chronoViewModel.insertLap(milliChronometer.lap(), milliChronometer.getLaps());
        chronoViewModel.stopTracking();

        pauseResumeButton.setVisibility(View.GONE);
        lapButton.setVisibility(View.GONE);

        listener.onStopTracking();
    }

    private void restartTracking() {

        milliChronometer.reset();

        startButton.setText(R.string.start_tracking);

        centralLayout.setVisibility(View.VISIBLE);

        sportActivityTextView.setVisibility(View.GONE);
        divider.setVisibility(View.GONE);
        lapsRecyclerView.setVisibility(View.GONE);

        lapAdapter.clear();

    }

    void setListener(Listener listener) {
        this.listener = listener;
    }

    public interface Listener {
        void onStartTracking();
        void onStopTracking();
    }
}
