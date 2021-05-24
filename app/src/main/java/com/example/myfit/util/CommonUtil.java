package com.example.myfit.util;

import android.content.Context;
import android.graphics.Rect;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.NavDirections;

import com.example.myfit.util.constant.ParentCategory;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;

import org.jetbrains.annotations.NotNull;

public class CommonUtil {
    public static void editTextLosableFocus(@NotNull MotionEvent ev, View getCurrentFocus, Context context) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            if (getCurrentFocus instanceof TextInputEditText || getCurrentFocus instanceof MaterialAutoCompleteTextView) {
                Rect outRect = new Rect();
                getCurrentFocus.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) ev.getRawX(), (int) ev.getRawY())) {
                    getCurrentFocus.clearFocus();
                    KeyBoardUtil.hideKeyBoard(getCurrentFocus);
                }
            }
        }
    }

    public static void setBadgeCount(TabLayout.Tab tab, int count, TypedValue colorControl){
        if (tab != null) {
            if (count == 0) tab.removeBadge();
            else {
                BadgeDrawable badge = tab.getOrCreateBadge();
                badge.setVisible(true);
                badge.setNumber(count);
                badge.setBackgroundColor(colorControl.data);
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
