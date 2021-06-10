package com.example.myfit.di;

import android.content.Context;

import androidx.appcompat.app.AlertDialog;
import androidx.navigation.NavController;

import com.example.myfit.R;
import com.example.myfit.ui.dialog.DialogBuilder;
import com.example.myfit.ui.dialog.tree.TreeNodeProvider;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.FragmentComponent;
import dagger.hilt.android.qualifiers.ActivityContext;

@Module
@InstallIn(FragmentComponent.class)
public class DialogModule {

    @Provides
    public static DialogBuilder provideDialogBuilder(AlertDialog dialog){
        return new DialogBuilder(dialog);
    }

    @Provides
    public static AlertDialog provideAlertDialog(@ActivityContext Context context) {
        return new MaterialAlertDialogBuilder(context, R.style.myAlertDialogStyle)
                .setNegativeButton(R.string.dialog_cancel, null)
                .setPositiveButton(R.string.dialog_confirm, null)
                .create();
    }

    @Provides
    public static TreeNodeProvider provideTreeNodeProvider(@ActivityContext Context context, NavController navController) {
        return new TreeNodeProvider(context, navController);
    }
}
