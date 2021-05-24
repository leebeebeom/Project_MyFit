package com.example.myfit.util.adapter.viewholder;

import android.annotation.SuppressLint;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.example.myfit.databinding.ItemRecyclerViewBinding;
import com.example.myfit.util.DragSelectImpl;
import com.example.myfit.util.adapter.BaseAdapter;
import com.example.myfit.util.constant.AutoScrollFlag;

import org.jetbrains.annotations.NotNull;

public class ViewPagerVH extends RecyclerView.ViewHolder {
    private final ItemRecyclerViewBinding binding;

    @SuppressLint("ClickableViewAccessibility")
    public ViewPagerVH(@NotNull ItemRecyclerViewBinding binding, ViewPagerAutoScrollListener listener) {
        super(binding.getRoot());
        this.binding = binding;

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
        binding.layoutNoResult.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
    }

    public void setNoData(boolean isEmpty) {
        binding.layoutNoData.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
    }

    public RecyclerView.Adapter<?> getAdapter() {
        return binding.rv.getAdapter();
    }

    public void setAdapter(BaseAdapter<?, ?, ?> adapter) {
        binding.rv.setAdapter(adapter);
    }

    public void addItemTouchListener(DragSelectImpl dragSelectListener) {
        binding.rv.addOnItemTouchListener(dragSelectListener.setRecyclerView(binding.rv));
    }

    public RecyclerView getRecyclerView() {
        return binding.rv;
    }

    public interface ViewPagerAutoScrollListener {
        void dragAutoScroll(AutoScrollFlag flag);
    }
}
