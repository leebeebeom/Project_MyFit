package com.example.myfit.di;

import com.example.myfit.data.model.category.CategoryTuple;
import com.example.myfit.data.model.folder.FolderTuple;
import com.example.myfit.data.model.size.SizeTuple;
import com.example.myfit.util.SizeLiveSet;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public class SelectedItemsModule {

    @Singleton
    @Provides
    public static SizeLiveSet<CategoryTuple> provideSelectedCategoryTuples() {
        return new SizeLiveSet<>();
    }

    @Singleton
    @Provides
    public static SizeLiveSet<FolderTuple> provideSelectedFolderTuples() {
        return new SizeLiveSet<>();
    }

    @Singleton
    @Provides
    public static SizeLiveSet<SizeTuple> provideSelectedSizeTuples() {
        return new SizeLiveSet<>();
    }

}
