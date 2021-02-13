package com.example.project_myfit.searchActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.app.AppCompatActivity;

import com.example.project_myfit.MainActivity;
import com.example.project_myfit.R;
import com.example.project_myfit.Repository;
import com.example.project_myfit.databinding.ActivitySearchBinding;
import com.example.project_myfit.searchActivity.adapter.AutoCompleteAdapter;
import com.example.project_myfit.searchActivity.adapter.RecentSearchAdapter;
import com.example.project_myfit.searchActivity.adapter.SearchAdapter;
import com.example.project_myfit.searchActivity.adapter.SearchViewPagerAdapter;
import com.example.project_myfit.searchActivity.database.RecentSearch;
import com.example.project_myfit.ui.main.listfragment.database.Folder;
import com.example.project_myfit.ui.main.listfragment.database.Size;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.android.material.textfield.TextInputLayout;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.example.project_myfit.MyFitConstant.BOTTOM;
import static com.example.project_myfit.MyFitConstant.ETC;
import static com.example.project_myfit.MyFitConstant.FOLDER_ID;
import static com.example.project_myfit.MyFitConstant.OUTER;
import static com.example.project_myfit.MyFitConstant.SIZE_ID;
import static com.example.project_myfit.MyFitConstant.TOP;

public class SearchActivity extends AppCompatActivity implements SearchAdapter.SearchAdapterListener {

    private final List<String> mRecentSearchStringList = new ArrayList<>();
    private final Repository mRepository = new Repository(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivitySearchBinding binding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.autoCompleteTextView.requestFocus();

        //action bar
        setSupportActionBar(binding.searchToolbar);

        RecentSearchAdapter recentSearchAdapter = new RecentSearchAdapter();
        setRecentSearchLive(recentSearchAdapter, binding);
        recentSearchAdapterClick(binding, recentSearchAdapter);
        binding.recentSearchRecycler.setAdapter(recentSearchAdapter);

        AutoCompleteAdapter autoCompleteAdapter = new AutoCompleteAdapter(this, R.layout.item_auto_complete, R.id.auto_complete_text, getAutoCompleteList(mRepository));
        binding.autoCompleteTextView.setAdapter(autoCompleteAdapter);
        autoCompleteImeListener(binding);

        SearchViewPagerAdapter searchViewPagerAdapter = new SearchViewPagerAdapter(getSearchAdapters(mRepository));
        binding.viewPager.setAdapter(searchViewPagerAdapter);
        tabLayoutInit(binding);

        autoCompleteTextChangeListener(binding, searchViewPagerAdapter);
    }

    private void setRecentSearchLive(RecentSearchAdapter recentSearchAdapter, ActivitySearchBinding binding) {
        mRepository.getRecentSearchLive().observe(this, recentSearches -> {
            mRecentSearchStringList.clear();
            for (RecentSearch recentSearch : recentSearches)
                mRecentSearchStringList.add(recentSearch.getWord());
            recentSearchAdapter.submitList(recentSearches);
            if (recentSearches.size() == 0) binding.recentSearchNoData.setVisibility(View.VISIBLE);
            else binding.recentSearchNoData.setVisibility(View.GONE);

        });
    }

    private void recentSearchAdapterClick(ActivitySearchBinding binding, @NotNull RecentSearchAdapter recentSearchAdapter) {
        recentSearchAdapter.setRecentSearchAdapterListener(new RecentSearchAdapter.RecentSearchAdapterListener() {
            @Override
            public void recentSearchItemClick(String word) {
                binding.autoCompleteTextView.setText(word);
                binding.autoCompleteTextView.setSelection(word.length());
            }

            @Override
            public void recentSearchDeleteClick(RecentSearch recentSearch) {
                mRepository.deleteRecentSearch(recentSearch);
            }
        });
    }

