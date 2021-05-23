package com.example.myfit.ui.dialog.tree.holder;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.navigation.NavController;

import com.example.myfit.R;
import com.example.myfit.data.model.tuple.BaseTuple;
import com.example.myfit.ui.dialog.tree.TreeViewDialogDirections;
import com.example.myfit.ui.dialog.tree.holder.value.BaseValue;
import com.example.myfit.util.CommonUtil;
import com.unnamed.b.atv.model.TreeNode;

public abstract class BaseTreeHolder<V extends BaseTuple, T extends BaseValue<V>> extends TreeNode.BaseNodeViewHolder<T> {
    private final NavController navController;
    protected boolean isClickable = true;

    public BaseTreeHolder(Context context, NavController navController) {
        super(context);
        this.navController = navController;
    }

    @Override
    public View createNodeView(TreeNode node, T value) {
        bind(value.getTuple());
        if (!node.getChildren().isEmpty()) {
            getArrowIcon().setVisibility(View.GONE);
            getFolderIconLayout().setOnClickListener(v -> tView.toggleNode(node));
        }

        getAddIcon().setOnClickListener(v ->
                CommonUtil.navigate(navController, R.id.treeViewDialog,
                        TreeViewDialogDirections.toAddFolderDialog(value.getId(), value.getParentIndex())));
        return getBindingRoot();
    }

    protected abstract void bind(V tuple);

    public void setAlpha() {
        if (isClickable) {
            getFolderIconLayout().setAlpha(0.5f);
            getNameTextView().setAlpha(0.5f);
            isClickable = false;
        }
    }

    public boolean isClickable() {
        return isClickable;
    }

    public void setIconClickable() {
        getFolderIconLayout().setOnClickListener(v -> tView.toggleNode(mNode));
        getArrowIcon().setVisibility(View.VISIBLE);
    }

    @Override
    public void toggle(boolean active) {
        getArrowIcon().setImageResource(active ? R.drawable.icon_triangle_down : R.drawable.icon_triangle_right);
        getFolderIcon().setImageResource(active ? R.drawable.icon_folder_open : R.drawable.icon_folder);
    }

    public TreeNode getNode() {
        return mNode;
    }

    public void showCurrentPosition() {
        getCurrentPosition().setVisibility(View.VISIBLE);
    }

    protected abstract View getBindingRoot();

    protected abstract LinearLayoutCompat getFolderIconLayout();

    protected abstract TextView getNameTextView();

    protected abstract AppCompatImageView getArrowIcon();

    protected abstract AppCompatImageView getAddIcon();

    protected abstract AppCompatImageView getFolderIcon();

    protected abstract TextView getCurrentPosition();
}
