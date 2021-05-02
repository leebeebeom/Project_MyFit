package com.example.project_myfit.util;

import android.view.View;

import androidx.core.widget.NestedScrollView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.NotNull;

import static com.example.project_myfit.util.MyFitConstant.DOWN;
import static com.example.project_myfit.util.MyFitConstant.STOP;
import static com.example.project_myfit.util.MyFitConstant.UP;

public class ListenerUtil {
    public void scrollChangeListener(@NotNull NestedScrollView scrollView, FloatingActionButton fabTop) {
        scrollView.setOnScrollChangeListener((View.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (v.getScrollY() == 0) fabTop.hide();
            else fabTop.show();

            if ((MyFitVariable.isDragSelecting || MyFitVariable.isDragging) && MyFitVariable.scrollEnable && scrollY > oldScrollY)
                v.postDelayed(() -> v.scrollBy(0, 1), 50);
            else if ((MyFitVariable.isDragSelecting || MyFitVariable.isDragging) && MyFitVariable.scrollEnable && scrollY < oldScrollY)
                v.postDelayed(() -> v.scrollBy(0, -1), 50);
        });
    }

    public void fabTopClick(NestedScrollView scrollView, FloatingActionButton fabTop) {
        fabTop.setOnClickListener(v -> {
            scrollView.scrollTo(0, 0);
            scrollView.setOnScrollChangeListener((View.OnScrollChangeListener) (v1, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                if (scrollY != 0)
                    scrollView.scrollTo(0, 0);
                else {
                    scrollView.setOnScrollChangeListener((View.OnScrollChangeListener) null);
                    scrollChangeListener(scrollView, fabTop);
                }
            });
        });

    }

    public void autoScroll(NestedScrollView scrollView, int upDownStop) {
        if ((MyFitVariable.isDragSelecting || MyFitVariable.isDragging))
            if (upDownStop == DOWN) {
                scrollView.scrollBy(0, 1);
                MyFitVariable.scrollEnable = true;
            } else if (upDownStop == UP) {
                scrollView.scrollBy(0, -1);
                MyFitVariable.scrollEnable = true;
            } else if (upDownStop == STOP) {
                scrollView.scrollBy(0, 0);
                MyFitVariable.scrollEnable = false;
            }
    }
}
