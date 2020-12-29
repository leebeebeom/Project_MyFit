package com.example.project_myfit;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.project_myfit.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private NavController mNavController;
    private AppBarConfiguration mAppBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Binding
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //ToolBar
        setSupportActionBar(binding.toolbar);

        //Top Level Destination
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.mainFragment, R.id.favoriteFragment, R.id.settingFragment).build();

        //NavController
        mNavController = Navigation.findNavController(this, R.id.host_fragment);

        //Action Bar Share
        NavigationUI.setupActionBarWithNavController(this, mNavController, mAppBarConfiguration);

        //Connect Bottom Navigation with NavController
        NavigationUI.setupWithNavController(binding.bottomNav, mNavController);

        //Bottom Nav Setting
        binding.bottomNav.setBackgroundTintList(null);
        binding.bottomNav.getMenu().getItem(2).setEnabled(false);
    }

    //Up Button
    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(mNavController, mAppBarConfiguration) ||
                super.onSupportNavigateUp();
    }

}