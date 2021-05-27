package com.example.myfit.di;

import android.content.Context;

import androidx.appcompat.app.AlertDialog;
import androidx.navigation.NavController;

import com.example.myfit.R;
import com.example.myfit.ui.dialog.tree.TreeNodeProvider;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.FragmentComponent;
import dagger.hilt.android.qualifiers.ActivityContext;
import dagger.hilt.android.qualifiers.ApplicationContext;

@Module
@InstallIn(FragmentComponent.class)
public class DialogModule {

    @Provides
    public static AlertDialog provideAlertDialog(@ApplicationContext Context context) {
        return new MaterialAlertDialogBuilder(context, R.style.myAlertDialogStyle)
                .setNegativeButton(R.string.dialog_cancel, null)
                .setPositiveButton(R.string.dialog_confirm, null)
                .create();
    }

    @Provides
    public static TreeNodeProvider provideTreeNodeProvider(@ActivityContext Context context, NavController navController){
        return new TreeNodeProvider(context, navController);
    }
}
