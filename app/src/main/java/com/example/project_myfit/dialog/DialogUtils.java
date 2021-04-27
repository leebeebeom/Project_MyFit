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
import com.example.project_myfit.dialog.searchdialog.SearchAddDialogDirections;
import com.example.project_myfit.dialog.searchdialog.SearchNameEditDialogDirections;
import com.example.project_myfit.dialog.searchdialog.SearchTreeViewDialogDirections;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textview.MaterialTextView;

import org.jetbrains.annotations.NotNull;

import static com.example.project_myfit.util.MyFitConstant.ADD_CONFIRM_CLICK;
import static com.example.project_myfit.util.MyFitConstant.ALERT_TITLE;
import static com.example.project_myfit.util.MyFitConstant.CATEGORY;
import static com.example.project_myfit.util.MyFitConstant.FOLDER;
import static com.example.project_myfit.util.MyFitConstant.GO_BACK_CONFIRM_CLICK;
import static com.example.project_myfit.util.MyFitConstant.ID;
import static com.example.project_myfit.util.MyFitConstant.IMAGE_CLEAR_CONFIRM_CLICK;
import static com.example.project_myfit.util.MyFitConstant.ITEM_MOVE_CONFIRM_CLICK;
import static com.example.project_myfit.util.MyFitConstant.NAME_EDIT_CONFIRM_CLICK;
import static com.example.project_myfit.util.MyFitConstant.SELECTED_ITEM_DELETE_CONFIRM_CLICK;
import static com.example.project_myfit.util.MyFitConstant.SIZE_DELETE_CONFIRM_CLICK;
import static com.example.project_myfit.util.MyFitConstant.SORT_CONFIRM_CLICK;

public class DialogUtils {
    private final Context mContext;
    private final LayoutInflater mInflater;
    private final NavController mNavController;
    private final DialogViewModel mDialogViewModel;
    private NavBackStackEntry mNavBackStackEntry;

    public DialogUtils(Context context, LayoutInflater inflater, DialogFragment fragment, int navGraphId) {
        this.mContext = context;
        this.mInflater = inflater;
        this.mNavController = NavHostFragment.findNavController(fragment);
        this.mDialogViewModel = new ViewModelProvider(mNavController.getViewModelStoreOwner(navGraphId)).get(DialogViewModel.class);
    }

    public DialogUtils backStackLiveSetValue(int destinationId) {
        mNavBackStackEntry = mNavController.getBackStackEntry(destinationId);
        mDialogViewModel.getBackStackEntryLive().setValue(mNavBackStackEntry);
        return this;
    }

    public DialogViewModel getDialogViewModel() {
        return mDialogViewModel;
    }

    @NotNull
    public ItemDialogEditTextBinding getBinding(@Nullable String oldName, @NotNull String itemType) {
        ItemDialogEditTextBinding binding = ItemDialogEditTextBinding.inflate(mInflater);
        binding.etDialog.requestFocus();
        if (itemType.equals(CATEGORY)) {
            binding.setHint(mContext.getString(R.string.dialog_hint_category_name));
            binding.setPlaceHolder(mContext.getString(R.string.dialog_place_holder_category_name));
        } else if (itemType.equals(FOLDER)) {
            binding.setHint(mContext.getString(R.string.dialog_hint_folder_name));
            binding.setPlaceHolder(mContext.getString(R.string.dialog_place_holder_folder_name));
        }
        if (oldName != null) binding.setSetText(oldName);
        return binding;
    }

    @NotNull
    public AlertDialog getConfirmDialog(String message) {
        AlertDialog dialog = new MaterialAlertDialogBuilder(mContext, R.style.myAlertDialogStyle)
                .setTitle(R.string.dialog_confirm)
                .setMessage(message)
                .setNegativeButton(R.string.dialog_cancel, null)
                .setPositiveButton(R.string.dialog_confirm, null)
                .show();
        setLayout(dialog.getWindow());
        setTextSize(dialog);
        return dialog;
    }

