package com.example.project_myfit.ui.main;

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
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.project_myfit.MainActivityViewModel;
import com.example.project_myfit.R;
import com.example.project_myfit.databinding.ActionModeTitleBinding;
import com.example.project_myfit.databinding.FragmentMainBinding;
import com.example.project_myfit.databinding.MainPopupMenuBinding;
import com.example.project_myfit.dialog.AddCategoryDialog;
import com.example.project_myfit.dialog.CategoryNameEditDialog;
import com.example.project_myfit.dialog.SelectedItemDeleteDialog;
import com.example.project_myfit.dialog.SortDialog;
import com.example.project_myfit.ui.main.adapter.CategoryAdapter;
import com.example.project_myfit.ui.main.adapter.MainDragCallBack;
import com.example.project_myfit.ui.main.adapter.MainViewPagerAdapter;
import com.example.project_myfit.ui.main.database.Category;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.michaelflisar.dragselectrecyclerview.DragSelectTouchListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.example.project_myfit.MyFitConstant.ACTION_MODE;
import static com.example.project_myfit.MyFitConstant.ACTION_MODE_OFF;
import static com.example.project_myfit.MyFitConstant.ACTION_MODE_ON;
import static com.example.project_myfit.MyFitConstant.BOTTOM;
import static com.example.project_myfit.MyFitConstant.CATEGORY_ADD_DIALOG;
import static com.example.project_myfit.MyFitConstant.CATEGORY_EDIT_DIALOG;
import static com.example.project_myfit.MyFitConstant.CURRENT_ITEM;
import static com.example.project_myfit.MyFitConstant.DELETE_DIALOG;
import static com.example.project_myfit.MyFitConstant.ETC;
import static com.example.project_myfit.MyFitConstant.OUTER;
import static com.example.project_myfit.MyFitConstant.SORT_CREATE;
import static com.example.project_myfit.MyFitConstant.SORT_CREATE_REVERSE;
import static com.example.project_myfit.MyFitConstant.SORT_DIALOG;
import static com.example.project_myfit.MyFitConstant.SORT_MAIN;
import static com.example.project_myfit.MyFitConstant.SORT_NAME;
import static com.example.project_myfit.MyFitConstant.SORT_NAME_REVERSE;
import static com.example.project_myfit.MyFitConstant.TOP;

//TODO 휴지통
//TODO 액션모드 딸깍거리는거 해결좀

