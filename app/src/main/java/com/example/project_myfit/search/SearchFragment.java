package com.example.project_myfit.search;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.ConcatAdapter;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.project_myfit.R;
import com.example.project_myfit.data.model.Folder;
import com.example.project_myfit.data.model.RecentSearch;
import com.example.project_myfit.data.model.Size;
import com.example.project_myfit.databinding.ActivitySearchBinding;
import com.example.project_myfit.databinding.FragmentSearchBinding;
import com.example.project_myfit.dialog.DialogViewModel;
import com.example.project_myfit.search.adapter.RecentSearchAdapter;
import com.example.project_myfit.search.adapter.SearchFolderAdapter;
import com.example.project_myfit.search.adapter.SearchSizeAdapter;
import com.example.project_myfit.search.adapter.SearchViewPagerAdapter;
import com.example.project_myfit.util.ActionModeImpl;
import com.example.project_myfit.util.CommonUtil;
import com.example.project_myfit.util.DragSelectImpl;
import com.example.project_myfit.util.ListenerUtil;
import com.example.project_myfit.util.MyFitVariable;
import com.example.project_myfit.util.SelectedItemTreat;
import com.example.project_myfit.util.adapter.ParentAdapter;
import com.example.project_myfit.util.adapter.viewholder.FolderVHListener;
import com.example.project_myfit.util.adapter.viewholder.SizeVHListener;
import com.example.project_myfit.util.adapter.viewholder.ViewPagerVH;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.tabs.TabLayoutMediator;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.example.project_myfit.util.MyFitConstant.ACTION_MODE;
import static com.example.project_myfit.util.MyFitConstant.BOTTOM;
import static com.example.project_myfit.util.MyFitConstant.ETC;
import static com.example.project_myfit.util.MyFitConstant.ITEM_MOVE_CONFIRM;
import static com.example.project_myfit.util.MyFitConstant.NAME_EDIT_CONFIRM;
import static com.example.project_myfit.util.MyFitConstant.OUTER;
import static com.example.project_myfit.util.MyFitConstant.RECENT_SEARCH_SEARCH;
import static com.example.project_myfit.util.MyFitConstant.SELECTED_ITEM_DELETE_CONFIRM;
import static com.example.project_myfit.util.MyFitConstant.TOP;


