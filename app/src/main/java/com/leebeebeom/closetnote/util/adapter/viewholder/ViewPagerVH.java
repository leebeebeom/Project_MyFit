package com.leebeebeom.closetnote.util.adapter.viewholder;

import android.annotation.SuppressLint;
import android.view.MotionEvent;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.leebeebeom.closetnote.databinding.ItemRecyclerViewBinding;
import com.leebeebeom.closetnote.util.adapter.BaseAdapter;
import com.leebeebeom.closetnote.util.constant.AutoScrollFlag;
import com.leebeebeom.closetnote.util.dragselect.DragSelect;

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
        mBinding.setViewPagerVH(this);
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
        mBinding.rv.addOnItemTouchListener(dragSelectListener);
    }

    public RecyclerView getRecyclerView() {
        return mBinding.rv;
    }

    public boolean recyclerViewTouch(MotionEvent event) {
        if (event.getRawY() < 250)
            mListener.dragAutoScroll(AutoScrollFlag.UP);
        else if (event.getRawY() > 2000)
            mListener.dragAutoScroll(AutoScrollFlag.DOWN);
        else if (event.getRawY() < 2000 && event.getRawY() > 250)
            mListener.dragAutoScroll(AutoScrollFlag.STOP);
        return false;
    }

    public interface AutoScrollListener {
        void dragAutoScroll(AutoScrollFlag flag);
    }
}
