package com.example.project_myfit.ui.main.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_myfit.ui.main.listfragment.adapter.ListFolderAdapter;

public class DragCallBack extends ItemTouchHelper.Callback {
    private final CategoryAdapter mCategoryAdapter;

    public DragCallBack(CategoryAdapter categoryAdapter) {
        mCategoryAdapter = categoryAdapter;
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        return makeMovementFlags(ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0);
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder
            viewHolder, @NonNull RecyclerView.ViewHolder target) {
        mCategoryAdapter.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

    }

    @Override
    public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder
            viewHolder) {
        mCategoryAdapter.onItemDrop();
    }

    public interface DragFolderListener {
        void onItemMove(int from, int to);

        void onItemDrop();
    }

    public interface StartDragListener {
        void startDrag(RecyclerView.ViewHolder viewHolder);
    }
}