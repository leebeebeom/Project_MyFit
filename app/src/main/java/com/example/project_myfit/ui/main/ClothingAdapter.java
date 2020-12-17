package com.example.project_myfit.ui.main;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_myfit.databinding.ItemMainFragmentBinding;
import com.example.project_myfit.ui.main.database.ChildCategory;

import java.util.List;

public class ClothingAdapter extends RecyclerView.Adapter<ClothingAdapter.ClothingViewModer> {
    private List<ChildCategory> mItem;

    //클릭 리스너
    interface OnCategoryItemClickListener {
        void onItemClicked(ChildCategory childCategory);
    }

    OnCategoryItemClickListener mListener;

    public void setOnCategoryItemClickListener(OnCategoryItemClickListener listener) {
        mListener = listener;
    }

    //아이템 셋팅
    public void setItem(List<ChildCategory> childCategories) {
        this.mItem = childCategories;
    }

    @NonNull
    @Override
    public ClothingViewModer onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //레이아웃 인플레이터 객체 가져오기
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        //바인딩
        ItemMainFragmentBinding binding = ItemMainFragmentBinding.inflate(layoutInflater, parent, false);
        return new ClothingViewModer(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ClothingViewModer holder, int position) {
        ChildCategory childCategory = mItem.get(position);
        //item_main_fragment.xml에 변수로 들어갈 거임.
        //XML 내부에서 자체적으로 처리
        holder.binding.setChildCategory(childCategory);
        //클릭 리스너
        holder.binding.itemImageClothing.setOnClickListener(v -> mListener.onItemClicked(childCategory));
    }


    @Override
    public int getItemCount() {
        return mItem.size();
    }


    public static class ClothingViewModer extends RecyclerView.ViewHolder {
        ItemMainFragmentBinding binding;
        public ClothingViewModer(ItemMainFragmentBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
