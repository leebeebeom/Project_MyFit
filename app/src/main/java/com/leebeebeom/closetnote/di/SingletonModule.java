package com.leebeebeom.closetnote.di;

import com.leebeebeom.closetnote.data.tuple.tuple.CategoryTuple;
import com.leebeebeom.closetnote.data.tuple.tuple.FolderTuple;
import com.leebeebeom.closetnote.data.tuple.tuple.SizeTuple;
import com.leebeebeom.closetnote.util.SizeLiveSet;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public class SingletonModule {
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
