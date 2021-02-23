package com.example.project_myfit.searchActivity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.project_myfit.R;
import com.example.project_myfit.Repository;
import com.example.project_myfit.databinding.ActivitySearchBinding;
import com.example.project_myfit.searchActivity.adapter.AutoCompleteAdapter;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private final Repository mRepository = new Repository(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivitySearchBinding binding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.searchToolbar);

        binding.autoCompleteTextView.requestFocus();
        getSupportFragmentManager().beginTransaction().replace(R.id.search_fragment_layout, new SearchFragment()).commit();

        AutoCompleteAdapter autoCompleteAdapter = new AutoCompleteAdapter(this, R.layout.item_auto_complete, R.id.auto_complete_text, getAutoCompleteList(mRepository));
        binding.autoCompleteTextView.setAdapter(autoCompleteAdapter);
    }

    @NotNull
    private List<String> getAutoCompleteList(@NotNull Repository repository) {
        List<String> autoCompleteList = new ArrayList<>();
        for (String s : repository.getFolderNameList())
            if (!autoCompleteList.contains(s.trim()))
                autoCompleteList.add(s.trim());

        for (String s : repository.getSizeBrandList())
            if (!autoCompleteList.contains(s.trim()))
                autoCompleteList.add(s.trim());

        for (String s : repository.getSizeNameList())
            if (!autoCompleteList.contains(s.trim()))
                autoCompleteList.add(s.trim());
        return autoCompleteList;
    }
}