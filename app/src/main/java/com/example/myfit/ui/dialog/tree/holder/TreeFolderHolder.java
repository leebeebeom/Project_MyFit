package com.example.myfit.ui.dialog.tree.holder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.navigation.NavController;

import com.example.myfit.R;
import com.example.myfit.data.model.folder.FolderTuple;
import com.example.myfit.databinding.ItemTreeFolderBinding;
import com.unnamed.b.atv.model.TreeNode;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TreeFolderHolder extends BaseTreeHolder<TreeFolderHolder.TreeFolderValue, FolderTuple> {
    private final List<FolderTuple> folderTuples;
    private ItemTreeFolderBinding binding;

    public TreeFolderHolder(Context context,
                            List<FolderTuple> folderTuples, NavController navController) {
        super(context, navController);
        this.folderTuples = folderTuples;
    }

    @Override
    protected FolderTuple getTuple() {
        return ((TreeFolderValue) mNode.getValue()).folderTuple;
    }

    @Override
    protected void bind() {
        binding = ItemTreeFolderBinding.inflate(LayoutInflater.from(context));
        binding.setFolderTuple(getTuple());
    }

    @Override
    protected View getBindingRoot() {
        return binding.getRoot();
    }

    @Override
    protected LinearLayoutCompat getFolderIconLayout() {
        return binding.layoutFolderIcon;
    }

    @Override
    protected TextView getNameTextView() {
        return binding.tvFolderName;
    }

    @Override
    protected AppCompatImageView getArrowIcon() {
        return binding.iconArrow;
    }

    @Override
    protected AppCompatImageView getAddIcon() {
        return binding.iconAdd;
    }

    @Override
    protected AppCompatImageView getFolderIcon() {
        return binding.iconFolder;
    }

    @Override
    protected TextView getCurrentPosition() {
        return binding.tvCurrentPosition;
    }

    @Override
    public View createNodeView(TreeNode node, @NotNull TreeFolderValue value) {
        super.createNodeView(node, value);
        setMargin();
        addChildNode();
        return binding.getRoot();
    }

    private void setMargin() {
        LinearLayoutCompat.LayoutParams params = (LinearLayoutCompat.LayoutParams) binding.iconArrow.getLayoutParams();
        params.leftMargin = getMargin();
    }

    private void addChildNode() {
        int margin = (int) context.getResources().getDimensionPixelSize(R.dimen._8sdp);
        folderTuples.stream()
                .filter(folderTuple -> folderTuple.getParentId() == getTuple().getId())
                .forEach(folderTuple -> {
                    TreeNode treeNode = new TreeNode(new TreeFolderValue(folderTuple, getMargin() + margin))
                            .setViewHolder(this);
                    mNode.addChild(treeNode);
                });
    }

    public int getMargin() {
        return ((TreeFolderValue) mNode.getValue()).margin;
    }

    public static class TreeFolderValue {
        private final FolderTuple folderTuple;
        private final int margin;

        public TreeFolderValue(FolderTuple folderTuple, int margin) {
            this.folderTuple = folderTuple;
            this.margin = margin;
        }
    }
}


