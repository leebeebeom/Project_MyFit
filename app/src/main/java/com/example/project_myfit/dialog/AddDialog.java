package com.example.project_myfit.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.NavController;

import com.example.project_myfit.R;
import com.example.project_myfit.databinding.ItemDialogEditTextBinding;
import com.example.project_myfit.util.CommonUtil;
import com.example.project_myfit.util.Constant;
import com.example.project_myfit.util.KeyBoardUtil;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class AddDialog extends ParentDialogFragment {

    private String mDialogTitle, mEditTextHint, mEditTextPlaceHolder;
    private long mParentId;
    private NavController mNavController;
    private DialogViewModel mDialogViewModel;
    private View.OnClickListener mPositiveButtonClickListener;
    private ItemDialogEditTextBinding mBinding;
    private int mItemTypeIndex, mParentCategoryIndex;
    private KeyBoardUtil mKeyBoardUtil;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mItemTypeIndex = AddDialogArgs.fromBundle(getArguments()).getItemTypeIndex();
        mParentCategoryIndex = AddDialogArgs.fromBundle(getArguments()).getParentCategoryIndex();
        mParentId = AddDialogArgs.fromBundle(getArguments()).getParentId();
        mNavController = getNavController(this);
        mDialogViewModel = getDialogViewModel(mNavController);

        if (isItemTypeCategory()) {
            mEditTextHint = getString(R.string.dialog_hint_category_name);
            mEditTextPlaceHolder = getString(R.string.dialog_place_holder_category_name);
            mDialogTitle = getString(R.string.all_add_category);
            mPositiveButtonClickListener = getCategoryPositiveClickListener();
        } else {
            mEditTextHint = getString(R.string.dialog_hint_folder_name);
            mEditTextPlaceHolder = getString(R.string.dialog_place_holder_folder_name);
            mDialogTitle = getString(R.string.all_create_folder);
            mPositiveButtonClickListener = getFolderPositiveClickListener();
        }
    }

    @NotNull
    @Contract(pure = true)
    private View.OnClickListener getCategoryPositiveClickListener() {
        return v -> {
            hideKeyBoard(v);

            String categoryName = getEditTextInputText();

            if (doesSameNameCategoryExist(categoryName))
                navigateAddSameNameDialog(categoryName);
            else {
                mDialogViewModel.insertCategory(categoryName, mParentCategoryIndex);
                setBackStackStateHandleValue();
                mNavController.popBackStack();
            }
        };
    }

    @NotNull
    @Contract(pure = true)
    private View.OnClickListener getFolderPositiveClickListener() {
        return v -> {
            hideKeyBoard(v);

            String folderName = getEditTextInputText();

            if (doesSameNameFolderExist(folderName)) {
                navigateAddSameNameDialog(folderName);
            } else {
                mDialogViewModel.insertFolder(folderName, mParentId, mParentCategoryIndex);
                setBackStackStateHandleValue();
                mNavController.popBackStack();
            }
        };
    }

    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        mBinding = new DialogBindingBuilder(getLayoutInflater())
                .setRequestFocus()
                .setHint(mEditTextHint)
                .setPlaceHolder(mEditTextPlaceHolder)
                .showErrorIfMoreThan30Characters(requireContext())
                .create();

        return new DialogBuilder(requireContext())
                .setTitle(mDialogTitle)
                .setView(mBinding.getRoot())
                .setBackgroundDrawable()
                .setTitleTextSize()
                .setButtonTextSize()
                .setPositiveButtonEnabledByIsTextEmpty(getEditTextInputText())
                .setPositiveButtonEnabledByIsChangedTextEmpty(mBinding.et)
                .showKeyboard()
                .setPositiveButtonClickListener(mPositiveButtonClickListener)
                .setPositiveButtonCallOnClickWhenImeClick(mBinding.et)
                .create();
    }

    @Override
    protected void setBackStackEntryLiveValue() {
        mDialogViewModel.getBackStackEntryLive().setValue(getBackStackEntry());
    }

    @Override
    protected void setBackStackStateHandleValue() {
        getBackStackEntry().getSavedStateHandle().set(Constant.DialogBackStackStateHandleKey.ADD_CONFIRM.name(), null);
    }

    @Override
    protected NavBackStackEntry getBackStackEntry() {
        return mNavController.getBackStackEntry(R.id.addDialog);
    }

    @Override
    protected View.OnClickListener getPositiveButtonClickListener() {
        return null;
    }

    @NotNull
    private String getEditTextInputText() {
        return String.valueOf(mBinding.et.getText());
    }

    private boolean doesSameNameCategoryExist(String categoryName) {
        return mDialogViewModel.doesSameNameCategoryExist(categoryName, mParentCategoryIndex);
    }

    private boolean doesSameNameFolderExist(String folderName) {
        return mDialogViewModel.doesSameNameFolderExist(folderName, mParentId);
    }

    private void navigateAddSameNameDialog(String itemName) {
        CommonUtil.navigate(mNavController, R.id.addDialog,
                AddDialogDirections.toAddSameNameDialog(mItemTypeIndex, mParentCategoryIndex, mParentId, itemName));
    }

    private boolean isItemTypeCategory() {
        return mItemTypeIndex == Constant.ItemType.CATEGORY.ordinal();
    }

    private void hideKeyBoard(View v) {
        if (mKeyBoardUtil == null) mKeyBoardUtil = new KeyBoardUtil(requireContext());
        mKeyBoardUtil.hideKeyBoard(v);
    }
}
