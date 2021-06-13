package com.example.myfit.di.fragment;

import androidx.recyclerview.widget.ItemTouchHelper;

import com.example.myfit.data.tuple.tuple.FolderTuple;
import com.example.myfit.data.tuple.tuple.SizeTuple;
import com.example.myfit.di.Qualifiers;
import com.example.myfit.ui.main.list.adapter.folderadapter.FolderAdapter;
import com.example.myfit.ui.main.list.adapter.folderadapter.FolderDragCallBack;
import com.example.myfit.ui.main.list.adapter.sizeadapter.SizeAdapterGrid;
import com.example.myfit.ui.main.list.adapter.sizeadapter.SizeAdapterList;
import com.example.myfit.util.SizeLiveSet;
import com.example.myfit.util.adapter.dragcallback.DragCallBackGrid;
import com.example.myfit.util.adapter.dragcallback.DragCallBackList;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ViewModelComponent;
import dagger.hilt.android.scopes.ViewModelScoped;

@Module
@InstallIn(ViewModelComponent.class)
public class ListViewModelModule {
    @Qualifiers.FolderItemTouchHelper
    @Provides
    public static ItemTouchHelper provideFolderItemTouchHelper(FolderAdapter folderAdapter) {
        return new ItemTouchHelper(new FolderDragCallBack(folderAdapter));
    }

    @Qualifiers.SizeItemTouchHelperList
    @Provides
    public static ItemTouchHelper provideSizeItemTouchHelperList(SizeAdapterList sizeAdapterList) {
        return new ItemTouchHelper(new DragCallBackList(sizeAdapterList));
    }

    @Qualifiers.SizeItemTouchHelperGrid
    @Provides
    public static ItemTouchHelper provideSizeItemTouchHelperGrid(SizeAdapterGrid sizeAdapterGrid) {
        return new ItemTouchHelper(new DragCallBackGrid(sizeAdapterGrid));
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
