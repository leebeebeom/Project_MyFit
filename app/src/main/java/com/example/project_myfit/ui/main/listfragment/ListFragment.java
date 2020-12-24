package com.example.project_myfit.ui.main.listfragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.example.project_myfit.MainActivityViewModel;
import com.example.project_myfit.R;
import com.example.project_myfit.databinding.FragmentListBinding;
import com.example.project_myfit.databinding.ItemDialogEditTextBinding;
import com.example.project_myfit.databinding.TitleListBinding;
import com.example.project_myfit.ui.main.listfragment.adapter.ListFragmentAdapter;
import com.example.project_myfit.ui.main.listfragment.database.ListFolder;
import com.example.project_myfit.ui.main.listfragment.database.Size;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;

import java.util.List;


public class ListFragment extends Fragment {

    public static final String TAG = "로그";
    private ListViewModel mModel;
    private FragmentListBinding mBinding;
    private MainActivityViewModel mActivityModel;
    private Animation rotateOpen, rotateClose, fromBottomAdd, toBottomAdd, fromBottomFolder, toBottomAddFolder;
    private boolean isFabOpened = false;
    private ListFragmentAdapter mAdapter;
    private ItemDialogEditTextBinding mDialogEditText;
    private List<ListFolder> mCurrentFolderList;
    private List<Size> mCurrentSizeList;
    private boolean mRefresh;
    private boolean mAddRenew;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: 호출");
        //Binding
        mBinding = FragmentListBinding.inflate(getLayoutInflater());
        mDialogEditText = ItemDialogEditTextBinding.inflate(getLayoutInflater());
        //Activity View Model
        mActivityModel = new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);
        //Set Title
        setTitle();
        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onActivityCreated: 호출");
        super.onActivityCreated(savedInstanceState);
        //Initialize
        init();
        //Adapter
        mModel.getAllFolderList(mActivityModel.getChildCategory().getId()).observe(getViewLifecycleOwner(), listFolders -> {
            //Set Current Folder List
            mCurrentFolderList = listFolders;
            //Set largest Order
            mModel.setLargestOrder(mActivityModel.getChildCategory().getId());
            if (mRefresh) {
                mAdapter.setList(listFolders);
                mModel.getSizeList(mActivityModel.getChildCategory().getId()).observe(getViewLifecycleOwner(), sizeList -> {
                    mCurrentSizeList = sizeList;
                    mAdapter.addData(sizeList);
                });
                mRefresh = false;
            }
            if (mAddRenew) {
                mAdapter.addData(listFolders.size() - 1, listFolders.get(listFolders.size() - 1));
                mAddRenew = false;
            }
        });
        //Click listener
        mAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            if (view.getId() == R.id.edit_icon_folder) {
                ListFolder listFolder = (ListFolder) mAdapter.getItem(position);
                showEditFolderDialog(listFolder);
                closeLayout(position);
            } else if (view.getId() == R.id.delete_icon_folder) {
                ListFolder listFolder = (ListFolder) mAdapter.getItem(position);
                showDeleteFolderDialog(listFolder);
                closeLayout(position);
            }
        });

        //Fab Animation
        mBinding.fabMain.setOnClickListener(v -> {
            setVisibility(isFabOpened);
            setAnimation(isFabOpened);
            setClickable(isFabOpened);
            isFabOpened = !isFabOpened;
        });
        mBinding.fabFolder.setOnClickListener(v -> showAddFolderDialog());
        mBinding.fabAdd.setOnClickListener(v -> Navigation.findNavController(requireView()).navigate(R.id.action_listFragment_to_inputFragment));
    }

    //Set Title
    private void setTitle() {
        TitleListBinding titleBinding = TitleListBinding.inflate(getLayoutInflater());
        titleBinding.setChildCategory(mActivityModel.getChildCategory().getChildCategory());
        ActionBar actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
        actionBar.setCustomView(titleBinding.getRoot());
    }

    //Initialize
    private void init() {
        //View Model
        mModel = new ViewModelProvider(this).get(ListViewModel.class);
        //Refresh On
        mRefresh = true;
        //Recycler View, Adapter
        mAdapter = new ListFragmentAdapter();
        mBinding.listRecyclerView.setHasFixedSize(true);
        mBinding.listRecyclerView.setAdapter(mAdapter);
        //Fab Animation
        rotateOpen = AnimationUtils.loadAnimation(requireContext(), R.anim.rotate_open);
        rotateClose = AnimationUtils.loadAnimation(requireContext(), R.anim.rotate_close);
        fromBottomAdd = AnimationUtils.loadAnimation(requireContext(), R.anim.add_from_bottom);
        toBottomAdd = AnimationUtils.loadAnimation(requireContext(), R.anim.add_to_bottom);
        fromBottomFolder = AnimationUtils.loadAnimation(requireContext(), R.anim.folder_from_bottom);
        toBottomAddFolder = AnimationUtils.loadAnimation(requireContext(), R.anim.folder_to_bottom);

    }

    //Fab Animation
    private void setAnimation(boolean isFabOpened) {
        if (!isFabOpened) {
            mBinding.fabAdd.startAnimation(fromBottomAdd);
            mBinding.fabFolder.startAnimation(fromBottomFolder);
            mBinding.fabMain.startAnimation(rotateOpen);
        } else {
            mBinding.fabAdd.startAnimation(toBottomAdd);
            mBinding.fabFolder.startAnimation(toBottomAddFolder);
            mBinding.fabMain.startAnimation(rotateClose);
        }
    }

    //Fab Visibility
    private void setVisibility(boolean isFabOpened) {
        if (!isFabOpened) {
            mBinding.fabAdd.setVisibility(View.VISIBLE);
            mBinding.fabFolder.setVisibility(View.VISIBLE);
        } else {
            mBinding.fabAdd.setVisibility(View.INVISIBLE);
            mBinding.fabFolder.setVisibility(View.INVISIBLE);
        }
    }

    //Fab Clickable
    private void setClickable(boolean isFabOpened) {
        mBinding.fabAdd.setClickable(!isFabOpened);
        mBinding.fabFolder.setClickable(!isFabOpened);
    }

    //Close Layout
    private void closeLayout(int position) {
        ((SwipeRevealLayout) mAdapter.getViewByPosition(position, R.id.swipeLayout)).close(true);
    }

    //Show Add Folder Dialog
    private void showAddFolderDialog() {
        setEditText("");
        int largestOrder = mModel.getLargestOrder() + 1;
        MaterialAlertDialogBuilder builder = getDialogBuilder().setTitle("Add Folder");
        builder.setPositiveButton("DONE", (dialog, which) -> {
            mModel.insertFolder(new ListFolder(mDialogEditText.editTextDialog.getText().toString(), mActivityModel.getChildCategory().getId(), largestOrder));
            mAddRenew = true;// -> Renew
            Snackbar.make(requireActivity().findViewById(R.id.coordinator_layout), "폴더 추가됨", BaseTransientBottomBar.LENGTH_LONG)
                    .setAnchorView(R.id.bottom_nav)
                    .setAction("Undo", v -> {
                        ListFolder addedListFolder = mCurrentFolderList.get(mCurrentFolderList.size() - 1);
                        if (getLastPosition() == mAdapter.getItemPosition(addedListFolder)) {
                            mRefresh = true;
                        } else {
                            mAdapter.remove(addedListFolder);
                        }
                        mModel.deleteFolder(addedListFolder);
                    }).show();
        }).show();
    }

    //Show Edit Folder Dialog
    private void showEditFolderDialog(ListFolder listFolder) {
        String oldFolderName = listFolder.getFolderName();
        setEditText(oldFolderName);
        getDialogBuilder()
                .setTitle("Edit Folder")
                .setView(mDialogEditText.getRoot())
                .setPositiveButton("DONE", (dialog, which) -> {
                    listFolder.setFolderName(mDialogEditText.editTextDialog.getText().toString());
                    if (getLastPosition() == mAdapter.getItemPosition(listFolder)) {
                        mRefresh = true;
                    } else {
                        mAdapter.setData(getIndex(mCurrentFolderList, listFolder), listFolder);
                    }
                    mModel.updateFolder(listFolder);
                    Snackbar.make(requireActivity().findViewById(R.id.coordinator_layout), "폴더 수정됨", BaseTransientBottomBar.LENGTH_LONG)
                            .setAnchorView(R.id.bottom_nav)
                            .setAction("Undo", v -> {
                                listFolder.setFolderName(oldFolderName);
                                if (getLastPosition() == mAdapter.getItemPosition(listFolder)) {
                                    mRefresh = true;
                                } else {
                                    mAdapter.setData(getIndex(mCurrentFolderList, listFolder), listFolder);
                                }
                                mModel.updateFolder(listFolder);

                            });
                }).show();
    }

    //Show Delete Folder Dialog
    private void showDeleteFolderDialog(ListFolder listFolder) {
        int index = getIndex(mCurrentFolderList, listFolder);
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext(), R.style.myAlertDialog)
                .setTitle("확인")
                .setMessage("삭제하시겠습니까?")
                .setNegativeButton("CANCEL", null)
                .setPositiveButton("DONE", (dialog, which) -> {
                    if (getLastPosition() == mAdapter.getItemPosition(listFolder)) {
                        mRefresh = true;
                    } else {
                        mAdapter.remove(listFolder);
                    }
                    mModel.deleteFolder(listFolder);
                    Snackbar.make(requireActivity().findViewById(R.id.coordinator_layout), "폴더 삭제됨", BaseTransientBottomBar.LENGTH_LONG)
                            .setAnchorView(R.id.bottom_nav)
                            .setAction("Undo", v -> {
                                mAdapter.addData(index, listFolder);
                                mModel.insertFolder(listFolder);
                            }).show();
                });
        builder.show();
    }

    //Get Dialog Builder
    @NotNull
    private MaterialAlertDialogBuilder getDialogBuilder() {
        return new MaterialAlertDialogBuilder(requireContext(), R.style.myAlertDialog)
                .setView(mDialogEditText.getRoot())
                .setNegativeButton("CANCEL", null)
                .setOnDismissListener(dialog -> ((ViewGroup) mDialogEditText.getRoot().getParent()).removeAllViews());
    }

    //Set Edit Text
    private void setEditText(String setText) {
        mDialogEditText.setHint("Folder Name");
        mDialogEditText.setSetText(setText);
        mDialogEditText.setPlaceHolder("ex)Nike or Favorite");
    }

    //Get Folder Index
    private int getIndex(List<ListFolder> mCurrentFolderList, ListFolder listFolder) {
        int index = 0;
        for (ListFolder l : mCurrentFolderList) {
            if (l.getId() == listFolder.getId()) {
                break;
            }
            index++;
        }
        return index;
    }

    //Get Adapter Last Position
    private int getLastPosition() {
        return mAdapter.getData().size() - 1;
    }

}