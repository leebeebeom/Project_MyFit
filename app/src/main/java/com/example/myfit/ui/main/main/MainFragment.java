package com.example.myfit.ui.main.main;


import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.hilt.navigation.HiltViewModelFactory;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.myfit.R;
import com.example.myfit.data.tuple.BaseTuple;
import com.example.myfit.data.tuple.tuple.CategoryTuple;
import com.example.myfit.databinding.FragmentMainBinding;
import com.example.myfit.di.Qualifiers;
import com.example.myfit.ui.BaseViewPagerFragment;
import com.example.myfit.ui.main.MainGraphViewModel;
import com.example.myfit.ui.main.main.adapter.CategoryAdapter;
import com.example.myfit.util.ActionModeImpl;
import com.example.myfit.util.CommonUtil;
import com.example.myfit.util.LockableScrollView;
import com.example.myfit.util.SortUtil;
import com.example.myfit.util.adapter.BaseAdapter;
import com.example.myfit.util.adapter.BaseViewPagerAdapter;
import com.example.myfit.util.adapter.viewholder.BaseVHListener;
import com.example.myfit.util.adapter.viewholder.ViewPagerVH;
import com.example.myfit.util.constant.Sort;
import com.example.myfit.util.popupwindow.MainPopupWindow;
import com.example.myfit.util.popupwindow.PopupWindowListener;
import com.google.android.material.button.MaterialButton;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import lombok.Getter;
import lombok.experimental.Accessors;

import static com.example.myfit.ui.dialog.BaseDialog.ACTION_MODE_OFF;
import static com.example.myfit.util.ActionModeImpl.ACTION_MODE_STRING;
import static com.example.myfit.util.ActionModeImpl.sActionMode;
import static com.example.myfit.util.ActionModeImpl.sActionModeOn;

@AndroidEntryPoint
@Accessors(prefix = "m")
public class MainFragment extends BaseViewPagerFragment implements ActionModeImpl.ViewPagerActionModeListener, BaseVHListener, PopupWindowListener.MainPopupWindowListener {
    @Inject
    NavController mNavController;
    @Inject
    ActionModeImpl.MainActionModeCallBack mActionModeCallBack;
    @Inject
    CategoryAdapter[] mCategoryAdapters;
    @Inject
    @Getter
    BaseViewPagerAdapter.MainViewPagerAdapter mMainViewPagerAdapter;
    @Inject
    MainPopupWindow mPopupWindow;
    @Qualifiers.ColorPrimary
    @Inject
    int mColorPrimary;
    @Qualifiers.ColorControl
    @Inject
    int mColorControl;

    private MainViewModel mModel;
    private FragmentMainBinding mBinding;
    private MaterialButton[] mButtons;
    private ColorStateList mButtonTextOriginColor;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mModel = new ViewModelProvider(this).get(MainViewModel.class);

        mActionModeCallBack.setListener(this);
        Arrays.stream(mCategoryAdapters).forEach(categoryAdapter -> categoryAdapter.setListener(this));
        mMainViewPagerAdapter.setListener(this);
        mPopupWindow.setListener(this);

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        addBottomAppBar();
        showCustomTitle();
        hideSearchBar();
        fabChange(R.drawable.icon_search);

        mBinding = FragmentMainBinding.inflate(inflater, container, false);
        mBinding.setMainViewPagerAdapter(mMainViewPagerAdapter);

        Arrays.stream(mMainViewPagerAdapter.getDragSelectListeners()).forEach(dragSelect -> dragSelect.setScrollView(mBinding.sv));

        mButtons = new MaterialButton[]{mBinding.btnTop, mBinding.btnBottom, mBinding.btnOuter, mBinding.btnEtc};

