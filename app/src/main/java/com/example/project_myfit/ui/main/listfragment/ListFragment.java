package com.example.project_myfit.ui.main.listfragment;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.project_myfit.MainActivityViewModel;
import com.example.project_myfit.R;
import com.example.project_myfit.databinding.FragmentListBinding;
import com.example.project_myfit.databinding.TitleListBinding;
import com.example.project_myfit.ui.main.listfragment.database.ListFolder;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;


public class ListFragment extends Fragment {

    public static final String TAG = "로그";
    private ListViewModel mModel;
    private FragmentListBinding mBinding;
    private MainActivityViewModel mActivityModel;
    private Animation rotateOpen, rotateClose, fromBottomAdd, toBottomAdd, fromBottomFolder, toBottomAddFolder;
    private boolean isFabOpened = false;

    /*
    TODO
     Folder Dao
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: 호출");
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
        Log.d(TAG, "onActivityCreated: 호출");
        super.onActivityCreated(savedInstanceState);
        //Initialize
        init();
        //Adapter
        mModel.getFolderList().observe(getViewLifecycleOwner(), listFolders -> {
            mModel.getAdapter().setList(listFolders);
        });
        //Fab Animation
        mBinding.fabMain.setOnClickListener(v -> onFabClicked());
        mBinding.fabFolder.setOnClickListener(v -> {
            showAddFolderDialog();
        });
        mBinding.fabAdd.setOnClickListener(v -> {
            Navigation.findNavController(requireView()).navigate(R.id.action_listFragment_to_inputFragment);
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: 호출");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: 호출");
    }

    //Set Title
    private void setTitle() {
        TitleListBinding titleBinding = TitleListBinding.inflate(getLayoutInflater());
        titleBinding.setChildCategory(mActivityModel.getChildCategory().getChildCategory());
        ActionBar actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(titleBinding.listTitleContainer);
    }

    //Initialize
    private void init() {
        //View Model
        mModel = new ViewModelProvider(this).get(ListViewModel.class);
        mBinding.listRecyclerView.setHasFixedSize(true);
        mBinding.listRecyclerView.setAdapter(mModel.getAdapter());
        //Fab Animation
        rotateOpen = AnimationUtils.loadAnimation(requireContext(), R.anim.rotate_open);
        rotateClose = AnimationUtils.loadAnimation(requireContext(), R.anim.rotate_close);
        fromBottomAdd = AnimationUtils.loadAnimation(requireContext(), R.anim.add_from_bottom);
        toBottomAdd = AnimationUtils.loadAnimation(requireContext(), R.anim.add_to_bottom);
        fromBottomFolder = AnimationUtils.loadAnimation(requireContext(), R.anim.folder_from_bottom);
        toBottomAddFolder = AnimationUtils.loadAnimation(requireContext(), R.anim.folder_to_bottom);

    }

    //Fab Animation
    private void onFabClicked() {
        setVisibility(isFabOpened);
        setAnimation(isFabOpened);
        setClickable(isFabOpened);
        isFabOpened = !isFabOpened;
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

    //Show Add Folder Dialog
    private void showAddFolderDialog() {
        FrameLayout frameLayout = (FrameLayout) View.inflate(requireContext(), R.layout.item_dialog_edit_text, null);
        EditText editText = frameLayout.findViewById(R.id.editText_dialog);
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext(), R.style.myAlertDialog);
        builder.setView(frameLayout);
        builder.setTitle("Add Folder");
        builder.setNegativeButton("CANCEL", null);
        builder.setPositiveButton("DONE", (dialog, which) -> {
            mModel.insertFolder(new ListFolder(editText.getText().toString()));
        });
        builder.show();
    }

}
//TODO
//item_list_fragment 완성 해야됨
//폴더 추가