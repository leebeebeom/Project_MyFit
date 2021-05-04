package com.example.project_myfit.util;

import android.content.Context;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;

import androidx.core.widget.NestedScrollView;

import com.example.project_myfit.data.Repository;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputLayout;

import org.jetbrains.annotations.NotNull;

import java.util.List;

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

    public void fabTopClick(NestedScrollView scrollView, @NotNull FloatingActionButton fabTop) {
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

    public void viewPagerAutoScroll(NestedScrollView scrollView, int upDownStop) {
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

    public void autoScroll(NestedScrollView scrollView, @NotNull MotionEvent event) {
        if (event.getRawY() > 2000) {
            scrollView.scrollBy(0, 1);
            MyFitVariable.scrollEnable = true;
        } else if (event.getRawY() < 250) {
            scrollView.scrollBy(0, -1);
            MyFitVariable.scrollEnable = true;
        } else if (event.getRawY() < 2000 && event.getRawY() > 250)
            MyFitVariable.scrollEnable = false;
    }

    public void recentSearchClick(String word, @NotNull MaterialAutoCompleteTextView autoCompleteTextView, @NotNull TextInputLayout textInputLayout, Context context, int type) {
        autoCompleteTextView.setText(word);
        autoCompleteTextView.setSelection(word.length());
        autoCompleteTextView.dismissDropDown();
        textInputLayout.setEndIconVisible(false);
        CommonUtil.keyBoardHide(context, autoCompleteTextView);
        Repository.getRecentSearchRepository(context).overLapRecentSearchReInsert(word, type);
    }

    public void autoCompleteImeClick(@NotNull MaterialAutoCompleteTextView autoCompleteTextView, Context context, List<String> recentSearchStringList, int type) {
        CommonUtil.keyBoardHide(context, autoCompleteTextView);

        autoCompleteTextView.dismissDropDown();

        String word = autoCompleteTextView.getText().toString().trim();
        //검색어 중복시 지우고 맨 위로
        if (!TextUtils.isEmpty(word)) {
            if (recentSearchStringList.contains(word))
                Repository.getRecentSearchRepository(context).overLapRecentSearchReInsert(word, type);
            else Repository.getRecentSearchRepository(context).recentSearchInsert(word, type);
        }
    }

    public void autoCompleteEndIconClick(MaterialAutoCompleteTextView autoCompleteTextView, @NotNull TextInputLayout textInputLayout, Context context) {
        textInputLayout.setEndIconOnClickListener(v -> {
            textInputLayout.setEndIconVisible(false);
            autoCompleteTextView.setText("");
            autoCompleteTextView.postDelayed(() -> CommonUtil.keyBoardShow(context, autoCompleteTextView), 50);
        });
    }

    public void autoCompleteItemClick(@NotNull MaterialAutoCompleteTextView autoCompleteTextView, int recentSearchType, Context context) {
        Repository.RecentSearchRepository recentSearchRepository = Repository.getRecentSearchRepository(context);

        autoCompleteTextView.setOnItemClickListener((parent, view, position, id) -> {
            autoCompleteTextView.clearFocus();
            CommonUtil.keyBoardHide(context, autoCompleteTextView);

            List<String> recentSearchStringList = recentSearchRepository.getRecentSearchStringList();

            String word = String.valueOf(autoCompleteTextView.getText());

            if (recentSearchStringList.contains(word))
                recentSearchRepository.overLapRecentSearchReInsert(word, recentSearchType);
            else recentSearchRepository.recentSearchInsert(word, recentSearchType);
        });
    }
}

