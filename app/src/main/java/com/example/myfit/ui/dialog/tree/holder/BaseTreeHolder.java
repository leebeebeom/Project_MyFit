package com.example.myfit.ui.dialog.tree.holder;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.navigation.NavController;

import com.example.myfit.R;
import com.example.myfit.data.tuple.tuple.CategoryTuple;
import com.example.myfit.databinding.ItemTreePrefixBinding;
import com.example.myfit.ui.dialog.tree.BaseTreeValue;
import com.example.myfit.ui.dialog.tree.TreeViewDialogDirections;
import com.example.myfit.util.CommonUtil;
import com.unnamed.b.atv.model.TreeNode;

import java.util.List;

import lombok.Getter;
import lombok.experimental.Accessors;

@Accessors(prefix = "m")
public abstract class BaseTreeHolder<U extends CategoryTuple> extends TreeNode.BaseNodeViewHolder<BaseTreeValue<U>> {
    protected final NavController mNavController;
    @Getter
    private boolean mClickable = true;

    public BaseTreeHolder(Context context, NavController navController) {
        super(context);
        this.mNavController = navController;
        bind();
    }

    @Override
    public View createNodeView(TreeNode node, BaseTreeValue<U> value) {
        return getBindingRoot();
    }

    protected abstract void bind();

    public void navigateAddFolderDialog() {
        CommonUtil.navigate(mNavController, R.id.treeViewDialog,
                TreeViewDialogDirections.toAddFolderDialog(getTuple().getId(), getTuple().getParentIndex()));
    }

    public void setUnClickable() {
        if (mClickable) {
            getFolderIconLayout().setAlpha(0.5f);
            getNameTextView().setAlpha(0.5f);
            mClickable = false;
        }
    }

    public void showArrowIcon() {
        if (getArrowIcon().getVisibility() != View.VISIBLE)
            getArrowIcon().setVisibility(View.VISIBLE);
    }

    @Override
    public void toggle(boolean active) {
        getPrefix().setActive(active);
    }

    public abstract U getTuple();

    public long getTupleId() {
        return getTuple().getId();
    }

    public BaseTreeHolder<?> getParentViewHolder() {
        return (BaseTreeHolder<?>) mNode.getParent().getViewHolder();
    }

    public void showCurrentPosition() {
        getCurrentPosition().setVisibility(View.VISIBLE);
    }

    protected abstract View getBindingRoot();

    protected abstract LinearLayoutCompat getFolderIconLayout();

    protected abstract TextView getNameTextView();

    protected abstract AppCompatImageView getArrowIcon();

    protected abstract TextView getCurrentPosition();

    public abstract TextView getContentSize();

    public TreeNode getNode() {
        return mNode;
    }

    protected abstract ItemTreePrefixBinding getPrefix();

    public void addChild(List<TreeNode> folderNodes){
        folderNodes.stream()
                .filter(folderNode -> ((BaseTreeValue.FolderValue) folderNode.getValue()).getTuple().getParentId() == getTupleId())
                .forEach(folderNode -> mNode.addChild(folderNode));
    }
}
