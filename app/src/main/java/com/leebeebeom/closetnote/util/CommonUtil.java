package com.leebeebeom.closetnote.util;

import androidx.navigation.NavController;
import androidx.navigation.NavDirections;

import com.leebeebeom.closetnote.data.tuple.BaseTuple;
import com.leebeebeom.closetnote.util.constant.ParentCategory;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.tabs.TabLayout;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommonUtil {
    public static void setBadgeCount(TabLayout.Tab tab, int count, int colorControl) {
        if (tab != null) {
            if (count == 0) tab.removeBadge();
            else {
                BadgeDrawable badge = tab.getOrCreateBadge();
                badge.setVisible(true);
                badge.setNumber(count);
                badge.setBackgroundColor(colorControl);
            }
        }
    }

    public static void navigate(@NotNull NavController navController, int originResId, NavDirections action) {
        if (navController.getCurrentBackStackEntry() == navController.getBackStackEntry(originResId))
            navController.navigate(action);
    }

    public static String getParentCategory(int parentIndex) {
        switch (ParentCategory.values()[parentIndex]) {
            case TOP:
                return ParentCategory.TOP.name();
            case BOTTOM:
                return ParentCategory.BOTTOM.name();
            case OUTER:
                return ParentCategory.OUTER.name();
            default:
                return ParentCategory.ETC.name();
        }
    }

    public static long createId() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
        return Long.parseLong(dateFormat.format(new Date(System.currentTimeMillis())));
    }

    public static String getRecentSearchDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM월 dd일", Locale.getDefault());
        return dateFormat.format(new Date(System.currentTimeMillis()));
    }

    public static <T extends BaseTuple> void replaceNewOrderSelectedItems(List<T> newOrderList, SizeLiveSet<T> selectedItems) {
        List<T> newOrderSelectedItems =
                newOrderList.stream()
                        .filter(selectedItems::contains)
                        .collect(Collectors.toList());

        selectedItems.clear();
        selectedItems.addAll(newOrderSelectedItems);
    }
}
