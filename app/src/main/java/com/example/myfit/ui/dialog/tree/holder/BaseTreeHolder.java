package com.example.myfit.ui.dialog.tree.holder;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.navigation.NavController;

import com.example.myfit.R;
import com.example.myfit.ui.dialog.tree.TreeViewDialogDirections;
import com.example.myfit.ui.dialog.tree.holder.value.BaseValue;
import com.example.myfit.util.CommonUtil;
import com.unnamed.b.atv.model.TreeNode;

public abstract class BaseTreeHolder<T extends BaseValue<?>> extends TreeNode.BaseNodeViewHolder<T> {
    private final NavController mNavController;
    private boolean mClickable = true;
    private T mValue;

    public BaseTreeHolder(Context context, NavController navController) {
        super(context);
        this.mNavController = navController;
    }

    @Override
    public View createNodeView(TreeNode node, T value) {
        this.mValue = value;
        bind(value);

        if (!node.getChildren().isEmpty()) {
            getArrowIcon().setVisibility(View.VISIBLE);
            getFolderIconLayout().setOnClickListener(v -> tView.toggleNode(node));
        }

        getAddIcon().setOnClickListener(v ->
                CommonUtil.navigate(mNavController, R.id.treeViewDialog,
                        TreeViewDialogDirections.toAddFolderDialog(value.getTupleId(), value.getParentIndex())));
        return getBindingRoot();
    }

    protected abstract void bind(T value);

    public void setAlpha() {
        if (mClickable) {
            getFolderIconLayout().setAlpha(0.5f);
            getNameTextView().setAlpha(0.5f);
            mClickable = false;
        }
    }

    public boolean isClickable() {
        return mClickable;
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

    public TreeNode getNode(){
        return mNode;
    };

    public long getId() {
        return mValue.getTupleId();
    }

    public BaseTreeHolder<?> getParent(){
        return (BaseTreeHolder<?>) mNode.getParent().getViewHolder();
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

    public abstract TextView getContentsSize();
}
