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
import com.example.myfit.databinding.ItemTreePostfixBinding;
import com.example.myfit.databinding.ItemTreePrefixBinding;
import com.example.myfit.ui.dialog.tree.BaseTreeValue;
import com.unnamed.b.atv.model.TreeNode;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class TreeFolderHolder extends BaseTreeHolder<FolderTuple> {
    private ItemTreeFolderBinding mBinding;

    public TreeFolderHolder(Context context, NavController navController) {
        super(context, navController);
    }

    @Override
    protected void bind() {
        mBinding = ItemTreeFolderBinding.inflate(LayoutInflater.from(context));
        mBinding.setFolderHolder(this);
    }

    @Override
    protected View getBindingRoot() {
        return mBinding.getRoot();
    }

    @Override
    public FolderTuple getTuple() {
        return ((BaseTreeValue.FolderValue) getNode().getValue()).getTuple();
    }

    @Override
    protected ItemTreePrefixBinding getPrefix() {
        return mBinding.prefix;
    }

    @Override
    protected TextView getNameTextView() {
        return mBinding.tv.tv;
    }

    @Override
    protected ItemTreePostfixBinding getPostFix() {
        return mBinding.postfix;
    }

    public int getMargin() {
        AtomicInteger parentCount = new AtomicInteger(0);
        parentCount(parentCount, mNode);
        int margin = context.getResources().getDimensionPixelSize(R.dimen._12sdp);
        int plusMargin = context.getResources().getDimensionPixelSize(R.dimen._8sdp);
        int count = parentCount.get();
        for (int i = 0; i < count; i++)
            margin = margin + plusMargin;
        return margin;
    }

    public void parentCount(AtomicInteger count, TreeNode node) {
        if (node.getParent() != null) {
            count.set(count.get() + 1);
            parentCount(count, node.getParent());
        }
    }
}


