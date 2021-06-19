//package com.leebeebeom.closetnote.ui.search;
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
//import androidx.lifecycle.MediatorLiveData;
//import androidx.lifecycle.ViewModelProvider;
//import androidx.navigation.NavBackStackEntry;
//import androidx.navigation.NavController;
//import androidx.navigation.fragment.NavHostFragment;
//import androidx.recyclerview.widget.ConcatAdapter;
//import androidx.recyclerview.widget.RecyclerView;
//import androidx.viewpager2.widget.ViewPager2;
//
//import com.leebeebeom.closetnote.R;
//import com.leebeebeom.closetnote.data.model.model.Folder;
//import com.leebeebeom.closetnote.data.model.model.RecentSearch;
//import com.leebeebeom.closetnote.databinding.ActivitySearchBinding;
//import com.leebeebeom.closetnote.databinding.FragmentSearchBinding;
//import com.leebeebeom.closetnote.ui.search.adapter.RecentSearchAdapter;
//import com.leebeebeom.closetnote.ui.search.adapter.SearchFolderAdapter;
//import com.leebeebeom.closetnote.ui.search.adapter.SearchSizeAdapter;
//import com.leebeebeom.closetnote.ui.search.adapter.SearchViewPagerAdapter;
//import com.leebeebeom.closetnote.util.ActionModeImpl;
//import com.leebeebeom.closetnote.util.CommonUtil;
//import com.leebeebeom.closetnote.util.ListenerUtil;
//import com.leebeebeom.closetnote.util.adapter.BaseAdapter;
//import com.leebeebeom.closetnote.util.adapter.viewholder.ViewPagerVH;
//import com.leebeebeom.closetnote.util.dragselect.DragSelect;
//import com.google.android.material.checkbox.MaterialCheckBox;
//import com.google.android.material.tabs.TabLayoutMediator;
//
//import org.jetbrains.annotations.NotNull;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//
//import static com.leebeebeom.closetnote.util.MyFitConstant.ACTION_MODE;
//import static com.leebeebeom.closetnote.util.MyFitConstant.DELETE_SELECTED_ITEM_CONFIRM;
//import static com.leebeebeom.closetnote.util.MyFitConstant.EDIT_NAME_CONFIRM;
//import static com.leebeebeom.closetnote.util.MyFitConstant.MOVE_ITEM_CONFIRM;
//import static com.leebeebeom.closetnote.util.MyFitConstant.RECENT_SEARCH_SEARCH;
//
//public class SearchFragment extends Fragment implements ViewPagerVH.AutoScrollListener, FolderVHListener, SizeVHListener
//        , ActionModeImpl.ActionModeListener, RecentSearchAdapter.RecentSearchAdapterListener {
//    private SearchViewModel mModel;
//    private FragmentSearchBinding mBinding;
//    private ActivitySearchBinding mActivityBinding;
//    private boolean mAutoScrollEnable;
//    private NavController mNavController;
//    private DialogViewModel mDialogViewModel;
//    private RecentSearchAdapter mRecentSearchAdapter;
//    private TypedValue mColorControl;
//    private SearchFolderAdapter[] mFolderAdapterArray;
//    private SearchSizeAdapter[] mSizeAdapterArray;
//    private ConcatAdapter[] mConcatAdapterArray;
//    private ActionModeImpl mActionMode;
//    private DragSelect[] mDragSelectListenerArray;
//    private ListenerUtil mListenerUtil;
//
//    @Override
//    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        mModel = new ViewModelProvider(requireActivity()).get(SearchViewModel.class);
//        mNavController = NavHostFragment.findNavController(this);
//        mDialogViewModel = new ViewModelProvider(mNavController.getViewModelStoreOwner(R.id.nav_graph_search)).get(DialogViewModel.class);
//        mColorControl = new TypedValue();
//        requireContext().getTheme().resolveAttribute(R.attr.myColorControl, mColorControl, true);
//    }
//
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
//                             @Nullable Bundle savedInstanceState) {
//        mBinding = FragmentSearchBinding.inflate(inflater);
//
//        mBinding.vp.setAdapter(getViewPagerAdapter());
//        mBinding.vp.setOffscreenPageLimit(3);
//        tabLayoutInit();
//
//        mRecentSearchAdapter = new RecentSearchAdapter(this);
//        mBinding.rvRecentSearch.setHasFixedSize(true);
//        mBinding.rvRecentSearch.setAdapter(mRecentSearchAdapter);
//
//        mActivityBinding = ((SearchActivity) requireActivity()).mBinding;
//
//        View view = mBinding.getRoot();
//
//        BaseAdapter<?, ?>[] baseAdapterArray = new BaseAdapter[8];
//        System.arraycopy(mFolderAdapterArray, 0, baseAdapterArray, 0, mFolderAdapterArray.length);
//        System.arraycopy(mSizeAdapterArray, 0, baseAdapterArray, 4, mSizeAdapterArray.length);
//        mActionMode = new ActionModeImpl(inflater, R.menu.menu_action_mode, this, baseAdapterArray)
//                .hasViewPager(mBinding.vp, mBinding.tabLayout);
//        return view;
//    }
//
//    @NotNull
//    private SearchViewPagerAdapter getViewPagerAdapter() {
//        mFolderAdapterArray = new SearchFolderAdapter[4];
//        for (int i = 0; i < 4; i++)
//            mFolderAdapterArray[i] = new SearchFolderAdapter(requireContext(), this, mModel, i);
//
//        mSizeAdapterArray = new SearchSizeAdapter[4];
//        for (int i = 0; i < 4; i++)
//            mSizeAdapterArray[i] = new SearchSizeAdapter(requireContext(), this, mModel, i);
//
//        ConcatAdapter.Config config = new ConcatAdapter.Config.Builder()
//                .setStableIdMode(ConcatAdapter.Config.StableIdMode.ISOLATED_STABLE_IDS).build();
//
//        mConcatAdapterArray = new ConcatAdapter[4];
//        for (int i = 0; i < 4; i++)
//            mConcatAdapterArray[i] = new ConcatAdapter(config, mFolderAdapterArray[i], mSizeAdapterArray[i]);
//
//        return new SearchViewPagerAdapter(mConcatAdapterArray, getDragSelectListener(), this);
//    }
//
//    private DragSelect[] getDragSelectListener() {
//        mDragSelectListenerArray = new DragSelect[4];
//        for (int i = 0; i < mDragSelectListenerArray.length; i++)
//            mDragSelectListenerArray[i] = new DragSelect(scrollView).setScrollView(mBinding.sv);
//        return mDragSelectListenerArray;
//    }
//
//    private void tabLayoutInit() {
//        new TabLayoutMediator(mBinding.tabLayout, mBinding.vp, (tab, position) -> {
//            if (position == 0) tab.setText(Constant.ParentCategory.TOP.name());
//            else if (position == 1) tab.setText(Constant.ParentCategory.BOTTOM.name());
//            else if (position == 2) tab.setText(Constant.ParentCategory.OUTER.name());
//            else tab.setText(Constant.ParentCategory.ETC.name());
//        }).attach();
//    }
//
//    @Override
//    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//        dialogLive();
//        recentSearchLive();
//        actionModeTitleLive();
//        folderLive();
//        sizeLive();
//        badgeCountLive();
//    }
//
//    private void dialogLive() {
//        SelectedItemTreat selectedItemTreat = new SelectedItemTreat(requireContext());
//
//        mDialogViewModel.getBackStackEntryLive().observe(getViewLifecycleOwner(), navBackStackEntry -> {
//            selectedItemDeleteLive(selectedItemTreat, navBackStackEntry);
//            itemMoveLive(selectedItemTreat, navBackStackEntry);
//            nameEditLive(navBackStackEntry);
//        });
//    }
//
//    private void selectedItemDeleteLive(SelectedItemTreat selectedItemTreat, @NotNull NavBackStackEntry navBackStackEntry) {
//        navBackStackEntry.getSavedStateHandle().getLiveData(DELETE_SELECTED_ITEM_CONFIRM).observe(navBackStackEntry, o -> {
//            selectedItemTreat.folderSizeTreat(true, mModel.getSelectedFolderList(), mModel.getSelectedSizeList(), true);
//            if (MyFitVariable.actionMode != null) MyFitVariable.actionMode.finish();
//        });
//    }
//
//    private void itemMoveLive(SelectedItemTreat selectedItemTreat, @NotNull NavBackStackEntry navBackStackEntry) {
//        navBackStackEntry.getSavedStateHandle().getLiveData(MOVE_ITEM_CONFIRM).observe(navBackStackEntry, o -> {
//            long parentId = (long) o;
//            selectedItemTreat.moveSelectedItems(true, parentId, mModel.getSelectedFolderList(), mModel.getSelectedSizeList());
//            if (MyFitVariable.actionMode != null) MyFitVariable.actionMode.finish();
//        });
//    }
//
//    private void nameEditLive(@NotNull NavBackStackEntry navBackStackEntry) {
//        navBackStackEntry.getSavedStateHandle().getLiveData(EDIT_NAME_CONFIRM).observe(navBackStackEntry, o -> {
//            if (MyFitVariable.actionMode != null) MyFitVariable.actionMode.finish();
//        });
//    }
//
//    private void recentSearchLive() {
//        mModel.getRecentSearchLive().observe(getViewLifecycleOwner(), recentSearchList -> {
//            mRecentSearchAdapter.submitList(recentSearchList);
//
//            if (recentSearchList.isEmpty())
//                mBinding.layoutNoResult.setVisibility(View.VISIBLE);
//            else mBinding.layoutNoResult.setVisibility(View.GONE);
//        });
//    }
//
//    private void actionModeTitleLive() {
//        mModel.getSelectedItemSizeLive().observe(getViewLifecycleOwner(), integer -> {
//            if (MyFitVariable.actionMode != null) {
//                mActionMode.getBinding().tvTitle.setText(getString(R.string.action_mode_title, integer));
//                int allItemSize = mConcatAdapterArray[mModel.getCurrentItem()].getItemCount();
//                mActionMode.getBinding().cb.setChecked(allItemSize == integer);
//
//                mActionMode.getMenuItems().get(0).setVisible(integer == 1 &&
//                        mModel.getSelectedFolderList().size() == 1 && mModel.getSelectedSizeList().isEmpty());
//                mActionMode.getMenuItems().get(1).setVisible(integer > 0);
//                mActionMode.getMenuItems().get(2).setVisible(integer > 0);
//            }
//        });
//    }
//
//    private void folderLive() {
//        mModel.getFolderLive().observe(getViewLifecycleOwner(), folderList -> {
//            List<List<Folder>> list = Arrays.asList(new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
//            for (Folder folder : folderList) {
//                Constant.ParentCategory parentCategory = Constant.ParentCategory.values()[folder.getParentIndex()];
//                list.get(parentCategory.ordinal()).add(folder);
//            }
//
//            for (int i = 0; i < mFolderAdapterArray.length; i++)
//                mFolderAdapterArray[i].setItem(list.get(i), mModel.getFolderParentIdList(i),
//                        mModel.getSizeParentIdList(i), mActivityBinding.acTv.getText());
//        });
//    }
//
//    private void sizeLive() {
//        mModel.getSizeLive().observe(getViewLifecycleOwner(), sizeList -> {
//            List<List<SizeTop>> list = Arrays.asList(new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
//            for (SizeTop size : sizeList) {
//                Constant.ParentCategory parentCategory = Constant.ParentCategory.values()[size.getParentCategoryIndex()];
//                list.get(parentCategory.ordinal()).add(size);
//            }
//            for (int i = 0; i < mSizeAdapterArray.length; i++)
//                mSizeAdapterArray[i].setItem(list.get(i), mActivityBinding.acTv.getText());
//        });
//    }
//
//    private void badgeCountLive() {
//        MediatorLiveData<Integer[]> badgeCountLive = new MediatorLiveData<>();
//        badgeCountLive.addSource(mModel.getFolderFilteredListSizeLive(), badgeCountLive::setValue);
//        badgeCountLive.addSource(mModel.getSizeFilteredListSizeLive(), badgeCountLive::setValue);
//        badgeCountLive.observe(getViewLifecycleOwner(), integer -> {
//            if (mModel.getFolderFilteredListSizeLive().getValue() != null && mModel.getSizeFilteredListSizeLive().getValue() != null) {
//                Integer[] countArray = new Integer[4];
//                for (int i = 0; i < 4; i++)
//                    countArray[i] = mModel.getFolderFilteredListSizeLive().getValue()[i];
//
//                for (int i = 0; i < 4; i++) {
//                    countArray[i] = countArray[i] + mModel.getSizeFilteredListSizeLive().getValue()[i];
//                    CommonUtil.setBadgeCount(mBinding.tabLayout.getTabAt(i), countArray[i], mColorControl);
//                }
//
//                ViewPagerVH[] viewPagerVHArray = new ViewPagerVH[4];
//                RecyclerView viewPager = (RecyclerView) mBinding.vp.getChildAt(0);
//                for (int i = 0; i < viewPagerVHArray.length; i++) {
//                    viewPagerVHArray[i] = (ViewPagerVH) viewPager.findViewHolderForAdapterPosition(i);
//                    if (viewPagerVHArray[i] != null)
//                        viewPagerVHArray[i].setNoResult(countArray[i] == 0);
//                }
//            }
//        });
//    }
//
//    @Override
//    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//        mListenerUtil = new ListenerUtil();
//        mListenerUtil.setScrollChangeListener(mBinding.sv, mActivityBinding.fabTop);
//        mListenerUtil.setFabTopClickListener(mBinding.sv, mActivityBinding.fabTop);
//        mListenerUtil.autoCompleteEndIconClick(mActivityBinding.acTv, mActivityBinding.acTvLayout, requireContext());
//        mListenerUtil.autoCompleteItemClick(mActivityBinding.acTv, RECENT_SEARCH_SEARCH, requireContext());
//        mListenerUtil.autoCompleteImeClick(mActivityBinding.acTv, RECENT_SEARCH_SEARCH, requireActivity());
//        vpPageChangeListener();
//        acTextChangeListener();
//        recentSearchDeleteAllClick();
//
//        actionModeRestore(savedInstanceState);
//    }
//
//    private void vpPageChangeListener() {
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
//                for (int i = 0; i < 4; i++) {
//                    mFolderAdapterArray[i].setWord(s);
//                    mSizeAdapterArray[i].setWord(s);
//                }
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//            }
//        });
//    }
//
//    private void recentSearchDeleteAllClick() {
//        mBinding.tvDeleteAll.setOnClickListener(v ->
//                CommonUtil.navigate(mNavController, R.id.searchFragment,
//                        SearchFragmentDirections.toDeleteAllRecentSearchDialog()));
//    }
//
//    private void actionModeRestore(Bundle savedInstanceState) {
//        if (savedInstanceState != null && savedInstanceState.getBoolean(ACTION_MODE)) {
//            mFolderAdapterArray[mModel.getCurrentItem()].setSelectedItemIds(mModel.getSelectedFolderList());
//            mSizeAdapterArray[mModel.getCurrentItem()].setSelectedItemIds(mModel.getSelectedSizeList());
//            ((AppCompatActivity) requireActivity()).startSupportActionMode(mActionMode);
//        }
//    }
//
//    @Override
//    public void dragAutoScroll(int upDownStop) {
//        if (MyFitVariable.isDragSelecting)
//            mListenerUtil.viewPagerAutoScroll(mBinding.sv, upDownStop);
//    }
//
//    @Override
//    public void onSaveInstanceState(@NonNull @NotNull Bundle outState) {
//        super.onSaveInstanceState(outState);
//        outState.putBoolean(ACTION_MODE, MyFitVariable.isActionModeOn);
//    }
//
//    //folder adapter listener-----------------------------------------------------------------------
//    @Override
//    public void onFolderItemViewClick(Folder folder, MaterialCheckBox checkBox) {
//        if (MyFitVariable.actionMode == null)
//            CommonUtil.navigate(mNavController, R.id.searchFragment,
//                    SearchFragmentDirections.toMainActivity(folder.getId(), 0, folder.getParentId()));
//        else {
//            checkBox.setChecked(!checkBox.isChecked());
//            mFolderAdapterArray[mModel.getCurrentItem()].itemSelected(folder.getId());
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
//    }
//
//    //size adapter listener-------------------------------------------------------------------------
//    @Override
//    public void sizeItemViewClick(SizeTop size, MaterialCheckBox checkBox) {
//        if (MyFitVariable.actionMode == null)
//            CommonUtil.navigate(mNavController, R.id.searchFragment,
//                    SearchFragmentDirections.toMainActivity(0, size.getId(), size.getParentId()));
//        else {
//            checkBox.setChecked(!checkBox.isChecked());
//            mSizeAdapterArray[mModel.getCurrentItem()].itemSelected(size.getId());
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
//        mModel.sizeUpdate(size);
//    }
//
//    private void actionModeStart(int position) {
//        if (MyFitVariable.actionMode == null) {
//            mModel.getSelectedFolderList().clear();
//            mModel.getSelectedSizeList().clear();
//            ((AppCompatActivity) requireActivity()).startSupportActionMode(mActionMode);
//        }
//        mDragSelectListenerArray[mModel.getCurrentItem()].startDragSelection(position);
//    }
//
//    //action mode listener--------------------------------------------------------------------------
//    @Override
//    public void selectAllClick(boolean isChecked) {
//        mModel.selectAllClick(isChecked, mFolderAdapterArray, mSizeAdapterArray);
//    }
//
//    @Override
//    public void actionItemClick(int itemId) {
//        if (itemId == R.id.menu_action_mode_delete)
//            CommonUtil.navigate(mNavController, R.id.searchFragment,
//                    SearchFragmentDirections.toDeleteSelectedItemDialog(mModel.getSelectedItemSize(), mModel.getSelectedItemIdArray()));
//        else if (itemId == R.id.menu_action_mode_move) {
//            mDialogViewModel.setTreeViewResources(mModel.getSelectedFolderList(), mModel.getSelectedSizeList(), null);
//            CommonUtil.navigate(mNavController, R.id.searchFragment,
//                    SearchFragmentDirections.toTreeViewDialog(mModel.getCurrentItem(), -1, -1, R.id.nav_graph_search));
//        } else
//            CommonUtil.navigate(mNavController, R.id.searchFragment,
//                    SearchFragmentDirections.toEditNameDialog(mModel.getSelectedFolderId(), Constant.ItemType.FOLDER.ordinal(), false, R.id.nav_graph_search));
//    }
//
//    //recent search adapter listener----------------------------------------------------------------
//    @Override
//    public void recentSearchItemViewClick(String word) {
//        mListenerUtil.recentSearchClick(word, mActivityBinding.acTv, mActivityBinding.acTvLayout, requireContext(), RECENT_SEARCH_SEARCH);
//    }
//
//    @Override
//    public void recentSearchDeleteClick(RecentSearch recentSearch) {
//        mModel.recentSearchDelete(recentSearch);
//    }
//}