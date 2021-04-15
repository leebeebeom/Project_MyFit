package com.example.project_myfit.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.InsetDrawable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.project_myfit.R;
import com.example.project_myfit.data.model.Category;
import com.example.project_myfit.data.model.Folder;
import com.example.project_myfit.databinding.ItemDialogEditTextBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textview.MaterialTextView;

import org.jetbrains.annotations.NotNull;

import static com.example.project_myfit.util.MyFitConstant.ADD_CONFIRM_CLICK;
import static com.example.project_myfit.util.MyFitConstant.ALERT_TITLE;
import static com.example.project_myfit.util.MyFitConstant.CATEGORY;
import static com.example.project_myfit.util.MyFitConstant.FOLDER;
import static com.example.project_myfit.util.MyFitConstant.ID;
import static com.example.project_myfit.util.MyFitConstant.IMAGE_CLEAR_CONFIRM_CLICK;
import static com.example.project_myfit.util.MyFitConstant.ITEM_MOVE_CONFIRM_CLICK;
import static com.example.project_myfit.util.MyFitConstant.NAME_EDIT_CONFIRM_CLICK;
import static com.example.project_myfit.util.MyFitConstant.SELECTED_ITEM_DELETE_CONFIRM_CLICK;
import static com.example.project_myfit.util.MyFitConstant.SORT_CONFIRM_CLICK;

public class DialogUtils {
    private final Context mContext;
    private final LayoutInflater mInflater;
    private final NavController mNavController;
    private final DialogViewModel mDialogViewModel;
    private final DialogFragment mThisFragment;
    private NavBackStackEntry mNavBackStackEntry;

    public DialogUtils(Context context, LayoutInflater inflater, DialogFragment fragment) {
        this.mContext = context;
        this.mInflater = inflater;
        this.mThisFragment = fragment;
        this.mNavController = NavHostFragment.findNavController(fragment);
        this.mDialogViewModel = new ViewModelProvider(mNavController.getViewModelStoreOwner(R.id.main_nav_graph)).get(DialogViewModel.class);
    }

    public DialogUtils backStackLiveSetValue(int destinationId) {
        mNavBackStackEntry = mNavController.getBackStackEntry(destinationId);
        mDialogViewModel.backStackEntryLiveSetValue(mNavBackStackEntry);
        return this;
    }

    public DialogViewModel getDialogViewModel() {
        return mDialogViewModel;
    }

    @NotNull
    public ItemDialogEditTextBinding getBinding(@Nullable String oldName, @NotNull String itemType) {
        ItemDialogEditTextBinding binding = ItemDialogEditTextBinding.inflate(mInflater);
        binding.dialogEditText.requestFocus();
        if (itemType.equals(CATEGORY)) {
            binding.setHint(mContext.getString(R.string.category_name));
            binding.setPlaceHolder(mContext.getString(R.string.category_name_korean));
        } else if (itemType.equals(FOLDER)) {
            binding.setHint(mContext.getString(R.string.folder_name));
            binding.setPlaceHolder(mContext.getString(R.string.folder_name_korean));
        }
        if (oldName != null) binding.setSetText(oldName);
        return binding;
    }

