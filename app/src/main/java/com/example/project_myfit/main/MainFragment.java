package com.example.project_myfit.main;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
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
import android.widget.PopupWindow;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.project_myfit.DragCallBackList;
import com.example.project_myfit.NavigationViewModel;
import com.example.project_myfit.R;
import com.example.project_myfit.data.model.Category;
import com.example.project_myfit.databinding.ActionModeTitleBinding;
import com.example.project_myfit.databinding.FragmentMainBinding;
import com.example.project_myfit.databinding.MainPopupMenuBinding;
import com.example.project_myfit.main.adapter.CategoryAdapter;
import com.example.project_myfit.main.adapter.MainViewPagerAdapter;
import com.example.project_myfit.util.ListenerZip;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.michaelflisar.dragselectrecyclerview.DragSelectTouchListener;

import org.jetbrains.annotations.NotNull;

import static com.example.project_myfit.MyFitConstant.ACTION_MODE;
import static com.example.project_myfit.MyFitConstant.ACTION_MODE_OFF;
import static com.example.project_myfit.MyFitConstant.ACTION_MODE_ON;
import static com.example.project_myfit.MyFitConstant.CATEGORY;
import static com.example.project_myfit.MyFitConstant.DOWN;
import static com.example.project_myfit.MyFitConstant.MAIN_FRAGMENT;
import static com.example.project_myfit.MyFitConstant.NAME_EDIT_CONFIRM_CLICK;
import static com.example.project_myfit.MyFitConstant.SELECTED_ITEM_DELETE_CONFIRM_CLICK;
import static com.example.project_myfit.MyFitConstant.SORT_CONFIRM_CLICK;
import static com.example.project_myfit.MyFitConstant.SORT_CUSTOM;
import static com.example.project_myfit.MyFitConstant.SORT_MAIN;
import static com.example.project_myfit.MyFitConstant.STOP;
import static com.example.project_myfit.MyFitConstant.UP;

//TODO 휴지통

public class MainFragment extends Fragment implements MainViewPagerAdapter.MainDragAutoScrollListener, CategoryAdapter.CategoryAdapterListener {

