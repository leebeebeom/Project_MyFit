package com.leebeebeom.closetnote.ui.main.list;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.leebeebeom.closetnote.NavGraphListArgs;
import com.leebeebeom.closetnote.NavGraphListDirections;
import com.leebeebeom.closetnote.R;
import com.leebeebeom.closetnote.data.tuple.BaseTuple;
import com.leebeebeom.closetnote.data.tuple.tuple.FolderTuple;
import com.leebeebeom.closetnote.data.tuple.tuple.FolderTuple2;
import com.leebeebeom.closetnote.data.tuple.tuple.SizeTuple;
import com.leebeebeom.closetnote.databinding.ActivityMainBinding;
import com.leebeebeom.closetnote.databinding.FragmentListBinding;
import com.leebeebeom.closetnote.di.Qualifiers;
import com.leebeebeom.closetnote.ui.BaseFragment;
import com.leebeebeom.closetnote.ui.main.list.adapter.folderadapter.FolderAdapter;
import com.leebeebeom.closetnote.ui.main.list.adapter.sizeadapter.SizeAdapterGrid;
import com.leebeebeom.closetnote.ui.main.list.adapter.sizeadapter.SizeAdapterList;
import com.leebeebeom.closetnote.ui.view.LockableScrollView;
import com.leebeebeom.closetnote.util.ActionModeImpl;
import com.leebeebeom.closetnote.util.CommonUtil;
import com.leebeebeom.closetnote.util.SortUtil;
import com.leebeebeom.closetnote.util.adapter.BaseAdapter;
import com.leebeebeom.closetnote.util.adapter.viewholder.BaseVHListener;
import com.leebeebeom.closetnote.util.constant.Sort;
import com.leebeebeom.closetnote.util.constant.ViewType;
import com.leebeebeom.closetnote.util.dragselect.FolderDragSelect;
import com.leebeebeom.closetnote.util.dragselect.ListDragSelect;
import com.leebeebeom.closetnote.util.dragselect.SizeDragSelect;
import com.leebeebeom.closetnote.util.popupwindow.BasePopupWindow;
import com.leebeebeom.closetnote.util.popupwindow.PopupWindowListener;
import com.google.android.material.textview.MaterialTextView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;

import static com.leebeebeom.closetnote.util.ActionModeImpl.ACTION_MODE_STRING;
import static com.leebeebeom.closetnote.util.ActionModeImpl.sActionMode;
import static com.leebeebeom.closetnote.util.ActionModeImpl.sActionModeOn;

