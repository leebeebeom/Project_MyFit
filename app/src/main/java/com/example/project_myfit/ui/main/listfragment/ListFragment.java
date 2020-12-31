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
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.ItemTouchHelper;

import com.example.project_myfit.MainActivityViewModel;
import com.example.project_myfit.R;
import com.example.project_myfit.databinding.FragmentListBinding;
import com.example.project_myfit.databinding.ItemDialogEditTextBinding;
import com.example.project_myfit.ui.main.listfragment.adapter.ListDragCallBack;
import com.example.project_myfit.ui.main.listfragment.adapter.ListFolderAdapter;
import com.example.project_myfit.ui.main.listfragment.adapter.ListSizeAdapter;
import com.example.project_myfit.ui.main.listfragment.database.ListFolder;
import com.example.project_myfit.ui.main.listfragment.database.Size;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;

import java.util.List;


public class ListFragment extends Fragment {

    private ListViewModel mModel;
    private FragmentListBinding mBinding;
    private MainActivityViewModel mActivityModel;
    private Animation rotateOpen, rotateClose, fromBottom, toBottom;
    private boolean isFabOpened;
    private ListFolderAdapter mFolderAdapter;
    private ListSizeAdapter mSizeAdapter;
    private ItemDialogEditTextBinding mEditTextBinding;
    private boolean mFolderRefresh;
    private List<ListFolder> mCurrentFolderList;
    private FloatingActionButton mActivityFab;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        //Binding
        mBinding = FragmentListBinding.inflate(inflater);
        //Activity View Model
        mActivityModel = new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);
        mBinding.setCategory(mActivityModel.getCategory());
        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //Initialize
        init();
        //Set Click Listener
        setClickListener();
        //Folder Renew
        mModel.getFolderList(mActivityModel.getCategory().getId()).observe(getViewLifecycleOwner(), listFolders -> {
            mCurrentFolderList = listFolders;
            mModel.setFolderLargestOrder(mActivityModel.getCategory().getId());
            if (mFolderRefresh) {
                mFolderAdapter.setItem(listFolders, mModel);
                mBinding.recyclerFolder.setAdapter(mFolderAdapter);
                mFolderRefresh = false;
            } else {
                mFolderAdapter.updateDiffUtils(listFolders);
            }
        });
        //Content Renew
        mModel.getSizeList(mActivityModel.getCategory().getId()).observe(getViewLifecycleOwner(), sizeList -> {
            mSizeAdapter.setItem(sizeList);
            mBinding.recyclerContent.setAdapter(mSizeAdapter);
        });
        //Folder Touch Helper
        ItemTouchHelper mTouchHelperFolder = new ItemTouchHelper(new ListDragCallBack(mFolderAdapter));
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
        mSizeAdapter.setOnSizeClickListener(new ListSizeAdapter.OnSizeClickListener() {
            @Override
            public void onItemClick(Size size) {
                mActivityModel.setSize(size);
                Navigation.findNavController(requireView()).navigate(R.id.action_listFragment_to_outputFragment);
            }

            @Override
            public void onEditClick(Size size, int position) {

            }

            @Override
            public void onDeleteClick(Size size) {

            }
        });
    }

    private void setClickListener() {
        //Fab Animation
        mActivityFab.setOnClickListener(v -> {
            setVisibility(isFabOpened);
            setAnimation(isFabOpened);
            setClickable(isFabOpened);
            isFabOpened = !isFabOpened;
        });
        //Add Fab Clicked
        mBinding.listFabAdd.setOnClickListener(v -> {
            mActivityFab.performClick();
            Navigation.findNavController(requireView()).navigate(R.id.action_listFragment_to_inputFragment);
        }); // TODO CIRCLE REVEAL
        //Add Folder Fab Clicked
        mBinding.listFabFolder.setOnClickListener(v -> {
            mActivityFab.performClick();
            showAddFolderDialog();
        });
        //Home Icon Click
        mBinding.listIconHome.setOnClickListener(v -> Navigation.findNavController(requireView()).navigate(R.id.action_listFragment_to_mainFragment));
    }

    private void showDeleteFolderDialog(ListFolder listFolder) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext(), R.style.myAlertDialog)
                .setTitle("확인")
                .setMessage("폴더를 삭제하시겠습니까?")
                .setNegativeButton("CANCEL", null)
                .setPositiveButton("DONE", (dialog, which) -> {
                    mModel.deleteFolder(listFolder);
                    Snackbar.make(requireActivity().findViewById(R.id.coordinator_layout), "폴더 삭제됨", BaseTransientBottomBar.LENGTH_LONG)
                            .setAnchorView(mActivityFab)
                            .setAction("Undo", v -> {
                                mModel.insertFolder(listFolder);
                                showUndoConfirmSnackBar();
                            }).show();
                });
        builder.show();
    }

    //Initialize
    private void init() {
        //Activity View Model ListFolder Initialize
        mActivityModel.setListFolder(null);
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
        mActivityFab = requireActivity().findViewById(R.id.activity_fab);
        rotateOpen = AnimationUtils.loadAnimation(requireContext(), R.anim.rotate_open);
        rotateClose = AnimationUtils.loadAnimation(requireContext(), R.anim.rotate_close);
        fromBottom = AnimationUtils.loadAnimation(requireContext(), R.anim.folder_from_bottom);
        toBottom = AnimationUtils.loadAnimation(requireContext(), R.anim.folder_to_bottom);
    }

    //Fab Animation
    private void setAnimation(boolean isFabOpened) {
        if (!isFabOpened) {
            mActivityFab.startAnimation(rotateOpen);
            mBinding.listFabAdd.startAnimation(fromBottom);
            mBinding.listFabFolder.startAnimation(fromBottom);
        } else {
            mActivityFab.startAnimation(rotateClose);
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
                            .setAnchorView(mActivityFab)
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
                            .setAnchorView(mActivityFab)
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
                .setAnchorView(mActivityFab).show();
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

}