package com.example.project_myfit.ui.main;

import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.entity.node.BaseNode;
import com.chad.library.adapter.base.listener.OnItemDragListener;
import com.chad.library.adapter.base.module.BaseDraggableModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.example.project_myfit.MainActivityViewModel;
import com.example.project_myfit.R;
import com.example.project_myfit.databinding.FragmentMainBinding;
import com.example.project_myfit.ui.main.adapter.MainFragmentAdapter;
import com.example.project_myfit.ui.main.database.ChildCategory;
import com.example.project_myfit.ui.main.database.ParentCategory;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class MainFragment extends Fragment implements DragCallBack.DragListener {
    private MainViewModel mModel;
    private FragmentMainBinding mBinding;
    private MainActivityViewModel mActivityModel;
    private boolean mRefreshOn;
    private boolean mAddRenewalOn = false;
    private ItemTouchHelper mItemTouchHelper;
    private MainFragmentAdapter mAdapter;
    private ParentCategory addParentCategory;
    private List<ChildCategory> mCurrentChildList;

    /*
    TODO
     최초실행 문제 해결 
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        //Binding
        mBinding = FragmentMainBinding.inflate(inflater);
        //Set Custom View to ActionBar title
        setTitle();
        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //Initialize
        init();
        //Get Child By Order
        mModel.getAllChildByOrder().observe(getViewLifecycleOwner(), childCategoryList -> {
            //Set Current Child List
            mCurrentChildList = childCategoryList;
            mModel.setCurrentChildList(childCategoryList);
            //Set Largest Order
            mModel.setLargestOrder();
            //Refresh
            if (mRefreshOn) {
                mAdapter.setList(mModel.generateParent(childCategoryList));
                mRefreshOn = false;
            } else if (mAddRenewalOn) {
                //Add Renewal
                mAdapter.nodeAddData(addParentCategory, childCategoryList.get(childCategoryList.size() - 1));
                mAddRenewalOn = false;
            }
            //for First Run
            if (mModel.isFirstRun()) {
                mAdapter.setList(mModel.generateParent(childCategoryList));
            }
        });
        //Click Listener
        setClickListener();
        //Drag
        setDrag();
    }

    //First Run Check
    @Override
    public void onStop() {
        super.onStop();
        if (mModel.isFirstRun()) {
            mModel.setPreferences();
        }
    }

    //Set Custom View to ActionBar title
    private void setTitle() {
        ActionBar actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setCustomView(R.layout.title_main);
        }
    }

    //Initialize
    private void init() {
        //View Model
        mModel = new ViewModelProvider(this).get(MainViewModel.class);
        //Activity View Model
        mActivityModel = new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);
        //List Refresh On
        mRefreshOn = true;
        //Adapter
        mAdapter = new MainFragmentAdapter(this);
        //Recycler View
        mBinding.recyclerView.setHasFixedSize(true);
        mBinding.recyclerView.setAdapter(mAdapter);
    }

    //Click Listener
    private void setClickListener() {
        mAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            // Add
            if (view.getId() == R.id.add_icon) {
                //Save Clicked Parent Category
                addParentCategory = (ParentCategory) adapter.getItem(position);
                //Show Add Dialog
                showAddDialog(addParentCategory);
            }// Edit
            else if (view.getId() == R.id.edit_icon) {
                //Casting
                ChildCategory childCategory = (ChildCategory) adapter.getItem(position);
                //Get Parent Category
                ParentCategory parentCategory = (ParentCategory) mAdapter.getItem(mAdapter.findParentNode(position));
                //Get Child Index
                int childIndex = getChildIndex(childCategory, parentCategory);
                //Show Edit Dialog
                showEditDialog(childCategory, parentCategory, childIndex, position);
            }// Delete
            else if (view.getId() == R.id.delete_icon) {
                //Casting
                ChildCategory childCategory = (ChildCategory) adapter.getItem(position);
                //Get Parent Category
                ParentCategory parentCategory = (ParentCategory) mAdapter.getItem(mAdapter.findParentNode(position));
                //Get Child Index
                int childIndex = getChildIndex(childCategory, parentCategory);
                //Show Delete Dialog
                showDeleteDialog(childCategory, parentCategory, childIndex, position);
            } else if (view.getId() == R.id.item_child_root) {
                //Clicked Child
                ChildCategory childCategory = (ChildCategory) mAdapter.getItem(position);
                mActivityModel.setChildCategory(childCategory);
                Navigation.findNavController(requireView()).navigate(R.id.action_mainFragment_to_listFragment);
            }
        });
    }

    //Drag
    private void setDrag() {
        BaseDraggableModule draggableModule = new BaseDraggableModule(mAdapter);
        draggableModule.setOnItemDragListener(onItemDragListener);
        mItemTouchHelper = new ItemTouchHelper(new DragCallBack(draggableModule, mAdapter));
        mItemTouchHelper.attachToRecyclerView(mBinding.recyclerView);
    }

    //Add Dialog
    private void showAddDialog(ParentCategory addParentCategory) {
        //Edit Text
        FrameLayout frameLayout = (FrameLayout) View.inflate(requireContext(), R.layout.item_dialog_edit_text, null);
        TextInputEditText editText = frameLayout.findViewById(R.id.editText_dialog);
        //builder
        MaterialAlertDialogBuilder builder = getDialogBuilder(frameLayout);
        builder.setTitle("Add");
        //DONE Clicked
        builder.setPositiveButton("DONE", (dialog, which) -> {
            //Get Largest Order
            int largestOrder = mModel.getLargestOrder();
            largestOrder++;
            //Create new ChildCategory
            ChildCategory newChildCategory;
            newChildCategory = new ChildCategory(editText.getText().toString(), addParentCategory.getParentCategory(), largestOrder);
            //insert
            mAddRenewalOn = true; // -> Renewal
            mModel.insert(newChildCategory);
            //Snack Bar
            Snackbar.make(requireActivity().findViewById(R.id.coordinator_layout), "카테고리 추가됨", BaseTransientBottomBar.LENGTH_LONG)
                    .setAnchorView(R.id.bottom_nav)
                    //Undo Clicked
                    .setAction("Undo", v -> {
                        //Last Added Data
                        ChildCategory addedChildCategory = mModel.getCurrentChildList().get(mModel.getCurrentChildList().size() - 1);
                        //Adapter Last Item(Parent or Child)
                        BaseNode baseNode = mAdapter.getData().get(mAdapter.getData().size() - 1);
                        if (baseNode instanceof ParentCategory) {
                            //if Last Item is Parent
                            mAdapter.nodeRemoveData(addParentCategory, addedChildCategory);
                        } else {
                            //if baseNode is Child
                            ChildCategory lastItem = (ChildCategory) baseNode;
                            if (lastItem.getId() == addedChildCategory.getId()) {
                                //if addedChildCategory is last item
                                mRefreshOn = true; // Refresh
                            } else {
                                //if addedChildCategory is not last item
                                mAdapter.nodeRemoveData(addParentCategory, addedChildCategory);
                            }
                        }
                        //delete
                        mModel.delete(addedChildCategory);
                        snackBarUndoConFirmed();
                    }).show();
        });
        builder.show();
    }

    //Edit Dialog
    private void showEditDialog(ChildCategory childCategory, ParentCategory parentCategory, int childIndex, int position) {
        //Save oldChildCategory Name
        String oldChildCategoryName = childCategory.getChildCategory();
        //Get Last Position
        int LastPosition = mAdapter.getData().size() - 1;
        //Edit Text
        FrameLayout frameLayout = (FrameLayout) View.inflate(requireContext(), R.layout.item_dialog_edit_text, null);
        TextInputEditText editText = frameLayout.findViewById(R.id.editText_dialog);
        //Builder
        MaterialAlertDialogBuilder builder = getDialogBuilder(frameLayout);
        builder.setTitle("Edit");
        //DONE Clicked
        builder.setPositiveButton("DONE", (dialog, which) -> {
            //childCategory = In Adapter
            childCategory.setChildCategory(editText.getText().toString());
            if (LastPosition == position) {
                //if ChildCategory is Last
                //Refresh
                mRefreshOn = true;
                //Update
                mModel.update(childCategory);
            } else {
                //if Not
                mModel.update(childCategory);
                mAdapter.nodeSetData(parentCategory, childIndex, childCategory);
            }
            //Snack Bar
            Snackbar.make(requireActivity().findViewById(R.id.coordinator_layout), "카테고리 수정됨", BaseTransientBottomBar.LENGTH_LONG)
                    .setAnchorView(R.id.bottom_nav)
                    //Undo
                    .setAction("Undo", v -> {
                        //Old Child Category
                        childCategory.setChildCategory(oldChildCategoryName);
                        if (LastPosition == position) {
                            //if ChildCategory is Last
                            //Refresh
                            mRefreshOn = true;
                        } else {
                            //if Not
                            mAdapter.nodeSetData(parentCategory, childIndex, childCategory);
                        }
                        //Update
                        mModel.update(childCategory);
                        //Undo Confirmed
                        snackBarUndoConFirmed();
                    }).show();
        });
        builder.show();
    }

    //Delete Dialog
    private void showDeleteDialog(ChildCategory childCategory, ParentCategory parentCategory, int childIndex, int position) {
        //To be Deleted SizeList
        mModel.getAllSizeByFolder(childCategory.getId());
        //Get Last Position
        int LastPosition = mAdapter.getData().size() - 1;
        //Builder
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext(), R.style.myAlertDialog);
        builder.setTitle("Warning");
        builder.setMessage("카테고리 안 모든 아이템이 삭제됩니다.\n삭제하시겠습니까?");
        builder.setNegativeButton("CANCEL", null);
        //DONE Clicked
        builder.setPositiveButton("DONE", (dialog, which) -> {
            if (LastPosition == position) {
                //if ChildCategory is Last
                //Refresh
                mRefreshOn = true;
            } else {
                //if Not
                mAdapter.nodeRemoveData(parentCategory, childCategory);
            }
            //Delete
            mModel.delete(childCategory);
            mModel.deleteSizeByFolder(childCategory.getId());
            //Snack Bar
            Snackbar.make(requireActivity().findViewById(R.id.coordinator_layout), "카테고리 삭제됨", BaseTransientBottomBar.LENGTH_LONG)
                    .setAnchorView(R.id.bottom_nav)
                    //Undo
                    .setAction("Undo", v -> {
                        if (LastPosition == position) {
                            //if ChildCategory is Last
                            mRefreshOn = true;
                        } else {
                            //if Not
                            mAdapter.nodeAddData(parentCategory, childIndex, childCategory);
                        }
                        //insert
                        mModel.insert(childCategory);
                        mModel.restoreDeletedSize();
                        //Undo ConFirmed
                        snackBarUndoConFirmed();
                    }).show();
        });
        builder.show();
    }

    //getChildIndex
    private int getChildIndex(ChildCategory childCategory, ParentCategory parentCategory) {
        int childIndex = 0;
        for (BaseNode child : parentCategory.getChildNode()) {
            if (!child.equals(childCategory)) {
                break;
            }
            childIndex++;
        }
        return childIndex;
    }

    //Get MaterialAlertDialogBuilder
    @NotNull
    private MaterialAlertDialogBuilder getDialogBuilder(FrameLayout frameLayout) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext(), R.style.myAlertDialog);
        builder.setView(frameLayout);
        builder.setNegativeButton("CANCEL", null);
        return builder;
    }

    //Undo Confirmed
    private void snackBarUndoConFirmed() {
        Snackbar.make(requireActivity().findViewById(R.id.coordinator_layout), "취소됨", BaseTransientBottomBar.LENGTH_SHORT)
                .setAnchorView(R.id.bottom_nav)
                .show();
    }

    //Start Drag
    @Override
    public void onStartDrag(BaseViewHolder holder) {
        mItemTouchHelper.startDrag(holder);
    }

    //Drag Listener
    private final OnItemDragListener onItemDragListener = new OnItemDragListener() {
        @Override
        public void onItemDragStart(RecyclerView.ViewHolder viewHolder, int pos) {
            BaseViewHolder holder = (BaseViewHolder) viewHolder;
            TypedValue typedValue = new TypedValue();
            requireContext().getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
            int colorPrimary = typedValue.data;
            holder.itemView.findViewById(R.id.divider_top1).setVisibility(View.VISIBLE);
            holder.itemView.findViewById(R.id.divider_top2).setVisibility(View.VISIBLE);
            holder.itemView.setBackgroundColor(colorPrimary);
        }

        @Override
        public void onItemDragMoving(RecyclerView.ViewHolder source, int from, RecyclerView.ViewHolder target, int to) {
            //If target is not parent
            if (target.getItemViewType() != -1) {
                ChildCategory sourceItem = (ChildCategory) mAdapter.getItem(to);
                ChildCategory targetItem = (ChildCategory) mAdapter.getItem(from);
                int sourceIndex = 0;
                for (ChildCategory childCategory : mCurrentChildList) {
                    if (childCategory.getId() == sourceItem.getId()) {
                        break;
                    }
                    sourceIndex++;
                }
                int targetIndex = 0;
                for (ChildCategory childCategory : mCurrentChildList) {
                    if (childCategory.getId() == targetItem.getId()) {
                        break;
                    }
                    targetIndex++;
                }
                orderChange(sourceIndex, targetIndex);
            }
        }

        @Override
        public void onItemDragEnd(RecyclerView.ViewHolder viewHolder, int pos) {
            mModel.updateOrder(mCurrentChildList);
            BaseViewHolder holder = (BaseViewHolder) viewHolder;
            holder.itemView.findViewById(R.id.divider_top1).setVisibility(View.GONE);
            holder.itemView.findViewById(R.id.divider_top2).setVisibility(View.GONE);
            holder.itemView.setBackgroundColor(0);
        }
    };

    //Order Change
    private void orderChange(int from, int to) {
        //Dragging Down
        if (from < to) {
            for (int i = from; i < to; i++) {
                Collections.swap(mModel.getCurrentChildList(), i, i + 1);
                int targetOrder = mModel.getCurrentChildList().get(i).getOrderNumber();
                int sourceOrder = mModel.getCurrentChildList().get(i + 1).getOrderNumber();
                mCurrentChildList.get(i).setOrderNumber(sourceOrder);
                mCurrentChildList.get(i + 1).setOrderNumber(targetOrder);
            }
        }//Dragging Up
        else {
            for (int i = from; i > to; i--) {
                Collections.swap(mModel.getCurrentChildList(), i, i - 1);
                int targetOrder = mModel.getCurrentChildList().get(i).getOrderNumber();
                int sourceOrder = mModel.getCurrentChildList().get(i - 1).getOrderNumber();
                mCurrentChildList.get(i).setOrderNumber(sourceOrder);
                mCurrentChildList.get(i - 1).setOrderNumber(targetOrder);
            }
        }
    }
}