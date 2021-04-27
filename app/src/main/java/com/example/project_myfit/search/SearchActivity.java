package com.example.project_myfit.search;

import android.graphics.Rect;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModelProvider;

import com.example.project_myfit.R;
import com.example.project_myfit.databinding.ActivitySearchBinding;
import com.example.project_myfit.search.main.adapter.AutoCompleteAdapter;
import com.example.project_myfit.util.KeyboardUtil;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivitySearchBinding binding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolBarSearch);

        SearchViewModel model = new ViewModelProvider(this).get(SearchViewModel.class);

        binding.acTvSearch.requestFocus();

        AutoCompleteAdapter autoCompleteAdapter = new AutoCompleteAdapter(this, R.layout.item_auto_complete_texv_view, R.id.tv_item_ac_tv);
        binding.acTvSearch.setAdapter(autoCompleteAdapter);
        acLive(model, autoCompleteAdapter);
    }

    private void acLive(@NotNull SearchViewModel model, AutoCompleteAdapter autoCompleteAdapter) {
        LiveData<List<String>> folderNameLive = model.getFolderNameLive();
        LiveData<List<String>> sizeBrandLive = model.getSizeBrandLive();
        LiveData<List<String>> sizeNameLive = model.getSizeNameLive();

        MediatorLiveData<List<String>> mediatorLiveData = new MediatorLiveData<>();
        mediatorLiveData.addSource(folderNameLive, mediatorLiveData::setValue);
        mediatorLiveData.addSource(sizeBrandLive, mediatorLiveData::setValue);
        mediatorLiveData.addSource(sizeNameLive, mediatorLiveData::setValue);

        HashSet<String> autoCompleteHashSet = new HashSet<>();
        List<String> autoCompleteList = new ArrayList<>();
        mediatorLiveData.observe(this, stringList -> {
            autoCompleteHashSet.clear();
            autoCompleteList.clear();

            acListAddValue(folderNameLive, autoCompleteHashSet);
            acListAddValue(sizeBrandLive, autoCompleteHashSet);
            acListAddValue(sizeNameLive, autoCompleteHashSet);

            autoCompleteList.addAll(autoCompleteHashSet);
            autoCompleteList.sort(String::compareTo);

            autoCompleteAdapter.setItem(autoCompleteList);
        });
    }

    private void acListAddValue(@NotNull LiveData<List<String>> liveData, HashSet<String> autoCompleteList) {
        if (liveData.getValue() != null)
            for (String s : liveData.getValue()) autoCompleteList.add(s.trim());
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
                    KeyboardUtil.hide(this, v);
                }
            }
        }
        return super.dispatchTouchEvent(ev);
    }
}