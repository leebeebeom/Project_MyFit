package com.example.project_myfit.searchActivity.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_myfit.databinding.ItemSearchRecyclerViewBinding;
import com.michaelflisar.dragselectrecyclerview.DragSelectTouchListener;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SearchViewPagerAdapter extends RecyclerView.Adapter<SearchViewPagerAdapter.SearchViewPagerVH> {
    private final List<SearchAdapter> mSearchAdapterList;
    private final DragSelectTouchListener mDragSelectListener;

    public SearchViewPagerAdapter(List<SearchAdapter> searchAdapterList, DragSelectTouchListener dragSelectListener) {
        this.mSearchAdapterList = searchAdapterList;
        this.mDragSelectListener = dragSelectListener;
        setHasStableIds(true);
    }

    @NonNull
    @NotNull
    @Override
    public SearchViewPagerVH onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        ItemSearchRecyclerViewBinding binding = ItemSearchRecyclerViewBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new SearchViewPagerVH(binding);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return 4;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull SearchViewPagerVH holder, int position) {
        if (holder.mBinding.searchRecyclerView.getAdapter() == null) {
            mSearchAdapterList.get(position).setSearchViewPagerVH(holder);
            holder.mBinding.searchRecyclerView.setAdapter(mSearchAdapterList.get(position));
            holder.mBinding.searchRecyclerView.addOnItemTouchListener(mDragSelectListener);
            holder.setNoResult(mSearchAdapterList.get(position).getCurrentList().isEmpty());

        }
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
        private final ItemSearchRecyclerViewBinding mBinding;

        public SearchViewPagerVH(@NotNull ItemSearchRecyclerViewBinding binding) {
            super(binding.getRoot());
            this.mBinding = binding;
            mBinding.getRoot().setBackgroundColor(Color.TRANSPARENT);
        }

        public void setNoResult(boolean isEmpty) {
            if (isEmpty)
                mBinding.noData.setVisibility(View.VISIBLE);
            else mBinding.noData.setVisibility(View.GONE);

        }
    }
}
