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
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.project_myfit.MainActivityViewModel;
import com.example.project_myfit.R;
import com.example.project_myfit.data.model.Category;
import com.example.project_myfit.databinding.ActionModeTitleBinding;
import com.example.project_myfit.databinding.FragmentMainBinding;
import com.example.project_myfit.databinding.ItemMainRecyclerBinding;
import com.example.project_myfit.databinding.MainPopupMenuBinding;
import com.example.project_myfit.dialog.AddCategoryDialog;
import com.example.project_myfit.dialog.CategoryNameEditDialog;
import com.example.project_myfit.dialog.SelectedItemDeleteDialog;
import com.example.project_myfit.dialog.SortDialog;
import com.example.project_myfit.main.adapter.CategoryAdapter;
import com.example.project_myfit.main.adapter.MainDragCallBack;
import com.example.project_myfit.main.adapter.MainViewPagerAdapter;
import com.example.project_myfit.util.SelectedItemTreat;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.michaelflisar.dragselectrecyclerview.DragSelectTouchListener;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.example.project_myfit.MyFitConstant.ACTION_MODE;
import static com.example.project_myfit.MyFitConstant.ACTION_MODE_OFF;
import static com.example.project_myfit.MyFitConstant.ACTION_MODE_ON;
import static com.example.project_myfit.MyFitConstant.BOTTOM;
import static com.example.project_myfit.MyFitConstant.CATEGORY_ADD_DIALOG;
import static com.example.project_myfit.MyFitConstant.CATEGORY_EDIT_DIALOG;
import static com.example.project_myfit.MyFitConstant.CURRENT_ITEM;
import static com.example.project_myfit.MyFitConstant.DELETE_DIALOG;
import static com.example.project_myfit.MyFitConstant.DOWN;
import static com.example.project_myfit.MyFitConstant.ETC;
import static com.example.project_myfit.MyFitConstant.OUTER;
import static com.example.project_myfit.MyFitConstant.SORT_CUSTOM;
import static com.example.project_myfit.MyFitConstant.SORT_DIALOG;
import static com.example.project_myfit.MyFitConstant.SORT_MAIN;
import static com.example.project_myfit.MyFitConstant.STOP;
import static com.example.project_myfit.MyFitConstant.TOP;
import static com.example.project_myfit.MyFitConstant.UP;

//TODO 휴지통
//키보드, 바텀앱바

