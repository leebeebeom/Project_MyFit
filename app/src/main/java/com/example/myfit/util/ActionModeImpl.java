package com.example.myfit.util;

import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;

import androidx.appcompat.view.ActionMode;

import com.example.myfit.R;
import com.example.myfit.databinding.TitleActionModeBinding;
import com.example.myfit.util.adapter.BaseAdapter;

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

@Accessors(chain = true, prefix = "m")
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class ActionModeImpl implements ActionMode.Callback {
    public static ActionMode sActionMode = null;
    public static boolean sActionModeOn;
    public static final String ACTION_MODE_STRING = "action mode";
    public static final int ACTION_MODE_ON = 1;
    public static final int ACTION_MODE_OFF = 2;

    @Getter
    private final TitleActionModeBinding mBinding;
    @Setter
    private ActionModeListener mListener;
    @Getter
    private final LinkedList<MenuItem> menuItems = new LinkedList<>();

    @Override
    public boolean onCreateActionMode(@NotNull ActionMode mode, Menu menu) {
        mode.getMenuInflater().inflate(getResId(), menu);
        mode.setCustomView(mBinding.getRoot());

        setActionMode(mode, ACTION_MODE_ON);

        mListener.setViewPagerEnable(false);
        return true;
    }

    protected void setActionMode(@Nullable ActionMode mode, int actionModeState) {
        sActionMode = mode;
        sActionModeOn = actionModeState == ACTION_MODE_ON;

        Arrays.stream(mListener.getAdapters()).forEach(adapter -> adapter.setActionModeState(actionModeState));
    }

//        else if (mTabLayout != null) {
//            LinearLayout tabLayout = (LinearLayout) this.mTabLayout.getChildAt(0);
//            int count = tabLayout.getChildCount();
//            for (int i = 0; i < count; i++) tabLayout.getChildAt(i).setClickable(enable);
//        }
//    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, @NotNull Menu menu) {
        mBinding.cb.setOnClickListener(v -> {
            if (mBinding.cb.isChecked())
                Arrays.stream(mListener.getAdapters()).forEach(BaseAdapter::selectAll);
            else Arrays.stream(mListener.getAdapters()).forEach(BaseAdapter::deselectAll);
        });

        int count = menu.size();
        for (int i = 0; i < count; i++) menuItems.add(menu.getItem(i));
        return true;
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

        mListener.setViewPagerEnable(true);
    }

    public interface ActionModeListener {
        void setViewPagerEnable(boolean enable);

        BaseAdapter<?, ?, ?>[] getAdapters();

        void actionItemClick(int itemId);
    }

    protected abstract int getResId();

    @FragmentScoped
    public static class MainActionModeCallBack extends ActionModeImpl {

        @Inject
        protected MainActionModeCallBack(TitleActionModeBinding binding) {
            super(binding);
        }

        @Override
        protected int getResId() {
            return R.menu.menu_action_mode;
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
