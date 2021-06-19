//package com.leebeebeom.closetnote.ui.recyclebin.search.adapter;
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
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.leebeebeom.closetnote.data.model.model.Folder;
//import com.leebeebeom.closetnote.databinding.ItemFolderListBinding;
//import com.leebeebeom.closetnote.databinding.ItemHeaderBinding;
//import com.leebeebeom.closetnote.ui.recyclebin.search.RecycleBinSearchViewModel;
//import com.leebeebeom.closetnote.util.adapter.BaseAdapter;
//import com.leebeebeom.closetnote.util.adapter.viewholder.FolderListVH;
//import com.leebeebeom.closetnote.util.adapter.viewholder.HeaderVH;
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
//public class RecycleBinFolderAdapter extends BaseAdapter<Folder, RecyclerView.ViewHolder> implements Filterable {
//    private final FolderVHListener mListener;
//    private final RecycleBinSearchViewModel mModel;
//    private List<Folder> mAllFolderList;
//
//    public RecycleBinFolderAdapter(Context context, FolderVHListener listener, RecycleBinSearchViewModel model) {
//        super(new DiffUtil.ItemCallback<Folder>() {
//            @Override
//            public boolean areItemsTheSame(@NonNull @NotNull Folder oldItem, @NonNull @NotNull Folder newItem) {
//                return oldItem.getId() == newItem.getId();
//            }
//
//            @Override
//            public boolean areContentsTheSame(@NonNull @NotNull Folder oldItem, @NonNull @NotNull Folder newItem) {
//                return true;
//            }
//        }, context);
//        this.mListener = listener;
//        this.mModel = model;
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
//            if (mModel.getFilteredListSizeLive().getValue() != null) {
//                Integer[] countArray = mModel.getFilteredListSizeLive().getValue();
//                countArray[1] = count;
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
//            ItemFolderListBinding binding = ItemFolderListBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
//            return new FolderListVH(binding, mListener);
//        } else {
//            ItemHeaderBinding binding = ItemHeaderBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
//            return new HeaderVH(binding);
//        }
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder holder, int position) {
//        if (holder instanceof FolderListVH) {
//            Folder folder = getItem(position);
//            FolderListVH folderListVH = (FolderListVH) holder;
//            folderListVH.bind(folder);
//
//
//            restoreSelectedHashSet();
//            setActionMode(LISTVIEW, folderListVH.getBinding().cardView, folderListVH.getBinding().cb, folder.getId(), position);
//        } else if (holder instanceof HeaderVH)
//            ((HeaderVH) holder).bind(
//                    Constant.ParentCategory.values()[getItem(position).getParentIndex()].name());
//    }
//
//    @Override
//    public void moveItem(int from, int to) {
//
//    }
//
//    private class RecycleBinSearchFolderFilter extends Filter {
//
//        @Override
//        protected FilterResults performFiltering(@NotNull CharSequence constraint) {
//            String keyWord = constraint.toString().toLowerCase(Locale.getDefault()).replaceAll("\\p{Z}", "");
//
//            List<List<Folder>> filteredList = Arrays.asList(new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
//
//            if (!TextUtils.isEmpty(keyWord)) {
//                for (Folder folder : mAllFolderList) {
//                    String folderName = folder.getName().toLowerCase(Locale.getDefault()).replaceAll("\\p{Z}", "");
//                    if (KoreanTextMatcher.isMatch(folderName, keyWord))
//                        FilterUtil.classify(folder, filteredList);
//                }
//            }
//
//            FilterResults filterResults = new FilterResults();
//            for (int i = 0; i < filteredList.size(); i++)
//                filterResults.count += filteredList.get(i).size();
//
//            for (int i = 0; i < filteredList.size(); i++)
//                if (!filteredList.get(i).isEmpty())
//                    filteredList.get(i).add(0, new Folder(-1, null, -1, -1, i));
//
//            submitList(FilterUtil.getCombineList(filteredList));
//            return filterResults;
//        }
//
//        @Override
//        protected void publishResults(CharSequence constraint, FilterResults results) {
//        }
//    }
//
//
//    @Override
//    public Filter getFilter() {
//        return new RecycleBinSearchFolderFilter();
//    }
//}
