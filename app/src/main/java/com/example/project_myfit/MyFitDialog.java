package com.example.project_myfit;

import android.content.Context;
import android.text.InputFilter;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.project_myfit.databinding.ItemDialogEditTextBinding;
import com.example.project_myfit.ui.main.database.Category;
import com.example.project_myfit.ui.main.listfragment.database.Folder;
import com.example.project_myfit.ui.main.listfragment.treeview.TreeHolderCategory;
import com.example.project_myfit.ui.main.listfragment.treeview.TreeHolderFolder;
import com.example.project_myfit.ui.main.listfragment.treeview.TreeViewAddClick;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.view.AndroidTreeView;

import java.util.List;

public class MyFitDialog {


    public static MaterialAlertDialogBuilder getGoBackConfirmDialog(Context context) {
        return new MaterialAlertDialogBuilder(context, R.style.myAlertDialog)
                .setTitle("확인")
                .setMessage("저장되지 않았습니다.\n종료하시겠습니까?")
                .setNegativeButton("취소", null);
    }

    public static MaterialAlertDialogBuilder getDeleteConfirmDialog(Context context) {
        return new MaterialAlertDialogBuilder(context, R.style.myAlertDialog)
                .setTitle("확인")
                .setMessage("휴지통으로 이동하시겠습니까?")
                .setNegativeButton("취소", null);
    }

    public static MaterialAlertDialogBuilder getImageClearDialog(Context context) {
        return new MaterialAlertDialogBuilder(context, R.style.myAlertDialog)
                .setTitle("확인")
                .setMessage("이미지를 삭제하시겠습니까?")
                .setNegativeButton("취소", null);
    }

    public static MaterialAlertDialogBuilder getAddFolderDialog(Context context) {
        return new MaterialAlertDialogBuilder(context, R.style.myAlertDialog)
                .setTitle("폴더 생성")
                .setNegativeButton("취소", null)
                .setOnDismissListener(dialog -> closeKeyboard(context));
    }

    public static MaterialAlertDialogBuilder getEditFolderNameDialog(Context context) {
        return new MaterialAlertDialogBuilder(context, R.style.myAlertDialog)
                .setTitle("폴더 이름 변경")
                .setNegativeButton("취소", null)
                .setOnDismissListener(dialog -> closeKeyboard(context));
    }

    public static MaterialAlertDialogBuilder getSelectedItemDeleteDialog(Context context, int selectedAmount) {
        return new MaterialAlertDialogBuilder(context, R.style.myAlertDialog)
                .setTitle("확인")
                .setMessage(selectedAmount + "개의 아이템을 휴지통으로 이동하시겠습니까?")
                .setNegativeButton("취소", null);
    }

    public static AlertDialog getTreeViewDialog(Context context, String parentCategory, View treeView) {
        AlertDialog dialog = new MaterialAlertDialogBuilder(context, R.style.myAlertDialog)
                .setTitle(parentCategory)
                .setView(treeView)
                .create();
        TextView textView = dialog.findViewById(android.R.id.message);
        if (textView != null)
            textView.setTextSize(context.getResources().getDimension(R.dimen._6sdp));
        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawableResource(R.drawable.tree_view_dialog_background);
        return dialog;
    }

    public static MaterialAlertDialogBuilder getItemMoveDialog(Context context, int selectedAmount) {
        return new MaterialAlertDialogBuilder(context, R.style.myAlertDialog)
                .setTitle("확인")
                .setMessage(selectedAmount + "개의 아이템을 이동하시겠습니까?")
                .setNegativeButton("취소", null);
    }

    public static AndroidTreeView getTreeView(Context context, List<Category> categoryList, List<Folder> allFolderList, TreeViewAddClick listener, List<Folder> selectedFolder) {
        TreeNode root = TreeNode.root();
        for (Category category : categoryList) {
            TreeNode categoryTreeNode = new TreeNode(new TreeHolderCategory.IconTreeHolder(category, listener)).setViewHolder(new TreeHolderCategory(context));
            root.addChild(categoryTreeNode);
            for (Folder folder : allFolderList) {
                if (category.getId() == folder.getFolderId()) {
                    TreeNode folderTreeNode = new TreeNode(new TreeHolderFolder.IconTreeHolder(folder, allFolderList, 40, listener, selectedFolder)).setViewHolder(new TreeHolderFolder(context));
                    categoryTreeNode.addChild(folderTreeNode);
                }
            }
        }
        AndroidTreeView treeView = new AndroidTreeView(context, root);
        treeView.setDefaultAnimation(false);
        treeView.setUseAutoToggle(false);
        return treeView;
    }

    public static ItemDialogEditTextBinding getFolderEditText(Context context, String setText) {
        ItemDialogEditTextBinding binding = ItemDialogEditTextBinding.inflate(((AppCompatActivity) context).getLayoutInflater());
        binding.setHint("Folder Name");
        binding.setPlaceHolder("ex)Nike, Adidas");
        binding.setSetText(setText);
        binding.editTextDialog.requestFocus();
        binding.editTextLayoutDialog.setCounterEnabled(false);
        InputFilter inputFilter = new InputFilter.LengthFilter(1000);
        binding.editTextDialog.setFilters(new InputFilter[]{inputFilter});
        binding.editTextDialog.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        showKeyboard(context);
        return binding;
    }

    private static void showKeyboard(Context context) {
        ((InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE)).toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    private static void closeKeyboard(Context context) {
        ((InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE)).toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }
}
