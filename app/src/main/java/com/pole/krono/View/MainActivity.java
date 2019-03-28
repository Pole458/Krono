package com.pole.krono.View;

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
import com.pole.krono.model.MyViewModel;
import com.pole.krono.model.Profile;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, ProfilesFragment.ProfilesFragmentListener {

    private MyViewModel viewModel;

    private DrawerLayout drawer;

    private NavigationView navigationView;

    private TextView profileFullName;
    private TextView profileSport;

    private Toolbar toolbar;

    private static int ADD_PROFILE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d("MainActivity", "onCreate");

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        profileFullName = navigationView.getHeaderView(0).findViewById(R.id.profileName);
        profileSport = navigationView.getHeaderView(0).findViewById(R.id.profileSport);

        viewModel = ViewModelProviders.of(this).get(MyViewModel.class);

        changeFragment(new ChronometerFragment(), R.id.nav_chronometer, R.string.app_name);

        if(getIntent().hasExtra("profile_name")) {
            viewModel.addProfile(new Profile(getIntent().getStringExtra("profile_name"),
                    getIntent().getStringExtra("profile_surname"),
                    getIntent().getStringExtra("profile_sport")));
        }

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        viewModel.getSelectedProfile().observe(this, profile -> {
            // Update the UI, in this case, a TextView.
            if (profile != null) {
                profileFullName.setText(profile.getFullName());
                profileSport.setText(profile.getSport());
            }
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

    @SuppressWarnings("StatementWithEmptyBody")
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
        } else if (id == R.id.nav_analytics) {

        } else if (id == R.id.nav_settings) {

        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);

        if(fragment instanceof ProfilesFragment)
            ((ProfilesFragment) fragment).setProfileFragmentListener(this);

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
        viewModel.setSelectedProfile(profile);
        changeFragment(new ChronometerFragment(), R.id.nav_chronometer, R.string.app_name);
    }

    private void startAddProfileActivity() {
        Intent i = new Intent(this, AddProfileActivity.class);
        startActivityForResult(i, ADD_PROFILE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == ADD_PROFILE_REQUEST && resultCode == RESULT_OK) {

            viewModel.addProfile(new Profile(data.getStringExtra("profile_name"),
                    data.getStringExtra("profile_surname"),
                    data.getStringExtra("profile_sport")));

            changeFragment(new ProfilesFragment(), R.id.nav_profiles, R.string.profiles);

        }
    }
}