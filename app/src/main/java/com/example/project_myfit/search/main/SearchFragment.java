package com.example.project_myfit.search.main;

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
import com.example.project_myfit.databinding.FragmentSearchBinding;
import com.example.project_myfit.databinding.TitleActionModeBinding;
import com.example.project_myfit.dialog.DialogViewModel;
import com.example.project_myfit.search.SearchViewModel;
import com.example.project_myfit.search.main.adapter.RecentSearchAdapter;
import com.example.project_myfit.search.main.adapter.SearchAdapter;
import com.example.project_myfit.search.main.adapter.SearchViewPagerAdapter;
import com.example.project_myfit.util.KeyboardUtil;
import com.example.project_myfit.util.SelectedItemTreat;
import com.example.project_myfit.util.adapter.viewholder.FolderVHListener;
import com.example.project_myfit.util.adapter.viewholder.SizeVHListener;
import com.example.project_myfit.util.adapter.viewholder.ViewPagerVH;
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
import static com.example.project_myfit.util.MyFitConstant.SELECTED_ITEM_DELETE_CONFIRM_CLICK;
import static com.example.project_myfit.util.MyFitConstant.TOP;


public class SearchFragment extends Fragment implements ViewPagerVH.ViewPagerAutoScrollListener, FolderVHListener, SizeVHListener {
    private SearchViewModel mModel;
    private FragmentSearchBinding mBinding;
    private List<String> mRecentSearchStringList;
    private MaterialAutoCompleteTextView mAutoCompleteTextView;
    private ActionMode mActionMode;
    private boolean isDragSelecting, mAutoScrollEnable;
    private TitleActionModeBinding mActionModeTitleBinding;
    private SearchViewPagerAdapter mViewPagerAdapter;
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
            mBinding.vpSearch.setUserInputEnabled(false);

            mActionMode = mode;
            mModel.setActionModeOn(true);

            LinearLayout tab = (LinearLayout) mBinding.tabLayoutSearch.getChildAt(0);
            for (int i = 0; i < 4; i++) tab.getChildAt(i).setClickable(false);

            mode.getMenuInflater().inflate(R.menu.menu_action_mode, menu);
            mode.setCustomView(mActionModeTitleBinding.getRoot());

