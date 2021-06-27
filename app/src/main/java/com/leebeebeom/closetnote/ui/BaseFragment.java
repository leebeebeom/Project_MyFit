package com.leebeebeom.closetnote.ui;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.hilt.navigation.HiltViewModelFactory;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavBackStackEntry;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.leebeebeom.closetnote.R;
import com.leebeebeom.closetnote.ui.main.MainGraphViewModel;
import com.leebeebeom.closetnote.ui.signin.BaseSignInFragment;
import com.leebeebeom.closetnote.ui.view.LockableScrollView;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import lombok.experimental.Accessors;

import static com.leebeebeom.closetnote.util.adapter.viewholder.BaseVH.sDragging;
import static com.leebeebeom.closetnote.util.dragselect.DragSelect.sDragSelecting;
import static com.leebeebeom.closetnote.util.dragselect.ListDragSelect.sScrollEnable;

@AndroidEntryPoint
@Accessors(prefix = "m")
public abstract class BaseFragment extends BaseSignInFragment {
    @Inject
    FirebaseAuth mAuth;
    private MainGraphViewModel mMainGraphViewModel;

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getScrollView().getScaleY() == 0) mActivityBinding.fabTop.setVisibility(View.GONE);
        else mActivityBinding.fabTop.setVisibility(View.VISIBLE);

        setOnScrollChange();
        setOnTopFabClick();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser() == null)
            mNavController.navigate(R.id.signInFragment);
    }

    public abstract LockableScrollView getScrollView();

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

    protected void clearScrollFlags() {
        MaterialToolbar toolBar = mActivityBinding.actionBar.toolBar;
        AppBarLayout.LayoutParams layoutParams = (AppBarLayout.LayoutParams) toolBar.getLayoutParams();
        layoutParams.setScrollFlags(0);
    }

    protected void fabChange(int resId) {
        mActivityBinding.fab.hide();
        mActivityBinding.fab.setImageResource(resId);
        mActivityBinding.fab.show();
    }

    private void setOnScrollChange() {
        getScrollView().setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (v.getScrollY() == 0) mActivityBinding.fabTop.hide();
            else mActivityBinding.fabTop.show();

            if ((sDragSelecting || sDragging) && sScrollEnable && scrollY > oldScrollY)
                v.postDelayed(() -> v.scrollBy(0, 1), 50);
            else if ((sDragSelecting || sDragging) && sScrollEnable && scrollY < oldScrollY)
                v.postDelayed(() -> v.scrollBy(0, -1), 50);
        });
    }

    private void setOnTopFabClick() {
        NestedScrollView scrollView = getScrollView();
        mActivityBinding.fabTop.setOnClickListener(v -> {
            scrollView.scrollTo(0, 0);
            scrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v1, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                if (scrollY != 0)
                    getScrollView().scrollTo(0, 0);
                else {
                    getScrollView().setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) null);
                    setOnScrollChange();
                }
            });
        });
    }

    protected MainGraphViewModel getMainGraphViewModel() {
        if (mMainGraphViewModel == null) {
            NavBackStackEntry mainBackStackEntry = mNavController.getBackStackEntry(R.id.nav_graph_main);
            mMainGraphViewModel = new ViewModelProvider(
                    mainBackStackEntry, HiltViewModelFactory.create(requireContext(), mainBackStackEntry)).get(MainGraphViewModel.class);
        }
        return mMainGraphViewModel;
    }
}