public class MainFragment extends Fragment implements AddCategoryDialog.AddCategoryConfirmClick, MainViewPagerAdapter.MainDragAutoScrollListener,
        SortDialog.SortConfirmClick, CategoryAdapter.CategoryAdapterListener, CategoryNameEditDialog.CategoryNameEditConfirmClick, SelectedItemDeleteDialog.SelectedItemDeleteConfirmClick {

    private MainViewModel mModel;
    private FragmentMainBinding mBinding;
    private MainActivityViewModel mActivityModel;
    private PopupWindow mPopupWindow;
    private SharedPreferences mSortPreference;
    private int mSort;
    private boolean mIsDragging, mActionModeOn, mIsDragSelecting, mScrollEnable;
    private ActionMode mActionMode;
    private ActionModeTitleBinding mActionModeTitleBinding;
    private MenuItem mEditMenu, mDeletedMenu;
    private MainViewPagerAdapter mViewPagerAdapter;
    private ItemTouchHelper[] mTouchHelperArray;
    private CategoryAdapter[] mCategoryAdapterArray;
    private MaterialButton[] mButtonArray;
    private DragSelectTouchListener mSelectListener;
    private LiveData<List<Category>> mMainLive;
    private final ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {
        //all checked
        @Override
        public boolean onCreateActionMode(@NotNull ActionMode mode, Menu menu) {
            //checked
            mBinding.viewPager.setUserInputEnabled(false);

            mActionMode = mode;
            mActionModeOn = true;

            mode.getMenuInflater().inflate(R.menu.action_mode, menu);
            mode.setCustomView(mActionModeTitleBinding.getRoot());

            parentCategoryButtonEnable(mBinding, false);
            mCategoryAdapterArray[mBinding.viewPager.getCurrentItem()].setActionModeState(ACTION_MODE_ON);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, @NotNull Menu menu) {
            //checked
            mActionModeTitleBinding.actionModeSelectAll.setOnClickListener(v -> {
                mModel.getSelectedCategoryList().clear();
                if (mActionModeTitleBinding.actionModeSelectAll.isChecked()) {
                    mModel.getSelectedCategoryList().addAll(mCategoryAdapterArray[mBinding.viewPager.getCurrentItem()].getCurrentList());
                    mCategoryAdapterArray[mBinding.viewPager.getCurrentItem()].selectAll();
                } else mCategoryAdapterArray[mBinding.viewPager.getCurrentItem()].deselectAll();
                mModel.setSelectedAmount();
            });

            mEditMenu = menu.getItem(0);
            menu.getItem(1).setVisible(false);
            mDeletedMenu = menu.getItem(2);
            return true;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, @NotNull MenuItem item) {
            //checked
            if (item.getItemId() == R.id.action_mode_del) {
                if (mModel.getSelectedAmount().getValue() != null)
                    showDialog(SelectedItemDeleteDialog.getInstance(mModel.getSelectedAmount().getValue()), DELETE_DIALOG);
            } else if (item.getItemId() == R.id.action_mode_edit)
                showDialog(CategoryNameEditDialog.getInstance(mModel.getSelectedCategoryList().get(0).getCategoryName()), CATEGORY_EDIT_DIALOG);
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            //checked
            mBinding.viewPager.setUserInputEnabled(true);

            mActionMode = null;
            mActionModeOn = false;
            mCategoryAdapterArray[mBinding.viewPager.getCurrentItem()].setActionModeState(ACTION_MODE_OFF);

            mActionModeTitleBinding.actionModeSelectAll.setChecked(false);
            ((ViewGroup) mActionModeTitleBinding.getRoot().getParent()).removeAllViews();

            parentCategoryButtonEnable(mBinding, true);
        }
    };
    private String mParentCategory;

    private void parentCategoryButtonEnable(@NotNull FragmentMainBinding binding, boolean enable) {
        //checked
        binding.btnTop.setEnabled(enable);
        binding.btnBottom.setEnabled(enable);
        binding.btnOuter.setEnabled(enable);
        binding.btnEtc.setEnabled(enable);
    }

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        //all checked
        super.onCreate(savedInstanceState);
        mModel = new ViewModelProvider(this).get(MainViewModel.class);
        mActivityModel = new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        //all checked
        mBinding = FragmentMainBinding.inflate(inflater, container, false);
        mActionModeTitleBinding = ActionModeTitleBinding.inflate(inflater);
        View view = mBinding.getRoot();

        mSortPreference = requireActivity().getSharedPreferences(SORT_MAIN, Context.MODE_PRIVATE);
        mSort = mSortPreference.getInt(SORT_MAIN, SORT_CUSTOM);

        MainPopupMenuBinding popupMenuBinding = MainPopupMenuBinding.inflate(inflater);
        popupMenuClick(mPopupWindow, popupMenuBinding);
        mPopupWindow = new PopupWindow(popupMenuBinding.getRoot(), ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mPopupWindow.setOutsideTouchable(true);

        mBinding.mainTopFab.setOnClickListener(v -> {
            mBinding.mainScrollView.scrollTo(0, 0);
            mBinding.mainScrollView.smoothScrollTo(0, 0);
        });
        return view;
    }

    private void popupMenuClick(PopupWindow popupWindow, @NotNull MainPopupMenuBinding binding) {
        //checked
        binding.addFolder.setOnClickListener(v -> {
            showDialog(AddCategoryDialog.getInstance(mParentCategory), CATEGORY_ADD_DIALOG);
            popupWindow.dismiss();
        });
        binding.sort.setOnClickListener(v -> {
            showDialog(SortDialog.getInstance(mSort), SORT_DIALOG);
            popupWindow.dismiss();
        });
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        //all checked
        super.onViewCreated(view, savedInstanceState);
        mSelectListener = dragSelectListenerInit();
        mCategoryAdapterArray = getCategoryAdapterArray();
        mTouchHelperArray = getTouchHelperArray(mCategoryAdapterArray);
        mBinding.viewPager.setAdapter(getViewPagerAdapter(mCategoryAdapterArray, mTouchHelperArray, mSelectListener));
        mButtonArray = new MaterialButton[]{mBinding.btnTop, mBinding.btnBottom, mBinding.btnOuter, mBinding.btnEtc};

        requireActivity().findViewById(R.id.activity_fab).setOnClickListener(v -> {
            if (mActionMode != null) mActionMode.finish();
            Navigation.findNavController(requireActivity(), R.id.host_fragment).navigate(R.id.action_mainFragment_to_searchActivity);
        });
    }

    private DragSelectTouchListener dragSelectListenerInit() {
        //all checked
        DragSelectTouchListener.OnAdvancedDragSelectListener listener = new DragSelectTouchListener.OnAdvancedDragSelectListener() {
            @Override
            public void onSelectionStarted(int i) {
                //checked
                mBinding.mainScrollView.setScrollable(false);
                mIsDragSelecting = true;
                RecyclerView recyclerView = mBinding.viewPager.getChildAt(0).findViewById(R.id.main_recyclerView);
                RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForLayoutPosition(i);
                if (viewHolder != null) viewHolder.itemView.callOnClick();
            }

            @Override
            public void onSelectionFinished(int i) {
                //checked
                mBinding.mainScrollView.setScrollable(true);
                mIsDragSelecting = false;
            }

            @Override
            public void onSelectChange(int i, int i1, boolean b) {
                //checked
                RecyclerView recyclerView = mBinding.viewPager.getChildAt(0).findViewById(R.id.main_recyclerView);
                for (int j = i; j <= i1; j++) {
                    RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForLayoutPosition(j);
                    if (viewHolder != null) viewHolder.itemView.callOnClick();
                }
            }
        };
        return new DragSelectTouchListener().withSelectListener(listener);
    }

    @NotNull
    private CategoryAdapter[] getCategoryAdapterArray() {
        //checked
        CategoryAdapter[] categoryAdapterArray = new CategoryAdapter[4];
        for (int i = 0; i < 4; i++)
            categoryAdapterArray[i] = new CategoryAdapter(mModel, this);
        return categoryAdapterArray;
    }

    @NotNull
    private ItemTouchHelper[] getTouchHelperArray(CategoryAdapter[] categoryAdapterArray) {
        //checked
        ItemTouchHelper[] itemTouchHelperArray = new ItemTouchHelper[4];
        for (int i = 0; i < 4; i++)
            itemTouchHelperArray[i] = new ItemTouchHelper(new MainDragCallBack(categoryAdapterArray[i]));
        return itemTouchHelperArray;
    }

    @NotNull
    private MainViewPagerAdapter getViewPagerAdapter(CategoryAdapter[] categoryAdapterArray, ItemTouchHelper[] itemTouchHelperArray,
                                                     DragSelectTouchListener dragSelectTouchListener) {
        //checked
        MainViewPagerAdapter mainViewPagerAdapter = new MainViewPagerAdapter(categoryAdapterArray, dragSelectTouchListener, itemTouchHelperArray);
        mainViewPagerAdapter.setOnMainDragAutoScrollListener(this);
        return mViewPagerAdapter = mainViewPagerAdapter;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //all checked
        setLiveData(mMainLive, mModel, mViewPagerAdapter, mSort);
        setScrollChangeListener(mBinding, mIsDragSelecting, mIsDragging, mScrollEnable);
        setToggleGroup(mBinding, mButtonArray);
        if (savedInstanceState == null) mBinding.btnTop.setChecked(true);
        viewPagerChangeListener(mBinding, mButtonArray, mModel);
        selectedItemAmountLive(mModel, mActionModeTitleBinding, mEditMenu, mDeletedMenu, mActionMode, mCategoryAdapterArray, mBinding);
        actionModeRecreate(savedInstanceState, mButtonArray, mCategoryAdapterArray, mBinding, mModel, mActionModeCallback);
    }

    public void setLiveData(LiveData<List<Category>> mainLive, MainViewModel model, MainViewPagerAdapter mainViewPagerAdapter, int sort) {
        //checked
        if (mainLive != null && mainLive.hasObservers())
            mainLive.removeObservers(getViewLifecycleOwner());
        else if (mainLive == null) mainLive = model.getRepository().getAllCategoryLive();

        mainLive.observe(getViewLifecycleOwner(), categoryList -> mainViewPagerAdapter.setItem(sort, categoryList, model.getRepository()));
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setScrollChangeListener(@NotNull FragmentMainBinding binding, boolean isDragSelecting, boolean isDragging, boolean scrollEnable) {
        //checked
        binding.mainScrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (oldScrollY < scrollY) binding.mainTopFab.show();
            else if (scrollY == 0) binding.mainTopFab.hide();

            if ((isDragSelecting || isDragging) && scrollEnable && scrollY > oldScrollY)
                v.postDelayed(() -> v.scrollBy(0, 1), 50);
            else if ((isDragSelecting || isDragging) && scrollEnable && scrollY < oldScrollY)
                v.postDelayed(() -> v.scrollBy(0, -1), 50);
        });
    }

    private void setToggleGroup(@NotNull FragmentMainBinding binding, @NotNull MaterialButton[] buttonArray) {
        //checked
        final ColorStateList textOriginColor = binding.btnEtc.getTextColors();
        TypedValue typedValue = new TypedValue();
        requireContext().getTheme().resolveAttribute(R.attr.colorControlNormal, typedValue, true);
        int colorControl = typedValue.data;
        requireContext().getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
        int colorPrimary = typedValue.data;

        binding.toggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            for (MaterialButton btn : buttonArray) {
                btn.setBackgroundColor(Color.TRANSPARENT);
                btn.setTextColor(textOriginColor);
            }
            if (checkedId == R.id.btn_top && isChecked) {
                binding.btnTop.setBackgroundColor(colorControl);
                binding.btnTop.setTextColor(colorPrimary);
            } else if (checkedId == R.id.btn_bottom && isChecked) {
                binding.btnBottom.setBackgroundColor(colorControl);
                binding.btnBottom.setTextColor(colorPrimary);
            } else if (checkedId == R.id.btn_outer && isChecked) {
                binding.btnOuter.setBackgroundColor(colorControl);
                binding.btnOuter.setTextColor(colorPrimary);
            } else if (checkedId == R.id.btn_etc && isChecked) {
                binding.btnEtc.setBackgroundColor(colorControl);
                binding.btnEtc.setTextColor(colorPrimary);
            }
        });

        for (int i = 0; i < buttonArray.length; i++) {
            int finalI = i;
            buttonArray[i].setOnClickListener(v -> binding.viewPager.setCurrentItem(finalI, false));
        }
    }

    private void viewPagerChangeListener(@NotNull FragmentMainBinding binding, MaterialButton[] buttonArray, MainViewModel model) {
        //checked
        binding.viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                binding.mainScrollView.smoothScrollTo(0, 0);
                buttonArray[position].setChecked(true);
                switch (position) {
                    case 0:
                        mParentCategory = TOP;
                        break;
                    case 1:
                        mParentCategory = BOTTOM;
                        break;
                    case 2:
                        mParentCategory = OUTER;
                        break;
                    case 3:
                        mParentCategory = ETC;
                        break;
                }
            }
        });
    }

    private void selectedItemAmountLive(@NotNull MainViewModel model, ActionModeTitleBinding actionModeTitleBinding, MenuItem editMenu, MenuItem deletedMenu,
                                        ActionMode actionMode, CategoryAdapter[] categoryAdapterArray, FragmentMainBinding binding) {
        //checked
        model.getSelectedAmount().observe(getViewLifecycleOwner(), integer -> {
            String title = integer + getString(R.string.item_selected);
            actionModeTitleBinding.actionModeTitle.setText(title);
            if (actionMode != null) {
                editMenu.setVisible(integer == 1);
                deletedMenu.setVisible(integer > 0);
            }

            int allItemSize = categoryAdapterArray[binding.viewPager.getCurrentItem()].getCurrentList().size();
            actionModeTitleBinding.actionModeSelectAll.setChecked(allItemSize == integer);
        });
    }

    private void actionModeRecreate(@org.jetbrains.annotations.Nullable Bundle savedInstanceState, MaterialButton[] buttonArray,
                                    CategoryAdapter[] categoryAdapterArray, FragmentMainBinding binding, MainViewModel model,
                                    ActionMode.Callback actionModeCallback) {
        //checked
        if (savedInstanceState != null && savedInstanceState.getBoolean(ACTION_MODE)) {
            buttonArray[savedInstanceState.getInt(CURRENT_ITEM)].setChecked(true);
            categoryAdapterArray[savedInstanceState.getInt(CURRENT_ITEM)].setSelectedItem(model.getSelectedCategoryList());
            ((AppCompatActivity) requireActivity()).startSupportActionMode(actionModeCallback);
        }
    }

    @Override
    public void onDestroyView() {
        //checked
        super.onDestroyView();
        if (mActionMode != null) mActionMode.finish();
        mBinding = null;
        mViewPagerAdapter = null;
        mSelectListener = null;
    }

    private void showDialog(@NotNull DialogFragment dialog, String tag) {
        //checked
        dialog.setTargetFragment(this, 0);
        dialog.show(getParentFragmentManager(), tag);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull @NotNull Menu menu, @NonNull @NotNull MenuInflater inflater) {
        //checked
        inflater.inflate(R.menu.main_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull @NotNull MenuItem item) {
        //checked
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
    public void onSaveInstanceState(@NonNull @NotNull Bundle outState) {
        //checked
        super.onSaveInstanceState(outState);
        outState.putBoolean(ACTION_MODE, mActionModeOn);
        if (mBinding != null)
            outState.putInt(CURRENT_ITEM, mBinding.viewPager.getCurrentItem());
    }

    //category adapter------------------------------------------------------------------------------
    @Override
    public void onCategoryCardViewClick(Category category, MaterialCheckBox checkBox) {
        //checked
        if (mActionMode == null) {
            mActivityModel.setCategory(category);
            Navigation.findNavController(requireActivity(), R.id.host_fragment).navigate(R.id.action_mainFragment_to_listFragment);
        } else {
            checkBox.setChecked(!checkBox.isChecked());
            mCategoryAdapterArray[mBinding.viewPager.getCurrentItem()].setSelectedPosition(category.getId());
            mModel.categorySelected(category, checkBox.isChecked());
        }
    }

    @Override
    public void onCategoryCardViewLongClick(int position) {
        //checked
        if (mActionMode == null) {
            mModel.getSelectedCategoryList().clear();
            ((AppCompatActivity) requireActivity()).startSupportActionMode(mActionModeCallback);
        }
        mSelectListener.startDragSelection(position);
    }

    @Override
    public void onCategoryDragHandleTouch(RecyclerView.ViewHolder viewHolder) {
        //checked
        if (!mIsDragging) {
            mIsDragging = true;
            viewHolder.itemView.setTranslationZ(10);
            ItemMainRecyclerBinding binding = ((CategoryAdapter.CategoryVH) viewHolder).getBinding();
            binding.mainCheckBox.setVisibility(View.INVISIBLE);
            binding.mainCategoryText.setAlpha(0.5f);
            binding.mainAmountLayout.setAlpha(0.5f);
            mTouchHelperArray[mBinding.viewPager.getCurrentItem()].startDrag(viewHolder);
        } else {
            mIsDragging = false;
            viewHolder.itemView.setTranslationZ(0);
            ItemMainRecyclerBinding binding = ((CategoryAdapter.CategoryVH) viewHolder).getBinding();
            binding.mainCheckBox.setVisibility(View.VISIBLE);
            binding.mainCategoryText.setAlpha(0.8f);
            binding.mainAmountLayout.setAlpha(0.8f);
        }
    }
    //----------------------------------------------------------------------------------------------

    //dialog lister---------------------------------------------------------------------------------
    @Override
    public void addCategoryConfirmClick(@NotNull String categoryName, String parentCategory) {
        //checked
        mModel.getRepository().categoryInsert(new Category(categoryName.trim(), parentCategory, mModel.getRepository().getCategoryLargestOrderPlus1()));
    }

    @Override
    public void sortConfirmClick(int sort) {
        //checked
        mSort = sort;
        setLiveData(mMainLive, mModel, mViewPagerAdapter, mSort);
        SharedPreferences.Editor editor = mSortPreference.edit();
        editor.putInt(SORT_MAIN, mSort);
        editor.apply();
    }

    @Override
    public void categoryNameEditConfirmClick(@NotNull String categoryName) {
        //checked
        mModel.categoryNameEdit(categoryName.trim());
        mActionMode.finish();
    }

    @Override
    public void selectedItemDeleteConfirmClick() {
        //checked
        SelectedItemTreat.categoryDelete(mModel.getRepository(), mModel.getSelectedCategoryList());
        mActionMode.finish();
    }
    //----------------------------------------------------------------------------------------------

    @Override
    public void dragAutoScroll(int upDownStop) {
        //checked
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