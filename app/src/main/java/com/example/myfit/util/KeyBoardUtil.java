package com.example.myfit.util;

import android.content.Context;
import android.graphics.Rect;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import org.jetbrains.annotations.NotNull;

public class KeyBoardUtil {
    public static boolean isKeyboardShowing(@NotNull View rootView) {
        Rect r = new Rect();
        rootView.getWindowVisibleDisplayFrame(r);

        int screenHeight = rootView.getRootView().getHeight();
        int keypadHeight = screenHeight - r.bottom;
        return keypadHeight > screenHeight * 0.15;
    }

    public static void showKeyBoard(@NotNull View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInputFromInputMethod(view.getWindowToken(), 0);
    }

    public static void hideKeyBoard(@NotNull View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
