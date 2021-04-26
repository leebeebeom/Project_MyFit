package com.example.project_myfit.util;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import org.jetbrains.annotations.NotNull;

public class KeyboardUtil {
    public static void keyboardShow(@NotNull Context context, @NotNull View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInputFromInputMethod(view.getWindowToken(), 0);
    }

    public static void keyboardHide(@NotNull Context context, @NotNull View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
