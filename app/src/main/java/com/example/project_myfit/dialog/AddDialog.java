package com.example.project_myfit.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.project_myfit.R;
import com.example.project_myfit.databinding.ItemDialogEditTextBinding;
import com.example.project_myfit.util.CommonUtil;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import static com.example.project_myfit.util.MyFitConstant.ADD_CONFIRM;
import static com.example.project_myfit.util.MyFitConstant.CATEGORY;

public class AddDialog extends ParentDialogFragment {

    private String mParentCategory, mDialogTitle, mEditTextHint, mEditTextPlaceHolder;
    private long mParentId;
    private NavController mNavController;
    private DialogViewModel mDialogViewModel;
    private View.OnClickListener mPositiveButtonClickListener;
    private ItemDialogEditTextBinding mBinding;
    private int mItemType;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mItemType = AddDialogArgs.fromBundle(getArguments()).getItemType();
        //TODO 이눔으로 인덱스 받기
        mParentCategory = AddDialogArgs.fromBundle(getArguments()).getParentCategory();
        mParentId = AddDialogArgs.fromBundle(getArguments()).getParentId();
        mNavController = NavHostFragment.findNavController(this);
        mDialogViewModel = new ViewModelProvider(mNavController.getViewModelStoreOwner(mNavController.getGraph().getId())).get(DialogViewModel.class);

        if (mItemType == CATEGORY) {
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
            CommonUtil.keyBoardHide(requireContext(), v);

            String categoryName = getEditTextInputText();

            if (isSameNameCategory(categoryName))
                navigateAddSameNameDialog(categoryName);
            else {
                mDialogViewModel.insertCategory(categoryName, mParentCategory);
                setBackStackStateHandle();
                mNavController.popBackStack();
            }
        };
    }


    @NotNull
    @Contract(pure = true)
    private View.OnClickListener getFolderPositiveClickListener() {
        return v -> {
            CommonUtil.keyBoardHide(requireContext(), v);

            String folderName = getEditTextInputText();

            if (isSameNameFolder(folderName)) {
                navigateAddSameNameDialog(folderName);
            } else {
                mDialogViewModel.insertFolder(folderName, mParentId, mParentCategory);
                setBackStackStateHandle();
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

        return new DialogBuilder.EditTextDialogBuilder(requireContext())
                .setTitle(mDialogTitle)
                .setView(mBinding.getRoot())
                .setBackgroundDrawable()
                .setAllTextSize()
                .setPositiveButtonEnabledByIsTextEmpty(getEditTextInputText())
                .setPositiveButtonEnabledByIsChangedTextEmpty(mBinding.et)
                .showKeyboard()
                .setPositiveButtonClickListener(mPositiveButtonClickListener)
                .setPositiveButtonCallOnClickWhenImeClick(mBinding.et)
                .create();
    }

    @NotNull
    private String getEditTextInputText() {
        return String.valueOf(mBinding.et.getText());
    }

    @Override
    protected void setBackStackEntryLiveValue() {
        mDialogViewModel.getBackStackEntryLive().setValue(mNavController.getBackStackEntry(R.id.addDialog));
    }

    @Override
    protected void setBackStackStateHandle() {
        mNavController.getBackStackEntry(R.id.addDialog).getSavedStateHandle().set(ADD_CONFIRM, null);
    }

    private boolean isSameNameCategory(String categoryName) {
        return mDialogViewModel.isSameNameCategory(categoryName, mParentCategory);
    }

    private boolean isSameNameFolder(String folderName) {
        return mDialogViewModel.isSameNameFolder(folderName, mParentId);
    }

    private void navigateAddSameNameDialog(String itemName) {
        CommonUtil.navigate(mNavController, R.id.addDialog,
                AddDialogDirections.toAddSameNameDialog(mItemType, mParentCategory, mParentId, itemName));
    }
}