            mSearchAdapterArray[mModel.getCurrentItem()].setActionModeState(ACTION_MODE_ON);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, @NotNull Menu menu) {
            mActionModeTitleBinding.cbActionModeTitle.setOnClickListener(v ->
                    mModel.selectAllClick(mActionModeTitleBinding.cbActionModeTitle.isChecked(), mSearchAdapterArray));

            mEditMenu = menu.getItem(0);
            mMoveMenu = menu.getItem(1);
            mDeletedMenu = menu.getItem(2);
            return true;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, @NotNull MenuItem item) {
            if (item.getItemId() == R.id.menu_action_mode_delete)
                mNavController.navigate(SearchFragmentDirections.actionSearchFragmentToSearchSelectedItemDeleteDialog(mModel.getSelectedItemSize()));
            else if (item.getItemId() == R.id.menu_action_mode_move) {
                mDialogViewModel.forTreeView(mModel.getSelectedFolderList(), mModel.getSelectedSizeList(), null);
                mNavController.navigate(SearchFragmentDirections.actionSearchFragmentToSearchTreeViewDialog(mModel.getParentCategory()));
            } else
                mNavController.navigate(SearchFragmentDirections.actionSearchFragmentToSearchNameEditDialog(mModel.getSelectedFolderId(), FOLDER, false));
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mBinding.vpSearch.setUserInputEnabled(true);

            mActionMode = null;
            mModel.setActionModeOn(false);

            LinearLayout tab = (LinearLayout) mBinding.tabLayoutSearch.getChildAt(0);
            for (int i = 0; i < 4; i++) tab.getChildAt(i).setClickable(true);

            mSearchAdapterArray[mModel.getCurrentItem()].setActionModeState(ACTION_MODE_OFF);

            mActionModeTitleBinding.cbActionModeTitle.setChecked(false);
            ((ViewGroup) mActionModeTitleBinding.getRoot().getParent()).removeAllViews();
        }
    };
    private TextInputLayout mAutoCompleteTextLayout;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mModel = new ViewModelProvider(requireActivity()).get(SearchViewModel.class);
        mNavController = NavHostFragment.findNavController(this);
        mDialogViewModel = new ViewModelProvider(mNavController.getViewModelStoreOwner(R.id.nav_graph_search)).get(DialogViewModel.class);
        mColorControl = new TypedValue();
        requireContext().getTheme().resolveAttribute(R.attr.myColorControl, mColorControl, true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = FragmentSearchBinding.inflate(inflater);
        View view = mBinding.getRoot();
        mActionModeTitleBinding = TitleActionModeBinding.inflate(inflater);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAutoCompleteTextView = requireActivity().findViewById(R.id.ac_tv_search);
        mAutoCompleteTextLayout = requireActivity().findViewById(R.id.ac_tv_search_layout);

        mBinding.vpSearch.setAdapter(getViewPagerAdapter());
        mBinding.vpSearch.setOffscreenPageLimit(1);
        tabLayoutInit();
        mRecentSearchAdapter = new RecentSearchAdapter();
        mBinding.rvSearchRecentSearch.setAdapter(mRecentSearchAdapter);

        setDialogLive();
        setRecentSearchLive();
        setActionModeTitleLive();
        setCombineLive();
    }

    @NotNull
    private SearchViewPagerAdapter getViewPagerAdapter() {
        mSearchAdapterArray = new SearchAdapter[4];
        for (int i = 0; i < 4; i++) mSearchAdapterArray[i] = new SearchAdapter(this, this);

        mViewPagerAdapter = new SearchViewPagerAdapter(mSearchAdapterArray, getDragSelectListener(), this);
        return mViewPagerAdapter;
    }

    private DragSelectTouchListener getDragSelectListener() {
        DragSelectTouchListener.OnAdvancedDragSelectListener listener = new DragSelectTouchListener.OnAdvancedDragSelectListener() {
            @Override
            public void onSelectionStarted(int i) {
                mBinding.svSearch.setScrollable(false);
                isDragSelecting = true;

                ViewPagerVH viewPagerVH = (ViewPagerVH)
                        ((RecyclerView) mBinding.vpSearch.getChildAt(0)).findViewHolderForAdapterPosition(mModel.getCurrentItem());
                if (viewPagerVH != null) {
                    RecyclerView recyclerView = viewPagerVH.getBinding().rvItemRv;
                    RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForLayoutPosition(i);
                    if (viewHolder != null) viewHolder.itemView.callOnClick();
                }
            }

            @Override
            public void onSelectionFinished(int i) {
                mBinding.svSearch.setScrollable(true);
                isDragSelecting = false;
            }

            @Override
            public void onSelectChange(int i, int i1, boolean b) {
                ViewPagerVH viewPagerVH = (ViewPagerVH)
                        ((RecyclerView) mBinding.vpSearch.getChildAt(0)).findViewHolderForAdapterPosition(mModel.getCurrentItem());
                if (viewPagerVH != null) {
                    RecyclerView recyclerView = viewPagerVH.getBinding().rvItemRv;
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
        new TabLayoutMediator(mBinding.tabLayoutSearch, mBinding.vpSearch, (tab, position) -> {
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

    private void setRecentSearchLive() {
        mModel.getAllRecentSearchLive().observe(getViewLifecycleOwner(), recentSearchList -> {
            mRecentSearchAdapter.submitList(recentSearchList);

            if (mRecentSearchStringList == null) mRecentSearchStringList = new ArrayList<>();
            if (!mRecentSearchStringList.isEmpty()) mRecentSearchStringList.clear();
            for (RecentSearch recentSearch : recentSearchList)
                mRecentSearchStringList.add(recentSearch.getWord());

            if (recentSearchList.isEmpty())
                mBinding.tvSearchNoResultLayout.setVisibility(View.VISIBLE);
            else mBinding.tvSearchNoResultLayout.setVisibility(View.GONE);
        });
    }

    private void setActionModeTitleLive() {
        mModel.getSelectedItemSizeLive().observe(getViewLifecycleOwner(), integer -> {
            String title = integer + getString(R.string.action_mode_title);
            mActionModeTitleBinding.tvActionModeTitle.setText(title);

            if (mActionMode != null) {
                mEditMenu.setVisible(integer == 1 && mModel.getSelectedItemList().get(0) instanceof Folder);
                mMoveMenu.setVisible(integer > 0);
                mDeletedMenu.setVisible(integer > 0);
            }

            int allItemSize = mSearchAdapterArray[mModel.getCurrentItem()].getItemCount();
            mActionModeTitleBinding.cbActionModeTitle.setChecked(allItemSize == integer);
        });
    }

    private void setCombineLive() {
        LiveData<List<Folder>> folderLiveData = mModel.getAllFolderLive();
        LiveData<List<Size>> sizeLiveData = mModel.getAllSizeLive();

        MediatorLiveData<Object> combineLiveData = new MediatorLiveData<>();
        combineLiveData.addSource(folderLiveData, combineLiveData::setValue);
        combineLiveData.addSource(sizeLiveData, combineLiveData::setValue);
        combineLiveData.observe(getViewLifecycleOwner(), o -> {
            if (o instanceof List<?>)
                if (((List<?>) o).get(0) instanceof Folder)
                    folderChangedValue(sizeLiveData, (Collection<?>) o);
                else if (((List<?>) o).get(0) instanceof Size)
                    sizeChangedValue(folderLiveData, (Collection<?>) o);
        });
    }

    private void folderChangedValue(@NotNull LiveData<List<Size>> sizeLiveData, Collection<?> o) {
        List<Object> allItemList = new ArrayList<>(o);
        if (sizeLiveData.getValue() != null)
            allItemList.addAll(sizeLiveData.getValue());
        mViewPagerAdapter.setItems(allItemList, mAutoCompleteTextView.getText(), mBinding.tabLayoutSearch, mColorControl);
    }

    private void sizeChangedValue(@NotNull LiveData<List<Folder>> folderLiveData, Collection<?> o) {
        List<Object> allItemList = new ArrayList<>(o);
        if (folderLiveData.getValue() != null)
            allItemList.addAll(0, folderLiveData.getValue());
        mViewPagerAdapter.setItems(allItemList, mAutoCompleteTextView.getText(), mBinding.tabLayoutSearch, mColorControl);
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
        mBinding.svSearch.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (v.getScrollY() != 0) mBinding.fabSearch.show();
            else mBinding.fabSearch.hide();

            if (isDragSelecting && mAutoScrollEnable && scrollY > oldScrollY)
                v.postDelayed(() -> v.scrollBy(0, 1), 50);
            else if (isDragSelecting && mAutoScrollEnable && scrollY < oldScrollY)
                v.postDelayed(() -> v.scrollBy(0, -1), 50);
        });
    }

    private void setRecentSearchScrollListener() {
        mBinding.svSearchRecentSearch.setOnScrollChangeListener((View.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (v.getScrollY() != 0) mBinding.fabSearch.show();
            else mBinding.fabSearch.hide();
        });
    }

    private void fabClickLister() {
        mBinding.fabSearch.setOnClickListener(v -> {
            if (mBinding.svSearchRecentSearch.getVisibility() != View.VISIBLE)
                svScrollToTop(mBinding.svSearch);
            else if (mBinding.svSearchRecentSearch.getVisibility() == View.VISIBLE)
                svScrollToTop(mBinding.svSearchRecentSearch);
        });
    }

    private void svScrollToTop(@NotNull NestedScrollView scrollView) {
        scrollView.scrollTo(0, 0);
        scrollView.setOnScrollChangeListener((View.OnScrollChangeListener) (v1, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (scrollY != 0) scrollView.scrollTo(0, 0);
            else {
                scrollView.setOnScrollChangeListener((View.OnScrollChangeListener) null);
                setMainScrollChangeListener();
            }
        });
    }

    private void viewPagerChangeListener() {
        mBinding.vpSearch.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                mBinding.svSearch.smoothScrollTo(0, 0);
                mModel.setCurrentItem(position);
            }
        });
    }

    private void recentSearchAdapterClickListener() {
        mRecentSearchAdapter.setRecentSearchAdapterListener(new RecentSearchAdapter.RecentSearchAdapterListener() {
            @Override
            public void recentSearchItemClick(String word) {
                mAutoCompleteTextView.setText(word);
                mAutoCompleteTextView.setSelection(word.length());
                mAutoCompleteTextView.dismissDropDown();
                mModel.overLapRecentSearchReInsert(word);
                mAutoCompleteTextLayout.setEndIconVisible(false);
                KeyboardUtil.keyboardHide(requireContext(), mAutoCompleteTextView);
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
                KeyboardUtil.keyboardHide(requireContext(), v);

                mAutoCompleteTextView.dismissDropDown();

                String word = mAutoCompleteTextView.getText().toString().trim();
                //검색어 중복시 지우고 맨 위로
                if (!TextUtils.isEmpty(word)) {
                    if (mRecentSearchStringList.contains(word))
                        mModel.overLapRecentSearchReInsert(word);
                    else mModel.insertRecentSearch(word);
                }
            }
            return true;
        });
    }

    private void autoCompleteTextChangeListener() {
        mAutoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s)) {
                    mBinding.svSearchRecentSearch.setVisibility(View.GONE);
                    if (mBinding.vpSearch.getScrollY() == 0)
                        mBinding.fabSearch.setVisibility(View.GONE);
                    else mBinding.fabSearch.setVisibility(View.VISIBLE);
                    mAutoCompleteTextLayout.setEndIconVisible(true);
                } else {
                    mBinding.svSearchRecentSearch.scrollTo(0, 0);
                    mBinding.svSearchRecentSearch.setVisibility(View.VISIBLE);
                    mBinding.fabSearch.setVisibility(View.GONE);
                    mAutoCompleteTextLayout.setEndIconVisible(false);
                    KeyboardUtil.keyboardShow(requireContext(), mAutoCompleteTextView);
                }

                for (int i = 0; i < 4; i++) {
                    int finalI = i;
                    mSearchAdapterArray[i].getFilter().filter(mAutoCompleteTextView.getText(), count1 -> {
                        TabLayout.Tab tab = mBinding.tabLayoutSearch.getTabAt(finalI);
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
        mBinding.tvSearchDeleteAll.setOnClickListener(v ->
                mNavController.navigate(SearchFragmentDirections.actionSearchFragmentToRecentSearchDeleteAllDialog()));
    }

    private void actionModeRecreate() {
        if (mModel.isActionModeOn()) {
            mSearchAdapterArray[mModel.getCurrentItem()].setSelectedItem(mModel.getSelectedItemList());
            ((AppCompatActivity) requireActivity()).startSupportActionMode(mActionModeCallback);
        }
    }

    @Override
    public void dragAutoScroll(int upDown) {
        if (isDragSelecting)
            if (upDown == 0) {
                mBinding.svSearch.scrollBy(0, 1);
                mAutoScrollEnable = true;
            } else if (upDown == 1) {
                mBinding.svSearch.scrollBy(0, -1);
                mAutoScrollEnable = true;
            } else if (upDown == 2) {
                mBinding.svSearch.scrollBy(0, 0);
                mAutoScrollEnable = false;
            }
    }

    @Override
    public void onFolderItemViewClick(Folder folder, MaterialCheckBox checkBox) {
        if (mActionMode == null)
            mNavController.navigate(SearchFragmentDirections.actionSearchFragmentToMainActivity(
                    folder.getId(), 0, folder.getParentId()));
        else {
            checkBox.setChecked(!checkBox.isChecked());
            mModel.itemSelected(folder, folder.getId(), checkBox.isChecked(), mSearchAdapterArray);
        }
    }

    @Override
    public void onFolderItemViewLongClick(int position) {
        actionModeStart(position);
    }

    @Override
    public void onFolderDragHandleTouch(RecyclerView.ViewHolder holder) {
    }

    @Override
    public void onSizeItemViewClick(Size size, MaterialCheckBox checkBox) {
        if (mActionMode == null)
            mNavController.navigate(SearchFragmentDirections.actionSearchFragmentToMainActivity(0, size.getId(), size.getParentId()));
        else {
            checkBox.setChecked(!checkBox.isChecked());
            mModel.itemSelected(size, size.getId(), checkBox.isChecked(), mSearchAdapterArray);
        }
    }

    @Override
    public void onSizeItemViewLongClick(int position) {
        actionModeStart(position);
    }

    @Override
    public void onSizeDragHandleTouch(RecyclerView.ViewHolder viewHolder) {

    }

    @Override
    public void onSizeFavoriteClick(Size size) {
        mModel.sizeUpdate(size);
    }

    private void actionModeStart(int position) {
        if (mActionMode == null) {
            mModel.getSelectedItemList().clear();
            ((AppCompatActivity) requireActivity()).startSupportActionMode(mActionModeCallback);
        }
        mDragSelectListener.startDragSelection(position);
    }
}