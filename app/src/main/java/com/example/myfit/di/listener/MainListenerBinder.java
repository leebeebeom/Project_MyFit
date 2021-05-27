package com.example.myfit.di.listener;

import com.example.myfit.di.Qualifiers;
import com.example.myfit.ui.main.main.listener.MainActionModeImpl;
import com.example.myfit.ui.main.main.listener.MainAutoScrollImpl;
import com.example.myfit.ui.main.main.listener.MainCategoryVHImpl;
import com.example.myfit.ui.main.main.listener.MainPopupWindowImpl;
import com.example.myfit.util.actionmodecallback.BaseActionModeCallBack;
import com.example.myfit.util.adapter.viewholder.BaseVHListener;
import com.example.myfit.util.adapter.viewholder.ViewPagerVH;
import com.example.myfit.util.popupwindow.MainPopupWindowListener;

import dagger.Binds;
import dagger.Module;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.FragmentComponent;

@Module
@InstallIn(FragmentComponent.class)
public abstract class MainListenerBinder {
    @Qualifiers.MainActionModeListener
    @Binds
    public abstract BaseActionModeCallBack.ActionModeListener provideMainActionModeListener(MainActionModeImpl mainActionModeImpl);

    @Binds
    public abstract MainPopupWindowListener provideMainPopupWindowListener(MainPopupWindowImpl mainPopupWindow);

    @Qualifiers.CategoryVHListener
    @Binds
    public abstract BaseVHListener provideCategoryVHListener(MainCategoryVHImpl mainCategoryVhImpl);

    @Qualifiers.MainAutoScrollListener
    @Binds
    public abstract ViewPagerVH.AutoScrollListener provideCategoryAutoScrollListener(MainAutoScrollImpl mainAutoScroll);
}
