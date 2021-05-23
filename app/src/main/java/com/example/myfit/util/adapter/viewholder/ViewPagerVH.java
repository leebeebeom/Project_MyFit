package com.example.myfit.util.adapter.viewholder;

import android.annotation.SuppressLint;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.example.myfit.databinding.ItemRecyclerViewBinding;

import org.jetbrains.annotations.NotNull;

import static com.example.myfit.util.MyFitConstant.DOWN;
import static com.example.myfit.util.MyFitConstant.STOP;
import static com.example.myfit.util.MyFitConstant.UP;

public class ViewPagerVH extends RecyclerView.ViewHolder {
    private final ItemRecyclerViewBinding binding;

    @SuppressLint("ClickableViewAccessibility")
    public ViewPagerVH(@NotNull ItemRecyclerViewBinding binding, ViewPagerAutoScrollListener listener) {
        super(binding.getRoot());
        this.binding = binding;

        binding.rv.setOnTouchListener((v, event) -> {
            if (event.getRawY() < 250)
                listener.dragAutoScroll(UP);
            else if (event.getRawY() > 2000)
                listener.dragAutoScroll(DOWN);
            else if (event.getRawY() < 2000 && event.getRawY() > 250)
                listener.dragAutoScroll(STOP);
            return false;
        });
    }

    public void setNoResult(boolean isEmpty) {
        binding.layoutNoResult.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
    }

    public void setNoData(boolean isEmpty) {
        binding.layoutNoData.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
    }

    public ItemRecyclerViewBinding getBinding() {
        return binding;
    }

    public interface ViewPagerAutoScrollListener {
        void dragAutoScroll(int upDownStop);
    }
}