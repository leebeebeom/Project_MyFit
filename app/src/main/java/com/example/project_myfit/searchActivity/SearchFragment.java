package com.example.project_myfit.searchActivity;

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
import com.example.project_myfit.searchActivity.adapter.RecentSearchAdapter;
import com.example.project_myfit.searchActivity.adapter.SearchAdapter;
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
import java.util.List;

import static com.example.project_myfit.util.MyFitConstant.ACTION_MODE_OFF;
import static com.example.project_myfit.util.MyFitConstant.ACTION_MODE_ON;
import static com.example.project_myfit.util.MyFitConstant.BOTTOM;
import static com.example.project_myfit.util.MyFitConstant.ETC;
import static com.example.project_myfit.util.MyFitConstant.FOLDER;
import static com.example.project_myfit.util.MyFitConstant.ITEM_MOVE_CONFIRM_CLICK;
import static com.example.project_myfit.util.MyFitConstant.NAME_EDIT_CONFIRM_CLICK;
import static com.example.project_myfit.util.MyFitConstant.OUTER;
import static com.example.project_myfit.util.MyFitConstant.SELECTED_ITEM_DELETE_CONFIRM_CLICK;
import static com.example.project_myfit.util.MyFitConstant.TOP;

//TODO 위치 표시

//TODO 아이콘
//TODO 하트
//TODO 폴더 아이템 갯수
//TODO 트리뷰 회색되는거 안되게
//TODO 서치 누르면 오토컴플리트 안나오게
//TODO 애니메이션 변경
//TODO 인풋아웃풋 브랜드 리스트
//TODO 액션모드시 키보드 숨기기
//TODO 바인드 될때 잠깐 나왔다 사라지ㄴ는거 수정
//TODO 최근 검색 전체삭제 추가

public class SearchFragment extends Fragment implements SearchAdapter.SearchAdapterListener, SearchViewPagerAdapter.SearchDragAutoScrollListener {
    private SearchViewModel mModel;
    private FragmentSearchBinding mBinding;
    private List<String> mRecentSearchStringList;
    private MaterialAutoCompleteTextView mAutoCompleteTextView;
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
            mBinding.viewPager.setUserInputEnabled(false);

            mActionMode = mode;
            mModel.setActionModeOn(true);

            LinearLayout tab = (LinearLayout) mBinding.tabLayout.getChildAt(0);
            for (int i = 0; i < 4; i++) tab.getChildAt(i).setClickable(false);

            mode.getMenuInflater().inflate(R.menu.action_mode, menu);
            mode.setCustomView(mActionModeTitleBinding.getRoot());

