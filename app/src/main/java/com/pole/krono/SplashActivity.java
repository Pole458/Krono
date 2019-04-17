package com.pole.krono;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import com.pole.krono.model.SelectedProfileViewModel;
import com.pole.krono.view.AddProfileActivity;
import com.pole.krono.view.MainActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ViewModelProviders.of(this).get(SelectedProfileViewModel.class).getSelectedProfile().observe(this, profile -> {
            Intent intent;
            if (profile != null) {
                intent = new Intent(this, MainActivity.class);
            } else {
                intent = new Intent(this, AddProfileActivity.class);
                intent.putExtra("first_profile", true);
            }
            startActivity(intent);
            finish();
        });
    }
}
