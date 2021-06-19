package com.leebeebeom.closetnote.util.dragselect;

import android.view.MotionEvent;
import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.leebeebeom.closetnote.data.repository.SizeRepository;
import com.leebeebeom.closetnote.ui.main.list.adapter.folderadapter.FolderAdapter;
import com.leebeebeom.closetnote.util.ListenerUtil;
import com.leebeebeom.closetnote.util.constant.ViewType;
import com.michaelflisar.dragselectrecyclerview.DragSelectTouchListener;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

import dagger.hilt.android.scopes.FragmentScoped;
import lombok.experimental.Accessors;

import static com.leebeebeom.closetnote.util.adapter.viewholder.BaseVH.sDragging;
import static com.leebeebeom.closetnote.util.dragselect.DragSelect.sDragSelecting;

@Accessors(prefix = "m")
@FragmentScoped
public class SizeDragSelect extends ListDragSelect implements DragSelectTouchListener.OnAdvancedDragSelectListener {

    @Inject
    public SizeDragSelect(FolderAdapter folderAdapter, SizeRepository sizeRepository, Fragment fragment) {
        super(folderAdapter, sizeRepository, (ListDragSelectListener) fragment);
        withSelectListener(this);
    }

    @Override
    public boolean onRecyclerViewTouch(View v, MotionEvent event) {
        if (!sFolderStart && (sSizeDragSelecting || sDragging))
            super.autoScroll(event);
        //drag up, down
        if (sSizeDragSelecting && sSizeStart && event.getY() < 0 && event.getAction() != MotionEvent.ACTION_UP) {
            dispatchEventToRvFolder(event);
            if (!sFolderDragSelecting)
                sizeDragUp();
        } else if (sSizeDragSelecting && event.getY() > 0
                && sFolderDragSelecting && event.getAction() != MotionEvent.ACTION_UP && sSizeStart)
            sizeDragDown();
        if (event.getAction() == MotionEvent.ACTION_UP && sSizeStart)
            dragSelectFinished(event, mListener.getFolderRecyclerView());
        return false;
    }

    @Override
    public void onSelectionStarted(int i) {
        sDragSelecting = true;
        sSizeDragSelecting = true;
        mListener.getScrollView().setScrollable(false);
        if (sSizeStartPosition == -1) sizeHolderCallOnClick(i);
        sSizeStartPosition = i;
    }

    @Override
    public void onSelectionFinished(int i) {
        sDragSelecting = false;
        sSizeStart = false;
        sSizeDragSelecting = false;
        mListener.getScrollView().setScrollable(true);
        sSizeStartPosition = -1;
    }

    @Override
    public void onSelectChange(int i, int i1, boolean b) {
        sSizeSelectedPosition1 = i;
        sSizeSelectedPosition2 = i1;
        sSizeSelectedState = b;
        for (int j = i; j <= i1; j++) sizeHolderCallOnClick(j);
    }

    private void dispatchEventToRvFolder(@NotNull MotionEvent event) {
        MotionEvent newEvent = MotionEvent.obtain(event.getDownTime(), event.getEventTime(), event.getAction(), event.getX(),
                event.getY() + mListener.getFolderRecyclerView().getBottom(), event.getMetaState());
        newEvent.recycle();
        mListener.getFolderRecyclerView().dispatchTouchEvent(newEvent);
    }

    private void sizeDragUp() {
        RecyclerView.ViewHolder viewHolder = mListener.getSizeRecyclerView().findViewHolderForLayoutPosition(0);
        RecyclerView.ViewHolder viewHolder2 = mListener.getSizeRecyclerView().findViewHolderForLayoutPosition(1);
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
        mListener.getFolderDragSelect().startDragSelection(mFolderAdapter.getItemCount() - 1);
    }

    private void sizeDragDown() {
        if (mSizeRepository.getViewType() == ViewType.GRID_VIEW) {
            RecyclerView.ViewHolder viewHolder = mListener.getSizeRecyclerView().findViewHolderForLayoutPosition(0);
            RecyclerView.ViewHolder viewHolder2 = mListener.getSizeRecyclerView().findViewHolderForLayoutPosition(1);
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
