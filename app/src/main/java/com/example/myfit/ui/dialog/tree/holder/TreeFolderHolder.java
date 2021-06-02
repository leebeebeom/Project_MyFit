package com.example.myfit.ui.dialog.tree.holder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.navigation.NavController;

import com.example.myfit.R;
import com.example.myfit.data.tuple.tuple.FolderTuple;
import com.example.myfit.databinding.ItemTreeFolderBinding;
import com.example.myfit.ui.dialog.tree.holder.value.FolderValue;
import com.unnamed.b.atv.model.TreeNode;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TreeFolderHolder extends BaseTreeHolder<FolderValue> {
    private final List<FolderTuple> mFolderTuples;
    private ItemTreeFolderBinding mBinding;

    public TreeFolderHolder(Context context,
                            List<FolderTuple> folderTuples, NavController navController) {
        super(context, navController);
        this.mFolderTuples = folderTuples;
    }

    @Override
    protected void bind(FolderValue value) {
        mBinding = ItemTreeFolderBinding.inflate(LayoutInflater.from(context));
        mBinding.setFolderHolder(this);
    }

    @Override
    protected View getBindingRoot() {
        return mBinding.getRoot();
    }

    @Override
    protected LinearLayoutCompat getFolderIconLayout() {
        return mBinding.layoutFolderIcon;
    }

    @Override
    protected TextView getNameTextView() {
        return mBinding.tvFolderName;
    }

    @Override
    protected AppCompatImageView getArrowIcon() {
        return mBinding.iconArrow;
    }

    @Override
    protected AppCompatImageView getFolderIcon() {
        return mBinding.iconFolder;
    }

    @Override
    protected TextView getCurrentPosition() {
        return mBinding.tvCurrentPosition;
    }

    @Override
    public TextView getContentSize() {
        return mBinding.tvContentSize;
    }

    public View createNodeView(TreeNode node, @NotNull FolderValue value) {
        addChildNode(value);
        return super.createNodeView(node, value);
    }

    private void addChildNode(@NotNull FolderValue value) {
        int plusMargin = (int) context.getResources().getDimensionPixelSize(R.dimen._8sdp);
        mFolderTuples.stream()
                .filter(folderTuple -> folderTuple.getParentId() == value.getTupleId())
                .forEach(folderTuple -> {
                    TreeNode treeNode = new TreeNode(new FolderValue(folderTuple, value.getMargin() + plusMargin))
                            .setViewHolder(new TreeFolderHolder(context, mFolderTuples, mNavController));
                    mNode.addChild(treeNode);
                });
    }
}