    private MainViewModel mModel;
    private FragmentMainBinding mBinding;
    private PopupWindow mPopupWindow;
    private boolean mIsDragging, mActionModeOn, mIsDragSelecting, mScrollEnable;
    private ActionMode mActionMode;
    private ActionModeTitleBinding mActionModeTitleBinding;
    private MenuItem mEditMenu, mDeletedMenu;
    private MainViewPagerAdapter mViewPagerAdapter;
    private ItemTouchHelper[] mTouchHelperArray;
    private CategoryAdapter[] mCategoryAdapterArray;
    private DragSelectTouchListener mSelectListener;
    private MaterialButton[] mButtonArray;
    private int mSort;
    private NavController mNavController;
    private final ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(@NotNull ActionMode mode, Menu menu) {
            viewPagerSetEnable(false);

            mActionMode = mode;
            mActionModeOn = true;

            mode.getMenuInflater().inflate(R.menu.action_mode, menu);
            mode.setCustomView(mActionModeTitleBinding.getRoot());

            mCategoryAdapterArray[mModel.getCurrentItem()].setActionModeState(ACTION_MODE_ON);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, @NotNull Menu menu) {
            mActionModeTitleBinding.actionModeSelectAll.setOnClickListener(v ->
                    mModel.selectAllClick(((MaterialCheckBox) v).isChecked(), mCategoryAdapterArray[mModel.getCurrentItem()]));

            mEditMenu = menu.getItem(0);
            menu.getItem(1).setVisible(false);
            mDeletedMenu = menu.getItem(2);
            return true;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, @NotNull MenuItem item) {
            if (item.getItemId() == R.id.action_mode_edit)
                mNavController.navigate(MainFragmentDirections.actionMainFragmentToNameEditDialog(mModel.getSelectedCategoryId(), CATEGORY, false));
            else if (item.getItemId() == R.id.action_mode_del)
                mNavController.navigate(MainFragmentDirections.actionMainFragmentToSelectedItemDeleteDialog(mModel.getSelectedCategorySize()));
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            viewPagerSetEnable(true);

            mActionMode = null;
            mActionModeOn = false;

            mCategoryAdapterArray[mModel.getCurrentItem()].setActionModeState(ACTION_MODE_OFF);

            mActionModeTitleBinding.actionModeSelectAll.setChecked(false);
            ((ViewGroup) mActionModeTitleBinding.getRoot().getParent()).removeAllViews();
        }
    };

    private void viewPagerSetEnable(boolean enable) {
        mBinding.viewPager.setUserInputEnabled(enable);
        mBinding.btnTop.setEnabled(enable);
        mBinding.btnBottom.setEnabled(enable);
        mBinding.btnOuter.setEnabled(enable);
        mBinding.btnEtc.setEnabled(enable);
    }

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mModel = new ViewModelProvider(this).get(MainViewModel.class);
        setHasOptionsMenu(true);
        mModel.orderNumberInit();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mNavController = NavHostFragment.findNavController(this);
        setNavigationLive();

        mBinding = FragmentMainBinding.inflate(inflater, container, false);
        mActionModeTitleBinding = ActionModeTitleBinding.inflate(inflater);
        View view = mBinding.getRoot();

        SharedPreferences sortPreferences = requireActivity().getSharedPreferences(SORT_MAIN, Context.MODE_PRIVATE);
        mSort = sortPreferences.getInt(SORT_MAIN, SORT_CUSTOM);

        MainPopupMenuBinding popupMenuBinding = MainPopupMenuBinding.inflate(inflater);
        popupMenuClick(popupMenuBinding);
        mPopupWindow = new PopupWindow(popupMenuBinding.getRoot(), ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mPopupWindow.setOutsideTouchable(true);

        mBinding.mainTopFab.setOnClickListener(v -> {
            mBinding.mainScrollView.scrollTo(0, 0);
            mBinding.mainScrollView.smoothScrollTo(0, 0);
        });
        return view;
    }

    private void setNavigationLive() {
        NavigationViewModel navigationViewModel = new ViewModelProvider(mNavController.getViewModelStoreOwner(R.id.main_nav_graph))
                .get(NavigationViewModel.class);

        navigationViewModel.getBackStackEntryLive().observe(getViewLifecycleOwner(), navBackStackEntry -> {
            //category name edit confirm
            navBackStackEntry.getSavedStateHandle().getLiveData(NAME_EDIT_CONFIRM_CLICK).observe(navBackStackEntry, o -> {
                if (mActionMode != null) mActionMode.finish();
            });

            //sort confirm
            navBackStackEntry.getSavedStateHandle().getLiveData(SORT_CONFIRM_CLICK).observe(navBackStackEntry, o -> {
                int sort = (int) o;
                if (mSort != sort) {
                    mSort = sort;
                    SharedPreferences.Editor editor = requireActivity().getSharedPreferences(SORT_MAIN, SORT_CUSTOM).edit();
                    editor.putInt(SORT_MAIN, sort);
                    editor.apply();
                    setCategoryLive();
                }
            });

            //selected item delete confirm
            navBackStackEntry.getSavedStateHandle().getLiveData(SELECTED_ITEM_DELETE_CONFIRM_CLICK).observe(navBackStackEntry, o -> {
                if (mActionMode != null) mActionMode.finish();
                mModel.selectedCategoryDelete();
            });
        });
    }

    private void popupMenuClick(@NotNull MainPopupMenuBinding binding) {
        binding.addFolder.setOnClickListener(v -> {
            mNavController.navigate(MainFragmentDirections.actionMainFragmentToAddDialog(mModel.getParentCategory(), CATEGORY, 0));
            mPopupWindow.dismiss();
        });
        binding.sort.setOnClickListener(v -> {
            mNavController.navigate(MainFragmentDirections.actionMainFragmentToSortDialog(mSort, MAIN_FRAGMENT));
            mPopupWindow.dismiss();
        });
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBinding.viewPager.setAdapter(getViewPagerAdapter());

        requireActivity().findViewById(R.id.activity_fab).setOnClickListener(v -> {
            if (mActionMode != null) mActionMode.finish();
            mNavController.navigate(R.id.action_mainFragment_to_searchActivity);
        });

        ListenerZip listenerZip = new ListenerZip();
        listenerZip.keyboardShowingListener(view, requireActivity().findViewById(R.id.activity_fab), requireActivity().findViewById(R.id.bottom_app_bar));
    }

    @NotNull
    private MainViewPagerAdapter getViewPagerAdapter() {
        mCategoryAdapterArray = new CategoryAdapter[4];
        for (int i = 0; i < 4; i++)
            mCategoryAdapterArray[i] = new CategoryAdapter(mModel, this);

        mTouchHelperArray = new ItemTouchHelper[4];
        for (int i = 0; i < 4; i++)
            mTouchHelperArray[i] = new ItemTouchHelper(new DragCallBackList(mCategoryAdapterArray[i], CATEGORY));

        return mViewPagerAdapter = new MainViewPagerAdapter(mCategoryAdapterArray, dragSelectListenerInit(), mTouchHelperArray, this);
    }

    private DragSelectTouchListener dragSelectListenerInit() {
        DragSelectTouchListener.OnAdvancedDragSelectListener listener = new DragSelectTouchListener.OnAdvancedDragSelectListener() {
            @Override
            public void onSelectionStarted(int i) {
                mBinding.mainScrollView.setScrollable(false);
                mIsDragSelecting = true;
                RecyclerView recyclerView = mBinding.viewPager.getChildAt(0).findViewById(R.id.main_recyclerView);
                RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForLayoutPosition(i);
                if (viewHolder != null) viewHolder.itemView.callOnClick();
            }

            @Override
            public void onSelectionFinished(int i) {
                mBinding.mainScrollView.setScrollable(true);
                mIsDragSelecting = false;
            }

            @Override
            public void onSelectChange(int i, int i1, boolean b) {
                RecyclerView recyclerView = mBinding.viewPager.getChildAt(0).findViewById(R.id.main_recyclerView);
                for (int j = i; j <= i1; j++) {
                    RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForLayoutPosition(j);
                    if (viewHolder != null) viewHolder.itemView.callOnClick();
                }
            }
        };
        mSelectListener = new DragSelectTouchListener().withSelectListener(listener);
        return mSelectListener;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setScrollChangeListener();
        setButtonClickListener();
        viewPagerChangeListener();
        setCategoryLive();
        selectedItemAmountLive();

        if (savedInstanceState == null) mBinding.viewPager.setCurrentItem(0);
        else if (savedInstanceState.getBoolean(ACTION_MODE)) {
            mButtonArray[mModel.getCurrentItem()].setChecked(true);
            mCategoryAdapterArray[mModel.getCurrentItem()].setSelectedCategoryList(mModel.getSelectedCategoryList());
            ((AppCompatActivity) requireActivity()).startSupportActionMode(mActionModeCallback);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setScrollChangeListener() {
        mBinding.mainScrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (oldScrollY < scrollY) mBinding.mainTopFab.show();
            else if (scrollY == 0) mBinding.mainTopFab.hide();

            if ((mIsDragSelecting || mIsDragging) && mScrollEnable && scrollY > oldScrollY)
                v.postDelayed(() -> v.scrollBy(0, 1), 50);
            else if ((mIsDragSelecting || mIsDragging) && mScrollEnable && scrollY < oldScrollY)
                v.postDelayed(() -> v.scrollBy(0, -1), 50);
        });
    }

    private void setButtonClickListener() {
        mButtonArray = new MaterialButton[]{mBinding.btnTop, mBinding.btnBottom, mBinding.btnOuter, mBinding.btnEtc};

        final ColorStateList textOriginColor = mBinding.btnEtc.getTextColors();
        TypedValue typedValue = new TypedValue();
        requireContext().getTheme().resolveAttribute(R.attr.colorControlNormal, typedValue, true);
        int colorControl = typedValue.data;
        requireContext().getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
        int colorPrimary = typedValue.data;

        mBinding.toggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            //버튼 클릭 시 첫번째 호출
            for (int i = 0; i < 4; i++) {
                mButtonArray[i].setBackgroundColor(Color.TRANSPARENT);
                mButtonArray[i].setTextColor(textOriginColor);
            }

            if (checkedId == R.id.btn_top && isChecked) {
                mButtonArray[0].setBackgroundColor(colorControl);
                mButtonArray[0].setTextColor(colorPrimary);
                mModel.setCurrentItem(0);
            } else if (checkedId == R.id.btn_bottom && isChecked) {
                mButtonArray[1].setBackgroundColor(colorControl);
                mButtonArray[1].setTextColor(colorPrimary);
                mModel.setCurrentItem(1);
            } else if (checkedId == R.id.btn_outer && isChecked) {
                mButtonArray[2].setBackgroundColor(colorControl);
                mButtonArray[2].setTextColor(colorPrimary);
                mModel.setCurrentItem(2);
            } else {
                mButtonArray[3].setBackgroundColor(colorControl);
                mButtonArray[3].setTextColor(colorPrimary);
                mModel.setCurrentItem(3);
            }
        });

        for (int i = 0; i < 4; i++) {
            int finalI = i;
            mButtonArray[i].setOnClickListener(v -> mBinding.viewPager.setCurrentItem(finalI, false));
        }
    }

    private void viewPagerChangeListener() {
        mBinding.viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                mBinding.mainScrollView.smoothScrollTo(0, 0);
                //스크롤 오류 해결
                if (mActionMode == null)
                    mCategoryAdapterArray[position].setActionModeState(0);

                mModel.setCurrentItem(position);
                mButtonArray[position].setChecked(true);
            }
        });
    }

    public void setCategoryLive() {
        mModel.getCategoryLive().observe(getViewLifecycleOwner(), categoryList -> mViewPagerAdapter.setItem(mSort, categoryList, mModel));
    }

    private void selectedItemAmountLive() {
        mModel.getSelectedCategorySizeLive().observe(getViewLifecycleOwner(), integer -> {
            String title = integer + getString(R.string.item_selected);
            mActionModeTitleBinding.actionModeTitle.setText(title);
            if (mActionMode != null) {
                mEditMenu.setVisible(integer == 1);
                mDeletedMenu.setVisible(integer > 0);
            }

            mActionModeTitleBinding.actionModeSelectAll.setChecked(mCategoryAdapterArray[mModel.getCurrentItem()].getCurrentList().size() == integer);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mActionMode != null) mActionMode.finish();
        mBinding = null;
        mViewPagerAdapter = null;
        mSelectListener = null;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull @NotNull Menu menu, @NonNull @NotNull MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull @NotNull MenuItem item) {
        if (item.getItemId() == R.id.menu_main_popup) {
            mPopupWindow.showAsDropDown(requireActivity().findViewById(R.id.menu_main_popup));
            return true;
        } else if (item.getItemId() == R.id.menu_main_search) {
            mNavController.navigate(R.id.action_mainFragment_to_searchActivity);
            return true;
        }
        return false;
    }

    @Override
    public void onSaveInstanceState(@NonNull @NotNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(ACTION_MODE, mActionModeOn);
    }

    //category adapter------------------------------------------------------------------------------
    @Override
    public void onCategoryCardViewClick(Category category, MaterialCheckBox checkBox) {
        if (mActionMode == null)
            mNavController.navigate(MainFragmentDirections.actionMainFragmentToListFragment(category.getId(), 0));
        else {
            checkBox.setChecked(!checkBox.isChecked());
            mCategoryAdapterArray[mModel.getCurrentItem()].categorySelected(category.getId());
            mModel.categorySelected(category, checkBox.isChecked());
        }
    }

    @Override
    public void onCategoryCardViewLongClick(int position) {
        if (mActionMode == null) {
            mModel.getSelectedCategoryList().clear();
            ((AppCompatActivity) requireActivity()).startSupportActionMode(mActionModeCallback);
        }
        mSelectListener.startDragSelection(position);
    }

    @Override
    public void onCategoryDragHandleTouch(RecyclerView.ViewHolder viewHolder) {
        if (!mIsDragging) {
            mIsDragging = true;
            mTouchHelperArray[mModel.getCurrentItem()].startDrag(viewHolder);
        } else
            mIsDragging = false;
    }
    //----------------------------------------------------------------------------------------------

    @Override
    public void dragAutoScroll(int upDownStop) {
        if ((mIsDragSelecting || mIsDragging))
            if (upDownStop == DOWN) {
                mBinding.mainScrollView.scrollBy(0, 1);
                mScrollEnable = true;
            } else if (upDownStop == UP) {
                mBinding.mainScrollView.scrollBy(0, -1);
                mScrollEnable = true;
            } else if (upDownStop == STOP) {
                mBinding.mainScrollView.scrollBy(0, 0);
                mScrollEnable = false;
            }
    }
}