package com.example.project_myfit.util;

import android.graphics.Rect;
import android.view.View;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.NotNull;

public class ListenerZip {
    private boolean mIsKeyboardShowing;

    public void keyboardShowingListener(@NotNull View view, FloatingActionButton floatingActionButton, BottomAppBar bottomAppBar) {
        view.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            Rect r = new Rect();
            view.getWindowVisibleDisplayFrame(r);
            int screenHeight = view.getRootView().getHeight();

            int keypadHeight = screenHeight - r.bottom;

            if (keypadHeight > screenHeight * 0.15) {
                if (!mIsKeyboardShowing) {
                    mIsKeyboardShowing = true;
                    floatingActionButton.setVisibility(View.INVISIBLE);
                    bottomAppBar.setVisibility(View.INVISIBLE);
                }
            } else {
                if (mIsKeyboardShowing) {
                    mIsKeyboardShowing = false;
                    bottomAppBar.setVisibility(View.VISIBLE);
                    floatingActionButton.show();
                }
            }
        });
    }
}
