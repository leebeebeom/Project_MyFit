package com.example.myfit.util.dragselect;

import android.view.MotionEvent;

import androidx.recyclerview.widget.RecyclerView;

import com.example.myfit.data.repository.SizeRepository;
import com.example.myfit.ui.main.list.adapter.folderadapter.FolderAdapter;
import com.example.myfit.util.LockableScrollView;
import com.michaelflisar.dragselectrecyclerview.DragSelectTouchListener;

import org.jetbrains.annotations.NotNull;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Accessors(prefix = "m")
public abstract class ListDragSelect extends DragSelectTouchListener {
    public static boolean sFolderDragSelecting, sFolderSelectedState, sFolderStart;
    public static int sFolderStartPosition = -1;
    public static int sFolderSelectedPosition1 = -1;
    public static int sFolderSelectedPosition2 = -1;

    public static boolean sSizeDragSelecting, sSizeStart, sSizeSelectedState;
    public static int sSizeStartPosition = -1;
    public static int sSizeSelectedPosition1 = -1;
    public static int sSizeSelectedPosition2 = -1;

    protected final FolderAdapter mFolderAdapter;
    protected final SizeRepository mSizeRepository;
    @Setter
    protected LockableScrollView mScrollView;
    private RecyclerView mFolderRv, mSizeRv;

    public ListDragSelect(FolderAdapter folderAdapter,
                          SizeRepository sizeRepository) {
        this.mFolderAdapter = folderAdapter;
        this.mSizeRepository = sizeRepository;
    }

    public void setRecyclerViews(RecyclerView folderRv, RecyclerView sizeRv) {
        setOnRecyclerViewTouchListener(folderRv, sizeRv);
    }

    protected abstract void setOnRecyclerViewTouchListener(RecyclerView folderRv, RecyclerView sizeRv);

    protected void dragSelectFinished(MotionEvent event, @NotNull RecyclerView recyclerView) {
        recyclerView.dispatchTouchEvent(event);
        sFolderStart = false;
        sFolderSelectedState = false;
        sSizeSelectedState = false;
        sFolderSelectedPosition1 = -1;
        sFolderSelectedPosition2 = -1;
        sSizeSelectedPosition1 = -1;
        sSizeSelectedPosition2 = -1;
        sFolderStartPosition = -1;
        sSizeStartPosition = -1;
    }

    protected void folderHolderCallOnClick(int i) {
        RecyclerView.ViewHolder viewHolder = mFolderRv.findViewHolderForLayoutPosition(i);
        if (viewHolder != null) viewHolder.itemView.callOnClick();
    }

    protected void sizeHolderCallOnClick(int i) {
        RecyclerView.ViewHolder viewHolder = mSizeRv.findViewHolderForLayoutPosition(i);
        if (viewHolder != null) viewHolder.itemView.callOnClick();
    }
}
