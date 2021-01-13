package com.example.project_myfit;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.project_myfit.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private NavController mNavController;
    private AppBarConfiguration mAppBarConfiguration;
    private MainActivityViewModel mModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Binding
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mModel = new ViewModelProvider(this).get(MainActivityViewModel.class);

        //ToolBar
        setSupportActionBar(binding.toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.setDisplayShowTitleEnabled(false);

        //NavController
        mNavController = Navigation.findNavController(this, R.id.host_fragment);

        //Top Level Destination
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.mainFragment, R.id.favoriteFragment, R.id.settingFragment)
                .build();

        //Action Bar Share
        NavigationUI.setupActionBarWithNavController(this, mNavController, mAppBarConfiguration);

        //Connect Bottom Navigation with NavController
        NavigationUI.setupWithNavController(binding.bottomNav, mNavController);

        //Bottom Nav Setting
        binding.bottomNav.setBackgroundTintList(null);
        binding.bottomNav.getMenu().getItem(2).setEnabled(false);

        mNavController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (destination.getId() == R.id.inputOutputFragment) {
                binding.activityFab.hide();
                binding.activityFab.setImageResource(R.drawable.icon_check);
                binding.activityFab.show();
                binding.toolbarTitle.setVisibility(View.GONE);
                if (actionBar != null) actionBar.setDisplayShowTitleEnabled(false);
            } else if (destination.getId() == R.id.listFragment) {
                binding.activityFab.hide();
                binding.activityFab.setImageResource(R.drawable.icon_add);
                binding.activityFab.show();
                binding.toolbarTitle.setVisibility(View.GONE);
                if (actionBar != null) actionBar.setDisplayShowTitleEnabled(true);
            } else if (destination.getId() == R.id.mainFragment) {
                binding.activityFab.hide();
                binding.activityFab.setImageResource(R.drawable.icon_add);
                binding.activityFab.show();
                binding.toolbarTitle.setVisibility(View.VISIBLE);
                if (actionBar != null) actionBar.setDisplayShowTitleEnabled(false);
                if (mModel.getFolderList().size() != 0)
                    mModel.getFolderList().clear();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(mNavController, mAppBarConfiguration) || super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        if (mModel.getFolderList().size() != 0)
            mModel.getFolderList().remove(mModel.getFolderList().size() - 1);
        super.onBackPressed();
    }
}