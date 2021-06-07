package com.example.myfit.ui;

import com.example.myfit.util.ListenerUtil;
import com.example.myfit.util.LockableScrollView;
import com.example.myfit.util.adapter.viewholder.ViewPagerVH;
import com.example.myfit.util.constant.AutoScrollFlag;

public abstract class BaseViewPagerFragment extends BaseFragment implements ViewPagerVH.AutoScrollListener {
    @Override
    public void dragAutoScroll(AutoScrollFlag flag) {
        ListenerUtil.viewPagerAutoScroll(getScrollView(), flag);
    }

    protected abstract LockableScrollView getScrollView();
}
