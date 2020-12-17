package com.example.project_myfit.ui.main.category_activity;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_myfit.databinding.ItemAddCategoryBinding;
import com.example.project_myfit.ui.main.database.ChildCategory;

import java.util.ArrayList;
import java.util.List;

public class AddCategoryAdapter extends RecyclerView.Adapter<AddCategoryAdapter.ClothingViewModel> {
    private final List<ChildCategory> mItem;
    //선택된 아이템
    private final List<ChildCategory> mSelectedItem;

    //생성자 아이템 셋팅
    public AddCategoryAdapter(List<ChildCategory> mItem) {
        this.mItem = mItem;
        mSelectedItem = new ArrayList<>();
    }

    @NonNull
    @Override
    public ClothingViewModel onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //레이아웃 인플레이터 얻기
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        //바인딩 객체 얻기
        ItemAddCategoryBinding binding = ItemAddCategoryBinding.inflate(layoutInflater, parent, false);
        return new ClothingViewModel(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ClothingViewModel holder, int position) {
        ChildCategory childCategory = mItem.get(position);
        //xml 바인딩
        holder.binding.setChildCategory(childCategory);
        //선택 이벤트
        holder.binding.imageClothing.setOnClickListener(v -> selectItem(childCategory, holder));
    }

    @Override
    public int getItemCount() {
        return mItem.size();
    }

    //뷰홀더
    public static class ClothingViewModel extends RecyclerView.ViewHolder {
        ItemAddCategoryBinding binding;

        public ClothingViewModel(@NonNull ItemAddCategoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }


    }
    //선택 이벤트
    private void selectItem(ChildCategory childCategory, ClothingViewModel holder) {
        //들어온 clothing 아이템이 없으면(최초 클릭)
        if (!mSelectedItem.contains(childCategory)) {
            //SelectedItem에 추가
            mSelectedItem.add(childCategory);
            holder.binding.imageClothing.setBackgroundColor(Color.GRAY);
            holder.binding.checkbox.setVisibility(View.VISIBLE);
            holder.binding.checkbox.setChecked(true);

        } else {//있으면 삭제
            mSelectedItem.remove(childCategory);
            holder.binding.imageClothing.setBackgroundColor(0);
            holder.binding.checkbox.setChecked(false);
            holder.binding.checkbox.setVisibility(View.GONE);



        }
    }
    //선택된 아이템 반환
    public List<ChildCategory> getSelectedItem() {
        return mSelectedItem;
    }


}
