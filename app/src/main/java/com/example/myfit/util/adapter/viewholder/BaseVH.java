package com.example.myfit.util.adapter.viewholder;

import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myfit.data.model.tuple.BaseTuple;
import com.google.android.material.checkbox.MaterialCheckBox;

public abstract class BaseVH<T extends BaseTuple, R extends ViewDataBinding> extends RecyclerView.ViewHolder {
    private T tuple;

    public BaseVH(R binding, BaseVHListener listener) {
        super(binding.getRoot());

        itemView.setOnClickListener(v -> listener.itemViewClick(tuple, getCheckBox()));

        itemView.setOnLongClickListener(v -> {
            listener.itemViewLongClick(getLayoutPosition());
            return false;
        });
    }

    public void setTuple(T tuple) {
        this.tuple = tuple;
        bind(tuple);
    }

    public abstract void bind(T tuple);

    protected abstract MaterialCheckBox getCheckBox();

    protected BaseTuple getTuple(){
        return tuple;
    }
}