public class SearchFragment extends Fragment implements ViewPagerVH.ViewPagerAutoScrollListener, FolderVHListener, SizeVHListener
        , ActionModeImpl.ActionModeListener, RecentSearchAdapter.RecentSearchAdapterListener {
    private SearchViewModel mModel;
    private FragmentSearchBinding mBinding;
    private ActivitySearchBinding mActivityBinding;
    private List<String> mRecentSearchStringList;
    private boolean mAutoScrollEnable;
    private NavController mNavController;
    private DialogViewModel mDialogViewModel;
    private RecentSearchAdapter mRecentSearchAdapter;
    private TypedValue mColorControl;
    private SearchFolderAdapter[] mFolderAdapterArray;
    private SearchSizeAdapter[] mSizeAdapterArray;
    private ConcatAdapter[] mConcatAdapterArray;
    private ActionModeImpl mActionMode;
    private DragSelectImpl[] mDragSelectListenerArray;
    private ListenerUtil mListenerUtil;

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

        mBinding.vp.setAdapter(getViewPagerAdapter());
        mBinding.vp.setOffscreenPageLimit(1);
        tabLayoutInit();

        mRecentSearchAdapter = new RecentSearchAdapter(this);
        mBinding.rvRecentSearch.setHasFixedSize(true);
        mBinding.rvRecentSearch.setAdapter(mRecentSearchAdapter);

        mActivityBinding = ((SearchActivity) requireActivity()).mBinding;

        View view = mBinding.getRoot();

        ParentAdapter<?, ?>[] parentAdapterArray = new ParentAdapter[8];
        System.arraycopy(mFolderAdapterArray, 0, parentAdapterArray, 0, mFolderAdapterArray.length);
        System.arraycopy(mSizeAdapterArray, 0, parentAdapterArray, 4, mSizeAdapterArray.length);
        mActionMode = new ActionModeImpl(inflater, R.menu.menu_action_mode, this, parentAdapterArray)
                .hasViewPager(mBinding.vp, mBinding.tabLayout);
        return view;
    }

    @NotNull
    private SearchViewPagerAdapter getViewPagerAdapter() {
        mFolderAdapterArray = new SearchFolderAdapter[4];
        for (int i = 0; i < 4; i++)
            mFolderAdapterArray[i] = new SearchFolderAdapter(requireContext(), this, mModel, i);

        mSizeAdapterArray = new SearchSizeAdapter[4];
        for (int i = 0; i < 4; i++)
            mSizeAdapterArray[i] = new SearchSizeAdapter(requireContext(), this, mModel, i);

        ConcatAdapter.Config config = new ConcatAdapter.Config.Builder()
                .setStableIdMode(ConcatAdapter.Config.StableIdMode.ISOLATED_STABLE_IDS).build();

        mConcatAdapterArray = new ConcatAdapter[4];
        for (int i = 0; i < 4; i++)
            mConcatAdapterArray[i] = new ConcatAdapter(config, mFolderAdapterArray[i], mSizeAdapterArray[i]);

        return new SearchViewPagerAdapter(mConcatAdapterArray, getDragSelectListener(), this);
    }

    private DragSelectImpl[] getDragSelectListener() {
        mDragSelectListenerArray = new DragSelectImpl[4];
        for (int i = 0; i < mDragSelectListenerArray.length; i++)
            mDragSelectListenerArray[i] = new DragSelectImpl().setScrollView(mBinding.sv);
        return mDragSelectListenerArray;
    }

    private void tabLayoutInit() {
        new TabLayoutMediator(mBinding.tabLayout, mBinding.vp, (tab, position) -> {
            if (position == 0) tab.setText(TOP);
            else if (position == 1) tab.setText(BOTTOM);
            else if (position == 2) tab.setText(OUTER);
            else tab.setText(ETC);
        }).attach();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dialogLive();
        recentSearchLive();
        actionModeTitleLive();
        folderLive();
        sizeLive();
        badgeCountLive();
    }

    private void dialogLive() {
        SelectedItemTreat selectedItemTreat = new SelectedItemTreat(requireContext());

        mDialogViewModel.getBackStackEntryLive().observe(getViewLifecycleOwner(), navBackStackEntry -> {
            selectedItemDeleteLive(selectedItemTreat, navBackStackEntry);
            itemMoveLive(selectedItemTreat, navBackStackEntry);
            nameEditLive(navBackStackEntry);
        });
    }

    private void selectedItemDeleteLive(SelectedItemTreat selectedItemTreat, @NotNull NavBackStackEntry navBackStackEntry) {
        navBackStackEntry.getSavedStateHandle().getLiveData(SELECTED_ITEM_DELETE_CONFIRM).observe(navBackStackEntry, o -> {
            selectedItemTreat.folderSizeDelete(true, mModel.getSelectedFolderList(), mModel.getSelectedSizeList());
            if (MyFitVariable.actionMode != null) MyFitVariable.actionMode.finish();
        });
    }

    private void itemMoveLive(SelectedItemTreat selectedItemTreat, @NotNull NavBackStackEntry navBackStackEntry) {
        navBackStackEntry.getSavedStateHandle().getLiveData(ITEM_MOVE_CONFIRM).observe(navBackStackEntry, o -> {
            long parentId = (long) o;
            selectedItemTreat.folderSizeMove(true, parentId, mModel.getSelectedFolderList(), mModel.getSelectedSizeList());
            if (MyFitVariable.actionMode != null) MyFitVariable.actionMode.finish();
        });
    }

    private void nameEditLive(@NotNull NavBackStackEntry navBackStackEntry) {
        navBackStackEntry.getSavedStateHandle().getLiveData(NAME_EDIT_CONFIRM).observe(navBackStackEntry, o -> {
            if (MyFitVariable.actionMode != null) MyFitVariable.actionMode.finish();
        });
    }

    private void recentSearchLive() {
        mModel.getRecentSearchLive().observe(getViewLifecycleOwner(), recentSearchList -> {
            mRecentSearchAdapter.submitList(recentSearchList);

            if (mRecentSearchStringList == null) mRecentSearchStringList = new ArrayList<>();
            if (!mRecentSearchStringList.isEmpty()) mRecentSearchStringList.clear();
            for (RecentSearch recentSearch : recentSearchList)
                mRecentSearchStringList.add(recentSearch.getWord());

            if (recentSearchList.isEmpty())
                mBinding.layoutNoResult.setVisibility(View.VISIBLE);
            else mBinding.layoutNoResult.setVisibility(View.GONE);
        });
    }

    private void actionModeTitleLive() {
        mModel.getSelectedItemSizeLive().observe(getViewLifecycleOwner(), integer -> {
            if (MyFitVariable.actionMode != null) {
                mActionMode.getBinding().tvTitle.setText(getString(R.string.action_mode_title, integer));
                int allItemSize = mConcatAdapterArray[mModel.getCurrentItem()].getItemCount();
                mActionMode.getBinding().cb.setChecked(allItemSize == integer);

                mActionMode.getMenuItemList().get(0).setVisible(integer == 1 &&
                        mModel.getSelectedFolderList().size() == 1 && mModel.getSelectedSizeList().isEmpty());
                mActionMode.getMenuItemList().get(1).setVisible(integer > 0);
                mActionMode.getMenuItemList().get(2).setVisible(integer > 0);
            }
        });
    }

    private void folderLive() {
        mModel.getFolderLive().observe(getViewLifecycleOwner(), folderList -> {
            List<List<Folder>> list = Arrays.asList(new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
            for (Folder folder : folderList)
                switch (folder.getParentCategory()) {
                    case TOP:
                        list.get(0).add(folder);
                        break;
                    case BOTTOM:
                        list.get(1).add(folder);
                        break;
                    case OUTER:
                        list.get(2).add(folder);
                        break;
                    default:
                        list.get(3).add(folder);
                }
            String[] parentCategory = {TOP, BOTTOM, OUTER, ETC};
            for (int i = 0; i < mFolderAdapterArray.length; i++)
                mFolderAdapterArray[i].setItem(list.get(0), mModel.getFolderParentIdList(parentCategory[i]), mModel.getSizeParentIdList(parentCategory[i]), mActivityBinding.acTv.getText());
        });
    }

    private void sizeLive() {
        mModel.getSizeLive().observe(getViewLifecycleOwner(), sizeList -> {
            List<List<Size>> list = Arrays.asList(new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
            for (Size size : sizeList)
                switch (size.getParentCategory()) {
                    case TOP:
                        list.get(0).add(size);
                        break;
                    case BOTTOM:
                        list.get(1).add(size);
                        break;
                    case OUTER:
                        list.get(2).add(size);
                        break;
                    default:
                        list.get(3).add(size);
                }

            for (int i = 0; i < mSizeAdapterArray.length; i++)
                mSizeAdapterArray[i].setItem(list.get(i), mActivityBinding.acTv.getText());
        });
    }

    private void badgeCountLive() {
        MediatorLiveData<Integer[]> badgeCountLive = new MediatorLiveData<>();
        badgeCountLive.addSource(mModel.getFolderFilteredSizeLive(), badgeCountLive::setValue);
        badgeCountLive.addSource(mModel.getSizeFilteredSizeLive(), badgeCountLive::setValue);
        badgeCountLive.observe(getViewLifecycleOwner(), integer -> {
            if (mModel.getFolderFilteredSizeLive().getValue() != null && mModel.getSizeFilteredSizeLive().getValue() != null) {
                Integer[] countArray = new Integer[4];
                for (int i = 0; i < 4; i++)
                    countArray[i] = mModel.getFolderFilteredSizeLive().getValue()[i];

                for (int i = 0; i < 4; i++) {
                    countArray[i] = countArray[i] + mModel.getSizeFilteredSizeLive().getValue()[i];
                    CommonUtil.setBadgeCount(mBinding.tabLayout.getTabAt(i), countArray[i], mColorControl);
                }
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mListenerUtil = new ListenerUtil();
        mListenerUtil.scrollChangeListener(mBinding.sv, mActivityBinding.fabTop);
        mListenerUtil.fabTopClick(mBinding.sv, mActivityBinding.fabTop);
        mListenerUtil.autoCompleteEndIconClick(mActivityBinding.acTv, mActivityBinding.acTvLayout, requireContext());
        vpPageChangeListener();
        acImeClick();
        acTextChangeListener();
        recentSearchDeleteAllClick();

        actionModeRestore(savedInstanceState);
    }

    private void vpPageChangeListener() {
        mBinding.vp.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                mBinding.sv.smoothScrollTo(0, 0);
                mModel.setCurrentItem(position);
            }
        });
    }

    private void acImeClick() {
        mActivityBinding.acTv.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH)
                mListenerUtil.autoCompleteImeClick(mActivityBinding.acTv, requireContext(), mRecentSearchStringList, RECENT_SEARCH_SEARCH);
            return true;
        });
    }

    private void acTextChangeListener() {
        mActivityBinding.acTv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s)) {
                    mBinding.svRecentSearch.setVisibility(View.GONE);
                    if (mBinding.vp.getScrollY() == 0)
                        mActivityBinding.fabTop.setVisibility(View.GONE);
                    else mActivityBinding.fabTop.setVisibility(View.VISIBLE);
                    mActivityBinding.acTvLayout.setEndIconVisible(true);
                } else {
                    mBinding.svRecentSearch.scrollTo(0, 0);
                    mBinding.svRecentSearch.setVisibility(View.VISIBLE);
                    mActivityBinding.fabTop.setVisibility(View.GONE);
                    mActivityBinding.acTvLayout.setEndIconVisible(false);
                }
                for (int i = 0; i < 4; i++) {
                    mFolderAdapterArray[i].setWord(s);
                    mSizeAdapterArray[i].setWord(s);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void recentSearchDeleteAllClick() {
        mBinding.tvDeleteAll.setOnClickListener(v ->
                mNavController.navigate(SearchFragmentDirections.actionSearchFragmentToRecentSearchDeleteAllDialog()));
    }

    private void actionModeRestore(Bundle savedInstanceState) {
        if (savedInstanceState != null && savedInstanceState.getBoolean(ACTION_MODE)) {
            mFolderAdapterArray[mModel.getCurrentItem()].setSelectedItemList(mModel.getSelectedFolderList());
            mSizeAdapterArray[mModel.getCurrentItem()].setSelectedItemList(mModel.getSelectedSizeList());
            ((AppCompatActivity) requireActivity()).startSupportActionMode(mActionMode);
        }
    }

    @Override
    public void dragAutoScroll(int upDownStop) {
        if (MyFitVariable.isDragSelecting)
            mListenerUtil.viewPagerAutoScroll(mBinding.sv, upDownStop);
    }

    @Override
    public void onSaveInstanceState(@NonNull @NotNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(ACTION_MODE, MyFitVariable.isActionModeOn);
    }

    //folder adapter listener-----------------------------------------------------------------------
    @Override
    public void onFolderItemViewClick(Folder folder, MaterialCheckBox checkBox) {
        if (MyFitVariable.actionMode == null)
            mNavController.navigate(SearchFragmentDirections.actionSearchFragmentToMainActivity(
                    folder.getId(), 0, folder.getParentId()));
        else {
            checkBox.setChecked(!checkBox.isChecked());
            mFolderAdapterArray[mModel.getCurrentItem()].itemSelected(folder.getId());
            mModel.itemSelected(folder, checkBox.isChecked());
        }
    }

    @Override
    public void onFolderItemViewLongClick(int position) {
        actionModeStart(position);
    }

    @Override
    public void onFolderDragHandleTouch(RecyclerView.ViewHolder holder) {
    }

    //size adapter listener-------------------------------------------------------------------------
    @Override
    public void onSizeItemViewClick(Size size, MaterialCheckBox checkBox) {
        if (MyFitVariable.actionMode == null)
            mNavController.navigate(SearchFragmentDirections.actionSearchFragmentToMainActivity(0, size.getId(), size.getParentId()));
        else {
            checkBox.setChecked(!checkBox.isChecked());
            mSizeAdapterArray[mModel.getCurrentItem()].itemSelected(size.getId());
            mModel.itemSelected(size, checkBox.isChecked());
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
        if (MyFitVariable.actionMode == null) {
            mModel.getSelectedFolderList().clear();
            mModel.getSelectedSizeList().clear();
            ((AppCompatActivity) requireActivity()).startSupportActionMode(mActionMode);
        }
        mDragSelectListenerArray[mModel.getCurrentItem()].startDragSelection(position);
    }

    //action mode listener--------------------------------------------------------------------------
    @Override
    public void selectAllClick(boolean isChecked) {
        mModel.selectAllClick(isChecked, mFolderAdapterArray, mSizeAdapterArray);
    }

    @Override
    public void actionItemClick(int itemId) {
        if (itemId == R.id.menu_action_mode_delete)
            mNavController.navigate(SearchFragmentDirections.actionSearchFragmentToSearchSelectedItemDeleteDialog(mModel.getSelectedItemSize()));
        else if (itemId == R.id.menu_action_mode_move) {
            mDialogViewModel.forTreeView(mModel.getSelectedFolderList(), mModel.getSelectedSizeList(), null);
            mNavController.navigate(SearchFragmentDirections.actionSearchFragmentToSearchTreeViewDialog(mModel.getParentCategory()));
        } else
            mNavController.navigate(SearchFragmentDirections.actionSearchFragmentToSearchNameEditDialog(mModel.getSelectedFolderId()));
    }

    //recent search adapter listener----------------------------------------------------------------
    @Override
    public void recentSearchItemViewClick(String word) {
        mListenerUtil.recentSearchClick(word, mActivityBinding.acTv, mActivityBinding.acTvLayout, requireContext(), RECENT_SEARCH_SEARCH);
    }

    @Override
    public void recentSearchDeleteClick(RecentSearch recentSearch) {
        mModel.recentSearchDelete(recentSearch);
    }
}