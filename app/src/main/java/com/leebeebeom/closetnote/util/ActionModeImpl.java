package com.leebeebeom.closetnote.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.view.ActionMode;
import androidx.fragment.app.Fragment;

import com.leebeebeom.closetnote.databinding.TitleActionModeBinding;
import com.leebeebeom.closetnote.util.adapter.BaseAdapter;
import com.google.android.material.checkbox.MaterialCheckBox;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.qualifiers.ActivityContext;
import lombok.Getter;
import lombok.experimental.Accessors;

@Accessors(prefix = "m")
public class ActionModeImpl implements ActionMode.Callback {
    public static ActionMode sActionMode = null;
    public static boolean sActionModeOn = false;
    public static final String ACTION_MODE_STRING = "action mode";
    public static final int ACTION_MODE_ON = 1;
    public static final int ACTION_MODE_OFF = 2;

    @Getter
    private final TitleActionModeBinding mBinding;
    @Getter
    private MenuItem[] mMenuItems;
    protected final ActionModeListener mListener;

    @Inject
    public ActionModeImpl(@ActivityContext Context context, Fragment fragment) {
        mBinding = TitleActionModeBinding.inflate(LayoutInflater.from(context));
        mListener = (ActionModeListener) fragment;
    }

    @Override
    public boolean onCreateActionMode(@NotNull ActionMode mode, Menu menu) {
        mode.getMenuInflater().inflate(mListener.getResId(), menu);
        mode.setCustomView(mBinding.getRoot());
        mBinding.setActionModeImpl(this);

        setActionMode(mode, ACTION_MODE_ON);
        return true;
    }

    protected void setActionMode(@Nullable ActionMode mode, int actionModeState) {
        sActionMode = mode;
        sActionModeOn = actionModeState == ACTION_MODE_ON;

        mListener.getAdapters().forEach(adapter -> adapter.setActionModeState(actionModeState));
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, @NotNull Menu menu) {
        mMenuItems = new MenuItem[menu.size()];
        int count = mMenuItems.length;
        for (int i = 0; i < count; i++)
            mMenuItems[i] = menu.getItem(i);
        return true;
    }

    public void allSelectClick(View view) {
        if (((MaterialCheckBox) view).isChecked())
            mListener.getAdapters().forEach(BaseAdapter::selectAll);
        else mListener.getAdapters().forEach(BaseAdapter::deselectAll);
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, @NotNull MenuItem item) {
        mListener.actionItemClick(item.getItemId());
        return true;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        setActionMode(null, ACTION_MODE_OFF);

        mBinding.cb.setChecked(false);
        ((ViewGroup) mBinding.getRoot().getParent()).removeAllViews();
    }

    public interface ActionModeListener {
        List<BaseAdapter<?, ?, ?>> getAdapters();

        void actionItemClick(int itemId);

        int getResId();
    }

    public interface ViewPagerActionModeListener extends ActionModeListener {
        void setViewPagerEnable(boolean enable);
    }

    public static class ViewPagerActionModeCallBack extends ActionModeImpl {

        private final ViewPagerActionModeListener mListener;

        @Inject
        protected ViewPagerActionModeCallBack(@ActivityContext Context context,
                                              Fragment fragment) {
            super(context, fragment);
            mListener = (ViewPagerActionModeListener) fragment;
        }

        @Override
        public boolean onCreateActionMode(@NotNull ActionMode mode, Menu menu) {
            super.onCreateActionMode(mode, menu);
            this.mListener.setViewPagerEnable(false);
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            super.onDestroyActionMode(mode);
            this.mListener.setViewPagerEnable(true);
        }
    }
}
