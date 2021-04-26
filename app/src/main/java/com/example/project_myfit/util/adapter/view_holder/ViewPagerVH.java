package com.example.project_myfit.util.adapter.view_holder;

import android.annotation.SuppressLint;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.example.project_myfit.databinding.ItemSearchRecyclerViewBinding;

import org.jetbrains.annotations.NotNull;

import static com.example.project_myfit.util.MyFitConstant.DOWN;
import static com.example.project_myfit.util.MyFitConstant.STOP;
import static com.example.project_myfit.util.MyFitConstant.UP;

public class ViewPagerVH extends RecyclerView.ViewHolder {
    private final ItemSearchRecyclerViewBinding mBinding;

    @SuppressLint("ClickableViewAccessibility")
    public ViewPagerVH(@NotNull ItemSearchRecyclerViewBinding binding, ViewPagerAutoScrollListener listener) {
        super(binding.getRoot());
        this.mBinding = binding;

        mBinding.rvItemSearchRv.setOnTouchListener((v, event) -> {
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
            mBinding.tvItemSearchRvNoResultLayout.setVisibility(View.VISIBLE);
        else mBinding.tvItemSearchRvNoResultLayout.setVisibility(View.GONE);
    }

    public ItemSearchRecyclerViewBinding getBinding() {
        return mBinding;
    }
    public interface ViewPagerAutoScrollListener {
        void dragAutoScroll(int upDown);
    }
}
