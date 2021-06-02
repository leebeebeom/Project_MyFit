package com.example.myfit.util;

import androidx.navigation.NavController;
import androidx.navigation.NavDirections;

import com.example.myfit.util.constant.ParentCategory;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.tabs.TabLayout;

import org.jetbrains.annotations.NotNull;

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
}
