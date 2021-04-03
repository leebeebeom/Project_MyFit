package com.example.project_myfit.util;

import android.content.Context;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.example.project_myfit.R;
import com.example.project_myfit.data.model.Category;
import com.example.project_myfit.data.model.Folder;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.checkbox.MaterialCheckBox;

import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.List;

public class AdapterUtils {
    //all checked
    private final Context mContext;
    private Animation mOpenAnimation;

    public AdapterUtils(Context context) {
        this.mContext = context;
    }

    public void listActionModeOn(@NotNull MaterialCardView cardView, @NotNull MaterialCheckBox checkBox, @NotNull HashSet<Long> selectedItemIdList, long id) {
        //checked
        if (mOpenAnimation == null) {
            mOpenAnimation = AnimationUtils.loadAnimation(mContext, R.anim.recycler_list_slide_right);
            cardView.setAnimation(mOpenAnimation);
        }
        checkBox.setChecked(selectedItemIdList.contains(id));
    }

    public void listActionModeOff(@NotNull MaterialCardView cardView, @NotNull MaterialCheckBox checkBox) {
        //checked
        cardView.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.recycler_list_slide_left));
        checkBox.setChecked(false);
        mOpenAnimation = null;
    }

    public int getCategoryContentsSize(Category category, @NotNull List<Long> folderFolderIdList, List<Long> sizeFolderIdList) {
        int amount = 0;
        for (Long l : folderFolderIdList)
            if (l == category.getId()) amount++;
        for (Long l : sizeFolderIdList)
            if (l == category.getId()) amount++;
        return amount;
    }

    public int getFolderContentsSize(Folder folder, @NotNull List<Long> folderFolderIdList, List<Long> sizeFolderIdList) {
        int amount = 0;
        for (Long l : folderFolderIdList)
            if (l == folder.getId()) amount++;
        for (Long l : sizeFolderIdList)
            if (l == folder.getId()) amount++;
        return amount;
    }
}
