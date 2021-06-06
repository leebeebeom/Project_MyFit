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

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Accessors(prefix = "m")
public abstract class BaseTreeHolder<T extends BaseValue<?>> extends TreeNode.BaseNodeViewHolder<T> {
    protected final NavController mNavController;
    @Getter
    private boolean mClickable = true;
    @Getter
    private T mValue;
    @Getter
    protected TreeNode mNode;

    public BaseTreeHolder(Context context, NavController navController) {
        super(context);
        this.mNavController = navController;
    }

    @Override
    public View createNodeView(TreeNode node, T value) {
        this.mValue = value;
        this.mNode = node;
        bind(value);
        return getBindingRoot();
    }

    protected abstract void bind(T value);

    public void navigateAddFolderDialog() {
        CommonUtil.navigate(mNavController, R.id.treeViewDialog,
                TreeViewDialogDirections.toAddFolderDialog(mValue.getTupleId(), mValue.getParentIndex()));
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
        getArrowIcon().setImageResource(active ? R.drawable.icon_triangle_down : R.drawable.icon_triangle_right);
        getFolderIcon().setImageResource(active ? R.drawable.icon_folder_open : R.drawable.icon_folder);
    }

    public void toggleNode() {
        if (!mNode.getChildren().isEmpty())
            tView.toggleNode(mNode);
    }

    public long getTupleId() {
        return mValue.getTupleId();
    }

    public BaseTreeHolder<?> getParent() {
        return (BaseTreeHolder<?>) mNode.getParent().getViewHolder();
    }

    public void showCurrentPosition() {
        getCurrentPosition().setVisibility(View.VISIBLE);
    }

    protected abstract View getBindingRoot();

    protected abstract LinearLayoutCompat getFolderIconLayout();

    protected abstract TextView getNameTextView();

    protected abstract AppCompatImageView getArrowIcon();

    protected abstract AppCompatImageView getFolderIcon();

    protected abstract TextView getCurrentPosition();

    public abstract TextView getContentSize();
}
