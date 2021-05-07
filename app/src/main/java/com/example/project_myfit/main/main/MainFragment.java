package com.example.project_myfit.main.main;

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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.project_myfit.MainActivity;
import com.example.project_myfit.R;
import com.example.project_myfit.data.model.Category;
import com.example.project_myfit.databinding.ActivityMainBinding;
import com.example.project_myfit.databinding.FragmentMainBinding;
import com.example.project_myfit.databinding.LayoutPopupBinding;
import com.example.project_myfit.dialog.DialogViewModel;
import com.example.project_myfit.main.main.adapter.CategoryAdapter;
import com.example.project_myfit.main.main.adapter.MainViewPagerAdapter;
import com.example.project_myfit.util.ActionModeImpl;
import com.example.project_myfit.util.CommonUtil;
import com.example.project_myfit.util.DragSelectImpl;
import com.example.project_myfit.util.ListenerUtil;
import com.example.project_myfit.util.MyFitVariable;
import com.example.project_myfit.util.PopupWindowImpl;
import com.example.project_myfit.util.Sort;
import com.example.project_myfit.util.adapter.DragCallBackList;
import com.example.project_myfit.util.adapter.viewholder.CategoryVH;
import com.example.project_myfit.util.adapter.viewholder.ViewPagerVH;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.project_myfit.util.MyFitConstant.ACTION_MODE;
import static com.example.project_myfit.util.MyFitConstant.CATEGORY;
import static com.example.project_myfit.util.MyFitConstant.DELETE_SELECTED_ITEM_CONFIRM;
import static com.example.project_myfit.util.MyFitConstant.EDIT_NAME_CONFIRM;
import static com.example.project_myfit.util.MyFitConstant.MAIN_FRAGMENT;
import static com.example.project_myfit.util.MyFitConstant.SORT_CONFIRM;

public class MainFragment extends Fragment implements ViewPagerVH.ViewPagerAutoScrollListener, CategoryVH.CategoryVHListener, ActionModeImpl.ActionModeListener, PopupWindowImpl.PopupWindowClickListener {

    private MainViewModel mModel;
    private FragmentMainBinding mBinding;
    private ActivityMainBinding mActivityBinding;
    private PopupWindowImpl mPopupWindow;
    private ItemTouchHelper[] mTouchHelperArray;
    private CategoryAdapter[] mCategoryAdapterArray;
    private MaterialButton[] mButtonArray;
    private NavController mNavController;
    private ActionModeImpl mActionMode;
    private DragSelectImpl[] mDragSelectListener;
    private ListenerUtil mListenerUtil;
    private ColorStateList mTextOriginColor;
    private int mColorControl;
    private int mColorPrimary;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mModel = new ViewModelProvider(this).get(MainViewModel.class);
        mNavController = NavHostFragment.findNavController(this);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = FragmentMainBinding.inflate(inflater);

        mBinding.vp.setAdapter(getViewPagerAdapter());
        mBinding.vp.setOffscreenPageLimit(3);

        mActivityBinding = ((MainActivity) requireActivity()).mBinding;

        mButtonArray = new MaterialButton[]{mBinding.btnTop, mBinding.btnBottom, mBinding.btnOuter, mBinding.btnEtc};
        mActionMode = new ActionModeImpl(getLayoutInflater(), R.menu.menu_action_mode, this, mCategoryAdapterArray)
                .hasViewPager(mBinding.vp, mButtonArray);

