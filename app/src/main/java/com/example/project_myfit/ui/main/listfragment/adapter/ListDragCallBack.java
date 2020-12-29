package com.example.project_myfit.ui.main.listfragment.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public class ListDragCallBack extends ItemTouchHelper.Callback {
    private final ListFolderAdapter mListFolderAdapter;

    public ListDragCallBack(ListFolderAdapter listFolderAdapter) {
        mListFolderAdapter = listFolderAdapter;
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        return makeMovementFlags(ItemTouchHelper.START | ItemTouchHelper.END, 0);
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return false;
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder
            viewHolder, @NonNull RecyclerView.ViewHolder target) {
        mListFolderAdapter.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

    }

    @Override
    public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder
            viewHolder) {
        mListFolderAdapter.onItemDrop();
    }

    public interface DragFolderListener {
        void onItemMove(int from, int to);

        void onItemDrop();
    }

    public interface StartDragListener {
        void startDrag(RecyclerView.ViewHolder viewHolder);
    }
}
