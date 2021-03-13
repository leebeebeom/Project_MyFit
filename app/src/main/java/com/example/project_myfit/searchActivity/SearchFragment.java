package com.example.project_myfit.searchActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
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
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.project_myfit.MainActivity;
import com.example.project_myfit.R;
import com.example.project_myfit.databinding.ActionModeTitleBinding;
import com.example.project_myfit.databinding.FragmentSearchBinding;
import com.example.project_myfit.dialog.AddFolderDialog;
import com.example.project_myfit.dialog.FolderNameEditDialog;
import com.example.project_myfit.dialog.ItemMoveDialog;
import com.example.project_myfit.dialog.SelectedItemDeleteDialog;
import com.example.project_myfit.dialog.TreeViewDialog;
import com.example.project_myfit.searchActivity.adapter.RecentSearchAdapter;
import com.example.project_myfit.searchActivity.adapter.SearchAdapter;
import com.example.project_myfit.searchActivity.adapter.SearchViewPagerAdapter;
import com.example.project_myfit.searchActivity.database.RecentSearch;
import com.example.project_myfit.ui.main.listfragment.database.Folder;
import com.example.project_myfit.ui.main.listfragment.database.Size;
import com.example.project_myfit.ui.main.listfragment.treeview.TreeHolderCategory;
import com.example.project_myfit.ui.main.listfragment.treeview.TreeHolderFolder;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputLayout;
import com.michaelflisar.dragselectrecyclerview.DragSelectTouchListener;
import com.unnamed.b.atv.model.TreeNode;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.example.project_myfit.MyFitConstant.ACTION_MODE_OFF;
import static com.example.project_myfit.MyFitConstant.ACTION_MODE_ON;
import static com.example.project_myfit.MyFitConstant.BOTTOM;
import static com.example.project_myfit.MyFitConstant.DELETE_DIALOG;
import static com.example.project_myfit.MyFitConstant.ETC;
import static com.example.project_myfit.MyFitConstant.FOLDER_EDIT_DIALOG;
import static com.example.project_myfit.MyFitConstant.FOLDER_ID;
import static com.example.project_myfit.MyFitConstant.MOVE_DIALOG;
import static com.example.project_myfit.MyFitConstant.OUTER;
import static com.example.project_myfit.MyFitConstant.SIZE_ID;
import static com.example.project_myfit.MyFitConstant.TOP;
import static com.example.project_myfit.MyFitConstant.TREE_ADD_FOLDER;
import static com.example.project_myfit.MyFitConstant.TREE_DIALOG;

//TODO 아이콘
//TODO 하트
//TODO 폴더 아이템 갯수
//TODO 액션모드 리크리에이트
//TODO 트리뷰 회색되는거 안되게
//TODO 서치 누르면 오토컴플리트 안나오게
//TODO 애니메이션 변경
//TODO 인풋아웃풋 브랜드 리스트
//TODO 액션모드시 키보드 숨기기
//TODO 바인드 될때 잠깐 나왔다 사라지ㄴ는거 수정
//TODO 최근 검색 전체삭제 추가

