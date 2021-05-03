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

import com.example.project_myfit.data.model.Size;
import com.example.project_myfit.databinding.ItemSizeListBinding;
import com.example.project_myfit.recyclebin.search.RecycleBinSearchViewModel;
import com.example.project_myfit.util.adapter.ParentAdapter;
import com.example.project_myfit.util.adapter.viewholder.SizeListVH;
import com.example.project_myfit.util.adapter.viewholder.SizeVHListener;
import com.example.project_myfit.util.ktw.KoreanTextMatcher;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.example.project_myfit.util.MyFitConstant.LISTVIEW;

public class RecycleBinSizeAdapter extends ParentAdapter<Size, SizeListVH> implements Filterable {
    private final SizeVHListener mListener;
    private final RecycleBinSearchViewModel mModel;
    private List<Size> mAllSizeList;

    public RecycleBinSizeAdapter(Context context, SizeVHListener listener, RecycleBinSearchViewModel model) {
        super(new DiffUtil.ItemCallback<Size>() {
            @Override
            public boolean areItemsTheSame(@NonNull @NotNull Size oldItem, @NonNull @NotNull Size newItem) {
                return oldItem.getId() == newItem.getId();
            }

            @Override
            public boolean areContentsTheSame(@NonNull @NotNull Size oldItem, @NonNull @NotNull Size newItem) {
                return true;
            }
        }, context);
        this.mListener = listener;
        this.mModel = model;
    }

    public void setItem(List<Size> list, CharSequence word) {
        this.mAllSizeList = list;
        setWord(word);
    }

    public void setWord(CharSequence word) {
        getFilter().filter(word, count -> {
            if (mModel.getFilteredListSizeLive().getValue() != null) {
                Integer[] countArray = mModel.getFilteredListSizeLive().getValue();
                countArray[2] = count;
                mModel.getFilteredListSizeLive().setValue(countArray);
            }
        });
    }

    @NonNull
    @NotNull
    @Override
    public SizeListVH onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        ItemSizeListBinding binding = ItemSizeListBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new SizeListVH(binding, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull SizeListVH holder, int position) {
        Size size = getItem(position);
        holder.setSize(size);

        restoreSelectedHashSet();
        setActionMode(LISTVIEW, holder.getBinding().cardView, holder.getBinding().cb, size.getId());
        holder.getBinding().iconDragHandle.setVisibility(View.GONE);
        holder.getBinding().cbFavorite.setClickable(false);
    }

    @Override
    public void itemMove(int from, int to) {

    }

    private class RecycleBinSearchSizeFilter extends Filter {

        @Override
        protected FilterResults performFiltering(@NotNull CharSequence constraint) {
            String keyWord = constraint.toString().toLowerCase(Locale.getDefault()).replaceAll("\\p{Z}", "");

            List<Size> filteredList = new ArrayList<>();

            if (!TextUtils.isEmpty(keyWord)) {
                for (Size size : mAllSizeList) {
                    if (KoreanTextMatcher.isMatch(size.getBrand().toLowerCase(Locale.getDefault()).replaceAll("\\p{Z}", ""), keyWord))
                        filteredList.add(size);
                    else if (KoreanTextMatcher.isMatch(size.getName().toLowerCase(Locale.getDefault()).replaceAll("\\p{Z}", ""), keyWord))
                        filteredList.add(size);
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
        return new RecycleBinSearchSizeFilter();
    }
}
