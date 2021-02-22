package com.example.project_myfit.ui.main.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_myfit.databinding.ItemMainRecyclerViewBinding;
import com.michaelflisar.dragselectrecyclerview.DragSelectTouchListener;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MainViewPagerAdapter extends RecyclerView.Adapter<MainViewPagerAdapter.ViewPagerVH> {
    private final List<CategoryAdapter> mAdapterList;
    private MainDragAutoScrollListener mListener;
    private final DragSelectTouchListener mDragSelectListener;
    private final List<ItemTouchHelper> mTouchHelperList;

    public MainViewPagerAdapter(List<CategoryAdapter> adapterList, DragSelectTouchListener dragSelectListener, List<ItemTouchHelper> touchHelperList) {
        this.mAdapterList = adapterList;
        this.mDragSelectListener = dragSelectListener;
        this.mTouchHelperList = touchHelperList;
        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setOnCategoryAdapterListener(MainDragAutoScrollListener listener) {
        this.mListener = listener;
    }

    @NonNull
    @NotNull
    @Override
    public ViewPagerVH onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        ItemMainRecyclerViewBinding binding = ItemMainRecyclerViewBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewPagerVH(binding, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewPagerVH holder, int position) {
        //top
        if (holder.getLayoutPosition() == 0) {
            if (holder.mBinding.mainRecyclerView.getAdapter() == null) {
                holder.mBinding.mainRecyclerView.setAdapter(mAdapterList.get(0));
                mTouchHelperList.get(0).attachToRecyclerView(holder.mBinding.mainRecyclerView);
                holder.mBinding.mainRecyclerView.addOnItemTouchListener(mDragSelectListener);
            }
            if (mAdapterList.get(0).getItemCount() == 0)
                holder.mBinding.noData.setVisibility(View.VISIBLE);
            else holder.mBinding.noData.setVisibility(View.GONE);
        }
        //bottom
        else if (holder.getLayoutPosition() == 1) {
            if (holder.mBinding.mainRecyclerView.getAdapter() == null) {
                holder.mBinding.mainRecyclerView.setAdapter(mAdapterList.get(1));
                mTouchHelperList.get(1).attachToRecyclerView(holder.mBinding.mainRecyclerView);
                holder.mBinding.mainRecyclerView.addOnItemTouchListener(mDragSelectListener);
            }
            if (mAdapterList.get(1).getItemCount() == 0)
                holder.mBinding.noData.setVisibility(View.VISIBLE);
            else holder.mBinding.noData.setVisibility(View.GONE);
        }
        //outer
        else if (holder.getLayoutPosition() == 2) {
            if (holder.mBinding.mainRecyclerView.getAdapter() == null) {
                holder.mBinding.mainRecyclerView.setAdapter(mAdapterList.get(2));
                mTouchHelperList.get(2).attachToRecyclerView(holder.mBinding.mainRecyclerView);
                holder.mBinding.mainRecyclerView.addOnItemTouchListener(mDragSelectListener);
            }
            if (mAdapterList.get(2).getItemCount() == 0)
                holder.mBinding.noData.setVisibility(View.VISIBLE);
            else holder.mBinding.noData.setVisibility(View.GONE);
        } else if (holder.getLayoutPosition() == 3) {
            if (holder.mBinding.mainRecyclerView.getAdapter() == null) {
                holder.mBinding.mainRecyclerView.setAdapter(mAdapterList.get(3));
                mTouchHelperList.get(3).attachToRecyclerView(holder.mBinding.mainRecyclerView);
                holder.mBinding.mainRecyclerView.addOnItemTouchListener(mDragSelectListener);
            }
            if (mAdapterList.get(3).getItemCount() == 0)
                holder.mBinding.noData.setVisibility(View.VISIBLE);
            else holder.mBinding.noData.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }

    public void setSort(int sort) {
        mAdapterList.get(0).setSort(sort);
        mAdapterList.get(1).setSort(sort);
        mAdapterList.get(2).setSort(sort);
        mAdapterList.get(3).setSort(sort);
    }

    public static class ViewPagerVH extends RecyclerView.ViewHolder {
        private final ItemMainRecyclerViewBinding mBinding;

        @SuppressLint("ClickableViewAccessibility")
        public ViewPagerVH(@NotNull ItemMainRecyclerViewBinding binding, MainDragAutoScrollListener listener2) {
            super(binding.getRoot());
            this.mBinding = binding;

            mBinding.mainRecyclerView.setOnTouchListener((v, event) -> {
                if (event.getRawY() > 2000)
                    listener2.dragAutoScroll(0);
                else if (event.getRawY() < 250)
                    listener2.dragAutoScroll(1);
                else if (event.getRawY() < 2000 && event.getRawY() > 250)
                    listener2.dragAutoScroll(2);
                return false;
            });
        }
    }

    public interface MainDragAutoScrollListener {
        void dragAutoScroll(int upDown);
    }
}
