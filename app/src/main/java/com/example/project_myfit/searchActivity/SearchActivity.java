package com.example.project_myfit.searchActivity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.project_myfit.R;
import com.example.project_myfit.data.model.Size;
import com.example.project_myfit.databinding.ActivitySearchBinding;
import com.example.project_myfit.searchActivity.adapter.AutoCompleteAdapter;
import com.example.project_myfit.util.Sort;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.example.project_myfit.util.MyFitConstant.SORT_BRAND;
import static com.example.project_myfit.util.MyFitConstant.SORT_NAME;

public class SearchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivitySearchBinding binding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.searchToolbar);

        SearchViewModel model = new ViewModelProvider(this).get(SearchViewModel.class);

        binding.autoCompleteTextView.requestFocus();

        AutoCompleteAdapter autoCompleteAdapter = new AutoCompleteAdapter(this, R.layout.item_auto_complete, R.id.auto_complete_text);
        binding.autoCompleteTextView.setAdapter(autoCompleteAdapter);
        setAutoCompleteLive(model, autoCompleteAdapter);
    }

    private void setAutoCompleteLive(@NotNull SearchViewModel model, AutoCompleteAdapter autoCompleteAdapter) {
        model.getAllSizeLive().observe(this, sizeList -> {
            List<String> autoCompleteList = new ArrayList<>();
            Sort.sizeSort(SORT_BRAND, sizeList);
            for (Size size : sizeList)
                if (!autoCompleteList.contains(size.getBrand()))
                    autoCompleteList.add(size.getBrand());
            Sort.sizeSort(SORT_NAME, sizeList);
            for (Size size : sizeList)
                if (!autoCompleteList.contains(size.getName()))
                    autoCompleteList.add(size.getName());
            autoCompleteAdapter.setItem(autoCompleteList);
        });
    }
}