    @NotNull
    public AlertDialog getConfirmDialog(String message) {
        AlertDialog dialog = new MaterialAlertDialogBuilder(mContext, R.style.myAlertDialog)
                .setTitle(R.string.confirm)
                .setMessage(message)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.confirm, null)
                .show();
        setLayout(dialog.getWindow());
        setTextSize(dialog);
        return dialog;
    }

    @NotNull
    public AlertDialog getEditTextDialog(@NotNull ItemDialogEditTextBinding binding, String title, String oldName) {
        AlertDialog dialog = new MaterialAlertDialogBuilder(mContext, R.style.myAlertDialog)
                .setTitle(title)
                .setView(binding.getRoot())
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.confirm, null)
                .create();

        Window window = dialog.getWindow();
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        setLayout(window);
        dialog.show();
        setTextSize(dialog);
        addTextChangeListener(binding, dialog, oldName);
        return dialog;
    }

    public void setLayout(@NotNull Window window) {
        int margin = (int) mContext.getResources().getDimension(R.dimen._20sdp);
        Drawable drawable = ContextCompat.getDrawable(mContext, R.drawable.tree_view_dialog_background);
        InsetDrawable inset = new InsetDrawable(drawable, margin);
        window.setBackgroundDrawable(inset);
    }

    public void setTextSize(@NotNull AlertDialog dialog) {
        TextView title = dialog.findViewById(mContext.getResources().getIdentifier(ALERT_TITLE, ID, mContext.getPackageName()));
        if (title != null)
            title.setTextSize(TypedValue.COMPLEX_UNIT_DIP, mContext.getResources().getDimension(R.dimen._5sdp));

        float textSize = mContext.getResources().getDimensionPixelSize(R.dimen._4sdp);

        Button positive = dialog.getButton(Dialog.BUTTON_POSITIVE);
        if (positive != null)
            positive.setTextSize(TypedValue.COMPLEX_UNIT_DIP, textSize);

        Button negative = dialog.getButton(Dialog.BUTTON_NEGATIVE);
        if (negative != null)
            negative.setTextSize(TypedValue.COMPLEX_UNIT_DIP, textSize);

        MaterialTextView message = dialog.findViewById(android.R.id.message);
        if (message != null) {
            int padding = (int) mContext.getResources().getDimension(R.dimen._8sdp);
            message.setPadding(message.getPaddingLeft(), padding, message.getPaddingRight(), message.getPaddingBottom());
            message.setTextSize(TypedValue.COMPLEX_UNIT_DIP, textSize);
        }
    }

    private void addTextChangeListener(@NotNull ItemDialogEditTextBinding binding, @NotNull AlertDialog dialog, String oldName) {
        Button positiveButton = dialog.getButton(Dialog.BUTTON_POSITIVE);
        String inputText = String.valueOf(binding.dialogEditText.getText()).trim();
        if (oldName == null)
            positiveButton.setEnabled(!TextUtils.isEmpty(inputText));
        else
            positiveButton.setEnabled(!TextUtils.isEmpty(inputText) && !oldName.equals(inputText));


        binding.dialogEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (oldName == null)
                    positiveButton.setEnabled(!TextUtils.isEmpty(String.valueOf(s).trim()));
                else
                    positiveButton.setEnabled(!TextUtils.isEmpty(String.valueOf(s).trim()) && !oldName.equals(String.valueOf(s).trim()));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.dialogEditText.setOnKeyListener((v, keyCode, event) -> {
            if (String.valueOf(binding.dialogEditText.getText()).length() == 30)
                binding.dialogEditTextLayout.setError(mContext.getString(R.string.max_length));
            else binding.dialogEditTextLayout.setErrorEnabled(false);
            return false;
        });
    }

    public void addCategoryConfirmClick(String categoryName, String parentCategory) {
        if (mDialogViewModel.isSameNameCategory(categoryName, parentCategory))
            mNavController.navigate(AddDialogDirections.actionAddDialogToSameNameAddDialog(CATEGORY, parentCategory, 0, categoryName));
        else {
            mDialogViewModel.categoryInsert(categoryName, parentCategory);
            mNavBackStackEntry.getSavedStateHandle().set(ADD_CONFIRM_CLICK, CATEGORY);
            mThisFragment.dismiss();
        }
    }

    public void addFolderConfirmClick(String folderName, long parentId, String parentCategory) {
        if (mDialogViewModel.isSameNameFolder(folderName, parentId))
            mNavController.navigate(AddDialogDirections.actionAddDialogToSameNameAddDialog(FOLDER, parentCategory, parentId, folderName));
        else {
            mDialogViewModel.folderInsert(folderName, parentId, parentCategory);
            mNavBackStackEntry.getSavedStateHandle().set(ADD_CONFIRM_CLICK, FOLDER);
            mThisFragment.dismiss();
        }
    }

    public void sameNameCategoryAddConfirmClick(String categoryName, String parentCategory) {
        mDialogViewModel.categoryInsert(categoryName, parentCategory);
        mNavBackStackEntry.getSavedStateHandle().set(ADD_CONFIRM_CLICK, CATEGORY);
        mNavController.popBackStack(R.id.addDialog, true);
    }

    public void sameNameFolderAddConfirmClick(String folderName, String parentCategory, long parentId) {
        mDialogViewModel.folderInsert(folderName, parentId, parentCategory);
        mNavBackStackEntry.getSavedStateHandle().set(ADD_CONFIRM_CLICK, FOLDER);
        mNavController.popBackStack(R.id.addDialog, true);
    }

    public void categoryNameEditConfirmClick(@NotNull Category category, String categoryName, boolean isParentName) {
        if (mDialogViewModel.isSameNameCategory(categoryName, category.getParentCategory())) {
            mNavController.navigate(NameEditDialogDirections.actionNameEditDialogToSameNameEditDialog(CATEGORY, category.getId(), categoryName, isParentName));
        } else {
            mDialogViewModel.categoryNameEditConfirmClick(category, categoryName, isParentName);
            mNavBackStackEntry.getSavedStateHandle().set(NAME_EDIT_CONFIRM_CLICK, isParentName);
            mThisFragment.dismiss();
        }
    }

    public void folderNameEditConfirmCLick(@NotNull Folder folder, String folderName, boolean isParentName) {
        if (mDialogViewModel.isSameNameFolder(folderName, folder.getParentId())) {
            mNavController.navigate(NameEditDialogDirections.actionNameEditDialogToSameNameEditDialog(FOLDER, folder.getId(), folderName, isParentName));
        } else {
            mDialogViewModel.folderNameEditConfirmClick(folder, folderName, isParentName);
            mNavBackStackEntry.getSavedStateHandle().set(NAME_EDIT_CONFIRM_CLICK, isParentName);
            mThisFragment.dismiss();
        }
    }

    public void sameNameCategoryEditConfirmClick(long categoryId, String categoryName, boolean isParentName) {
        mDialogViewModel.sameNameCategoryEditConfirmClick(categoryId, categoryName, isParentName);
        mNavBackStackEntry.getSavedStateHandle().set(NAME_EDIT_CONFIRM_CLICK, isParentName);
        mNavController.popBackStack(R.id.NameEditDialog, true);
    }

    public void sameNameFolderEditConfirmClick(long folderId, String folderName, boolean isParentName) {
        mDialogViewModel.sameNameFolderEditConfirmClick(folderId, folderName, isParentName);
        mNavBackStackEntry.getSavedStateHandle().set(NAME_EDIT_CONFIRM_CLICK, isParentName);
        mNavController.popBackStack(R.id.NameEditDialog, true);
    }

    public void sortConfirmClick(int sort) {
        mNavBackStackEntry.getSavedStateHandle().set(SORT_CONFIRM_CLICK, sort);
    }

    public void selectedItemDeleteConfirmClick() {
        mNavBackStackEntry.getSavedStateHandle().set(SELECTED_ITEM_DELETE_CONFIRM_CLICK, null);
        mThisFragment.dismiss();
    }

    public void deletedConfirmClick(long sizeId) {
        mDialogViewModel.deleteConfirmClick(sizeId);
        mNavController.popBackStack(R.id.inputOutputFragment, true);
    }

    public void goBackConfirmClick() {
        mNavController.popBackStack(R.id.inputOutputFragment, true);
    }

    public void imageDeleteConfirm() {
        mNavBackStackEntry.getSavedStateHandle().set(IMAGE_CLEAR_CONFIRM_CLICK, null);
        mThisFragment.dismiss();
    }

    public void treeViewAddCategoryClick(String itemType, String parentCategory) {
        mNavController.navigate(TreeViewDialogDirections.actionTreeViewDialogToAddDialog(itemType, parentCategory, 0));
    }

    public void treeViewAddFolderClick(String itemType, String parentCategory, long parentId) {
        mNavController.navigate(TreeViewDialogDirections.actionTreeViewDialogToAddDialog(itemType, parentCategory, parentId));
    }

    public void itemMoveConfirmClick(long parentId) {
        mNavBackStackEntry.getSavedStateHandle().set(ITEM_MOVE_CONFIRM_CLICK, parentId);
        mNavController.popBackStack(R.id.treeViewDialog, true);
    }

    public void treeViewNodeClick(int selectedItemSize, long parentId) {
        mNavController.navigate(TreeViewDialogDirections.actionTreeViewDialogToItemMoveDialog(selectedItemSize, parentId));
    }
}
