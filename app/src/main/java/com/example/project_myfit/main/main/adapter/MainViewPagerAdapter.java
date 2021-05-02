package com.example.project_myfit.main.main.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_myfit.databinding.ItemRecyclerViewBinding;
import com.example.project_myfit.util.DragSelectImpl;
import com.example.project_myfit.util.adapter.viewholder.ViewPagerVH;

import org.jetbrains.annotations.NotNull;

public class MainViewPagerAdapter extends RecyclerView.Adapter<ViewPagerVH> {
    private final CategoryAdapter[] mCategoryAdapterArray;
    private final ViewPagerVH.ViewPagerAutoScrollListener mListener;
    private final DragSelectImpl[] mDragSelectListenerArray;
    private final ItemTouchHelper[] mTouchHelperArray;

    public MainViewPagerAdapter(CategoryAdapter[] adapterList, DragSelectImpl[] dragSelectListenerArray,
                                ItemTouchHelper[] touchHelperList, ViewPagerVH.ViewPagerAutoScrollListener listener) {
        this.mCategoryAdapterArray = adapterList;
        this.mDragSelectListenerArray = dragSelectListenerArray;
        this.mTouchHelperArray = touchHelperList;
        this.mListener = listener;
        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @NotNull
    @Override
    public ViewPagerVH onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        ItemRecyclerViewBinding binding = ItemRecyclerViewBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewPagerVH(binding, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewPagerVH holder, int position) {
        if (holder.getBinding().rv.getAdapter() == null) {
            mCategoryAdapterArray[position].setViewPagerVH(holder);
            holder.getBinding().rv.setAdapter(mCategoryAdapterArray[position]);

            mTouchHelperArray[position].attachToRecyclerView(holder.getBinding().rv);
            holder.getBinding().rv.addOnItemTouchListener(mDragSelectListenerArray[position].setRecyclerView(holder.getBinding().rv));
            holder.setNoData(mCategoryAdapterArray[position].getCurrentList().isEmpty());
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}
