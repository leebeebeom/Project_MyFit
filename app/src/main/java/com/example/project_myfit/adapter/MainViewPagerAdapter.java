package com.example.project_myfit.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_myfit.data.Repository;
import com.example.project_myfit.data.model.Category;
import com.example.project_myfit.databinding.ItemMainRecyclerViewBinding;
import com.example.project_myfit.util.Sort;
import com.michaelflisar.dragselectrecyclerview.DragSelectTouchListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.example.project_myfit.MyFitConstant.BOTTOM;
import static com.example.project_myfit.MyFitConstant.DOWN;
import static com.example.project_myfit.MyFitConstant.ETC;
import static com.example.project_myfit.MyFitConstant.OUTER;
import static com.example.project_myfit.MyFitConstant.STOP;
import static com.example.project_myfit.MyFitConstant.TOP;
import static com.example.project_myfit.MyFitConstant.UP;

public class MainViewPagerAdapter extends RecyclerView.Adapter<MainViewPagerAdapter.ViewPagerVH> {
    //all checked
    private final CategoryAdapter[] mAdapterArray;
    private MainDragAutoScrollListener mListener;
    private final DragSelectTouchListener mDragSelectListener;
    private final ItemTouchHelper[] mTouchHelperArray;

    public MainViewPagerAdapter(CategoryAdapter[] adapterList, DragSelectTouchListener dragSelectListener, ItemTouchHelper[] touchHelperList) {
        //checked
        this.mAdapterArray = adapterList;
        this.mDragSelectListener = dragSelectListener;
        this.mTouchHelperArray = touchHelperList;
        setHasStableIds(true);
    }

    public void setItem(int sort, List<Category> categoryList, Repository repository) {
        //checked
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

        mAdapterArray[0].submitList(sort, topList, repository.getFolderFolderIdByParent(TOP), repository.getSizeFolderIdByParent(TOP));
        mAdapterArray[1].submitList(sort, bottomList, repository.getFolderFolderIdByParent(BOTTOM), repository.getSizeFolderIdByParent(BOTTOM));
        mAdapterArray[2].submitList(sort, outerList, repository.getFolderFolderIdByParent(OUTER), repository.getSizeFolderIdByParent(OUTER));
        mAdapterArray[3].submitList(sort, etcList, repository.getFolderFolderIdByParent(ETC), repository.getSizeFolderIdByParent(ETC));
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setOnCategoryAdapterListener(MainDragAutoScrollListener listener) {
        //checked
        this.mListener = listener;
    }

    @NonNull
    @NotNull
    @Override
    public ViewPagerVH onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        //checked
        ItemMainRecyclerViewBinding binding = ItemMainRecyclerViewBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewPagerVH(binding, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewPagerVH holder, int position) {
        //checked
        if (holder.mBinding.mainRecyclerView.getAdapter() == null) {
            mAdapterArray[position].setViewPagerVH(holder);

            holder.mBinding.mainRecyclerView.setAdapter(mAdapterArray[position]);

            mTouchHelperArray[position].attachToRecyclerView(holder.mBinding.mainRecyclerView);
            holder.mBinding.mainRecyclerView.addOnItemTouchListener(mDragSelectListener);

            if (mAdapterArray[position].getItemCount() == 0)
                holder.mBinding.noData.setVisibility(View.VISIBLE);
            else holder.mBinding.noData.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }

    public static class ViewPagerVH extends RecyclerView.ViewHolder {
        //all checked
        private final ItemMainRecyclerViewBinding mBinding;

        @SuppressLint("ClickableViewAccessibility")
        public ViewPagerVH(@NotNull ItemMainRecyclerViewBinding binding, MainDragAutoScrollListener listener) {
            //checked
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
            //checked
            if (isEmpty) mBinding.noData.setVisibility(View.VISIBLE);
            else mBinding.noData.setVisibility(View.GONE);
        }
    }

    public interface MainDragAutoScrollListener {
        //checked
        void dragAutoScroll(int upDownStop);
    }
}
