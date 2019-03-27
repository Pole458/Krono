package com.pole.krono.View;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.pole.krono.MillisecondChronometer;
import com.pole.krono.R;

public class ChronometerFragment extends MyFragment {

    private MillisecondChronometer milliChronometer;

    private Button startButton;
    private Button pauseResumeButton;
    private Button lapButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_chronometer, container, false);

        milliChronometer = view.findViewById(R.id.chronometer);

        startButton = view.findViewById(R.id.startTrackingButton);
        startButton.setOnClickListener(v -> {

            if (startButton.getText().equals("Start Tracking")) {

                startButton.setText("Stop Tracking");
                milliChronometer.restart();

                pauseResumeButton.setVisibility(View.VISIBLE);
                lapButton.setVisibility(View.VISIBLE);

            } else {

                startButton.setText("Start Tracking");
                pauseResumeButton.setText("Pause");
                milliChronometer.stop();

                pauseResumeButton.setVisibility(View.GONE);
                lapButton.setVisibility(View.GONE);

            }
        });

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


        return view;
    }
}
