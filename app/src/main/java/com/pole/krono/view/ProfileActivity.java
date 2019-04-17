package com.pole.krono.view;

import android.Manifest;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import com.pole.krono.CSVExporter;
import com.pole.krono.R;
import com.pole.krono.model.Profile;
import com.pole.krono.model.ProfileViewModel;

import java.io.IOException;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = "Pole: ProfileActivity";

    private static final int REQUEST_EXTERNAL_STORAGE = 1;

    private TrackingSessionsFragment trackingSessionsFragment;
    private TrackingSessionCalendarFragment trackingSessionCalendarFragment;

    private ProfileViewModel viewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Log.v(TAG, "onCreate");

        viewModel = ViewModelProviders.of(this).get(ProfileViewModel.class);

        if(getIntent().hasExtra("profile_name")) {
            viewModel.setProfile(new Profile(getIntent().getStringExtra("profile_name"),
                    getIntent().getStringExtra("profile_surname"),
                    getIntent().getStringExtra("profile_sport")));
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(viewModel.getProfile().getFullName());
        setSupportActionBar(toolbar);

        ViewPager pager = findViewById(R.id.viewPager);
        PagerAdapter pagerAdapter = new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            static final int NUM_ITEMS = 2;

            @Override
            public Fragment getItem(int i) {
                Log.v(TAG, "getItem: " + i);
                switch (i) {
                    case 0:
                        trackingSessionsFragment = new TrackingSessionsFragment();
                        return trackingSessionsFragment;
                    case 1:
                        trackingSessionCalendarFragment = new TrackingSessionCalendarFragment();
                        return trackingSessionCalendarFragment;
                }
                return null;
            }

            @Override
            public int getCount() {
                return NUM_ITEMS;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                switch (position) {
                    case 0:
                        return "All";
                    case 1:
                        return "Calendar";
                }
                return "";
            }
        };
        pager.setAdapter(pagerAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        menu.add(Menu.NONE, 1, Menu.NONE, "Export to CSV");
        menu.add(Menu.NONE, 2, Menu.NONE, "Delete Profile");
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
                        .setTitle("Delete profile")
                        .setMessage("Are you sure you want to delete this profile?\nYou will also lose all the tracking sessions related to this profile")

                        // Specifying a listener allows you to take an action before dismissing the dialog.
                        // The dialog is automatically dismissed when a dialog button is clicked.
                        .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                            if(viewModel.deleteProfile())
                                finish();
                            else
                                Toast.makeText(this, "Cannot delete selected profile", Toast.LENGTH_LONG).show();
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
        CSVExporter.exportToCsv(getApplicationContext(), viewModel.getProfile(), viewModel.getAllTrackingSession().getValue());
    }
}
