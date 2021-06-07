package com.example.myfit.ui;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myfit.R;
import com.example.myfit.databinding.ActivityMainBinding;
import com.example.myfit.util.LockableScrollView;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import lombok.Getter;
import lombok.experimental.Accessors;

import static com.example.myfit.util.ListenerUtil.sScrollEnable;
import static com.example.myfit.util.adapter.viewholder.BaseVH.sDragging;
import static com.example.myfit.util.dragselect.DragSelect.sDragSelecting;

@AndroidEntryPoint
@Accessors(prefix = "m")
public abstract class BaseFragment extends Fragment {
    @Inject
    @Getter
    protected ActivityMainBinding mActivityBinding;

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getScrollView().getScaleY() == 0) mActivityBinding.fabTop.hide();
        else mActivityBinding.fabTop.show();

        setOnScrollChange();
        setOnTopFabClick();
    }

    protected abstract LockableScrollView getScrollView();

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

    private void setOnScrollChange() {
        getScrollView().setOnScrollChangeListener((View.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (v.getScrollY() == 0) mActivityBinding.fabTop.hide();
            else mActivityBinding.fabTop.show();

            if ((sDragSelecting || sDragging) && sScrollEnable && scrollY > oldScrollY)
                v.postDelayed(() -> v.scrollBy(0, 1), 50);
            else if ((sDragSelecting || sDragging) && sScrollEnable && scrollY < oldScrollY)
                v.postDelayed(() -> v.scrollBy(0, -1), 50);
        });
    }

    private void setOnTopFabClick() {
        LockableScrollView scrollView = getScrollView();
        mActivityBinding.fabTop.setOnClickListener(v -> {
            scrollView.scrollTo(0, 0);
            scrollView.setOnScrollChangeListener((View.OnScrollChangeListener) (v1, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                if (scrollY != 0)
                    getScrollView().scrollTo(0, 0);
                else {
                    getScrollView().setOnScrollChangeListener((View.OnScrollChangeListener) null);
                    setOnScrollChange();
                }
            });
        });
    }
}
