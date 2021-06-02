package com.example.myfit.util.adapter.viewholder;

import android.annotation.SuppressLint;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.example.myfit.databinding.ItemRecyclerViewBinding;
import com.example.myfit.util.adapter.BaseAdapter;
import com.example.myfit.util.constant.AutoScrollFlag;
import com.example.myfit.util.dragselect.DragSelect;

import org.jetbrains.annotations.NotNull;

import lombok.Getter;
import lombok.experimental.Accessors;

@Accessors(prefix = "m")
public class ViewPagerVH extends RecyclerView.ViewHolder {
    private final ItemRecyclerViewBinding mBinding;
    @Getter
    private final AutoScrollListener mListener;

    @SuppressLint("ClickableViewAccessibility")
    public ViewPagerVH(@NotNull ItemRecyclerViewBinding binding, AutoScrollListener listener) {
        super(binding.getRoot());
        this.mBinding = binding;
        this.mListener = listener;
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
