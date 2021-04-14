package com.example.project_myfit.fragment.main.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_myfit.data.model.Category;
import com.example.project_myfit.databinding.ItemMainRecyclerViewBinding;
import com.example.project_myfit.fragment.main.MainViewModel;
import com.example.project_myfit.util.Sort;
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

public class MainViewPagerAdapter extends RecyclerView.Adapter<MainViewPagerAdapter.ViewPagerVH> {
    private final CategoryAdapter[] mCategoryAdapterArray;
    private final MainDragAutoScrollListener mListener;
    private final DragSelectTouchListener mDragSelectListener;
    private final ItemTouchHelper[] mTouchHelperArray;

    public MainViewPagerAdapter(CategoryAdapter[] adapterList, DragSelectTouchListener dragSelectListener,
                                ItemTouchHelper[] touchHelperList, MainDragAutoScrollListener listener) {
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
        ItemMainRecyclerViewBinding binding = ItemMainRecyclerViewBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewPagerVH(binding, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewPagerVH holder, int position) {
        if (holder.mBinding.mainRecyclerView.getAdapter() == null) {
            mCategoryAdapterArray[position].setViewPagerVH(holder);

            holder.mBinding.mainRecyclerView.setAdapter(mCategoryAdapterArray[position]);

            mTouchHelperArray[position].attachToRecyclerView(holder.mBinding.mainRecyclerView);
            holder.mBinding.mainRecyclerView.addOnItemTouchListener(mDragSelectListener);

            holder.mBinding.noData.setVisibility(mCategoryAdapterArray[position].getItemCount() == 0 ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }

    public static class ViewPagerVH extends RecyclerView.ViewHolder {
        private final ItemMainRecyclerViewBinding mBinding;

        @SuppressLint("ClickableViewAccessibility")
        public ViewPagerVH(@NotNull ItemMainRecyclerViewBinding binding, MainDragAutoScrollListener listener) {
            super(binding.getRoot());
            this.mBinding = binding;

            mBinding.mainRecyclerView.setOnTouchListener((v, event) -> {
                if (event.getRawY() > 2000)
                    listener.dragAutoScroll(DOWN);
                else if (event.getRawY() < 250)
                    listener.dragAutoScroll(UP);
                else if (event.getRawY() < 2000 && event.getRawY() > 250)
                    listener.dragAutoScroll(STOP);
                return false;
            });
        }

        public void setNoData(boolean isEmpty) {
            mBinding.noData.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        }
    }

    public interface MainDragAutoScrollListener {
        void dragAutoScroll(int upDownStop);
    }
}
