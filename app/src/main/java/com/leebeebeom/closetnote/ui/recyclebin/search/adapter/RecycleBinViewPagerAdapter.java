//package com.leebeebeom.closetnote.ui.recyclebin.search.adapter;
//
//import android.view.LayoutInflater;
//import android.view.ViewGroup;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.leebeebeom.closetnote.databinding.ItemRecyclerViewBinding;
//import com.leebeebeom.closetnote.util.dragselect.DragSelect;
//import com.leebeebeom.closetnote.util.adapter.BaseAdapter;
//import com.leebeebeom.closetnote.util.adapter.viewholder.ViewPagerVH;
//
//import org.jetbrains.annotations.NotNull;
//
//public class RecycleBinViewPagerAdapter extends RecyclerView.Adapter<ViewPagerVH> {
//    private final BaseAdapter<?, ?>[] mAdapterArray;
//    private final DragSelect[] mDragSelectArray;
//    private final ViewPagerVH.AutoScrollListener mListener;
//
//    public RecycleBinViewPagerAdapter(BaseAdapter<?, ?>[] mAdapterArray, DragSelect[] dragSelectArray, ViewPagerVH.AutoScrollListener mListener) {
//        this.mAdapterArray = mAdapterArray;
//        this.mDragSelectArray = dragSelectArray;
//        this.mListener = mListener;
//        setHasStableIds(true);
//    }
//
//    @Override
//    public long getItemId(int position) {
//        return position;
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
//    public void onBindViewHolder(@NonNull @NotNull ViewPagerVH holder, int position) {
//        if (holder.getBinding().rv.getAdapter() == null) {
//            holder.getBinding().rv.setAdapter(mAdapterArray[position]);
//            mDragSelectArray[position].setRecyclerView(holder.getBinding().rv);
//
//            holder.getBinding().rv.addOnItemTouchListener(mDragSelectArray[position]);
//        }
//    }
//
//    @Override
//    public int getItemCount() {
//        return 5;
//    }
//}
