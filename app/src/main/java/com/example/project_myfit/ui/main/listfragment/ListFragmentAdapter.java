package com.example.project_myfit.ui.main.listfragment;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_myfit.databinding.ItemListFragmentBinding;
import com.example.project_myfit.ui.main.listfragment.database.Size;

import java.util.List;

public class ListFragmentAdapter extends RecyclerView.Adapter<ListFragmentAdapter.ListViewViewHolder> {
    private List<Size> mSize;

    //클릭 리스너
    interface OnItemClickedListener {
        void onItemClicked(Size size);
    }

    OnItemClickedListener mListener;

    public void setOnItemClickedListener(OnItemClickedListener listener) {
        mListener = listener;
    }
    //여기까지 클릭 리스너

    //아이템 셋팅
    public void setItems(List<Size> sizes) {
        this.mSize = sizes;
    }

    @NonNull
    @Override
    public ListViewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemListFragmentBinding binding = ItemListFragmentBinding.inflate(inflater, parent, false);
        return new ListViewViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ListViewViewHolder holder, int position) {
        Size size = mSize.get(position);
        if (position == 0) {
            holder.binding.layout.setPadding(0, 40, 0, 0);
        } else if (position + 1 == mSize.size()) {
            holder.binding.layout.setPadding(0, 0, 0, 40);
        }
        holder.binding.setSize(size);
        holder.itemView.setOnClickListener(v -> mListener.onItemClicked(size));
    }

    @Override
    public int getItemCount() {
        return mSize.size();
    }

    public static class ListViewViewHolder extends RecyclerView.ViewHolder {
        ItemListFragmentBinding binding;

        public ListViewViewHolder(ItemListFragmentBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
