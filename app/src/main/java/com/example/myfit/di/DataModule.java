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
import dagger.hilt.android.components.ActivityRetainedComponent;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.android.scopes.ActivityRetainedScoped;

@Module
@InstallIn(ActivityRetainedComponent.class)
public class DataModule {
    @ActivityRetainedScoped
    @NotNull
    @Provides
    public static CategoryRepository provideCategoryRepository(@ApplicationContext Context context) {
        return new CategoryRepository(context);
    }

    @ActivityRetainedScoped
    @NotNull
    @Provides
    public static FolderRepository provideFolderRepository(@ApplicationContext Context context) {
        return new FolderRepository(context);
    }

    @ActivityRetainedScoped
    @NotNull
    @Provides
    public static SizeRepository provideSizeRepository(@ApplicationContext Context context) {
        return new SizeRepository(context);
    }

    @ActivityRetainedScoped
    @NotNull
    @Provides
    public static RecentSearchRepository provideRecentSearchRepository(@ApplicationContext Context context) {
        return new RecentSearchRepository(context);
    }
}
