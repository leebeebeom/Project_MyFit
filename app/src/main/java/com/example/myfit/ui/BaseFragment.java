package com.example.myfit.ui;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myfit.R;
import com.example.myfit.databinding.ActivityMainBinding;
import com.example.myfit.util.LockableScrollView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

import static com.example.myfit.util.ListenerUtil.sScrollEnable;
import static com.example.myfit.util.adapter.viewholder.BaseVH.sDragging;
import static com.example.myfit.util.dragselect.DragSelect.sDragSelecting;

@AndroidEntryPoint
public abstract class BaseFragment extends Fragment {
    @Inject
    ActivityMainBinding mActivityMainBinding;

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        LockableScrollView scrollView = getScrollView();
        FloatingActionButton topFab = getTopFab();

        if (getScrollView().getScaleY() == 0) getTopFab().hide();
        else getTopFab().show();

        setScrollChangeListener(scrollView, topFab);
        setTopFabClickListener(scrollView, topFab);
    }

    private void setScrollChangeListener(LockableScrollView scrollView, FloatingActionButton topFab) {
        scrollView.setOnScrollChangeListener((View.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (v.getScrollY() == 0) topFab.hide();
            else topFab.show();

            if ((sDragSelecting || sDragging) && sScrollEnable && scrollY > oldScrollY)
                v.postDelayed(() -> v.scrollBy(0, 1), 50);
            else if ((sDragSelecting || sDragging) && sScrollEnable && scrollY < oldScrollY)
                v.postDelayed(() -> v.scrollBy(0, -1), 50);
        });
    }

    private void setTopFabClickListener(LockableScrollView scrollView, FloatingActionButton topFab) {
        topFab.setOnClickListener(v -> {
            scrollView.scrollTo(0, 0);
            scrollView.setOnScrollChangeListener((View.OnScrollChangeListener) (v1, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                if (scrollY != 0)
                    scrollView.scrollTo(0, 0);
                else {
                    scrollView.setOnScrollChangeListener((View.OnScrollChangeListener) null);
                    setScrollChangeListener(scrollView, topFab);
                }
            });
        });
    }

    protected void addBottomAppBar() {
        if (mActivityMainBinding.root.findViewById(R.id.bottomAppBar) == null)
            mActivityMainBinding.root.addView(mActivityMainBinding.bottomAppBar);
    }

    protected void removeBottomAppBar() {
        if (mActivityMainBinding.root.findViewById(R.id.bottomAppBar) != null)
            mActivityMainBinding.root.removeView(mActivityMainBinding.bottomAppBar);
    }

    public abstract LockableScrollView getScrollView();

    public abstract FloatingActionButton getTopFab();
}
