package com.example.project_myfit.searchActivity;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_myfit.data.model.Folder;
import com.example.project_myfit.data.model.Size;
import com.example.project_myfit.databinding.ItemSearchRecyclerViewBinding;
import com.example.project_myfit.searchActivity.adapter.SearchAdapter;
import com.google.android.material.tabs.TabLayout;
import com.michaelflisar.dragselectrecyclerview.DragSelectTouchListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.example.project_myfit.util.MyFitConstant.BOTTOM;
import static com.example.project_myfit.util.MyFitConstant.OUTER;
import static com.example.project_myfit.util.MyFitConstant.TOP;

public class SearchViewPagerAdapter extends RecyclerView.Adapter<SearchViewPagerAdapter.SearchViewPagerVH> {
    private final SearchAdapter[] mSearchAdapterArray;
    private final DragSelectTouchListener mDragSelectListener;
    private final SearchDragAutoScrollListener mListener;

    public SearchViewPagerAdapter(SearchAdapter[] searchAdapterArray, DragSelectTouchListener dragSelectListener, SearchDragAutoScrollListener listener) {
        this.mSearchAdapterArray = searchAdapterArray;
        this.mDragSelectListener = dragSelectListener;
        this.mListener = listener;
        setHasStableIds(true);
    }

    public void setItems(@NotNull List<Folder> allFolderList, List<Size> allSizeList, CharSequence word, TabLayout tabLayout, TypedValue colorControl) {
        List<Object> topList = new ArrayList<>();
        List<Object> bottomList = new ArrayList<>();
        List<Object> outerList = new ArrayList<>();
        List<Object> etcList = new ArrayList<>();
        for (Folder folder : allFolderList)
            switch (folder.getParentCategory()) {
                case TOP:
                    topList.add(folder);
                    break;
                case BOTTOM:
                    bottomList.add(folder);
                    break;
                case OUTER:
                    outerList.add(folder);
                    break;
                default:
                    etcList.add(folder);
                    break;
            }
        for (Size size : allSizeList)
            switch (size.getParentCategory()) {
                case TOP:
                    topList.add(size);
                    break;
                case BOTTOM:
                    bottomList.add(size);
                    break;
                case OUTER:
                    outerList.add(size);
                    break;
                default:
                    etcList.add(size);
                    break;
            }

        mSearchAdapterArray[0].setItem(topList, word, tabLayout.getTabAt(0), colorControl);
        mSearchAdapterArray[1].setItem(bottomList, word, tabLayout.getTabAt(1), colorControl);
        mSearchAdapterArray[2].setItem(outerList, word, tabLayout.getTabAt(2), colorControl);
        mSearchAdapterArray[3].setItem(etcList, word, tabLayout.getTabAt(3), colorControl);
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
            mSearchAdapterArray[position].setSearchViewPagerVH(holder);
            holder.mBinding.searchRecyclerView.setAdapter(mSearchAdapterArray[position]);
            holder.mBinding.searchRecyclerView.addOnItemTouchListener(mDragSelectListener);
            holder.setNoResult(mSearchAdapterArray[position].getCurrentList().isEmpty());
        }
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

        public ItemSearchRecyclerViewBinding getBinding() {
            return mBinding;
        }
    }

    public interface SearchDragAutoScrollListener {
        void dragAutoScroll(int upDown);
    }
}
