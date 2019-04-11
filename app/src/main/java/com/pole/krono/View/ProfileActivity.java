package com.pole.krono.View;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import com.pole.krono.R;
import com.pole.krono.model.Profile;
import com.pole.krono.model.ProfileViewModel;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        ProfileViewModel viewModel = ViewModelProviders.of(this).get(ProfileViewModel.class);

        if(getIntent().hasExtra("profile_name")) {
            viewModel.setProfile(new Profile(getIntent().getStringExtra("profile_name"),
                    getIntent().getStringExtra("profile_surname"),
                    getIntent().getStringExtra("profile_sport")));
        }

        ((Toolbar)findViewById(R.id.toolbar)).setTitle(viewModel.getProfile().getFullName());

        ViewPager pager = findViewById(R.id.viewPager);
        PagerAdapter pagerAdapter = new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            static final int NUM_ITEMS = 2;

            @Override
            public Fragment getItem(int i) {
                switch (i) {
                    case 0:
                        return new TrackingSessionsFragment();
                    case 1:
                        return new TrackingSessionCalendarFragment();
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
}
