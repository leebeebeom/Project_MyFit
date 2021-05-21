package com.example.myfit.di;

import android.content.Context;
import android.view.LayoutInflater;

import com.example.myfit.ui.dialog.DialogBindingBuilder;
import com.example.myfit.ui.dialog.DialogBuilder;
import com.example.myfit.ui.dialog.tree.TreeNodeProvider;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.FragmentComponent;
import dagger.hilt.android.qualifiers.ActivityContext;
import dagger.hilt.android.qualifiers.ApplicationContext;

@Module
@InstallIn(FragmentComponent.class)
public class DialogModule {
    @NotNull
    @Provides
    public static DialogBuilder provideDialogBuilder(@ApplicationContext Context context) {
        return new DialogBuilder(context);
    }

    @NotNull
    @Contract("_ -> new")
    @Provides
    public static DialogBindingBuilder provideDialogBinding(LayoutInflater inflater) {
        return new DialogBindingBuilder(inflater);
    }

    @Provides
    public static LayoutInflater provideLayoutInflater(@ActivityContext Context context) {
        return LayoutInflater.from(context);
    }

    @Provides
    public static TreeNodeProvider provideTreeNodeProvider(@ActivityContext Context context) {
        return new TreeNodeProvider(context);
    }
}
