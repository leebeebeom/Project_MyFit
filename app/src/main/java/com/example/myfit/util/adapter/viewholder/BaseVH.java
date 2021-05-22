package com.example.myfit.util.adapter.viewholder;

import android.annotation.SuppressLint;
import android.view.MotionEvent;
import android.view.View;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myfit.data.model.tuple.BaseTuple;
import com.google.android.material.checkbox.MaterialCheckBox;

public abstract class BaseVH<T extends BaseTuple> extends RecyclerView.ViewHolder {
    public static boolean IS_DRAGGING = false;
    private final BaseVHListener listener;
    private T tuple;

    @SuppressLint("ClickableViewAccessibility")
    public BaseVH(ViewDataBinding binding, BaseVHListener listener) {
        super(binding.getRoot());
        this.listener = listener;

        itemView.setOnClickListener(v -> {
            listener.itemViewClick(tuple, getCheckBox());
        });

        itemView.setOnLongClickListener(v -> {
            listener.itemViewLongClick(getLayoutPosition());
            return false;
        });

        getDragHandleIcon().setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN && !IS_DRAGGING)
                dragStart();
            return false;
        });
    }

    public void setTuple(T tuple) {
        this.tuple = tuple;
        bind(tuple);
    }

    protected abstract void bind(T tuple);

    protected BaseTuple getTuple() {
        return tuple;
    }

    protected abstract MaterialCheckBox getCheckBox();

    protected abstract AppCompatImageView getDragHandleIcon();

    private void dragStart() {
        IS_DRAGGING = true;
        listener.dragStart(this);
        setDraggingView();
    }

    protected abstract void setDraggingView();

    public void dragStop() {
        IS_DRAGGING = false;
        listener.dragStop();
        setDropView();
    }

    protected abstract void setDropView();

    protected void setItemViewTranslationZ(View itemView, int value) {
        itemView.setTranslationZ(value);
    }
}
