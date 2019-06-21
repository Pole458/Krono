package com.pole.krono.view;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import com.pole.krono.R;
import com.pole.krono.viewmodel.MainViewModel;
import com.pole.krono.model.Profile;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, ProfilesFragment.Listener, SelectProfileFragment.Listener, ChronometerFragment.Listener {

    private MainViewModel viewModel;

    private DrawerLayout drawer;

    private NavigationView navigationView;

    private TextView profileFullName;
    private TextView profileSport;

    private Toolbar toolbar;

    private ActionBarDrawerToggle toggle;

    private static int ADD_PROFILE_REQUEST = 1;

    private static final String TAG = "Pole: MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);

        Intent intent = getIntent();

        if(intent.hasExtra("profile_name")) {
            Profile profile = new Profile(intent.getStringExtra("profile_name"),
                    intent.getStringExtra("profile_surname"),
                    intent.getStringExtra("profile_sport"));
            Log.v(TAG, "Profile received: " + profile.getFullName());
            viewModel.insertProfile(getApplicationContext(), profile);
        } else
            Log.v(TAG, "No intent received");

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);

        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        profileFullName = navigationView.getHeaderView(0).findViewById(R.id.profileName);
        profileSport = navigationView.getHeaderView(0).findViewById(R.id.profileSport);

        changeFragment(new ChronometerFragment(), R.id.nav_chronometer, R.string.app_name);

        viewModel.getSelectedProfile().observe(this, profile -> {
            if (profile != null) {
                profileFullName.setText(profile.getFullName());
                profileSport.setText(profile.getSport());
            }
        });

        navigationView.getHeaderView(0).findViewById(R.id.profileImageView).setOnClickListener(v -> {
            SelectProfileFragment bottomNavigationDrawerFragment = new SelectProfileFragment();
            bottomNavigationDrawerFragment.show(getSupportFragmentManager(), bottomNavigationDrawerFragment.getTag());

        });
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if(!navigationView.getMenu().getItem(0).isChecked()) {
            changeFragment(new ChronometerFragment(), R.id.nav_chronometer, R.string.app_name);
        }else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_chronometer) {
            changeFragment(new ChronometerFragment(), 0, R.string.app_name);
        } else if (id == R.id.nav_profiles) {
            changeFragment(new ProfilesFragment(), 0, R.string.profiles);
        } else if(id == R.id.nav_sports) {
            changeFragment(new SportsFragment(), 0, R.string.sports);
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);

        if(fragment instanceof ProfilesFragment)
            ((ProfilesFragment) fragment).setListener(this);

        if(fragment instanceof SelectProfileFragment)
            ((SelectProfileFragment) fragment).setListener(this);

        if(fragment instanceof ChronometerFragment)
            ((ChronometerFragment) fragment).setListener(this);
    }

    private void changeFragment(Fragment newFragment, int nav, int stringId) {

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, newFragment);
        fragmentTransaction.commit();

        toolbar.setTitle(stringId);

        if(nav != 0)
            navigationView.setCheckedItem(nav);

    }

    @Override
    public void onAddProfileButtonPressed() {
        startAddProfileActivity();
    }

    @Override
    public void setSelectedProfile(Profile profile) {
        viewModel.setSelectedProfile(getApplicationContext(), profile);
        drawer.closeDrawer(GravityCompat.START);
//        changeFragment(new ChronometerFragment(), R.id.nav_chronometer, R.string.app_name);
    }

    private void startAddProfileActivity() {
        Intent i = new Intent(this, AddProfileActivity.class);
        startActivityForResult(i, ADD_PROFILE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == ADD_PROFILE_REQUEST && resultCode == RESULT_OK) {

            viewModel.insertProfile(getApplicationContext(), new Profile(data.getStringExtra("profile_name"),
                    data.getStringExtra("profile_surname"),
                    data.getStringExtra("profile_sport")));

            changeFragment(new ProfilesFragment(), R.id.nav_profiles, R.string.profiles);

        }
    }

    @Override
    public void onStartTracking() {
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        toggle.setDrawerIndicatorEnabled(false);
    }

    @Override
    public void onStopTracking() {
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        toggle.setDrawerIndicatorEnabled(true);
    }
}
