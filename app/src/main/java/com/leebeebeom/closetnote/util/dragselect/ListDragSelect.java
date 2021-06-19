package com.leebeebeom.closetnote.util.dragselect;

import android.view.MotionEvent;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.leebeebeom.closetnote.data.repository.SizeRepository;
import com.leebeebeom.closetnote.ui.main.list.adapter.folderadapter.FolderAdapter;
import com.leebeebeom.closetnote.ui.view.LockableScrollView;
import com.michaelflisar.dragselectrecyclerview.DragSelectTouchListener;

import org.jetbrains.annotations.NotNull;

import lombok.experimental.Accessors;

@Accessors(prefix = "m")
public abstract class ListDragSelect extends DragSelectTouchListener {
    public static boolean sFolderDragSelecting, sFolderSelectedState, sFolderStart, sScrollEnable;
    public static int sFolderStartPosition = -1;
    public static int sFolderSelectedPosition1 = -1;
    public static int sFolderSelectedPosition2 = -1;

    public static boolean sSizeDragSelecting, sSizeStart, sSizeSelectedState;
    public static int sSizeStartPosition = -1;
    public static int sSizeSelectedPosition1 = -1;
    public static int sSizeSelectedPosition2 = -1;

    protected final FolderAdapter mFolderAdapter;
    protected final SizeRepository mSizeRepository;
    protected final ListDragSelectListener mListener;

    public ListDragSelect(FolderAdapter folderAdapter, SizeRepository sizeRepository, ListDragSelectListener listener) {
        this.mFolderAdapter = folderAdapter;
        this.mSizeRepository = sizeRepository;
        this.mListener = listener;
    }

    public abstract boolean onRecyclerViewTouch(View v, MotionEvent event);

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
        RecyclerView.ViewHolder viewHolder = mListener.getFolderRecyclerView().findViewHolderForLayoutPosition(i);
        if (viewHolder != null) viewHolder.itemView.callOnClick();
    }

    protected void sizeHolderCallOnClick(int i) {
        RecyclerView.ViewHolder viewHolder = mListener.getSizeRecyclerView().findViewHolderForLayoutPosition(i);
        if (viewHolder != null) viewHolder.itemView.callOnClick();
    }

    protected void autoScroll(@NotNull MotionEvent event){
        if (event.getRawY() > 2000) {
            mListener.getScrollView().scrollBy(0, 1);
            sScrollEnable = true;
        } else if (event.getRawY() < 250) {
            mListener.getScrollView().scrollBy(0, -1);
            sScrollEnable = true;
        } else if (event.getRawY() < 2000 && event.getRawY() > 250)
            sScrollEnable = false;
    }

    public interface ListDragSelectListener {
        FolderDragSelect getFolderDragSelect();

        SizeDragSelect getSizeDragSelect();

        LockableScrollView getScrollView();

        RecyclerView getFolderRecyclerView();

        RecyclerView getSizeRecyclerView();
    }
}
