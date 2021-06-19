package com.leebeebeom.closetnote.util.adapter.viewholder;

import androidx.recyclerview.widget.RecyclerView;

import com.leebeebeom.closetnote.data.tuple.BaseTuple;
import com.leebeebeom.closetnote.data.tuple.tuple.SizeTuple;

public interface BaseVHListener {
    void itemViewClick(BaseTuple tuple);

    void itemViewLongClick(int position, BaseTuple tuple);

    void dragStart(RecyclerView.ViewHolder viewHolder, BaseTuple tuple);

    void dragStop(BaseTuple tuple);

    interface SizeVHListener extends BaseVHListener {
        void favoriteClick(SizeTuple tuple);
    }
}
