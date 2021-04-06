package com.example.project_myfit.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.widget.LinearLayoutCompat;

import com.example.project_myfit.R;
import com.example.project_myfit.data.model.Folder;
import com.example.project_myfit.data.model.Size;
import com.example.project_myfit.databinding.ItemTreeFolderBinding;
import com.example.project_myfit.util.AdapterUtil;
import com.unnamed.b.atv.model.TreeNode;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TreeHolderFolder extends TreeNode.BaseNodeViewHolder<TreeHolderFolder.FolderTreeHolder> {
    private ItemTreeFolderBinding mBinding;
    private boolean isSelected;
    private boolean mIsClickable;

    public TreeHolderFolder(Context context) {
        super(context);
    }

    @Override
    public View createNodeView(TreeNode node, @NotNull FolderTreeHolder value) {
        //checked
        mBinding = ItemTreeFolderBinding.inflate(LayoutInflater.from(context));
        mBinding.setFolder(value.folder);

        mBinding.contentsSize.setText(String.valueOf(new AdapterUtil(context)
                .getFolderContentsSize(value.folder, value.folderFolderIdList, value.sizeFolderIdList)));

        LinearLayoutCompat.LayoutParams params = (LinearLayoutCompat.LayoutParams) mBinding.arrowIcon.getLayoutParams();
        params.leftMargin = value.margin;

        //선택된 폴더가 이 노드라면
        for (Folder selectedFolder : value.selectedFolderList) {
            if (selectedFolder.getId() == value.folder.getId()) {
                setAlpha();
                isSelected = true;
                //부모 노드 알파
                TreeNode parent = node.getParent();
                if (parent.getViewHolder() instanceof TreeHolderCategory) {
                    TreeHolderCategory holder = (TreeHolderCategory) parent.getViewHolder();
                    holder.setAlpha();
                } else {
                    TreeHolderFolder holder = (TreeHolderFolder) parent.getViewHolder();
                    holder.setAlpha();
                }
                break;
            }
        }

        //부모 노드가 선택된 폴더라면
        if (node.getParent().getViewHolder() instanceof TreeHolderFolder && ((TreeHolderFolder) node.getParent().getViewHolder()).isSelected) {
            setAlpha();
            isSelected = true;
        }

        //현재위치가 이 노드라면
        if (value.selectedSizeList.size() != 0) {
            Size selectedSize = value.selectedSizeList.get(0);
            if (selectedSize.getFolderId() == value.folder.getId())
                setAlpha();
        }

        if (value.selectedFolderList.size() != 0) {
            Folder selectedFolder = value.selectedFolderList.get(0);
            if (selectedFolder.getFolderId() == value.folder.getId())
                setAlpha();
        }

        int margin = (int) context.getResources().getDimension(R.dimen._8sdp);
        //자식노드 생성
        for (Folder folder : value.allFolderList) {
            if (value.folder.getId() == folder.getFolderId()) {
                TreeNode treeNode = new TreeNode(new FolderTreeHolder(folder, value.margin + margin, value.listener,
                        value.selectedFolderList, value.selectedSizeList, value.allFolderList, value.folderFolderIdList, value.sizeFolderIdList))
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

    private void setAlpha() {
        if (mBinding.iconLayout.getAlpha() != 0.5f && mBinding.text.getAlpha() != 0.5f) {
            mIsClickable = false;
            mBinding.iconLayout.setAlpha(0.5f);
            mBinding.text.setAlpha(0.5f);
        }
    }

    public boolean isClickable() {
        return mIsClickable;
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
        private final List<Folder> selectedFolderList, allFolderList;
        private final List<Size> selectedSizeList;
        private final List<Long> folderFolderIdList, sizeFolderIdList;

        public FolderTreeHolder(Folder folder, int margin, TreeViewFolderFolderAddListener listener, List<Folder> selectedFolderList,
                                List<Size> selectedSizeList, List<Folder> allFolderList, List<Long> folderFolderIdList, List<Long> sizeFolderIdList) {
            //checked
            this.folder = folder;
            this.margin = margin;
            this.listener = listener;
            this.selectedFolderList = selectedFolderList;
            this.selectedSizeList = selectedSizeList;
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


