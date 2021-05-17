package com.example.myfit.di;

import android.content.Context;

import com.example.myfit.data.repository.CategoryRepository;
import com.example.myfit.data.repository.FolderRepository;
import com.example.myfit.data.repository.RecentSearchRepository;
import com.example.myfit.data.repository.SizeRepository;

import org.jetbrains.annotations.NotNull;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ViewModelComponent;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.android.scopes.ViewModelScoped;

@Module
@InstallIn(ViewModelComponent.class)
public class DataModule {
    @ViewModelScoped
    @NotNull
    @Provides
    public static CategoryRepository provideCategoryRepository(@ApplicationContext Context context) {
        return new CategoryRepository(context);
    }

    @ViewModelScoped
    @NotNull
    @Provides
    public static FolderRepository provideFolderRepository(@ApplicationContext Context context) {
        return new FolderRepository(context);
    }

    @ViewModelScoped
    @NotNull
    @Provides
    public static SizeRepository provideSizeRepository(@ApplicationContext Context context) {
        return new SizeRepository(context);
    }

    @ViewModelScoped
    @NotNull
    @Provides
    public static RecentSearchRepository provideRecentSearchRepository(@ApplicationContext Context context) {
        return new RecentSearchRepository(context);
    }
}
