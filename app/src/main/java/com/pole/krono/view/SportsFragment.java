package com.pole.krono.view;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import com.pole.krono.R;
import com.pole.krono.viewmodel.MainViewModel;
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
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(activity);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mLayoutManager.scrollToPosition(0);
        sportRecyclerView.setLayoutManager(mLayoutManager);
        sportRecyclerView.setHasFixedSize(true);
        final SportAdapter adapter = new SportAdapter(activity);
        sportRecyclerView.setAdapter(adapter);
        sportRecyclerView.setLayoutManager(new LinearLayoutManager(activity));

        MainViewModel viewModel = ViewModelProviders.of(activity).get(MainViewModel.class);
        // update UI
        viewModel.getSports().observe(this, adapter::setSports);

        view.findViewById(R.id.addButton).setOnClickListener(v -> {

            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle("Insert Sport");

            // Set up the input
            final EditText input = new EditText(activity);
            // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setView(input);

            // Set up the buttons
            builder.setPositiveButton("OK", (dialog, which) -> viewModel.insertSport(input.getText().toString()));
            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

            builder.show();

        });

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
            private String sportName;

            ViewHolder(View itemView) {
                super(itemView);

                sportTextView = itemView.findViewById(R.id.sportTextView);

                itemView.setOnClickListener(v -> {
                    Intent intent = new Intent(activity, SportActivity.class);
                    intent.putExtra("sport", sportName);
                    startActivity(intent);
                });
            }

            public void setSport(Sport sport) {
                if(sport != null) {
                    sportTextView.setText(sport.name);
                    sportName = sport.name;
                } else {
                    sportTextView.setText(R.string.loading);
                }
            }
        }
    }
}
