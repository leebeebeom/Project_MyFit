package com.example.myfit.util;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.view.ActionMode;

import com.example.myfit.R;
import com.example.myfit.databinding.TitleActionModeBinding;
import com.example.myfit.util.adapter.BaseAdapter;
import com.google.android.material.checkbox.MaterialCheckBox;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.LinkedList;

import javax.inject.Inject;

import dagger.hilt.android.scopes.FragmentScoped;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Accessors(prefix = "m")
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class ActionModeImpl implements ActionMode.Callback {
    public static ActionMode sActionMode = null;
    public static boolean sActionModeOn;
    public static final String ACTION_MODE_STRING = "action mode";
    public static final int ACTION_MODE_ON = 1;
    public static final int ACTION_MODE_OFF = 2;

    @Getter
    private final TitleActionModeBinding mBinding;
    @Getter
    private final LinkedList<MenuItem> mMenuItems = new LinkedList<>();
    @Setter
    protected ActionModeListener mListener;

    @Override
    public boolean onCreateActionMode(@NotNull ActionMode mode, Menu menu) {
        mode.getMenuInflater().inflate(getResId(), menu);
        mode.setCustomView(mBinding.getRoot());
        mBinding.setActionModeImpl(this);

        setActionMode(mode, ACTION_MODE_ON);
        return true;
    }

    protected void setActionMode(@Nullable ActionMode mode, int actionModeState) {
        sActionMode = mode;
        sActionModeOn = actionModeState == ACTION_MODE_ON;

        Arrays.stream(mListener.getAdapters()).forEach(adapter -> adapter.setActionModeState(actionModeState));
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, @NotNull Menu menu) {
        int count = menu.size();
        for (int i = 0; i < count; i++) mMenuItems.add(menu.getItem(i));
        return true;
    }

    public void allSelectClick(View view) {
        if (((MaterialCheckBox) view).isChecked())
            Arrays.stream(mListener.getAdapters()).forEach(BaseAdapter::selectAll);
        else Arrays.stream(mListener.getAdapters()).forEach(BaseAdapter::deselectAll);
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
        BaseAdapter<?, ?, ?>[] getAdapters();

        void actionItemClick(int itemId);
    }

    public interface ViewPagerActionModeListener extends ActionModeListener {
        void setViewPagerEnable(boolean enable);
    }

    protected abstract int getResId();

    @FragmentScoped
    public static class MainActionModeCallBack extends ActionModeImpl {

        private ViewPagerActionModeListener mListener;

        @Inject
        protected MainActionModeCallBack(TitleActionModeBinding binding) {
            super(binding);
        }

        @Override
        protected int getResId() {
            return R.menu.menu_action_mode;
        }

        public void setListener(ViewPagerActionModeListener mListener) {
            this.mListener = mListener;
            super.setListener(mListener);
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
            this.mListener.setViewPagerEnable(false);
        }
    }

    @FragmentScoped
    public static class ListActionModeCallBack extends ActionModeImpl {
        @Inject
        public ListActionModeCallBack(TitleActionModeBinding binding) {
            super(binding);
        }

        @Override
        protected int getResId() {
            return R.menu.menu_action_mode;
        }
    }
}
