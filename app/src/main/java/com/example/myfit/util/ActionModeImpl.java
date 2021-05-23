package com.example.myfit.util;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.view.ActionMode;
import androidx.viewpager2.widget.ViewPager2;

import com.example.myfit.databinding.TitleActionModeBinding;
import com.example.myfit.util.adapter.BaseAdapter;
import com.google.android.material.tabs.TabLayout;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class ActionModeImpl implements ActionMode.Callback {
    public static final String ACTION_MODE = "action mode";
    public static ActionMode actionMode;
    public static boolean isActionModeOn;
    public static final int ACTION_MODE_ON = 1;
    public static final int ACTION_MODE_OFF = 2;

    private final TitleActionModeBinding binding;
    private final int resId;
    private final ActionModeListener listener;
    private final BaseAdapter<?, ?, ?>[] adapters;
    private final ViewPager2 viewPager;
    private final Button[] buttonArray;
    private final TabLayout tabLayout;
    private List<MenuItem> menuItemList;

    private ActionModeImpl(ActionModeImpl.Builder builder) {
        this.binding = builder.binding;
        this.resId = builder.resId;
        this.listener = builder.listener;
        this.adapters = builder.adapters;
        this.viewPager = builder.viewPager;
        this.buttonArray = builder.buttonArray;
        this.tabLayout = builder.tabLayout;
    }

    public static class Builder {
        private final TitleActionModeBinding binding;
        private final int resId;
        private final ActionModeListener listener;
        private final BaseAdapter<?, ?, ?>[] adapters;
        private ViewPager2 viewPager;
        private Button[] buttonArray;
        private TabLayout tabLayout;

        public Builder(LayoutInflater inflater, int resId, ActionModeListener listener, BaseAdapter<?, ?, ?>... adapters) {
            this.binding = TitleActionModeBinding.inflate(inflater);
            this.resId = resId;
            this.listener = listener;
            this.adapters = adapters;
        }

        public Builder hasViewPager(ViewPager2 viewPager, Button[] buttonArray) {
            this.viewPager = viewPager;
            this.buttonArray = buttonArray;
            return this;
        }

        public Builder hasViewPager(ViewPager2 viewPager, TabLayout tabLayout) {
            this.viewPager = viewPager;
            this.tabLayout = tabLayout;
            return this;
        }

        public ActionModeImpl build() {
            return new ActionModeImpl(this);
        }
    }


    @Override
    public boolean onCreateActionMode(@NotNull ActionMode mode, Menu menu) {
        mode.getMenuInflater().inflate(resId, menu);
        mode.setCustomView(binding.getRoot());

        setActionMode(mode, ACTION_MODE_ON);

        if (viewPager != null) setViewPagerEnable(false);
        return true;
    }

    private void setActionMode(@Nullable ActionMode mode, int actionModeState) {
        actionMode = mode;
        isActionModeOn = actionModeState == ACTION_MODE_ON;

        Arrays.stream(adapters).forEach(adapter -> adapter.setActionModeState(actionModeState));
    }

    public void setViewPagerEnable(boolean enable) {
        viewPager.setUserInputEnabled(enable);
        if (buttonArray != null)
            Arrays.stream(buttonArray).forEach(button -> button.setEnabled(enable));
        else if (tabLayout != null) {
            LinearLayout tabLayout = (LinearLayout) this.tabLayout.getChildAt(0);
            int count = tabLayout.getChildCount();
            for (int i = 0; i < count; i++) tabLayout.getChildAt(i).setClickable(enable);
        }
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, @NotNull Menu menu) {
        binding.cb.setOnClickListener(v -> listener.selectAllClick(binding.cb.isChecked()));

        menuItemList = new LinkedList<>();
        int count = menu.size();
        for (int i = 0; i < count; i++) menuItemList.add(menu.getItem(i));
        return true;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, @NotNull MenuItem item) {
        listener.actionItemClick(item.getItemId());
        return true;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        setActionMode(null, ACTION_MODE_OFF);

        binding.cb.setChecked(false);
        ((ViewGroup) binding.getRoot().getParent()).removeAllViews();

        if (viewPager != null) setViewPagerEnable(true);
    }

    public List<MenuItem> getMenuItemList() {
        return menuItemList;
    }

    public TitleActionModeBinding getBinding() {
        return binding;
    }

    public interface ActionModeListener {
        void selectAllClick(boolean isChecked);

        void actionItemClick(int itemId);
    }
}
