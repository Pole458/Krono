package com.pole.krono.view;

import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import com.pole.krono.R;
import com.pole.krono.model.MainViewModel;
import com.pole.krono.model.Sport;

public class AddProfileActivity extends AppCompatActivity {

    private Spinner sportSpinner;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_profile);

        Log.d("AddProfileActivity", "onCreate");

        sportSpinner = findViewById(R.id.sportSpinner);

        MainViewModel viewModel = ViewModelProviders.of(this).get(MainViewModel.class);

        viewModel.getSports().observe(this, sports -> {
            if(sports != null) {
                ArrayAdapter<Sport> playerAdapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, sports);
                sportSpinner.setAdapter(playerAdapter);
            }
        });

        findViewById(R.id.add_profile_button).setOnClickListener(v -> {

            String name = ((EditText)findViewById(R.id.nameEditText)).getText().toString();
            String surname = ((EditText)findViewById(R.id.surnameEditText)).getText().toString();
            String sport = ((Sport)(sportSpinner).getSelectedItem()).name;

            if(!name.equals("") && !surname.equals("") && !sport.equals("")) {
                Intent intent = new Intent();

                intent.putExtra("profile_name", name);
                intent.putExtra("profile_surname", surname);
                intent.putExtra("profile_sport", sport);

                if(getIntent().hasExtra("first_profile")) {
                    intent.setClass(this, MainActivity.class);
                    startActivity(intent);
                } else {
                    setResult(Activity.RESULT_OK, intent);
                }
                finish();
            }
        });
    }
}
