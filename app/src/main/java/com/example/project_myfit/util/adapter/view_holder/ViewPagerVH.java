package com.example.project_myfit.util.adapter.view_holder;

import android.annotation.SuppressLint;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.example.project_myfit.databinding.ItemRecyclerViewBinding;

import org.jetbrains.annotations.NotNull;

import static com.example.project_myfit.util.MyFitConstant.DOWN;
import static com.example.project_myfit.util.MyFitConstant.STOP;
import static com.example.project_myfit.util.MyFitConstant.UP;

public class ViewPagerVH extends RecyclerView.ViewHolder {
    private final ItemRecyclerViewBinding mBinding;

    @SuppressLint("ClickableViewAccessibility")
    public ViewPagerVH(@NotNull ItemRecyclerViewBinding binding, ViewPagerAutoScrollListener listener) {
        super(binding.getRoot());
        this.mBinding = binding;

        mBinding.rvItemRv.setOnTouchListener((v, event) -> {
            if (event.getRawY() > 2000)
                listener.dragAutoScroll(DOWN);
            else if (event.getRawY() < 250)
                listener.dragAutoScroll(UP);
            else if (event.getRawY() < 2000 && event.getRawY() > 250)
                listener.dragAutoScroll(STOP);
            return false;
        });
    }

    public void setNoResult(boolean isEmpty) {
        if (isEmpty)
            mBinding.tvItemRvNoResultLayout.setVisibility(View.VISIBLE);
        else mBinding.tvItemRvNoResultLayout.setVisibility(View.GONE);
    }

    public void setNoData(boolean isEmpty) {
        if (isEmpty)
            mBinding.tvItemRvNoDataLayout.setVisibility(View.VISIBLE);
        else mBinding.tvItemRvNoDataLayout.setVisibility(View.GONE);
    }

    public ItemRecyclerViewBinding getBinding() {
        return mBinding;
    }

    public interface ViewPagerAutoScrollListener {
        void dragAutoScroll(int upDown);
    }
}
