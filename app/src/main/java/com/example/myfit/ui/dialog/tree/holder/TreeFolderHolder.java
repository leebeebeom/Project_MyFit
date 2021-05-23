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
import com.example.myfit.ui.dialog.tree.holder.value.FolderValue;
import com.unnamed.b.atv.model.TreeNode;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TreeFolderHolder extends BaseTreeHolder<FolderTuple, FolderValue> {
    private final List<FolderTuple> folderTuples;
    private ItemTreeFolderBinding binding;

    public TreeFolderHolder(Context context,
                            List<FolderTuple> folderTuples, NavController navController) {
        super(context, navController);
        this.folderTuples = folderTuples;
    }

    @Override
    protected void bind(FolderTuple tuple) {
        binding = ItemTreeFolderBinding.inflate(LayoutInflater.from(context));
        binding.setFolderTuple(tuple);
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

    @Override//TODO super 콜 후 다시 뷰 리턴 되는지 확인
    public View createNodeView(TreeNode node, @NotNull FolderValue value) {
        super.createNodeView(node, value);
        setMargin(value.getMargin());
        addChildNode(value);
        return binding.getRoot();
    }

    private void setMargin(int margin) {
        LinearLayoutCompat.LayoutParams params = (LinearLayoutCompat.LayoutParams) binding.iconArrow.getLayoutParams();
        params.leftMargin = margin;
    }

    private void addChildNode(@NotNull FolderValue value) {
        int margin = (int) context.getResources().getDimensionPixelSize(R.dimen._8sdp);
        folderTuples.stream()
                .filter(folderTuple -> folderTuple.getParentId() == value.getId())
                .forEach(folderTuple -> {
                    TreeNode treeNode = new TreeNode(new FolderValue(folderTuple, value.getMargin() + margin))
                            .setViewHolder(this);
                    mNode.addChild(treeNode);
                });
    }
}


