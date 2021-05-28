package com.example.myfit.util.adapter.viewholder;

import android.annotation.SuppressLint;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.example.myfit.databinding.ItemRecyclerViewBinding;
import com.example.myfit.util.dragselect.DragSelect;
import com.example.myfit.util.adapter.BaseAdapter;
import com.example.myfit.util.constant.AutoScrollFlag;

import org.jetbrains.annotations.NotNull;

public class ViewPagerVH extends RecyclerView.ViewHolder {
    private final ItemRecyclerViewBinding mBinding;

    @SuppressLint("ClickableViewAccessibility")
    public ViewPagerVH(@NotNull ItemRecyclerViewBinding binding, AutoScrollListener listener) {
        super(binding.getRoot());
        this.mBinding = binding;

        binding.rv.setOnTouchListener((v, event) -> {
            if (event.getRawY() < 250)
                listener.dragAutoScroll(AutoScrollFlag.UP);
            else if (event.getRawY() > 2000)
                listener.dragAutoScroll(AutoScrollFlag.DOWN);
            else if (event.getRawY() < 2000 && event.getRawY() > 250)
                listener.dragAutoScroll(AutoScrollFlag.STOP);
            return false;
        });
    }

    public void setNoResult(boolean isEmpty) {
        mBinding.layoutNoResult.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
    }

    public void setNoData(boolean isEmpty) {
        mBinding.layoutNoData.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
    }

    public RecyclerView.Adapter<?> getAdapter() {
        return mBinding.rv.getAdapter();
    }

    public void setAdapter(BaseAdapter<?, ?, ?> adapter) {
        mBinding.rv.setAdapter(adapter);
    }

    public void addItemTouchListener(DragSelect dragSelectListener) {
        mBinding.rv.addOnItemTouchListener(dragSelectListener.setRecyclerView(mBinding.rv));
    }

    public RecyclerView getRecyclerView() {
        return mBinding.rv;
    }

    public interface AutoScrollListener {
        void dragAutoScroll(AutoScrollFlag flag);
    }
}
