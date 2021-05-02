package com.example.project_myfit.main.main.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_myfit.data.model.Category;
import com.example.project_myfit.databinding.ItemCategoryBinding;
import com.example.project_myfit.main.main.MainViewModel;
import com.example.project_myfit.util.MyFitVariable;
import com.example.project_myfit.util.adapter.ParentAdapter;
import com.example.project_myfit.util.adapter.viewholder.CategoryVH;
import com.example.project_myfit.util.adapter.viewholder.ViewPagerVH;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.example.project_myfit.util.MyFitConstant.LISTVIEW;

public class CategoryAdapter extends ParentAdapter<Category, CategoryVH> {
    private final MainViewModel mModel;
    private ViewPagerVH mMainViewPagerVH;
    private final CategoryVH.CategoryVHListener mListener;
    private List<Category> mCategoryList;

    public CategoryAdapter(Context context, MainViewModel model, CategoryVH.CategoryVHListener listener) {
        super(new DiffUtil.ItemCallback<Category>() {
            @Override
            public boolean areItemsTheSame(@NonNull @NotNull Category oldItem, @NonNull @NotNull Category newItem) {
                return oldItem.getId() == newItem.getId();
            }

            @Override
            public boolean areContentsTheSame(@NonNull @NotNull Category oldItem, @NonNull @NotNull Category newItem) {
                return oldItem.getCategoryName().equals(newItem.getCategoryName()) &&
                        oldItem.getDummy() == newItem.getDummy();
            }
        }, context);
        this.mModel = model;
        this.mListener = listener;
    }

    @Override
    public void setItem(int sort, List<Category> list, List<Long> folderParentIdList, List<Long> sizeParentIdList) {
        super.setItem(sort, list, folderParentIdList, sizeParentIdList);
        this.mCategoryList = list;
        if (list != null && mMainViewPagerVH != null)
            mMainViewPagerVH.setNoData(list.isEmpty());
    }

    public void setViewPagerVH(ViewPagerVH mainViewPagerVH) {
        this.mMainViewPagerVH = mainViewPagerVH;
    }

    @NonNull
    @NotNull
    @Override
    public CategoryVH onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        ItemCategoryBinding binding = ItemCategoryBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new CategoryVH(binding, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull CategoryVH holder, int position) {
        Category category = getItem(position);
        holder.setCategory(category);
        setContentsSize(holder.getBinding().tvContentsSize, category.getId());
        dragHandleTouch(holder);

        restoreSelectedHashSet();
        setActionMode(LISTVIEW, holder.getBinding().cardView, holder.getBinding().cb, category.getId());
        dragHandleVisibility(holder.getBinding().iconDragHandle);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void dragHandleTouch(@NotNull CategoryVH holder) {
        holder.getBinding().iconDragHandle.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN && !MyFitVariable.isDragging) {
                mListener.onCategoryDragHandleTouch(holder);
                draggingView(holder);
            }
            return false;
        });
    }

    @Override
    protected void draggingView(@NotNull RecyclerView.ViewHolder viewHolder) {
        super.draggingView(viewHolder);
        CategoryVH categoryVH = (CategoryVH) viewHolder;
        categoryVH.getBinding().cb.setVisibility(View.INVISIBLE);
        categoryVH.getBinding().tvCategoryName.setAlpha(0.5f);
        categoryVH.getBinding().layoutContentsSize.setAlpha(0.5f);
    }

    @Override
    protected void dropView(@NotNull RecyclerView.ViewHolder viewHolder) {
        super.dropView(viewHolder);
        CategoryVH categoryVH = (CategoryVH) viewHolder;
        categoryVH.getBinding().cb.setVisibility(View.VISIBLE);
        categoryVH.getBinding().tvCategoryName.setAlpha(0.8f);
        categoryVH.getBinding().layoutContentsSize.setAlpha(0.8f);
    }

    @Override
    public void itemMove(int from, int to) {
        mAdapterUtil.itemMove(from, to, mCategoryList);
        notifyItemMoved(from, to);
    }

    @Override
    public void itemDrop(RecyclerView.ViewHolder viewHolder) {
        super.itemDrop(viewHolder);
        mModel.categoryItemDrop(mCategoryList);
    }
}
