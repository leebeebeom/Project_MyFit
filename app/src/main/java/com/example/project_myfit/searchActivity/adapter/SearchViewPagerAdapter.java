package com.example.project_myfit.searchActivity.adapter;

import android.annotation.SuppressLint;
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
    private final SearchDragAutoScrollListener mListener;

    public SearchViewPagerAdapter(List<SearchAdapter> searchAdapterList, DragSelectTouchListener dragSelectListener, SearchDragAutoScrollListener listener) {
        this.mSearchAdapterList = searchAdapterList;
        this.mDragSelectListener = dragSelectListener;
        this.mListener = listener;
        setHasStableIds(true);
    }

    @NonNull
    @NotNull
    @Override
    public SearchViewPagerVH onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        ItemSearchRecyclerViewBinding binding = ItemSearchRecyclerViewBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new SearchViewPagerVH(binding, mListener);
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

        @SuppressLint("ClickableViewAccessibility")
        public SearchViewPagerVH(@NotNull ItemSearchRecyclerViewBinding binding, SearchDragAutoScrollListener listener) {
            super(binding.getRoot());
            this.mBinding = binding;
            mBinding.getRoot().setBackgroundColor(Color.TRANSPARENT);

            mBinding.searchRecyclerView.setOnTouchListener((v, event) -> {
                if (event.getRawY() > 2000)
                    listener.dragAutoScroll(0);
                else if (event.getRawY() < 250)
                    listener.dragAutoScroll(1);
                else if (event.getRawY() < 2000 && event.getRawY() > 250)
                    listener.dragAutoScroll(2);
                return false;
            });
        }

        public void setNoResult(boolean isEmpty) {
            if (isEmpty)
                mBinding.noData.setVisibility(View.VISIBLE);
            else mBinding.noData.setVisibility(View.GONE);

        }
    }

    public interface SearchDragAutoScrollListener {
        void dragAutoScroll(int upDown);
    }
}
