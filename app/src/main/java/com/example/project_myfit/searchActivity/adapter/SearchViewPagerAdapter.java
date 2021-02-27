package com.example.project_myfit.searchActivity.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_myfit.databinding.ItemMainRecyclerViewBinding;
import com.michaelflisar.dragselectrecyclerview.DragSelectTouchListener;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SearchViewPagerAdapter extends RecyclerView.Adapter<SearchViewPagerAdapter.SearchViewPagerVH> {
    private final List<SearchAdapter> mSearchAdapterList;
    private final DragSelectTouchListener mDragSelectListener;

    public SearchViewPagerAdapter(List<SearchAdapter> searchAdapterList, DragSelectTouchListener dragSelectListener) {
        this.mSearchAdapterList = searchAdapterList;
        this.mDragSelectListener = dragSelectListener;
    }

    @NonNull
    @NotNull
    @Override
    public SearchViewPagerVH onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        ItemMainRecyclerViewBinding binding = ItemMainRecyclerViewBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new SearchViewPagerVH(binding);
    }

    @Override
    public int getItemCount() {
        return 4;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull SearchViewPagerVH holder, int position) {
        if (position == 0)
            holder.mBinding.mainRecyclerView.setAdapter(mSearchAdapterList.get(0));
        else if (position == 1)
            holder.mBinding.mainRecyclerView.setAdapter(mSearchAdapterList.get(1));
        else if (position == 2)
            holder.mBinding.mainRecyclerView.setAdapter(mSearchAdapterList.get(2));
        else if (position == 3)
            holder.mBinding.mainRecyclerView.setAdapter(mSearchAdapterList.get(3));
        holder.mBinding.mainRecyclerView.addOnItemTouchListener(mDragSelectListener);
    }

    public List<SearchAdapter> getSearchAdapterList() {
        return mSearchAdapterList;
    }

    public void setActionModeState(int actionModeState) {
        mSearchAdapterList.get(0).setActionModeState(actionModeState);
        mSearchAdapterList.get(1).setActionModeState(actionModeState);
        mSearchAdapterList.get(2).setActionModeState(actionModeState);
        mSearchAdapterList.get(3).setActionModeState(actionModeState);
    }

    public static class SearchViewPagerVH extends RecyclerView.ViewHolder {
        private final ItemMainRecyclerViewBinding mBinding;

        public SearchViewPagerVH(@NotNull ItemMainRecyclerViewBinding binding) {
            super(binding.getRoot());
            this.mBinding = binding;
            mBinding.getRoot().setBackgroundColor(Color.TRANSPARENT);
        }

        public ItemMainRecyclerViewBinding getBinding() {
            return mBinding;
        }
    }
}
