//package com.leebeebeom.closetnote.ui.search.adapter;
//
//import android.view.LayoutInflater;
//import android.view.ViewGroup;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.ConcatAdapter;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.leebeebeom.closetnote.databinding.ItemRecyclerViewBinding;
//import com.leebeebeom.closetnote.util.dragselect.DragSelect;
//import com.leebeebeom.closetnote.util.adapter.viewholder.ViewPagerVH;
//
//import org.jetbrains.annotations.NotNull;
//
//public class SearchViewPagerAdapter extends RecyclerView.Adapter<ViewPagerVH> {
//    private final ConcatAdapter[] mConcatAdapterArray;
//    private final DragSelect[] mDragSelectArray;
//    private final ViewPagerVH.AutoScrollListener mListener;
//
//    public SearchViewPagerAdapter(ConcatAdapter[] adapterArray, DragSelect[] dragSelectArray,
//                                  ViewPagerVH.AutoScrollListener listener) {
//        this.mConcatAdapterArray = adapterArray;
//        this.mDragSelectArray = dragSelectArray;
//        this.mListener = listener;
//        setHasStableIds(true);
//    }
//
//    @NonNull
//    @NotNull
//    @Override
//    public ViewPagerVH onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
//        ItemRecyclerViewBinding binding = ItemRecyclerViewBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
//        return new ViewPagerVH(binding, mListener);
//    }
//
//    @Override
//    public long getItemId(int position) {
//        return position;
//    }
//
//    @Override
//    public int getItemCount() {
//        return 4;
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull @NotNull ViewPagerVH holder, int position) {
//        if (holder.getBinding().rv.getAdapter() == null) {
//            holder.getBinding().rv.setAdapter(mConcatAdapterArray[position]);
//
//            mDragSelectArray[position].setRecyclerView(holder.getBinding().rv);
//            holder.getBinding().rv.addOnItemTouchListener(mDragSelectArray[position]);
//        }
//    }
//}
