//package com.leebeebeom.closetnote.ui.search.adapter;
//
//import android.content.Context;
//import android.text.TextUtils;
//import android.view.LayoutInflater;
//import android.view.ViewGroup;
//import android.widget.Filter;
//import android.widget.Filterable;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.DiffUtil;
//
//import com.leebeebeom.closetnote.databinding.ItemSizeListBinding;
//import com.leebeebeom.closetnote.ui.search.SearchViewModel;
//import com.leebeebeom.closetnote.util.adapter.BaseAdapter;
//import com.leebeebeom.closetnote.util.adapter.viewholder.SizeListVH;
//import com.leebeebeom.closetnote.util.ktw.KoreanTextMatcher;
//
//import org.jetbrains.annotations.NotNull;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Locale;
//
//import static com.leebeebeom.closetnote.util.MyFitConstant.ACTION_MODE_ON;
//import static com.leebeebeom.closetnote.util.MyFitConstant.LISTVIEW;
//
//public class SearchSizeAdapter extends BaseAdapter<SizeTop, SizeListVH> implements Filterable {
//    private final SizeVHListener mListener;
//    private List<SizeTop> mAllSizeList;
//    private final SearchViewModel mModel;
//    private final int mAdapterNumber;
//
//    public SearchSizeAdapter(Context context, SizeVHListener listener, SearchViewModel model, int adapterNumber) {
//        super(new DiffUtil.ItemCallback<SizeTop>() {
//            @Override
//            public boolean areItemsTheSame(@NonNull @NotNull SizeTop oldItem, @NonNull @NotNull SizeTop newItem) {
//                return oldItem.getId() == newItem.getId();
//            }
//
//            @Override
//            public boolean areContentsTheSame(@NonNull @NotNull SizeTop oldItem, @NonNull @NotNull SizeTop newItem) {
//                return ((SizeTop) oldItem).getBrand().equals(((SizeTop) newItem).getBrand()) &&
//                        ((SizeTop) oldItem).getName().equals(((SizeTop) newItem).getName()) &&
//                        ((SizeTop) oldItem).isFavorite() == ((SizeTop) newItem).isFavorite() &&
//                        String.valueOf(((SizeTop) oldItem).getImageUri()).equals(String.valueOf(((SizeTop) newItem).getImageUri())) &&
//                        //for Item move
//                        ((SizeTop) oldItem).getParentId() == ((SizeTop) newItem).getParentId();
//            }
//        }, context);
//        this.mListener = listener;
//        this.mModel = model;
//        this.mAdapterNumber = adapterNumber;
//    }
//
//    public void setItem(List<SizeTop> list, CharSequence word) {
//        this.mAllSizeList = list;
//        setWord(word);
//    }
//
//    public void setWord(CharSequence word) {
//        getFilter().filter(word, count -> {
//            if (mModel.getSizeFilteredListSizeLive().getValue() != null) {
//                Integer[] countArray = mModel.getSizeFilteredListSizeLive().getValue();
//                countArray[mAdapterNumber] = count;
//                mModel.getSizeFilteredListSizeLive().setValue(countArray);
//            }
//        });
//    }
//
//    @NonNull
//    @NotNull
//    @Override
//    public SizeListVH onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
//        ItemSizeListBinding binding = ItemSizeListBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
//        return new SizeListVH(binding, mListener);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull @NotNull SizeListVH holder, int position) {
//        SizeTop size = getItem(position);
//        holder.setSize(size);
//
//        restoreSelectedHashSet();
//        setActionMode(LISTVIEW, holder.getBinding().cardView, holder.getBinding().cb, size.getId(), position);
//        holder.getBinding().cbFavorite.setClickable(mActionModeState != ACTION_MODE_ON);
//    }
//
//    @Override
//    public void moveItem(int from, int to) {
//
//    }
//
//    private class SearchSizeFilter extends Filter {
//        @Override
//        protected FilterResults performFiltering(@NotNull CharSequence constraint) {
//            String keyWord = constraint.toString().toLowerCase(Locale.getDefault()).replaceAll("\\p{Z}", "");
//            List<SizeTop> filteredList = new ArrayList<>();
//            if (!TextUtils.isEmpty(keyWord)) {
//                for (SizeTop size : mAllSizeList) {
//                    String brand = size.getBrand().toLowerCase(Locale.getDefault()).replaceAll("\\p{Z}", "");
//                    String name = size.getName().toLowerCase(Locale.getDefault()).replaceAll("\\p{Z}", "");
//                    if (KoreanTextMatcher.isMatch(brand, keyWord) || KoreanTextMatcher.isMatch(name, keyWord))
//                        filteredList.add(size);
//                }
//            }
//            submitList(filteredList);
//            FilterResults results = new FilterResults();
//            results.count = filteredList.size();
//            return results;
//        }
//
//        @Override
//        protected void publishResults(CharSequence constraint, FilterResults results) {
//        }
//    }
//
//    @Override
//    public Filter getFilter() {
//        return new SearchSizeFilter();
//    }
//}
