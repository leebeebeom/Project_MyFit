package com.example.myfit.util.dragselect;

import android.annotation.SuppressLint;
import android.view.MotionEvent;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.example.myfit.data.repository.SizeRepository;
import com.example.myfit.ui.main.list.adapter.folderadapter.FolderAdapter;
import com.example.myfit.util.ListenerUtil;
import com.example.myfit.util.constant.ViewType;
import com.michaelflisar.dragselectrecyclerview.DragSelectTouchListener;

import javax.inject.Inject;

import dagger.hilt.android.scopes.FragmentScoped;

import static com.example.myfit.util.adapter.viewholder.BaseVH.sDragging;

@FragmentScoped
public class FolderDragSelect extends ListDragSelect implements DragSelectTouchListener.OnAdvancedDragSelectListener {
    private final SizeDragSelect mSizeDragSelect;

    @Inject
    public FolderDragSelect(FolderAdapter folderAdapter,
                            SizeRepository sizeRepository,
                            SizeDragSelect sizeDragSelect) {
        super(folderAdapter, sizeRepository);
        withSelectListener(this);
        this.mSizeDragSelect = sizeDragSelect;
    }

    @Override
    public void onSelectionStarted(int i) {
        DragSelect.sDragSelecting = true;
        sFolderDragSelecting = true;
        mScrollView.setScrollable(false);
        if (sFolderStartPosition == -1) folderHolderCallOnClick(i);
        sFolderStartPosition = i;
    }

    @Override
    public void onSelectionFinished(int i) {
        DragSelect.sDragSelecting = false;
        sFolderDragSelecting = false;
        mScrollView.setScrollable(true);
        sFolderStartPosition = -1;
    }

    @Override
    public void onSelectChange(int i, int i1, boolean b) {
        sFolderSelectedPosition1 = i;
        sFolderSelectedPosition2 = i1;
        sFolderSelectedState = b;
        for (int j = i; j <= i1; j++) folderHolderCallOnClick(j);
    }

    @SuppressLint("ClickableViewAccessibility")
    protected void setOnRecyclerViewTouchListener(RecyclerView folderRv, RecyclerView sizeRv) {
        folderRv.setOnTouchListener((v, event) -> {
            if (!sSizeStart && (sFolderDragSelecting || sDragging))
                ListenerUtil.autoScroll(mScrollView, event);
            //drag down, up
            if (sFolderDragSelecting && sFolderStart && event.getY() > v.getBottom() - 50
                    && event.getAction() != MotionEvent.ACTION_UP) {
                dispatchEventToRvSize(v, event, sizeRv);
                if (!sSizeDragSelecting) folderDragDown();
            } else if (sFolderDragSelecting && sFolderStart && event.getY() < v.getBottom()
                    && sSizeDragSelecting && event.getAction() != MotionEvent.ACTION_UP)
                folderDragUp(sizeRv);
            //finish
            if (event.getAction() == MotionEvent.ACTION_UP && sFolderStart)
                dragSelectFinished(event, sizeRv);
            return false;
        });
    }

    private void dispatchEventToRvSize(View v, MotionEvent event, RecyclerView sizeRv) {
        MotionEvent newEvent = MotionEvent.obtain(event.getDownTime(), event.getEventTime(), event.getAction(), event.getX(),
                event.getY() - v.getBottom(), event.getMetaState());
        newEvent.recycle();
        sizeRv.dispatchTouchEvent(newEvent);
    }

    private void folderDragDown() {
        int lastPosition = mFolderAdapter.getItemCount();

        if (sFolderSelectedPosition1 == -1) {
            for (int i = sFolderStartPosition + 1; i < lastPosition; i++)
                folderHolderCallOnClick(i);
        } else if (sFolderStartPosition < sFolderSelectedPosition1) {
            if (sFolderSelectedPosition1 == sFolderSelectedPosition2) {
                if (sFolderSelectedState) {
                    for (int i = sFolderSelectedPosition1 + 1; i < lastPosition; i++)
                        folderHolderCallOnClick(i);
                } else {
                    for (int i = sFolderSelectedPosition1; i < lastPosition; i++)
                        folderHolderCallOnClick(i);
                }
            } else if (sFolderSelectedPosition1 < sFolderSelectedPosition2) {
                if (sFolderSelectedState) {
                    for (int i = sFolderSelectedPosition2 + 1; i < lastPosition; i++)
                        folderHolderCallOnClick(i);
                }
            }
        } else if (sFolderStartPosition > sFolderSelectedPosition1) {
            for (int i = sFolderStartPosition + 1; i < lastPosition; i++)
                folderHolderCallOnClick(i);
            if (sFolderSelectedPosition1 == sFolderSelectedPosition2) {
                if (sFolderSelectedState) {
                    for (int i = sFolderSelectedPosition1; i < sFolderStartPosition; i++)
                        folderHolderCallOnClick(i);
                } else {
                    for (int i = sFolderSelectedPosition1 + 1; i < sFolderStartPosition; i++)
                        folderHolderCallOnClick(i);
                }
            } else {
                if (sFolderSelectedState) {
                    for (int i = sFolderSelectedPosition1; i < sFolderStartPosition; i++)
                        folderHolderCallOnClick(i);
                } else {
                    for (int i = sFolderSelectedPosition2 + 1; i < sFolderStartPosition; i++)
                        folderHolderCallOnClick(i);
                }
            }
        }

        mSizeDragSelect.startDragSelection(0);
        sFolderSelectedState = false;
        sFolderSelectedPosition1 = -1;
        sFolderSelectedPosition2 = -1;
    }

    private void folderDragUp(RecyclerView sizeRv) {
        for (int i = sFolderStartPosition + 1; i < mFolderAdapter.getItemCount(); i++)
            folderHolderCallOnClick(i);
        this.startDragSelection(sFolderStartPosition);

        RecyclerView.ViewHolder viewHolder = sizeRv.findViewHolderForLayoutPosition(0);
        if (viewHolder != null) viewHolder.itemView.callOnClick();

        RecyclerView.ViewHolder viewHolder2 = sizeRv.findViewHolderForLayoutPosition(1);
        if (mSizeRepository.getViewType() == ViewType.GRID_VIEW && viewHolder2 != null &&
                ((sSizeSelectedPosition2 == 1 && sSizeSelectedState) || (sSizeSelectedPosition2 == 3 && !sSizeSelectedState)))
            viewHolder2.itemView.callOnClick();

        sSizeDragSelecting = false;
        sSizeSelectedState = false;
        sSizeSelectedPosition1 = -1;
        sSizeSelectedPosition2 = -1;
        sSizeStartPosition = -1;
    }
}
