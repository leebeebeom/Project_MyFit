package com.example.project_myfit.search_activity;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.project_myfit.R;
import com.example.project_myfit.data.model.Folder;
import com.example.project_myfit.data.model.RecentSearch;
import com.example.project_myfit.data.model.Size;
import com.example.project_myfit.databinding.ActionModeTitleBinding;
import com.example.project_myfit.databinding.FragmentSearchBinding;
import com.example.project_myfit.dialog.DialogViewModel;
import com.example.project_myfit.search_activity.adapter.RecentSearchAdapter;
import com.example.project_myfit.search_activity.adapter.SearchAdapter;
import com.example.project_myfit.search_activity.adapter.SearchViewPagerAdapter;
import com.example.project_myfit.util.LockableScrollView;
import com.example.project_myfit.util.SelectedItemTreat;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputLayout;
import com.michaelflisar.dragselectrecyclerview.DragSelectTouchListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.example.project_myfit.util.MyFitConstant.ACTION_MODE_OFF;
import static com.example.project_myfit.util.MyFitConstant.ACTION_MODE_ON;
import static com.example.project_myfit.util.MyFitConstant.BOTTOM;
import static com.example.project_myfit.util.MyFitConstant.ETC;
import static com.example.project_myfit.util.MyFitConstant.FOLDER;
import static com.example.project_myfit.util.MyFitConstant.ITEM_MOVE_CONFIRM_CLICK;
import static com.example.project_myfit.util.MyFitConstant.NAME_EDIT_CONFIRM_CLICK;
import static com.example.project_myfit.util.MyFitConstant.OUTER;
import static com.example.project_myfit.util.MyFitConstant.RECENT_SEARCH_ALL_CLEAR_CONFIRM_CLICK;
import static com.example.project_myfit.util.MyFitConstant.SELECTED_ITEM_DELETE_CONFIRM_CLICK;
import static com.example.project_myfit.util.MyFitConstant.TOP;


