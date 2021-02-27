package com.example.project_myfit.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.project_myfit.R;
import com.example.project_myfit.databinding.TreeViewRootBinding;
import com.example.project_myfit.searchActivity.SearchFragment;
import com.example.project_myfit.searchActivity.SearchViewModel;
import com.example.project_myfit.ui.main.database.Category;
import com.example.project_myfit.ui.main.listfragment.ListFragment;
import com.example.project_myfit.ui.main.listfragment.ListViewModel;
import com.example.project_myfit.ui.main.listfragment.database.Folder;
import com.example.project_myfit.ui.main.listfragment.database.Size;
import com.example.project_myfit.ui.main.listfragment.treeview.TreeHolderCategory;
import com.example.project_myfit.ui.main.listfragment.treeview.TreeHolderFolder;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.view.AndroidTreeView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.example.project_myfit.MyFitConstant.TREE_VIEW_STATE;

public class TreeViewDialog extends DialogFragment implements AddCategoryDialog.AddCategoryConfirmClick {
    private TreeNode mNodeRoot;
    private ListViewModel mListModel;
    private SearchViewModel mSearchModel;
    private AndroidTreeView mTreeView;
    private TreeNode.TreeNodeClickListener mListener;
    private List<TreeNode> mCategoryTreeNodeList;

    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        super.onAttach(context);
        mListener = (TreeNode.TreeNodeClickListener) getTargetFragment();
    }

    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        if (getTargetFragment() != null) {
            if (getTargetFragment() instanceof ListFragment)
                mListModel = new ViewModelProvider(getTargetFragment()).get(ListViewModel.class);
            else if (getTargetFragment() instanceof SearchFragment)
                mSearchModel = new ViewModelProvider(getTargetFragment()).get(SearchViewModel.class);
        }
        String parentCategory = null;
        if (mListModel != null)
            parentCategory = " " + mListModel.getThisCategory().getParentCategory();
        else if (mSearchModel != null) parentCategory = " " + mSearchModel.getParentCategory();
        mTreeView = getTreeView();

        //tree view root binding for addCategory
        TreeViewRootBinding binding = TreeViewRootBinding.inflate(getLayoutInflater());
        binding.treeViewRoot.addView(mTreeView.getView(), 2);
        binding.treeViewParentText.setText(parentCategory);
        binding.addCategoryLayout.setOnClickListener(v -> {
            DialogFragment dialog = new AddCategoryDialog();
            dialog.setTargetFragment(this, 0);
            dialog.show(getParentFragmentManager(), null);
        });

        //create dialog
        AlertDialog dialog = new MaterialAlertDialogBuilder(requireContext(), R.style.myAlertDialog)
                .setView(binding.getRoot())
                .setTitle(R.string.tree_view_dialog_title)
                .create();

        Window window = dialog.getWindow();
        DialogUtils.setLayout(requireContext(), window);
        DialogUtils.setTextSize(requireContext(), dialog);
        return dialog;
    }

    @NotNull
    private AndroidTreeView getTreeView() {
        int margin = (int) getResources().getDimension(R.dimen._12sdp);
        mCategoryTreeNodeList = new ArrayList<>();

        //category list
        List<Category> categoryList = new ArrayList<>();
        if (mListModel != null)
            categoryList = mListModel.getCategoryListByParent();
        else if (mSearchModel != null)
            categoryList = mSearchModel.getRepository().getCategoryListByParent(mSearchModel.getParentCategory());

        //folder list
        List<Folder> folderList = new ArrayList<>();
        if (mListModel != null)
            folderList = mListModel.getAllFolderByParent();
        else if (mSearchModel != null)
            folderList = mSearchModel.getRepository().getAllFolderListByParent(mSearchModel.getParentCategory());

        //selected item folder list
        List<Folder> selectedItemFolder = new ArrayList<>();
        if (mListModel != null)
            selectedItemFolder = mListModel.getSelectedItemFolder();
        else if (mSearchModel != null) {
            List<Object> selectedItem = mSearchModel.getSelectedItem();
            for (Object o : selectedItem)
                if (o instanceof Folder)
                    selectedItemFolder.add((Folder) o);
        }

        //selected item size list
        List<Size> selectedItemSize = new ArrayList<>();
        if (mListModel != null)
            selectedItemSize = mListModel.getSelectedItemSize();
        else if (mSearchModel != null) {
            List<Object> selectedItem = mSearchModel.getSelectedItem();
            for (Object o : selectedItem)
                if (o instanceof Size)
                    selectedItemSize.add((Size) o);
        }

        //folder folderId list
        List<Long> folderFolderIdList = new ArrayList<>();
        if (mListModel != null)
            folderFolderIdList = mListModel.getRepository().getFolderFolderIdByParent(mListModel.getThisCategory().getParentCategory());
        else if (mSearchModel != null)
            folderFolderIdList = mSearchModel.getRepository().getFolderFolderIdByParent(mSearchModel.getParentCategory());

        //size folderId list
        List<Long> sizeFolderIdList = new ArrayList<>();
        if (mListModel != null)
            sizeFolderIdList = mListModel.getRepository().getSizeFolderIdByParent(mListModel.getThisCategory().getParentCategory());
        else if (mSearchModel != null)
            sizeFolderIdList = mSearchModel.getRepository().getSizeFolderIdByParent(mSearchModel.getParentCategory());

        for (Category category : categoryList) {
            TreeNode categoryTreeNode = new TreeNode(new TreeHolderCategory.CategoryTreeHolder(category, (TreeViewAddClick) mListener, selectedItemSize,
                    folderFolderIdList, sizeFolderIdList))
                    .setViewHolder(new TreeHolderCategory(requireContext()));
            for (Folder folder : folderList) {
                if (category.getId() == folder.getFolderId()) {
                    TreeNode folderTreeNode = new TreeNode(new TreeHolderFolder.FolderTreeHolder(folder, margin, (TreeViewAddClick) mListener,
                            selectedItemFolder, selectedItemSize, folderList, folderFolderIdList, sizeFolderIdList)).setViewHolder(new TreeHolderFolder(requireContext()));
                    categoryTreeNode.addChild(folderTreeNode);
                }
            }
            mCategoryTreeNodeList.add(categoryTreeNode);
        }

        mNodeRoot = TreeNode.root();
        mNodeRoot.addChildren(mCategoryTreeNodeList);
        AndroidTreeView treeView = new AndroidTreeView(requireContext(), mNodeRoot);
        treeView.setDefaultAnimation(false);
        treeView.setUseAutoToggle(false);
        treeView.setDefaultNodeClickListener(mListener);
        if (mListModel != null)
            mListModel.setTreeNodeRoot(mNodeRoot);
        else if (mSearchModel != null)
            mSearchModel.setTreeNodeRoot(mNodeRoot);

        return treeView;
    }

    @Override
    public void onActivityCreated(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        List<TreeNode> topFolderList = new ArrayList<>();
        for (TreeNode categoryNode : mCategoryTreeNodeList)
            if (categoryNode.getChildren().size() != 0)
                topFolderList.addAll(categoryNode.getChildren());
        if (mListModel != null) expandingNode(topFolderList);

        if (savedInstanceState != null)
            mTreeView.restoreState(savedInstanceState.getString(TREE_VIEW_STATE));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mListModel != null)
            mListModel.setTreeNodeRoot(null);
        else if (mSearchModel != null)
            mSearchModel.setTreeNodeRoot(null);
    }

    private void expandingNode(List<TreeNode> topFolderNodeList) {
        for (TreeNode categoryTreeNode : mCategoryTreeNodeList) {
            TreeHolderCategory.CategoryTreeHolder holder = (TreeHolderCategory.CategoryTreeHolder) categoryTreeNode.getValue();
            if (mListModel.getThisCategory().getId() == holder.category.getId() && categoryTreeNode.getChildren().size() != 0)
                mTreeView.expandNode(categoryTreeNode);
        }

        if (mListModel.getThisFolder() != null) {//if current position is folder
            for (TreeNode folderNode : topFolderNodeList) {
                //visible current position text
                if (mListModel.getFolderHistory().get(mListModel.getFolderHistory().size() - 1).getId() == ((TreeHolderFolder.FolderTreeHolder) folderNode.getValue()).getFolder().getId())
                    ((TreeHolderFolder) folderNode.getViewHolder()).getBinding().currentPosition.setVisibility(View.VISIBLE);
                for (Folder folder : mListModel.getFolderHistory())
                    if (((TreeHolderFolder.FolderTreeHolder) folderNode.getValue()).getFolder().getId() == folder.getId() && folderNode.getChildren().size() != 0)
                        mTreeView.expandNode(folderNode);
                if (folderNode.getChildren().size() != 0)
                    expandingNode(folderNode.getChildren());
            }
        } else {//current position is category
            for (TreeNode categoryNode : mCategoryTreeNodeList)
                if (mListModel.getThisCategory().getId() == ((TreeHolderCategory.CategoryTreeHolder) categoryNode.getValue()).category.getId())
                    ((TreeHolderCategory) categoryNode.getViewHolder()).getBinding().currentPosition.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void addCategoryConfirmClick(@NotNull String categoryName) {
        Category addedCategory = null;
        List<Size> selectedItemSize = new ArrayList<>();
        List<Long> folderFolderIdList = new ArrayList<>();
        List<Long> sizeFolderIdList = new ArrayList<>();
        if (mListModel != null) {
            int orderNumber = mListModel.getRepository().getCategoryLargestOrder() + 1;
            Category category = new Category(categoryName.trim(), mListModel.getThisCategory().getParentCategory(), orderNumber);
            addedCategory = mListModel.getRepository().treeViewAddCategory(category);
            selectedItemSize = mListModel.getSelectedItemSize();
            folderFolderIdList = mListModel.getRepository().getFolderFolderIdByParent(mListModel.getThisCategory().getParentCategory());
            sizeFolderIdList = mListModel.getRepository().getSizeFolderIdByParent(mListModel.getThisCategory().getParentCategory());
        } else if (mSearchModel != null) {
            int orderNumber = mSearchModel.getRepository().getCategoryLargestOrder() + 1;
            Category category = new Category(categoryName.trim(), mSearchModel.getParentCategory(), orderNumber);
            addedCategory = mSearchModel.getRepository().treeViewAddCategory(category);
            for (Object o : mSearchModel.getSelectedItem())
                if (o instanceof Size) selectedItemSize.add((Size) o);
            folderFolderIdList = mSearchModel.getRepository().getFolderFolderIdByParent(mSearchModel.getParentCategory());
            sizeFolderIdList = mSearchModel.getRepository().getSizeFolderIdByParent(mSearchModel.getParentCategory());
        }

        TreeNode categoryNode = new TreeNode(new TreeHolderCategory.CategoryTreeHolder(addedCategory, (TreeViewAddClick) mListener, selectedItemSize, folderFolderIdList, sizeFolderIdList))
                .setViewHolder(new TreeHolderCategory(requireContext()));
        mTreeView.addNode(mNodeRoot, categoryNode);
    }

    @Override
    public void onSaveInstanceState(@NonNull @NotNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(TREE_VIEW_STATE, mTreeView.getSaveState());
    }

    public interface TreeViewAddClick {
        void treeViewCategoryAddClick(TreeNode node, TreeHolderCategory.CategoryTreeHolder value);

        void treeViewFolderAddClick(TreeNode node, TreeHolderFolder.FolderTreeHolder value);
    }
}
