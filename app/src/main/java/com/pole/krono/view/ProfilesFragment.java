package com.pole.krono.view;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.pole.krono.R;
import com.pole.krono.viewmodel.MainViewModel;
import com.pole.krono.model.Profile;

import java.util.List;

public class ProfilesFragment extends Fragment {

    private Listener listener;

    private AppCompatActivity activity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.activity = (AppCompatActivity) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_recycler_button, container, false);

        RecyclerView profilesRecyclerView = view.findViewById(R.id.recyclerView);
        // use a linear layout manager
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(activity);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mLayoutManager.scrollToPosition(0);
        profilesRecyclerView.setLayoutManager(mLayoutManager);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        profilesRecyclerView.setHasFixedSize(true);

        // specify an adapter (see also next example)
        final MyAdapter adapter = new MyAdapter(activity);
        profilesRecyclerView.setAdapter(adapter);
        profilesRecyclerView.setLayoutManager(new LinearLayoutManager(activity));

        MainViewModel viewModel = ViewModelProviders.of(activity).get(MainViewModel.class);
        // update UI
        viewModel.getProfiles().observe(this, adapter::setProfiles);

        FloatingActionButton addButton = view.findViewById(R.id.addButton);
        addButton.setImageDrawable(ContextCompat.getDrawable(activity, R.mipmap.ic_action_person_add));

        view.findViewById(R.id.addButton).setOnClickListener(v -> listener.onAddProfileButtonPressed());

        return view;
    }

    private class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

        private LayoutInflater layoutInflater;
        private List<Profile> profiles; // cached copy of profiles

        MyAdapter(Context context) {
            layoutInflater = LayoutInflater.from(context);
        }

        @NonNull
        @Override
        public MyAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            // create a new view
            // set the view's size, margins, paddings and layout parameters
            return new ViewHolder(layoutInflater.inflate(R.layout.recycler_item_profile, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull MyAdapter.ViewHolder holder, int position) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
            holder.setProfile(profiles.get(position));
        }

        void setProfiles(List<Profile> profiles){
            this.profiles = profiles;
            notifyDataSetChanged();
        }

        @Override
        public int getItemCount() {
            if (profiles != null)
                return profiles.size();
            else return 0;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private TextView nameTextView;
            private TextView sportTextView;
            private Profile profile;

            ViewHolder(View itemView) {
                super(itemView);

                profile = null;

                nameTextView = itemView.findViewById(R.id.fullnameTextView);
                sportTextView = itemView.findViewById(R.id.sportTextView);

                itemView.setOnClickListener(v -> {

                    if(profile != null) {

                        Intent intent = new Intent(activity, ProfileActivity.class);
                        intent.putExtra("profile_name", profile.getName());
                        intent.putExtra("profile_surname", profile.getSurname());
                        intent.putExtra("profile_sport", profile.getSport());
                        startActivity(intent);

                    }
                });
            }

            public void setProfile(Profile profile) {
                this.profile = profile;
                if(profile != null) {
                    nameTextView.setText(profile.getFullName());
                    sportTextView.setText(profile.getSport());
                } else {
                    nameTextView.setText(R.string.loading);
                    sportTextView.setText(R.string.loading);
                }
            }
        }
    }

    void setListener(Listener listener) {
        this.listener = listener;
    }

    public interface Listener {
        void onAddProfileButtonPressed();
    }
}
