package com.example.myfit.util.adapter.viewholder;

import androidx.recyclerview.widget.RecyclerView;

import com.example.myfit.data.model.tuple.BaseTuple;

public interface BaseVHListener {
    void itemViewClick(BaseTuple tuple);

    void itemViewLongClick(int position, int parentIndex);

    void dragStart(RecyclerView.ViewHolder viewHolder, int parentIndex);

    void dragStop(int parentIndex);
}
