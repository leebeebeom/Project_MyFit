package com.leebeebeom.closetnote.ui.view.recyclerview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.leebeebeom.closetnote.ui.main.list.adapter.folderadapter.FolderAdapter;
import com.leebeebeom.closetnote.util.dragselect.FolderDragSelect;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import dagger.hilt.android.WithFragmentBindings;

@AndroidEntryPoint
@WithFragmentBindings
public class FolderRecyclerViewGrid extends RecyclerView {
    @Inject
    FolderAdapter mFolderAdapter;
    @Inject
    FolderDragSelect mFolderDragSelect;

    public FolderRecyclerViewGrid(@NonNull @NotNull Context context) {
        super(context);
        init();
    }

    public FolderRecyclerViewGrid(@NonNull @NotNull Context context, @Nullable @org.jetbrains.annotations.Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FolderRecyclerViewGrid(@NonNull @NotNull Context context, @Nullable @org.jetbrains.annotations.Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void init() {
        setAdapter(mFolderAdapter);
        LayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 4, VERTICAL, false);
        setLayoutManager(gridLayoutManager);
        setOnTouchListener((v, event) -> {
            mFolderDragSelect.onRecyclerViewTouch(v, event);
            return false;
        });
    }
}
