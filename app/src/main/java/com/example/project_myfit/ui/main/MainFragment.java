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
import com.example.project_myfit.ui.main.adapter.CategoryAdapterListener;
import com.example.project_myfit.ui.main.adapter.ViewPagerAdapter;
import com.example.project_myfit.ui.main.database.Category;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.michaelflisar.dragselectrecyclerview.DragSelectTouchListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.example.project_myfit.MyFitConstant.ACTION_MODE_OFF;
import static com.example.project_myfit.MyFitConstant.ACTION_MODE_ON;
import static com.example.project_myfit.MyFitConstant.BOTTOM;
import static com.example.project_myfit.MyFitConstant.ETC;
import static com.example.project_myfit.MyFitConstant.OUTER;
import static com.example.project_myfit.MyFitConstant.SORT_CREATE;
import static com.example.project_myfit.MyFitConstant.SORT_CREATE_REVERSE;
import static com.example.project_myfit.MyFitConstant.SORT_MAIN;
import static com.example.project_myfit.MyFitConstant.SORT_NAME;
import static com.example.project_myfit.MyFitConstant.SORT_NAME_REVERSE;
import static com.example.project_myfit.MyFitConstant.TOP;

//FAB 서치
//휴지통 하면 끝
public class MainFragment extends Fragment implements AddCategoryDialog.AddCategoryConfirmClick, ViewPagerAdapter.MainDragAutoScroll,
        SortDialog.SortConfirmClick, CategoryAdapterListener, CategoryNameEditDialog.CategoryNameEditConfirmClick, SelectedItemDeleteDialog.SelectedItemDeleteConfirmClick {
    private MainViewModel mModel;
    private FragmentMainBinding mBinding;
    private MainActivityViewModel mActivityModel;
    private String mCheckedCategory;
    private PopupWindow mPopupWindow;
    private MainPopupMenuBinding mPopupMenuBinding;
    private SharedPreferences mSortPreference;
    private int mSort;
    private ViewPagerAdapter mViewPagerAdapter;
    private boolean isDragging;
    private ActionMode.Callback mActionModeCallback;
    private boolean mActionModeOn;
    private ActionMode mActionMode;
    private ActionModeTitleBinding mActionModeTitleBinding;
    private MenuItem mEditMenu;
    private MenuItem mDeletedMenu;
    private DragSelectTouchListener mDragSelectListener;
    private boolean isDragSelectEnable;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mModel = new ViewModelProvider(this).get(MainViewModel.class);
        mActivityModel = new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        mBinding = FragmentMainBinding.inflate(inflater, container, false);
        View view = mBinding.getRoot();

        dragSelectListenerInit();

        CategoryAdapter topAdapter = new CategoryAdapter(mModel, this, 0);
        CategoryAdapter bottomAdapter = new CategoryAdapter(mModel, this, 1);
        CategoryAdapter outerAdapter = new CategoryAdapter(mModel, this, 2);
        CategoryAdapter etcAdapter = new CategoryAdapter(mModel, this, 3);
        List<CategoryAdapter> adapterList = new ArrayList<>();
        adapterList.add(topAdapter);
        adapterList.add(bottomAdapter);
        adapterList.add(outerAdapter);
        adapterList.add(etcAdapter);
        mViewPagerAdapter = new ViewPagerAdapter(adapterList, mDragSelectListener);

        mBinding.viewPager.setAdapter(mViewPagerAdapter);

        mActionModeTitleBinding = ActionModeTitleBinding.inflate(inflater);

        mPopupMenuBinding = MainPopupMenuBinding.inflate(inflater);

        mSortPreference = requireActivity().getSharedPreferences(SORT_MAIN, Context.MODE_PRIVATE);
        mSort = mSortPreference.getInt(SORT_MAIN, 0);
        mModel.setSort(mSort);

        return view;
    }

    private void dragSelectListenerInit() {
        DragSelectTouchListener.OnAdvancedDragSelectListener topSelectListener = new DragSelectTouchListener.OnAdvancedDragSelectListener() {
            @Override
            public void onSelectionStarted(int i) {
                mBinding.mainScrollView.setScrollable(false);
                isDragSelectEnable = true;
                RecyclerView recyclerView = mBinding.viewPager.getChildAt(0).findViewById(R.id.main_recyclerView);
                RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForLayoutPosition(i);
                if (viewHolder != null) viewHolder.itemView.callOnClick();
            }

            @Override
            public void onSelectionFinished(int i) {
                isDragSelectEnable = false;
                mBinding.mainScrollView.setScrollable(true);
            }

            @Override
            public void onSelectChange(int i, int i1, boolean b) {
                RecyclerView recyclerView = mBinding.viewPager.getChildAt(0).findViewById(R.id.main_recyclerView);
                for (int j = i; j <= i1; j++) {
                    RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForLayoutPosition(i);
                    if (viewHolder != null) viewHolder.itemView.callOnClick();
                }
            }
        };
        mDragSelectListener = new DragSelectTouchListener().withSelectListener(topSelectListener);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        actionModeCallBackInit();


        if (savedInstanceState != null && savedInstanceState.getBoolean("action mode")) {
            mCheckedCategory = savedInstanceState.getString("checked category");
            mViewPagerAdapter.setSelectedPosition(mModel.getSelectedPositionList(), mCheckedCategory);
            ((AppCompatActivity) requireActivity()).startSupportActionMode(mActionModeCallback);
        }

        setLiveData();

        setAutoScroll();

        mBinding.viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    mBinding.btnTop.setChecked(true);
                } else if (position == 1) {
                    mBinding.btnBottom.setChecked(true);
                } else if (position == 2) {
                    mBinding.btnOuter.setChecked(true);
                } else if (position == 3) {
                    mBinding.btnEtc.setChecked(true);
                }
            }
        });

        mPopupWindow = new PopupWindow(mPopupMenuBinding.getRoot(), ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mPopupWindow.setOutsideTouchable(true);

        //Toggle View Setting
        setToggleGroup();
        if (mCheckedCategory == null) {
            mBinding.btnTop.setChecked(true);
        }
        //Click Listener
        setClickListener();

        mModel.getSelectedAmount().observe(getViewLifecycleOwner(), integer -> {
            String title = integer + getString(R.string.item_selected);
            mActionModeTitleBinding.actionModeTitle.setText(title);

            if (mActionMode != null) {
                mEditMenu.setVisible(integer == 1);
                mDeletedMenu.setVisible(integer > 0);
            }

            int selectedItemSize;
            switch (mCheckedCategory) {
                case TOP:
                    selectedItemSize = mViewPagerAdapter.getTopAdapter().getCurrentList().size();
                    break;
                case BOTTOM:
                    selectedItemSize = mViewPagerAdapter.getBottomAdapter().getCurrentList().size();
                    break;
                case OUTER:
                    selectedItemSize = mViewPagerAdapter.getOuterAdapter().getCurrentList().size();
                    break;
                default:
                    selectedItemSize = mViewPagerAdapter.getETCAdapter().getCurrentList().size();
                    break;
            }
            mActionModeTitleBinding.actionModeSelectAll.setChecked(selectedItemSize == integer);
        });
    }

    private void actionModeCallBackInit() {
        mActionModeCallback = new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                mBinding.viewPager.setUserInputEnabled(false);

                mActionModeOn = true;
                mActionMode = mode;

                mode.getMenuInflater().inflate(R.menu.list_action_mode, menu);
                mode.setCustomView(mActionModeTitleBinding.getRoot());

                mViewPagerAdapter.setActionModeState(ACTION_MODE_ON, mCheckedCategory);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                mActionModeTitleBinding.actionModeSelectAll.setOnClickListener(v -> {
                    mModel.selectedItemClear();
                    if (mActionModeTitleBinding.actionModeSelectAll.isChecked()) {
                        switch (mCheckedCategory) {
                            case TOP:
                                mModel.getSelectedItemTop().addAll(mViewPagerAdapter.getTopAdapter().getCurrentList());
                                break;
                            case BOTTOM:
                                mModel.getSelectedItemBottom().addAll(mViewPagerAdapter.getBottomAdapter().getCurrentList());
                                break;
                            case OUTER:
                                mModel.getSelectedItemOuter().addAll(mViewPagerAdapter.getOuterAdapter().getCurrentList());
                                break;
                            default:
                                mModel.getSelectedItemETC().addAll(mViewPagerAdapter.getETCAdapter().getCurrentList());
                                break;
                        }
                        mViewPagerAdapter.selectAll(mCheckedCategory);
                    } else
                        mViewPagerAdapter.deSelectAll(mCheckedCategory);
                    mModel.setSelectedAmount();
                });
                mEditMenu = menu.getItem(0);
                menu.getItem(1).setVisible(false);
                mDeletedMenu = menu.getItem(2);
                return true;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                if (item.getItemId() == R.id.list_action_mode_del) {
                    if (mModel.getSelectedAmount().getValue() != null)
                        showDialog(SelectedItemDeleteDialog.getInstance(mModel.getSelectedAmount().getValue()), "delete");
                } else {
                    if (mModel.getSelectedItemTop().size() != 0)
                        showDialog(CategoryNameEditDialog.getInstance(mModel.getSelectedItemTop().get(0).getCategory()), "edit");
                    else if (mModel.getSelectedItemBottom().size() != 0)
                        showDialog(CategoryNameEditDialog.getInstance(mModel.getSelectedItemBottom().get(0).getCategory()), "edit");
                    else if (mModel.getSelectedItemOuter().size() != 0)
                        showDialog(CategoryNameEditDialog.getInstance(mModel.getSelectedItemOuter().get(0).getCategory()), "edit");
                    else
                        showDialog(CategoryNameEditDialog.getInstance(mModel.getSelectedItemETC().get(0).getCategory()), "edit");
                }
                return true;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                mBinding.viewPager.setUserInputEnabled(true);

                mActionMode = null;
                mActionModeOn = false;

                mModel.setSelectedPosition(mViewPagerAdapter, mCheckedCategory);

                mViewPagerAdapter.setActionModeState(ACTION_MODE_OFF, mCheckedCategory);

                mActionModeTitleBinding.actionModeSelectAll.setChecked(false);
                ((ViewGroup) mActionModeTitleBinding.getRoot().getParent()).removeAllViews();
            }
        };
    }

    @Override
    public void dragAutoScroll(int upDown) {
        if ((isDragSelectEnable || isDragging) && upDown == 0) mBinding.mainScrollView.scrollBy(0, 1);
        else if ((isDragSelectEnable || isDragging) && upDown == 1) mBinding.mainScrollView.scrollBy(0, -1);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setAutoScroll() {
        mBinding.mainScrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if ((isDragSelectEnable || isDragging) && scrollY > oldScrollY)
                v.postDelayed(() -> v.scrollBy(0, 1), 50);
            else if ((isDragSelectEnable || isDragging) && scrollY < oldScrollY)
                v.postDelayed(() -> v.scrollBy(0, -1), 50);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mActionMode != null) mActionMode.finish();
        mBinding = null;
        mViewPagerAdapter = null;
    }

    private void setLiveData() {
        mModel.getCategoryLive().observe(getViewLifecycleOwner(), categoryList -> {
            if (mSort == SORT_CREATE)
                categoryList.sort((o1, o2) -> Long.compare(o2.getId(), o1.getId()));
            else if (mSort == SORT_CREATE_REVERSE)
                categoryList.sort((o1, o2) -> Long.compare(o1.getId(), o2.getId()));
            else if (mSort == SORT_NAME)
                categoryList.sort((o1, o2) -> o1.getCategory().compareTo(o2.getCategory()));
            else if (mSort == SORT_NAME_REVERSE)
                categoryList.sort((o1, o2) -> o2.getCategory().compareTo(o1.getCategory()));
            mViewPagerAdapter.setItem(categoryList);
            mViewPagerAdapter.setSort(mSort);
        });
    }

    //Toggle Group Setting
    private void setToggleGroup() {
        final ColorStateList textOriginColor = mBinding.btnEtc.getTextColors();
        TypedValue typedValue = new TypedValue();
        requireContext().getTheme().resolveAttribute(R.attr.colorControlNormal, typedValue, true);
        int colorControl = typedValue.data;
        requireContext().getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
        int colorPrimary = typedValue.data;

        mBinding.toggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (checkedId == R.id.btn_top && isChecked) {
                mCheckedCategory = TOP;
                mBinding.viewPager.setCurrentItem(0, true);
                mBinding.btnTop.setBackgroundColor(colorControl);
                mBinding.btnTop.setTextColor(colorPrimary);
            } else if (checkedId == R.id.btn_bottom && isChecked) {
                mCheckedCategory = BOTTOM;
                mBinding.viewPager.setCurrentItem(1, true);
                mBinding.btnBottom.setBackgroundColor(colorControl);
                mBinding.btnBottom.setTextColor(colorPrimary);
            } else if (checkedId == R.id.btn_outer && isChecked) {
                mCheckedCategory = OUTER;
                mBinding.viewPager.setCurrentItem(2, true);
                mBinding.btnOuter.setBackgroundColor(colorControl);
                mBinding.btnOuter.setTextColor(colorPrimary);
            } else if (checkedId == R.id.btn_etc && isChecked) {
                mCheckedCategory = ETC;
                mBinding.viewPager.setCurrentItem(3, true);
                mBinding.btnEtc.setBackgroundColor(colorControl);
                mBinding.btnEtc.setTextColor(colorPrimary);
            }

            if (checkedId != R.id.btn_top) {
                mBinding.btnTop.setBackgroundColor(Color.TRANSPARENT);
                mBinding.btnTop.setTextColor(textOriginColor);
            }
            if (checkedId != R.id.btn_bottom) {
                mBinding.btnBottom.setBackgroundColor(Color.TRANSPARENT);
                mBinding.btnBottom.setTextColor(textOriginColor);
            }
            if (checkedId != R.id.btn_outer) {
                mBinding.btnOuter.setBackgroundColor(Color.TRANSPARENT);
                mBinding.btnOuter.setTextColor(textOriginColor);
            }
            if (checkedId != R.id.btn_etc) {
                mBinding.btnEtc.setBackgroundColor(Color.TRANSPARENT);
                mBinding.btnEtc.setTextColor(textOriginColor);
            }
        });
    }

    //Click Listener
    private void setClickListener() {
        mPopupMenuBinding.addFolder.setOnClickListener(v -> {
            showDialog(new AddCategoryDialog(), "add category");
            mPopupWindow.dismiss();
        });
        mPopupMenuBinding.sort.setOnClickListener(v -> {
            showDialog(SortDialog.getInstance(mSort), "sort");
            mPopupWindow.dismiss();
        });

        mViewPagerAdapter.setOnCategoryAdapterListener(this);
    }

    private void showDialog(DialogFragment dialog, String tag) {
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
        }
        return false;
    }

    @Override
    public void addCategoryConfirmClick(String categoryName) {
        mModel.addCategory(mCheckedCategory, categoryName);
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
        outState.putBoolean("action mode", mActionModeOn);
        outState.putString("checked category", mCheckedCategory);
    }

    @Override
    public void onCategoryCardViewClick(Category category, MaterialCheckBox checkBox, int position, int viewPagerPosition) {
        if (mActionMode == null) {
            mActivityModel.setCategory(category);
            Navigation.findNavController(requireView()).navigate(R.id.action_mainFragment_to_listFragment);
        } else {
            checkBox.setChecked(!checkBox.isChecked());
            mModel.categorySelected(category, checkBox.isChecked(), position, viewPagerPosition, mViewPagerAdapter);
        }
    }

    @Override
    public void onCategoryCardViewLongClick(MaterialCardView cardView, int position) {
        if (mActionMode == null) {
            mModel.selectedItemListInit();
            ((AppCompatActivity) requireActivity()).startSupportActionMode(mActionModeCallback);
        }
        mDragSelectListener.startDragSelection(position);
    }

    @Override
    public void onCategoryDragHandleTouch(RecyclerView.ViewHolder viewHolder, int viewPagerPosition) {
        List<ItemTouchHelper> touchHelperList = mViewPagerAdapter.getTouchHelperList();
        if (mActionMode == null && !isDragging) {
            isDragging = true;
            touchHelperList.get(viewPagerPosition).startDrag(viewHolder);
        } else if (isDragging) isDragging = false;
    }

    @Override
    public void categoryNameEditConfirmClick(String categoryName) {
        mModel.categoryNameEdit(categoryName, mCheckedCategory);
        mActionMode.finish();
    }

    @Override
    public void selectedItemDeleteConfirmClick() {
        mModel.selectedItemDelete(mCheckedCategory);
        mActionMode.finish();
    }
}