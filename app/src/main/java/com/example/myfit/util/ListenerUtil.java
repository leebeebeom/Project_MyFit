package com.example.myfit.util;

import android.content.Context;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;

import androidx.core.widget.NestedScrollView;

import com.example.myfit.util.constant.AutoScrollFlag;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputLayout;

import org.jetbrains.annotations.NotNull;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import static com.example.myfit.util.adapter.viewholder.BaseVH.sDragging;
import static com.example.myfit.util.dragselect.DragSelect.sDragSelecting;

public class ListenerUtil {
    public static boolean sScrollEnable;

    public static void setScrollChangeListener(@NotNull NestedScrollView scrollView, FloatingActionButton fabTop) {
        scrollView.setOnScrollChangeListener((View.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (v.getScrollY() == 0) fabTop.hide();
            else fabTop.show();

            if ((sDragSelecting || sDragging) && sScrollEnable && scrollY > oldScrollY)
                v.postDelayed(() -> v.scrollBy(0, 1), 50);
            else if ((sDragSelecting || sDragging) && sScrollEnable && scrollY < oldScrollY)
                v.postDelayed(() -> v.scrollBy(0, -1), 50);
        });
    }

    public static void setFabTopClickListener(NestedScrollView scrollView, @NotNull FloatingActionButton fabTop) {
        fabTop.setOnClickListener(v -> {
            scrollView.scrollTo(0, 0);
            scrollView.setOnScrollChangeListener((View.OnScrollChangeListener) (v1, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                if (scrollY != 0)
                    scrollView.scrollTo(0, 0);
                else {
                    scrollView.setOnScrollChangeListener((View.OnScrollChangeListener) null);
                    setScrollChangeListener(scrollView, fabTop);
                }
            });
        });
    }

    public static void viewPagerAutoScroll(NestedScrollView scrollView, AutoScrollFlag flag) {
        if ((sDragSelecting || sDragging))
            switch (flag) {
                case UP:
                    scrollView.scrollBy(0, -1);
                    sScrollEnable = true;
                case DOWN:
                    scrollView.scrollBy(0, 1);
                    sScrollEnable = true;
                case STOP:
                    scrollView.scrollBy(0, 0);
                    sScrollEnable = false;
            }
    }

    public static void autoScroll(NestedScrollView scrollView, @NotNull MotionEvent event) {
        if (event.getRawY() > 2000) {
            scrollView.scrollBy(0, 1);
            sScrollEnable = true;
        } else if (event.getRawY() < 250) {
            scrollView.scrollBy(0, -1);
            sScrollEnable = true;
        } else if (event.getRawY() < 2000 && event.getRawY() > 250)
            sScrollEnable = false;
    }

    public static void recentSearchClick(String word, @NotNull MaterialAutoCompleteTextView autoCompleteTextView, @NotNull TextInputLayout textInputLayout, Context context, int type) {
        autoCompleteTextView.setText(word);
        autoCompleteTextView.setSelection(word.length());
        autoCompleteTextView.dismissDropDown();
        textInputLayout.setEndIconVisible(false);
        CommonUtil.keyBoardHide(context, autoCompleteTextView);

        Repository repository = new Repository(context);
        repository.getRecentSearchRepository().insertRecentSearch(word, type);
    }

    public static void autoCompleteImeClick(@NotNull MaterialAutoCompleteTextView autoCompleteTextView, int recentSearchType, Context context) {
        autoCompleteTextView.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                CommonUtil.keyBoardHide(context, autoCompleteTextView);

                autoCompleteTextView.dismissDropDown();

                String word = autoCompleteTextView.getText().toString().trim();

                if (!TextUtils.isEmpty(word)) {
                    Repository repository = new Repository(context);
                    repository.getRecentSearchRepository().insertRecentSearch(word, recentSearchType);
                }
            }
            return false;
        });
    }

    public static void autoCompleteEndIconClick(MaterialAutoCompleteTextView autoCompleteTextView, @NotNull TextInputLayout textInputLayout, Context context) {
        textInputLayout.setEndIconOnClickListener(v -> {
            textInputLayout.setEndIconVisible(false);
            autoCompleteTextView.setText("");
            autoCompleteTextView.postDelayed(() -> CommonUtil.keyBoardShow(context, autoCompleteTextView), 50);
        });
    }

    public static void autoCompleteItemClick(@NotNull MaterialAutoCompleteTextView autoCompleteTextView, int recentSearchType, Context context) {
        autoCompleteTextView.setOnItemClickListener((parent, view, position, id) -> {
            autoCompleteTextView.clearFocus();
            CommonUtil.keyBoardHide(context, autoCompleteTextView);

            String word = String.valueOf(autoCompleteTextView.getText());
            Repository repository = new Repository(context);
            repository.getRecentSearchRepository().insertRecentSearch(word, recentSearchType);
        });
    }
}