public class MainFragment extends Fragment implements AddCategoryDialog.AddCategoryConfirmClick, MainViewPagerAdapter.MainDragAutoScrollListener,
        SortDialog.SortConfirmClick, CategoryAdapter.CategoryAdapterListener, CategoryNameEditDialog.CategoryNameEditConfirmClick, SelectedItemDeleteDialog.SelectedItemDeleteConfirmClick {
    private MainViewModel mModel;
    private FragmentMainBinding mBinding;
    private MainActivityViewModel mActivityModel;
    private PopupWindow mPopupWindow;
    private SharedPreferences mSortPreference;
    private int mSort;
    private boolean isDragging, mActionModeOn, isDragSelecting, mScrollEnable;
    private ActionMode mActionMode;
    private ActionModeTitleBinding mActionModeTitleBinding;
    private MenuItem mEditMenu, mDeletedMenu;
    private MainViewPagerAdapter mViewPagerAdapter;
    private List<Category> mTopList, mBottomList, mOuterList, mEtcList;
    private List<ItemTouchHelper> mTouchHelperList;
    private List<CategoryAdapter> mAdapterList;
    private final ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(@NotNull ActionMode mode, Menu menu) {
            mBinding.viewPager.setUserInputEnabled(false);

            mActionMode = mode;
            mActionModeOn = true;

            mode.getMenuInflater().inflate(R.menu.list_action_mode, menu);
            mode.setCustomView(mActionModeTitleBinding.getRoot());

            mBinding.btnTop.setEnabled(false);
            mBinding.btnBottom.setEnabled(false);
            mBinding.btnOuter.setEnabled(false);
            mBinding.btnEtc.setEnabled(false);
            mAdapterList.get(mBinding.viewPager.getCurrentItem()).setActionModeState(ACTION_MODE_ON);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, @NotNull Menu menu) {
            mActionModeTitleBinding.actionModeSelectAll.setOnClickListener(v -> {
                mModel.getSelectedItem().clear();
                if (mActionModeTitleBinding.actionModeSelectAll.isChecked()) {
                    mModel.getSelectedItem().addAll(mAdapterList.get(mBinding.viewPager.getCurrentItem()).getCurrentList());
                    mAdapterList.get(mBinding.viewPager.getCurrentItem()).selectAll();
                } else mAdapterList.get(mBinding.viewPager.getCurrentItem()).deselectAll();
                mModel.setSelectedAmount();
            });
            mEditMenu = menu.getItem(0);
            menu.getItem(1).setVisible(false);
            mDeletedMenu = menu.getItem(2);
            return true;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, @NotNull MenuItem item) {
            if (item.getItemId() == R.id.action_mode_del) {
                if (mModel.getSelectedAmount().getValue() != null)
                    showDialog(SelectedItemDeleteDialog.getInstance(mModel.getSelectedAmount().getValue()), DELETE_DIALOG);
            } else if (item.getItemId() == R.id.action_mode_edit)
                showDialog(CategoryNameEditDialog.getInstance(mModel.getSelectedItem().get(0).getCategory()), CATEGORY_EDIT_DIALOG);
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mBinding.viewPager.setUserInputEnabled(true);

            mActionMode = null;
            mActionModeOn = false;
            mAdapterList.get(mBinding.viewPager.getCurrentItem()).setActionModeState(ACTION_MODE_OFF);
            mModel.setSelectedPosition(mAdapterList.get(mBinding.viewPager.getCurrentItem()).getSelectedPosition());

            mActionModeTitleBinding.actionModeSelectAll.setChecked(false);
            ((ViewGroup) mActionModeTitleBinding.getRoot().getParent()).removeAllViews();

            mBinding.btnTop.setEnabled(true);
            mBinding.btnBottom.setEnabled(true);
            mBinding.btnOuter.setEnabled(true);
            mBinding.btnEtc.setEnabled(true);
        }
    };
    private DragSelectTouchListener mSelectListener;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mModel = new ViewModelProvider(this).get(MainViewModel.class);
        mActivityModel = new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        dragSelectListenerInit();
        mBinding = FragmentMainBinding.inflate(inflater, container, false);
        mActionModeTitleBinding = ActionModeTitleBinding.inflate(inflater);
        View view = mBinding.getRoot();
        mBinding.viewPager.setAdapter(getViewPagerAdapter());

        mSortPreference = requireActivity().getSharedPreferences(SORT_MAIN, Context.MODE_PRIVATE);
        mSort = mSortPreference.getInt(SORT_MAIN, 0);
        mModel.setSort(mSort);

        MainPopupMenuBinding popupMenuBinding = MainPopupMenuBinding.inflate(inflater);
        popupMenuClick(popupMenuBinding);
        mPopupWindow = new PopupWindow(popupMenuBinding.getRoot(), ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mPopupWindow.setOutsideTouchable(true);

        mBinding.mainScrollView.setOnScrollChangeListener((View.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (oldScrollY < scrollY) mBinding.mainFab.show();
            else if (scrollY == 0) mBinding.mainFab.hide();
        });

        mBinding.mainFab.setOnClickListener(v -> {
            mBinding.mainScrollView.scrollTo(0, 0);
            mBinding.mainScrollView.smoothScrollTo(0, 0);
        });
        return view;
    }

    private void dragSelectListenerInit() {
        DragSelectTouchListener.OnAdvancedDragSelectListener selectListener = new DragSelectTouchListener.OnAdvancedDragSelectListener() {
            @Override
            public void onSelectionStarted(int i) {
                mBinding.mainScrollView.setScrollable(false);
                isDragSelecting = true;
                RecyclerView recyclerView = mBinding.viewPager.getChildAt(0).findViewById(R.id.main_recyclerView);
                RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForLayoutPosition(i);
                if (viewHolder != null) viewHolder.itemView.callOnClick();
            }

            @Override
            public void onSelectionFinished(int i) {
                mBinding.mainScrollView.setScrollable(true);
                isDragSelecting = false;
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
        mSelectListener = new DragSelectTouchListener().withSelectListener(selectListener);
    }

    private MainViewPagerAdapter getViewPagerAdapter() {
        CategoryAdapter topAdapter = new CategoryAdapter(mModel, this, 0);
        CategoryAdapter bottomAdapter = new CategoryAdapter(mModel, this, 1);
        CategoryAdapter outerAdapter = new CategoryAdapter(mModel, this, 2);
        CategoryAdapter etcAdapter = new CategoryAdapter(mModel, this, 3);
        mAdapterList = Arrays.asList(topAdapter, bottomAdapter, outerAdapter, etcAdapter);

        ItemTouchHelper topTouchHelper = new ItemTouchHelper(new MainDragCallBack(topAdapter));
        ItemTouchHelper bottomTouchHelper = new ItemTouchHelper(new MainDragCallBack(bottomAdapter));
        ItemTouchHelper outerTouchHelper = new ItemTouchHelper(new MainDragCallBack(outerAdapter));
        ItemTouchHelper etcTouchHelper = new ItemTouchHelper(new MainDragCallBack(etcAdapter));
        mTouchHelperList = Arrays.asList(topTouchHelper, bottomTouchHelper, outerTouchHelper, etcTouchHelper);

        mViewPagerAdapter = new MainViewPagerAdapter(mAdapterList, mSelectListener, mTouchHelperList);
        mViewPagerAdapter.setOnCategoryAdapterListener(this);
        return mViewPagerAdapter;
    }

    private void popupMenuClick(@NotNull MainPopupMenuBinding binding) {
        binding.addFolder.setOnClickListener(v -> {
            showDialog(new AddCategoryDialog(), CATEGORY_ADD_DIALOG);
            mPopupWindow.dismiss();
        });
        binding.sort.setOnClickListener(v -> {
            showDialog(SortDialog.getInstance(mSort), SORT_DIALOG);
            mPopupWindow.dismiss();
        });
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        requireActivity().findViewById(R.id.activity_fab).setOnClickListener(v -> {
            if (mActionMode != null) mActionMode.finish();
            Navigation.findNavController(requireActivity(), R.id.host_fragment).navigate(R.id.action_mainFragment_to_searchActivity);
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setLiveData();
        setAutoScroll();
        setToggleGroup();
        if (savedInstanceState == null) mBinding.btnTop.setChecked(true);
        pagerChangeListener();
        selectedItemAmountLive();
        actionModeRecreate(savedInstanceState);
    }

    private void setLiveData() {
        mModel.getRepository().getCategoryLive().observe(getViewLifecycleOwner(), categoryList -> {
            if (mSort == SORT_CREATE)
                categoryList.sort((o1, o2) -> Long.compare(o2.getId(), o1.getId()));
            else if (mSort == SORT_CREATE_REVERSE)
                categoryList.sort((o1, o2) -> Long.compare(o1.getId(), o2.getId()));
            else if (mSort == SORT_NAME)
                categoryList.sort((o1, o2) -> o1.getCategory().compareTo(o2.getCategory()));
            else if (mSort == SORT_NAME_REVERSE)
                categoryList.sort((o1, o2) -> o2.getCategory().compareTo(o1.getCategory()));

            mTopList = new ArrayList<>();
            mBottomList = new ArrayList<>();
            mOuterList = new ArrayList<>();
            mEtcList = new ArrayList<>();

            for (Category category : categoryList) {
                switch (category.getParentCategory()) {
                    case TOP:
                        mTopList.add(category);
                        break;
                    case BOTTOM:
                        mBottomList.add(category);
                        break;
                    case OUTER:
                        mOuterList.add(category);
                        break;
                    case ETC:
                        mEtcList.add(category);
                        break;
                }
            }

            mAdapterList.get(0).submitList(mTopList, mModel.getRepository().getFolderFolderIdByParent(TOP), mModel.getRepository().getSizeFolderIdByParent(TOP));
            mAdapterList.get(1).submitList(mBottomList, mModel.getRepository().getFolderFolderIdByParent(BOTTOM), mModel.getRepository().getSizeFolderIdByParent(BOTTOM));
            mAdapterList.get(2).submitList(mOuterList, mModel.getRepository().getFolderFolderIdByParent(OUTER), mModel.getRepository().getSizeFolderIdByParent(OUTER));
            mAdapterList.get(3).submitList(mEtcList, mModel.getRepository().getFolderFolderIdByParent(ETC), mModel.getRepository().getSizeFolderIdByParent(ETC));

            mViewPagerAdapter.setSort(mSort);
            mViewPagerAdapter.notifyDataSetChanged();//for noData
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setAutoScroll() {
        mBinding.mainScrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if ((isDragSelecting || isDragging) && mScrollEnable && scrollY > oldScrollY)
                v.postDelayed(() -> v.scrollBy(0, 1), 50);
            else if ((isDragSelecting || isDragging) && mScrollEnable && scrollY < oldScrollY)
                v.postDelayed(() -> v.scrollBy(0, -1), 50);
        });
    }

    private void setToggleGroup() {
        final ColorStateList textOriginColor = mBinding.btnEtc.getTextColors();
        TypedValue typedValue = new TypedValue();
        requireContext().getTheme().resolveAttribute(R.attr.colorControlNormal, typedValue, true);
        int colorControl = typedValue.data;
        requireContext().getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
        int colorPrimary = typedValue.data;

        mBinding.toggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            mBinding.btnTop.setBackgroundColor(Color.TRANSPARENT);
            mBinding.btnTop.setTextColor(textOriginColor);
            mBinding.btnBottom.setBackgroundColor(Color.TRANSPARENT);
            mBinding.btnBottom.setTextColor(textOriginColor);
            mBinding.btnOuter.setBackgroundColor(Color.TRANSPARENT);
            mBinding.btnOuter.setTextColor(textOriginColor);
            mBinding.btnEtc.setBackgroundColor(Color.TRANSPARENT);
            mBinding.btnEtc.setTextColor(textOriginColor);

            if (checkedId == R.id.btn_top && isChecked) {
                mBinding.viewPager.setCurrentItem(0, true);
                mBinding.btnTop.setBackgroundColor(colorControl);
                mBinding.btnTop.setTextColor(colorPrimary);
            } else if (checkedId == R.id.btn_bottom && isChecked) {
                mBinding.viewPager.setCurrentItem(1, true);
                mBinding.btnBottom.setBackgroundColor(colorControl);
                mBinding.btnBottom.setTextColor(colorPrimary);
            } else if (checkedId == R.id.btn_outer && isChecked) {
                mBinding.viewPager.setCurrentItem(2, true);
                mBinding.btnOuter.setBackgroundColor(colorControl);
                mBinding.btnOuter.setTextColor(colorPrimary);
            } else if (checkedId == R.id.btn_etc && isChecked) {
                mBinding.viewPager.setCurrentItem(3, true);
                mBinding.btnEtc.setBackgroundColor(colorControl);
                mBinding.btnEtc.setTextColor(colorPrimary);
            }
        });
    }

    private void pagerChangeListener() {
        mBinding.viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                if (position == 0) mBinding.btnTop.setChecked(true);
                else if (position == 1) mBinding.btnBottom.setChecked(true);
                else if (position == 2) mBinding.btnOuter.setChecked(true);
                else if (position == 3) mBinding.btnEtc.setChecked(true);
            }
        });
    }

    private void selectedItemAmountLive() {
        mModel.getSelectedAmount().observe(getViewLifecycleOwner(), integer -> {
            String title = integer + getString(R.string.item_selected);
            mActionModeTitleBinding.actionModeTitle.setText(title);
            if (mActionMode != null) {
                mEditMenu.setVisible(integer == 1);
                mDeletedMenu.setVisible(integer > 0);
            }

            int allItemSize = mAdapterList.get(mBinding.viewPager.getCurrentItem()).getCurrentList().size();
            mActionModeTitleBinding.actionModeSelectAll.setChecked(allItemSize == integer);
        });
    }

    private void actionModeRecreate(@org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null && savedInstanceState.getBoolean(ACTION_MODE)) {
            switch (savedInstanceState.getInt(CURRENT_ITEM)) {
                case 0:
                    mBinding.btnTop.setChecked(true);
                    break;
                case 1:
                    mBinding.btnBottom.setChecked(true);
                    break;
                case 2:
                    mBinding.btnOuter.setChecked(true);
                    break;
                case 3:
                    mBinding.btnEtc.setChecked(true);
                    break;
            }
            mAdapterList.get(mBinding.viewPager.getCurrentItem()).setSelectedPosition(mModel.getSelectedPosition());
            ((AppCompatActivity) requireActivity()).startSupportActionMode(mActionModeCallback);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mActionMode != null) mActionMode.finish();
        mBinding = null;
        mViewPagerAdapter = null;
        mSelectListener = null;
    }

    private void showDialog(@NotNull DialogFragment dialog, String tag) {
        dialog.setTargetFragment(this, 0);
        dialog.show(getParentFragmentManager(), tag);
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
            Navigation.findNavController(requireActivity(), R.id.host_fragment).navigate(R.id.action_mainFragment_to_searchActivity);
            return true;
        }
        return false;
    }

    @Override
    public void addCategoryConfirmClick(@NotNull String categoryName) {
        int largestOrder = mModel.getRepository().getCategoryLargestOrder() + 1;
        int currentItem = mBinding.viewPager.getCurrentItem();
        String parentCategory = null;
        switch (currentItem) {
            case 0:
                parentCategory = TOP;
                break;
            case 1:
                parentCategory = BOTTOM;
                break;
            case 2:
                parentCategory = OUTER;
                break;
            case 3:
                parentCategory = ETC;
                break;
        }
        mModel.getRepository().categoryInsert(new Category(categoryName.trim(), parentCategory, largestOrder));
    }

    @Override
    public void sortConfirmClick(int sort) {
        mSort = sort;
        mModel.setSort(mSort);
        setLiveData();
        SharedPreferences.Editor editor = mSortPreference.edit();
        editor.putInt(SORT_MAIN, mSort);
        editor.apply();
    }

    @Override
    public void onSaveInstanceState(@NonNull @NotNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(ACTION_MODE, mActionModeOn);
        if (mBinding != null)
            outState.putInt(CURRENT_ITEM, mBinding.viewPager.getCurrentItem());
    }

    @Override
    public void onCategoryCardViewClick(Category category, MaterialCheckBox checkBox, int position, int viewPagerPosition) {
        if (mActionMode == null) {
            mActivityModel.setCategory(category);
            Navigation.findNavController(requireActivity(), R.id.host_fragment).navigate(R.id.action_mainFragment_to_listFragment);
        } else {
            checkBox.setChecked(!checkBox.isChecked());
            mModel.categorySelected(category, checkBox.isChecked(), position, viewPagerPosition, mAdapterList);
        }
    }

    @Override
    public void onCategoryCardViewLongClick(int position) {
        if (mActionMode == null) {
            mModel.getSelectedItem().clear();
            ((AppCompatActivity) requireActivity()).startSupportActionMode(mActionModeCallback);
        }
        mSelectListener.startDragSelection(position);
    }

    @Override
    public void onCategoryDragHandleTouch(RecyclerView.ViewHolder viewHolder, int viewPagerPosition) {
        if (mActionMode == null && !isDragging) {
            isDragging = true;
            mTouchHelperList.get(viewPagerPosition).startDrag(viewHolder);
        } else if (isDragging) isDragging = false;
    }

    @Override
    public void categoryNameEditConfirmClick(@NotNull String categoryName) {
        mModel.categoryNameEdit(categoryName.trim());
        mActionMode.finish();
    }

    @Override
    public void selectedItemDeleteConfirmClick() {
        mModel.selectedItemDelete();
        mActionMode.finish();
    }

    @Override
    public void dragAutoScroll(int upDown) {
        if ((isDragSelecting || isDragging))
            if (upDown == 0) {
                mBinding.mainScrollView.scrollBy(0, 1);
                mScrollEnable = true;
            } else if (upDown == 1) {
                mBinding.mainScrollView.scrollBy(0, -1);
                mScrollEnable = true;
            } else if (upDown == 2) {
                mBinding.mainScrollView.scrollBy(0, 0);
                mScrollEnable = false;
            }
    }
}