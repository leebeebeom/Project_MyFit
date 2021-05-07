package com.example.project_myfit.recyclebin.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.project_myfit.R;
import com.example.project_myfit.databinding.FragmentRecycleBinMainBinding;
import com.example.project_myfit.util.CommonUtil;

import org.jetbrains.annotations.NotNull;

public class RecycleBinMainFragment extends Fragment {

    private NavController mNavController;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mNavController = NavHostFragment.findNavController(this);
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        FragmentRecycleBinMainBinding binding = FragmentRecycleBinMainBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull @NotNull Menu menu, @NonNull @NotNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_recycle_bin_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull @NotNull MenuItem item) {
        if (item.getItemId() == R.id.menu_recycle_bin_search) {
            CommonUtil.navigate(mNavController, R.id.recycleBinMain, RecycleBinMainFragmentDirections.toRecycleBinSearchFragment());
            return true;
        }
        return false;
    }
}