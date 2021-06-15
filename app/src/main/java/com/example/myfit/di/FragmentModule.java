package com.example.myfit.di;

import android.content.Context;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.myfit.R;
import com.example.myfit.util.adapter.AutoCompleteAdapter;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.FragmentComponent;
import dagger.hilt.android.qualifiers.ActivityContext;

@Module
@InstallIn(FragmentComponent.class)
public class FragmentModule {
    @Provides
    public static NavController provideNavController(Fragment fragment) {
        return NavHostFragment.findNavController(fragment);
    }

    @Provides
    public static AlertDialog provideAlertDialog(@ActivityContext Context context) {
        return new MaterialAlertDialogBuilder(context, R.style.myAlertDialogStyle)
                .setNegativeButton(R.string.dialog_cancel, null)
                .setPositiveButton(R.string.dialog_confirm, null)
                .create();
    }
}
