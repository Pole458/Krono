package com.pole.krono.view;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.*;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.pole.krono.R;
import com.pole.krono.model.ActivityType;
import com.pole.krono.model.Sport;
import com.pole.krono.model.SportViewModel;

import java.util.List;

public class SportActivity extends AppCompatActivity {

    private SportViewModel viewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sport);

        Toolbar toolbar = findViewById(R.id.toolbar);

        Intent intent = getIntent();

        viewModel = ViewModelProviders.of(this).get(SportViewModel.class);

        if(intent.hasExtra("sport")) {
            String sportName = intent.getStringExtra("sport");
            viewModel.getSport().setValue(new Sport(sportName));
            toolbar.setTitle(sportName);
        } else {
            finish();
        }

        setSupportActionBar(toolbar);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mLayoutManager.scrollToPosition(0);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(true);
        final ActivityTypesAdapter adapter = new ActivityTypesAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // update UI
        viewModel.getActivityTypes().observe(this, adapter::setItems);

        findViewById(R.id.addButton).setOnClickListener(v -> {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Insert Activity Type");

            // Set up the input
            final EditText input = new EditText(this);
            // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setView(input);

            // Set up the buttons
            builder.setPositiveButton("OK", (dialog, which) -> viewModel.insertActivityType(input.getText().toString()));
            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

            builder.show();

        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE, 1, Menu.NONE, "Delete Sport");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == 1) {
            new AlertDialog.Builder(this)
                    .setTitle("Delete sport")
                    .setMessage("Are you sure you want to delete this sport?\nYou will also lose all the tracking sessions related to this sport")

                    // Specifying a listener allows you to take an action before dismissing the dialog.
                    // The dialog is automatically dismissed when a dialog button is clicked.
                    .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                        viewModel.deleteSport();
                        finish();
                        Toast.makeText(this, "Sport deleted", Toast.LENGTH_LONG).show();
                    })

                    // A null listener allows the button to dismiss the dialog and take no further action.
                    .setNegativeButton(android.R.string.no, null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
            return true;
        }
        return false;
    }

    private class ActivityTypesAdapter extends RecyclerView.Adapter<ActivityTypesAdapter.ViewHolder> {

        private LayoutInflater layoutInflater;
        private List<ActivityType> activityTypes;

        ActivityTypesAdapter(Context context) {
            layoutInflater = LayoutInflater.from(context);
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            // create a new view
            // set the view's size, margins, paddings and layout parameters
            return new ViewHolder(layoutInflater.inflate(R.layout.recycler_item_activity_type, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
            holder.setTrackingSession(activityTypes.get(position));
        }

        void setItems(List<ActivityType> activityTypes) {
            this.activityTypes = activityTypes;
            notifyDataSetChanged();
        }

        @Override
        public int getItemCount() {
            if (activityTypes != null)
                return activityTypes.size();
            else return 0;
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            private TextView activityTypeTextView;
            private ActivityType activityType;

            ViewHolder(View itemView) {
                super(itemView);

                activityTypeTextView = itemView.findViewById(R.id.activityTypeTextView);

                itemView.findViewById(R.id.deleteButton).setOnClickListener(v -> new AlertDialog.Builder(SportActivity.this)
                        .setTitle("Delete Activity Type")
                        .setMessage("Are you sure you want to delete this Activity Type?")
                        // Specifying a listener allows you to take an action before dismissing the dialog.
                        // The dialog is automatically dismissed when a dialog button is clicked.
                        .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                            viewModel.deleteActivityType(activityType);
                            finish();
                            Toast.makeText(SportActivity.this, "Activity Type deleted", Toast.LENGTH_LONG).show();
                        })
                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show());
            }

            void setTrackingSession(ActivityType activityType) {
                if (activityType != null) {
                    this.activityType = activityType;
                    activityTypeTextView.setText(activityType.name);
                }
            }
        }
    }
}