public class SearchFragment extends Fragment implements SearchAdapter.SearchAdapterListener, SelectedItemDeleteDialog.SelectedItemDeleteConfirmClick
        , FolderNameEditDialog.FolderNameEditConfirmClick, TreeViewDialog.TreeViewAddClick, AddFolderDialog.TreeAddFolderConfirmClick,
        TreeNode.TreeNodeClickListener, ItemMoveDialog.ItemMoveConfirmClick, SearchViewPagerAdapter.SearchDragAutoScrollListener {
    private SearchViewModel mModel;
    private FragmentSearchBinding mBinding;
    private List<String> mRecentSearchStringList;
    private MaterialAutoCompleteTextView mAutoCompleteTextView;
    private TextInputLayout mAutoCompleteTextLayout;
    private ActionMode mActionMode;
    private boolean mActionModeOn, isDragSelecting, mScrollEnable;
    private ActionModeTitleBinding mActionModeTitleBinding;
    private SearchViewPagerAdapter mSearchViewPagerAdapter;
    private MenuItem mEditMenu, mMoveMenu, mDeletedMenu;
    private DragSelectTouchListener mDragSelectListener;
    private final ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(@NotNull ActionMode mode, Menu menu) {
            mBinding.viewPager.setUserInputEnabled(false);

            mActionMode = mode;
            mActionModeOn = true;

            LinearLayout tab = (LinearLayout) mBinding.tabLayout.getChildAt(0);
            for (int i = 0; i < tab.getChildCount(); i++) {
                tab.getChildAt(i).setClickable(false);
            }

            mode.getMenuInflater().inflate(R.menu.list_action_mode, menu);
            mode.setCustomView(mActionModeTitleBinding.getRoot());

            mSearchViewPagerAdapter.setActionModeState(ACTION_MODE_ON);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, @NotNull Menu menu) {
            mActionModeTitleBinding.actionModeSelectAll.setOnClickListener(v -> {
                mModel.getSelectedItem().clear();
                if (mActionModeTitleBinding.actionModeSelectAll.isChecked()) {
                    mModel.getSelectedItem().addAll(mSearchViewPagerAdapter.getSearchAdapterList().get(mBinding.viewPager.getCurrentItem()).getCurrentList());
                    mSearchViewPagerAdapter.getSearchAdapterList().get(mBinding.viewPager.getCurrentItem()).selectAll();
                } else
                    mSearchViewPagerAdapter.getSearchAdapterList().get(mBinding.viewPager.getCurrentItem()).deselectAll();
                mModel.setSelectedAmount();
            });
            mEditMenu = menu.getItem(0);
            mMoveMenu = menu.getItem(1);
            mDeletedMenu = menu.getItem(2);
            return true;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, @NotNull MenuItem item) {
            if (item.getItemId() == R.id.action_mode_del) {
                if (mModel.getSelectedAmount().getValue() != null)
                    showDialog(SelectedItemDeleteDialog.getInstance(mModel.getSelectedAmount().getValue()), DELETE_DIALOG);
            } else if (item.getItemId() == R.id.action_mode_move)
                showDialog(new TreeViewDialog(), TREE_DIALOG);
            else
                showDialog(FolderNameEditDialog.getInstance(((Folder) mModel.getSelectedItem().get(0)).getFolderName(), false), FOLDER_EDIT_DIALOG);
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mBinding.viewPager.setUserInputEnabled(true);

            mActionMode = null;
            mActionModeOn = false;

            LinearLayout tab = (LinearLayout) mBinding.tabLayout.getChildAt(0);
            for (int i = 0; i < tab.getChildCount(); i++) {
                tab.getChildAt(i).setClickable(true);
            }

            mModel.setSelectedPosition(mSearchViewPagerAdapter.getSearchAdapterList().get(mBinding.viewPager.getCurrentItem()).getSelectedPosition());
            mSearchViewPagerAdapter.setActionModeState(ACTION_MODE_OFF);

            mActionModeTitleBinding.actionModeSelectAll.setChecked(false);
            ((ViewGroup) mActionModeTitleBinding.getRoot().getParent()).removeAllViews();
        }
    };

    private void showDialog(@NotNull DialogFragment dialog, String tag) {
        dialog.setTargetFragment(this, 0);
        dialog.show(requireActivity().getSupportFragmentManager(), tag);
    }

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mModel = new ViewModelProvider(this).get(SearchViewModel.class);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        dragSelectInit();
        mSearchViewPagerAdapter = new SearchViewPagerAdapter(getSearchAdapters(), mDragSelectListener, this);

        mBinding = FragmentSearchBinding.inflate(inflater);
        mActionModeTitleBinding = ActionModeTitleBinding.inflate(inflater);
        View view = mBinding.getRoot();
        mBinding.viewPager.setAdapter(mSearchViewPagerAdapter);
        scrollChangeListener();
        mBinding.searchFab.setOnClickListener(v -> {
            mBinding.viewPagerLayout.scrollTo(0, 0);
            mBinding.viewPagerLayout.smoothScrollTo(0, 0);
        });
        mBinding.viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                mBinding.viewPagerLayout.smoothScrollTo(0, 0);
                mModel.setParentCategory(position);
            }
        });
        return view;
    }

    private void dragSelectInit() {
        DragSelectTouchListener.OnAdvancedDragSelectListener listener = new DragSelectTouchListener.OnAdvancedDragSelectListener() {
            @Override
            public void onSelectionStarted(int i) {
                mBinding.viewPagerLayout.setScrollable(false);
                isDragSelecting = true;
                RecyclerView recyclerView = mBinding.viewPager.getChildAt(0).findViewById(R.id.search_recyclerView);
                RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForLayoutPosition(i);
                if (viewHolder != null) viewHolder.itemView.callOnClick();
            }

            @Override
            public void onSelectionFinished(int i) {
                mBinding.viewPagerLayout.setScrollable(true);
                isDragSelecting = false;
            }

            @Override
            public void onSelectChange(int i, int i1, boolean b) {
                RecyclerView recyclerView = mBinding.viewPager.getChildAt(0).findViewById(R.id.search_recyclerView);
                for (int j = i; j <= i1; j++) {
                    RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForLayoutPosition(j);
                    if (viewHolder != null) viewHolder.itemView.callOnClick();
                }
            }
        };
        mDragSelectListener = new DragSelectTouchListener().withSelectListener(listener);
    }

    private void scrollChangeListener() {
        mBinding.viewPagerLayout.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (isDragSelecting && mScrollEnable && scrollY > oldScrollY)
                v.postDelayed(() -> v.scrollBy(0, 1), 50);
            else if (isDragSelecting && mScrollEnable && scrollY < oldScrollY)
                v.postDelayed(() -> v.scrollBy(0, -1), 50);

            if (oldScrollY < scrollY) mBinding.searchFab.show();
            else if (scrollY == 0) mBinding.searchFab.hide();
        });
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAutoCompleteTextView = requireActivity().findViewById(R.id.auto_complete_text_view);
        mAutoCompleteTextLayout = requireActivity().findViewById(R.id.auto_complete_text_layout);
        autoCompleteImeListener();
        autoCompleteTextChangeListener();
    }

    @NotNull
    private List<SearchAdapter> getSearchAdapters() {
        SearchAdapter topAdapter = new SearchAdapter(this, mModel);
        SearchAdapter bottomAdapter = new SearchAdapter(this, mModel);
        SearchAdapter outerAdapter = new SearchAdapter(this, mModel);
        SearchAdapter etcAdapter = new SearchAdapter(this, mModel);
        return Arrays.asList(topAdapter, bottomAdapter, outerAdapter, etcAdapter);
    }

    private void setSearchAdapterData(List<SearchAdapter> searchAdapterList, @Nullable List<Folder> allFolderList, @Nullable List<Size> allSizeList) {
        List<Object> topList = new ArrayList<>();
        List<Object> bottomList = new ArrayList<>();
        List<Object> outerList = new ArrayList<>();
        List<Object> etcList = new ArrayList<>();

        if (allFolderList == null) allFolderList = mModel.getRepository().getAllFolder();
        if (allSizeList == null) allSizeList = mModel.getRepository().getAllSize();

        for (Folder f : allFolderList) {
            switch (f.getParentCategory()) {
                case TOP:
                    topList.add(f);
                    break;
                case BOTTOM:
                    bottomList.add(f);
                    break;
                case OUTER:
                    outerList.add(f);
                    break;
                case ETC:
                    etcList.add(f);
                    break;
            }
        }
        for (Size s : allSizeList) {
            switch (s.getParentCategory()) {
                case TOP:
                    topList.add(s);
                    break;
                case BOTTOM:
                    bottomList.add(s);
                    break;
                case OUTER:
                    outerList.add(s);
                    break;
                case ETC:
                    etcList.add(s);
                    break;
            }
        }

        searchAdapterList.get(0).setItem(topList, mModel.getRepository().getFolderFolderIdByParent(TOP), mModel.getRepository().getSizeFolderIdByParent(TOP));
        searchAdapterList.get(1).setItem(bottomList, mModel.getRepository().getFolderFolderIdByParent(BOTTOM), mModel.getRepository().getSizeFolderIdByParent(BOTTOM));
        searchAdapterList.get(2).setItem(outerList, mModel.getRepository().getFolderFolderIdByParent(OUTER), mModel.getRepository().getSizeFolderIdByParent(OUTER));
        searchAdapterList.get(3).setItem(etcList, mModel.getRepository().getFolderFolderIdByParent(ETC), mModel.getRepository().getSizeFolderIdByParent(ETC));
        searchAdapterList.get(0).getFilter().filter(mAutoCompleteTextView.getText());
        searchAdapterList.get(1).getFilter().filter(mAutoCompleteTextView.getText());
        searchAdapterList.get(2).getFilter().filter(mAutoCompleteTextView.getText());
        searchAdapterList.get(3).getFilter().filter(mAutoCompleteTextView.getText());
    }

    private void autoCompleteImeListener() {
        mAutoCompleteTextView.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mAutoCompleteTextLayout.getWindowToken(), 0);
                String word = mAutoCompleteTextView.getText().toString().trim();
                if (!mRecentSearchStringList.contains(word) && !TextUtils.isEmpty(word))
                    mModel.getRepository().insertRecentSearch(new RecentSearch(word, getCurrentDate()));
            }
            return true;
        });
    }

    private void autoCompleteTextChangeListener() {
        TypedValue typedValue = new TypedValue();
        requireContext().getTheme().resolveAttribute(R.attr.myColorControl, typedValue, true);

        mAutoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s)) {
                    mAutoCompleteTextLayout.setEndIconMode(TextInputLayout.END_ICON_CLEAR_TEXT);
                    mBinding.recentSearchLayout.setVisibility(View.GONE);
                } else {
                    mBinding.recentSearchLayout.setVisibility(View.VISIBLE);
                    mBinding.searchFab.setVisibility(View.GONE);
                }

                List<SearchAdapter> searchAdapterList = mSearchViewPagerAdapter.getSearchAdapterList();
                searchAdapterList.get(0).getFilter().filter(s, count1 -> {
                    TabLayout.Tab topTab = mBinding.tabLayout.getTabAt(0);
                    if (topTab != null) {
                        if (count1 == 0) topTab.getOrCreateBadge().setVisible(false);
                        else {
                            topTab.getOrCreateBadge().setVisible(true);
                            topTab.getOrCreateBadge().setNumber(count1);
                            topTab.getOrCreateBadge().setBackgroundColor(typedValue.data);
                        }
                    }
                });
                searchAdapterList.get(1).getFilter().filter(s, count1 -> {
                    TabLayout.Tab bottomTab = mBinding.tabLayout.getTabAt(1);
                    if (bottomTab != null) {
                        if (count1 == 0) bottomTab.getOrCreateBadge().setVisible(false);
                        else {
                            bottomTab.getOrCreateBadge().setVisible(true);
                            bottomTab.getOrCreateBadge().setNumber(count1);
                            bottomTab.getOrCreateBadge().setBackgroundColor(typedValue.data);
                        }
                    }
                });
                searchAdapterList.get(2).getFilter().filter(s, count1 -> {
                    TabLayout.Tab outerTab = mBinding.tabLayout.getTabAt(2);
                    if (outerTab != null) {
                        if (count1 == 0) outerTab.getOrCreateBadge().setVisible(false);
                        else {
                            outerTab.getOrCreateBadge().setVisible(true);
                            outerTab.getOrCreateBadge().setNumber(count1);
                            outerTab.getOrCreateBadge().setBackgroundColor(typedValue.data);
                        }
                    }
                });
                searchAdapterList.get(3).getFilter().filter(s, count1 -> {
                    TabLayout.Tab etcTap = mBinding.tabLayout.getTabAt(3);
                    if (etcTap != null) {
                        if (count1 == 0) etcTap.getOrCreateBadge().setVisible(false);
                        else {
                            etcTap.getOrCreateBadge().setVisible(true);
                            etcTap.getOrCreateBadge().setNumber(count1);
                            etcTap.getOrCreateBadge().setBackgroundColor(typedValue.data);
                        }
                    }
                });
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    @NotNull
    private String getCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM월 dd일", Locale.getDefault());
        return dateFormat.format(new Date(System.currentTimeMillis()));
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        RecentSearchAdapter recentSearchAdapter = new RecentSearchAdapter();
        setSearchViewLiveData();
        setRecentSearchLive(recentSearchAdapter);
        recentSearchAdapterClick(recentSearchAdapter);
        mBinding.recentSearchRecycler.setAdapter(recentSearchAdapter);
        mModel.getSelectedAmount().observe(getViewLifecycleOwner(), integer -> {
            String title = integer + getString(R.string.item_selected);
            mActionModeTitleBinding.actionModeTitle.setText(title);

            if (mActionMode != null) {
                boolean isFolder = false;
                if (!mModel.getSelectedItem().isEmpty() && mModel.getSelectedItem().get(0) instanceof Folder)
                    isFolder = true;
                mEditMenu.setVisible(integer == 1 && isFolder);
                mMoveMenu.setVisible(integer > 0);
                mDeletedMenu.setVisible(integer > 0);
            }

            int allItemSize = mSearchViewPagerAdapter.getSearchAdapterList().get(mBinding.viewPager.getCurrentItem()).getItemCount();
            mActionModeTitleBinding.actionModeSelectAll.setChecked(allItemSize == integer);
        });

        tabLayoutInit();
    }

    private void setSearchViewLiveData() {
        List<SearchAdapter> adapterList = mSearchViewPagerAdapter.getSearchAdapterList();
        mModel.getRepository().getAllFolderLive().observe(getViewLifecycleOwner(), folderList -> setSearchAdapterData(adapterList, folderList, null));
        mModel.getRepository().getAllSizeLive().observe(getViewLifecycleOwner(), sizeList -> setSearchAdapterData(adapterList, null, sizeList));
        MutableLiveData<List<Object>> combineList = new MutableLiveData<>();

    }

    private void setRecentSearchLive(RecentSearchAdapter recentSearchAdapter) {
        mModel.getRepository().getRecentSearchLive().observe(getViewLifecycleOwner(), recentSearches -> {
            mRecentSearchStringList = new ArrayList<>();
            for (RecentSearch recentSearch : recentSearches)
                mRecentSearchStringList.add(recentSearch.getWord());
            recentSearchAdapter.submitList(recentSearches);
            if (recentSearches.size() == 0) mBinding.recentSearchNoData.setVisibility(View.VISIBLE);
            else mBinding.recentSearchNoData.setVisibility(View.GONE);
        });
    }

    private void recentSearchAdapterClick(@NotNull RecentSearchAdapter recentSearchAdapter) {
        recentSearchAdapter.setRecentSearchAdapterListener(new RecentSearchAdapter.RecentSearchAdapterListener() {
            @Override
            public void recentSearchItemClick(String word) {
                mAutoCompleteTextView.setText(word);
                mAutoCompleteTextView.setSelection(word.length());
            }

            @Override
            public void recentSearchDeleteClick(RecentSearch recentSearch) {
                mModel.getRepository().deleteRecentSearch(recentSearch);
            }
        });
    }

    private void tabLayoutInit() {
        new TabLayoutMediator(mBinding.tabLayout, mBinding.viewPager, (tab, position) -> {
            if (position == 0) tab.setText(TOP);
            else if (position == 1) tab.setText(BOTTOM);
            else if (position == 2) tab.setText(OUTER);
            else tab.setText(ETC);
        }).attach();
    }


    @Override
    public void searchAdapterSizeClick(@NotNull Size size, MaterialCheckBox checkBox, int position) {
        if (mActionMode == null) {
            Intent intent = new Intent(requireActivity(), MainActivity.class);
            intent.putExtra(SIZE_ID, size.getId());
            startActivity(intent);
        } else {
            checkBox.setChecked(!checkBox.isChecked());
            mModel.sizeSelected(size, checkBox.isChecked(), position, mSearchViewPagerAdapter.getSearchAdapterList().get(mBinding.viewPager.getCurrentItem()));
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
    public void searchAdapterFolderClick(@NotNull Folder folder, MaterialCheckBox checkBox, int position) {
        if (mActionMode == null) {
            Intent intent = new Intent(requireActivity(), MainActivity.class);
            intent.putExtra(FOLDER_ID, folder.getId());
            startActivity(intent);
        } else {
            checkBox.setChecked(!checkBox.isChecked());
            mModel.folderSelected(folder, checkBox.isChecked(), position, mSearchViewPagerAdapter.getSearchAdapterList().get(mBinding.viewPager.getCurrentItem()));
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
    public void selectedItemDeleteConfirmClick() {
        TabLayout.Tab currentTab = mBinding.tabLayout.getTabAt(mBinding.viewPager.getCurrentItem());
        if (currentTab != null) {
            int numb = currentTab.getOrCreateBadge().getNumber();
            numb = numb - mModel.getSelectedItem().size();
            if (numb == 0)
                currentTab.getOrCreateBadge().setVisible(false);
            else
                currentTab.getOrCreateBadge().setNumber(numb);
        }
        mModel.selectedItemDelete();
        mActionMode.finish();
    }

    @Override
    public void folderNameEditConfirmClick(String folderName, boolean isParentName) {
        mActionMode.finish();
        ((Folder) mModel.getSelectedItem().get(0)).setFolderName(folderName);
        mModel.getRepository().folderUpdate(((Folder) mModel.getSelectedItem().get(0)));
    }

    @Override
    public void treeViewCategoryAddClick(TreeNode node, TreeHolderCategory.CategoryTreeHolder
            value) {
        mModel.nodeAddClick(node, value);
        showDialog(AddFolderDialog.getInstance(true), TREE_ADD_FOLDER);
    }

    @Override
    public void treeViewFolderAddClick(TreeNode node, TreeHolderFolder.FolderTreeHolder value) {
        mModel.nodeAddClick(node, value);
        showDialog(AddFolderDialog.getInstance(true), TREE_ADD_FOLDER);
    }

    @Override
    public void treeAddFolderConfirmClick(String folderName) {
        TreeNode oldNode = mModel.getAddNode();

        List<Folder> selectedFolder = new ArrayList<>();
        List<Size> selectedSize = new ArrayList<>();
        List<Folder> allFolderList = mModel.getRepository().getAllFolderListByParent(mModel.getParentCategory());
        for (Object o : mModel.getSelectedItem()) {
            if (o instanceof Folder) selectedFolder.add((Folder) o);
            else if (o instanceof Size) selectedSize.add((Size) o);
        }

        if (mModel.getCategoryAddValue() != null) {//category node
            TreeHolderCategory.CategoryTreeHolder oldViewHolder = mModel.getCategoryAddValue();

            int largestOrder = mModel.getRepository().getFolderLargestOrder() + 1;
            Folder folder = new Folder(mModel.getCurrentTime(), folderName.trim(), oldViewHolder.category.getId(), largestOrder, mModel.getParentCategory());
            mModel.getRepository().folderInsert(folder);

            //find node(recreate)
            List<TreeNode> categoryNodeList = mModel.getTreeNodeRoot().getChildren();
            for (TreeNode newNode : categoryNodeList) {
                TreeHolderCategory.CategoryTreeHolder newViewHolder = (TreeHolderCategory.CategoryTreeHolder) newNode.getValue();
                if (oldViewHolder.category.getId() == newViewHolder.category.getId()) {
                    oldNode = newNode;
                    break;
                }
            }

            List<Long> folderFolderIdList = mModel.getRepository().getFolderFolderIdByParent(mModel.getParentCategory());
            List<Long> sizeFolderIdList = mModel.getRepository().getSizeFolderIdByParent(mModel.getParentCategory());

            TreeNode treeNode = new TreeNode(new TreeHolderFolder.FolderTreeHolder(folder, 40,
                    this, selectedFolder, selectedSize, allFolderList, folderFolderIdList, sizeFolderIdList))
                    .setViewHolder(new TreeHolderFolder(requireContext()));
            oldNode.getViewHolder().getTreeView().addNode(oldNode, treeNode);
            oldNode.getViewHolder().getTreeView().expandNode(oldNode);
            ((TreeHolderCategory) oldNode.getViewHolder()).setIconClickable();

            TreeHolderCategory.CategoryTreeHolder newViewHolder = (TreeHolderCategory.CategoryTreeHolder) oldNode.getValue();
            TreeHolderCategory newViewHolder2 = (TreeHolderCategory) oldNode.getViewHolder();
            String amount = String.valueOf(Integer.parseInt(String.valueOf(newViewHolder2.getBinding().amount.getText())) + 1);
            newViewHolder2.getBinding().amount.setText(amount);
        } else {//folder node
            TreeHolderFolder.FolderTreeHolder oldViewHolder = mModel.getFolderAddValue();

            int largestOrder = mModel.getRepository().getFolderLargestOrder() + 1;
            Folder folder = new Folder(mModel.getCurrentTime(), folderName.trim(), oldViewHolder.getFolder().getId(), largestOrder, mModel.getParentCategory());
            mModel.getRepository().folderInsert(folder);

            //find node (recreate)
            for (TreeNode folderNode : getAllFolderNode()) {
                TreeHolderFolder.FolderTreeHolder newViewHolder = (TreeHolderFolder.FolderTreeHolder) folderNode.getValue();
                if (oldViewHolder.getFolder().getId() == newViewHolder.getFolder().getId()) {
                    oldNode = folderNode;
                    break;
                }
            }

            List<Long> folderFolderIdList = mModel.getRepository().getFolderFolderIdByParent(mModel.getParentCategory());
            List<Long> sizeFolderIdList = mModel.getRepository().getSizeFolderIdByParent(mModel.getParentCategory());

            int margin = (int) getResources().getDimension(R.dimen._8sdp);
            TreeNode treeNode = new TreeNode(new TreeHolderFolder.FolderTreeHolder(folder,
                    oldViewHolder.getMargin() + margin, this, selectedFolder, selectedSize, allFolderList, folderFolderIdList, sizeFolderIdList))
                    .setViewHolder(new TreeHolderFolder(requireContext()));
            oldNode.getViewHolder().getTreeView().addNode(oldNode, treeNode);
            oldNode.getViewHolder().getTreeView().expandNode(oldNode);
            ((TreeHolderFolder) oldNode.getViewHolder()).setIconClickable();

            TreeHolderFolder.FolderTreeHolder newViewHolder = (TreeHolderFolder.FolderTreeHolder) oldNode.getValue();
            TreeHolderFolder newViewHolder2 = (TreeHolderFolder) oldNode.getViewHolder();
            String amount = String.valueOf(Integer.parseInt(String.valueOf(newViewHolder2.getBinding().amount.getText())) + 1);
            newViewHolder2.getBinding().amount.setText(amount);

            Folder dummy = newViewHolder.getFolder();
            if (dummy.getDummy() == 0) dummy.setDummy(1);
            else dummy.setDummy(0);
            mModel.getRepository().folderUpdate(dummy);
        }
    }

    @NotNull
    private List<TreeNode> getAllFolderNode() {
        List<TreeNode> categoryNodeList = mModel.getTreeNodeRoot().getChildren();
        List<TreeNode> folderNodeList = new ArrayList<>();
        for (TreeNode categoryNode : categoryNodeList) {
            if (categoryNode.getChildren().size() != 0)
                folderNodeList.addAll(categoryNode.getChildren());
        }
        List<TreeNode> newFolderList = new ArrayList<>(folderNodeList);
        listingFolderNode(folderNodeList, newFolderList);
        return newFolderList;
    }

    private void listingFolderNode
            (@NotNull List<TreeNode> folderNodeList, List<TreeNode> newFolderList) {
        for (TreeNode folderNode : folderNodeList) {
            if (folderNode.getChildren().size() != 0) {
                newFolderList.addAll(folderNode.getChildren());
                listingFolderNode(folderNode.getChildren(), newFolderList);
            }
        }
    }

    @Override
    public void onClick(TreeNode node, Object value) {
        if (value instanceof TreeHolderCategory.CategoryTreeHolder && mModel.getSelectedAmount().getValue() != null) {//category node click
            TreeHolderCategory.CategoryTreeHolder categoryHolder = (TreeHolderCategory.CategoryTreeHolder) node.getValue();
            showDialog(ItemMoveDialog.getInstance(mModel.getSelectedAmount().getValue(), categoryHolder.category.getId()), MOVE_DIALOG);
        } else if (value instanceof TreeHolderFolder.FolderTreeHolder && ((TreeHolderFolder) node.getViewHolder()).isNotSelected() && mModel.getSelectedAmount().getValue() != null) {
            TreeHolderFolder.FolderTreeHolder folderHolder = (TreeHolderFolder.FolderTreeHolder) node.getValue();
            showDialog(ItemMoveDialog.getInstance(mModel.getSelectedAmount().getValue(), folderHolder.getFolder().getId()), MOVE_DIALOG);
        }
    }

    @Override
    public void itemMoveConfirmClick(long folderId) {
        mModel.selectedItemMove(folderId);
        TreeViewDialog treeViewDialog = ((TreeViewDialog) getParentFragmentManager().findFragmentByTag(TREE_DIALOG));
        if (treeViewDialog != null) treeViewDialog.dismiss();
        mActionMode.finish();
    }

    @Override
    public void dragAutoScroll(int upDown) {
        if (isDragSelecting)
            if (upDown == 0) {
                mBinding.viewPagerLayout.scrollBy(0, 1);
                mScrollEnable = true;
            } else if (upDown == 1) {
                mBinding.viewPagerLayout.scrollBy(0, -1);
                mScrollEnable = true;
            } else if (upDown == 2) {
                mBinding.viewPagerLayout.scrollBy(0, 0);
                mScrollEnable = false;
            }
    }
}