package com.example.project_myfit.searchActivity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.project_myfit.R;
import com.example.project_myfit.Repository;
import com.example.project_myfit.databinding.ActivitySearchBinding;
import com.example.project_myfit.searchActivity.adapter.AutoCompleteAdapter;
import com.example.project_myfit.ui.main.database.Category;
import com.example.project_myfit.ui.main.listfragment.database.Folder;
import com.example.project_myfit.ui.main.listfragment.database.Size;

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
        getSupportFragmentManager().beginTransaction().add(R.id.search_fragment_layout, new SearchFragment()).commit();

        AutoCompleteAdapter autoCompleteAdapter = new AutoCompleteAdapter(this, R.layout.item_auto_complete, R.id.auto_complete_text, getAutoCompleteList(mRepository));
        binding.autoCompleteTextView.setAdapter(autoCompleteAdapter);
    }

    @NotNull
    private List<String> getAutoCompleteList(@NotNull Repository repository) {
        List<Folder> folderList = new ArrayList<>();
        List<Size> sizeList = new ArrayList<>();
        List<String> autoCompleteList = new ArrayList<>();

        folderListing(folderList, repository);
        sizeListing(sizeList, repository);
        List<String> folderNameList = new ArrayList<>();
        List<String> sizeBrandNameList = new ArrayList<>();
        for (Folder f : folderList)
            folderNameList.add(f.getFolderName().trim());
        for (Size s : sizeList) {
            sizeBrandNameList.add(s.getBrand().trim());
            sizeBrandNameList.add(s.getName().trim());
        }

        for (String s : folderNameList)
            if (!autoCompleteList.contains(s.trim()))
                autoCompleteList.add(s.trim());

        for (String s : sizeBrandNameList)
            if (!autoCompleteList.contains(s.trim()))
                autoCompleteList.add(s.trim());

        autoCompleteList.sort(String::compareTo);

        return autoCompleteList;
    }

    private void sizeListing(List<Size> sizeList, @NotNull Repository repository) {
        List<Size> allSizeList = repository.getAllSize();
        for (Size s : allSizeList) {
            Category parentCategory = repository.getCategory(s.getFolderId());
            if (parentCategory != null && parentCategory.getIsDeleted() == 0) sizeList.add(s);
            else if (parentCategory == null) {
                Folder parentFolder = repository.getFolder(s.getFolderId());
                if (parentFolder.getIsDeleted() == 0)
                    checkParentIsDeleted(parentFolder, s, sizeList, repository);
            }
        }
    }

    private void folderListing(List<Folder> folderList, @NotNull Repository repository) {
        List<Folder> allFolderList = repository.getAllFolder();
        for (Folder f : allFolderList) {
            Category parentCategory = repository.getCategory(f.getFolderId());
            if (parentCategory != null && parentCategory.getIsDeleted() == 0) folderList.add(f);
            else if (parentCategory == null) {
                Folder parentFolder = repository.getFolder(f.getFolderId());
                if (parentFolder.getIsDeleted() == 0)
                    checkParentIsDeleted(parentFolder, f, folderList, repository);
            }
        }
    }

    private void checkParentIsDeleted(@NotNull Folder parentFolder, Folder folder, List<Folder> folderList, @NotNull Repository repository) {
        Folder parentFolder2 = repository.getFolder(parentFolder.getFolderId());
        if (parentFolder2 == null) {
            Category category = repository.getCategory(parentFolder.getFolderId());
            if (category.getIsDeleted() == 0) folderList.add(folder);
        } else {
            if (parentFolder2.getIsDeleted() == 0) {
                checkParentIsDeleted(parentFolder2, folder, folderList, repository);
            }
        }
    }

    private void checkParentIsDeleted(@NotNull Folder parentFolder, Size size, List<Size> sizeList, @NotNull Repository repository) {
        Folder parentFolder2 = repository.getFolder(parentFolder.getFolderId());
        if (parentFolder2 == null) {
            Category category = repository.getCategory(parentFolder.getFolderId());
            if (category.getIsDeleted() == 0) sizeList.add(size);
        } else {
            if (parentFolder2.getIsDeleted() == 0) {
                checkParentIsDeleted(parentFolder2, size, sizeList, repository);
            }
        }
    }
}