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
import com.pole.krono.R;
import com.pole.krono.model.ProfileViewModel;

public class TrackingSessionsFragment extends Fragment {

    private static final String TAG = "Pole: TSFragment";

    private AppCompatActivity activity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.activity = (AppCompatActivity) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Log.v(TAG, "onCreateView");

        View view = inflater.inflate(R.layout.layout_recycler, container, false);

        RecyclerView profilesRecyclerView = view.findViewById(R.id.recyclerView);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(activity);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mLayoutManager.scrollToPosition(0);
        profilesRecyclerView.setLayoutManager(mLayoutManager);
        profilesRecyclerView.setHasFixedSize(true);
        final TrackingSessionAdapter adapter = new TrackingSessionAdapter(activity);
        profilesRecyclerView.setAdapter(adapter);
        profilesRecyclerView.setLayoutManager(new LinearLayoutManager(activity));

        ProfileViewModel viewModel = ViewModelProviders.of(activity).get(ProfileViewModel.class);

        // update UI
        viewModel.getAllTrackingSession().observe(activity, adapter::setTrackingSession);

        return view;
    }

}
