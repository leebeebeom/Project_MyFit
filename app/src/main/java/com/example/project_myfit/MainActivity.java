package com.example.project_myfit;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.example.project_myfit.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Binding
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //ToolBar
        setSupportActionBar(binding.toolbar);

        //NavController
        NavController navController = Navigation.findNavController(this, R.id.host_fragment);

        //Top Level Destination
//        AppBarConfiguration mAppBarConfiguration = new AppBarConfiguration.Builder(
//                R.id.mainFragment, R.id.favoriteFragment, R.id.settingFragment)
//                .build();
//
//        //Action Bar Share
//        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);

        //Connect Bottom Navigation with NavController
        NavigationUI.setupWithNavController(binding.bottomNav, navController);

        //Bottom Nav Setting
        binding.bottomNav.setBackgroundTintList(null);
        binding.bottomNav.getMenu().getItem(2).setEnabled(false);

        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (destination.getId() == R.id.inputOutputFragment) {
                binding.activityFab.hide();
                binding.activityFab.setImageResource(R.drawable.icon_check);
                binding.activityFab.show();
            } else {
                binding.activityFab.hide();
                binding.activityFab.setImageResource(R.drawable.icon_add);
                binding.activityFab.show();
            }
        });
    }
}