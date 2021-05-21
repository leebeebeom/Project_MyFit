package com.example.myfit.util.adapter.viewholder;

import androidx.recyclerview.widget.RecyclerView;

import com.example.myfit.data.model.tuple.BaseTuple;
import com.google.android.material.checkbox.MaterialCheckBox;

public abstract class BaseVHListener {
    public abstract void itemViewClick(BaseTuple tuple, MaterialCheckBox checkBox);

    public abstract void itemViewLongClick(int position);

    public abstract void dragStart(RecyclerView.ViewHolder viewHolder);

    public abstract void dragStop();
}
