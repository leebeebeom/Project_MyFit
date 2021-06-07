package com.example.myfit.ui;

import android.view.View;

import androidx.fragment.app.Fragment;

import com.example.myfit.R;
import com.example.myfit.databinding.ActivityMainBinding;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import lombok.Getter;
import lombok.experimental.Accessors;

@AndroidEntryPoint
@Accessors(prefix = "m")
public abstract class BaseFragment extends Fragment {
    @Inject
    @Getter
    protected ActivityMainBinding mActivityBinding;

    protected void addBottomAppBar() {
        if (mActivityBinding.root.findViewById(R.id.bottomAppBar) == null)
            mActivityBinding.root.addView(mActivityBinding.bottomAppBar);
    }

    protected void removeBottomAppBar() {
        if (mActivityBinding.root.findViewById(R.id.bottomAppBar) != null)
            mActivityBinding.root.removeView(mActivityBinding.bottomAppBar);
    }

    protected void showCustomTitle() {
        if (mActivityBinding.actionBar.tvCustomTitle.getVisibility() == View.GONE)
            mActivityBinding.actionBar.tvCustomTitle.setVisibility(View.VISIBLE);
    }

    protected void hideCustomTitle() {
        if (mActivityBinding.actionBar.tvCustomTitle.getVisibility() == View.VISIBLE)
            mActivityBinding.actionBar.tvCustomTitle.setVisibility(View.GONE);
    }

    protected void showSearchBar() {
        if (mActivityBinding.actionBar.searchBar.layout.getVisibility() == View.GONE)
            mActivityBinding.actionBar.searchBar.layout.setVisibility(View.VISIBLE);
    }

    protected void hideSearchBar() {
        if (mActivityBinding.actionBar.searchBar.layout.getVisibility() == View.VISIBLE)
            mActivityBinding.actionBar.searchBar.layout.setVisibility(View.GONE);
    }

    protected void fabChange(int resId) {
        mActivityBinding.fab.hide();
        mActivityBinding.fab.setImageResource(resId);
        mActivityBinding.fab.show();
    }
}
