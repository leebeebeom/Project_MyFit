//package com.leebeebeom.closetnote.ui.recyclebin.search.adapter;
//
//import android.content.Context;
//import android.text.TextUtils;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Filter;
//import android.widget.Filterable;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.DiffUtil;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.leebeebeom.closetnote.databinding.ItemHeaderBinding;
//import com.leebeebeom.closetnote.databinding.ItemSizeListBinding;
//import com.leebeebeom.closetnote.ui.recyclebin.search.RecycleBinSearchViewModel;
//import com.leebeebeom.closetnote.util.adapter.BaseAdapter;
//import com.leebeebeom.closetnote.util.adapter.viewholder.HeaderVH;
//import com.leebeebeom.closetnote.util.adapter.viewholder.SizeListVH;
//import com.leebeebeom.closetnote.util.ktw.KoreanTextMatcher;
//
//import org.jetbrains.annotations.NotNull;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//import java.util.Locale;
//
//import static com.leebeebeom.closetnote.util.MyFitConstant.LISTVIEW;
//
//public class RecycleBinSizeAdapter extends BaseAdapter<SizeTop, RecyclerView.ViewHolder> implements Filterable {
//    private final SizeVHListener mListener;
//    private final RecycleBinSearchViewModel mModel;
//    private List<SizeTop> mAllSizeList;
//
//    public RecycleBinSizeAdapter(Context context, SizeVHListener listener, RecycleBinSearchViewModel model) {
//        super(new DiffUtil.ItemCallback<SizeTop>() {
//            @Override
//            public boolean areItemsTheSame(@NonNull @NotNull SizeTop oldItem, @NonNull @NotNull SizeTop newItem) {
//                return oldItem.getId() == newItem.getId();
//            }
//
//            @Override
//            public boolean areContentsTheSame(@NonNull @NotNull SizeTop oldItem, @NonNull @NotNull SizeTop newItem) {
//                return true;
//            }
//        }, context);
//        this.mListener = listener;
//        this.mModel = model;
//    }
//
//    public void setItem(List<SizeTop> list, CharSequence word) {
//        this.mAllSizeList = list;
//        setWord(word);
//    }
//
//    public void setWord(CharSequence word) {
//        getFilter().filter(word, count -> {
//            if (mModel.getFilteredListSizeLive().getValue() != null) {
//                Integer[] countArray = mModel.getFilteredListSizeLive().getValue();
//                countArray[2] = count;
//                mModel.getFilteredListSizeLive().setValue(countArray);
//            }
//        });
//    }
//
//    @Override
//    public int getItemViewType(int position) {
//        if (getItemId(position) != -1)
//            return 0;
//        else return 1;
//    }
//
//    @NonNull
//    @NotNull
//    @Override
//    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
//        if (viewType == 0) {
//            ItemSizeListBinding binding = ItemSizeListBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
//            return new SizeListVH(binding, mListener);
//        } else {
//            ItemHeaderBinding binding = ItemHeaderBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
//            return new HeaderVH(binding);
//        }
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder holder, int position) {
//        if (holder instanceof SizeListVH) {
//            SizeTop size = getItem(position);
//            SizeListVH sizeListVH = (SizeListVH) holder;
//            sizeListVH.setSize(size);
//
//            restoreSelectedHashSet();
//            setActionMode(LISTVIEW, sizeListVH.getBinding().cardView, sizeListVH.getBinding().cb, size.getId(), position);
//            sizeListVH.getBinding().iconDragHandle.setVisibility(View.GONE);
//            sizeListVH.getBinding().cbFavorite.setClickable(false);
//        } else if (holder instanceof HeaderVH)
//            ((HeaderVH) holder).bind(
//                    Constant.ParentCategory.values()[getItem(position).getParentCategoryIndex()].name());
//    }
//
//    @Override
//    public void moveItem(int from, int to) {
//
//    }
//
//    private class RecycleBinSearchSizeFilter extends Filter {
//
//        @Override
//        protected FilterResults performFiltering(@NotNull CharSequence constraint) {
//            String keyWord = constraint.toString().toLowerCase(Locale.getDefault()).replaceAll("\\p{Z}", "");
//
//            List<List<SizeTop>> filteredList = Arrays.asList(new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
//
//            if (!TextUtils.isEmpty(keyWord)) {
//                for (SizeTop size : mAllSizeList) {
//                    String brand = size.getBrand().toLowerCase(Locale.getDefault()).replaceAll("\\p{Z}", "");
//                    String name = size.getName().toLowerCase(Locale.getDefault()).replaceAll("\\p{Z}", "");
//                    if (KoreanTextMatcher.isMatch(brand, keyWord) || KoreanTextMatcher.isMatch(name, keyWord))
//                        FilterUtil.classify(size, filteredList);
//                }
//            }
//
//            FilterResults filterResults = new FilterResults();
//            for (int i = 0; i < filteredList.size(); i++)
//                filterResults.count += filteredList.get(i).size();
//
//            for (int i = 0; i < filteredList.size(); i++)
//                if (!filteredList.get(i).isEmpty())
//                    filteredList.get(i).add(0, new SizeTop(-1, i));
//
//            List<SizeTop> sizeList = FilterUtil.getCombineList(filteredList);
//            submitList(FilterUtil.getCombineList(filteredList));
//            return filterResults;
//        }
//
//        @Override
//        protected void publishResults(CharSequence constraint, FilterResults results) {
//        }
//    }
//
//    @Override
//    public Filter getFilter() {
//        return new RecycleBinSearchSizeFilter();
//    }
//}
