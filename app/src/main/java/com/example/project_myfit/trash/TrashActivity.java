package com.example.project_myfit.trash;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.project_myfit.databinding.ActivityTrashBinding;

public class TrashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityTrashBinding binding = ActivityTrashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.trashToolBar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayShowTitleEnabled(false);
    }
}