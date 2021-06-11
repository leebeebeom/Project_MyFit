package com.example.myfit.util.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myfit.data.tuple.tuple.CategoryTuple;
import com.example.myfit.databinding.ItemRecyclerViewBinding;
import com.example.myfit.ui.main.main.adapter.CategoryAdapter;
import com.example.myfit.util.SizeLiveSet;
import com.example.myfit.util.adapter.dragcallback.DragCallBackList;
import com.example.myfit.util.adapter.viewholder.ViewPagerVH;
import com.example.myfit.util.dragselect.DragSelect;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Accessors(prefix = "m")
public class BaseViewPagerAdapter<T extends BaseAdapter<?, ?, ?>> extends RecyclerView.Adapter<ViewPagerVH> {
    @Setter
    private ViewPagerVH.AutoScrollListener mListener;
    @Getter
    private final T[] mAdapters;
    @Getter
    private final DragSelect[] mDragSelectListeners;
    @Getter
    private ItemTouchHelper[] mItemTouchHelpers;

    public BaseViewPagerAdapter(T[] adapters,
                                DragSelect[] dragSelectListeners,
                                ItemTouchHelper[] itemTouchHelpers) {
        this.mAdapters = adapters;
        this.mDragSelectListeners = dragSelectListeners;
        this.mItemTouchHelpers = new ItemTouchHelper[adapters.length];

        int count = mAdapters.length;
        for (int i = 0; i < count; i++)
            mItemTouchHelpers[i] = new ItemTouchHelper(new DragCallBackList(adapters[i]));

        setHasStableIds(true);
    }

    public BaseViewPagerAdapter(T[] adapters,
                                DragSelect[] dragSelectListeners) {
        this.mAdapters = adapters;
        this.mDragSelectListeners = dragSelectListeners;
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
            holder.setAdapter(mAdapters[position]);
            holder.addItemTouchListener(mDragSelectListeners[position].setRecyclerView(holder.getRecyclerView()));
            if (mItemTouchHelpers != null)
                mItemTouchHelpers[position].attachToRecyclerView(holder.getRecyclerView());
        }
    }

    @Override
    public int getItemCount() {
        return mAdapters.length;
    }

    public static class MainViewPagerAdapter extends BaseViewPagerAdapter<CategoryAdapter> {

        @Inject
        public MainViewPagerAdapter(SizeLiveSet<CategoryTuple> selectedCategoryTuples) {
            super(new CategoryAdapter[]{new CategoryAdapter(selectedCategoryTuples),
                            new CategoryAdapter(selectedCategoryTuples),
                            new CategoryAdapter(selectedCategoryTuples),
                            new CategoryAdapter(selectedCategoryTuples)},
                    new DragSelect[]{new DragSelect(), new DragSelect(), new DragSelect(), new DragSelect()},
                    new ItemTouchHelper[0]);
        }
    }
}
