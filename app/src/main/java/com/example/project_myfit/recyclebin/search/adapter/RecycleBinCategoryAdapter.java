package com.example.project_myfit.recyclebin.search.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import com.example.project_myfit.data.model.Category;
import com.example.project_myfit.databinding.ItemCategoryBinding;
import com.example.project_myfit.recyclebin.search.RecycleBinSearchViewModel;
import com.example.project_myfit.util.adapter.ParentAdapter;
import com.example.project_myfit.util.adapter.viewholder.CategoryVH;
import com.example.project_myfit.util.ktw.KoreanTextMatcher;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.example.project_myfit.util.MyFitConstant.LISTVIEW;

public class RecycleBinCategoryAdapter extends ParentAdapter<Category, CategoryVH> implements Filterable {
    private final CategoryVH.CategoryVHListener mListener;
    private final RecycleBinSearchViewModel mModel;
    private List<Category> mAllCategoryList;

    public RecycleBinCategoryAdapter(Context context, CategoryVH.CategoryVHListener listener, RecycleBinSearchViewModel model) {
        super(new DiffUtil.ItemCallback<Category>() {
            @Override
            public boolean areItemsTheSame(@NonNull @NotNull Category oldItem, @NonNull @NotNull Category newItem) {
                return oldItem.getId() == newItem.getId();
            }

            @Override
            public boolean areContentsTheSame(@NonNull @NotNull Category oldItem, @NonNull @NotNull Category newItem) {
                return true;
            }
        }, context);
        this.mListener = listener;
        this.mModel = model;
    }

    public void setItem(List<Category> list, List<Long> folderParentIdList, List<Long> sizeParentIdList, CharSequence word) {
        super.setItem(folderParentIdList, sizeParentIdList);
        this.mAllCategoryList = list;
        setWord(word);
    }

    public void setWord(CharSequence word) {
        getFilter().filter(word, count -> {
            if (mModel.getFilteredListSizeLive().getValue() != null) {
                Integer[] countArray = mModel.getFilteredListSizeLive().getValue();
                countArray[0] = count;
                mModel.getFilteredListSizeLive().setValue(countArray);
            }
        });
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

        restoreSelectedHashSet();
        setActionMode(LISTVIEW, holder.getBinding().cardView, holder.getBinding().cb, category.getId());
        holder.getBinding().iconDragHandle.setVisibility(View.GONE);
    }

    @Override
    public void itemMove(int from, int to) {

    }

    private class RecycleBinSearchCategoryFilter extends Filter {

        @Override
        protected FilterResults performFiltering(@NotNull CharSequence constraint) {
            String keyWord = constraint.toString().toLowerCase(Locale.getDefault()).replaceAll("\\p{Z}", "");

            List<Category> filteredList = new ArrayList<>();

            if (!TextUtils.isEmpty(keyWord)) {
                for (Category category : mAllCategoryList) {
                    if (KoreanTextMatcher.isMatch(category.getCategoryName().toLowerCase(Locale.getDefault()).replaceAll("\\p{Z}", ""), keyWord))
                        filteredList.add(category);
                }
            }

            submitList(filteredList);
            FilterResults filterResults = new FilterResults();
            filterResults.count = filteredList.size();
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
        }
    }

    @Override
    public Filter getFilter() {
        return new RecycleBinSearchCategoryFilter();
    }
}
