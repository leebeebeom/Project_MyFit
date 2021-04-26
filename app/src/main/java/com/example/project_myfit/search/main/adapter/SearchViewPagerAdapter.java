package com.example.project_myfit.search.main.adapter;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_myfit.data.model.Folder;
import com.example.project_myfit.data.model.Size;
import com.example.project_myfit.databinding.ItemRecyclerViewBinding;
import com.example.project_myfit.util.adapter.view_holder.ViewPagerVH;
import com.google.android.material.tabs.TabLayout;
import com.michaelflisar.dragselectrecyclerview.DragSelectTouchListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.example.project_myfit.util.MyFitConstant.BOTTOM;
import static com.example.project_myfit.util.MyFitConstant.ETC;
import static com.example.project_myfit.util.MyFitConstant.OUTER;
import static com.example.project_myfit.util.MyFitConstant.TOP;

public class SearchViewPagerAdapter extends RecyclerView.Adapter<ViewPagerVH> {
    private final SearchAdapter[] mSearchAdapterArray;
    private final DragSelectTouchListener mDragSelectListener;
    private final ViewPagerVH.ViewPagerAutoScrollListener mListener;

    public SearchViewPagerAdapter(SearchAdapter[] searchAdapterArray, DragSelectTouchListener dragSelectListener,
                                  ViewPagerVH.ViewPagerAutoScrollListener listener) {
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
    public ViewPagerVH onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        ItemRecyclerViewBinding binding = ItemRecyclerViewBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewPagerVH(binding, mListener);
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
    public void onBindViewHolder(@NonNull @NotNull ViewPagerVH holder, int position) {
        if (holder.getBinding().rvItemRv.getAdapter() == null) {
            mSearchAdapterArray[position].setSearchViewPagerVH(holder);
            holder.getBinding().rvItemRv.setAdapter(mSearchAdapterArray[position]);
            holder.getBinding().rvItemRv.addOnItemTouchListener(mDragSelectListener);
            holder.setNoResult(mSearchAdapterArray[position].getCurrentList().isEmpty());
        }
    }
}
