package com.example.myfit.ui.main.list.adapter.folderadapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.example.myfit.data.tuple.tuple.FolderTuple;
import com.example.myfit.databinding.ItemVhFolderGridBinding;
import com.example.myfit.util.SizeLiveSet;
import com.example.myfit.util.adapter.BaseAdapter;
import com.example.myfit.util.adapter.viewholder.BaseVHListener;
import com.example.myfit.util.adapter.viewholder.FolderGridVH;

import org.jetbrains.annotations.NotNull;

import java.util.Set;

import javax.inject.Inject;

import dagger.hilt.android.scopes.FragmentScoped;

@FragmentScoped
public class FolderAdapter extends BaseAdapter<FolderTuple, BaseVHListener, FolderGridVH> {
    @Inject
    public FolderAdapter(SizeLiveSet<FolderTuple> selectedItems) {
        super(selectedItems);
    }

    @Override
    protected FolderGridVH getViewHolder(@NotNull ViewGroup parent, BaseVHListener listener, Set<FolderTuple> selectedItemIds) {
        ItemVhFolderGridBinding binding = ItemVhFolderGridBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new FolderGridVH(binding, listener, selectedItemIds);
    }
}
