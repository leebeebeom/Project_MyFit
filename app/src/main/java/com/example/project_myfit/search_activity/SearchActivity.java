package com.example.project_myfit.search_activity;

import android.graphics.Rect;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModelProvider;

import com.example.project_myfit.R;
import com.example.project_myfit.databinding.ActivitySearchBinding;
import com.example.project_myfit.search_activity.adapter.AutoCompleteAdapter;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivitySearchBinding binding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.searchToolbar);

        SearchViewModel model = new ViewModelProvider(this).get(SearchViewModel.class);

        binding.searchAutoCompleteEditText.requestFocus();

        AutoCompleteAdapter autoCompleteAdapter = new AutoCompleteAdapter(this, R.layout.item_auto_complete, R.id.itemAutoCompleteText);
        binding.searchAutoCompleteEditText.setAdapter(autoCompleteAdapter);
        setAutoCompleteLive(model, autoCompleteAdapter);
    }

    private void setAutoCompleteLive(@NotNull SearchViewModel model, AutoCompleteAdapter autoCompleteAdapter) {
        LiveData<List<String>> folderNameLive = model.getAllFolderNameLive();
        LiveData<List<String>> sizeBrandLive = model.getAllSizeBrandLive();
        LiveData<List<String>> sizeNameLive = model.getAllSizeNameLive();

        MediatorLiveData<List<String>> mediatorLiveData = new MediatorLiveData<>();
        mediatorLiveData.addSource(folderNameLive, mediatorLiveData::setValue);
        mediatorLiveData.addSource(sizeBrandLive, mediatorLiveData::setValue);
        mediatorLiveData.addSource(sizeNameLive, mediatorLiveData::setValue);

        List<String> autoCompleteList = new ArrayList<>();
        mediatorLiveData.observe(this, stringList -> {
            autoCompleteList.clear();
            autoCompleteListAddValue(folderNameLive, autoCompleteList);
            autoCompleteListAddValue(sizeBrandLive, autoCompleteList);
            autoCompleteListAddValue(sizeNameLive, autoCompleteList);
            autoCompleteList.sort(String::compareTo);
            autoCompleteAdapter.setItem(autoCompleteList);
        });
    }

    private void autoCompleteListAddValue(@NotNull LiveData<List<String>> liveData, List<String> autoCompleteList) {
        if (liveData.getValue() != null)
            for (String s : liveData.getValue())
                if (!autoCompleteList.contains(s.trim()))
                    autoCompleteList.add(s);
    }

    @Override
    public boolean dispatchTouchEvent(@NotNull MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof TextInputEditText || v instanceof MaterialAutoCompleteTextView) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) ev.getRawX(), (int) ev.getRawY())) {
                    v.clearFocus();
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(ev);
    }
}