package com.pole.krono.View;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.graphics.Color;
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

    private RecyclerView lapsRecyclerView;

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

            if (startButton.getText().equals("Start Tracking")) {

                startTracking();

            } else {

                stopTracking();

            }
        });

        fullnameTextView = view.findViewById(R.id.fullnameTextView);
        sportActivityTextView = view.findViewById(R.id.sportActivityTextView);

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

        lapButton = view.findViewById(R.id.lapTrackingButton);

        lapButton.setOnClickListener(v -> chronoViewModel.insertLap(milliChronometer.lap(), milliChronometer.getLaps()));

        sportSpinner = view.findViewById(R.id.sportSpinner);
        sportArrayAdapter = new ArrayAdapter<>(activity, R.layout.support_simple_spinner_dropdown_item);
        sportSpinner.setAdapter(sportArrayAdapter);

        activityTypeSpinner = view.findViewById(R.id.activityTypeSpinner);
        activityTpeTextView = view.findViewById(R.id.activityTypeTextView);
        activityTypeArrayAdapter = new ArrayAdapter<>(activity, R.layout.support_simple_spinner_dropdown_item);
        activityTypeSpinner.setAdapter(activityTypeArrayAdapter);

        mainViewModel = ViewModelProviders.of(activity).get(MainViewModel.class);
        chronoViewModel = ViewModelProviders.of(activity).get(ChronometerViewModel.class);

        mainViewModel.getSelectedProfile().observe(activity, selectedProfile -> {
            if(selectedProfile != null) {
                fullnameTextView.setText(selectedProfile.getFullName());
                chronoViewModel.getSelectedSport().setValue(new Sport(selectedProfile.getSport()));
            }
        });

        chronoViewModel.getSports().observe(this, sports -> {
            if(sports != null) {
                sportArrayAdapter.addAll(sports);
                sportArrayAdapter.notifyDataSetChanged();
                Log.v("Pole", "Sport spinner updated");
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

        chronoViewModel.getSelectedSport().observe(activity, sport -> {
            if(sport != null) {
                Log.v("Pole", "Selected sport: " + sport.name);
                selectSpinnerItemByValue(sportSpinner, sport.name);
            }
        });

        chronoViewModel.getActivityTypes().observe(activity, activityTypes -> {
            activityTypeArrayAdapter.clear();
            if (activityTypes != null && activityTypes.size() > 0) {
                activityTypeArrayAdapter.addAll(activityTypes);
                Log.v("Pole", "Found " + activityTypes.size() + " activity types");
                activityTypeSpinner.setVisibility(View.VISIBLE);
                activityTpeTextView.setVisibility(View.VISIBLE);
            } else {
                Log.v("Pole", "Found 0 activity types");
                activityTypeSpinner.setVisibility(View.INVISIBLE);
                activityTpeTextView.setVisibility(View.INVISIBLE);
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
        mainViewModel = ViewModelProviders.of(activity).get(MainViewModel.class);
        chronoViewModel.getLaps().observe(this, laps -> {
            if (laps != null) {
                adapter.setLaps(laps);
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
            if(activityType != null) {
                session.activityType = activityType.name;
                sportActivityTextView.setText(String.format("%s, %s",sport.name, activityType.name));
            } else
                sportActivityTextView.setText(sport.name);

            session.startTime = Calendar.getInstance().getTimeInMillis();

            chronoViewModel.insertTrackingSession(session);
        }

        pauseResumeButton.setVisibility(View.VISIBLE);
        lapButton.setVisibility(View.VISIBLE);

        sportSpinner.setVisibility(View.INVISIBLE);
        activityTypeSpinner.setVisibility(View.INVISIBLE);
        View view = getView();
        if(view != null) {
            getView().findViewById(R.id.sportTextView).setVisibility(View.INVISIBLE);
            getView().findViewById(R.id.activityTypeTextView).setVisibility(View.INVISIBLE);
            view.findViewById(R.id.line).setVisibility(View.VISIBLE);
        }

        sportActivityTextView.setVisibility(View.VISIBLE);

        lapsRecyclerView.setVisibility(View.VISIBLE);

        listener.onStartTracking();

    }

    private void stopTracking() {

        startButton.setText(R.string.start_tracking);
        pauseResumeButton.setText(R.string.pause);
        milliChronometer.stop();

        chronoViewModel.insertLap(milliChronometer.lap(), milliChronometer.getLaps());
        chronoViewModel.stopTracking();

        pauseResumeButton.setVisibility(View.GONE);
        lapButton.setVisibility(View.GONE);

        sportSpinner.setVisibility(View.VISIBLE);
        activityTypeSpinner.setVisibility(View.VISIBLE);
        View view = getView();
        if(view != null) {
            getView().findViewById(R.id.sportTextView).setVisibility(View.VISIBLE);
            getView().findViewById(R.id.activityTypeTextView).setVisibility(View.VISIBLE);
            getView().findViewById(R.id.line).setVisibility(View.GONE);
        }

        sportActivityTextView.setVisibility(View.GONE);

        lapsRecyclerView.setVisibility(View.GONE);

        listener.onStopTracking();
    }

    void setListener(Listener listener) {
        this.listener = listener;
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
            holder.setLap(position);
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

            void setLap(int pos) {
                Lap lap = laps.get(pos);
                if(lap != null) {
                    lapTimeTextView.setText(MillisecondChronometer.getTimeString(lap.time));
                    lapNumberTextView.setText(String.valueOf(lap.lapNumber));
                    if(pos < laps.size() - 1) {
                        long gap = lap.time - laps.get(pos + 1).time;
                        gapTextView.setText(MillisecondChronometer.getGapString(gap));
                        gapTextView.setTextColor(gap > 0 ? Color.RED : Color.GREEN);
                    } else
                        gapTextView.setText("");
                } else {
                    lapTimeTextView.setText(R.string.loading);
                    gapTextView.setText(R.string.loading);
                    lapNumberTextView.setText(R.string.loading);
                }
            }
        }
    }

    public interface Listener {
        void onStartTracking();
        void onStopTracking();
    }
}
