package com.example.myfit.ui.main.list;

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
import androidx.hilt.navigation.HiltViewModelFactory;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myfit.R;
import com.example.myfit.data.model.model.Folder;
import com.example.myfit.data.tuple.BaseTuple;
import com.example.myfit.data.tuple.tuple.CategoryTuple;
import com.example.myfit.data.tuple.tuple.FolderTuple;
import com.example.myfit.data.tuple.tuple.SizeTuple;
import com.example.myfit.databinding.ActivityMainBinding;
import com.example.myfit.databinding.FragmentListBinding;
import com.example.myfit.di.Qualifiers;
import com.example.myfit.ui.BaseFragment;
import com.example.myfit.ui.main.MainGraphViewModel;
import com.example.myfit.ui.main.list.adapter.folderadapter.FolderAdapter;
import com.example.myfit.ui.main.list.adapter.sizeadapter.SizeAdapterGrid;
import com.example.myfit.ui.main.list.adapter.sizeadapter.SizeAdapterList;
import com.example.myfit.util.ActionModeImpl;
import com.example.myfit.util.CommonUtil;
import com.example.myfit.util.LockableScrollView;
import com.example.myfit.util.adapter.BaseAdapter;
import com.example.myfit.util.adapter.viewholder.BaseVHListener;
import com.example.myfit.util.constant.ViewType;
import com.example.myfit.util.dragselect.FolderDragSelect;
import com.example.myfit.util.dragselect.SizeDragSelect;
import com.example.myfit.util.popupwindow.ListPopupWindow;
import com.example.myfit.util.popupwindow.PopupWindowListener;
import com.google.android.material.textview.MaterialTextView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;

import static com.example.myfit.ui.dialog.BaseDialog.ACTION_MODE_OFF;
import static com.example.myfit.util.ActionModeImpl.ACTION_MODE_STRING;
import static com.example.myfit.util.ActionModeImpl.sActionMode;
import static com.example.myfit.util.ActionModeImpl.sActionModeOn;

@AndroidEntryPoint
@Accessors(prefix = "m")
public class ListFragment extends BaseFragment implements ActionModeImpl.ActionModeListener, BaseVHListener, BaseVHListener.SizeVHListener, PopupWindowListener.ListPopupWindowListener {
    public static final String THIS_FOLDER_TEXT_VIEW = "this folder text view";
    @Inject
    NavController mNavController;
    @Inject
    ActivityMainBinding mActivityBinding;
    @Inject
    ActionModeImpl.ListActionModeCallBack mActionModeCallBack;
    @Qualifiers.FolderItemTouchHelper
    @Inject
    ItemTouchHelper mTouchHelperFolder;
    @Qualifiers.SizeItemTouchHelperList
    @Inject
    ItemTouchHelper mTouchHelperList;
    @Qualifiers.SizeItemTouchHelperGrid
    @Inject
    ItemTouchHelper mTouchHelperGrid;
    @Inject
    FolderDragSelect mFolderDragSelect;
    @Inject
    SizeDragSelect mSizeDragSelect;
    @Inject
    @Getter
    FolderAdapter mFolderAdapter;
    @Inject
    @Getter
    SizeAdapterList mSizeAdapterList;
    @Inject
    @Getter
    SizeAdapterGrid mSizeAdapterGrid;
    @Qualifiers.NavigationTextViewSize
    @Inject
    float mNavigationTextViewSize;
    @Inject
    ListPopupWindow mPopupWindow;

    private ListViewModel mModel;
    private FragmentListBinding mBinding;
    private ActionBar mActionBar;
    private long mCategoryId, mParentId;
    private int mParentIndex;

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

        mActionModeCallBack.setListener(this);
        mFolderAdapter.setListener(this);
        mSizeAdapterList.setListener(this);
        mSizeAdapterGrid.setListener(this);
        mPopupWindow.setListener(this);

