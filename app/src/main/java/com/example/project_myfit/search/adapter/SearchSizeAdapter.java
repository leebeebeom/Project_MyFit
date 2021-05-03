package com.example.project_myfit.search.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import com.example.project_myfit.data.model.Size;
import com.example.project_myfit.databinding.ItemSizeListBinding;
import com.example.project_myfit.search.SearchViewModel;
import com.example.project_myfit.util.adapter.ParentAdapter;
import com.example.project_myfit.util.adapter.viewholder.SizeListVH;
import com.example.project_myfit.util.adapter.viewholder.SizeVHListener;
import com.example.project_myfit.util.ktw.KoreanTextMatcher;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.example.project_myfit.util.MyFitConstant.ACTION_MODE_ON;
import static com.example.project_myfit.util.MyFitConstant.LISTVIEW;

public class SearchSizeAdapter extends ParentAdapter<Size, SizeListVH> implements Filterable {
    private final SizeVHListener mListener;
    private List<Size> mAllSizeList;
    private final SearchViewModel mModel;
    private final int mAdapterNumber;

    public SearchSizeAdapter(Context context, SizeVHListener listener, SearchViewModel model, int adapterNumber) {
        super(new DiffUtil.ItemCallback<Size>() {
            @Override
            public boolean areItemsTheSame(@NonNull @NotNull Size oldItem, @NonNull @NotNull Size newItem) {
                return oldItem.getId() == newItem.getId();
            }

            @Override
            public boolean areContentsTheSame(@NonNull @NotNull Size oldItem, @NonNull @NotNull Size newItem) {
                return ((Size) oldItem).getBrand().equals(((Size) newItem).getBrand()) &&
                        ((Size) oldItem).getName().equals(((Size) newItem).getName()) &&
                        ((Size) oldItem).isFavorite() == ((Size) newItem).isFavorite() &&
                        String.valueOf(((Size) oldItem).getImageUri()).equals(String.valueOf(((Size) newItem).getImageUri())) &&
                        //for Item move
                        ((Size) oldItem).getParentId() == ((Size) newItem).getParentId();
            }
        }, context);
        this.mListener = listener;
        this.mModel = model;
        this.mAdapterNumber = adapterNumber;
    }

    public void setItem(List<Size> list, CharSequence word) {
        this.mAllSizeList = list;
        setWord(word);
    }

    public void setWord(CharSequence word) {
        getFilter().filter(word, count -> {
            if (mModel.getSizeFilteredListSizeLive().getValue() != null) {
                Integer[] countArray = mModel.getSizeFilteredListSizeLive().getValue();
                countArray[mAdapterNumber] = count;
                mModel.getSizeFilteredListSizeLive().setValue(countArray);
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
        holder.getBinding().cbFavorite.setClickable(mActionModeState != ACTION_MODE_ON);
    }

    @Override
    public void itemMove(int from, int to) {

    }

    private class SearchSizeFilter extends Filter {
        @Override
        protected FilterResults performFiltering(@NotNull CharSequence constraint) {
            String keyWord = constraint.toString().toLowerCase(Locale.getDefault()).replaceAll("\\p{Z}", "");
            List<Size> filteredList = new ArrayList<>();
            if (!TextUtils.isEmpty(keyWord)) {
                for (Size size : mAllSizeList) {
                    String brand = size.getBrand().toLowerCase(Locale.getDefault()).replaceAll("\\p{Z}", "");
                    String name = size.getName().toLowerCase(Locale.getDefault()).replaceAll("\\p{Z}", "");
                    if (KoreanTextMatcher.isMatch(brand, keyWord) || KoreanTextMatcher.isMatch(name, keyWord))
                        filteredList.add(size);
                }
            }
            submitList(filteredList);
            FilterResults results = new FilterResults();
            results.count = filteredList.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
        }
    }

    @Override
    public Filter getFilter() {
        return new SearchSizeFilter();
    }
}
