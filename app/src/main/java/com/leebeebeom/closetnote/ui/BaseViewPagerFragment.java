package com.leebeebeom.closetnote.ui;

import com.leebeebeom.closetnote.util.adapter.viewholder.ViewPagerVH;
import com.leebeebeom.closetnote.util.constant.AutoScrollFlag;

import static com.leebeebeom.closetnote.util.adapter.viewholder.BaseVH.sDragging;
import static com.leebeebeom.closetnote.util.dragselect.DragSelect.sDragSelecting;
import static com.leebeebeom.closetnote.util.dragselect.ListDragSelect.sScrollEnable;

public abstract class BaseViewPagerFragment extends BaseFragment implements ViewPagerVH.AutoScrollListener {
    @Override
    public void dragAutoScroll(AutoScrollFlag flag) {
        if (sDragSelecting || sDragging)
            switch (flag) {
                case UP:
                    getScrollView().scrollBy(0, -1);
                    sScrollEnable = true;
                    break;
                case DOWN:
                    getScrollView().scrollBy(0, 1);
                    sScrollEnable = true;
                    break;
                default:
                    getScrollView().scrollBy(0, 0);
                    sScrollEnable = false;
            }
    }
}
