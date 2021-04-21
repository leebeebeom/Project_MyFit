package com.example.project_myfit.searchActivity.adapter;

import android.annotation.SuppressLint;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_myfit.data.model.Folder;
import com.example.project_myfit.data.model.Size;
import com.example.project_myfit.databinding.ItemSearchRecyclerViewBinding;
import com.google.android.material.tabs.TabLayout;
import com.michaelflisar.dragselectrecyclerview.DragSelectTouchListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.example.project_myfit.util.MyFitConstant.BOTTOM;
import static com.example.project_myfit.util.MyFitConstant.DOWN;
import static com.example.project_myfit.util.MyFitConstant.ETC;
import static com.example.project_myfit.util.MyFitConstant.OUTER;
import static com.example.project_myfit.util.MyFitConstant.STOP;
import static com.example.project_myfit.util.MyFitConstant.TOP;
import static com.example.project_myfit.util.MyFitConstant.UP;

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

    public void setItems(@NotNull List<Object> allItemList, CharSequence word, TabLayout tabLayout, TypedValue colorControl) {
        List<Object> topList = new ArrayList<>();
        List<Object> bottomList = new ArrayList<>();
        List<Object> outerList = new ArrayList<>();
        List<Object> etcList = new ArrayList<>();

        for (Object o : allItemList) {
            if (o instanceof Folder) {
                switch (((Folder) o).getParentCategory()) {
                    case TOP:
                        topList.add(o);
                        break;
                    case BOTTOM:
                        bottomList.add(o);
                        break;
                    case OUTER:
                        outerList.add(o);
                        break;
                    case ETC:
                        etcList.add(o);
                        break;
                }
            }
        }
        for (Object o : allItemList) {
            if (o instanceof Size) {
                switch (((Size) o).getParentCategory()) {
                    case TOP:
                        topList.add(o);
                        break;
                    case BOTTOM:
                        bottomList.add(o);
                        break;
                    case OUTER:
                        outerList.add(o);
                        break;
                    case ETC:
                        etcList.add(o);
                        break;
                }
            }
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

            mBinding.searchRecyclerView.setOnTouchListener((v, event) -> {
                if (event.getRawY() > 2000)
                    listener.dragAutoScroll(DOWN);
                else if (event.getRawY() < 250)
                    listener.dragAutoScroll(UP);
                else if (event.getRawY() < 2000 && event.getRawY() > 250)
                    listener.dragAutoScroll(STOP);
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
