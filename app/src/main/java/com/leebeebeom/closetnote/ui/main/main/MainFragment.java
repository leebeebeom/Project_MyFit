package com.leebeebeom.closetnote.ui.main.main;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.leebeebeom.closetnote.R;
import com.leebeebeom.closetnote.data.tuple.BaseTuple;
import com.leebeebeom.closetnote.data.tuple.tuple.CategoryTuple;
import com.leebeebeom.closetnote.databinding.FragmentMainBinding;
import com.leebeebeom.closetnote.ui.BaseViewPagerFragment;
import com.leebeebeom.closetnote.ui.view.LockableScrollView;
import com.leebeebeom.closetnote.util.ActionModeImpl;
import com.leebeebeom.closetnote.util.CommonUtil;
import com.leebeebeom.closetnote.util.adapter.BaseAdapter;
import com.leebeebeom.closetnote.util.adapter.BaseViewPagerAdapter;
import com.leebeebeom.closetnote.util.adapter.viewholder.BaseVHListener;
import com.leebeebeom.closetnote.util.adapter.viewholder.ViewPagerVH;
import com.leebeebeom.closetnote.util.dragselect.DragSelect;
import com.leebeebeom.closetnote.util.popupwindow.BasePopupWindow;
import com.leebeebeom.closetnote.util.popupwindow.PopupWindowListener;
import com.google.android.material.button.MaterialButton;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import lombok.experimental.Accessors;

import static com.leebeebeom.closetnote.util.ActionModeImpl.ACTION_MODE_STRING;
import static com.leebeebeom.closetnote.util.ActionModeImpl.sActionMode;
import static com.leebeebeom.closetnote.util.ActionModeImpl.sActionModeOn;

