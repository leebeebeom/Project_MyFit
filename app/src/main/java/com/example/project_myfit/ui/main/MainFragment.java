package com.example.project_myfit.ui.main;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.ItemTouchHelper;

import com.example.project_myfit.MainActivityViewModel;
import com.example.project_myfit.R;
import com.example.project_myfit.databinding.FragmentMainBinding;
import com.example.project_myfit.databinding.ItemDialogEditTextBinding;
import com.example.project_myfit.ui.main.adapter.CategoryAdapter;
import com.example.project_myfit.ui.main.adapter.MainDragCallBack;
import com.example.project_myfit.ui.main.database.Category;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.jetbrains.annotations.NotNull;

import java.util.List;

//TODO 레이아웃 정리

public class MainFragment extends Fragment {
    public static final String TOP = "TOP";
    public static final String BOTTOM = "BOTTOM";
    public static final String OUTER = "OUTER";
    public static final String ETC = "ETC";
    private MainViewModel mModel;
    private FragmentMainBinding mBinding;
    private MainActivityViewModel mActivityModel;
    private ItemDialogEditTextBinding mDialogEditText;
    private CategoryAdapter mAdapter;
    private List<Category> mCurrentCategoryList;
    private boolean mRefresh;
    private String checkedCategory;
    private LiveData<List<Category>> mLiveData;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = FragmentMainBinding.inflate(inflater);
        mDialogEditText = ItemDialogEditTextBinding.inflate(inflater);
        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //Initialize
        init();
        //Get Category
        getCategory();
        //Toggle View Setting
        ColorStateList textOriginColor = mBinding.btnEtc.getTextColors();
        setToggleGroup(textOriginColor);
        if (checkedCategory == null) {
            mBinding.btnTop.setChecked(true);
        }
        //Click Listener
        setClickListener();
    }

    //Initialize
    private void init() {
        //View Model
        mModel = new ViewModelProvider(this).get(MainViewModel.class);
        //Activity View Model
        mActivityModel = new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);
        //RefreshOn
        mRefresh = true;
        //Adapter
        mAdapter = new CategoryAdapter();
        //Recycler View
        mBinding.recyclerView.setHasFixedSize(true);
        //ItemTouchHelper
        ItemTouchHelper touchHelper = new ItemTouchHelper(new MainDragCallBack(mAdapter));
        touchHelper.attachToRecyclerView(mBinding.recyclerView);
    }

    //Get Category
    private void getCategory() {
        mLiveData = mModel.getAllChild(checkedCategory);
        mLiveData.observe(getViewLifecycleOwner(), categoryList -> {
            mCurrentCategoryList = categoryList;
            mModel.setLargestOrder();
            if (mRefresh) {
                mAdapter.setItem(categoryList, mModel);
                mBinding.recyclerView.setAdapter(mAdapter);
                mRefresh = false;
            } else {
                mAdapter.updateDiffUtils(categoryList);
            }
        });
    }

    //Toggle Group Setting
    private void setToggleGroup(ColorStateList textOriginColor) {
        mBinding.toggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            TypedValue typedValue = new TypedValue();
            requireContext().getTheme().resolveAttribute(R.attr.colorControlNormal, typedValue, true);
            int colorControl = typedValue.data;
            requireContext().getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
            int colorPrimary = typedValue.data;
            if (checkedId == mBinding.btnTop.getId()) {
                checkedCategory = TOP;

                mBinding.btnTop.setBackgroundColor(colorControl);
                mBinding.btnTop.setTextColor(colorPrimary);

                mBinding.btnBottom.setBackgroundColor(Color.TRANSPARENT);
                mBinding.btnBottom.setTextColor(textOriginColor);
                mBinding.btnOuter.setBackgroundColor(Color.TRANSPARENT);
                mBinding.btnOuter.setTextColor(textOriginColor);
                mBinding.btnEtc.setBackgroundColor(Color.TRANSPARENT);
                mBinding.btnEtc.setTextColor(textOriginColor);
            } else if (checkedId == mBinding.btnBottom.getId()) {
                checkedCategory = BOTTOM;

                mBinding.btnBottom.setBackgroundColor(colorControl);
                mBinding.btnBottom.setTextColor(colorPrimary);

                mBinding.btnTop.setBackgroundColor(Color.TRANSPARENT);
                mBinding.btnTop.setTextColor(textOriginColor);
                mBinding.btnOuter.setBackgroundColor(Color.TRANSPARENT);
                mBinding.btnOuter.setTextColor(textOriginColor);
                mBinding.btnEtc.setBackgroundColor(Color.TRANSPARENT);
                mBinding.btnEtc.setTextColor(textOriginColor);
            } else if (checkedId == mBinding.btnOuter.getId()) {
                checkedCategory = OUTER;

                mBinding.btnOuter.setBackgroundColor(colorControl);
                mBinding.btnOuter.setTextColor(colorPrimary);

                mBinding.btnTop.setBackgroundColor(Color.TRANSPARENT);
                mBinding.btnTop.setTextColor(textOriginColor);
                mBinding.btnBottom.setBackgroundColor(Color.TRANSPARENT);
                mBinding.btnBottom.setTextColor(textOriginColor);
                mBinding.btnEtc.setBackgroundColor(Color.TRANSPARENT);
                mBinding.btnEtc.setTextColor(textOriginColor);
            } else if (checkedId == mBinding.btnEtc.getId()) {
                checkedCategory = ETC;

                mBinding.btnEtc.setBackgroundColor(colorControl);
                mBinding.btnEtc.setTextColor(colorPrimary);

                mBinding.btnTop.setBackgroundColor(Color.TRANSPARENT);
                mBinding.btnTop.setTextColor(textOriginColor);
                mBinding.btnBottom.setBackgroundColor(Color.TRANSPARENT);
                mBinding.btnBottom.setTextColor(textOriginColor);
                mBinding.btnOuter.setBackgroundColor(Color.TRANSPARENT);
                mBinding.btnOuter.setTextColor(textOriginColor);

            }
            mRefresh = true;
            if (mLiveData.hasObservers()) {
                mLiveData.removeObservers(getViewLifecycleOwner());
            }
            getCategory();
        });
    }

    //Click Listener
    private void setClickListener() {
        requireActivity().findViewById(R.id.activity_fab).setOnClickListener(v -> showAddDialog());
        mAdapter.setOnCategoryClickListener(new CategoryAdapter.OnCategoryClickListener() {
            @Override
            public void onItemClick(Category category) {
                mActivityModel.setCategory(category);
                Navigation.findNavController(requireView()).navigate(R.id.action_mainFragment_to_listFragment);
            }

            @Override
            public void onEditClick(Category category, int position) {
                showEditDialog(category, position);
            }

            @Override
            public void onDeleteClick(Category category) {
                showDeleteDialog(category);
            }
        });
    }

    //Add Dialog
    private void showAddDialog() {
        setEditText("");
        //builder
        MaterialAlertDialogBuilder builder = getDialogBuilder().setTitle("Add Category");
        //DONE Clicked
        builder.setPositiveButton("DONE", (dialog, which) -> {
            //Get Largest Order
            int largestOrder = mModel.getLargestOrder();
            largestOrder++;
            //Create new ChildCategory
            Category newCategory = new Category(mDialogEditText.editTextDialog.getText().toString(), checkedCategory, largestOrder);
            //Insert
            mModel.insert(newCategory);
            //Snack Bar
//            Snackbar.make(requireActivity().findViewById(R.id.coordinator_layout), "카테고리 추가됨", BaseTransientBottomBar.LENGTH_LONG)
//                    .setAnchorView(R.id.activity_fab)
//                    Undo Clicked
//                    .setAction("Undo", v -> {
//                        Last Added Data
//                        Category addedCategory = mCurrentCategoryList.get(mCurrentCategoryList.size() - 1);
//                        Delete
//                        mModel.delete(addedCategory);
//                        showUndoConfirmSnackBar();
//                    }).show();
        }).show();
    }

    //Edit Dialog
    private void showEditDialog(Category category, int position) {
        //Save oldChildCategory Name
        String oldCategoryName = category.getCategory();
        //Edit Text Setting
        setEditText(oldCategoryName);
        //Builder
        MaterialAlertDialogBuilder builder = getDialogBuilder().setTitle("Edit Category");
        //DONE Clicked
        builder.setPositiveButton("DONE", (dialog, which) -> {
            category.setCategory(mDialogEditText.editTextDialog.getText().toString());
            mAdapter.notifyItemChanged(position);
            //Update
            mModel.update(category);
            //Snack Bar
//            Snackbar.make(requireActivity().findViewById(R.id.coordinator_layout), "카테고리 수정됨", BaseTransientBottomBar.LENGTH_LONG)
//                    .setAnchorView(R.id.activity_fab)
//                    .setAction("Undo", v -> {
//                        category.setCategory(oldCategoryName);
//                        mAdapter.notifyItemChanged(position);
//                        mModel.update(category);
//                        showUndoConfirmSnackBar();
//                    }).show();
        }).show();

    }

    //Delete Dialog
    private void showDeleteDialog(Category category) {
        //To be Deleted SizeList
        mModel.getAllSizeByFolder(category.getId());
        //Builder
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext(), R.style.myAlertDialog)
                .setTitle("Warning")
                .setMessage("카테고리 안 모든 아이템이 삭제됩니다.\n삭제하시겠습니까?")
                .setNegativeButton("CANCEL", null)
                .setPositiveButton("DONE", (dialog, which) -> {
                    mModel.delete(category);
                    //Snack Bar
//                    Snackbar.make(requireActivity().findViewById(R.id.coordinator_layout), "카테고리 삭제됨", BaseTransientBottomBar.LENGTH_LONG)
//                            .setAnchorView(R.id.activity_fab)
//                            .setAction("Undo", v -> {
//                                mModel.insert(category);
//                                showUndoConfirmSnackBar();
//                            }).show();
                });
        builder.show();
    }

    //Get MaterialAlertDialogBuilder
    @NotNull
    private MaterialAlertDialogBuilder getDialogBuilder() {
        return new MaterialAlertDialogBuilder(requireContext(), R.style.myAlertDialog)
                .setView(mDialogEditText.getRoot())
                .setNegativeButton("CANCEL", null)
                .setOnDismissListener(dialog -> ((ViewGroup) mDialogEditText.getRoot().getParent()).removeAllViews());
    }

    //Edit Text Setting
    private void setEditText(String setText) {
        mDialogEditText.setHint("Category");
        mDialogEditText.setSetText(setText);
        mDialogEditText.setPlaceHolder("ex)Short Sleeve");
    }

    //Undo Confirmed
//    private void showUndoConfirmSnackBar() {
//        Snackbar.make(requireActivity().findViewById(R.id.coordinator_layout), "취소됨", BaseTransientBottomBar.LENGTH_SHORT)
//                .setAnchorView(R.id.activity_fab)
//                .show();
//    }
}