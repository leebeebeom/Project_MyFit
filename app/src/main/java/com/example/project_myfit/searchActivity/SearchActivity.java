package com.example.project_myfit.searchActivity;

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
import com.example.project_myfit.data.model.Folder;
import com.example.project_myfit.data.model.Size;
import com.example.project_myfit.databinding.ActivitySearchBinding;
import com.example.project_myfit.searchActivity.adapter.AutoCompleteAdapter;
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
        LiveData<List<Folder>> folderLiveData = model.getAllFolderLive();
        LiveData<List<Size>> sizeLiveData = model.getAllSizeLive();

        MediatorLiveData<Object> mediatorLiveData = new MediatorLiveData<>();
        mediatorLiveData.addSource(folderLiveData, mediatorLiveData::setValue);
        mediatorLiveData.addSource(sizeLiveData, mediatorLiveData::setValue);

        mediatorLiveData.observe(this, o -> {
            List<String> autoCompleteList = new ArrayList<>();

            if (o instanceof List<?>){
                if (((List<?>) o).get(0) instanceof Folder)
                    folderChangedValue(sizeLiveData, (List<?>) o, autoCompleteList);
                else if (((List<?>) o).get(0) instanceof Size)
                    sizeChangedValue(folderLiveData, (List<?>) o, autoCompleteList);
            }
            autoCompleteList.sort(String::compareTo);
            autoCompleteAdapter.setItem(autoCompleteList);
        });
    }

    private void folderChangedValue(LiveData<List<Size>> sizeLiveData, @NotNull List<?> objects, List<String> autoCompleteList) {
        for (Object o : objects)
            if (o instanceof Folder && !autoCompleteList.contains(((Folder) o).getFolderName().trim()))
                autoCompleteList.add(((Folder) o).getFolderName().trim());

        if (sizeLiveData.getValue() != null) {
            List<Size> sizeList = sizeLiveData.getValue();
            for (Size size : sizeList) {
                if (!autoCompleteList.contains(size.getBrand().trim()))
                    autoCompleteList.add(size.getBrand().trim());
                if (!autoCompleteList.contains(size.getName().trim()))
                    autoCompleteList.add(size.getName().trim());
            }
        }
    }

    private void sizeChangedValue(@NotNull LiveData<List<Folder>> folderLiveData, List<?> objects, List<String> autoCompleteList) {
        if (folderLiveData.getValue() != null) {
            List<Folder> folderList = folderLiveData.getValue();
            for (Folder folder : folderList)
                if (!autoCompleteList.contains(folder.getFolderName().trim()))
                    autoCompleteList.add(folder.getFolderName().trim());
        }

        for (Object o : objects) {
            if (o instanceof Size) {
                if (!autoCompleteList.contains(((Size) o).getBrand().trim()))
                    autoCompleteList.add(((Size) o).getBrand().trim());
                if (!autoCompleteList.contains(((Size) o).getName().trim()))
                    autoCompleteList.add(((Size) o).getName().trim());
            }
        }
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