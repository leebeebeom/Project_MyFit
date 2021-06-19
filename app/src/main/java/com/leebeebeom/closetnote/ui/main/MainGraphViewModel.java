package com.leebeebeom.closetnote.ui.main;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.navigation.NavBackStackEntry;

import com.leebeebeom.closetnote.data.repository.CategoryRepository;
import com.leebeebeom.closetnote.data.repository.FolderRepository;
import com.leebeebeom.closetnote.data.repository.RecentSearchRepository;
import com.leebeebeom.closetnote.data.repository.SizeRepository;
import com.leebeebeom.closetnote.util.constant.RecentSearchType;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import lombok.Getter;
import lombok.experimental.Accessors;

@HiltViewModel
@Accessors(prefix = "m")
public class MainGraphViewModel extends ViewModel {
    @Getter
    private final MutableLiveData<NavBackStackEntry> mBackStackEntryLive = new MutableLiveData<>();
    private final RecentSearchRepository mRecentSearchRepository;
    @Getter
    private final FolderRepository mFolderRepository;
    private final SizeRepository mSizeRepository;
    @Getter
    private final CategoryRepository mCategoryRepository;

    @Inject
    public MainGraphViewModel(RecentSearchRepository recentSearchRepository, FolderRepository folderRepository, SizeRepository sizeRepository, CategoryRepository categoryRepository) {
        mRecentSearchRepository = recentSearchRepository;
        mFolderRepository = folderRepository;
        mSizeRepository = sizeRepository;
        mCategoryRepository = categoryRepository;
    }

    public void setBackStackEntryLive(NavBackStackEntry backStackEntry) {
        mBackStackEntryLive.setValue(backStackEntry);
    }

    public void recentSearchRepositoryDeleteAll(RecentSearchType type) {
        mRecentSearchRepository.deleteAll(type);
    }

    public void folderMove(long targetId, long[] selectedFolderIds) {
        mFolderRepository.move(targetId, selectedFolderIds);
    }

    public void sizeMove(long targetId, long[] selectedSizeIds) {
        mSizeRepository.move(targetId, selectedSizeIds);
    }

    public void categoriesDeleteOrRestore(long[] selectedItemIds) {
        mCategoryRepository.deleteOrRestore(selectedItemIds, mFolderRepository, mSizeRepository, 0);
    }

    public void foldersDeleteOrRestore(long[] selectedFolderIds) {
        mFolderRepository.deleteOrRestore(selectedFolderIds, mSizeRepository);
    }

    public void sizesDeleteOrRestore(long[] selectedSizeIds) {
        mSizeRepository.deleteOrRestore(selectedSizeIds);
    }

    public void sizeDelete(long id) {
        mSizeRepository.delete(id);
    }
}
