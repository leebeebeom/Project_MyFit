//package com.leebeebeom.closetnote.ui.search.adapter;
//
//import android.view.LayoutInflater;
//import android.view.ViewGroup;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.DiffUtil;
//import androidx.recyclerview.widget.ListAdapter;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.leebeebeom.closetnote.data.model.model.RecentSearch;
//import com.leebeebeom.closetnote.databinding.ItemRecentSearchBinding;
//
//import org.jetbrains.annotations.NotNull;
//
//public class RecentSearchAdapter extends ListAdapter<RecentSearch, RecentSearchAdapter.RecentSearchVH> {
//    private final RecentSearchAdapterListener mListener;
//
//    public RecentSearchAdapter(RecentSearchAdapterListener listener) {
//        super(new DiffUtil.ItemCallback<RecentSearch>() {
//            @Override
//            public boolean areItemsTheSame(@NonNull @NotNull RecentSearch oldItem, @NonNull @NotNull RecentSearch newItem) {
//                return oldItem.getId() == newItem.getId();
//            }
//
//            @Override
//            public boolean areContentsTheSame(@NonNull @NotNull RecentSearch oldItem, @NonNull @NotNull RecentSearch newItem) {
//                return String.valueOf(oldItem.getWord()).equals(String.valueOf(newItem.getWord()));
//            }
//        });
//        this.mListener = listener;
//    }
//
//    @NonNull
//    @NotNull
//    @Override
//    public RecentSearchVH onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
//        ItemRecentSearchBinding binding = ItemRecentSearchBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
//        return new RecentSearchVH(binding, mListener);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull @NotNull RecentSearchVH holder, int position) {
//        holder.setRecentSearch(getItem(position));
//    }
//
//    public static class RecentSearchVH extends RecyclerView.ViewHolder {
//        private final ItemRecentSearchBinding mBinding;
//        private RecentSearch mRecentSearch;
//
//        public RecentSearchVH(@NotNull ItemRecentSearchBinding binding, RecentSearchAdapterListener listener) {
//            super(binding.getRoot());
//            this.mBinding = binding;
//
//            itemView.setOnClickListener(v -> listener.recentSearchItemViewClick(mRecentSearch.getWord()));
//            binding.iconDelete.setOnClickListener(v -> listener.recentSearchDeleteClick(mRecentSearch));
//        }
//
//        public void setRecentSearch(RecentSearch recentSearch) {
//            mBinding.setRecentSearch(recentSearch);
//            this.mRecentSearch = recentSearch;
//        }
//    }
//
//    public interface RecentSearchAdapterListener {
//        void recentSearchItemViewClick(String word);
//
//        void recentSearchDeleteClick(RecentSearch recentSearch);
//    }
//}
