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
import com.example.myfit.util.CommonUtil;
import com.unnamed.b.atv.model.TreeNode;

//TODO 테스트 시급
public abstract class BaseTreeHolder<T, Y extends BaseTuple> extends TreeNode.BaseNodeViewHolder<T> {
    private final NavController navController;
    protected boolean isClickable = true;

    public BaseTreeHolder(Context context, NavController navController) {
        super(context);
        this.navController = navController;
    }

    @Override
    public View createNodeView(TreeNode node, T value) {
        bind();
        if (!node.getChildren().isEmpty())
            getFolderIconLayout().setOnClickListener(v -> tView.toggleNode(node));
        else getArrowIcon().setVisibility(View.GONE);

        getAddIcon().setOnClickListener(v -> {
            BaseTuple tuple = getTuple();
            CommonUtil.navigate(navController, R.id.treeViewDialog,
                    TreeViewDialogDirections.toAddFolderDialog(tuple.getId(), tuple.getParentIndex()));
        });
        return getBindingRoot();
    }

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

    public long getId() {
        return getTuple().getId();
    }

    public TreeNode getNode() {
        return mNode;
    }

    public void showCurrentPosition() {
        getCurrentPosition().setVisibility(View.VISIBLE);
    }

    protected abstract Y getTuple();

    protected abstract void bind();

    protected abstract View getBindingRoot();

    protected abstract LinearLayoutCompat getFolderIconLayout();

    protected abstract TextView getNameTextView();

    protected abstract AppCompatImageView getArrowIcon();

    protected abstract AppCompatImageView getAddIcon();

    protected abstract AppCompatImageView getFolderIcon();

    protected abstract TextView getCurrentPosition();
}
