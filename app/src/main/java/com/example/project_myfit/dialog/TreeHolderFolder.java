package com.example.project_myfit.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.widget.LinearLayoutCompat;

import com.example.project_myfit.R;
import com.example.project_myfit.data.model.Folder;
import com.example.project_myfit.data.model.Size;
import com.example.project_myfit.databinding.ItemTreeFolderBinding;
import com.example.project_myfit.fragment.list.ListViewModel;
import com.example.project_myfit.util.AdapterUtil;
import com.unnamed.b.atv.model.TreeNode;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TreeHolderFolder extends TreeNode.BaseNodeViewHolder<TreeHolderFolder.FolderTreeHolder> {
    private final TreeViewFolderFolderAddListener mListener;
    private List<Folder> mSelectedFolderList, mAllFolderList;
    private List<Size> mSelectedSizeList;
    private List<Long> mFolderParentIdList, mSizeParentIdList;
    private ItemTreeFolderBinding mBinding;
    private boolean isSelected;
    private boolean mIsClickable = true;
    private final AdapterUtil mAdapterUtil;
    private final ListViewModel mListViewModel;


    public TreeHolderFolder(Context context, TreeViewFolderFolderAddListener listener, ListViewModel listViewModel) {
        super(context);
        this.mListener = listener;
        this.mListViewModel = listViewModel;
        mAdapterUtil = new AdapterUtil(context);
    }

    public TreeHolderFolder setItems(List<Folder> selectedFolderList,
                                     List<Size> selectedSizeList, List<Folder> allFolderList, List<Long> folderParentIdList, List<Long> sizeParentIdList) {
        this.mSelectedFolderList = selectedFolderList;
        this.mSelectedSizeList = selectedSizeList;
        this.mAllFolderList = allFolderList;
        this.mFolderParentIdList = folderParentIdList;
        this.mSizeParentIdList = sizeParentIdList;
        return this;
    }

    @Override
    public View createNodeView(TreeNode node, @NotNull FolderTreeHolder value) {
        mBinding = ItemTreeFolderBinding.inflate(LayoutInflater.from(context));
        mBinding.setFolder(value.folder);

        mBinding.contentsSize.setText(String.valueOf(mAdapterUtil
                .getFolderContentsSize(value.folder, mFolderParentIdList, mSizeParentIdList)));

        LinearLayoutCompat.LayoutParams params = (LinearLayoutCompat.LayoutParams) mBinding.arrowIcon.getLayoutParams();
        params.leftMargin = value.margin;

        //선택된 폴더가 이 폴더 노드라면
        for (Folder selectedFolder : mSelectedFolderList) {
            if (selectedFolder.getId() == value.folder.getId()) {
                setAlpha();
                isSelected = true;
                //부모 노드 알파
                if (node.getParent().getViewHolder() instanceof TreeHolderCategory)
                    ((TreeHolderCategory) node.getParent().getViewHolder()).setAlpha();
                else
                    ((TreeHolderFolder) node.getParent().getViewHolder()).setAlpha();
                break;
            }
        }

        //부모 노드가 선택된 폴더라면
        if (node.getParent().getViewHolder() instanceof TreeHolderFolder && ((TreeHolderFolder) node.getParent().getViewHolder()).isSelected) {
            setAlpha();
            isSelected = true;
        }

        //현재위치가 이 노드라면
        if (!mSelectedFolderList.isEmpty())
            for (Folder selectedFolder : mSelectedFolderList)
                if (selectedFolder.getParentId() == value.folder.getId()) {
                    setAlpha();
                    break;
                }


        if (!mSelectedSizeList.isEmpty())
            for (Size selectedSize : mSelectedSizeList)
                if (selectedSize.getParentId() == value.folder.getId()) {
                    setAlpha();
                    break;
                }


        int margin = (int) context.getResources().getDimension(R.dimen._8sdp);
        //자식노드 생성
        for (Folder folder : mAllFolderList)
            if (value.folder.getId() == folder.getParentId()) {
                TreeNode treeNode = new TreeNode(new FolderTreeHolder(folder, value.margin + margin))
                        .setViewHolder(new TreeHolderFolder(context, mListener, mListViewModel).
                                setItems(mSelectedFolderList, mSelectedSizeList, mAllFolderList, mFolderParentIdList, mSizeParentIdList));
                node.addChild(treeNode);
            }


        //expandable
        if (!node.getChildren().isEmpty())
            mBinding.iconLayout.setOnClickListener(v -> tView.toggleNode(node));
        else mBinding.arrowIcon.setVisibility(View.INVISIBLE);

        if (mListViewModel != null && mListViewModel.getThisFolder() != null &&
                value.folder.getId() == mListViewModel.getThisFolder().getId())
            mBinding.currentPosition.setVisibility(View.VISIBLE);
        else mBinding.currentPosition.setVisibility(View.GONE);

        mBinding.addIcon.setOnClickListener(v -> mListener.treeViewFolderAddFolderClick(mNode, value));
        return mBinding.getRoot();
    }

    private void setAlpha() {
        if (mIsClickable) {
            mBinding.iconLayout.setAlpha(0.5f);
            mBinding.text.setAlpha(0.5f);
            mIsClickable = false;
        }
    }

    public boolean isClickable() {
        return mIsClickable;
    }

    public void setIconClickable() {
        mBinding.arrowIcon.setVisibility(View.VISIBLE);
        mBinding.iconLayout.setOnClickListener(v -> tView.toggleNode(mNode));
    }

    public ItemTreeFolderBinding getBinding() {
        return mBinding;
    }

    public Folder getFolder() {
        return ((FolderTreeHolder) mNode.getValue()).folder;
    }

    public long getParentId() {
        return ((FolderTreeHolder) mNode.getValue()).folder.getId();
    }

    public int getMargin() {
        return ((FolderTreeHolder) mNode.getValue()).margin;
    }

    @Override
    public void toggle(boolean active) {
        mBinding.arrowIcon.setImageResource(active ? R.drawable.icon_triangle_down : R.drawable.icon_triangle_right);
        mBinding.folderIcon.setImageResource(active ? R.drawable.icon_folder_open : R.drawable.icon_folder);
    }

    public static class FolderTreeHolder {
        private final Folder folder;
        private final int margin;

        public FolderTreeHolder(Folder folder, int margin) {
            this.folder = folder;
            this.margin = margin;
        }
    }

    public interface TreeViewFolderFolderAddListener {
        void treeViewFolderAddFolderClick(TreeNode node, TreeHolderFolder.FolderTreeHolder value);
    }
}


