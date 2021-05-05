package com.example.project_myfit.util;

import android.content.Context;
import android.graphics.Rect;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.navigation.NavController;
import androidx.navigation.NavDirections;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CommonUtil {
    public static void editTextLosableFocus(@NotNull MotionEvent ev, View getCurrentFocus, Context context) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            if (getCurrentFocus instanceof TextInputEditText || getCurrentFocus instanceof MaterialAutoCompleteTextView) {
                Rect outRect = new Rect();
                getCurrentFocus.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) ev.getRawX(), (int) ev.getRawY())) {
                    getCurrentFocus.clearFocus();
                    keyBoardHide(context, getCurrentFocus);
                }
            }
        }
    }

    public static boolean isKeyboardShowing(@NotNull View rootView) {
        Rect r = new Rect();
        rootView.getWindowVisibleDisplayFrame(r);

        int screenHeight = rootView.getRootView().getHeight();
        int keypadHeight = screenHeight - r.bottom;
        return keypadHeight > screenHeight * 0.15;
    }

    public static void keyBoardShow(@NotNull Context context, @NotNull View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInputFromInputMethod(view.getWindowToken(), 0);
    }

    public static void keyBoardHide(@NotNull Context context, @NotNull View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void keyBoardToggle(@NotNull Context context) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(0, 0);
    }

    public static long createId(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
        return Long.parseLong(dateFormat.format(new Date(System.currentTimeMillis())));
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
}
