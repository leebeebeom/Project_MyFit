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
import java.util.Arrays;
import java.util.List;

import static com.example.project_myfit.util.MyFitConstant.ACTION_MODE;
import static com.example.project_myfit.util.MyFitConstant.BOTTOM;
import static com.example.project_myfit.util.MyFitConstant.CATEGORY;
import static com.example.project_myfit.util.MyFitConstant.ETC;
import static com.example.project_myfit.util.MyFitConstant.MAIN_FRAGMENT;
import static com.example.project_myfit.util.MyFitConstant.NAME_EDIT_CONFIRM;
import static com.example.project_myfit.util.MyFitConstant.OUTER;
import static com.example.project_myfit.util.MyFitConstant.SELECTED_ITEM_DELETE_CONFIRM;
import static com.example.project_myfit.util.MyFitConstant.SORT_CONFIRM;
import static com.example.project_myfit.util.MyFitConstant.TOP;

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
        mBinding.vp.setOffscreenPageLimit(1);

        mActivityBinding = ((MainActivity) requireActivity()).mBinding;

        mActionMode = new ActionModeImpl(getLayoutInflater(), R.menu.menu_action_mode, this, mCategoryAdapterArray)
                .hasViewPager(mBinding.vp, mButtonArray);

        View view = mBinding.getRoot();

        mButtonArray = new MaterialButton[]{mBinding.btnTop, mBinding.btnBottom, mBinding.btnOuter, mBinding.btnEtc};

        LayoutPopupBinding popupMenuBinding = LayoutPopupBinding.inflate(inflater);
        popupMenuBinding.tvCreateFolder.setVisibility(View.GONE);
        mPopupWindow = new PopupWindowImpl(popupMenuBinding, this);
        return view;
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
            mDragSelectListener[i] = new DragSelectImpl().hasScrollView(mBinding.sv);

        return new MainViewPagerAdapter(mCategoryAdapterArray, mDragSelectListener, mTouchHelperArray, this);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (mBinding.sv.getScrollY() == 0) mActivityBinding.fabTop.hide();

        dialogLive();
        categoryLive();
        actionModeTitleLive();
    }

    private void dialogLive() {
        DialogViewModel dialogViewModel = new ViewModelProvider(mNavController.getViewModelStoreOwner(R.id.nav_graph_main))
                .get(DialogViewModel.class);
        dialogViewModel.orderNumberInit();

        dialogViewModel.getBackStackEntryLive().observe(getViewLifecycleOwner(), navBackStackEntry -> {
            nameEditLive(navBackStackEntry);
            sortLive(navBackStackEntry);
            selectedItemDeletedLive(navBackStackEntry);
        });
    }

    private void nameEditLive(@NotNull androidx.navigation.NavBackStackEntry navBackStackEntry) {
        navBackStackEntry.getSavedStateHandle().getLiveData(NAME_EDIT_CONFIRM).observe(navBackStackEntry, o -> {
            if (MyFitVariable.actionMode != null) MyFitVariable.actionMode.finish();
        });
    }

    private void sortLive(@NotNull androidx.navigation.NavBackStackEntry navBackStackEntry) {
        navBackStackEntry.getSavedStateHandle().getLiveData(SORT_CONFIRM).observe(navBackStackEntry, o -> {
            if (o instanceof Integer && mModel.sortChanged((Integer) o)) categoryLive();
        });
    }

    private void selectedItemDeletedLive(@NotNull androidx.navigation.NavBackStackEntry navBackStackEntry) {
        navBackStackEntry.getSavedStateHandle().getLiveData(SELECTED_ITEM_DELETE_CONFIRM).observe(navBackStackEntry, o -> {
            mModel.selectedCategoryDelete();
            if (MyFitVariable.actionMode != null) MyFitVariable.actionMode.finish();
        });
    }

    public void categoryLive() {
        mModel.getCategoryLive(false).observe(getViewLifecycleOwner(), categoryList -> {
            List<List<Category>> list = Arrays.asList(new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
            for (Category category : Sort.categorySort(mModel.getSort(), categoryList)) {
                switch (category.getParentCategory()) {
                    case TOP:
                        list.get(0).add(category);
                        break;
                    case BOTTOM:
                        list.get(1).add(category);
                        break;
                    case OUTER:
                        list.get(2).add(category);
                        break;
                    case ETC:
                        list.get(3).add(category);
                        break;
                }
            }

            String[] parentCategory = {TOP, BOTTOM, OUTER, ETC};
            for (int i = 0; i < mCategoryAdapterArray.length; i++)
                mCategoryAdapterArray[i].setItem(mModel.getSort(), list.get(i),
                        mModel.getFolderParentIdList(parentCategory[i]), mModel.getSizeParentIdList(parentCategory[i]));
        });
    }

    private void actionModeTitleLive() {
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
        mListenerUtil.scrollChangeListener(mBinding.sv, mActivityBinding.fabTop);
        mListenerUtil.fabTopClick(mBinding.sv, mActivityBinding.fabTop);
        vpPageChangeListener();
        buttonClick();
        fabClick();
        restoreActionMode(savedInstanceState);
    }

    private void vpPageChangeListener() {
        mBinding.vp.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                mBinding.sv.smoothScrollTo(0, 0);
                mModel.setCurrentItem(position);
                mButtonArray[position].setChecked(true);
            }
        });
    }

    private void buttonClick() {
        final ColorStateList textOriginColor = mBinding.btnEtc.getTextColors();
        TypedValue typedValue = new TypedValue();
        requireContext().getTheme().resolveAttribute(R.attr.colorControlNormal, typedValue, true);
        int colorControl = typedValue.data;
        requireContext().getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
        int colorPrimary = typedValue.data;

        //버튼 클릭 시 첫번째 호출
        mBinding.toggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            for (int i = 0; i < 4; i++) {
                mButtonArray[i].setBackgroundColor(Color.TRANSPARENT);
                mButtonArray[i].setTextColor(textOriginColor);
            }

            if (checkedId == R.id.btnTop && isChecked) {
                mButtonArray[0].setBackgroundColor(colorControl);
                mButtonArray[0].setTextColor(colorPrimary);
            } else if (checkedId == R.id.btnBottom && isChecked) {
                mButtonArray[1].setBackgroundColor(colorControl);
                mButtonArray[1].setTextColor(colorPrimary);
            } else if (checkedId == R.id.btnOuter && isChecked) {
                mButtonArray[2].setBackgroundColor(colorControl);
                mButtonArray[2].setTextColor(colorPrimary);
            } else {
                mButtonArray[3].setBackgroundColor(colorControl);
                mButtonArray[3].setTextColor(colorPrimary);
            }
        });

        for (int i = 0; i < 4; i++) {
            int finalI = i;
            mButtonArray[i].setOnClickListener(v -> mBinding.vp.setCurrentItem(finalI));
        }

        mButtonArray[mModel.getCurrentItem()].setChecked(true);
    }

    private void fabClick() {
        mActivityBinding.fab.setOnClickListener(v -> {
            if (MyFitVariable.actionMode != null) MyFitVariable.actionMode.finish();
            mNavController.navigate(MainFragmentDirections.actionMainFragmentToSearchActivity());
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
            mNavController.navigate(MainFragmentDirections.actionMainFragmentToSearchActivity());
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
            mNavController.navigate(MainFragmentDirections.actionMainFragmentToListFragment(category.getId(), 0, category.getParentCategory()));
        else {
            checkBox.setChecked(!checkBox.isChecked());
            mModel.categorySelected(category, checkBox.isChecked(), mCategoryAdapterArray);
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
        mListenerUtil.autoScroll(mBinding.sv, upDownStop);
    }

    //action mode-----------------------------------------------------------------------------------
    @Override
    public void selectAllClick(boolean isChecked) {
        mModel.selectAllClick(isChecked, mCategoryAdapterArray);
    }

    @Override
    public void actionItemClick(int itemId) {
        if (itemId == R.id.menu_action_mode_edit)
            mNavController.navigate(MainFragmentDirections.actionMainFragmentToNameEditDialog(mModel.getSelectedCategoryId(), CATEGORY, false));
        else if (itemId == R.id.menu_action_mode_delete)
            mNavController.navigate(MainFragmentDirections.actionMainFragmentToSelectedItemDeleteDialog(mModel.getSelectedCategorySize()));
    }

    //popup menu click------------------------------------------------------------------------------
    @Override
    public void addCategoryClick() {
        mNavController.navigate(MainFragmentDirections.actionMainFragmentToAddDialog(CATEGORY, mModel.getParentCategory(), 0));
    }

    @Override
    public void createFolderClick() {
    }

    @Override
    public void sortClick() {
        mNavController.navigate(MainFragmentDirections.actionMainFragmentToSortDialog(mModel.getSort(), MAIN_FRAGMENT));
    }

    @Override
    public void recycleBinClick() {
        mNavController.navigate(MainFragmentDirections.actionMainFragmentToRecycleBinActivity());
    }
}