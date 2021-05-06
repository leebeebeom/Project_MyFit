package com.example.project_myfit.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.InsetDrawable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.TextView;

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
import com.example.project_myfit.util.CommonUtil;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textview.MaterialTextView;

import org.jetbrains.annotations.NotNull;

import static com.example.project_myfit.util.MyFitConstant.ADD_CONFIRM;
import static com.example.project_myfit.util.MyFitConstant.ALERT_TITLE;
import static com.example.project_myfit.util.MyFitConstant.CATEGORY;
import static com.example.project_myfit.util.MyFitConstant.FOLDER;
import static com.example.project_myfit.util.MyFitConstant.ID;
import static com.example.project_myfit.util.MyFitConstant.EDIT_NAME_CONFIRM;

public class DialogUtil {
    private final Context mContext;
    private final NavController mNavController;
    private final DialogViewModel mDialogViewModel;
    private NavBackStackEntry mNavBackStackEntry;

    public DialogUtil(Context context, DialogFragment fragment, int navGraphId) {
        this.mContext = context;
        this.mNavController = NavHostFragment.findNavController(fragment);
        this.mDialogViewModel = new ViewModelProvider(mNavController.getViewModelStoreOwner(navGraphId)).get(DialogViewModel.class);
    }

    public DialogUtil setValueBackStackLive(int destinationId) {
        mNavBackStackEntry = mNavController.getBackStackEntry(destinationId);
        mDialogViewModel.getBackStackEntryLive().setValue(mNavBackStackEntry);
        return this;
    }

    public DialogViewModel getDialogViewModel() {
        return mDialogViewModel;
    }

    public NavController getNavController() {
        return mNavController;
    }

    public ItemDialogEditTextBinding getCategoryBinding() {
        com.example.project_myfit.databinding.ItemDialogEditTextBinding binding = getBinding();
        binding.setHint(mContext.getString(R.string.dialog_hint_category_name));
        binding.setPlaceHolder(mContext.getString(R.string.dialog_place_holder_category_name));
        return binding;
    }

    public ItemDialogEditTextBinding getFolderBinding() {
        ItemDialogEditTextBinding binding = getBinding();
        binding.setHint(mContext.getString(R.string.dialog_hint_folder_name));
        binding.setPlaceHolder(mContext.getString(R.string.dialog_place_holder_folder_name));
        return binding;
    }

    @NotNull
    private ItemDialogEditTextBinding getBinding() {
        ItemDialogEditTextBinding binding = ItemDialogEditTextBinding.inflate(LayoutInflater.from(mContext));
        binding.et.requestFocus();
        return binding;
    }

    public AlertDialog getAddDialog(ItemDialogEditTextBinding binding, String title) {
        AlertDialog dialog = getEditTextDialog(binding, title);

        Button positiveButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);

        String inputText = String.valueOf(binding.et.getText()).trim();
        positiveButton.setEnabled(!TextUtils.isEmpty(inputText));

        addTextChangeListener(positiveButton, binding);
        return dialog;
    }

    private void addTextChangeListener(Button positiveButton, @NotNull ItemDialogEditTextBinding binding) {
        binding.et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                positiveButton.setEnabled(!TextUtils.isEmpty(s));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public AlertDialog getEditDialog(ItemDialogEditTextBinding binding, String title, String oldName) {
        AlertDialog dialog = getEditTextDialog(binding, title);

        Button positiveButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);

        String inputText = String.valueOf(binding.et.getText()).trim();
        positiveButton.setEnabled(!TextUtils.isEmpty(inputText) && !oldName.equals(inputText));

        addTextChangeListener(positiveButton, binding, oldName);
        return dialog;
    }

    private void addTextChangeListener(Button positiveButton, @NotNull ItemDialogEditTextBinding binding, String oldName) {
        binding.et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                positiveButton.setEnabled(!TextUtils.isEmpty(s) && !oldName.equals(String.valueOf(s).trim()));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @NotNull
    private AlertDialog getEditTextDialog(@NotNull ItemDialogEditTextBinding binding, String title) {
        AlertDialog dialog = new MaterialAlertDialogBuilder(mContext, R.style.myAlertDialogStyle)
                .setTitle(title)
                .setView(binding.getRoot())
                .setNegativeButton(R.string.dialog_cancel, null)
                .setPositiveButton(R.string.dialog_confirm, null)
                .create();

        CommonUtil.keyBoardShow(mContext, binding.et);
        setBackground(dialog.getWindow());
        dialog.show();
        setTextSize(dialog);
        setOnKeyListener(binding);
        return dialog;
    }

    private void setOnKeyListener(@NotNull ItemDialogEditTextBinding binding) {
        binding.et.setOnKeyListener((v, keyCode, event) -> {
            if (String.valueOf(binding.et.getText()).length() == 30)
                binding.layout.setError(mContext.getString(R.string.dialog_et_max_length));
            else binding.layout.setErrorEnabled(false);
            return false;
        });
    }

    @NotNull
    public AlertDialog getConfirmDialog(String message) {
        AlertDialog dialog = new MaterialAlertDialogBuilder(mContext, R.style.myAlertDialogStyle)
                .setTitle(R.string.dialog_confirm)
                .setMessage(message)
                .setNegativeButton(R.string.dialog_cancel, null)
                .setPositiveButton(R.string.dialog_confirm, null)
                .show();

        setBackground(dialog.getWindow());
        setTextSize(dialog);
        return dialog;
    }

    public void setBackground(@NotNull Window window) {
        int margin = (int) mContext.getResources().getDimension(R.dimen._20sdp);
        Drawable dialogBackground = ContextCompat.getDrawable(mContext, R.drawable.tree_view_dialog_background);
        InsetDrawable inset = new InsetDrawable(dialogBackground, margin);
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

    public void addCategory(String categoryName, String parentCategory) {
        mDialogViewModel.insertCategory(categoryName, parentCategory);
        mNavBackStackEntry.getSavedStateHandle().set(ADD_CONFIRM, CATEGORY);
        mNavController.popBackStack(R.id.addDialog, true);
    }

    public void addFolder(String folderName, long parentId, String parentCategory) {
        mDialogViewModel.insertFolder(folderName, parentId, parentCategory);
        mNavBackStackEntry.getSavedStateHandle().set(ADD_CONFIRM, FOLDER);
        mNavController.popBackStack(R.id.addDialog, true);
    }

    public void editCategoryName(@NotNull Category category, String newCategoryName, boolean isParentName) {
        mDialogViewModel.editCategoryName(category, newCategoryName, isParentName);
        mNavBackStackEntry.getSavedStateHandle().set(EDIT_NAME_CONFIRM, isParentName);
        mNavController.popBackStack(R.id.editNameDialog, true);
    }

    public void editFolderName(@NotNull Folder folder, String newFolderName, boolean isParentName) {
        mDialogViewModel.editFolderName(folder, newFolderName, isParentName);
        mNavBackStackEntry.getSavedStateHandle().set(EDIT_NAME_CONFIRM, isParentName);
        mNavController.popBackStack(R.id.editNameDialog, true);
    }

    public void setOnImeClickListener(@NotNull ItemDialogEditTextBinding binding, Button positiveButton) {
        binding.et.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE && positiveButton.isEnabled())
                positiveButton.callOnClick();
            return false;
        });
    }

    public NavBackStackEntry getBackStackEntry() {
        return mNavBackStackEntry;
    }
}
