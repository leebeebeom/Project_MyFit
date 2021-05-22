package com.example.myfit.util.adapter.viewholder.size;

import androidx.databinding.ViewDataBinding;

import com.example.myfit.data.model.size.SizeTuple;
import com.example.myfit.util.adapter.viewholder.BaseVH;
import com.example.myfit.util.adapter.viewholder.SizeVHListener;

public abstract class BaseSizeVH extends BaseVH<SizeTuple> {
    private final SizeVHListener listener;

    public BaseSizeVH(ViewDataBinding binding, SizeVHListener listener) {
        super(binding, listener);
        this.listener = listener;
    }

    protected SizeVHListener getListener() {
        return listener;
    }
}
