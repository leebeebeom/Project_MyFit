package com.example.myfit.di.fragment;

import android.content.Context;

import androidx.recyclerview.widget.ItemTouchHelper;

import com.example.myfit.R;
import com.example.myfit.di.Qualifiers;
import com.example.myfit.ui.main.list.adapter.folderadapter.FolderAdapter;
import com.example.myfit.ui.main.list.adapter.folderadapter.FolderDragCallBack;
import com.example.myfit.ui.main.list.adapter.sizeadapter.SizeAdapterGrid;
import com.example.myfit.ui.main.list.adapter.sizeadapter.SizeAdapterList;
import com.example.myfit.util.adapter.dragcallback.DragCallBackList;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.FragmentComponent;
import dagger.hilt.android.qualifiers.ActivityContext;
import dagger.hilt.android.scopes.FragmentScoped;

@Module
@InstallIn(FragmentComponent.class)
public class ListFragmentModule {

    @FragmentScoped
    @Qualifiers.FolderItemTouchHelper
    @Provides
    public static ItemTouchHelper provideFolderItemTouchHelper(FolderAdapter folderAdapter) {
        return new ItemTouchHelper(new FolderDragCallBack(folderAdapter));
    }

    @FragmentScoped
    @Qualifiers.SizeItemTouchHelperList
    @Provides
    public static ItemTouchHelper provideSizeItemTouchHelperList(SizeAdapterList sizeAdapterList) {
        return new ItemTouchHelper(new DragCallBackList(sizeAdapterList));
    }

    @FragmentScoped
    @Qualifiers.SizeItemTouchHelperGrid
    @Provides
    public static ItemTouchHelper provideSizeItemTouchHelperGrid(SizeAdapterGrid sizeAdapterGrid) {
        return new ItemTouchHelper(new DragCallBackList(sizeAdapterGrid));
    }

    @Qualifiers.Padding8dp
    @FragmentScoped
    @Provides
    public static int providePadding8dp(@ActivityContext Context context) {
        return context.getResources().getDimensionPixelSize(R.dimen._8sdp);
    }

    @Qualifiers.Padding60dp
    @FragmentScoped
    @Provides
    public static int providePadding60dp(@ActivityContext Context context) {
        return context.getResources().getDimensionPixelSize(R.dimen._60sdp);
    }

    @Qualifiers.NavigationTextViewSize
    @FragmentScoped
    @Provides
    public static float provideNavigationTextViewSize(@ActivityContext Context context) {
        return context.getResources().getDimension(R.dimen._4sdp);
    }
}
