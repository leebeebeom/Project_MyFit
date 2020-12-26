package com.example.project_myfit.ui.main.listfragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.project_myfit.MainActivityViewModel;
import com.example.project_myfit.R;
import com.example.project_myfit.databinding.FragmentListBinding;
import com.example.project_myfit.databinding.ItemDialogEditTextBinding;
import com.example.project_myfit.databinding.TitleListBinding;
import com.example.project_myfit.ui.main.listfragment.adapter.ListFolderAdapter;
import com.example.project_myfit.ui.main.listfragment.adapter.ListSizeAdapter;
import com.example.project_myfit.ui.main.listfragment.database.ListFolder;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;


public class ListFragment extends Fragment {

    private ListViewModel mModel;
    private FragmentListBinding mBinding;
    private MainActivityViewModel mActivityModel;
    private Animation rotateOpen, rotateClose, fromBottomAdd, toBottomAdd, fromBottomFolder, toBottomAddFolder;
    private boolean isFabOpened = false;
    private ListFolderAdapter mFolderAdapter;
    private ListSizeAdapter mSizeAdapter;
    private ItemDialogEditTextBinding mEditTextBinding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        //Binding
        mBinding = FragmentListBinding.inflate(getLayoutInflater());
        //Activity View Model
        mActivityModel = new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);
        //Set Title
        setTitle();
        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //Initialize
        init();
        //Fab Animation
        mBinding.fabMain.setOnClickListener(v -> {
            setVisibility(isFabOpened);
            setAnimation(isFabOpened);
            setClickable(isFabOpened);
            isFabOpened = !isFabOpened;
        });
        //Add Fab Clicked
        mBinding.fabAdd.setOnClickListener(v -> Navigation.findNavController(requireView()).navigate(R.id.action_listFragment_to_inputFragment));
        //Add Folder Fab Clicked
        mBinding.fabFolder.setOnClickListener(v -> showAddFolderDialog());
        //Renew
        mModel.getFolderList(mActivityModel.getChildCategory().getId()).observe(getViewLifecycleOwner(), listFolders -> {
            mFolderAdapter.setItem(listFolders);
            mBinding.recyclerViewFolder.setAdapter(mFolderAdapter);
            mModel.setFolderLargestOrder(mActivityModel.getChildCategory().getId());
        });
        mModel.getSizeList(mActivityModel.getChildCategory().getId()).observe(getViewLifecycleOwner(), sizeList -> {
            mSizeAdapter.setItem(sizeList);
            mBinding.recyclerViewContent.setAdapter(mSizeAdapter);
        });
    }

    //Set Title
    private void setTitle() {
        TitleListBinding titleBinding = TitleListBinding.inflate(getLayoutInflater());
        titleBinding.setChildCategory(mActivityModel.getChildCategory().getChildCategory());
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setCustomView(titleBinding.getRoot());
    }

    //Initialize
    private void init() {
        //View Model
        mModel = new ViewModelProvider(this).get(ListViewModel.class);
        //Edit Text
        mEditTextBinding = ItemDialogEditTextBinding.inflate(getLayoutInflater());
        //Recycler View, Adapter
        mFolderAdapter = new ListFolderAdapter();
        mSizeAdapter = new ListSizeAdapter();
        mBinding.recyclerViewFolder.setHasFixedSize(true);
        mBinding.recyclerViewContent.setHasFixedSize(true);
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
            mBinding.fabMain.startAnimation(rotateOpen);
            mBinding.fabAdd.startAnimation(fromBottomAdd);
            mBinding.fabFolder.startAnimation(fromBottomFolder);
        } else {
            mBinding.fabMain.startAnimation(rotateClose);
            mBinding.fabAdd.startAnimation(toBottomAdd);
            mBinding.fabFolder.startAnimation(toBottomAddFolder);
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

    //Add Folder Dialog
    private void showAddFolderDialog() {
        //Set Edit Text
        setEditText("");
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext(), R.style.myAlertDialog)
                .setTitle("Add Folder")
                .setView(mEditTextBinding.getRoot())
                .setNegativeButton("CANCEL", null)
                .setPositiveButton("DONE", (dialog, which) -> {
                    mModel.insertFolder(new ListFolder(mEditTextBinding.editTextDialog.getText().toString(),
                            mActivityModel.getChildCategory().getId(),
                            mModel.getLargestOrder(),
                            0));
                })
                .setOnDismissListener(dialog -> {
                    ((ViewGroup)mEditTextBinding.getRoot().getParent()).removeAllViews();
                });
        builder.show();
    }

    private void setEditText(String setText) {
        mEditTextBinding.setHint("Folder Name");
        mEditTextBinding.setPlaceHolder("ex)Nike, Adidas");
        mEditTextBinding.setSetText(setText);
    }
}