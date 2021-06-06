package com.example.myfit.ui.main;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.example.myfit.R;
import com.example.myfit.data.repository.AutoCompleteRepository;

import java.util.Set;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import lombok.Getter;
import lombok.experimental.Accessors;

@HiltViewModel
@Accessors(prefix = "m")
public class MainActivityViewModel extends ViewModel {
    private final AutoCompleteRepository mAutoCompleteRepository;
    @Getter
    private final MutableLiveData<Boolean> mKeyboardShowingLive = new MutableLiveData<>(false);
    @Getter
    private final MutableLiveData<Integer> mDestinationIdLive = new MutableLiveData<>();
    @Getter
    private final LiveData<Set<String>> mAutoCompleteLive;

    @Inject
    public MainActivityViewModel(AutoCompleteRepository autoCompleteRepository) {
        this.mAutoCompleteRepository = autoCompleteRepository;

        mAutoCompleteLive = Transformations.switchMap(mDestinationIdLive, id -> {
            if (id == R.id.searchFragment)
                return mAutoCompleteRepository.getAutoCompleteWordsLive();
            else if (id == R.id.recycleBinSearch)
                return mAutoCompleteRepository.getDeletedAutoCompleteWordsLive();
            else return null;
        });
    }
}
