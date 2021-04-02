package com.example.project_myfit.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.widget.LinearLayoutCompat;

import com.example.project_myfit.R;
import com.example.project_myfit.data.model.Folder;
import com.example.project_myfit.data.model.Size;
import com.example.project_myfit.databinding.ItemTreeFolderBinding;
import com.example.project_myfit.util.AdapterUtils;
import com.unnamed.b.atv.model.TreeNode;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TreeHolderFolder extends TreeNode.BaseNodeViewHolder<TreeHolderFolder.FolderTreeHolder> {
    private ItemTreeFolderBinding mBinding;
    private boolean isSelected;

    public TreeHolderFolder(Context context) {
        super(context);
    }

    @Override
    public View createNodeView(TreeNode node, @NotNull FolderTreeHolder value) {
        //checked
        mBinding = ItemTreeFolderBinding.inflate(LayoutInflater.from(context));
        mBinding.setFolder(value.folder);

        mBinding.amount.setText(String.valueOf(new AdapterUtils(context)
                .getFolderContentsSize(value.folder, value.folderFolderIdList, value.sizeFolderIdList)));

        LinearLayoutCompat.LayoutParams params = (LinearLayoutCompat.LayoutParams) mBinding.arrowIcon.getLayoutParams();
        params.leftMargin = value.margin;

        //이 노드가 선택된 폴더라면 알파
        for (Folder f : value.selectedItemFolder) {
            if (f.getId() == value.folder.getId()) {
                mBinding.iconLayout.setAlpha(0.5f);
                mBinding.text.setAlpha(0.5f);
                isSelected = true;
                //부모 노드 알파
                TreeNode parent = node.getParent();
                if (parent.getViewHolder() instanceof TreeHolderCategory) {
                    TreeHolderCategory holder = (TreeHolderCategory) parent.getViewHolder();
                    holder.getBinding().iconLayout.setAlpha(0.5f);
                    holder.getBinding().text.setAlpha(0.5f);
                } else {
                    TreeHolderFolder holder = (TreeHolderFolder) parent.getViewHolder();
                    holder.getBinding().iconLayout.setAlpha(0.5f);
                    holder.getBinding().text.setAlpha(0.5f);
                }
                break;
            }
        }

        //부모 노드가 선택된 폴더라면
        if (node.getParent().getViewHolder() instanceof TreeHolderFolder && ((TreeHolderFolder) node.getParent().getViewHolder()).isSelected) {
            mBinding.iconLayout.setAlpha(0.5f);
            mBinding.text.setAlpha(0.5f);
            isSelected = true;
        }

        //현재위치가 이 노드라면
        if (value.selectedItemSize.size() != 0) {
            Size size = value.selectedItemSize.get(0);
            if (size.getFolderId() == value.folder.getId()) {
                mBinding.folderIcon.setAlpha(0.5f);
                mBinding.text.setAlpha(0.5f);
            }
        }

        int margin = (int) context.getResources().getDimension(R.dimen._8sdp);
        //자식노드 생성
        for (Folder folder : value.allFolderList) {
            if (value.folder.getId() == folder.getFolderId()) {
                TreeNode treeNode = new TreeNode(new FolderTreeHolder(folder, value.margin + margin, value.listener,
                        value.selectedItemFolder, value.selectedItemSize, value.allFolderList, value.folderFolderIdList, value.sizeFolderIdList))
                        .setViewHolder(new TreeHolderFolder(context));
                node.addChild(treeNode);
            }
        }

        if (node.getChildren().size() != 0)
            mBinding.iconLayout.setOnClickListener(v -> tView.toggleNode(node));
        else mBinding.arrowIcon.setVisibility(View.INVISIBLE);

        mBinding.addIcon.setOnClickListener(v -> value.listener.treeViewFolderFolderAddClick(mNode, value));
        return mBinding.getRoot();
    }

    public void setIconClickable() {
        //checked
        mBinding.arrowIcon.setVisibility(View.VISIBLE);
        mBinding.iconLayout.setOnClickListener(v -> tView.toggleNode(mNode));
    }

    public boolean isNotSelected() {
        return !isSelected;
    }

    public ItemTreeFolderBinding getBinding() {
        return mBinding;
    }

    @Override
    public void toggle(boolean active) {
        mBinding.arrowIcon.setImageResource(active ? R.drawable.icon_triangle_down : R.drawable.icon_triangle_right);
        mBinding.folderIcon.setImageResource(active ? R.drawable.icon_folder_open : R.drawable.icon_folder);
    }

    public static class FolderTreeHolder {
        //all checked
        private final Folder folder;
        private final int margin;
        private final TreeViewFolderFolderAddListener listener;
        private final List<Folder> selectedItemFolder, allFolderList;
        private final List<Size> selectedItemSize;
        private final List<Long> folderFolderIdList, sizeFolderIdList;

        public FolderTreeHolder(Folder folder, int margin, TreeViewFolderFolderAddListener listener, List<Folder> selectedItemFolder,
                                List<Size> selectedItemSize, List<Folder> allFolderList, List<Long> folderFolderIdList, List<Long> sizeFolderIdList) {
            //checked
            this.folder = folder;
            this.margin = margin;
            this.listener = listener;
            this.selectedItemFolder = selectedItemFolder;
            this.selectedItemSize = selectedItemSize;
            this.allFolderList = allFolderList;
            this.folderFolderIdList = folderFolderIdList;
            this.sizeFolderIdList = sizeFolderIdList;
        }

        public Folder getFolder() {
            return folder;
        }
    }

    public interface TreeViewFolderFolderAddListener {
        //checked
        void treeViewFolderFolderAddClick(TreeNode node, TreeHolderFolder.FolderTreeHolder value);
    }
}


