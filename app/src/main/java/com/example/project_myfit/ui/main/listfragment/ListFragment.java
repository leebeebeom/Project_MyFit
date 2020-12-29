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
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_myfit.MainActivityViewModel;
import com.example.project_myfit.R;
import com.example.project_myfit.databinding.FragmentListBinding;
import com.example.project_myfit.databinding.ItemDialogEditTextBinding;
import com.example.project_myfit.ui.main.adapter.DragCallBack;
import com.example.project_myfit.ui.main.listfragment.adapter.ListFolderAdapter;
import com.example.project_myfit.ui.main.listfragment.adapter.ListSizeAdapter;
import com.example.project_myfit.ui.main.listfragment.database.ListFolder;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;


public class ListFragment extends Fragment implements DragCallBack.StartDragListener {

    private ListViewModel mModel;
    private FragmentListBinding mBinding;
    private MainActivityViewModel mActivityModel;
    private Animation rotateOpen, rotateClose, fromBottomAdd, toBottomAdd, fromBottomFolder, toBottomAddFolder;
    private boolean isFabOpened;
    private ListFolderAdapter mFolderAdapter;
    private ListSizeAdapter mSizeAdapter;
    private ItemDialogEditTextBinding mEditTextBinding;
    private ItemTouchHelper mTouchHelperFolder;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        //Binding
        mBinding = FragmentListBinding.inflate(getLayoutInflater());
        //Activity View Model
        mActivityModel = new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);
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
        mModel.getFolderList(mActivityModel.getCategory().getId()).observe(getViewLifecycleOwner(), listFolders -> {
            mModel.setFolderLargestOrder(mActivityModel.getCategory().getId());
            if (mModel.isFolderRefreshOn()) {
                mFolderAdapter.setItem(listFolders, this, requireContext(), mModel);
                mBinding.recyclerViewFolder.setAdapter(mFolderAdapter);
                mModel.setFolderRefreshOn(false);
            } else {
                mFolderAdapter.updateDiffUtils(listFolders);
            }
        });
        mModel.getSizeList(mActivityModel.getCategory().getId()).observe(getViewLifecycleOwner(), sizeList -> {
            mSizeAdapter.setItem(sizeList);
            mBinding.recyclerViewContent.setAdapter(mSizeAdapter);
        });
//        mTouchHelperFolder = new ItemTouchHelper(new DragCallBack(mFolderAdapter));
        mTouchHelperFolder.attachToRecyclerView(mBinding.recyclerViewFolder);
        mFolderAdapter.setOnFolderClickListener(new ListFolderAdapter.OnFolderClickListener() {
            @Override
            public void onItemClicked() {
            }

            @Override
            public void onEditClicked() {
            }

            @Override
            public void onDeleteClicked() {
            }
        });
    }

    //Initialize
    private void init() {
        //View Model
        mModel = new ViewModelProvider(this).get(ListViewModel.class);
        mModel.setFolderRefreshOn(true);
        isFabOpened = false;
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
                    int largestOrder = mModel.getLargestOrder();
                    largestOrder++;
                    mModel.insertFolder(new ListFolder(mEditTextBinding.editTextDialog.getText().toString(),
                            mActivityModel.getCategory().getId(),
                            largestOrder,
                            0));
                })
                .setOnDismissListener(dialog -> {
                    ((ViewGroup) mEditTextBinding.getRoot().getParent()).removeAllViews();
                });
        builder.show();
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