package com.example.project_myfit.ui.main.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_myfit.databinding.ItemMainRecyclerViewBinding;
import com.example.project_myfit.ui.main.database.Category;
import com.michaelflisar.dragselectrecyclerview.DragSelectTouchListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static com.example.project_myfit.MyFitConstant.BOTTOM;
import static com.example.project_myfit.MyFitConstant.ETC;
import static com.example.project_myfit.MyFitConstant.OUTER;
import static com.example.project_myfit.MyFitConstant.TOP;

public class ViewPagerAdapter extends RecyclerView.Adapter<ViewPagerAdapter.ViewPagerVH> {
    private final List<CategoryAdapter> mAdapterList;
    private List<Category> mCategoryList;
    int mSort;
    private ItemTouchHelper mTopTouchHelper, mBottomTouchHelper, mOuterTouchHelper, mETCTouchHelper;
    private MainDragAutoScroll mListener2;
    private final DragSelectTouchListener mDragSelectListener;

    public ViewPagerAdapter(List<CategoryAdapter> adapterList, DragSelectTouchListener dragSelectListenerList) {
        this.mAdapterList = adapterList;
        this.mDragSelectListener = dragSelectListenerList;
        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setOnCategoryAdapterListener(CategoryAdapter.CategoryAdapterListener listener) {
        this.mListener2 = (MainDragAutoScroll) listener;
    }

    public void setItem(List<Category> categoryList) {
        this.mCategoryList = categoryList;
        notifyDataSetChanged();
    }

    @NonNull
    @NotNull
    @Override
    public ViewPagerVH onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        ItemMainRecyclerViewBinding binding = ItemMainRecyclerViewBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewPagerVH(binding, mListener2);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewPagerVH holder, int position) {
        List<Category> newCategory = new ArrayList<>();
        if (mCategoryList != null) {
            if (holder.getLayoutPosition() == 0) {
                CategoryAdapter topAdapter = mAdapterList.get(0);
                if (mTopTouchHelper == null) {
                    mTopTouchHelper = new ItemTouchHelper(new MainDragCallBack(topAdapter));
                    mTopTouchHelper.attachToRecyclerView(holder.mBinding.mainRecyclerView);
                    holder.mBinding.mainRecyclerView.addOnItemTouchListener(mDragSelectListener);
                }
                if (holder.mBinding.mainRecyclerView.getAdapter() == null)
                    holder.mBinding.mainRecyclerView.setAdapter(topAdapter);
                for (Category category : mCategoryList)
                    if (category.getParentCategory().equals(TOP)) newCategory.add(category);
                topAdapter.setItem(newCategory);
            } else if (holder.getLayoutPosition() == 1) {
                CategoryAdapter bottomAdapter = mAdapterList.get(1);
                if (mBottomTouchHelper == null) {
                    mBottomTouchHelper = new ItemTouchHelper(new MainDragCallBack(bottomAdapter));
                    mBottomTouchHelper.attachToRecyclerView(holder.mBinding.mainRecyclerView);
                    holder.mBinding.mainRecyclerView.addOnItemTouchListener(mDragSelectListener);
                }
                if (holder.mBinding.mainRecyclerView.getAdapter() == null)
                    holder.mBinding.mainRecyclerView.setAdapter(bottomAdapter);
                for (Category category : mCategoryList)
                    if (category.getParentCategory().equals(BOTTOM)) newCategory.add(category);
                bottomAdapter.setItem(newCategory);
            } else if (holder.getLayoutPosition() == 2) {
                CategoryAdapter outerAdapter = mAdapterList.get(2);
                if (mOuterTouchHelper == null) {
                    mOuterTouchHelper = new ItemTouchHelper(new MainDragCallBack(outerAdapter));
                    mOuterTouchHelper.attachToRecyclerView(holder.mBinding.mainRecyclerView);
                    holder.mBinding.mainRecyclerView.addOnItemTouchListener(mDragSelectListener);
                }
                if (holder.mBinding.mainRecyclerView.getAdapter() == null)
                    holder.mBinding.mainRecyclerView.setAdapter(outerAdapter);

                for (Category category : mCategoryList)
                    if (category.getParentCategory().equals(OUTER)) newCategory.add(category);
                outerAdapter.setItem(newCategory);
            } else if (holder.getLayoutPosition() == 3) {
                CategoryAdapter etcAdapter = mAdapterList.get(3);
                if (mETCTouchHelper == null) {
                    mETCTouchHelper = new ItemTouchHelper(new MainDragCallBack(etcAdapter));
                    mETCTouchHelper.attachToRecyclerView(holder.mBinding.mainRecyclerView);
                    holder.mBinding.mainRecyclerView.addOnItemTouchListener(mDragSelectListener);
                }
                if (holder.mBinding.mainRecyclerView.getAdapter() == null)
                    holder.mBinding.mainRecyclerView.setAdapter(etcAdapter);

                for (Category category : mCategoryList)
                    if (category.getParentCategory().equals(ETC)) newCategory.add(category);
                etcAdapter.setItem(newCategory);
            }
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }

    public void setSort(int sort) {
        if (mSort != sort) {
            this.mSort = sort;
            mAdapterList.get(0).setSort(mSort);
            mAdapterList.get(1).setSort(mSort);
            mAdapterList.get(2).setSort(mSort);
            mAdapterList.get(3).setSort(mSort);
        }
    }

    public List<ItemTouchHelper> getTouchHelperList() {
        List<ItemTouchHelper> touchHelperList = new ArrayList<>();
        touchHelperList.add(mTopTouchHelper);
        touchHelperList.add(mBottomTouchHelper);
        touchHelperList.add(mOuterTouchHelper);
        touchHelperList.add(mETCTouchHelper);
        return touchHelperList;
    }

    public void setActionModeState(int actionModeState, @NotNull String parentCategory) {
        switch (parentCategory) {
            case TOP:
                mAdapterList.get(0).setActionModeState(actionModeState);
                break;
            case BOTTOM:
                mAdapterList.get(1).setActionModeState(actionModeState);
                break;
            case OUTER:
                mAdapterList.get(2).setActionModeState(actionModeState);
                break;
            default:
                mAdapterList.get(3).setActionModeState(actionModeState);
                break;
        }
    }

    public CategoryAdapter getTopAdapter() {
        return mAdapterList.get(0);
    }

    public CategoryAdapter getBottomAdapter() {
        return mAdapterList.get(1);
    }

    public CategoryAdapter getOuterAdapter() {
        return mAdapterList.get(2);
    }

    public CategoryAdapter getETCAdapter() {
        return mAdapterList.get(3);
    }

    public void selectAll(@NotNull String parentCategory) {
        switch (parentCategory) {
            case TOP:
                mAdapterList.get(0).selectAll();
                break;
            case BOTTOM:
                mAdapterList.get(1).selectAll();
                break;
            case OUTER:
                mAdapterList.get(2).selectAll();
                break;
            default:
                mAdapterList.get(3).selectAll();
                break;
        }
    }

    public void deSelectAll(@NotNull String parentCategory) {
        switch (parentCategory) {
            case TOP:
                mAdapterList.get(0).deselectAll();
                break;
            case BOTTOM:
                mAdapterList.get(1).deselectAll();
                break;
            case OUTER:
                mAdapterList.get(2).deselectAll();
                break;
            default:
                mAdapterList.get(3).deselectAll();
                break;
        }
    }

    public void setSelectedPosition(HashSet<Integer> selectedPosition, @NotNull String parentCategory) {
        switch (parentCategory) {
            case TOP:
                mAdapterList.get(0).setSelectedPosition(selectedPosition);
                break;
            case BOTTOM:
                mAdapterList.get(1).setSelectedPosition(selectedPosition);
                break;
            case OUTER:
                mAdapterList.get(2).setSelectedPosition(selectedPosition);
                break;
            case ETC:
                mAdapterList.get(3).setSelectedPosition(selectedPosition);
                break;
        }
    }

    public static class ViewPagerVH extends RecyclerView.ViewHolder {
        private final ItemMainRecyclerViewBinding mBinding;

        @SuppressLint("ClickableViewAccessibility")
        public ViewPagerVH(@NotNull ItemMainRecyclerViewBinding binding, MainDragAutoScroll listener2) {
            super(binding.getRoot());
            this.mBinding = binding;

            mBinding.mainRecyclerView.setOnTouchListener((v, event) -> {
                if (event.getRawY() > 2000)
                    listener2.dragAutoScroll(0);
                else if (event.getRawY() < 250)
                    listener2.dragAutoScroll(1);
                return false;
            });
        }

        public ItemMainRecyclerViewBinding getBinding() {
            return mBinding;
        }
    }

    public interface MainDragAutoScroll {
        void dragAutoScroll(int upDown);
    }
}