        LayoutPopupBinding popupBinding = LayoutPopupBinding.inflate(inflater);
        popupBinding.tvCreateFolder.setVisibility(View.GONE);
        mPopupWindow = new PopupWindowImpl(popupBinding, this);
        return mBinding.getRoot();
    }

    @NotNull
    private MainViewPagerAdapter getViewPagerAdapter() {
        mCategoryAdapterArray = new CategoryAdapter[4];
        for (int i = 0; i < 4; i++)
            mCategoryAdapterArray[i] = new CategoryAdapter(requireContext(), mModel, this);

        mTouchHelperArray = new ItemTouchHelper[4];
        for (int i = 0; i < 4; i++)
            mTouchHelperArray[i] = new ItemTouchHelper(new DragCallBackList(mCategoryAdapterArray[i]));

        mDragSelectListener = new DragSelectImpl[4];
        for (int i = 0; i < mDragSelectListener.length; i++)
            mDragSelectListener[i] = new DragSelectImpl().setScrollView(mBinding.sv);

        return new MainViewPagerAdapter(mCategoryAdapterArray, mDragSelectListener, mTouchHelperArray, this);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (mBinding.sv.getScrollY() == 0) mActivityBinding.fabTop.hide();

        observeDialogLive();
        observeCategoryLive();
        observeSelectedItemSizeLive();
    }

    private void observeDialogLive() {
        DialogViewModel dialogViewModel = new ViewModelProvider(mNavController.getViewModelStoreOwner(R.id.nav_graph_main)).get(DialogViewModel.class);
        dialogViewModel.orderNumberInit();

        dialogViewModel.getBackStackEntryLive().observe(getViewLifecycleOwner(), navBackStackEntry -> {
            observeEditNameLive(navBackStackEntry);
            observeSortLive(navBackStackEntry);
            observeDeleteSelectedItemLive(navBackStackEntry);
        });
    }

    private void observeEditNameLive(@NotNull androidx.navigation.NavBackStackEntry navBackStackEntry) {
        navBackStackEntry.getSavedStateHandle().getLiveData(EDIT_NAME_CONFIRM).observe(navBackStackEntry, o -> {
            if (MyFitVariable.actionMode != null) MyFitVariable.actionMode.finish();
        });
    }

    private void observeSortLive(@NotNull androidx.navigation.NavBackStackEntry navBackStackEntry) {
        navBackStackEntry.getSavedStateHandle().getLiveData(SORT_CONFIRM).observe(navBackStackEntry, o -> {
            if (o instanceof Integer && (Integer) o != mModel.getSort()) {
                mModel.changeSort((Integer) o);
                observeCategoryLive();
            }
        });
    }

    private void observeDeleteSelectedItemLive(@NotNull androidx.navigation.NavBackStackEntry navBackStackEntry) {
        navBackStackEntry.getSavedStateHandle().getLiveData(DELETE_SELECTED_ITEM_CONFIRM).observe(navBackStackEntry, o -> {
            mModel.deleteSelectedCategory();
            if (MyFitVariable.actionMode != null) MyFitVariable.actionMode.finish();
        });
    }

    public void observeCategoryLive() {
        mModel.getCategoryLive(false).observe(getViewLifecycleOwner(), categoryList -> {

            List<Category> sortedList = Sort.categorySort(mModel.getSort(), categoryList);
            List<List<Category>> categorizedList = new ArrayList<>(4);
            for (int i = 0; i < 4; i++) {
                int finalI = i;
                categorizedList.add(sortedList.stream()
                        .filter(category -> category.getParentCategory().equals(MyFitVariable.parentCategoryArray[finalI]))
                        .collect(Collectors.toList()));
            }

            for (int i = 0; i < mCategoryAdapterArray.length; i++)
                mCategoryAdapterArray[i].setItem(mModel.getSort(), categorizedList.get(i),
                        mModel.getFolderParentIdList(MyFitVariable.parentCategoryArray[i]), mModel.getSizeParentIdList(MyFitVariable.parentCategoryArray[i]));

            RecyclerView viewPager = (RecyclerView) mBinding.vp.getChildAt(0);
            ViewPagerVH[] viewPagerVHArray = new ViewPagerVH[4];
            for (int i = 0; i < viewPagerVHArray.length; i++)
                viewPagerVHArray[i] = (ViewPagerVH) viewPager.findViewHolderForLayoutPosition(i);

            for (int i = 0; i < viewPagerVHArray.length; i++)
                if (viewPagerVHArray[i] != null)
                    viewPagerVHArray[i].setNoData(categorizedList.get(i).isEmpty());
        });
    }

    private void observeSelectedItemSizeLive() {
        mModel.getSelectedCategorySizeLive().observe(getViewLifecycleOwner(), integer -> {
            if (MyFitVariable.actionMode != null) {
                mActionMode.getBinding().tvTitle.setText(getString(R.string.action_mode_title, integer));
                mActionMode.getBinding().cb.setChecked(mCategoryAdapterArray[mModel.getCurrentItem()].getItemCount() == integer);

                mActionMode.getMenuItemList().get(0).setVisible(integer == 1);
                mActionMode.getMenuItemList().get(1).setVisible(false);
                mActionMode.getMenuItemList().get(2).setVisible(integer > 0);
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mListenerUtil = new ListenerUtil();
        mListenerUtil.setScrollChangeListener(mBinding.sv, mActivityBinding.fabTop);
        mListenerUtil.setFabTopClickListener(mBinding.sv, mActivityBinding.fabTop);
        setVpPageChangeListener();
        setToggleGroupCheckedListener();
        setButtonClickListener();
        mButtonArray[mModel.getCurrentItem()].setChecked(true);
        setFabClickListener();
        restoreActionMode(savedInstanceState);
    }

    private void setVpPageChangeListener() {
        mBinding.vp.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                mBinding.sv.smoothScrollTo(0, 0);
                mModel.setCurrentItem(position);
                mButtonArray[position].setChecked(true);
            }
        });
    }

    private void setToggleGroupCheckedListener() {
        if (mTextOriginColor == null) mTextOriginColor = mBinding.btnEtc.getTextColors();

        if (mColorControl == 0 || mColorPrimary == 0) {
            TypedValue typedValue = new TypedValue();
            if (mColorControl == 0) {
                requireContext().getTheme().resolveAttribute(R.attr.colorControlNormal, typedValue, true);
                mColorControl = typedValue.data;
            }
            if (mColorPrimary == 0) {
                requireContext().getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
                mColorPrimary = typedValue.data;
            }
        }

        //버튼 클릭 시 첫번째 호출
        mBinding.toggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            for (int i = 0; i < 4; i++) {
                mButtonArray[i].setBackgroundColor(Color.TRANSPARENT);
                mButtonArray[i].setTextColor(mTextOriginColor);
            }

            if (checkedId == R.id.btnTop && isChecked) {
                mButtonArray[0].setBackgroundColor(mColorControl);
                mButtonArray[0].setTextColor(mColorPrimary);
            } else if (checkedId == R.id.btnBottom && isChecked) {
                mButtonArray[1].setBackgroundColor(mColorControl);
                mButtonArray[1].setTextColor(mColorPrimary);
            } else if (checkedId == R.id.btnOuter && isChecked) {
                mButtonArray[2].setBackgroundColor(mColorControl);
                mButtonArray[2].setTextColor(mColorPrimary);
            } else if (checkedId == R.id.btnEtc && isChecked) {
                mButtonArray[3].setBackgroundColor(mColorControl);
                mButtonArray[3].setTextColor(mColorPrimary);
            }
        });
    }

    private void setButtonClickListener() {
        for (int i = 0; i < 4; i++) {
            int finalI = i;
            mButtonArray[i].setOnClickListener(v -> mBinding.vp.setCurrentItem(finalI));
        }
    }

    private void setFabClickListener() {
        mActivityBinding.fab.setOnClickListener(v -> {
            if (MyFitVariable.actionMode != null) MyFitVariable.actionMode.finish();
            CommonUtil.navigate(mNavController, R.id.mainFragment, MainFragmentDirections.toSearchActivity());
        });
    }

    private void restoreActionMode(@org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null && savedInstanceState.getBoolean(ACTION_MODE)) {
            mCategoryAdapterArray[mModel.getCurrentItem()].setSelectedItemList(mModel.getSelectedCategoryList());
            ((AppCompatActivity) requireActivity()).startSupportActionMode(mActionMode);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (MyFitVariable.actionMode != null) MyFitVariable.actionMode.finish();
        if (mActivityBinding.fabTop.getVisibility() == View.VISIBLE) mActivityBinding.fabTop.hide();
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
            CommonUtil.navigate(mNavController, R.id.mainFragment, MainFragmentDirections.toSearchActivity());
            return true;
        }
        return false;
    }

    @Override
    public void onSaveInstanceState(@NonNull @NotNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(ACTION_MODE, MyFitVariable.isActionModeOn);
    }

    //category adapter------------------------------------------------------------------------------
    @Override
    public void onCategoryItemViewClick(Category category, MaterialCheckBox checkBox) {
        if (MyFitVariable.actionMode == null)
            CommonUtil.navigate(mNavController, R.id.mainFragment,
                    MainFragmentDirections.toListFragment(category.getId(), 0, category.getParentCategory()));
        else {
            checkBox.setChecked(!checkBox.isChecked());

            if (checkBox.isChecked())
                mModel.getSelectedCategoryList().add(category);
            else mModel.getSelectedCategoryList().remove(category);

            mCategoryAdapterArray[mModel.getCurrentItem()].itemSelected(category.getId());
            mModel.setSelectedCategorySizeLiveValue();
        }
    }

    @Override
    public void onCategoryItemViewLongClick(int position) {
        if (MyFitVariable.actionMode == null) {
            mModel.getSelectedCategoryList().clear();
            ((AppCompatActivity) requireActivity()).startSupportActionMode(mActionMode);
        }
        mDragSelectListener[mModel.getCurrentItem()].startDragSelection(position);
    }

    @Override
    public void onCategoryDragHandleTouch(RecyclerView.ViewHolder viewHolder) {
            mTouchHelperArray[mModel.getCurrentItem()].startDrag(viewHolder);
    }
    //----------------------------------------------------------------------------------------------

    @Override
    public void dragAutoScroll(int upDownStop) {
        mListenerUtil.viewPagerAutoScroll(mBinding.sv, upDownStop);
    }

    //action mode-----------------------------------------------------------------------------------
    @Override
    public void selectAllClick(boolean isChecked) {
        CategoryAdapter categoryAdapter = mCategoryAdapterArray[mModel.getCurrentItem()];
        mModel.getSelectedCategoryList().clear();

        if (isChecked) {
            mModel.getSelectedCategoryList().addAll(categoryAdapter.getCurrentList());
            categoryAdapter.selectAll();
        } else categoryAdapter.deselectAll();

        mModel.setSelectedCategorySizeLiveValue();
    }

    @Override
    public void actionItemClick(int itemId) {
        if (itemId == R.id.menu_action_mode_edit)
            CommonUtil.navigate(mNavController, R.id.mainFragment,
                    MainFragmentDirections.toEditNameDialog(mModel.getSelectedCategoryId(), CATEGORY, false, R.id.nav_graph_main));
        else if (itemId == R.id.menu_action_mode_delete)
            CommonUtil.navigate(mNavController, R.id.mainFragment,
                    MainFragmentDirections.toDeleteSelectedItemDialog(mModel.getSelectedCategorySize(), R.id.nav_graph_main));
    }

    //popup menu click------------------------------------------------------------------------------
    @Override
    public void addCategoryClick() {
        CommonUtil.navigate(mNavController, R.id.mainFragment,
                MainFragmentDirections.toAddDialog(CATEGORY, mModel.getParentCategory(), 0, R.id.nav_graph_main));
    }

    @Override
    public void createFolderClick() {
    }

    @Override
    public void sortClick() {
        CommonUtil.navigate(mNavController, R.id.mainFragment,
                MainFragmentDirections.toSortDialog(mModel.getSort(), MAIN_FRAGMENT, R.id.nav_graph_main));
    }

    @Override
    public void recycleBinClick() {
        CommonUtil.navigate(mNavController, R.id.mainFragment,
                MainFragmentDirections.toRecycleBinActivity());
    }
}