            mSearchAdapterArray[mModel.getCurrentItem()].setActionModeState(ACTION_MODE_ON);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, @NotNull Menu menu) {
            mActionModeTitleBinding.actionModeSelectAll.setOnClickListener(v ->
                    mModel.selectAllClick(mActionModeTitleBinding.actionModeSelectAll.isChecked(), mSearchAdapterArray));

            mEditMenu = menu.getItem(0);
            mMoveMenu = menu.getItem(1);
            mDeletedMenu = menu.getItem(2);
            return true;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, @NotNull MenuItem item) {
            if (item.getItemId() == R.id.action_mode_del)
                mNavController.navigate(SearchFragmentDirections.actionSearchFragmentToSearchSelectedItemDeleteDialog(mModel.getSelectedItemSize()));
            else if (item.getItemId() == R.id.action_mode_move) {
                mDialogViewModel.forTreeView(mModel.getSelectedFolderList(), mModel.getSelectedSizeList(), null);
                mNavController.navigate(SearchFragmentDirections.actionSearchFragmentToSearchTreeViewDialog(mModel.getParentCategory()));
            } else
                mNavController.navigate(SearchFragmentDirections.actionSearchFragmentToSearchNameEditDialog(mModel.getSelectedFolderId(), FOLDER, false));
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mBinding.viewPager.setUserInputEnabled(true);

            mActionMode = null;
            mModel.setActionModeOn(false);

            LinearLayout tab = (LinearLayout) mBinding.tabLayout.getChildAt(0);
            for (int i = 0; i < 4; i++) tab.getChildAt(i).setClickable(true);

            mSearchAdapterArray[mModel.getCurrentItem()].setActionModeState(ACTION_MODE_OFF);

            mActionModeTitleBinding.actionModeSelectAll.setChecked(false);
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
        mAutoCompleteTextView = requireActivity().findViewById(R.id.auto_complete_text_view);

        mBinding.viewPager.setAdapter(getViewPagerAdapter());
        mBinding.viewPager.setOffscreenPageLimit(1);
        tabLayoutInit();
        mRecentSearchAdapter = new RecentSearchAdapter();
        mBinding.recentSearchRecycler.setAdapter(mRecentSearchAdapter);

        setDialogLive();
        setRecentSearchLive();
        setActionModeTitleLive();

        mModel.getAllFolderLive().observe(getViewLifecycleOwner(), folderList -> setSearchAdapterData(folderList, null));
        mModel.getAllSizeLive().observe(getViewLifecycleOwner(), sizeList -> setSearchAdapterData(null, sizeList));
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
                mBinding.viewPagerLayout.setScrollable(false);
                isDragSelecting = true;

                SearchViewPagerAdapter.SearchViewPagerVH viewPagerVH = (SearchViewPagerAdapter.SearchViewPagerVH)
                        ((RecyclerView) mBinding.viewPager.getChildAt(0)).findViewHolderForAdapterPosition(mModel.getCurrentItem());
                if (viewPagerVH != null) {
                    RecyclerView recyclerView = viewPagerVH.getBinding().searchRecyclerView;
                    RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForLayoutPosition(i);
                    if (viewHolder != null) viewHolder.itemView.callOnClick();
                }
            }

            @Override
            public void onSelectionFinished(int i) {
                mBinding.viewPagerLayout.setScrollable(true);
                isDragSelecting = false;
            }

            @Override
            public void onSelectChange(int i, int i1, boolean b) {
                SearchViewPagerAdapter.SearchViewPagerVH viewPagerVH = (SearchViewPagerAdapter.SearchViewPagerVH)
                        ((RecyclerView) mBinding.viewPager.getChildAt(0)).findViewHolderForAdapterPosition(mModel.getCurrentItem());
                if (viewPagerVH != null) {
                    RecyclerView recyclerView = viewPagerVH.getBinding().searchRecyclerView;
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
        new TabLayoutMediator(mBinding.tabLayout, mBinding.viewPager, (tab, position) -> {
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
            long folderId = (long) o;
            selectedItemTreat.folderSizeMove(true, folderId, mModel.getSelectedFolderList(), mModel.getSelectedSizeList());
            if (mActionMode != null) mActionMode.finish();
        });
    }

    private void nameEditDialogLive(@NotNull NavBackStackEntry navBackStackEntry) {
        navBackStackEntry.getSavedStateHandle().getLiveData(NAME_EDIT_CONFIRM_CLICK).observe(navBackStackEntry, o -> {
            if (mActionMode != null) mActionMode.finish();
        });
    }

    private void setRecentSearchLive() {
        mModel.getAllRecentSearchLive().observe(getViewLifecycleOwner(), recentSearchList -> {
            mRecentSearchAdapter.submitList(recentSearchList);

            mRecentSearchStringList = new ArrayList<>();
            for (RecentSearch recentSearch : recentSearchList)
                mRecentSearchStringList.add(recentSearch.getWord());

            if (recentSearchList.isEmpty()) mBinding.recentSearchNoData.setVisibility(View.VISIBLE);
            else mBinding.recentSearchNoData.setVisibility(View.GONE);
        });
    }

    private void setActionModeTitleLive() {
        mModel.getSelectedAmount().observe(getViewLifecycleOwner(), integer -> {
            String title = integer + getString(R.string.item_selected);
            mActionModeTitleBinding.actionModeTitle.setText(title);

            if (mActionMode != null) {
                mEditMenu.setVisible(integer == 1 && mModel.getSelectedItem().get(0) instanceof Folder);
                mMoveMenu.setVisible(integer > 0);
                mDeletedMenu.setVisible(integer > 0);
            }

            int allItemSize = mSearchAdapterArray[mBinding.viewPager.getCurrentItem()].getItemCount();
            mActionModeTitleBinding.actionModeSelectAll.setChecked(allItemSize == integer);
        });
    }

    private void setSearchAdapterData(@Nullable List<Folder> allFolderList, @Nullable List<Size> allSizeList) {
        if (allFolderList == null) allFolderList = mModel.getAllFolderList();
        if (allSizeList == null) allSizeList = mModel.getAllSizeList();

        mSearchViewPagerAdapter.setItems(allFolderList, allSizeList, mAutoCompleteTextView.getText(), mBinding.tabLayout, mColorControl);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setMainScrollChangeListener();
        setRecentSearchScrollListener();
        fabClickLister();
        viewPagerChangeListener();
        recentSearchAdapterClick();
        autoCompleteImeListener();
        autoCompleteTextChangeListener();


        actionModeRecreate();
    }

    private void setMainScrollChangeListener() {
        mBinding.viewPagerLayout.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (v.getScrollY() != 0) mBinding.searchFab.show();
            else mBinding.searchFab.hide();

            if (isDragSelecting && mAutoScrollEnable && scrollY > oldScrollY)
                v.postDelayed(() -> v.scrollBy(0, 1), 50);
            else if (isDragSelecting && mAutoScrollEnable && scrollY < oldScrollY)
                v.postDelayed(() -> v.scrollBy(0, -1), 50);
        });
    }

    private void setRecentSearchScrollListener() {
        mBinding.recentSearchLayout.setOnScrollChangeListener((View.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (v.getScrollY() != 0) mBinding.searchFab.show();
            else mBinding.searchFab.hide();
        });
    }

    private void fabClickLister() {
        mBinding.searchFab.setOnClickListener(v -> {
            if (mBinding.recentSearchLayout.getVisibility() != View.VISIBLE) {
                mBinding.viewPagerLayout.scrollTo(0, 0);
                mBinding.viewPagerLayout.setOnScrollChangeListener((View.OnScrollChangeListener) (v1, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                    if (scrollY != 0) mBinding.viewPagerLayout.scrollTo(0, 0);
                    else {
                        mBinding.viewPagerLayout.setOnScrollChangeListener((View.OnScrollChangeListener) null);
                        setMainScrollChangeListener();
                    }
                });
            } else if (mBinding.recentSearchLayout.getVisibility() == View.VISIBLE) {
                mBinding.recentSearchLayout.scrollTo(0, 0);
                mBinding.recentSearchLayout.setOnScrollChangeListener((View.OnScrollChangeListener) (v1, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                    if (scrollY != 0) mBinding.recentSearchLayout.scrollTo(0, 0);
                    else {
                        mBinding.recentSearchLayout.setOnScrollChangeListener((View.OnScrollChangeListener) null);
                        setRecentSearchScrollListener();
                    }
                });
            }
        });
    }

    private void viewPagerChangeListener() {
        mBinding.viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                mBinding.viewPagerLayout.smoothScrollTo(0, 0);
                mModel.setCurrentItem(position);
            }
        });
    }

    private void recentSearchAdapterClick() {
        mRecentSearchAdapter.setRecentSearchAdapterListener(new RecentSearchAdapter.RecentSearchAdapterListener() {
            @Override
            public void recentSearchItemClick(String word) {
                mAutoCompleteTextView.setText(word);
                mAutoCompleteTextView.setSelection(word.length());
                mAutoCompleteTextView.dismissDropDown();
                mModel.deleteOverLapRecentSearch(word);
                mModel.insertRecentSearch(word);
                InputMethodManager inputMethodManager = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(mAutoCompleteTextView.getWindowToken(), 0);
            }

            @Override
            public void recentSearchDeleteClick(RecentSearch recentSearch) {
                mModel.deleteRecentSearch(recentSearch);
            }
        });
    }

    private void autoCompleteImeListener() {
        mAutoCompleteTextView.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                mAutoCompleteTextView.dismissDropDown();

                String word = mAutoCompleteTextView.getText().toString().trim();
                //검색어 중복시 본래 검색어 지우고 맨 위로
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
        TextInputLayout autoCompleteTextLayout = requireActivity().findViewById(R.id.auto_complete_text_layout);

        mAutoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s)) {
                    mBinding.recentSearchLayout.setVisibility(View.GONE);
                    if (mBinding.viewPagerLayout.getScrollY() == 0)
                        mBinding.searchFab.setVisibility(View.GONE);
                    else mBinding.searchFab.setVisibility(View.VISIBLE);
                    autoCompleteTextLayout.setEndIconVisible(true);
                } else {
                    mBinding.recentSearchLayout.scrollTo(0, 0);
                    mBinding.recentSearchLayout.setVisibility(View.VISIBLE);
                    mBinding.searchFab.setVisibility(View.GONE);
                    autoCompleteTextLayout.setEndIconVisible(false);
                }

                for (int i = 0; i < 4; i++) {
                    int finalI = i;
                    mSearchAdapterArray[i].getFilter().filter(mAutoCompleteTextView.getText(), count1 -> {
                        TabLayout.Tab tab = mBinding.tabLayout.getTabAt(finalI);
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
            mModel.sizeSelected(size, checkBox.isChecked(), mSearchAdapterArray[mBinding.viewPager.getCurrentItem()]);
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
    public void searchAdapterFolderClick(@NotNull Folder folder, MaterialCheckBox checkBox) {
        if (mActionMode == null)
            mNavController.navigate(SearchFragmentDirections.actionSearchFragmentToMainActivity(
                    folder.getId(), 0, folder.getParentId()));
        else {
            checkBox.setChecked(!checkBox.isChecked());
            mModel.folderSelected(folder, checkBox.isChecked(), mSearchAdapterArray[mBinding.viewPager.getCurrentItem()]);
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
                mBinding.viewPagerLayout.scrollBy(0, 1);
                mAutoScrollEnable = true;
            } else if (upDown == 1) {
                mBinding.viewPagerLayout.scrollBy(0, -1);
                mAutoScrollEnable = true;
            } else if (upDown == 2) {
                mBinding.viewPagerLayout.scrollBy(0, 0);
                mAutoScrollEnable = false;
            }
    }
}