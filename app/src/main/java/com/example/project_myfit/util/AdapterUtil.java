package com.example.project_myfit.util;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.example.project_myfit.R;
import com.example.project_myfit.data.model.Category;
import com.example.project_myfit.data.model.Folder;
import com.example.project_myfit.data.model.Size;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.checkbox.MaterialCheckBox;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public class AdapterUtil {
    //all checked
    private final Context mContext;
    private Animation mOpenAnimation;

    public AdapterUtil(Context context) {
        this.mContext = context;
    }

    public void listActionModeOn(@NotNull MaterialCardView cardView, @NotNull MaterialCheckBox checkBox, @NotNull HashSet<Long> selectedItemIdHashSet, long id) {
        //checked
        if (mOpenAnimation == null)
            mOpenAnimation = AnimationUtils.loadAnimation(mContext, R.anim.recycler_list_slide_right);
        if (!mOpenAnimation.hasStarted())
            cardView.setAnimation(mOpenAnimation);
        checkBox.setChecked(selectedItemIdHashSet.contains(id));
    }

    public void listActionModeOff(@NotNull MaterialCardView cardView, @NotNull MaterialCheckBox checkBox, @NotNull HashSet<Long> selectedCategoryIdHashSet) {
        //checked
        cardView.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.recycler_list_slide_left));
        checkBox.setChecked(false);
        mOpenAnimation = null;
        if (selectedCategoryIdHashSet.size() != 0) selectedCategoryIdHashSet.clear();
    }

    public void gridActionModeOn(@NotNull MaterialCheckBox checkBox, @NotNull HashSet<Long> selectedItemIdHashSet, long id) {
        checkBox.setVisibility(View.VISIBLE);
        checkBox.setChecked(selectedItemIdHashSet.contains(id));
    }

    public void gridActionModeOff(@NotNull MaterialCheckBox checkBox, @NotNull HashSet<Long> selectedItemHashSet) {
        checkBox.setVisibility(View.GONE);
        checkBox.setChecked(false);
        if (!selectedItemHashSet.isEmpty()) selectedItemHashSet.clear();
    }

    public void itemMove(int from, int to, List<?> list) {
        if (from < to) {//down
            for (int i = from; i < to; i++) {
                Collections.swap(list, i, i + 1);
                swap(list, i, true);
            }
        } else {//up
            for (int i = from; i > to; i--) {
                Collections.swap(list, i, i - 1);
                swap(list, i, false);
            }
        }
    }

    private void swap(@NotNull List<?> list, int i, boolean isDown) {
        //checked
        if (list.get(i) instanceof Category) {
            Category category1 = (Category) list.get(i);
            Category category2 = isDown ? (Category) list.get(i + 1) : (Category) list.get(i - 1);
            int toOrder = category1.getOrderNumber();
            int fromOrder = category2.getOrderNumber();
            category1.setOrderNumber(fromOrder);
            category2.setOrderNumber(toOrder);
        } else if (list.get(i) instanceof Folder) {
            Folder folder1 = (Folder) list.get(i);
            Folder folder2 = isDown ? (Folder) list.get(i + 1) : (Folder) list.get(i - 1);
            if (folder1.getId() != -1 && folder2.getId() != -1) {
                int toOrder = folder1.getOrderNumber();
                int fromOrder = folder2.getOrderNumber();
                folder1.setOrderNumber(fromOrder);
                folder2.setOrderNumber(toOrder);
            }
        } else if (list.get(i) instanceof Size) {
            Size size1 = (Size) list.get(i);
            Size size2 = isDown ? (Size) list.get(i + 1) : (Size) list.get(i - 1);
            int toOrder = size1.getOrderNumber();
            int fromOrder = size2.getOrderNumber();
            size1.setOrderNumber(fromOrder);
            size2.setOrderNumber(toOrder);
        }
    }

    public int getCategoryContentsSize(Category category, @NotNull List<Long> folderFolderIdList, List<Long> sizeFolderIdList) {
        //checked
        int amount = 0;
        for (Long l : folderFolderIdList)
            if (l == category.getId()) amount++;
        for (Long l : sizeFolderIdList)
            if (l == category.getId()) amount++;
        return amount;
    }

    public int getFolderContentsSize(Folder folder, @NotNull List<Long> folderFolderIdList, List<Long> sizeFolderIdList) {
        //checked
        int amount = 0;
        for (Long l : folderFolderIdList)
            if (l == folder.getId()) amount++;
        for (Long l : sizeFolderIdList)
            if (l == folder.getId()) amount++;
        return amount;
    }
}
