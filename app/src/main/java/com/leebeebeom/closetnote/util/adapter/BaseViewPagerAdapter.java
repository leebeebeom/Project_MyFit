package com.leebeebeom.closetnote.util.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.leebeebeom.closetnote.data.tuple.tuple.CategoryTuple;
import com.leebeebeom.closetnote.databinding.ItemRecyclerViewBinding;
import com.leebeebeom.closetnote.ui.main.main.adapter.CategoryAdapter;
import com.leebeebeom.closetnote.util.SizeLiveSet;
import com.leebeebeom.closetnote.util.adapter.dragcallback.DragCallBackList;
import com.leebeebeom.closetnote.util.adapter.viewholder.BaseVHListener;
import com.leebeebeom.closetnote.util.adapter.viewholder.ViewPagerVH;
import com.leebeebeom.closetnote.util.dragselect.DragSelect;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import lombok.Getter;
import lombok.experimental.Accessors;

@Accessors(prefix = "m")
public class BaseViewPagerAdapter<T extends BaseAdapter<?, ?, ?>> extends RecyclerView.Adapter<ViewPagerVH> {
    private final ViewPagerVH.AutoScrollListener mListener;
    @Getter
    private final List<T> mAdapters;
    @Getter
    private final List<DragSelect> mDragSelectListeners;
    @Getter
    private final List<ItemTouchHelper> mItemTouchHelpers;

    public BaseViewPagerAdapter(List<T> adapters,
                                List<DragSelect> dragSelectListeners,
                                Fragment fragment) {
        this.mAdapters = adapters;
        this.mDragSelectListeners = dragSelectListeners;
        this.mItemTouchHelpers = mAdapters.stream().map(adapter -> new ItemTouchHelper(new DragCallBackList(adapter))).collect(Collectors.toList());
        this.mListener = (ViewPagerVH.AutoScrollListener) fragment;

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
        if (holder.getAdapter() == null) {
            holder.setAdapter(mAdapters.get(position));
            holder.addItemTouchListener(mDragSelectListeners.get(position));
            mItemTouchHelpers.get(position).attachToRecyclerView(holder.getRecyclerView());
            if (getItemCount() == 0) holder.setNoData(true);
        }
    }

    @Override
    public int getItemCount() {
        return mAdapters.size();
    }

    public static class MainViewPagerAdapter extends BaseViewPagerAdapter<CategoryAdapter> {

        @Inject
        public MainViewPagerAdapter(SizeLiveSet<CategoryTuple> selectedCategoryTuples, Fragment fragment) {
            super(Arrays.asList(new CategoryAdapter(selectedCategoryTuples, (BaseVHListener) fragment),
                    new CategoryAdapter(selectedCategoryTuples, (BaseVHListener) fragment),
                    new CategoryAdapter(selectedCategoryTuples, (BaseVHListener) fragment),
                    new CategoryAdapter(selectedCategoryTuples, (BaseVHListener) fragment)),
                    Arrays.asList(new DragSelect(fragment), new DragSelect(fragment), new DragSelect(fragment), new DragSelect(fragment)),
                    fragment);
        }
    }
}