@AndroidEntryPoint
@Accessors(prefix = "m")
public class MainFragment extends BaseViewPagerFragment implements ActionModeImpl.ViewPagerActionModeListener,
        BaseVHListener, PopupWindowListener.MainPopupWindowListener, DragSelect.DragSelectListener {
    @Inject
    ActionModeImpl.ViewPagerActionModeCallBack mActionModeCallBack;
    @Inject
    BasePopupWindow.MainPopupWindow mPopupWindow;
    @Inject
    BaseViewPagerAdapter.MainViewPagerAdapter mMainViewPagerAdapter;

    private MainViewModel mModel;
    private FragmentMainBinding mBinding;
    private List<MaterialButton> mButtons;

    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        super.onAttach(context);
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (mPopupWindow.isShowing()) mPopupWindow.dismiss();
                else requireActivity().finish();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mModel = new ViewModelProvider(this).get(MainViewModel.class);
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

        mButtons = Arrays.asList(mBinding.btnTop.button, mBinding.btnBottom.button, mBinding.btnOuter.button, mBinding.btnEtc.button);

        setVpPageChangeListener();
        setToggleGroupCheckedListener();
        setButtonClickListener();
        mButtons.get(mModel.getCurrentItem()).setChecked(true);
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
                mButtons.get(position).setChecked(true);
            }
        });
    }

    private void setToggleGroupCheckedListener() {
        TypedValue typedValue = new TypedValue();
        requireContext().getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
        int colorPrimary = typedValue.data;

        TypedValue typedValue2 = new TypedValue();
        requireContext().getTheme().resolveAttribute(R.attr.myColorControl, typedValue2, true);
        int colorControl = typedValue2.data;

        ColorStateList buttonTextOriginColor = mBinding.btnEtc.button.getTextColors();

        //버튼 클릭 시 첫번째 호출
        mBinding.toggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            int childCount = group.getChildCount();
            for (int i = 0; i < 4; i++) {
                mButtons.get(i).setBackgroundColor(Color.TRANSPARENT);
                mButtons.get(i).setTextColor(buttonTextOriginColor);
            }
            if (isChecked) {
                if (checkedId == R.id.btnTop)
                    setButtonCheckedColor(0, colorControl, colorPrimary);
                else if (checkedId == R.id.btnBottom)
                    setButtonCheckedColor(1, colorControl, colorPrimary);
                else if (checkedId == R.id.btnOuter)
                    setButtonCheckedColor(2, colorControl, colorPrimary);
                else if (checkedId == R.id.btnEtc)
                    setButtonCheckedColor(3, colorControl, colorPrimary);
            }
        });
    }

    private void setButtonCheckedColor(int i, int colorControl, int colorPrimary) {
        mButtons.get(i).setBackgroundColor(colorControl);
        mButtons.get(i).setTextColor(colorPrimary);
    }

    private void setButtonClickListener() {
        for (int i = 0; i < 4; i++) {
            int finalI = i;
            mButtons.get(i).setOnClickListener(v -> mBinding.vp.setCurrentItem(finalI));
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
    }

    public void observeCategoryTuplesLive() {
        RecyclerView viewPager = (RecyclerView) mBinding.vp.getChildAt(0);

        mModel.getCategoryLive().observe(getViewLifecycleOwner(), categoryList -> {
                    for (int i = 0; i < 4; i++) {
                        mMainViewPagerAdapter.getAdapters().get(i).setItems(categoryList.get(i));
                        ViewPagerVH viewPagerVH = (ViewPagerVH) viewPager.findViewHolderForAdapterPosition(i);
                        if (viewPagerVH != null) viewPagerVH.setNoData(categoryList.get(i).isEmpty());
                    }
                }
        );
    }

    private void observeSelectedItemSizeLive() {
        mModel.getSelectedItemsLive().observe(getViewLifecycleOwner(), integer -> {
            if (sActionMode != null) {
                mActionModeCallBack.getBinding().tvTitle.setText(getString(R.string.action_mode_title, integer));
                if (mMainViewPagerAdapter.getAdapters().get(mModel.getCurrentItem()).getItemCount() == integer) {
                    Toast.makeText(requireContext(), "호출", Toast.LENGTH_SHORT).show();
                    mActionModeCallBack.getBinding().cb.setChecked(true);
                } else mActionModeCallBack.getBinding().cb.setChecked(false);

                mActionModeCallBack.getMenuItems()[0].setVisible(integer == 1);
                mActionModeCallBack.getMenuItems()[1].setVisible(false);
                mActionModeCallBack.getMenuItems()[2].setVisible(integer > 0);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mActivityBinding.fabTop.hide();
        if (sActionMode != null) sActionMode.finish();
        mBinding = null;
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
    public RecyclerView getRecyclerView() {
        RecyclerView viewPager = (RecyclerView) mBinding.vp.getChildAt(0);
        ViewPagerVH viewPagerPage = (ViewPagerVH) viewPager.findViewHolderForLayoutPosition(mModel.getCurrentItem());
        return viewPagerPage != null ? viewPagerPage.getRecyclerView() : null;
    }

    @Override
    public void setViewPagerEnable(boolean enable) {
        mBinding.vp.setUserInputEnabled(enable);
        mButtons.forEach(button -> button.setEnabled(enable));
    }

    @Override
    public List<BaseAdapter<?, ?, ?>> getAdapters() {
        return Collections.singletonList(mMainViewPagerAdapter.getAdapters().get(mModel.getCurrentItem()));
    }

    @Override
    public void actionItemClick(int itemId) {
        if (itemId == R.id.menu_action_mode_edit)
            navigateNameEditDialog();
        else if (itemId == R.id.menu_action_mode_delete)
            navigateDeleteDialog();
    }

    @Override
    public int getResId() {
        return R.menu.menu_action_mode;
    }

    private void navigateNameEditDialog() {
        CategoryTuple selectedCategoryTuple = mModel.getSelectedCategoryTuples().iterator().next();
        MainFragmentDirections.ToEditCategoryName action =
                MainFragmentDirections.toEditCategoryName(selectedCategoryTuple.getId(), selectedCategoryTuple.getName(), selectedCategoryTuple.getParentIndex());
        CommonUtil.navigate(mNavController, R.id.mainFragment, action);
    }

    private void navigateDeleteDialog() {
        MainFragmentDirections.ToDeleteCategories action = MainFragmentDirections.toDeleteCategories(mModel.getSelectedItemIds());
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
        mMainViewPagerAdapter.getDragSelectListeners().get(tuple.getParentIndex()).startDragSelection(position);
    }

    @Override
    public void dragStart(RecyclerView.ViewHolder viewHolder, BaseTuple tuple) {
        mMainViewPagerAdapter.getItemTouchHelpers().get(tuple.getParentIndex()).startDrag(viewHolder);
    }

    @Override
    public void dragStop(BaseTuple tuple) {
        List<CategoryTuple> newOrderList = mMainViewPagerAdapter.getAdapters().get(mBinding.vp.getCurrentItem()).getNewOrderList();
        mModel.dragDrop(newOrderList);
    }

    @Override
    public void addCategoryClick() {
        MainFragmentDirections.ToAddCategory action = MainFragmentDirections.toAddCategory(mModel.getCurrentItem());
        CommonUtil.navigate(mNavController, R.id.mainFragment, action);
        mPopupWindow.dismiss();
    }

    @Override
    public void sortClick() {
        CommonUtil.navigate(mNavController, R.id.mainFragment, MainFragmentDirections.toSortMain());
        mPopupWindow.dismiss();
    }

    @Override
    public void recycleBinClick() {
        //TODO
//        CommonUtil.navigate(navController, R.id.mainFragment, MainFragmentDirections.toRecycleBinActivity());
    }
}