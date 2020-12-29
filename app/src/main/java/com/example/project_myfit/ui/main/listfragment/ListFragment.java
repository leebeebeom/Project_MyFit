package com.example.project_myfit.ui.main.listfragment;

import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_myfit.MainActivityViewModel;
import com.example.project_myfit.R;
import com.example.project_myfit.databinding.FragmentListBinding;
import com.example.project_myfit.databinding.ItemDialogEditTextBinding;
import com.example.project_myfit.ui.main.adapter.MainDragCallBack;
import com.example.project_myfit.ui.main.listfragment.adapter.ListDragCallBack;
import com.example.project_myfit.ui.main.listfragment.adapter.ListFolderAdapter;
import com.example.project_myfit.ui.main.listfragment.adapter.ListSizeAdapter;
import com.example.project_myfit.ui.main.listfragment.database.ListFolder;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;

import java.util.List;


public class ListFragment extends Fragment implements MainDragCallBack.StartDragListener {

    private ListViewModel mModel;
    private FragmentListBinding mBinding;
    private MainActivityViewModel mActivityModel;
    private Animation rotateOpen, rotateClose, fromBottom, toBottom;
    private boolean isFabOpened;
    private ListFolderAdapter mFolderAdapter;
    private ListSizeAdapter mSizeAdapter;
    private ItemDialogEditTextBinding mEditTextBinding;
    private ItemTouchHelper mTouchHelperFolder;
    private boolean mFolderRefresh;
    private List<ListFolder> mCurrentFolderList;

