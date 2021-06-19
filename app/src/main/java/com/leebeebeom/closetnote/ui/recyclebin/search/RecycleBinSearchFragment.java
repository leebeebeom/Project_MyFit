//package com.leebeebeom.closetnote.ui.recyclebin.search;
//
//import android.os.Bundle;
//import android.text.Editable;
//import android.text.TextUtils;
//import android.text.TextWatcher;
//import android.util.TypedValue;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.fragment.app.Fragment;
//import androidx.lifecycle.LiveData;
//import androidx.lifecycle.MediatorLiveData;
//import androidx.lifecycle.ViewModelProvider;
//import androidx.navigation.NavBackStackEntry;
//import androidx.navigation.NavController;
//import androidx.navigation.fragment.NavHostFragment;
//import androidx.recyclerview.widget.RecyclerView;
//import androidx.viewpager2.widget.ViewPager2;
//
//import com.leebeebeom.closetnote.R;
//import com.leebeebeom.closetnote.data.model.model.Category;
//import com.leebeebeom.closetnote.data.model.model.Folder;
//import com.leebeebeom.closetnote.data.model.model.RecentSearch;
//import com.leebeebeom.closetnote.databinding.ActivityRecycleBinBinding;
//import com.leebeebeom.closetnote.databinding.FragmentRecycleBinSearchBinding;
//import com.leebeebeom.closetnote.ui.dialog.RestoreDialogDirections;
//import com.leebeebeom.closetnote.ui.recyclebin.RecycleBinActivity;
//import com.leebeebeom.closetnote.ui.recyclebin.search.adapter.RecycleBinCategoryAdapter;
//import com.leebeebeom.closetnote.ui.recyclebin.search.adapter.RecycleBinFolderAdapter;
//import com.leebeebeom.closetnote.ui.recyclebin.search.adapter.RecycleBinSizeAdapter;
//import com.leebeebeom.closetnote.ui.recyclebin.search.adapter.RecycleBinViewPagerAdapter;
//import com.leebeebeom.closetnote.util.adapter.AutoCompleteAdapter;
//import com.leebeebeom.closetnote.ui.search.adapter.RecentSearchAdapter;
//import com.leebeebeom.closetnote.util.ActionModeImpl;
//import com.leebeebeom.closetnote.util.CommonUtil;
//import com.leebeebeom.closetnote.util.dragselect.DragSelect;
//import com.leebeebeom.closetnote.util.ListenerUtil;
//import com.leebeebeom.closetnote.util.adapter.BaseAdapter;
//import com.leebeebeom.closetnote.util.adapter.viewholder.CategoryVH;
//import com.leebeebeom.closetnote.util.adapter.viewholder.ViewPagerVH;
//import com.google.android.material.checkbox.MaterialCheckBox;
//import com.google.android.material.tabs.TabLayoutMediator;
//
//import org.jetbrains.annotations.NotNull;
//
//import java.util.ArrayList;
//import java.util.HashSet;
//import java.util.List;
//
//import static com.leebeebeom.closetnote.util.MyFitConstant.ACTION_MODE;
//import static com.leebeebeom.closetnote.util.MyFitConstant.DELETE_FOREVER_CONFIRM;
//import static com.leebeebeom.closetnote.util.MyFitConstant.NO_PARENT_CONFIRM;
//import static com.leebeebeom.closetnote.util.MyFitConstant.RECENT_SEARCH_RECYCLE_BIN;
//import static com.leebeebeom.closetnote.util.MyFitConstant.RESTORE_CONFIRM;
//
//public class RecycleBinSearchFragment extends Fragment implements CategoryVH.CategoryVHListener, FolderVHListener, SizeVHListener,
//        ViewPagerVH.AutoScrollListener, RecentSearchAdapter.RecentSearchAdapterListener, ActionModeImpl.ActionModeListener {
//
//    private FragmentRecycleBinSearchBinding mBinding;
//    private ActivityRecycleBinBinding mActivityBinding;
//    private NavController mNavController;
//    private RecycleBinCategoryAdapter mCategoryAdapter;
//    private RecycleBinFolderAdapter mFolderAdapter;
//    private RecycleBinSizeAdapter mSizeAdapter;
//    private RecycleBinSearchViewModel mModel;
//    private TypedValue mColorControl;
//    private RecentSearchAdapter mRecentSearchAdapter;
//    private DialogViewModel mDialogViewModel;
//    private DragSelect[] mDragSelectArray;
//    private ActionModeImpl mActionMode;
//    private AutoCompleteAdapter mAutoCompleteAdapter;
//    private BaseAdapter<?, ?>[] mAdapterArray;
//    private ListenerUtil mListenerUtil;
//
//    @Override
//    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        mModel = new ViewModelProvider(this).get(RecycleBinSearchViewModel.class);
//        mNavController = NavHostFragment.findNavController(this);
//        mDialogViewModel = new ViewModelProvider(mNavController.getViewModelStoreOwner(R.id.nav_graph_recycle_bin)).get(DialogViewModel.class);
//    }
//
//    @Override
//    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container,
//                             @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
//        mBinding = FragmentRecycleBinSearchBinding.inflate(inflater);
//
//        mBinding.vp.setAdapter(getViewPagerAdapter());
//        mBinding.vp.setOffscreenPageLimit(4);
//        initTabLayout();
//
//        mRecentSearchAdapter = new RecentSearchAdapter(this);
//        mBinding.rvRecentSearch.setHasFixedSize(true);
//        mBinding.rvRecentSearch.setAdapter(mRecentSearchAdapter);
//
//        mActivityBinding = ((RecycleBinActivity) requireActivity()).mBinding;
//
//        mAutoCompleteAdapter = new AutoCompleteAdapter(requireContext(), R.layout.item_auto_complete_texv_view, R.id.tv_item_ac_tv);
//        mActivityBinding.acTv.setAdapter(mAutoCompleteAdapter);
//
//        mActionMode = new ActionModeImpl(inflater, R.menu.menu_action_mode_recycle_bin, this, mAdapterArray)
//                .hasViewPager(mBinding.vp, mBinding.tabLayout);
//        return mBinding.getRoot();
//    }
//
//    @NotNull
//    private RecycleBinViewPagerAdapter getViewPagerAdapter() {
//        mCategoryAdapter = new RecycleBinCategoryAdapter(requireContext(), this, mModel);
//        mFolderAdapter = new RecycleBinFolderAdapter(requireContext(), this, mModel);
//        mSizeAdapter = new RecycleBinSizeAdapter(requireContext(), this, mModel);
//        RecycleBinSizeAdapter dummy = new RecycleBinSizeAdapter(requireContext(), this, mModel);
//        RecycleBinSizeAdapter dummy2 = new RecycleBinSizeAdapter(requireContext(), this, mModel);
//
//        mAdapterArray = new BaseAdapter<?, ?>[]{mCategoryAdapter, mFolderAdapter, mSizeAdapter, dummy, dummy2};
//
//        return new RecycleBinViewPagerAdapter(mAdapterArray, getDragSelectArray(), this);
//    }
//
//    private DragSelect[] getDragSelectArray() {
//        mDragSelectArray = new DragSelect[5];
//        for (int i = 0; i < mDragSelectArray.length; i++)
//            mDragSelectArray[i] = new DragSelect(scrollView).setScrollView(mBinding.sv);
//        return mDragSelectArray;
//    }
//
//    private void initTabLayout() {
//        new TabLayoutMediator(mBinding.tabLayout, mBinding.vp, (tab, position) -> {
//            if (position == 0) tab.setText(getString(R.string.all_category_cap));
//            else if (position == 1) tab.setText(getString(R.string.all_folder_cap));
//            else if (position == 2) tab.setText(getString(R.string.all_size_cap));
//            else if (position == 3) tab.setText(getString(R.string.all_daily_look_cap));
//            else if (position == 4) tab.setText(getString(R.string.all_wish_list_cap));
//        }).attach();
//    }
//
//    @Override
//    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//        mActivityBinding.acTv.requestFocus();
//        mActivityBinding.acTv.postDelayed(() -> CommonUtil.keyBoardToggle(requireContext()), 150);
//
//        observeContentsLive();
//        observeDialogLive();
//        observeSelecetedItemSizeLive();
//        observeRecentSearchLive();
//        observeAutoCompleteLive();
//        observeBadgeCountLive();
//    }
//
//    private void observeContentsLive() {
//        mModel.getCategoryLive().observe(getViewLifecycleOwner(), categoryList ->
//                mCategoryAdapter.setItem(categoryList, mModel.getFolderParentIdList(), mModel.getSizeParentIdList(), mActivityBinding.acTv.getText()));
//
//        mModel.getFolderLive().observe(getViewLifecycleOwner(), folderList ->
//                mFolderAdapter.setItem(folderList, mModel.getFolderParentIdList(), mModel.getSizeParentIdList(), mActivityBinding.acTv.getText()));
//
//        mModel.getSizeLive().observe(getViewLifecycleOwner(), sizeList ->
//                mSizeAdapter.setItem(sizeList, mActivityBinding.acTv.getText()));
//    }
//
//    private void observeDialogLive() {
//        mDialogViewModel.getBackStackEntryLive().observe(getViewLifecycleOwner(), navBackStackEntry -> {
//            restoreLive(navBackStackEntry);
//            noParentList(navBackStackEntry);
//            deleteForeverLive(navBackStackEntry);
//        });
//    }
//
//    private void restoreLive(@NotNull NavBackStackEntry navBackStackEntry) {
//        navBackStackEntry.getSavedStateHandle().getLiveData(RESTORE_CONFIRM).observe(navBackStackEntry, o -> {
//            if (mModel.restore())
//                CommonUtil.navigate(mNavController, R.id.restoreDialog,
//                        RestoreDialogDirections.toNoParentDialog(mModel.getNoParentNameArray(), R.id.nav_graph_recycle_bin));
//            else {
//                mNavController.popBackStack();
//                if (MyFitVariable.actionMode != null) MyFitVariable.actionMode.finish();
//            }
//        });
//    }
//
//    private void noParentList(@NotNull NavBackStackEntry navBackStackEntry) {
//        navBackStackEntry.getSavedStateHandle().getLiveData(NO_PARENT_CONFIRM).observe(navBackStackEntry, o -> {
//            mModel.noParentRestore();
//            mNavController.popBackStack(R.id.recycleBinSearch, false);
//            if (MyFitVariable.actionMode != null) MyFitVariable.actionMode.finish();
//        });
//    }
//
//    private void deleteForeverLive(@NotNull NavBackStackEntry navBackStackEntry) {
//        navBackStackEntry.getSavedStateHandle().getLiveData(DELETE_FOREVER_CONFIRM).observe(navBackStackEntry, o -> {
//            //TODO
//            if (MyFitVariable.actionMode != null) MyFitVariable.actionMode.finish();
//        });
//    }
//
//    private void observeSelecetedItemSizeLive() {
//        mModel.getSelectedItemSizeLive().observe(getViewLifecycleOwner(), integer -> {
//            if (MyFitVariable.actionMode != null) {
//                mActionMode.getBinding().tvTitle.setText(getString(R.string.action_mode_title, integer));
//                int allItemSize = mAdapterArray[mModel.getCurrentItem()].getItemCount();
//                mActionMode.getBinding().cb.setChecked(allItemSize == integer);
//
//                mActionMode.getMenuItems().get(0).setVisible(integer != 0);
//                mActionMode.getMenuItems().get(1).setVisible(integer != 0);
//            }
//        });
//    }
//
//    private void observeRecentSearchLive() {
//        mModel.getRecentSearchLive().observe(getViewLifecycleOwner(), recentSearchList -> {
//            mRecentSearchAdapter.submitList(recentSearchList);
//
//            if (recentSearchList.isEmpty())
//                mBinding.layoutNoResult.setVisibility(View.VISIBLE);
//            else mBinding.layoutNoResult.setVisibility(View.GONE);
//        });
//    }
//
//    private void observeAutoCompleteLive() {
//        LiveData<List<String>> categoryNameLive = mModel.getCategoryNameLive();
//        LiveData<List<String>> folderNameLive = mModel.getFolderNameLive();
//        LiveData<List<String>> sizeBrandLive = mModel.getSizeBrandLive();
//        LiveData<List<String>> sizeNameLive = mModel.getSizeNameLive();
//
//        MediatorLiveData<List<String>> autoCompleteLive = new MediatorLiveData<>();
//        autoCompleteLive.addSource(categoryNameLive, autoCompleteLive::setValue);
//        autoCompleteLive.addSource(folderNameLive, autoCompleteLive::setValue);
//        autoCompleteLive.addSource(sizeBrandLive, autoCompleteLive::setValue);
//        autoCompleteLive.addSource(sizeNameLive, autoCompleteLive::setValue);
//
//        HashSet<String> autoCompleteHashSet = new HashSet<>();
//        List<String> autoCompleteList = new ArrayList<>();
//        autoCompleteLive.observe(getViewLifecycleOwner(), stringList -> {
//            if (!autoCompleteHashSet.isEmpty()) autoCompleteHashSet.clear();
//            if (!autoCompleteList.isEmpty()) autoCompleteList.clear();
//
//            acHashSetAddValue(categoryNameLive, autoCompleteHashSet);
//            acHashSetAddValue(folderNameLive, autoCompleteHashSet);
//            acHashSetAddValue(sizeBrandLive, autoCompleteHashSet);
//            acHashSetAddValue(sizeNameLive, autoCompleteHashSet);
//
//            autoCompleteList.addAll(autoCompleteHashSet);
//            autoCompleteList.sort(String::compareTo);
//
//            mAutoCompleteAdapter.setItem(autoCompleteList);
//        });
//    }
//
//    private void acHashSetAddValue(@NotNull LiveData<List<String>> liveData, HashSet<String> autoCompleteHashSet) {
//        if (liveData.getValue() != null)
//            for (String s : liveData.getValue()) autoCompleteHashSet.add(s.trim());
//    }
//
//    private void observeBadgeCountLive() {
//        mModel.getFilteredListSizeLive().observe(getViewLifecycleOwner(), integerArray -> {
//            if (mColorControl == null) {
//                mColorControl = new TypedValue();
//                requireContext().getTheme().resolveAttribute(R.attr.myColorControl, mColorControl, true);
//            }
//
//            for (int i = 0; i < integerArray.length; i++)
//                CommonUtil.setBadgeCount(mBinding.tabLayout.getTabAt(i), integerArray[i], mColorControl);
//
//            RecyclerView viewPager = (RecyclerView) mBinding.vp.getChildAt(0);
//            ViewPagerVH[] viewPagerVHArray = new ViewPagerVH[5];
//            for (int i = 0; i < viewPagerVHArray.length; i++) {
//                viewPagerVHArray[i] = (ViewPagerVH) viewPager.findViewHolderForLayoutPosition(i);
//                if (viewPagerVHArray[i] != null)
//                    viewPagerVHArray[i].setNoResult(integerArray[i] == 0);
//            }
//        });
//    }
//
//    @Override
//    public void onActivityCreated(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//        mListenerUtil = new ListenerUtil();
//        mListenerUtil.setScrollChangeListener(mBinding.sv, mActivityBinding.fabTop);
//        mListenerUtil.setFabTopClickListener(mBinding.sv, mActivityBinding.fabTop);
//        mListenerUtil.autoCompleteEndIconClick(mActivityBinding.acTv, mActivityBinding.acTvLayout, requireContext());
//        mListenerUtil.autoCompleteItemClick(mActivityBinding.acTv, RECENT_SEARCH_RECYCLE_BIN, requireContext());
//        mListenerUtil.autoCompleteImeClick(mActivityBinding.acTv, RECENT_SEARCH_RECYCLE_BIN, requireContext());
//        pagerChangeListener();
//        acTextChangeListener();
//        recentSearchAllDeleteClick();
//
//        actionModeRestore(savedInstanceState);
//    }
//
//    private void pagerChangeListener() {
//        mBinding.vp.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
//            @Override
//            public void onPageSelected(int position) {
//                mBinding.sv.smoothScrollTo(0, 0);
//                mModel.setCurrentItem(position);
//            }
//        });
//    }
//
//    private void acTextChangeListener() {
//        mActivityBinding.acTv.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                if (!TextUtils.isEmpty(s)) {
//                    mBinding.svRecentSearch.setVisibility(View.GONE);
//                    if (mBinding.vp.getScrollY() == 0)
//                        mActivityBinding.fabTop.setVisibility(View.GONE);
//                    else mActivityBinding.fabTop.setVisibility(View.VISIBLE);
//                    mActivityBinding.acTvLayout.setEndIconVisible(true);
//                } else {
//                    mBinding.svRecentSearch.scrollTo(0, 0);
//                    mBinding.svRecentSearch.setVisibility(View.VISIBLE);
//                    mActivityBinding.fabTop.setVisibility(View.GONE);
//                    mActivityBinding.acTvLayout.setEndIconVisible(false);
//                }
//
//                mCategoryAdapter.setWord(s);
//                mFolderAdapter.setWord(s);
//                mSizeAdapter.setWord(s);
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//            }
//        });
//    }
//
//    private void recentSearchAllDeleteClick() {
//        mBinding.tvDeleteAll.setOnClickListener(v ->
//                CommonUtil.navigate(mNavController, R.id.recycleBinSearch,
//                        RecycleBinSearchFragmentDirections.toDeleteAllRecentSearchDialog()));
//    }
//
//    private void actionModeRestore(Bundle savedInstanceState) {
//        if (savedInstanceState != null && savedInstanceState.getBoolean(ACTION_MODE)) {
//            switch (mModel.getCurrentItem()) {
//                case 0:
//                    List<Category> categoryList = new ArrayList<>();
//                    for (Object o : mModel.getSelectedItem())
//                        if (o instanceof Category) categoryList.add((Category) o);
//                    mCategoryAdapter.setSelectedItemIds(categoryList);
//                    break;
//                case 1:
//                    List<Folder> folderList = new ArrayList<>();
//                    for (Object o : mModel.getSelectedItem())
//                        if (o instanceof Folder) folderList.add((Folder) o);
//                    mFolderAdapter.setSelectedItemIds(folderList);
//                    break;
//                default:
//                    List<SizeTop> sizeList = new ArrayList<>();
//                    for (Object o : mModel.getSelectedItem())
//                        if (o instanceof SizeTop) sizeList.add((SizeTop) o);
//                    mSizeAdapter.setSelectedItemIds(sizeList);
//            }
//            ((AppCompatActivity) requireActivity()).startSupportActionMode(mActionMode);
//        }
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        mActivityBinding.acTv.setText("");
//    }
//
//    @Override
//    public void onSaveInstanceState(@NonNull @NotNull Bundle outState) {
//        super.onSaveInstanceState(outState);
//        outState.putBoolean(ACTION_MODE, MyFitVariable.isActionModeOn);
//    }
//
//    //category adapter listener---------------------------------------------------------------------
//    @Override
//    public void onCategoryItemViewClick(Category category, MaterialCheckBox checkBox) {
//        if (MyFitVariable.actionMode == null) {
//            //TODO
//        } else {
//            checkBox.setChecked(!checkBox.isChecked());
//            mCategoryAdapter.itemSelected(category.getId());
//            mModel.itemSelected(category, checkBox.isChecked());
//        }
//    }
//
//    @Override
//    public void onCategoryItemViewLongClick(int position) {
//        actionModeStart(position);
//    }
//
//
//    @Override
//    public void onCategoryDragHandleTouch(RecyclerView.ViewHolder viewHolder) {
//    }
//
//    //folder adapter listener-----------------------------------------------------------------------
//    @Override
//    public void onFolderItemViewClick(Folder folder, MaterialCheckBox checkBox) {
//        if (MyFitVariable.actionMode == null) {
//            //TODO
//        } else {
//            checkBox.setChecked(!checkBox.isChecked());
//            mFolderAdapter.itemSelected(folder.getId());
//            mModel.itemSelected(folder, checkBox.isChecked());
//        }
//    }
//
//    @Override
//    public void onFolderItemViewLongClick(int position) {
//        actionModeStart(position);
//    }
//
//    @Override
//    public void onFolderDragHandleTouch(RecyclerView.ViewHolder holder) {
//
//    }
//
//    //size adapter listener-------------------------------------------------------------------------
//    @Override
//    public void sizeItemViewClick(SizeTop size, MaterialCheckBox checkBox) {
//        if (MyFitVariable.actionMode == null) {
//            //TODO
//        } else {
//            checkBox.setChecked(!checkBox.isChecked());
//            mSizeAdapter.itemSelected(size.getId());
//            mModel.itemSelected(size, checkBox.isChecked());
//        }
//    }
//
//    @Override
//    public void sizeItemViewLongClick(int position) {
//        actionModeStart(position);
//    }
//
//    @Override
//    public void sizeDragHandleTouch(RecyclerView.ViewHolder viewHolder) {
//
//    }
//
//    @Override
//    public void sizeFavoriteClick(SizeTop size) {
//
//    }
//
//    private void actionModeStart(int position) {
//        if (MyFitVariable.actionMode == null) {
//            mModel.getSelectedItem().clear();
//            ((AppCompatActivity) requireActivity()).startSupportActionMode(mActionMode);
//        }
//        mDragSelectArray[mModel.getCurrentItem()].startDragSelection(position);
//    }
//
//    //view pager listener---------------------------------------------------------------------------
//    @Override
//    public void dragAutoScroll(int upDownStop) {
//        mListenerUtil.viewPagerAutoScroll(mBinding.sv, upDownStop);
//    }
//
//    //recent search adapter-------------------------------------------------------------------------
//    @Override
//    public void recentSearchItemViewClick(String word) {
//        mListenerUtil.recentSearchClick(word, mActivityBinding.acTv, mActivityBinding.acTvLayout, requireContext(), RECENT_SEARCH_RECYCLE_BIN);
//        mActivityBinding.acTv.postDelayed(() -> CommonUtil.keyBoardHide(requireContext(), mActivityBinding.acTv), 150);
//    }
//
//    @Override
//    public void recentSearchDeleteClick(RecentSearch recentSearch) {
//        mModel.recentSearchDelete(recentSearch);
//    }
//
//    //action mode-----------------------------------------------------------------------------------
//    @Override
//    public void selectAllClick(boolean isChecked) {
//        switch (mModel.getCurrentItem()) {
//            case 0:
//                mModel.selectAllClick(isChecked, mCategoryAdapter.getCurrentList());
//                break;
//            case 1:
//                mModel.selectAllClick(isChecked, mFolderAdapter.getCurrentList());
//                break;
//            case 2:
//                mModel.selectAllClick(isChecked, mSizeAdapter.getCurrentList());
//                break;
//        }
//    }
//
//    @Override
//    public void actionItemClick(int itemId) {
//        if (itemId == R.id.menu_action_mode_recycle_bin_restore)
//            CommonUtil.navigate(mNavController, R.id.recycleBinSearch,
//                    RecycleBinSearchFragmentDirections.toRestoreDialog(mModel.getSelectedItemSize(), R.id.nav_graph_recycle_bin));
//        else if (itemId == R.id.menu_action_mode_recycle_bin_delete_forever)
//            CommonUtil.navigate(mNavController, R.id.recycleBinSearch,
//                    RecycleBinSearchFragmentDirections.toDeleteForeverDialog(mModel.getSelectedItemSize()));
//    }
//}