package com.pole.krono;

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
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import com.pole.krono.View.ChronometerFragment;
import com.pole.krono.View.MyFragment;
import com.pole.krono.View.ProfilesFragment;
import com.pole.krono.model.MyViewModel;
import com.pole.krono.model.Profile;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, ProfilesFragment.ProfilesFragmentListener {

    private MyViewModel viewModel;

    private DrawerLayout drawer;

    private MyFragment currentFragment;

    private NavigationView navigationView;

    private TextView profileFullName;
    private TextView profileSport;

    public static int ADD_PROFILE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
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

        changeFragment(new ChronometerFragment(), null, false, R.id.nav_chronometer);

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
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_chronometer) {
            changeFragment(new ChronometerFragment(), null, true, 0);
        } else if (id == R.id.nav_profiles) {
            changeFragment(new ProfilesFragment(), null, true, 0);
        } else if(id == R.id.nav_sports) {

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

    private void changeFragment(MyFragment newFragment, Bundle bundle, boolean addToBackStack, int nav) {

        if(currentFragment != null)
            currentFragment.OnReplaced();

        if(bundle != null)
            newFragment.setArguments(bundle);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, newFragment);
        if(addToBackStack)
            fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

        if(nav != 0)
            navigationView.setCheckedItem(nav);

        currentFragment = newFragment;

    }

    @Override
    public void onAddProfileButtonPressed() {
        startAddProfileActivity();
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

            changeFragment(new ProfilesFragment(), null, false, R.id.nav_profiles);

        }
    }
}