    //TODO FAB IN OUT ANIMATION, FOLDER LONG CLICK
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        //Binding
        mBinding = FragmentListBinding.inflate(inflater);
        //Activity View Model
        mActivityModel = new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);
        //Up Button Disable
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //Initialize
        init();
        //Fab Animation
        mBinding.listFabMain.setOnClickListener(v -> {
            setVisibility(isFabOpened);
            setAnimation(isFabOpened);
            setClickable(isFabOpened);
            isFabOpened = !isFabOpened;
        });
        //Add Fab Clicked
        mBinding.listFabAdd.setOnClickListener(v -> Navigation.findNavController(requireView()).navigate(R.id.action_listFragment_to_inputFragment)); // TODO CIRCLE REVEAL
        //Add Folder Fab Clicked
        mBinding.listFabFolder.setOnClickListener(v -> {
            mBinding.listFabMain.performClick();
            showAddFolderDialog();
        });
        //Folder Renew
        mModel.getFolderList(mActivityModel.getCategory().getId()).observe(getViewLifecycleOwner(), listFolders -> {
            mCurrentFolderList = listFolders;
            mModel.setFolderLargestOrder(mActivityModel.getCategory().getId());
            if (mFolderRefresh) {
                mFolderAdapter.setItem(listFolders, this, mModel);
                mBinding.recyclerFolder.setAdapter(mFolderAdapter);
                mFolderRefresh = false;
            } else {
                mFolderAdapter.updateDiffUtils(listFolders);
            }
            if (listFolders.size() == 0) {
                mBinding.listTextFolders.setVisibility(View.GONE);
            } else {
                mBinding.listTextFolders.setVisibility(View.VISIBLE);
            }
        });
        //Content Renew
        mModel.getSizeList(mActivityModel.getCategory().getId()).observe(getViewLifecycleOwner(), sizeList -> {
            mSizeAdapter.setItem(sizeList);
            mBinding.recyclerContent.setAdapter(mSizeAdapter);
        });
        //Folder Touch Helper
        mTouchHelperFolder = new ItemTouchHelper(new ListDragCallBack(mFolderAdapter));
        mTouchHelperFolder.attachToRecyclerView(mBinding.recyclerFolder);
        mFolderAdapter.setOnFolderClickListener(new ListFolderAdapter.OnFolderClickListener() {
            @Override
            public void onItemClicked() {
            }

            @Override
            public void onEditClicked(ListFolder listFolder, int position) {
                showEditFolderDialog(listFolder, position);
            }

            @Override
            public void onDeleteClicked(ListFolder listFolder) {
                showDeleteFolderDialog(listFolder);
            }

        });
    }

    private void showDeleteFolderDialog(ListFolder listFolder) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext(), R.style.myAlertDialog)
                .setTitle("확인")
                .setMessage("폴더를 삭제하시겠습니까?")
                .setNegativeButton("CANCEL", null)
                .setPositiveButton("DONE", (dialog, which) -> {
                    mModel.deleteFolder(listFolder);
                    Snackbar.make(requireActivity().findViewById(R.id.coordinator_layout), "폴더 삭제됨", BaseTransientBottomBar.LENGTH_LONG)
                            .setAnchorView(R.id.list_fab_main)
                            .setAction("Undo", v -> {
                                mModel.insertFolder(listFolder);
                                showUndoConfirmSnackBar();
                            }).show();
                });
        builder.show();
    }

    //Initialize
    private void init() {
        //View Model
        mModel = new ViewModelProvider(this).get(ListViewModel.class);
        //Set Refresh
        mFolderRefresh = true;
        //Set Fab Open
        isFabOpened = false;
        //Edit Text
        mEditTextBinding = ItemDialogEditTextBinding.inflate(getLayoutInflater());
        //Recycler View, Adapter
        mFolderAdapter = new ListFolderAdapter();
        mSizeAdapter = new ListSizeAdapter(); // TODO
        mBinding.recyclerFolder.setHasFixedSize(true);
        mBinding.recyclerContent.setHasFixedSize(true); //TODO
        //Fab Animation
        rotateOpen = AnimationUtils.loadAnimation(requireContext(), R.anim.rotate_open);
        rotateClose = AnimationUtils.loadAnimation(requireContext(), R.anim.rotate_close);
        fromBottom = AnimationUtils.loadAnimation(requireContext(), R.anim.folder_from_bottom);
        toBottom = AnimationUtils.loadAnimation(requireContext(), R.anim.folder_to_bottom);
    }

    //Fab Animation
    private void setAnimation(boolean isFabOpened) {
        if (!isFabOpened) {
            mBinding.listFabMain.startAnimation(rotateOpen);
            mBinding.listFabAdd.startAnimation(fromBottom);
            mBinding.listFabFolder.startAnimation(fromBottom);
        } else {
            mBinding.listFabMain.startAnimation(rotateClose);
            mBinding.listFabAdd.startAnimation(toBottom);
            mBinding.listFabFolder.startAnimation(toBottom);
        }
    }

    //Fab Visibility
    private void setVisibility(boolean isFabOpened) {
        if (!isFabOpened) {
            mBinding.listFabAdd.setVisibility(View.VISIBLE);
            mBinding.listFabFolder.setVisibility(View.VISIBLE);
        } else {
            mBinding.listFabAdd.setVisibility(View.INVISIBLE);
            mBinding.listFabFolder.setVisibility(View.INVISIBLE);
        }
    }

    //Fab Clickable
    private void setClickable(boolean isFabOpened) {
        mBinding.listFabAdd.setClickable(!isFabOpened);
        mBinding.listFabFolder.setClickable(!isFabOpened);
    }

    //Add Folder Dialog
    private void showAddFolderDialog() {
        //Set Edit Text
        setEditText("");
        MaterialAlertDialogBuilder builder = getDialogBuilder();
        builder.setTitle("Add Folder")
                .setPositiveButton("DONE", (dialog, which) -> {
                    int largestOrder = mModel.getLargestOrder();
                    largestOrder++;
                    mModel.insertFolder(new ListFolder(mEditTextBinding.editTextDialog.getText().toString(),
                            mActivityModel.getCategory().getId(),
                            largestOrder,
                            0));
                    Snackbar.make(requireActivity().findViewById(R.id.coordinator_layout), "폴더 추가됨", BaseTransientBottomBar.LENGTH_LONG)
                            .setAnchorView(R.id.list_fab_main)
                            .setAction("Undo", v -> {
                                ListFolder addedListFolder = mCurrentFolderList.get(mCurrentFolderList.size() - 1);
                                mModel.deleteFolder(addedListFolder);
                                showUndoConfirmSnackBar();
                            }).show();
                });
        builder.show();
    }

    private void showEditFolderDialog(ListFolder listFolder, int position) {
        String oldFolderName = listFolder.getFolderName();
        setEditText(listFolder.getFolderName());
        getDialogBuilder()
                .setTitle("Edit Folder")
                .setPositiveButton("DONE", (dialog, which) -> {
                    listFolder.setFolderName(mEditTextBinding.editTextDialog.getText().toString());
                    mFolderAdapter.notifyItemChanged(position);
                    mModel.updateFolder(listFolder);
                    Snackbar.make(requireActivity().findViewById(R.id.coordinator_layout), "카테고리 수정됨", BaseTransientBottomBar.LENGTH_LONG)
                            .setAnchorView(R.id.list_fab_main)
                            .setAction("Undo", v -> {
                                listFolder.setFolderName(oldFolderName);
                                mFolderAdapter.notifyItemChanged(position);
                                mModel.updateFolder(listFolder);
                                showUndoConfirmSnackBar();
                            }).show();
                }).show();
    }

    private void showUndoConfirmSnackBar() {
        Snackbar.make(requireActivity().findViewById(R.id.coordinator_layout), "취소됨", BaseTransientBottomBar.LENGTH_LONG)
                .setAnchorView(R.id.list_fab_main).show();
    }

    @NotNull
    private MaterialAlertDialogBuilder getDialogBuilder() {
        return new MaterialAlertDialogBuilder(requireContext(), R.style.myAlertDialog)
                .setView(mEditTextBinding.getRoot())
                .setNegativeButton("CANCEL", null)
                .setOnDismissListener(dialog -> ((ViewGroup) mEditTextBinding.getRoot().getParent()).removeAllViews());
    }

    private void setEditText(String setText) {
        mEditTextBinding.setHint("Folder Name");
        mEditTextBinding.setPlaceHolder("ex)Nike, Adidas");
        mEditTextBinding.setSetText(setText);
        mEditTextBinding.editTextDialog.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        mEditTextBinding.editTextDialog.setFilters(new InputFilter[]{new InputFilter.LengthFilter(30)});
        mEditTextBinding.editTextDialog.setMaxLines(3);
        mEditTextBinding.editTextLayoutDialog.setCounterMaxLength(30);
    }

    @Override
    public void startDrag(RecyclerView.ViewHolder viewHolder) {
        mTouchHelperFolder.startDrag(viewHolder);
    }
}