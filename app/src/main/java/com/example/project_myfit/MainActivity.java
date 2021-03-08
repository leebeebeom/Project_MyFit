package com.example.project_myfit;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.project_myfit.databinding.ActivityMainBinding;
import com.example.project_myfit.ui.main.database.Category;
import com.example.project_myfit.ui.main.listfragment.database.Folder;
import com.example.project_myfit.ui.main.listfragment.database.Size;

import org.jetbrains.annotations.NotNull;

import static com.example.project_myfit.MyFitConstant.FOLDER_ID;
import static com.example.project_myfit.MyFitConstant.SIZE_ID;

public class MainActivity extends AppCompatActivity {

    private NavController mNavController;
    private AppBarConfiguration mAppBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //binding
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //view model
        MainActivityViewModel model = new ViewModelProvider(this).get(MainActivityViewModel.class);

        //toolbar
        setSupportActionBar(binding.toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.setDisplayShowTitleEnabled(false);

        //navigation controller
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.host_fragment);
        if (navHostFragment != null) mNavController = navHostFragment.getNavController();

        //top level destination
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.mainFragment, R.id.favoriteFragment, R.id.settingFragment)
                .build();

        //action bar share
        NavigationUI.setupActionBarWithNavController(this, mNavController, mAppBarConfiguration);

        //connect bottom navigation with navController
        NavigationUI.setupWithNavController(binding.bottomNav, mNavController);

        //bottom bav setting
        binding.bottomNav.setBackgroundTintList(null);
        binding.bottomNav.getMenu().getItem(2).setEnabled(false);

        destinationChangeListener(binding, actionBar);

        if (getIntent() != null) {
            Intent intent = getIntent();
            if (intent.getIntExtra(SIZE_ID, 0) != 0) {
                searchSizeClick(model, intent);
            } else if (intent.getLongExtra(FOLDER_ID, 0) != 0) {
                searchFolderClick(model, intent);
            }
        }
    }

    private void destinationChangeListener(ActivityMainBinding binding, ActionBar actionBar) {
        mNavController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (destination.getId() == R.id.mainFragment) {
                binding.activityFab.hide();
                binding.activityFab.setImageResource(R.drawable.icon_search);
                binding.activityFab.show();
                binding.toolbarMainTitle.setVisibility(View.VISIBLE);
                if (actionBar != null) actionBar.setDisplayShowTitleEnabled(false);
            } else if (destination.getId() == R.id.listFragment) {
                binding.activityFab.hide();
                binding.activityFab.setImageResource(R.drawable.icon_add);
                binding.activityFab.show();
                binding.toolbarMainTitle.setVisibility(View.GONE);
                if (actionBar != null) actionBar.setDisplayShowTitleEnabled(true);
            } else if (destination.getId() == R.id.inputOutputFragment) {
                binding.activityFab.hide();
                binding.activityFab.setImageResource(R.drawable.icon_save);
                binding.activityFab.show();
                binding.toolbarMainTitle.setVisibility(View.GONE);
                if (actionBar != null) actionBar.setDisplayShowTitleEnabled(false);
            }
        });
    }

    private void searchSizeClick(@NotNull MainActivityViewModel model, @NotNull Intent intent) {
        Size size = model.getRepository().getSize(intent.getIntExtra(SIZE_ID, 0));
        model.setSize(size);

        Category category = model.getRepository().getCategory(size.getFolderId());
        Folder folder = model.getRepository().getFolder(size.getFolderId());
        if (category != null) model.setCategory(category);
        else {
            category = getCategory(folder, model);
            model.setCategory(category);
            model.setFolder(folder);
        }
        mNavController.navigate(R.id.action_mainFragment_to_inputOutputFragment);
    }

    private void searchFolderClick(@NotNull MainActivityViewModel model, @NotNull Intent intent) {
        Folder folder = model.getRepository().getFolder(intent.getLongExtra(FOLDER_ID, 0));
        Category category = getCategory(folder, model);
        model.setFolder(folder);
        model.setCategory(category);
        mNavController.navigate(R.id.action_mainFragment_to_listFragment);
    }

    @NotNull
    private Category getCategory(@NotNull Folder folder, @NotNull MainActivityViewModel model) {
        Category category = model.getRepository().getCategory(folder.getFolderId());
        if (category == null) {
            Folder newFolder = model.getRepository().getFolder(folder.getFolderId());
            category = getCategory(newFolder, model);
        }
        return category;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        setSupportActionBar(null);
    }

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(mNavController, mAppBarConfiguration) || super.onSupportNavigateUp();
    }
}