package com.example.myfit.ui.dialog.edit.folder;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.Transformations;

import com.example.myfit.data.repository.FolderRepository;
import com.example.myfit.ui.dialog.edit.BaseEditViewModel;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class EditFolderNameViewModel extends BaseEditViewModel {
    public static final String EDIT_FOLDER_NAME = "edit folder name";
    private final SavedStateHandle savedStateHandle;
    private final FolderRepository folderRepository;
    private String name;
    private final MediatorLiveData<Boolean> isExistingMutable = new MediatorLiveData<>();
    private long id, parentId;

    @Inject
    public EditFolderNameViewModel(@NotNull SavedStateHandle savedStateHandle, FolderRepository folderRepository) {
        this.savedStateHandle = savedStateHandle;
        this.folderRepository = folderRepository;

        LiveData<Boolean> isExistingLive = Transformations.switchMap(savedStateHandle.getLiveData(EDIT_FOLDER_NAME),
                name -> folderRepository.isExistingName((String) name, parentId));
        isExistingMutable.addSource(isExistingLive, isExistingMutable::setValue);
    }

    @Override
    public MutableLiveData<Boolean> getIsExistingLive() {
        return isExistingMutable;
    }

    @Override
    public void queryIsExistingName(long id, String inputText, byte parentIndex) {

    }

    @Override
    public void queryIsExistingName(long id, String inputText, long parentId) {
        this.id = id;
        this.name = inputText;
        this.parentId = parentId;
        savedStateHandle.set(EDIT_FOLDER_NAME, name);
    }

    @Override
    public void update() {
        folderRepository.update(id, name);
    }
}
