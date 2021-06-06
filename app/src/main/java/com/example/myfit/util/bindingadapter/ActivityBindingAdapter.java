package com.example.myfit.util.bindingadapter;

import android.view.View;
import android.widget.TextView;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.databinding.BindingAdapter;

import com.example.myfit.R;
import com.example.myfit.ui.main.MainActivity;
import com.example.myfit.ui.main.MainActivityViewModel;
import com.example.myfit.ui.search.adapter.AutoCompleteAdapter;
import com.example.myfit.util.KeyBoardUtil;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Set;

public class ActivityBindingAdapter {
    @BindingAdapter("setContentView")
    public static void setContentView(CoordinatorLayout root, MainActivity activity) {
        activity.setContentView(root);
    }

    @BindingAdapter("setSupportActionBar")
    public static void setSupportActionBar(MaterialToolbar toolbar, MainActivity activity) {
        activity.setSupportActionBar(toolbar);
    }

    @BindingAdapter("removeBottomNavShadow")
    public static void removeBottomNavShadow(BottomNavigationView bottomNavigationView, String dummy) {
        bottomNavigationView.setBackgroundTintList(null);
    }

    @BindingAdapter("alignBottomNavIcons")
    public static void alignBottomNavIcons(BottomNavigationView bottomNavigationView, String dummy) {
        bottomNavigationView.getMenu().getItem(2).setVisible(false);
    }

    @BindingAdapter("change")
    public static void change(FloatingActionButton fab, int destinationId) {
        if (destinationId == R.id.mainFragment)
            fabChange(fab, R.drawable.icon_search);
        else if (destinationId == R.id.listFragment)
            fabChange(fab, R.drawable.icon_add);
        else if (destinationId == R.id.sizeFragment)
            fabChange(fab, R.drawable.icon_save);
    }

    private static void fabChange(FloatingActionButton fab, int resId) {
        fab.hide();
        fab.setImageResource(resId);
        fab.show();
    }

    @BindingAdapter("setAutoCompleteItems")
    public static void setAutoCompleteItems(MaterialAutoCompleteTextView autoCompleteTextView, Set<String> data) {
        if (autoCompleteTextView.getAdapter() instanceof AutoCompleteAdapter && data != null) {
            ArrayList<String> listData = new ArrayList<>(data);
            ((AutoCompleteAdapter) autoCompleteTextView.getAdapter()).setItem(listData);
        }
    }

    @BindingAdapter("keyboardShowingListener")
    public static void keyboardShowingListener(CoordinatorLayout layout, MainActivityViewModel model) {
        layout.getViewTreeObserver().addOnGlobalLayoutListener(() ->
                model.getKeyboardShowingLive().setValue(KeyBoardUtil.isKeyboardShowing(layout)));
    }
}
