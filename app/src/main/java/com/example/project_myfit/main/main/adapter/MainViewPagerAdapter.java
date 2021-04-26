package com.example.project_myfit.main.main.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_myfit.data.model.Category;
import com.example.project_myfit.databinding.ItemRecyclerViewBinding;
import com.example.project_myfit.main.main.MainViewModel;
import com.example.project_myfit.util.Sort;
import com.example.project_myfit.util.adapter.view_holder.ViewPagerVH;
import com.michaelflisar.dragselectrecyclerview.DragSelectTouchListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.example.project_myfit.util.MyFitConstant.BOTTOM;
import static com.example.project_myfit.util.MyFitConstant.ETC;
import static com.example.project_myfit.util.MyFitConstant.OUTER;
import static com.example.project_myfit.util.MyFitConstant.TOP;

public class MainViewPagerAdapter extends RecyclerView.Adapter<ViewPagerVH> {
    private final CategoryAdapter[] mCategoryAdapterArray;
    private final ViewPagerVH.ViewPagerAutoScrollListener mListener;
    private final DragSelectTouchListener mDragSelectListener;
    private final ItemTouchHelper[] mTouchHelperArray;

    public MainViewPagerAdapter(CategoryAdapter[] adapterList, DragSelectTouchListener dragSelectListener,
                                ItemTouchHelper[] touchHelperList, ViewPagerVH.ViewPagerAutoScrollListener listener) {
        this.mCategoryAdapterArray = adapterList;
        this.mDragSelectListener = dragSelectListener;
        this.mTouchHelperArray = touchHelperList;
        this.mListener = listener;
        setHasStableIds(true);
    }

    public void setItem(int sort, List<Category> categoryList, MainViewModel model) {
        List<Category> topList = new ArrayList<>();
        List<Category> bottomList = new ArrayList<>();
        List<Category> outerList = new ArrayList<>();
        List<Category> etcList = new ArrayList<>();
        for (Category category : Sort.categorySort(sort, categoryList)) {
            switch (category.getParentCategory()) {
                case TOP:
                    topList.add(category);
                    break;
                case BOTTOM:
                    bottomList.add(category);
                    break;
                case OUTER:
                    outerList.add(category);
                    break;
                case ETC:
                    etcList.add(category);
                    break;
            }
        }

        mCategoryAdapterArray[0].submitList(sort, topList, model.getFolderParentIdList(TOP), model.getSizeParentIdList(TOP));
        mCategoryAdapterArray[1].submitList(sort, bottomList, model.getFolderParentIdList(BOTTOM), model.getSizeParentIdList(BOTTOM));
        mCategoryAdapterArray[2].submitList(sort, outerList, model.getFolderParentIdList(OUTER), model.getSizeParentIdList(OUTER));
        mCategoryAdapterArray[3].submitList(sort, etcList, model.getFolderParentIdList(ETC), model.getSizeParentIdList(ETC));
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
        if (holder.getBinding().rvItemRv.getAdapter() == null) {
            mCategoryAdapterArray[position].setViewPagerVH(holder);

            holder.getBinding().rvItemRv.setAdapter(mCategoryAdapterArray[position]);

            mTouchHelperArray[position].attachToRecyclerView(holder.getBinding().rvItemRv);
            holder.getBinding().rvItemRv.addOnItemTouchListener(mDragSelectListener);

            holder.getBinding().tvItemRvNoDataLayout.setVisibility(mCategoryAdapterArray[position].getItemCount() == 0 ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}