    @NotNull
    private List<String> getAutoCompleteList(@NotNull Repository repository) {
        List<String> autoCompleteList = new ArrayList<>();
        for (String s : repository.getFolderNameList())
            if (!autoCompleteList.contains(s.trim()))
                autoCompleteList.add(s.trim());

        for (String s : repository.getBrandList())
            if (!autoCompleteList.contains(s.trim()))
                autoCompleteList.add(s.trim());

        for (String s : repository.getNameList())
            if (!autoCompleteList.contains(s.trim()))
                autoCompleteList.add(s.trim());
        return autoCompleteList;
    }

    private void tabLayoutInit(@NotNull ActivitySearchBinding binding) {
        new TabLayoutMediator(binding.tabLayout, binding.viewPager, (tab, position) -> {
            if (position == 0) tab.setText(TOP);
            else if (position == 1) tab.setText(BOTTOM);
            else if (position == 2) tab.setText(OUTER);
            else tab.setText(ETC);
        }).attach();
    }

    private void autoCompleteImeListener(@NotNull ActivitySearchBinding binding) {
        binding.autoCompleteTextView.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(binding.autoCompleteTextView.getWindowToken(), 0);

                String word = binding.autoCompleteTextView.getText().toString().trim();

                if (!mRecentSearchStringList.contains(word) && !TextUtils.isEmpty(word))
                    mRepository.insertRecentSearch(new RecentSearch(word, getCurrentDate()));
            }
            return true;
        });
    }

    @NotNull
    private List<SearchAdapter> getSearchAdapters(@NotNull Repository repository) {
        List<Object> topList = new ArrayList<>();
        List<Object> bottomList = new ArrayList<>();
        List<Object> outerList = new ArrayList<>();
        List<Object> etcList = new ArrayList<>();
        topList.addAll(repository.getAllFolderList2(TOP));
        topList.addAll(repository.getAllSizeList(TOP));
        bottomList.addAll(repository.getAllFolderList2(BOTTOM));
        bottomList.addAll(repository.getAllSizeList(BOTTOM));
        outerList.addAll(repository.getAllFolderList2(OUTER));
        outerList.addAll(repository.getAllSizeList(OUTER));
        etcList.addAll(repository.getAllFolderList2(ETC));
        etcList.addAll(repository.getAllSizeList(ETC));
        SearchAdapter topAdapter = new SearchAdapter(topList, this);
        SearchAdapter bottomAdapter = new SearchAdapter(bottomList, this);
        SearchAdapter outerAdapter = new SearchAdapter(outerList, this);
        SearchAdapter etcAdapter = new SearchAdapter(etcList, this);
        return Arrays.asList(topAdapter, bottomAdapter, outerAdapter, etcAdapter);
    }

    private void autoCompleteTextChangeListener(@NotNull ActivitySearchBinding binding, SearchViewPagerAdapter searchViewPagerAdapter) {
        binding.autoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s)) {
                    binding.searchEditTextLayout.setEndIconMode(TextInputLayout.END_ICON_CLEAR_TEXT);
                    binding.recentSearchLayout.setVisibility(View.GONE);
                    binding.viewPagerLayout.setVisibility(View.VISIBLE);
                } else {
                    binding.recentSearchLayout.setVisibility(View.VISIBLE);
                    binding.viewPagerLayout.setVisibility(View.GONE);
                }

                List<SearchAdapter> searchAdapterList = searchViewPagerAdapter.getSearchAdapterList();
                searchAdapterList.get(0).getFilter().filter(s);
                searchAdapterList.get(1).getFilter().filter(s);
                searchAdapterList.get(2).getFilter().filter(s);
                searchAdapterList.get(3).getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    @NotNull
    private String getCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM월 dd일", Locale.getDefault());
        return dateFormat.format(new Date(System.currentTimeMillis()));
    }

    @Override
    public void searchAdapterSizeClick(@NotNull Size size) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(SIZE_ID, size.getId());
        startActivity(intent);
    }

    @Override
    public void searchAdapterFolderClick(@NotNull Folder folder) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(FOLDER_ID, folder.getId());
        startActivity(intent);
    }
}