package com.example.myfit.ui.dialog.tree.holder;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.navigation.NavController;

import com.example.myfit.R;
import com.example.myfit.data.tuple.tuple.CategoryTuple;
import com.example.myfit.databinding.ItemTreePostfixBinding;
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

    protected abstract void bind();

    @Override
    public View createNodeView(TreeNode node, BaseTreeValue<U> value) {
        return getBindingRoot();
    }

    protected abstract View getBindingRoot();

    public void navigateAddFolderDialog() {
        CommonUtil.navigate(mNavController, R.id.treeViewDialog,
                TreeViewDialogDirections.toAddFolderDialog(getTuple().getId(), getTuple().getParentIndex()));
    }

    public abstract U getTuple();

    public void setUnClickable() {
        if (mClickable) {
            getPrefix().layout.setAlpha(0.5f);
            getNameTextView().setAlpha(0.5f);
            mClickable = false;
        }
    }

    protected abstract ItemTreePrefixBinding getPrefix();

    protected abstract TextView getNameTextView();

    public void showArrowIcon() {
        if (getPrefix().iconArrow.getVisibility() != View.VISIBLE)
            getPrefix().iconArrow.setVisibility(View.VISIBLE);
    }

    @Override
    public void toggle(boolean active) {
        getPrefix().setActive(active);
    }


    public long getTupleId() {
        return getTuple().getId();
    }

    public BaseTreeHolder<?> getParentViewHolder() {
        return (BaseTreeHolder<?>) mNode.getParent().getViewHolder();
    }

    public void showCurrentPosition() {
        getPostFix().tvCurrentPosition.setVisibility(View.VISIBLE);
    }

    public abstract ItemTreePostfixBinding getPostFix();

    public TreeNode getNode() {
        return mNode;
    }

    public void addChild(List<TreeNode> folderNodes){
        folderNodes.stream()
                .filter(folderNode -> ((BaseTreeValue.FolderValue) folderNode.getValue()).getTuple().getParentId() == getTupleId())
                .forEach(folderNode -> mNode.addChild(folderNode));
    }
}
