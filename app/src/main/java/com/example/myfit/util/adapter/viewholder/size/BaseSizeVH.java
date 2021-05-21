package com.example.myfit.util.adapter.viewholder.size;

import androidx.databinding.ViewDataBinding;

import com.example.myfit.data.model.size.SizeTuple;
import com.example.myfit.util.adapter.viewholder.BaseVH;
import com.example.myfit.util.adapter.viewholder.SizeVHListener;

public abstract class BaseSizeVH extends BaseVH<SizeTuple> {
    private SizeVHListener listener;

    public BaseSizeVH(ViewDataBinding binding) {
        super(binding);
    }

    public void setListener(SizeVHListener listener) {
        super.setListener(listener);
        this.listener = listener;
    }

    protected SizeVHListener getListener(){
        return listener;
    }
}
