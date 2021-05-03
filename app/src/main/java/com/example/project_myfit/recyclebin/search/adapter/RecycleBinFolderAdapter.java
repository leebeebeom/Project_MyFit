package com.example.project_myfit.recyclebin.search.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import com.example.project_myfit.data.model.Folder;
import com.example.project_myfit.databinding.ItemFolderListBinding;
import com.example.project_myfit.recyclebin.search.RecycleBinSearchViewModel;
import com.example.project_myfit.util.adapter.ParentAdapter;
import com.example.project_myfit.util.adapter.viewholder.FolderListVH;
import com.example.project_myfit.util.adapter.viewholder.FolderVHListener;
import com.example.project_myfit.util.ktw.KoreanTextMatcher;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.example.project_myfit.util.MyFitConstant.LISTVIEW;

public class RecycleBinFolderAdapter extends ParentAdapter<Folder, FolderListVH> implements Filterable {
    private final FolderVHListener mListener;
    private final RecycleBinSearchViewModel mModel;
    private List<Folder> mAllFolderList;

    public RecycleBinFolderAdapter(Context context, FolderVHListener listener, RecycleBinSearchViewModel model) {
        super(new DiffUtil.ItemCallback<Folder>() {
            @Override
            public boolean areItemsTheSame(@NonNull @NotNull Folder oldItem, @NonNull @NotNull Folder newItem) {
                return oldItem.getId() == newItem.getId();
            }

            @Override
            public boolean areContentsTheSame(@NonNull @NotNull Folder oldItem, @NonNull @NotNull Folder newItem) {
                return true;
            }
        }, context);
        this.mListener = listener;
        this.mModel = model;
    }

    public void setItem(List<Folder> list, List<Long> folderParentIdList, List<Long> sizeParentIdList, CharSequence word) {
        super.setItem(folderParentIdList, sizeParentIdList);
        this.mAllFolderList = list;
        setWord(word);
    }

    public void setWord(CharSequence word) {
        getFilter().filter(word, count -> {
            if (mModel.getFilteredListSizeLive().getValue() != null) {
                Integer[] countArray = mModel.getFilteredListSizeLive().getValue();
                countArray[1] = count;
                mModel.getFilteredListSizeLive().setValue(countArray);
            }
        });
    }

    @NonNull
    @NotNull
    @Override
    public FolderListVH onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        ItemFolderListBinding binding = ItemFolderListBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new FolderListVH(binding, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull FolderListVH holder, int position) {
        Folder folder = getItem(position);
        holder.setFolder(folder);
        setContentsSize(holder.getBinding().tvContentsSize, folder.getId());

        restoreSelectedHashSet();
        setActionMode(LISTVIEW, holder.getBinding().cardView, holder.getBinding().cb, folder.getId());
    }

    @Override
    public void itemMove(int from, int to) {

    }

    private class RecycleBinSearchFolderFilter extends Filter {

        @Override
        protected FilterResults performFiltering(@NotNull CharSequence constraint) {
            String keyWord = constraint.toString().toLowerCase(Locale.getDefault()).replaceAll("\\p{Z}", "");

            List<Folder> filteredList = new ArrayList<>();

            if (!TextUtils.isEmpty(keyWord)) {
                for (Folder folder : mAllFolderList) {
                    if (KoreanTextMatcher.isMatch(folder.getFolderName().toLowerCase(Locale.getDefault()).replaceAll("\\p{Z}", ""), keyWord))
                        filteredList.add(folder);
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
        return new RecycleBinSearchFolderFilter();
    }
}