        setVpPageChangeListener();
        setToggleGroupCheckedListener();
        setButtonClickListener();
        mButtons[mModel.getCurrentItem()].setChecked(true);
        setFabClickListener();
        restoreActionMode(savedInstanceState);
        return mBinding.getRoot();

    }

    private void setVpPageChangeListener() {
        mBinding.vp.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                mBinding.sv.smoothScrollTo(0, 0);
                mModel.setCurrentItem(position);
                mButtons[position].setChecked(true);
            }
        });
    }

    private void setToggleGroupCheckedListener() {
        if (mButtonTextOriginColor == null)
            mButtonTextOriginColor = mBinding.btnEtc.getTextColors();

        //버튼 클릭 시 첫번째 호출
        mBinding.toggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            for (int i = 0; i < 4; i++) {
                mButtons[i].setBackgroundColor(Color.TRANSPARENT);
                mButtons[i].setTextColor(mButtonTextOriginColor);
            }
            if (isChecked) {
                if (checkedId == R.id.btnTop)
                    setButtonCheckedColor(0);
                else if (checkedId == R.id.btnBottom)
                    setButtonCheckedColor(1);
                else if (checkedId == R.id.btnOuter)
                    setButtonCheckedColor(2);
                else if (checkedId == R.id.btnEtc)
                    setButtonCheckedColor(3);
            }
        });
    }

    private void setButtonCheckedColor(int i) {
        mButtons[i].setBackgroundColor(mColorControl);
        mButtons[i].setTextColor(mColorPrimary);
    }

    private void setButtonClickListener() {
        for (int i = 0; i < 4; i++) {
            int finalI = i;
            mButtons[i].setOnClickListener(v -> mBinding.vp.setCurrentItem(finalI));
        }
    }

    private void setFabClickListener() {
        //TODO
        mActivityBinding.fab.setOnClickListener(v -> {
            if (sActionMode != null) sActionMode.finish();
//            CommonUtil.navigate(mNavController, R.id.mainFragment, MainFragmentDirections.toSearchActivity());
        });
    }

    private void restoreActionMode(@org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null && savedInstanceState.getBoolean(ACTION_MODE_STRING))
            ((AppCompatActivity) requireActivity()).startSupportActionMode(mActionModeCallBack);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        observeCategoryTuplesLive();
        observeSelectedItemSizeLive();
        observeMainGraphDialogLive();
    }

    public void observeCategoryTuplesLive() {
        RecyclerView viewPager = (RecyclerView) mBinding.vp.getChildAt(0);

        mModel.getCategoryLive().observe(getViewLifecycleOwner(), categoryList ->
                mModel.getMainSortPreferenceLive().observe(getViewLifecycleOwner(), sortInt -> {
                    Sort sort = Sort.values()[sortInt];

                    for (int i = 0; i < 4; i++) {
                        SortUtil.sortCategoryFolderTuples(sort, categoryList.get(i));

                        mCategoryAdapters[i].setItems(sort, categoryList.get(i));

                        ViewPagerVH viewHolder = (ViewPagerVH) viewPager.findViewHolderForAdapterPosition(i);
                        if (viewHolder != null) viewHolder.setNoData(categoryList.get(i).isEmpty());
                    }
                }));
    }

    private void observeSelectedItemSizeLive() {
        mModel.getSelectedItemsLive().observe(getViewLifecycleOwner(), integer -> {
            if (sActionMode != null) {
                mActionModeCallBack.getBinding().tvTitle.setText(getString(R.string.action_mode_title, integer));
                mActionModeCallBack.getBinding().cb.setChecked(mCategoryAdapters[mModel.getCurrentItem()].getItemCount() == integer);

                mActionModeCallBack.getMenuItems().get(0).setVisible(integer == 1);
                mActionModeCallBack.getMenuItems().get(1).setVisible(false);
                mActionModeCallBack.getMenuItems().get(2).setVisible(integer > 0);
            }
        });
    }

    private void observeMainGraphDialogLive() {
        NavBackStackEntry mainGraphBackStack = mNavController.getBackStackEntry(R.id.nav_graph_main);
        MainGraphViewModel mainGraphViewModel = new ViewModelProvider(mainGraphBackStack, HiltViewModelFactory.create(requireContext(), mainGraphBackStack)).get(MainGraphViewModel.class);

        mainGraphViewModel.getBackStackEntryLive().observe(getViewLifecycleOwner(), navBackStackEntry ->
                navBackStackEntry.getSavedStateHandle().getLiveData(ACTION_MODE_OFF).observe(navBackStackEntry, o -> {
                    if (sActionMode != null) sActionMode.finish();
                }));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinding = null;
        mActivityBinding.fabTop.hide();
        if (sActionMode != null) sActionMode.finish();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull @NotNull Menu menu, @NonNull @NotNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull @NotNull MenuItem item) {
        if (item.getItemId() == R.id.menu_main_popup) {
            mPopupWindow.showAsDropDown(requireActivity().findViewById(R.id.menu_main_popup));
            return true;
        } else if (item.getItemId() == R.id.menu_main_search) {
            //TODO
//            CommonUtil.navigate(mNavController, R.id.mainFragment, MainFragmentDirections.toSearchActivity());
            return true;
        }
        return false;
    }

    @Override
    public void onSaveInstanceState(@NonNull @NotNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(ACTION_MODE_STRING, sActionModeOn);
    }

    @Override
    public LockableScrollView getScrollView() {
        return mBinding.sv;
    }

    @Override
    public void setViewPagerEnable(boolean enable) {
        mBinding.vp.setUserInputEnabled(enable);
        Arrays.stream(mButtons).forEach(button -> button.setEnabled(enable));
    }

    @Override
    public BaseAdapter<?, ?, ?>[] getAdapters() {
        return new CategoryAdapter[]{mCategoryAdapters[mBinding.vp.getCurrentItem()]};
    }

    @Override
    public void actionItemClick(int itemId) {
        if (itemId == R.id.menu_action_mode_edit)
            navigateNameEditDialog();
        else if (itemId == R.id.menu_action_mode_delete)
            navigateDeleteDialog();
    }

    private void navigateNameEditDialog() {
        CategoryTuple selectedCategoryTuple = mModel.getSelectedItems().iterator().next();
        MainFragmentDirections.ToEditCategoryNameDialog action =
                MainFragmentDirections.toEditCategoryNameDialog(
                        selectedCategoryTuple.getId(), selectedCategoryTuple.getName(), selectedCategoryTuple.getParentIndex());
        CommonUtil.navigate(mNavController, R.id.mainFragment, action);
    }

    private void navigateDeleteDialog() {
        MainFragmentDirections.ToDeleteCategoriesDialog action = MainFragmentDirections.toDeleteCategoriesDialog(mModel.getSelectedItemIds());
        CommonUtil.navigate(mNavController, R.id.mainFragment, action);
    }

    @Override
    public void itemViewClick(BaseTuple tuple) {
        MainFragmentDirections.ToListFragment action = MainFragmentDirections.toListFragment(tuple.getId(), tuple.getId(), tuple.getParentIndex());
        CommonUtil.navigate(mNavController, R.id.mainFragment, action);
    }

    @Override
    public void itemViewLongClick(int position, BaseTuple tuple) {
        if (sActionMode == null)
            ((AppCompatActivity) requireActivity()).startSupportActionMode(mActionModeCallBack);
        mMainViewPagerAdapter.getDragSelectListeners()[tuple.getParentIndex()].startDragSelection(position);
    }

    @Override
    public void dragStart(RecyclerView.ViewHolder viewHolder, BaseTuple tuple) {
        mMainViewPagerAdapter.getItemTouchHelpers()[tuple.getParentIndex()].startDrag(viewHolder);
    }

    @Override
    public void dragStop(BaseTuple tuple) {
        List<CategoryTuple> newOrderList = mCategoryAdapters[tuple.getParentIndex()].getNewOrderList();
        mModel.dragDrop(newOrderList);
    }

    @Override
    public void addCategoryClick() {
        MainFragmentDirections.ToAddCategoryDialog action = MainFragmentDirections.toAddCategoryDialog(mModel.getCurrentItem());
        CommonUtil.navigate(mNavController, R.id.mainFragment, action);
    }

    @Override
    public void sortClick() {
        CommonUtil.navigate(mNavController, R.id.mainFragment, MainFragmentDirections.toSortMainDialog());
    }

    @Override
    public void recycleBinClick() {
        //TODO
//        CommonUtil.navigate(navController, R.id.mainFragment, MainFragmentDirections.toRecycleBinActivity());
    }
}