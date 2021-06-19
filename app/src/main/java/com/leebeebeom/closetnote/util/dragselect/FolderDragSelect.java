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

import javax.inject.Inject;

import lombok.experimental.Accessors;

import static com.leebeebeom.closetnote.util.adapter.viewholder.BaseVH.sDragging;

@Accessors(prefix = "m")
public class FolderDragSelect extends ListDragSelect implements DragSelectTouchListener.OnAdvancedDragSelectListener {

    @Inject
    public FolderDragSelect(FolderAdapter folderAdapter, SizeRepository sizeRepository, Fragment fragment) {
        super(folderAdapter, sizeRepository, (ListDragSelectListener) fragment);
        withSelectListener(this);
    }

    @Override
    public void onSelectionStarted(int i) {
        DragSelect.sDragSelecting = true;
        sFolderDragSelecting = true;
        mListener.getScrollView().setScrollable(false);
        if (sFolderStartPosition == -1) folderHolderCallOnClick(i);
        sFolderStartPosition = i;
    }

    @Override
    public void onSelectionFinished(int i) {
        DragSelect.sDragSelecting = false;
        sFolderDragSelecting = false;
        mListener.getScrollView().setScrollable(true);
        sFolderStartPosition = -1;
    }

    @Override
    public void onSelectChange(int i, int i1, boolean b) {
        sFolderSelectedPosition1 = i;
        sFolderSelectedPosition2 = i1;
        sFolderSelectedState = b;
        for (int j = i; j <= i1; j++) folderHolderCallOnClick(j);
    }

    @Override
    public boolean onRecyclerViewTouch(View v, MotionEvent event) {
        if (!sSizeStart && (sFolderDragSelecting || sDragging))
            super.autoScroll(event);
        //drag down, up
        if (sFolderDragSelecting && sFolderStart && event.getY() > v.getBottom() - 50
                && event.getAction() != MotionEvent.ACTION_UP) {
            dispatchEventToRvSize(v, event);
            if (!sSizeDragSelecting) folderDragDown();
        } else if (sFolderDragSelecting && sFolderStart && event.getY() < v.getBottom()
                && sSizeDragSelecting && event.getAction() != MotionEvent.ACTION_UP)
            folderDragUp();
        //finish
        if (event.getAction() == MotionEvent.ACTION_UP && sFolderStart)
            dragSelectFinished(event, mListener.getSizeRecyclerView());
        return false;
    }

    private void dispatchEventToRvSize(View v, MotionEvent event) {
        MotionEvent newEvent = MotionEvent.obtain(event.getDownTime(), event.getEventTime(), event.getAction(), event.getX(),
                event.getY() - v.getBottom(), event.getMetaState());
        newEvent.recycle();
        mListener.getSizeRecyclerView().dispatchTouchEvent(newEvent);
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

        mListener.getSizeDragSelect().startDragSelection(0);
        sFolderSelectedState = false;
        sFolderSelectedPosition1 = -1;
        sFolderSelectedPosition2 = -1;
    }

    private void folderDragUp() {
        for (int i = sFolderStartPosition + 1; i < mFolderAdapter.getItemCount(); i++)
            folderHolderCallOnClick(i);
        this.startDragSelection(sFolderStartPosition);

        RecyclerView.ViewHolder viewHolder = mListener.getSizeRecyclerView().findViewHolderForLayoutPosition(0);
        if (viewHolder != null) viewHolder.itemView.callOnClick();

        RecyclerView.ViewHolder viewHolder2 = mListener.getSizeRecyclerView().findViewHolderForLayoutPosition(1);
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