@AndroidEntryPoint
@Accessors(prefix = "m")
public class ListFragment extends BaseFragment implements ActionModeImpl.ActionModeListener, BaseVHListener,
        BaseVHListener.SizeVHListener, PopupWindowListener.ListPopupWindowListener, ListDragSelect.ListDragSelectListener {
    public static final String THIS_FOLDER_TEXT_VIEW = "this folder text view";
    @Inject
    ActivityMainBinding mActivityBinding;
    @Inject
    ActionModeImpl mActionModeCallBack;
    @Inject
    BasePopupWindow.ListPopupWindow mPopupWindow;
    @Getter
    @Inject
    FolderAdapter mFolderAdapter;
    @Getter
    @Inject
    SizeAdapterGrid mSizeAdapterGrid;
    @Getter
    @Inject
    SizeAdapterList mSizeAdapterList;
    @Qualifiers.FolderItemTouchHelper
    @Inject
    ItemTouchHelper mFolderItemTouchHelper;
    @Qualifiers.SizeItemTouchHelperList
    @Inject
    ItemTouchHelper mSizeItemTouchHelperList;
    @Qualifiers.SizeItemTouchHelperGrid
    @Inject
    ItemTouchHelper mSizeItemTouchHelperGrid;
    @Inject
    FolderDragSelect mFolderDragSelect;
    @Inject
    SizeDragSelect mSizeDragSelect;

    private ListViewModel mModel;
    private FragmentListBinding mBinding;
    private ActionBar mActionBar;
    private long mCategoryId, mParentId;
    private int mParentIndex;
    private boolean mTextNavigation;

    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        super.onAttach(context);
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (mPopupWindow.isShowing()) mPopupWindow.dismiss();
                else mNavController.popBackStack();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mModel = new ViewModelProvider(this).get(ListViewModel.class);

        mCategoryId = NavGraphListArgs.fromBundle(getArguments()).getCategoryId();
        mParentId = NavGraphListArgs.fromBundle(getArguments()).getParentId();
        mParentIndex = NavGraphListArgs.fromBundle(getArguments()).getParentIndex();

        mModel.setParentId(mParentId);
        mModel.setCategoryId(mCategoryId);

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        addBottomAppBar();
        hideCustomTitle();
        hideSearchBar();
        fabChange(R.drawable.icon_add);

        mBinding = FragmentListBinding.inflate(inflater, container, false);
        mBinding.setListFragment(this);
        mBinding.setModel(mModel);
        mBinding.setLifecycleOwner(this);

        mFolderItemTouchHelper.attachToRecyclerView(mBinding.rvFolder.rv);
        mSizeItemTouchHelperList.attachToRecyclerView(mBinding.rvSize.rv);
        mSizeItemTouchHelperGrid.attachToRecyclerView(mBinding.rvSize.rv);

        mBinding.rvFolder.rv.addOnItemTouchListener(mFolderDragSelect);
        mBinding.rvSize.rv.addOnItemTouchListener(mSizeDragSelect);

        setFabClickListener();
        return mBinding.getRoot();
    }

    private void setFabClickListener() {
        ListFragmentDirections.ToSize action = ListFragmentDirections.toSize(mParentId, -1, mParentIndex);
        mActivityBinding.fab.setOnClickListener(v ->
                CommonUtil.navigate(mNavController, R.id.listFragment, action));
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        observeParentLive();
        mModel.getFolderPathTuplesLive().observe(getViewLifecycleOwner(), this::setTextNavigation);
        observeFolderTuplesLive();
        observeSizeTuplesLive();
        observeSelectedItemSizeLive();
        if (savedInstanceState != null && savedInstanceState.getBoolean(ACTION_MODE_STRING))
            ((AppCompatActivity) requireActivity()).startSupportActionMode(mActionModeCallBack);
    }

    private void observeParentLive() {
        mModel.getParentLive(mParentId).observe(getViewLifecycleOwner(), parent -> {
            if (parent instanceof String)
                setActionBarTitle((String) parent);
            else if (parent instanceof FolderTuple2) {
                FolderTuple2 folderTuple2 = (FolderTuple2) parent;
                setActionBarTitle(folderTuple2.getName());
                if (folderTuple2.isDeleted() || folderTuple2.isParentDeleted())
                    mNavController.popBackStack();
                mModel.setFolderId(folderTuple2.getId());
            }
        });
    }

    private void setActionBarTitle(String title) {
        ActionBar actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
        if (actionBar != null)
            actionBar.setTitle(title);
    }

    private void setTextNavigation(List<FolderTuple> folderPathTuples) {
        if (!mTextNavigation) {
            mTextNavigation = true;
            float textSize = requireContext().getResources().getDimension(R.dimen._4sdp);

            folderPathTuples.forEach(folderTuple -> {
                mBinding.textNavigation.layout.addView(getArrowIcon());
                mBinding.textNavigation.layout.addView(getFolderNameTv(folderTuple, textSize));
            });
        }
    }

    @NotNull
    public ImageView getArrowIcon() {
        Drawable arrowIcon = ResourcesCompat.getDrawable(requireActivity().getResources(), R.drawable.icon_arrow_forward, requireActivity().getTheme());
        ImageView imageView = new ImageView(requireContext());
        imageView.setImageDrawable(arrowIcon);
        imageView.setAlpha(0.8f);
        return imageView;
    }

    @NotNull
    public MaterialTextView getFolderNameTv(@NotNull FolderTuple tuple, float textSize) {
        MaterialTextView textView = new MaterialTextView(requireContext());
        textView.setAlpha(0.9f);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, textSize);
        textView.setText(tuple.getName());
        if (mParentId != tuple.getId())
            setTextNavigationTvClickListener(textView, tuple);
        else textView.setTag(THIS_FOLDER_TEXT_VIEW);
        textView.setMaxLines(1);
        return textView;
    }

    private void setTextNavigationTvClickListener(@NotNull MaterialTextView textView, FolderTuple tuple) {
        textView.setOnClickListener(v -> {
            ListFragmentDirections.ToSelf action = ListFragmentDirections.toSelf(mCategoryId, tuple.getId(), mParentIndex);
            CommonUtil.navigate(mNavController, R.id.listFragment, action);
        });
    }

    private void observeFolderTuplesLive() {
        mModel.getFolderTuplesLive().observe(getViewLifecycleOwner(), folderTuples ->
                mModel.getListSortPreferenceLive().observe(getViewLifecycleOwner(), sortInt -> {
                    SortUtil.sortCategoryFolderTuples(Sort.values()[sortInt], folderTuples);
                    folderTuples.removeIf(tuple -> tuple.getId() == -1);
                    addDummy(folderTuples);
                    mFolderAdapter.setItems(folderTuples);
                }));
    }

    private void addDummy(List<FolderTuple> tuples) {
        if (tuples != null) {
            int size = tuples.size();
            if (size % 4 == 1)
                addDummyFolder(tuples, 3);
            else if (size % 4 == 2)
                addDummyFolder(tuples, 2);
            else if (size % 4 == 3)
                addDummyFolder(tuples, 1);
        }
    }

    private void addDummyFolder(List<FolderTuple> tuples, int count) {
        for (int i = 0; i < count; i++) {
            FolderTuple dummy = new FolderTuple();
            dummy.setId(-1);
            dummy.setName("");
            tuples.add(dummy);
        }
    }

    private void observeSizeTuplesLive() {
        mModel.getSizeTuplesLive().observe(getViewLifecycleOwner(), sizeTuples ->
                mModel.getFavoriteLive().observe(getViewLifecycleOwner(), isFavorite ->
                        mModel.getListSortPreferenceLive().observe(getViewLifecycleOwner(), sortInt -> {
                            if (isFavorite)
                                sizeTuples.removeIf(sizeTuple -> !sizeTuple.isFavorite());

                            SortUtil.sortSizeTuples(Sort.values()[sortInt], sizeTuples);

                            mSizeAdapterList.setItems(sizeTuples);
                            mSizeAdapterGrid.setItems(sizeTuples);
                        })));
    }

    private void observeSelectedItemSizeLive() {
        mModel.getSelectedSizeLive().observe(getViewLifecycleOwner(), integer -> {
            if (sActionMode != null) {
                mActionModeCallBack.getBinding().tvTitle.setText(getString(R.string.action_mode_title, integer));

                int selectedSizeListSize = mSizeAdapterList.getCurrentList().size();
                List<FolderTuple> folderList = new ArrayList<>(mFolderAdapter.getCurrentList());
                folderList.removeIf(tuple -> tuple.getId() == -1);
                int selectedFolderListSize = folderList.size();
                mActionModeCallBack.getBinding().cb.setChecked(selectedSizeListSize + selectedFolderListSize == integer);

                mActionModeCallBack.getMenuItems()[0].setVisible(integer == 1 && mModel.getSelectedSizeTuples().isEmpty());
                mActionModeCallBack.getMenuItems()[1].setVisible(integer > 0);
                mActionModeCallBack.getMenuItems()[2].setVisible(integer > 0);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mTextNavigation = false;
        if (sActionMode != null) sActionMode.finish();
        mBinding = null;
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull @NotNull Menu menu) {
        MenuItem favoriteMenu = menu.getItem(1);
        MenuItem viewTypeMenu = menu.getItem(2);

        favoriteMenu.setIcon(mModel.isFavorite() ? R.drawable.icon_favorite : R.drawable.icon_favorite_border);
        viewTypeMenu.setIcon(mModel.getViewType() == ViewType.LIST_VIEW ? R.drawable.icon_list : R.drawable.icon_grid);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull @NotNull Menu menu, @NonNull @NotNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull @NotNull MenuItem item) {
        if (item.getItemId() == R.id.menu_list_view_type) {
            mModel.changeViewType();
            item.setIcon(mModel.getViewType() == ViewType.LIST_VIEW ? R.drawable.icon_list : R.drawable.icon_grid);
            return true;
        } else if (item.getItemId() == R.id.menu_list_popup) {
            mPopupWindow.showAsDropDown(requireActivity().findViewById(R.id.menu_list_popup));
            return true;
        } else if (item.getItemId() == R.id.menu_list_favorite) {
            mModel.changeFavorite();
            item.setIcon(mModel.isFavorite() ? R.drawable.icon_favorite : R.drawable.icon_favorite_border);
            return true;
        } else if (item.getItemId() == R.id.menu_list_search) {
            //TODO
//            CommonUtil.navigate(mNavController, R.id.listFragment,
//                    ListFragmentDirections.toSearchActivity());
//            return true;
        }
        return false;
    }

    @Override
    public FolderDragSelect getFolderDragSelect() {
        return mFolderDragSelect;
    }

    @Override
    public SizeDragSelect getSizeDragSelect() {
        return mSizeDragSelect;
    }

    @Override
    public LockableScrollView getScrollView() {
        return mBinding.sv;
    }

    @Override
    public RecyclerView getFolderRecyclerView() {
        return mBinding.rvFolder.rv;
    }

    @Override
    public RecyclerView getSizeRecyclerView() {
        return mBinding.rvSize.rv;
    }

    @Override
    public void onSaveInstanceState(@NonNull @NotNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(ACTION_MODE_STRING, sActionModeOn);
    }

    @Override
    public List<BaseAdapter<?, ?, ?>> getAdapters() {
        if (mModel.getViewType() == ViewType.LIST_VIEW)
            return Arrays.asList(mFolderAdapter, mSizeAdapterList);
        else return Arrays.asList(mFolderAdapter, mSizeAdapterGrid);
    }

    @Override
    public void actionItemClick(int itemId) {
        if (itemId == R.id.menu_action_mode_edit)
            navigateNameEdit();
        else if (itemId == R.id.menu_action_mode_move)
            navigateTreeView();
        else navigateFolderDelete();
    }

    @Override
    public int getResId() {
        return R.menu.menu_action_mode;
    }

    private void navigateNameEdit() {
        FolderTuple selectedTuple = mModel.getSelectedFolderTuples().iterator().next();
        NavGraphListDirections.ToEditFolderName action = NavGraphListDirections.toEditFolderName(
                selectedTuple.getId(), selectedTuple.getName(), selectedTuple.getParentId());
        CommonUtil.navigate(mNavController, R.id.listFragment, action);
    }

    private void navigateTreeView() {
        NavGraphListDirections.ToTreeView action = NavGraphListDirections.toTreeView(mParentIndex,
                mModel.getSelectedFolderIds(), mModel.getSelectedSizeIds(), mCategoryId, mParentId, mModel.getFolderPathCompleteIds());
        CommonUtil.navigate(mNavController, R.id.listFragment, action);
    }

    private void navigateFolderDelete() {
        ListFragmentDirections.ToDeleteFolderAndSizes action = ListFragmentDirections.toDeleteFolderAndSizes(
                mModel.getSelectedFolderIds(), mModel.getSelectedSizeIds());
        CommonUtil.navigate(mNavController, R.id.listFragment, action);
    }

    @Override
    public void itemViewClick(BaseTuple tuple) {
        NavDirections action;
        if (isFolderTuple(tuple))
            action = ListFragmentDirections.toSelf(mCategoryId, tuple.getId(), tuple.getParentIndex());
        else
            action = ListFragmentDirections.toSize(mParentId, tuple.getId(), tuple.getParentIndex());
        CommonUtil.navigate(mNavController, R.id.listFragment, action);
    }

    @Override
    public void itemViewLongClick(int position, BaseTuple tuple) {
        if (sActionMode == null)
            ((AppCompatActivity) requireActivity()).startSupportActionMode(mActionModeCallBack);
        if (isFolderTuple(tuple)) {
            FolderDragSelect.sFolderStart = true;
            SizeDragSelect.sSizeStart = false;
            mFolderDragSelect.startDragSelection(position);
        } else {
            FolderDragSelect.sFolderStart = false;
            SizeDragSelect.sSizeStart = true;
            mSizeDragSelect.startDragSelection(position);
        }
    }

    @Override
    public void dragStart(RecyclerView.ViewHolder viewHolder, BaseTuple tuple) {
        if (isFolderTuple(tuple))
            mFolderItemTouchHelper.startDrag(viewHolder);
        else {
            if (mModel.getViewType() == ViewType.LIST_VIEW)
                mSizeItemTouchHelperList.startDrag(viewHolder);
            else mSizeItemTouchHelperGrid.startDrag(viewHolder);
        }
    }

    @Override
    public void dragStop(BaseTuple tuple) {
        if (isFolderTuple(tuple)) {
            mModel.folderDragDrop(mFolderAdapter.getNewOrderList());
        } else {
            if (mModel.getViewType() == ViewType.LIST_VIEW)
                mModel.sizeDragDrop(mSizeAdapterList.getNewOrderList());
            else mModel.sizeDragDrop(mSizeAdapterGrid.getNewOrderList());
        }
    }

    private boolean isFolderTuple(BaseTuple tuple) {
        return tuple instanceof FolderTuple;
    }

    @Override
    public void favoriteClick(SizeTuple tuple) {
        mModel.sizeTupleUpdate(tuple);
    }

    @Override
    public void createFolderClick() {
        NavGraphListDirections.ToAddFolder action = NavGraphListDirections.toAddFolder(mParentId, mParentIndex);
        CommonUtil.navigate(mNavController, R.id.listFragment, action);
        mPopupWindow.dismiss();
    }

    @Override
    public void sortClick() {
        NavDirections action = ListFragmentDirections.toSortList();
        CommonUtil.navigate(mNavController, R.id.listFragment, action);
        mPopupWindow.dismiss();
    }

    @Override
    public void recycleBinClick() {
        //TODO
    }

    public void navigateMainFragment() {
        mNavController.popBackStack(R.id.mainFragment, true);
    }

    public void navigateSelf() {
        if (mCategoryId != mParentId)
            CommonUtil.navigate(mNavController, R.id.listFragment,
                    ListFragmentDirections.toSelf(mCategoryId, mCategoryId, mParentIndex));
    }
}