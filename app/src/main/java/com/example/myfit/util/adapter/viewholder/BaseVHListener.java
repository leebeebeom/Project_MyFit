package com.example.myfit.util.adapter.viewholder;

import androidx.recyclerview.widget.RecyclerView;

import com.example.myfit.data.tuple.BaseTuple;

public interface BaseVHListener {
    void itemViewClick(BaseTuple tuple);

    void itemViewLongClick(int position, BaseTuple tuple);

    void dragStart(RecyclerView.ViewHolder viewHolder, BaseTuple tuple);

    void dragStop(BaseTuple tuple);
}
