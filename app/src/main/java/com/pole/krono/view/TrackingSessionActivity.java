package com.pole.krono.view;

import android.Manifest;
import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModelProviders;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.pole.krono.CSVExporter;
import com.pole.krono.R;
import com.pole.krono.model.TrackingSessionViewModel;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class TrackingSessionActivity extends AppCompatActivity {

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static final String TAG = "Pole: TSActivity";

    private Toolbar toolbar;

    private TrackingSessionViewModel viewModel;

    private TextView sportActivityTextView;
    private TextView dateTextView;
    private TextView timeTextView;
    private TextView distanceTextView;
    private TextView speedTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trackingsession);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sportActivityTextView = findViewById(R.id.sportActivityTextView);
        dateTextView = findViewById(R.id.dateTextView);
        timeTextView = findViewById(R.id.timeTextView);
        distanceTextView = findViewById(R.id.distanceTextView);
        speedTextView = findViewById(R.id.speedTextView);

        viewModel = ViewModelProviders.of(this).get(TrackingSessionViewModel.class);

        viewModel.getId().setValue(getIntent().getLongExtra("id",-1));

        viewModel.getSession().observe(this, session -> {

            if(session != null) {
                toolbar.setTitle(String.format("%s %s", session.profileName, session.profileSurname));
                if(session.activityType == null)
                    sportActivityTextView.setText(session.sport);
                else
                    sportActivityTextView.setText(String.format("%s - %s", session.sport, session.activityType));

                dateTextView.setText(DateFormat.getDateInstance().format(session.startTime));
                @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("HH:mm");
                timeTextView.setText(String.format("%s - %s", df.format(session.startTime), df.format(session.endTime)));

                DecimalFormat decimalFormat = new DecimalFormat("#.###");
                DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols();
                decimalFormatSymbols.setDecimalSeparator('.');
                decimalFormat.setDecimalFormatSymbols(decimalFormatSymbols);
                speedTextView.setText(String.format("%s Km/h", decimalFormat.format(session.distance * 3600 / (session.endTime - session.startTime))));

                if(session.distance > 1000) {
                    distanceTextView.setText(String.format(Locale.US,"%s Km", decimalFormat.format(session.distance / 1000)));
                } else if(session.distance > 0){
                    distanceTextView.setText(String.format(Locale.US,"%s m", decimalFormat.format(session.distance)));
                } else {
                    distanceTextView.setVisibility(View.INVISIBLE);
                    speedTextView.setVisibility(View.INVISIBLE);
                }
            }
        });

        RecyclerView lapsRecyclerView = findViewById(R.id.lapsRecyclerView);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mLayoutManager.scrollToPosition(0);
        lapsRecyclerView.setLayoutManager(mLayoutManager);
        lapsRecyclerView.setHasFixedSize(true);
        LapAdapter lapAdapter = new LapAdapter(this);
        lapsRecyclerView.setAdapter(lapAdapter);
        lapsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        viewModel.getLaps().observe(this, laps -> {
            if (laps != null) {
                lapAdapter.setLaps(laps);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        menu.add(Menu.NONE, 1, Menu.NONE, "Export to CSV");
        menu.add(Menu.NONE, 2, Menu.NONE, "Delete session");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 1:
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                    exportToCSV();
                else
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_EXTERNAL_STORAGE);
                return true;
            case 2:
                new AlertDialog.Builder(this)
                        .setTitle("Delete session")
                        .setMessage("Are you sure you want to delete this tracking session?\nYou will lose the data forever")

                        // Specifying a listener allows you to take an action before dismissing the dialog.
                        // The dialog is automatically dismissed when a dialog button is clicked.
                        .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                            viewModel.deleteTrackingSession();
                            finish();
                        })

                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();

                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_EXTERNAL_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                exportToCSV();
            } else {
                Toast.makeText(this, "WRITE_EXTERNAL_STORAGE Denied", Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void exportToCSV() {
        CSVExporter.exportToCsv(getApplicationContext(), viewModel.getSession().getValue(), viewModel.getLaps().getValue());
    }
}
