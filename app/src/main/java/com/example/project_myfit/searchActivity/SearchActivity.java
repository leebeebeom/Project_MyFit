package com.example.project_myfit.searchActivity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.project_myfit.R;
import com.example.project_myfit.databinding.ActivitySearchBinding;
import com.example.project_myfit.searchActivity.adapter.AutoCompleteAdapter;

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

        binding.autoCompleteTextView.requestFocus();

        AutoCompleteAdapter autoCompleteAdapter = new AutoCompleteAdapter(this, R.layout.item_auto_complete, R.id.auto_complete_text, getAutoCompleteList(model));
        binding.autoCompleteTextView.setAdapter(autoCompleteAdapter);
    }

    @NotNull
    private List<String> getAutoCompleteList(@NotNull SearchViewModel model) {
        List<String> autoCompleteList = new ArrayList<>();

        List<String> sizeBrandNameList = model.getSizeBrandList();
        List<String> sizeNameList = model.getSizeNameList();

        for (String s : sizeBrandNameList)
            if (!autoCompleteList.contains(s)) autoCompleteList.add(s);
        for (String s : sizeNameList)
            if (!autoCompleteList.contains(s)) autoCompleteList.add(s);
        return autoCompleteList;
    }
}