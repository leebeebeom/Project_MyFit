package com.example.myfit.di;

import com.example.myfit.data.tuple.tuple.CategoryTuple;
import com.example.myfit.data.tuple.tuple.FolderTuple;
import com.example.myfit.data.tuple.tuple.SizeTuple;
import com.example.myfit.ui.main.list.adapter.folderadapter.FolderAdapter;
import com.example.myfit.ui.main.list.adapter.sizeadapter.SizeAdapterGrid;
import com.example.myfit.ui.main.list.adapter.sizeadapter.SizeAdapterList;
import com.example.myfit.ui.main.main.adapter.CategoryAdapter;
import com.example.myfit.util.SizeLiveSet;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ViewModelComponent;
import dagger.hilt.android.scopes.ViewModelScoped;

@Module
@InstallIn(ViewModelComponent.class)
public class AdapterModule {
    @ViewModelScoped
    @Provides
    public static CategoryAdapter provideCategoryAdapter(SizeLiveSet<CategoryTuple> selectedCategories) {
        return new CategoryAdapter(selectedCategories);
    }

    @ViewModelScoped
    @Provides
    public static FolderAdapter provideFolderAdapter(SizeLiveSet<FolderTuple> selectedFolders) {
        return new FolderAdapter(selectedFolders);
    }

    @ViewModelScoped
    @Provides
    public static SizeAdapterList provideSizeAdapterList(SizeLiveSet<SizeTuple> selectedSizes) {
        return new SizeAdapterList(selectedSizes);
    }

    @ViewModelScoped
    @Provides
    public static SizeAdapterGrid provideSizeAdapterGrid(SizeLiveSet<SizeTuple> selectedSizes) {
        return new SizeAdapterGrid(selectedSizes);
    }

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
