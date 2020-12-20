package com.example.project_myfit.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;

import com.chad.library.adapter.base.entity.node.BaseNode;
import com.example.project_myfit.MainActivityViewModel;
import com.example.project_myfit.R;
import com.example.project_myfit.databinding.FragmentMainBinding;
import com.example.project_myfit.ui.main.database.ChildCategory;
import com.example.project_myfit.ui.main.database.ParentCategory;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

public class MainFragment extends Fragment {
    public static final String TAG = "로그";
    private MainViewModel mModel;
    private FragmentMainBinding mBinding;
    private MainActivityViewModel mActivityModel;
    private boolean mFirstLoad = true;
    private int clickedPosition;
    private boolean mAddLoad = false;
    private List<ChildCategory> mCurrentChildList;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        //바인딩
        mBinding = FragmentMainBinding.inflate(inflater);
        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //Initialize
        init();
        mModel.getAll().observe(getViewLifecycleOwner(), childCategoryList -> {
            mCurrentChildList = childCategoryList;
            //프래그먼트 생성시 한번만 갱신(for nodeAddData)
            if (mFirstLoad) {
                mModel.getMainFragmentAdapter().setList(mModel.getData(childCategoryList));
                mFirstLoad = false;
            }
            else if (mAddLoad) {
                //Add Update
                addLoad(childCategoryList);
                mAddLoad = false;
            }
            //for First Run
            if (mModel.isFirstRun()) {
                mModel.getMainFragmentAdapter().setList(mModel.getData(childCategoryList));
            }
        });
        setClickListener();
    }

    @Override
    public void onStop() {
        super.onStop();
        //First Run Check
        if (mModel.isFirstRun()) {
            mModel.setPreferences();
        }
        mCurrentChildList.clear();
    }

    //Initialize
    private void init() {
        //View Model
        mModel = new ViewModelProvider(this).get(MainViewModel.class);
        //Activity View Model
        mActivityModel = new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);
        //Options Menu
        setHasOptionsMenu(true);
        //Recycler View
        mBinding.recyclerView.setHasFixedSize(true);
        mBinding.recyclerView.addItemDecoration(new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL));
        mBinding.recyclerView.setAdapter(mModel.getMainFragmentAdapter());
    }

    //Click Listener
    private void setClickListener() {
        mModel.getMainFragmentAdapter().setOnItemChildClickListener((adapter, view, position) -> {
            Toast.makeText(requireContext(), "포지션" + position, Toast.LENGTH_SHORT).show();
            // Add
            if (view.getId() == R.id.add_icon) {
                //Save Clicked Position
                clickedPosition = position;
                //Show Add Dialog
                showAddDialog((ParentCategory) adapter.getData().get(position));
            }// Edit
            else if (view.getId() == R.id.edit_icon) {
                ChildCategory childCategory = (ChildCategory) adapter.getItem(position);
                //Show Edit Dialog
                showEditDialog(childCategory);
            } else if (view.getId() == R.id.delete_icon) {
                ChildCategory childCategory = (ChildCategory) adapter.getItem(position);
                //Show Delete Dialog
                showDeleteDialog(childCategory, position);
            }
        });
    }

    //Add Dialog
    private void showAddDialog(ParentCategory parentCategory) {
        //Edit Text
        FrameLayout frameLayout = (FrameLayout) View.inflate(requireContext(), R.layout.item_dialog_edit_text, null);
        TextInputEditText editText = frameLayout.findViewById(R.id.editText_dialog);
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext(), R.style.myAlertDialog);
        builder.setView(frameLayout);
        builder.setNegativeButton("CANCEL", null);
        builder.setTitle("Add");
        builder.setPositiveButton("DONE", (dialog, which) -> {
            ChildCategory childCategory = new ChildCategory(editText.getText().toString(), parentCategory.getParentCategory());
            Snackbar.make(requireActivity().findViewById(R.id.coordinator_layout), "카테고리 추가됨", BaseTransientBottomBar.LENGTH_LONG)
                    .setAnchorView(R.id.bottom_nav)
                    .setAction("Undo", v -> {
                        //Undo
                        mFirstLoad = true;
                        //Last Data
                        mModel.delete(mCurrentChildList.get(mCurrentChildList.size() - 1));
                    }).show();
            //Insert
            mAddLoad = true;
            mModel.insert(childCategory);
        });
        builder.show();
    }

    //Add Update
    private void addLoad(List<ChildCategory> childCategoryList) {
        BaseNode parentNode = mModel.getMainFragmentAdapter().getData().get(clickedPosition);//Parent Category
        final int childIndex = mModel.getMainFragmentAdapter().getData().get(clickedPosition).getChildNode().size(); //Add Position
        final int addItem = childCategoryList.size() - 1; //Add Item Index
        mModel.getMainFragmentAdapter().nodeAddData(parentNode, childIndex, childCategoryList.get(addItem));
    }

    //Edit Dialog
    private void showEditDialog(ChildCategory childCategory) {
        String oldChildCategory = childCategory.getChildCategory();
        FrameLayout frameLayout = (FrameLayout) View.inflate(requireContext(), R.layout.item_dialog_edit_text, null);
        TextInputEditText editText = frameLayout.findViewById(R.id.editText_dialog);
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext(), R.style.myAlertDialog);
        builder.setView(frameLayout);
        builder.setNegativeButton("CANCEL", null);
        builder.setTitle("Edit");
        builder.setPositiveButton("DONE", (dialog, which) -> {
            Snackbar.make(requireActivity().findViewById(R.id.coordinator_layout), "카테고리 수정됨", BaseTransientBottomBar.LENGTH_LONG)
                    .setAnchorView(R.id.bottom_nav)
                    .setAction("Undo", v -> {
                        childCategory.setChildCategory(oldChildCategory);
                        mModel.update(childCategory);
                        mModel.getMainFragmentAdapter().notifyDataSetChanged();
                    }).show();
            //childCategory = In Adapter
            childCategory.setChildCategory(editText.getText().toString());
            mModel.update(childCategory);
            mModel.getMainFragmentAdapter().notifyDataSetChanged();
        });
        builder.show();
    }

    //Delete Dialog
    private void showDeleteDialog(ChildCategory childCategory, int position) {
        int parentPosition = mModel.getMainFragmentAdapter().findParentNode(childCategory);
        ParentCategory parentCategory = (ParentCategory) mModel.getMainFragmentAdapter().getData().get(parentPosition);
        mModel.getCurrentSizeList(childCategory.getId());
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext(), R.style.myAlertDialog);
        builder.setTitle("Warning");
        builder.setMessage("카테고리 안 모든 아이템이 삭제됩니다.\n삭제하시겠습니까?");
        builder.setPositiveButton("DONE", (dialog, which) -> {
            //Undo
            Snackbar.make(requireActivity().findViewById(R.id.coordinator_layout), "카테고리 삭제됨", BaseTransientBottomBar.LENGTH_LONG)
                    .setAnchorView(R.id.bottom_nav)
                    .setAction("Undo", v -> {
                        mModel.insert(childCategory);
                        mModel.restoreCurrentSizeList();
                        mFirstLoad = true;
                    }).show();
            int LastPosition = mModel.getMainFragmentAdapter().getData().size() - 1;
            if (LastPosition == position) {
                //if childCategory is last data
                //Live Data Load
                mFirstLoad = true;
                //Delete
                mModel.delete(childCategory);
                mModel.deleteSizeListByFolderId(childCategory.getId());
            } else {
                mModel.delete(childCategory);
                mModel.deleteSizeListByFolderId(childCategory.getId());
                mModel.getMainFragmentAdapter().nodeRemoveData(parentCategory, childCategory);
            }

        });
        builder.setNegativeButton("CANCEL", null);
        builder.show();
    }
}