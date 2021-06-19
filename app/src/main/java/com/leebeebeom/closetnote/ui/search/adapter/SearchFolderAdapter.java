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
//import com.leebeebeom.closetnote.data.model.model.Folder;
//import com.leebeebeom.closetnote.databinding.ItemFolderListBinding;
//import com.leebeebeom.closetnote.ui.search.SearchViewModel;
//import com.leebeebeom.closetnote.util.adapter.BaseAdapter;
//import com.leebeebeom.closetnote.util.adapter.viewholder.FolderListVH;
//import com.leebeebeom.closetnote.util.ktw.KoreanTextMatcher;
//
//import org.jetbrains.annotations.NotNull;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Locale;
//
//import static com.leebeebeom.closetnote.util.MyFitConstant.LISTVIEW;
//
//public class SearchFolderAdapter extends BaseAdapter<Folder, FolderListVH> implements Filterable {
//    private final FolderVHListener mListener;
//    private List<Folder> mAllFolderList;
//    private final SearchViewModel mModel;
//    private final int mAdapterNumber;
//
//    public SearchFolderAdapter(Context context, FolderVHListener listener, SearchViewModel model, int adapterNumber) {
//        super(new DiffUtil.ItemCallback<Folder>() {
//            @Override
//            public boolean areItemsTheSame(@NonNull @NotNull Folder oldItem, @NonNull @NotNull Folder newItem) {
//                return oldItem.getId() == newItem.getId();
//            }
//
//            @Override
//            public boolean areContentsTheSame(@NonNull @NotNull Folder oldItem, @NonNull @NotNull Folder newItem) {
//                return oldItem.getName().equals(newItem.getName()) &&
//                        oldItem.getDummy() == newItem.getDummy() &&
//                        oldItem.getParentId() == newItem.getParentId();
//            }
//        }, context);
//        this.mListener = listener;
//        this.mModel = model;
//        this.mAdapterNumber = adapterNumber;
//    }
//
//    public void setItem(List<Folder> list, List<Long> folderParentIdList, List<Long> sizeParentIdList, CharSequence word) {
//        super.setItems(folderParentIdList, sizeParentIdList);
//        this.mAllFolderList = list;
//        setWord(word);
//    }
//
//    public void setWord(CharSequence word) {
//        getFilter().filter(word, count -> {
//            if (mModel.getFolderFilteredListSizeLive().getValue() != null){
//                Integer[] countArray = mModel.getFolderFilteredListSizeLive().getValue();
//                countArray[mAdapterNumber] = count;
//                mModel.getFolderFilteredListSizeLive().setValue(countArray);
//            }
//        });
//    }
//
//    @NonNull
//    @NotNull
//    @Override
//    public FolderListVH onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
//        ItemFolderListBinding binding = ItemFolderListBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
//        return new FolderListVH(binding, mListener);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull @NotNull FolderListVH holder, int position) {
//        Folder folder = getItem(position);
//        holder.bind(folder);
//
//
//        restoreSelectedHashSet();
//        setActionMode(LISTVIEW, holder.getBinding().cardView, holder.getBinding().cb, folder.getId(), position);
//    }
//
//    @Override
//    public void moveItem(int from, int to) {
//
//    }
//
//    private class SearchFolderFilter extends Filter {
//        @Override
//        protected FilterResults performFiltering(@NotNull CharSequence constraint) {
//            String keyWord = constraint.toString().toLowerCase(Locale.getDefault()).replaceAll("\\p{Z}", "");
//            List<Folder> filteredList = new ArrayList<>();
//            if (!TextUtils.isEmpty(keyWord)) {
//                for (Folder folder : mAllFolderList) {
//                    String folderName = folder.getName().toLowerCase(Locale.getDefault()).replaceAll("\\p{Z}", "");
//                    if (KoreanTextMatcher.isMatch(folderName, keyWord))
//                        filteredList.add(folder);
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
//        return new SearchFolderFilter();
//    }
//}
