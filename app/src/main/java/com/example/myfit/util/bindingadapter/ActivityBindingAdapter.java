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

    @BindingAdapter("setAutoCompleteItems")
    public static void setAutoCompleteItems(MaterialAutoCompleteTextView autoCompleteTextView, Set<String> data) {
        if (autoCompleteTextView.getAdapter() instanceof AutoCompleteAdapter && data != null) {
            ArrayList<String> listData = new ArrayList<>(data);
            ((AutoCompleteAdapter) autoCompleteTextView.getAdapter()).setItem(listData);
        }
    }
}
