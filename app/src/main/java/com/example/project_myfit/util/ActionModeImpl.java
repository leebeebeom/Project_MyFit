package com.example.project_myfit.util;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.view.ActionMode;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.project_myfit.data.model.ParentModel;
import com.example.project_myfit.databinding.TitleActionModeBinding;
import com.example.project_myfit.util.adapter.ParentAdapter;
import com.google.android.material.tabs.TabLayout;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static com.example.project_myfit.util.MyFitConstant.ACTION_MODE_OFF;
import static com.example.project_myfit.util.MyFitConstant.ACTION_MODE_ON;

public class ActionModeImpl implements ActionMode.Callback {
    public static ActionMode actionMode;
    public static boolean isActionModeOn;
    private final TitleActionModeBinding mBinding;
    private final ParentAdapter<? extends ParentModel, ? extends RecyclerView.ViewHolder>[] mParentAdapterArray;
    private final ActionModeListener mListener;
    private List<MenuItem> mMenuItemList;
    private ViewPager2 mViewPager;
    private Button[] mButtonArray;
    private final int mResId;
    private TabLayout mTabLayout;

    @SafeVarargs
    public <T extends ParentModel, T2 extends RecyclerView.ViewHolder> ActionModeImpl
            (LayoutInflater inflater, int resId, ActionModeListener listener, ParentAdapter<T, T2>... parentAdapterArray) {
        this.mBinding = TitleActionModeBinding.inflate(inflater);
        this.mResId = resId;
        this.mParentAdapterArray = parentAdapterArray;
        this.mListener = listener;
    }

    public ActionModeImpl hasViewPager(ViewPager2 viewPager, Button[] buttonArray) {
        this.mViewPager = viewPager;
        this.mButtonArray = buttonArray;
        return this;
    }

    public ActionModeImpl hasViewPager(ViewPager2 viewPager, TabLayout tabLayout) {
        this.mViewPager = viewPager;
        this.mTabLayout = tabLayout;
        return this;
    }

    @Override
    public boolean onCreateActionMode(@NotNull ActionMode mode, Menu menu) {
        mode.getMenuInflater().inflate(mResId, menu);
        mode.setCustomView(mBinding.getRoot());

        setActionMode(mode, ACTION_MODE_ON);

        if (mViewPager != null) viewPagerEnable(false);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, @NotNull Menu menu) {
        mBinding.cb.setOnClickListener(v -> mListener.selectAllClick(mBinding.cb.isChecked()));

        mMenuItemList = new ArrayList<>();
        for (int i = 0; i < menu.size(); i++) mMenuItemList.add(menu.getItem(i));
        return true;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, @NotNull MenuItem item) {
        mListener.actionItemClick(item.getItemId());
        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        setActionMode(null, ACTION_MODE_OFF);

        mBinding.cb.setChecked(false);
        ((ViewGroup) mBinding.getRoot().getParent()).removeAllViews();

        if (mViewPager != null) viewPagerEnable(true);
    }

    private void setActionMode(@Nullable ActionMode mode, int actionModeState) {
        actionMode = mode;
        isActionModeOn = actionModeState == ACTION_MODE_ON;

        for (ParentAdapter<? extends ParentModel, ? extends RecyclerView.ViewHolder> parentModelViewHolderParentAdapter : mParentAdapterArray)
            parentModelViewHolderParentAdapter.setActionModeState(actionModeState);
    }

    public List<MenuItem> getMenuItemList() {
        return mMenuItemList;
    }

    public TitleActionModeBinding getBinding() {
        return mBinding;
    }

    public void viewPagerEnable(boolean enable) {
        mViewPager.setUserInputEnabled(enable);
        if (mButtonArray != null) for (Button button : mButtonArray) button.setEnabled(enable);
        else if (mTabLayout != null) {
            LinearLayout tabLayout = (LinearLayout) mTabLayout.getChildAt(0);
            for (int i = 0; i < 4; i++) tabLayout.getChildAt(i).setClickable(enable);
        }
    }

    public interface ActionModeListener {
        void selectAllClick(boolean isChecked);

        void actionItemClick(int itemId);
    }

}
