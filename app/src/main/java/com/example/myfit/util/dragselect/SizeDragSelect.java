package com.example.myfit.util.dragselect;

import android.annotation.SuppressLint;
import android.view.MotionEvent;

import androidx.recyclerview.widget.RecyclerView;

import com.example.myfit.data.repository.SizeRepository;
import com.example.myfit.ui.main.list.adapter.folderadapter.FolderAdapter;
import com.example.myfit.util.ListenerUtil;
import com.example.myfit.util.constant.ViewType;
import com.michaelflisar.dragselectrecyclerview.DragSelectTouchListener;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

import dagger.hilt.android.scopes.FragmentScoped;

import static com.example.myfit.util.adapter.viewholder.BaseVH.sDragging;
import static com.example.myfit.util.dragselect.DragSelect.sDragSelecting;

@FragmentScoped
public class SizeDragSelect extends ListDragSelect implements DragSelectTouchListener.OnAdvancedDragSelectListener {
    private final FolderDragSelect mFolderDragSelect;

    @Inject
    public SizeDragSelect(FolderAdapter folderAdapter,
                          SizeRepository sizeRepository,
                          FolderDragSelect folderDragSelect) {
        super(folderAdapter, sizeRepository);
        this.mFolderDragSelect = folderDragSelect;
        withSelectListener(this);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void setOnRecyclerViewTouchListener(RecyclerView folderRv, RecyclerView sizeRv) {
        sizeRv.setOnTouchListener((v, event) -> {
            if (!sFolderStart && (sSizeDragSelecting || sDragging))
                ListenerUtil.autoScroll(mScrollView, event);
            //drag up, down
            if (sSizeDragSelecting && sSizeStart && event.getY() < 0 && event.getAction() != MotionEvent.ACTION_UP) {
                dispatchEventToRvFolder(event, folderRv);
                if (!sFolderDragSelecting)
                    sizeDragUp(sizeRv);
            } else if (sSizeDragSelecting && event.getY() > 0
                    && sFolderDragSelecting && event.getAction() != MotionEvent.ACTION_UP && sSizeStart)
                sizeDragDown(sizeRv);
            if (event.getAction() == MotionEvent.ACTION_UP && sSizeStart)
                dragSelectFinished(event, folderRv);
            return false;
        });
    }

    @Override
    public void onSelectionStarted(int i) {
        sDragSelecting = true;
        sSizeDragSelecting = true;
        mScrollView.setScrollable(false);
        if (sSizeStartPosition == -1) sizeHolderCallOnClick(i);
        sSizeStartPosition = i;
    }

    @Override
    public void onSelectionFinished(int i) {
        sDragSelecting = false;
        sSizeStart = false;
        sSizeDragSelecting = false;
        mScrollView.setScrollable(true);
        sSizeStartPosition = -1;
    }

    @Override
    public void onSelectChange(int i, int i1, boolean b) {
        sSizeSelectedPosition1 = i;
        sSizeSelectedPosition2 = i1;
        sSizeSelectedState = b;
        for (int j = i; j <= i1; j++) sizeHolderCallOnClick(j);
    }

    private void dispatchEventToRvFolder(@NotNull MotionEvent event, RecyclerView folderRv) {
        MotionEvent newEvent = MotionEvent.obtain(event.getDownTime(), event.getEventTime(), event.getAction(), event.getX(),
                event.getY() + folderRv.getBottom(), event.getMetaState());
        newEvent.recycle();
        folderRv.dispatchTouchEvent(newEvent);
    }

    private void sizeDragUp(RecyclerView sizeRv) {
        RecyclerView.ViewHolder viewHolder = sizeRv.findViewHolderForLayoutPosition(0);
        RecyclerView.ViewHolder viewHolder2 = sizeRv.findViewHolderForLayoutPosition(1);
        if (mSizeRepository.getViewType() == ViewType.GRID_VIEW && viewHolder != null && viewHolder2 != null) {
            if (sSizeStartPosition == 0 && sSizeSelectedPosition2 >= 1
                    && (sSizeSelectedState || sSizeSelectedPosition2 >= 3))
                viewHolder2.itemView.callOnClick();
            else if (sSizeStartPosition == 1) {
                if (sSizeSelectedPosition1 != 0 && sSizeSelectedPosition2 != 2)
                    viewHolder.itemView.callOnClick();
                else if (sSizeSelectedPosition2 == 0 && !sSizeSelectedState) {
                    viewHolder.itemView.callOnClick();
                }
            } else if (sSizeStartPosition > 1) {
                if (sSizeSelectedPosition1 != 0) viewHolder.itemView.callOnClick();
                else if (!sSizeSelectedState) viewHolder.itemView.callOnClick();
            }
        }
        mFolderDragSelect.startDragSelection(mFolderAdapter.getItemCount() - 1);
    }

    private void sizeDragDown(RecyclerView sizeRv) {
        if (mSizeRepository.getViewType() == ViewType.GRID_VIEW) {
            RecyclerView.ViewHolder viewHolder = sizeRv.findViewHolderForLayoutPosition(0);
            RecyclerView.ViewHolder viewHolder2 = sizeRv.findViewHolderForLayoutPosition(1);
            if (viewHolder != null && viewHolder2 != null)
                if (sSizeStartPosition == 0) {
                    if ((sSizeSelectedPosition2 == 1 && sSizeSelectedState)
                            || (sSizeSelectedPosition2 == 3 && !sSizeSelectedState))
                        viewHolder2.itemView.callOnClick();
                } else if (sSizeStartPosition == 1) {
                    if (sSizeSelectedPosition2 == -1
                            || (sSizeSelectedPosition2 == 0 && !sSizeSelectedState)
                            || (sSizeSelectedPosition2 == 3 && !sSizeSelectedState))
                        viewHolder.itemView.callOnClick();
                } else if (sSizeStartPosition > 1)
                    if (sSizeSelectedPosition1 != 0 || !sSizeSelectedState)
                        viewHolder.itemView.callOnClick();
        }

        if (sFolderSelectedPosition2 == -1)
            folderHolderCallOnClick(mFolderAdapter.getItemCount() - 1);
        else if (sFolderSelectedState)
            for (int i = sFolderSelectedPosition1; i < mFolderAdapter.getItemCount(); i++)
                folderHolderCallOnClick(i);
        else
            for (int i = sFolderSelectedPosition2 + 1; i < mFolderAdapter.getItemCount(); i++)
                folderHolderCallOnClick(i);

        sFolderDragSelecting = false;
        sFolderSelectedState = false;
        sFolderStartPosition = -1;
        sFolderSelectedPosition1 = -1;
        sFolderSelectedPosition2 = -1;
    }
}