        mCategoryId = ListFragmentArgs.fromBundle(getArguments()).getCategoryId();
        mParentId = ListFragmentArgs.fromBundle(getArguments()).getParentId();
        mModel.setParentId(mParentId);
        mParentIndex = ListFragmentArgs.fromBundle(getArguments()).getParentIndex();
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = FragmentListBinding.inflate(inflater, container, false);
        mBinding.setListFragment(this);
        mBinding.setModel(mModel);
        mBinding.setLifecycleOwner(this);

        mTouchHelperFolder.attachToRecyclerView(mBinding.rvFolder);
        mTouchHelperList.attachToRecyclerView(mBinding.rvSize);
        mTouchHelperGrid.attachToRecyclerView(mBinding.rvSize);

        mBinding.rvFolder.addOnItemTouchListener(mFolderDragSelect);
        mFolderDragSelect.setScrollView(mBinding.sv);
        mFolderDragSelect.setRecyclerViews(mBinding.rvFolder, mBinding.rvSize);

        mBinding.rvSize.addOnItemTouchListener(mSizeDragSelect);
        mSizeDragSelect.setScrollView(mBinding.sv);
        mSizeDragSelect.setRecyclerViews(mBinding.rvFolder, mBinding.rvSize);

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
        mModel.getFolderTuplesLive().observe(getViewLifecycleOwner(), folderTuples -> mFolderAdapter.setItems(mModel.getSort(), folderTuples));
        observeSizeTuplesLive();
        observeMainGraphDialogLive();
        observeSelectedItemSizeLive();

