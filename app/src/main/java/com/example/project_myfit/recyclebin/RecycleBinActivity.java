package com.example.project_myfit.recyclebin;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.project_myfit.R;
import com.example.project_myfit.databinding.ActivityRecycleBinBinding;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;

public class RecycleBinActivity extends AppCompatActivity {

    private NavController mNavController;
    private AppBarConfiguration mAppBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityRecycleBinBinding binding = ActivityRecycleBinBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolBarRecycleBin);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayShowTitleEnabled(false);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.host_fragment_recycle_bin);
        if (navHostFragment != null) mNavController = navHostFragment.getNavController();

        mAppBarConfiguration = new AppBarConfiguration.Builder(R.id.recycleBinMain).build();

        setDestinationChangeListener();
    }

    private void setDestinationChangeListener() {
        MaterialTextView materialTextView = findViewById(R.id.tv_recycle_bin_title);
        TextInputLayout autoCompleteLayout = findViewById(R.id.ac_tv_recycle_bin_search_layout);

        mNavController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (destination.getId() == R.id.recycleBinMain) {
                materialTextView.setVisibility(View.VISIBLE);
                autoCompleteLayout.setVisibility(View.GONE);
            } else if (destination.getId() == R.id.recycleBinSearch) {
                materialTextView.setVisibility(View.GONE);
                autoCompleteLayout.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(mNavController, mAppBarConfiguration) || super.onSupportNavigateUp();
    }
}