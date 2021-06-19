package com.leebeebeom.closetnote.ui.main.list.adapter.folderadapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.leebeebeom.closetnote.data.tuple.tuple.FolderTuple;
import com.leebeebeom.closetnote.databinding.ItemVhFolderGridBinding;
import com.leebeebeom.closetnote.util.SizeLiveSet;
import com.leebeebeom.closetnote.util.adapter.BaseAdapter;
import com.leebeebeom.closetnote.util.adapter.viewholder.BaseVHListener;
import com.leebeebeom.closetnote.util.adapter.viewholder.FolderGridVH;
import com.leebeebeom.closetnote.util.constant.Sort;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

import dagger.hilt.android.scopes.FragmentScoped;

@FragmentScoped
public class FolderAdapter extends BaseAdapter<FolderTuple, BaseVHListener, FolderGridVH> {

    @Inject
    public FolderAdapter(SizeLiveSet<FolderTuple> selectedFolderTuples, Fragment fragment) {
        super(selectedFolderTuples, (BaseVHListener) fragment);
    }

    @Override
    public void setSort(Sort sort) {
        //TODO
    }

    @Override
    protected FolderGridVH getViewHolder(@NotNull ViewGroup parent, BaseVHListener listener, SizeLiveSet<FolderTuple> selectedItems) {
        ItemVhFolderGridBinding binding = ItemVhFolderGridBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new FolderGridVH(binding, listener, selectedItems);
    }
}
