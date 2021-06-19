package com.leebeebeom.closetnote.ui.dialog.tree.holder;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.navigation.NavController;

import com.leebeebeom.closetnote.NavGraphTreeViewDirections;
import com.leebeebeom.closetnote.R;
import com.leebeebeom.closetnote.data.tuple.tuple.CategoryTuple;
import com.leebeebeom.closetnote.databinding.ItemTreePostfixBinding;
import com.leebeebeom.closetnote.databinding.ItemTreePrefixBinding;
import com.leebeebeom.closetnote.ui.dialog.tree.BaseTreeValue;
import com.leebeebeom.closetnote.ui.dialog.tree.TreeViewDialogDirections;
import com.leebeebeom.closetnote.util.CommonUtil;
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
                NavGraphTreeViewDirections.toAddFolder(getTuple().getId(), getTuple().getParentIndex()));
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