        if (savedInstanceState != null && savedInstanceState.getBoolean(ACTION_MODE_STRING))
            ((AppCompatActivity) requireActivity()).startSupportActionMode(mActionModeCallBack);
    }

    private void observeParentLive() {
        mModel.getParentLive(mParentId).observe(getViewLifecycleOwner(), parent -> {
            if (parent instanceof CategoryTuple)
                setActionBarTitle(((CategoryTuple) parent).getName());
            else if (parent instanceof Folder) {
                if (((Folder) parent).isDeleted() || ((Folder) parent).isParentDeleted())
                    mNavController.popBackStack();
                setActionBarTitle(((Folder) parent).getName());
                mModel.setStateHandFolderPath(((Folder) parent).getId());
            }
        });
    }

    private void setActionBarTitle(String title) {
        mActivityBinding.actionBar.toolBar.setTitle(title);
    }

    private void setTextNavigation(List<FolderTuple> folderPathTuples) {
        folderPathTuples.forEach(folderTuple -> {
            mBinding.layoutTextNavigation.addView(getArrowIcon());
            mBinding.layoutTextNavigation.addView(getFolderNameTv(folderTuple));
        });
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
    public MaterialTextView getFolderNameTv(@NotNull FolderTuple tuple) {
        MaterialTextView textView = new MaterialTextView(requireContext());
        textView.setAlpha(0.9f);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, mNavigationTextViewSize);
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

    private void observeSizeTuplesLive() {
        mModel.getSizeLive().observe(getViewLifecycleOwner(), sizeTuples -> {
            if (mModel.isFavorite())
                sizeTuples.removeIf(sizeTuple -> !sizeTuple.isFavorite());

            mSizeAdapterList.setItems(mModel.getSort(), sizeTuples);
            mSizeAdapterGrid.setItems(mModel.getSort(), sizeTuples);
        });
    }

    private void observeMainGraphDialogLive() {
        NavBackStackEntry mainBackStackEntry = mNavController.getBackStackEntry(R.id.nav_graph_main);
        MainGraphViewModel mainGraphViewModel = new ViewModelProvider(
                mainBackStackEntry, HiltViewModelFactory.create(requireContext(), mainBackStackEntry)).get(MainGraphViewModel.class);

        mainGraphViewModel.getBackStackEntryLive().observe(getViewLifecycleOwner(), navBackStackEntry ->
                navBackStackEntry.getSavedStateHandle().getLiveData(ACTION_MODE_OFF).observe(navBackStackEntry, o -> {
                    //TODO 패런트 네임 변경(액션바에 텍스트뷰 추가?)
                    if (sActionMode != null) sActionMode.finish();
                }));
    }

    private void observeSelectedItemSizeLive() {
        mModel.getSelectedItemSizeLive().observe(getViewLifecycleOwner(), integer -> {
            if (sActionMode != null) {
                mActionModeCallBack.getBinding().tvTitle.setText(getString(R.string.action_mode_title, integer));

                int selectedSizeListSize = mSizeAdapterList.getCurrentList().size();
                List<FolderTuple> folderList = new ArrayList<>(mFolderAdapter.getCurrentList());
                folderList.removeIf(tuple -> tuple.getId() == -1);
                int selectedFolderListSize = folderList.size();
                mActionModeCallBack.getBinding().cb.setChecked(selectedSizeListSize + selectedFolderListSize == integer);

                mActionModeCallBack.getMenuItems().get(0).setVisible(integer == 1 && mModel.getSelectedSizes().isEmpty());
                mActionModeCallBack.getMenuItems().get(1).setVisible(integer > 0);
                mActionModeCallBack.getMenuItems().get(2).setVisible(integer > 0);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinding = null;
        if (sActionMode != null) sActionMode.finish();
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
    public LockableScrollView getScrollView() {
        return mBinding.sv;
    }

    @Override
    public void onSaveInstanceState(@NonNull @NotNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(ACTION_MODE_STRING, sActionModeOn);
    }

    @Override
    public BaseAdapter<?, ?, ?>[] getAdapters() {
        if (mModel.getViewType() == ViewType.LIST_VIEW)
            return new BaseAdapter[]{mFolderAdapter, mSizeAdapterList};
        else return new BaseAdapter[]{mFolderAdapter, mSizeAdapterGrid};
    }

    @Override
    public void actionItemClick(int itemId) {
        if (itemId == R.id.menu_action_mode_edit)
            navigateNameEditDialog();
        else if (itemId == R.id.menu_action_mode_move)
            navigateTreeView();
        else navigateFolderDeleteDialog();
    }

    private void navigateNameEditDialog() {
        FolderTuple selectedTuple = mModel.getSelectedFolders().iterator().next();
        ListFragmentDirections.ToEditFolderName action = ListFragmentDirections.toEditFolderName(
                selectedTuple.getId(), selectedTuple.getName(), selectedTuple.getParentId());
        CommonUtil.navigate(mNavController, R.id.listFragment, action);
    }

    private void navigateTreeView() {
        ListFragmentDirections.ToTreeView action = ListFragmentDirections.toTreeView(mParentIndex,
                mModel.getSelectedFolderIds(), mModel.getSelectedSizeIds(), mCategoryId, mParentId, mModel.getFolderPathCompleteIds());
        CommonUtil.navigate(mNavController, R.id.listFragment, action);
    }

    private void navigateFolderDeleteDialog() {
        ListFragmentDirections.ToDeleteFolderAndSizesDialog action = ListFragmentDirections.toDeleteFolderAndSizesDialog(
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
        if (isFolderTuple(tuple)) {
            mTouchHelperFolder.startDrag(viewHolder);
        } else {
            if (mModel.getViewType() == ViewType.LIST_VIEW)
                mTouchHelperList.startDrag(viewHolder);
            else mTouchHelperGrid.startDrag(viewHolder);
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
        ListFragmentDirections.ToAddFolder action = ListFragmentDirections.toAddFolder(mParentId, mParentIndex);
        CommonUtil.navigate(mNavController, R.id.listFragment, action);
    }

    @Override
    public void sortClick() {
        NavDirections action = ListFragmentDirections.toSortListDialog();
        CommonUtil.navigate(mNavController, R.id.listFragment, action);
    }

    @Override
    public void recycleBinClick() {
        //TODO
    }

    public void navigateMainFragment() {
        CommonUtil.navigate(mNavController, R.id.listFragment, ListFragmentDirections.toMain());
    }

    public void navigateSelf() {
        if (mCategoryId != mParentId)
            CommonUtil.navigate(mNavController, R.id.listFragment,
                    ListFragmentDirections.toSelf(mCategoryId, mCategoryId, mParentIndex));
    }
}