    @NotNull
    public AlertDialog getEditTextDialog(@NotNull ItemDialogEditTextBinding binding, String title, String oldName) {
        AlertDialog dialog = new MaterialAlertDialogBuilder(mContext, R.style.myAlertDialogStyle)
                .setTitle(title)
                .setView(binding.getRoot())
                .setNegativeButton(R.string.dialog_cancel, null)
                .setPositiveButton(R.string.dialog_confirm, null)
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
        String inputText = String.valueOf(binding.etDialog.getText()).trim();
        if (oldName == null)
            positiveButton.setEnabled(!TextUtils.isEmpty(inputText));
        else
            positiveButton.setEnabled(!TextUtils.isEmpty(inputText) && !oldName.equals(inputText));


        binding.etDialog.addTextChangedListener(new TextWatcher() {
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

        binding.etDialog.setOnKeyListener((v, keyCode, event) -> {
            if (String.valueOf(binding.etDialog.getText()).length() == 30)
                binding.etDialogLayout.setError(mContext.getString(R.string.dialog_et_max_length));
            else binding.etDialogLayout.setErrorEnabled(false);
            return false;
        });
    }

    public void addCategory(String categoryName, String parentCategory, boolean isSearchView) {
        if (mDialogViewModel.isSameNameCategory(categoryName, parentCategory))
            if (!isSearchView)
                mNavController.navigate(AddDialogDirections.actionAddDialogToSameNameAddDialog(CATEGORY, parentCategory, 0, categoryName));
            else
                mNavController.navigate(SearchAddDialogDirections.actionSearchAddDialogToSearchSameNameAddDialog(CATEGORY, parentCategory, 0, categoryName));
        else {
            mDialogViewModel.categoryInsert(categoryName, parentCategory);
            mNavBackStackEntry.getSavedStateHandle().set(ADD_CONFIRM_CLICK, CATEGORY);
            mNavController.popBackStack(R.id.addDialog, true);
        }
    }

    public void addFolder(String folderName, long parentId, String parentCategory, boolean isSearchView) {
        if (mDialogViewModel.isSameNameFolder(folderName, parentId))
            if (!isSearchView) mNavController.navigate(
                    AddDialogDirections.actionAddDialogToSameNameAddDialog(FOLDER, parentCategory, parentId, folderName));
            else mNavController.navigate(
                    SearchAddDialogDirections.actionSearchAddDialogToSearchSameNameAddDialog(FOLDER, parentCategory, parentId, folderName));
        else {
            mDialogViewModel.folderInsert(folderName, parentId, parentCategory);
            mNavBackStackEntry.getSavedStateHandle().set(ADD_CONFIRM_CLICK, FOLDER);
            mNavController.popBackStack(R.id.addDialog, true);
        }
    }

    public void sameNameCategoryAdd(String categoryName, String parentCategory, boolean isSearchView) {
        mDialogViewModel.categoryInsert(categoryName, parentCategory);
        mNavBackStackEntry.getSavedStateHandle().set(ADD_CONFIRM_CLICK, CATEGORY);
        if (!isSearchView) mNavController.popBackStack(R.id.addDialog, true);
        else mNavController.popBackStack(R.id.searchAddDialog, true);
    }

    public void sameNameFolderAdd(String folderName, String parentCategory, long parentId, boolean isSearchView) {
        mDialogViewModel.folderInsert(folderName, parentId, parentCategory);
        mNavBackStackEntry.getSavedStateHandle().set(ADD_CONFIRM_CLICK, FOLDER);
        if (!isSearchView) mNavController.popBackStack(R.id.addDialog, true);
        else mNavController.popBackStack(R.id.searchAddDialog, true);
    }

    public void categoryNameEdit(@NotNull Category category, String categoryName, boolean isParentName, boolean isSearchView) {
        if (mDialogViewModel.isSameNameCategory(categoryName, category.getParentCategory())) {
            if (!isParentName)
                mNavController.navigate(NameEditDialogDirections.actionNameEditDialogToSameNameEditDialog(CATEGORY, category.getId(), categoryName, isParentName));
            else
                mNavController.navigate(SearchNameEditDialogDirections.actionSearchNameEditDialogToSearchSameNameEditDialog(CATEGORY, category.getId(), categoryName, isParentName));
        } else {
            mDialogViewModel.categoryNameEdit(category, categoryName, isParentName);
            mNavBackStackEntry.getSavedStateHandle().set(NAME_EDIT_CONFIRM_CLICK, isParentName);
            mNavController.popBackStack(R.id.nameEditDialog, true);
        }
    }

    public void folderNameEdit(@NotNull Folder folder, String folderName, boolean isParentName, boolean isSearchView) {
        if (mDialogViewModel.isSameNameFolder(folderName, folder.getParentId())) {
            if (!isSearchView)
                mNavController.navigate(NameEditDialogDirections.actionNameEditDialogToSameNameEditDialog(FOLDER, folder.getId(), folderName, isParentName));
            else
                mNavController.navigate(SearchNameEditDialogDirections.actionSearchNameEditDialogToSearchSameNameEditDialog(FOLDER, folder.getId(), folderName, isParentName));
        } else {
            mDialogViewModel.folderNameEdit(folder, folderName, isParentName);
            mNavBackStackEntry.getSavedStateHandle().set(NAME_EDIT_CONFIRM_CLICK, isParentName);
            mNavController.popBackStack(R.id.nameEditDialog, true);
        }
    }

    public void sameNameCategoryEdit(long categoryId, String categoryName, boolean isParentName, boolean isSearchView) {
        mDialogViewModel.sameNameCategoryEdit(categoryId, categoryName, isParentName);
        mNavBackStackEntry.getSavedStateHandle().set(NAME_EDIT_CONFIRM_CLICK, isParentName);
        if (!isParentName) mNavController.popBackStack(R.id.nameEditDialog, true);
        else mNavController.popBackStack(R.id.searchNameEditDialog, true);
    }

    public void sameNameFolderEditC(long folderId, String folderName, boolean isParentName, boolean isSearchView) {
        mDialogViewModel.sameNameFolderEdit(folderId, folderName, isParentName);
        mNavBackStackEntry.getSavedStateHandle().set(NAME_EDIT_CONFIRM_CLICK, isParentName);
        if (!isSearchView) mNavController.popBackStack(R.id.nameEditDialog, true);
        else mNavController.popBackStack(R.id.searchNameEditDialog, true);
    }

    public void sortConfirmClick(int sort) {
        mNavBackStackEntry.getSavedStateHandle().set(SORT_CONFIRM_CLICK, sort);
    }

    public void selectedItemDeleteConfirmClick() {
        mNavBackStackEntry.getSavedStateHandle().set(SELECTED_ITEM_DELETE_CONFIRM_CLICK, null);
        mNavController.popBackStack(R.id.selectedItemDeleteDialog, true);
    }

    public void deletedConfirmClick(long sizeId) {
        mDialogViewModel.sizeDelete(sizeId);
        mNavBackStackEntry.getSavedStateHandle().set(SIZE_DELETE_CONFIRM_CLICK, null);
    }

    public void goBackConfirmClick() {
        mNavBackStackEntry.getSavedStateHandle().set(GO_BACK_CONFIRM_CLICK, null);
    }

    public void imageClearConfirm() {
        mNavBackStackEntry.getSavedStateHandle().set(IMAGE_CLEAR_CONFIRM_CLICK, null);
        mNavController.popBackStack(R.id.imageClearDialog, true);
    }

    public void treeViewAddCategory(String itemType, String parentCategory, boolean isSearchView) {
        if (!isSearchView)
            mNavController.navigate(TreeViewDialogDirections.actionTreeViewDialogToAddDialog(itemType, parentCategory, 0));
        else
            mNavController.navigate(SearchTreeViewDialogDirections.actionSearchTreeViewDialogToSearchAddDialog(itemType, parentCategory, 0));
    }

    public void treeViewAddFolder(String itemType, String parentCategory, long parentId, boolean isSearchView) {
        if (!isSearchView)
            mNavController.navigate(TreeViewDialogDirections.actionTreeViewDialogToAddDialog(itemType, parentCategory, parentId));
        else
            mNavController.navigate(SearchTreeViewDialogDirections.actionSearchTreeViewDialogToSearchAddDialog(itemType, parentCategory, parentId));
    }

    public void treeViewNodeClick(int selectedItemSize, long parentId, boolean isSearchView) {
        if (!isSearchView)
            mNavController.navigate(TreeViewDialogDirections.actionTreeViewDialogToItemMoveDialog(selectedItemSize, parentId));
        else
            mNavController.navigate(SearchTreeViewDialogDirections.actionSearchTreeViewDialogToSearchItemMoveDialog(selectedItemSize, parentId));
    }

    public void itemMove(long parentId, boolean isSearchView) {
        mNavBackStackEntry.getSavedStateHandle().set(ITEM_MOVE_CONFIRM_CLICK, parentId);
        if (!isSearchView) mNavController.popBackStack(R.id.treeViewDialog, true);
        else mNavController.popBackStack(R.id.searchTreeViewDialog, true);
    }

    public void recentSearchDeleteAll() {
        mDialogViewModel.recentSearchDeleteAll();
        mNavController.popBackStack(R.id.recentSearchDeleteAllDialog, true);
    }
}
