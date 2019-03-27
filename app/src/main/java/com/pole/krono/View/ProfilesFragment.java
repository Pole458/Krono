package com.pole.krono.View;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.pole.krono.R;
import com.pole.krono.model.MyViewModel;
import com.pole.krono.model.Profile;

import java.util.List;

public class ProfilesFragment extends MyFragment {

    private ProfilesFragmentListener listener;

    private MyViewModel viewModel;

    private RecyclerView profilesRecyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profiles, container, false);

        profilesRecyclerView = view.findViewById(R.id.profiles_recyclerView);
        // use a linear layout manager
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mLayoutManager.scrollToPosition(0);
        profilesRecyclerView.setLayoutManager(mLayoutManager);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        profilesRecyclerView.setHasFixedSize(true);

        // specify an adapter (see also next example)
        final MyAdapter adapter = new MyAdapter(getContext());
        profilesRecyclerView.setAdapter(adapter);
        profilesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        viewModel = ViewModelProviders.of(getActivity()).get(MyViewModel.class);
        // update UI
        viewModel.getProfiles().observe(this, adapter::setProfiles);

        view.findViewById(R.id.add_profile_button).setOnClickListener(v -> listener.onAddProfileButtonPressed());

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
            return new ViewHolder(layoutInflater.inflate(R.layout.recycler_item, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull MyAdapter.ViewHolder holder, int position) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
            if(profiles != null) {
                holder.setText(profiles.get(position));
            } else {
                holder.setLoading();
            }
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

            ViewHolder(View itemView) {
                super(itemView);

                nameTextView = itemView.findViewById(R.id.fullnameTextView);
                sportTextView = itemView.findViewById(R.id.sportTextView);
            }

            public void setText(Profile profile) {
                nameTextView.setText(profile.getFullName());
                sportTextView.setText(profile.getSport());
            }

            void setLoading() {
                nameTextView.setText("Loading");
                sportTextView.setText("Loading");
            }
        }
    }

    public void setProfileFragmentListener(ProfilesFragmentListener listener) {
        this.listener = listener;
    }

    public interface ProfilesFragmentListener {
        void onAddProfileButtonPressed();
    }
}
