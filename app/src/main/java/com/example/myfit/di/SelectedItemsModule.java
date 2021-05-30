package com.example.myfit.di;

import com.example.myfit.data.tuple.tuple.CategoryTuple;
import com.example.myfit.data.tuple.tuple.FolderTuple;
import com.example.myfit.data.tuple.tuple.SizeTuple;
import com.example.myfit.util.SizeLiveSet;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ViewModelComponent;
import dagger.hilt.android.scopes.ViewModelScoped;

@Module
@InstallIn(ViewModelComponent.class)
public class SelectedItemsModule {

    @ViewModelScoped
    @Provides
    public static SizeLiveSet<CategoryTuple> provideSelectedCategoryTuples() {
        return new SizeLiveSet<>();
    }

    @ViewModelScoped
    @Provides
    public static SizeLiveSet<FolderTuple> provideSelectedFolderTuples() {
        return new SizeLiveSet<>();
    }

    @ViewModelScoped
    @Provides
    public static SizeLiveSet<SizeTuple> provideSelectedSizeTuples() {
        return new SizeLiveSet<>();
    }

}
