package com.example.project_myfit.recycler_bin_activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.project_myfit.databinding.ActivityRecycleBinBinding;

public class RecycleBinActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityRecycleBinBinding binding = ActivityRecycleBinBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.trashToolBar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayShowTitleEnabled(false);
    }
}