public class SearchFragment extends Fragment implements SearchAdapter.SearchAdapterListener, SearchViewPagerAdapter.ViewPagerAutoScrollListener {
    private SearchViewModel mModel;
    private FragmentSearchBinding mBinding;
    private List<String> mRecentSearchStringList;
    private MaterialAutoCompleteTextView mAutoCompleteEditText;
    private ActionMode mActionMode;
    private boolean isDragSelecting, mAutoScrollEnable;
    private ActionModeTitleBinding mActionModeTitleBinding;
    private SearchViewPagerAdapter mSearchViewPagerAdapter;
    private MenuItem mEditMenu, mMoveMenu, mDeletedMenu;
    private DragSelectTouchListener mDragSelectListener;
    private SearchAdapter[] mSearchAdapterArray;
    private NavController mNavController;
    private DialogViewModel mDialogViewModel;
    private RecentSearchAdapter mRecentSearchAdapter;
    private TypedValue mColorControl;
    private final ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(@NotNull ActionMode mode, Menu menu) {
            mBinding.searchViewPager.setUserInputEnabled(false);

            mActionMode = mode;
            mModel.setActionModeOn(true);

            LinearLayout tab = (LinearLayout) mBinding.searchTabLayout.getChildAt(0);
            for (int i = 0; i < 4; i++) tab.getChildAt(i).setClickable(false);

            mode.getMenuInflater().inflate(R.menu.action_mode, menu);
            mode.setCustomView(mActionModeTitleBinding.getRoot());

            mSearchAdapterArray[mModel.getCurrentItem()].setActionModeState(ACTION_MODE_ON);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, @NotNull Menu menu) {
            mActionModeTitleBinding.actionModeCheckBox.setOnClickListener(v ->
                    mModel.selectAllClick(mActionModeTitleBinding.actionModeCheckBox.isChecked(), mSearchAdapterArray));

            mEditMenu = menu.getItem(0);
            mMoveMenu = menu.getItem(1);
            mDeletedMenu = menu.getItem(2);
            return true;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, @NotNull MenuItem item) {
            if (item.getItemId() == R.id.menuActionModeDelete)
                mNavController.navigate(SearchFragmentDirections.actionSearchFragmentToSearchSelectedItemDeleteDialog(mModel.getSelectedItemSizeLiveValue()));
            else if (item.getItemId() == R.id.menuActionModeMove) {
                mDialogViewModel.forTreeView(mModel.getSelectedFolderList(), mModel.getSelectedSizeList(), null);
                mNavController.navigate(SearchFragmentDirections.actionSearchFragmentToSearchTreeViewDialog(mModel.getParentCategory()));
            } else
                mNavController.navigate(SearchFragmentDirections.actionSearchFragmentToSearchNameEditDialog(mModel.getSelectedFolderId(), FOLDER, false));
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mBinding.searchViewPager.setUserInputEnabled(true);

            mActionMode = null;
            mModel.setActionModeOn(false);

            LinearLayout tab = (LinearLayout) mBinding.searchTabLayout.getChildAt(0);
            for (int i = 0; i < 4; i++) tab.getChildAt(i).setClickable(true);

            mSearchAdapterArray[mModel.getCurrentItem()].setActionModeState(ACTION_MODE_OFF);

            mActionModeTitleBinding.actionModeCheckBox.setChecked(false);
            ((ViewGroup) mActionModeTitleBinding.getRoot().getParent()).removeAllViews();
        }
    };

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mModel = new ViewModelProvider(requireActivity()).get(SearchViewModel.class);
        mNavController = NavHostFragment.findNavController(this);
        mDialogViewModel = new ViewModelProvider(mNavController.getViewModelStoreOwner(R.id.search_nav_gragh)).get(DialogViewModel.class);
        mColorControl = new TypedValue();
        requireContext().getTheme().resolveAttribute(R.attr.myColorControl, mColorControl, true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = FragmentSearchBinding.inflate(inflater);
        View view = mBinding.getRoot();
        mActionModeTitleBinding = ActionModeTitleBinding.inflate(inflater);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAutoCompleteEditText = requireActivity().findViewById(R.id.searchAutoCompleteEditText);

        mBinding.searchViewPager.setAdapter(getViewPagerAdapter());
        mBinding.searchViewPager.setOffscreenPageLimit(1);
        tabLayoutInit();
        mRecentSearchAdapter = new RecentSearchAdapter();
        mBinding.searchRecentSearchRecycler.setAdapter(mRecentSearchAdapter);

        setDialogLive();
        setRecentSearchLive();
        setActionModeTitleLive();
        setCombineLive();
    }

    @NotNull
    private SearchViewPagerAdapter getViewPagerAdapter() {
        mSearchAdapterArray = new SearchAdapter[4];
        for (int i = 0; i < 4; i++) mSearchAdapterArray[i] = new SearchAdapter(this);

        mSearchViewPagerAdapter = new SearchViewPagerAdapter(mSearchAdapterArray, getDragSelectListener(), this);
        return mSearchViewPagerAdapter;
    }

    private DragSelectTouchListener getDragSelectListener() {
        DragSelectTouchListener.OnAdvancedDragSelectListener listener = new DragSelectTouchListener.OnAdvancedDragSelectListener() {
            @Override
            public void onSelectionStarted(int i) {
                mBinding.searchViewPagerScrollView.setScrollable(false);
                isDragSelecting = true;

                SearchViewPagerAdapter.ViewPagerVH viewPagerVH = (SearchViewPagerAdapter.ViewPagerVH)
                        ((RecyclerView) mBinding.searchViewPager.getChildAt(0)).findViewHolderForAdapterPosition(mModel.getCurrentItem());
                if (viewPagerVH != null) {
                    RecyclerView recyclerView = viewPagerVH.getBinding().itemSearchRecyclerView;
                    RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForLayoutPosition(i);
                    if (viewHolder != null) viewHolder.itemView.callOnClick();
                }
            }

            @Override
            public void onSelectionFinished(int i) {
                mBinding.searchViewPagerScrollView.setScrollable(true);
                isDragSelecting = false;
            }

            @Override
            public void onSelectChange(int i, int i1, boolean b) {
                SearchViewPagerAdapter.ViewPagerVH viewPagerVH = (SearchViewPagerAdapter.ViewPagerVH)
                        ((RecyclerView) mBinding.searchViewPager.getChildAt(0)).findViewHolderForAdapterPosition(mModel.getCurrentItem());
                if (viewPagerVH != null) {
                    RecyclerView recyclerView = viewPagerVH.getBinding().itemSearchRecyclerView;
                    for (int j = i; j <= i1; j++) {
                        RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForLayoutPosition(j);
                        if (viewHolder != null) viewHolder.itemView.callOnClick();
                    }
                }
            }
        };
        mDragSelectListener = new DragSelectTouchListener().withSelectListener(listener);
        return mDragSelectListener;
    }

    private void tabLayoutInit() {
        new TabLayoutMediator(mBinding.searchTabLayout, mBinding.searchViewPager, (tab, position) -> {
            if (position == 0) tab.setText(TOP);
            else if (position == 1) tab.setText(BOTTOM);
            else if (position == 2) tab.setText(OUTER);
            else tab.setText(ETC);
        }).attach();
    }

    private void setDialogLive() {
        SelectedItemTreat selectedItemTreat = new SelectedItemTreat(requireContext());

        mDialogViewModel.getBackStackEntryLive().observe(getViewLifecycleOwner(), navBackStackEntry -> {
            selectedItemDeleteLive(selectedItemTreat, navBackStackEntry);
            itemMoveDialogLive(selectedItemTreat, navBackStackEntry);
            nameEditDialogLive(navBackStackEntry);
            recentSearchAllClearDialogLive(navBackStackEntry);
        });
    }

    private void selectedItemDeleteLive(SelectedItemTreat selectedItemTreat, @NotNull NavBackStackEntry navBackStackEntry) {
        navBackStackEntry.getSavedStateHandle().getLiveData(SELECTED_ITEM_DELETE_CONFIRM_CLICK).observe(navBackStackEntry, o -> {
            selectedItemTreat.folderSizeDelete(true, mModel.getSelectedFolderList(), mModel.getSelectedSizeList());
            if (mActionMode != null) mActionMode.finish();
        });
    }

    private void itemMoveDialogLive(SelectedItemTreat selectedItemTreat, @NotNull NavBackStackEntry navBackStackEntry) {
        navBackStackEntry.getSavedStateHandle().getLiveData(ITEM_MOVE_CONFIRM_CLICK).observe(navBackStackEntry, o -> {
            long parentId = (long) o;
            selectedItemTreat.folderSizeMove(true, parentId, mModel.getSelectedFolderList(), mModel.getSelectedSizeList());
            if (mActionMode != null) mActionMode.finish();
        });
    }

    private void nameEditDialogLive(@NotNull NavBackStackEntry navBackStackEntry) {
        navBackStackEntry.getSavedStateHandle().getLiveData(NAME_EDIT_CONFIRM_CLICK).observe(navBackStackEntry, o -> {
            if (mActionMode != null) mActionMode.finish();
        });
    }

    private void recentSearchAllClearDialogLive(@NotNull NavBackStackEntry navBackStackEntry) {
        navBackStackEntry.getSavedStateHandle().getLiveData(RECENT_SEARCH_ALL_CLEAR_CONFIRM_CLICK).observe(navBackStackEntry,
                o -> mModel.deleteAllRecentSearch());
    }

    private void setRecentSearchLive() {
        mModel.getAllRecentSearchLive().observe(getViewLifecycleOwner(), recentSearchList -> {
            mRecentSearchAdapter.submitList(recentSearchList);

            mRecentSearchStringList = new ArrayList<>();
            for (RecentSearch recentSearch : recentSearchList)
                mRecentSearchStringList.add(recentSearch.getWord());

            if (recentSearchList.isEmpty()) mBinding.searchRecentSearchNoDataLayout.setVisibility(View.VISIBLE);
            else mBinding.searchRecentSearchNoDataLayout.setVisibility(View.GONE);
        });
    }

    private void setActionModeTitleLive() {
        mModel.getSelectedItemSizeLive().observe(getViewLifecycleOwner(), integer -> {
            String title = integer + getString(R.string.item_selected);
            mActionModeTitleBinding.actionModeTitle.setText(title);

            if (mActionMode != null) {
                mEditMenu.setVisible(integer == 1 && mModel.getSelectedItem().get(0) instanceof Folder);
                mMoveMenu.setVisible(integer > 0);
                mDeletedMenu.setVisible(integer > 0);
            }

            int allItemSize = mSearchAdapterArray[mBinding.searchViewPager.getCurrentItem()].getItemCount();
            mActionModeTitleBinding.actionModeCheckBox.setChecked(allItemSize == integer);
        });
    }

    private void setCombineLive() {
        LiveData<List<Folder>> folderLiveData = mModel.getAllFolderLive();
        LiveData<List<Size>> sizeLiveData = mModel.getAllSizeLive();

        MediatorLiveData<Object> mediatorLiveData = new MediatorLiveData<>();
        mediatorLiveData.addSource(folderLiveData, mediatorLiveData::setValue);
        mediatorLiveData.addSource(sizeLiveData, mediatorLiveData::setValue);
        mediatorLiveData.observe(getViewLifecycleOwner(), o -> {
            if (o instanceof List<?>)
                if (((List<?>) o).get(0) instanceof Folder)
                    folderChangedValue(sizeLiveData, (Collection<?>) o);
                else if (((List<?>) o).get(0) instanceof Size)
                    sizeChangedValue(folderLiveData, (Collection<?>) o);
        });
    }

    private void sizeChangedValue(@NotNull LiveData<List<Folder>> folderLiveData, Collection<?> o) {
        List<Object> allItemList = new ArrayList<>(o);
        if (folderLiveData.getValue() != null)
            allItemList.addAll(0, folderLiveData.getValue());
        mSearchViewPagerAdapter.setItems(allItemList, mAutoCompleteEditText.getText(), mBinding.searchTabLayout, mColorControl);
    }

    private void folderChangedValue(@NotNull LiveData<List<Size>> sizeLiveData, Collection<?> o) {
        List<Object> allItemList = new ArrayList<>(o);
        if (sizeLiveData.getValue() != null)
            allItemList.addAll(sizeLiveData.getValue());
        mSearchViewPagerAdapter.setItems(allItemList, mAutoCompleteEditText.getText(), mBinding.searchTabLayout, mColorControl);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setMainScrollChangeListener();
        setRecentSearchScrollListener();
        fabClickLister();
        viewPagerChangeListener();
        recentSearchAdapterClickListener();
        autoCompleteImeListener();
        autoCompleteTextChangeListener();
        recentSearchAllClearClickListener();

        actionModeRecreate();
    }

    private void setMainScrollChangeListener() {
        mBinding.searchViewPagerScrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (v.getScrollY() != 0) mBinding.searchTopFab.show();
            else mBinding.searchTopFab.hide();

            if (isDragSelecting && mAutoScrollEnable && scrollY > oldScrollY)
                v.postDelayed(() -> v.scrollBy(0, 1), 50);
            else if (isDragSelecting && mAutoScrollEnable && scrollY < oldScrollY)
                v.postDelayed(() -> v.scrollBy(0, -1), 50);
        });
    }

    private void setRecentSearchScrollListener() {
        mBinding.searchRecentSearchScrollView.setOnScrollChangeListener((View.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (v.getScrollY() != 0) mBinding.searchTopFab.show();
            else mBinding.searchTopFab.hide();
        });
    }

    private void fabClickLister() {
        mBinding.searchTopFab.setOnClickListener(v -> {
            NestedScrollView recentSearchScrollView = (LockableScrollView) mBinding.searchRecentSearchScrollView;
            LockableScrollView viewPagerScrollView = mBinding.searchViewPagerScrollView;

            if (recentSearchScrollView.getVisibility() != View.VISIBLE) {
                viewPagerScrollView.scrollTo(0, 0);
                viewPagerScrollView.setOnScrollChangeListener((View.OnScrollChangeListener) (v1, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                    if (scrollY != 0) viewPagerScrollView.scrollTo(0, 0);
                    else {
                        viewPagerScrollView.setOnScrollChangeListener((View.OnScrollChangeListener) null);
                        setMainScrollChangeListener();
                    }
                });
            } else if (recentSearchScrollView.getVisibility() == View.VISIBLE) {
                recentSearchScrollView.scrollTo(0, 0);
                recentSearchScrollView.setOnScrollChangeListener((View.OnScrollChangeListener) (v1, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                    if (scrollY != 0) recentSearchScrollView.scrollTo(0, 0);
                    else {
                        recentSearchScrollView.setOnScrollChangeListener((View.OnScrollChangeListener) null);
                        setRecentSearchScrollListener();
                    }
                });
            }
        });
    }

    private void viewPagerChangeListener() {
        mBinding.searchViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                mBinding.searchViewPagerScrollView.smoothScrollTo(0, 0);
                mModel.setCurrentItem(position);
            }
        });
    }

    private void recentSearchAdapterClickListener() {
        mRecentSearchAdapter.setRecentSearchAdapterListener(new RecentSearchAdapter.RecentSearchAdapterListener() {
            @Override
            public void recentSearchItemClick(String word) {
                mAutoCompleteEditText.setText(word);
                mAutoCompleteEditText.setSelection(word.length());
                mAutoCompleteEditText.dismissDropDown();
                mModel.deleteOverLapRecentSearch(word);
                mModel.insertRecentSearch(word);
                InputMethodManager inputMethodManager = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(mAutoCompleteEditText.getWindowToken(), 0);
            }

            @Override
            public void recentSearchDeleteClick(RecentSearch recentSearch) {
                mModel.deleteRecentSearch(recentSearch);
            }
        });
    }

    private void autoCompleteImeListener() {
        mAutoCompleteEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                mAutoCompleteEditText.dismissDropDown();

                String word = mAutoCompleteEditText.getText().toString().trim();
                //검색어 중복시 지우고 맨 위로
                if (!TextUtils.isEmpty(word)) {
                    if (mRecentSearchStringList.contains(word))
                        mModel.deleteOverLapRecentSearch(word);
                    mModel.insertRecentSearch(word);
                }
            }
            return true;
        });
    }

    private void autoCompleteTextChangeListener() {
        TextInputLayout autoCompleteTextLayout = requireActivity().findViewById(R.id.searchAutoCompleteLayout);

        mAutoCompleteEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s)) {
                    mBinding.searchRecentSearchScrollView.setVisibility(View.GONE);
                    if (mBinding.searchViewPagerScrollView.getScrollY() == 0)
                        mBinding.searchTopFab.setVisibility(View.GONE);
                    else mBinding.searchTopFab.setVisibility(View.VISIBLE);
                    autoCompleteTextLayout.setEndIconVisible(true);
                } else {
                    mBinding.searchRecentSearchScrollView.scrollTo(0, 0);
                    mBinding.searchRecentSearchScrollView.setVisibility(View.VISIBLE);
                    mBinding.searchTopFab.setVisibility(View.GONE);
                    autoCompleteTextLayout.setEndIconVisible(false);
                }

                for (int i = 0; i < 4; i++) {
                    int finalI = i;
                    mSearchAdapterArray[i].getFilter().filter(mAutoCompleteEditText.getText(), count1 -> {
                        TabLayout.Tab tab = mBinding.searchTabLayout.getTabAt(finalI);
                        if (tab != null) {
                            if (count1 == 0) tab.removeBadge();
                            else {
                                BadgeDrawable badge = tab.getOrCreateBadge();
                                badge.setVisible(true);
                                badge.setNumber(count1);
                                badge.setBackgroundColor(mColorControl.data);
                            }
                        }
                    });
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void recentSearchAllClearClickListener() {
        mBinding.searchRecentSearchAllClearText.setOnClickListener(v ->
                mNavController.navigate(SearchFragmentDirections.actionSearchFragmentToRecentSearchAllClearDialog()));
    }

    private void actionModeRecreate() {
        if (mModel.isActionModeOn()) {
            mSearchAdapterArray[mModel.getCurrentItem()].setSelectedItem(mModel.getSelectedItem());
            ((AppCompatActivity) requireActivity()).startSupportActionMode(mActionModeCallback);
        }
    }

    @Override
    public void searchAdapterSizeClick(@NotNull Size size, MaterialCheckBox checkBox) {
        if (mActionMode == null)
            mNavController.navigate(SearchFragmentDirections.actionSearchFragmentToMainActivity(0, size.getId(), size.getParentId()));
        else {
            checkBox.setChecked(!checkBox.isChecked());
            mModel.sizeSelected(size, checkBox.isChecked(), mSearchAdapterArray[mBinding.searchViewPager.getCurrentItem()]);
        }
    }

    @Override
    public void searchAdapterSizeLongClick(int position) {
        if (mActionMode == null) {
            mModel.getSelectedItem().clear();
            ((AppCompatActivity) requireActivity()).startSupportActionMode(mActionModeCallback);
        }
        mDragSelectListener.startDragSelection(position);
    }

    @Override
    public void searchAdapterFavoriteClick(Size size) {
        mModel.sizeFavoriteClick(size);
    }

    @Override
    public void searchAdapterFolderClick(@NotNull Folder folder, MaterialCheckBox checkBox) {
        if (mActionMode == null)
            mNavController.navigate(SearchFragmentDirections.actionSearchFragmentToMainActivity(
                    folder.getId(), 0, folder.getParentId()));
        else {
            checkBox.setChecked(!checkBox.isChecked());
            mModel.folderSelected(folder, checkBox.isChecked(), mSearchAdapterArray[mBinding.searchViewPager.getCurrentItem()]);
        }
    }

    @Override
    public void searchAdapterFolderLongClick(int position) {
        if (mActionMode == null) {
            mModel.getSelectedItem().clear();
            ((AppCompatActivity) requireActivity()).startSupportActionMode(mActionModeCallback);
        }
        mDragSelectListener.startDragSelection(position);
    }

    @Override
    public void dragAutoScroll(int upDown) {
        if (isDragSelecting)
            if (upDown == 0) {
                mBinding.searchViewPagerScrollView.scrollBy(0, 1);
                mAutoScrollEnable = true;
            } else if (upDown == 1) {
                mBinding.searchViewPagerScrollView.scrollBy(0, -1);
                mAutoScrollEnable = true;
            } else if (upDown == 2) {
                mBinding.searchViewPagerScrollView.scrollBy(0, 0);
                mAutoScrollEnable = false;
            }
    }
}