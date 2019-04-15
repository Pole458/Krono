package com.pole.krono.View;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.pole.krono.R;
import com.pole.krono.model.MainViewModel;
import com.pole.krono.model.Sport;

import java.util.List;

public class SportsFragment extends Fragment {

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

        RecyclerView sportRecyclerView = view.findViewById(R.id.recyclerView);
        // use a linear layout manager
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(activity);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mLayoutManager.scrollToPosition(0);
        sportRecyclerView.setLayoutManager(mLayoutManager);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        sportRecyclerView.setHasFixedSize(true);

        // specify an adapter (see also next example)
        final SportAdapter adapter = new SportAdapter(activity);
        sportRecyclerView.setAdapter(adapter);
        sportRecyclerView.setLayoutManager(new LinearLayoutManager(activity));

        MainViewModel viewModel = ViewModelProviders.of(activity).get(MainViewModel.class);
        // update UI
        viewModel.getSports().observe(this, adapter::setSports);

        return view;
    
    }

    private class SportAdapter extends RecyclerView.Adapter<SportAdapter.ViewHolder> {

        private LayoutInflater layoutInflater;
        private List<Sport> sports;

        SportAdapter(Context context) {
            layoutInflater = LayoutInflater.from(context);
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            // create a new view
            // set the view's size, margins, paddings and layout parameters
            return new ViewHolder(layoutInflater.inflate(R.layout.recycler_item_sport, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
            holder.setSport(sports.get(position));
        }

        void setSports(List<Sport> sports){
            this.sports = sports;
            notifyDataSetChanged();
        }

        @Override
        public int getItemCount() {
            if (sports != null)
                return sports.size();
            else return 0;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private TextView sportTextView;

            ViewHolder(View itemView) {
                super(itemView);

                sportTextView = itemView.findViewById(R.id.sportTextView);
            }

            public void setSport(Sport sport) {
                if(sport != null) {
                    sportTextView.setText(sport.name);
                } else {
                    sportTextView.setText(R.string.loading);
                }

            }
        }
    }


}
