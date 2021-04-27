package com.example.project_myfit.main.list;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_myfit.R;
import com.example.project_myfit.data.model.Folder;
import com.example.project_myfit.data.model.Size;
import com.example.project_myfit.databinding.FragmentListBinding;
import com.example.project_myfit.databinding.LayoutPopupBinding;
import com.example.project_myfit.databinding.TitleActionModeBinding;
import com.example.project_myfit.dialog.DialogViewModel;
import com.example.project_myfit.main.list.adapter.folderadapter.FolderAdapter;
import com.example.project_myfit.main.list.adapter.folderadapter.FolderDragCallBack;
import com.example.project_myfit.main.list.adapter.sizeadapter.SizeAdapterGrid;
import com.example.project_myfit.main.list.adapter.sizeadapter.SizeAdapterList;
import com.example.project_myfit.util.SelectedItemTreat;
import com.example.project_myfit.util.Sort;
import com.example.project_myfit.util.adapter.DragCallBackGrid;
import com.example.project_myfit.util.adapter.DragCallBackList;
import com.example.project_myfit.util.adapter.viewholder.FolderVHListener;
import com.example.project_myfit.util.adapter.viewholder.SizeListVH;
import com.example.project_myfit.util.adapter.viewholder.SizeVHListener;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textview.MaterialTextView;
import com.michaelflisar.dragselectrecyclerview.DragSelectTouchListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.example.project_myfit.util.MyFitConstant.ACTION_MODE;
import static com.example.project_myfit.util.MyFitConstant.ACTION_MODE_OFF;
import static com.example.project_myfit.util.MyFitConstant.ACTION_MODE_ON;
import static com.example.project_myfit.util.MyFitConstant.FOLDER;
import static com.example.project_myfit.util.MyFitConstant.FOLDER_TOGGLE;
import static com.example.project_myfit.util.MyFitConstant.GRIDVIEW;
import static com.example.project_myfit.util.MyFitConstant.ITEM_MOVE_CONFIRM;
import static com.example.project_myfit.util.MyFitConstant.LISTVIEW;
import static com.example.project_myfit.util.MyFitConstant.LIST_FRAGMENT;
import static com.example.project_myfit.util.MyFitConstant.NAME_EDIT_CONFIRM;
import static com.example.project_myfit.util.MyFitConstant.SELECTED_ITEM_DELETE_CONFIRM;
import static com.example.project_myfit.util.MyFitConstant.SORT_CONFIRM;
import static com.example.project_myfit.util.MyFitConstant.SORT_CUSTOM;
import static com.example.project_myfit.util.MyFitConstant.SORT_LIST;
import static com.example.project_myfit.util.MyFitConstant.THIS_FOLDER_TEXT_VIEW;
import static com.example.project_myfit.util.MyFitConstant.VIEW_TYPE;

@SuppressLint("ClickableViewAccessibility")
public class ListFragment extends Fragment implements SizeVHListener {

    private ListViewModel mModel;
    private FragmentListBinding mBinding;
    private TitleActionModeBinding mActionModeTitleBinding;
    private ItemTouchHelper mTouchHelperList, mTouchHelperGrid, mTouchHelperFolder;
    private boolean isFolderDragSelecting, isSizeDragSelecting, mScrollEnable, mActionModeOn,
            mFolderToggleOpen, isDragging, isFolderStart, isSizeStart, mSizeSelectedState,
            mFolderSelectedState, mNavigationSet, isSearchView;
    private MenuItem mEditMenu, mMoveMenu, mDeletedMenu;
    private DragSelectTouchListener mSizeDragSelectListener, mFolderDragSelectListener;
    private SharedPreferences mViewTypePreference, mSortPreference, mFolderTogglePreference;
    private int mViewType, mSort, mPadding8dp, mPaddingBottom;
    private ActionMode mActionMode;
    private PopupWindow mPopupWindow;
    private LiveData<List<Folder>> mFolderLive;
    private LiveData<List<Size>> mSizeLive;
    private int mFolderStartPosition = -1;
    private int mSizeStartPosition = -1;
    private int mFolderSelectedPosition2 = -1;
    private int mFolderSelectedPosition1 = -1;
    private int mSizeSelectedPosition2 = -1;
    private int mSizeSelectedPosition1 = -1;
    private FolderAdapter mFolderAdapter;
    private SizeAdapterList mSizeAdapterList;
    private SizeAdapterGrid mSizeAdapterGrid;
    private NavController mNavController;
    private DialogViewModel mDialogViewModel;
    private FloatingActionButton mTopFab;
    private LayoutPopupBinding mPopupMenuBinding;
    private float mNavigationTextViewSize;
    private ActionBar mActionBar;
    private long mThisCategoryId, mThisFolderId, mParentId;
    private String mParentCategory;
    private final ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(@NotNull ActionMode mode, Menu menu) {
            mActionMode = mode;
            mActionModeOn = true;

            mode.getMenuInflater().inflate(R.menu.menu_action_mode, menu);
            mode.setCustomView(mActionModeTitleBinding.getRoot());

            mFolderAdapter.setActionModeState(ACTION_MODE_ON);
            if (mViewType == LISTVIEW) mSizeAdapterList.setActionModeState(ACTION_MODE_ON);
            else mSizeAdapterGrid.setActionModeState(ACTION_MODE_ON);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, @NotNull Menu menu) {
            mActionModeTitleBinding.cbActionModeTitle.setOnClickListener(v ->
                    mModel.selectAllClick(((MaterialCheckBox) v).isChecked(), mFolderAdapter,
                            mViewType, mSizeAdapterList, mSizeAdapterGrid));

            mEditMenu = menu.getItem(0);
            mMoveMenu = menu.getItem(1);
            mDeletedMenu = menu.getItem(2);
            return true;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, @NotNull MenuItem item) {
            if (item.getItemId() == R.id.menu_action_mode_edit)
                mNavController.navigate(ListFragmentDirections.actionListFragmentToNameEditDialog(mModel.getSelectedFolderId(), FOLDER, false));
            else if (item.getItemId() == R.id.menu_action_mode_move) {
                mDialogViewModel.forTreeView(mModel.getSelectedFolderList(), mModel.getSelectedSizeList(), mModel.getFolderHistory3());
                mNavController.navigate(ListFragmentDirections.actionListFragmentToTreeViewDialog(mParentCategory, mThisCategoryId, mThisFolderId));
            } else if (item.getItemId() == R.id.menu_action_mode_delete)
                mNavController.navigate(ListFragmentDirections.actionListFragmentToSelectedItemDeleteDialog(mModel.getSelectedItemSize()));
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mActionMode = null;
            mActionModeOn = false;

            mFolderAdapter.setActionModeState(ACTION_MODE_OFF);
            if (mViewType == LISTVIEW) mSizeAdapterList.setActionModeState(ACTION_MODE_OFF);
            else mSizeAdapterGrid.setActionModeState(ACTION_MODE_OFF);

            mActionModeTitleBinding.cbActionModeTitle.setChecked(false);
            ((ViewGroup) mActionModeTitleBinding.getRoot().getParent()).removeAllViews();
        }
    };

    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        super.onAttach(context);
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (mPopupWindow.isShowing()) mPopupWindow.dismiss();
                else if (isSearchView && getParentFragmentManager().getBackStackEntryCount() == 0)
                    requireActivity().finish();
                else mNavController.popBackStack();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mThisCategoryId = ListFragmentArgs.fromBundle(getArguments()).getCategoryId();
        mThisFolderId = ListFragmentArgs.fromBundle(getArguments()).getFolderId();
        mParentId = mThisFolderId == 0 ? mThisCategoryId : mThisFolderId;
        mParentCategory = ListFragmentArgs.fromBundle(getArguments()).getParentCategory();
        isSearchView = requireActivity().getIntent().getExtras() != null;

        mModel = new ViewModelProvider(this).get(ListViewModel.class);
        mNavController = NavHostFragment.findNavController(this);
        mDialogViewModel = new ViewModelProvider(mNavController.getViewModelStoreOwner(R.id.nav_graph_main)).get(DialogViewModel.class);
        preferenceInit();
        setHasOptionsMenu(true);
    }

    private void preferenceInit() {
        mViewTypePreference = requireActivity().getSharedPreferences(VIEW_TYPE, Context.MODE_PRIVATE);
        mViewType = mViewTypePreference.getInt(VIEW_TYPE, LISTVIEW);

        mSortPreference = requireActivity().getSharedPreferences(SORT_LIST, Context.MODE_PRIVATE);
        mSort = mSortPreference.getInt(SORT_LIST, SORT_CUSTOM);

        mFolderTogglePreference = requireActivity().getSharedPreferences(FOLDER_TOGGLE, Context.MODE_PRIVATE);
        mFolderToggleOpen = mFolderTogglePreference.getBoolean(FOLDER_TOGGLE, true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = FragmentListBinding.inflate(inflater);
        View view = mBinding.getRoot();
        mActionModeTitleBinding = TitleActionModeBinding.inflate(inflater);
        mPopupMenuBinding = LayoutPopupBinding.inflate(inflater);
        mPopupMenuBinding.tvPopupAddCategory.setVisibility(View.GONE);
        mPopupWindow = new PopupWindow(mPopupMenuBinding.getRoot(), ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mPopupWindow.setOutsideTouchable(true);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mTopFab = requireActivity().findViewById(R.id.fab_main_top);
        if (mBinding.svList.getScrollY() == 0) mTopFab.hide();
        else mTopFab.show();

        adapterInit();
        itemTouchHelperInit();
        rvInit();

        thisCategoryLive();
        thisFolderLive();
        folderLive();
        sizeLive();
        dialogLive();
        actionModeTitleLive();
    }

    private void adapterInit() {
        mFolderAdapter = new FolderAdapter(mModel);
        mSizeAdapterList = new SizeAdapterList(mModel, this);
        mSizeAdapterGrid = new SizeAdapterGrid(mModel, this);
    }

    private void itemTouchHelperInit(){
        mTouchHelperFolder = new ItemTouchHelper(new FolderDragCallBack(mFolderAdapter));
        mTouchHelperFolder.attachToRecyclerView(mBinding.rvListFolder);

        mTouchHelperList = new ItemTouchHelper(new DragCallBackList(mSizeAdapterList));
        mTouchHelperGrid = new ItemTouchHelper(new DragCallBackGrid(mSizeAdapterGrid));
    }

    private void rvInit() {
        mBinding.rvListFolder.setAdapter(mFolderAdapter);
        mBinding.rvListFolder.addOnItemTouchListener(getFolderDragSelectListener());

        mBinding.rvListSize.setHasFixedSize(true);
        mBinding.rvListSize.addOnItemTouchListener(getSizeDragSelectListener());
        sizeRvInit();
    }

    private void sizeRvInit() {
        if (mPadding8dp == 0) mPadding8dp = (int) getResources().getDimension(R.dimen._8sdp);
        if (mPaddingBottom == 0) mPaddingBottom = (int) getResources().getDimension(R.dimen._60sdp);

        if (mViewType == LISTVIEW) {
            mBinding.rvListSize.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false));
            mBinding.rvListSize.setAdapter(mSizeAdapterList);
            mBinding.rvListSize.setPadding(0, mPadding8dp, 0, mPaddingBottom);
            mTouchHelperList.attachToRecyclerView(mBinding.rvListSize);
        } else {
            mBinding.rvListSize.setLayoutManager(new GridLayoutManager(requireContext(), 2, RecyclerView.VERTICAL, false));
            mBinding.rvListSize.setAdapter(mSizeAdapterGrid);
            mBinding.rvListSize.setPadding(mPadding8dp, mPadding8dp, mPadding8dp, mPaddingBottom);
            mTouchHelperGrid.attachToRecyclerView(mBinding.rvListSize);
        }
    }

    private DragSelectTouchListener getFolderDragSelectListener() {
            DragSelectTouchListener.OnAdvancedDragSelectListener dragSelectListenerFolder = new DragSelectTouchListener.OnAdvancedDragSelectListener() {
                @Override
                public void onSelectionStarted(int i) {
                    isFolderDragSelecting = true;
                    mBinding.svList.setScrollable(false);
                    if (mFolderStartPosition == -1) folderHolderCallOnClick(i);
                    mFolderStartPosition = i;
                }

                @Override
                public void onSelectionFinished(int i) {
                    isFolderStart = false;
                    isFolderDragSelecting = false;
                    mBinding.svList.setScrollable(true);
                    mFolderStartPosition = -1;
                }

                @Override
                public void onSelectChange(int i, int i1, boolean b) {
                    mFolderSelectedPosition1 = i;
                    mFolderSelectedPosition2 = i1;
                    mFolderSelectedState = b;
                    for (int j = i; j <= i1; j++) folderHolderCallOnClick(j);
                }
            };
            mFolderDragSelectListener = new DragSelectTouchListener().withSelectListener(dragSelectListenerFolder);
        return mFolderDragSelectListener;
    }

    private DragSelectTouchListener getSizeDragSelectListener() {
            DragSelectTouchListener.OnAdvancedDragSelectListener dragSelectListenerSize = new DragSelectTouchListener.OnAdvancedDragSelectListener() {
                @Override
                public void onSelectionStarted(int i) {
                    isSizeDragSelecting = true;
                    mBinding.svList.setScrollable(false);
                    if (mSizeStartPosition == -1) sizeHolderCallOnClick(i);
                    mSizeStartPosition = i;
                }

                @Override
                public void onSelectionFinished(int i) {
                    isSizeStart = false;
                    isSizeDragSelecting = false;
                    mBinding.svList.setScrollable(true);
                    mSizeStartPosition = -1;
                }

                @Override
                public void onSelectChange(int i, int i1, boolean b) {
                    mSizeSelectedPosition1 = i;
                    mSizeSelectedPosition2 = i1;
                    mSizeSelectedState = b;
                    for (int j = i; j <= i1; j++) sizeHolderCallOnClick(j);
                }
            };
            mSizeDragSelectListener = new DragSelectTouchListener().withSelectListener(dragSelectListenerSize);
        return mSizeDragSelectListener;
    }

    private void folderHolderCallOnClick(int i) {
        RecyclerView.ViewHolder viewHolder = mBinding.rvListFolder.findViewHolderForLayoutPosition(i);
        if (viewHolder != null) viewHolder.itemView.callOnClick();
    }

    private void sizeHolderCallOnClick(int i) {
        RecyclerView.ViewHolder viewHolder = mBinding.rvListSize.findViewHolderForLayoutPosition(i);
        if (viewHolder != null) viewHolder.itemView.callOnClick();
    }

    private void thisCategoryLive() {
        mModel.getThisCategoryLive(mThisCategoryId).observe(getViewLifecycleOwner(), category -> {
            mBinding.setCategory(category);
            if (mThisFolderId == 0) setActionBarTitle(category.getCategoryName());
        });
    }

    private void thisFolderLive() {
        mModel.getThisFolderLive(mThisFolderId).observe(getViewLifecycleOwner(), folder -> {
            if (folder != null) {
                if (folder.getIsDeleted() || folder.getParentIsDeleted())
                    mNavController.popBackStack();
                setActionBarTitle(folder.getFolderName());
                if (!mNavigationSet) {
                    setNavigation(folder);
                    mNavigationSet = true;
                }
            }
        });
    }

    private void setNavigation(Folder thisFolder) {
        for (Folder folder : mModel.getFolderHistory(mSort, thisFolder)) {
            mBinding.layoutListTextNavigation.addView(getArrowIcon());
            mBinding.layoutListTextNavigation.addView(getFolderNameTv(folder));
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
    public MaterialTextView getFolderNameTv(@NotNull Folder folder) {
        MaterialTextView textView = new MaterialTextView(requireContext());
        textView.setAlpha(0.9f);
        if (mNavigationTextViewSize == 0)
            mNavigationTextViewSize = getResources().getDimension(R.dimen._4sdp);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, mNavigationTextViewSize);
        textView.setText(folder.getFolderName());
        if (mThisFolderId != folder.getId()) {
            textView.setOnClickListener(v -> mNavController.navigate(ListFragmentDirections.actionListFragmentRefresh(mThisCategoryId, folder.getId(), mParentCategory)));
        } else textView.setTag(THIS_FOLDER_TEXT_VIEW);
        textView.setMaxLines(1);
        return textView;
    }

    private void setActionBarTitle(String title) {
        if (mActionBar == null)
            mActionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
        if (mActionBar != null) mActionBar.setTitle(title);
    }

    private void folderLive() {
        if (mFolderLive != null && mFolderLive.hasObservers())
            mFolderLive.removeObservers(getViewLifecycleOwner());

        mFolderLive = mModel.getFolderLive(mParentId);
        mFolderLive.observe(getViewLifecycleOwner(), folderList -> {
            List<Folder> sortFolderList = Sort.folderSort(mSort, folderList);

            int size = sortFolderList.size();
            if (size % 4 == 1) {
                for (int i = 0; i < 3; i++) {
                    Folder dummy = new Folder(-1, "dummy", 0, 0, "dummy");
                    folderList.add(dummy);
                }
            } else if (size % 4 == 2) {
                for (int i = 0; i < 2; i++) {
                    Folder dummy = new Folder(-1, "dummy", 0, 0, "dummy");
                    folderList.add(dummy);
                }
            } else if (size % 4 == 3) {
                Folder dummy = new Folder(-1, "dummy", 0, 0, "dummy");
                folderList.add(dummy);
            }

            mBinding.rvListFolderLayout.setVisibility(folderList.size() == 0 ? View.GONE : View.VISIBLE);
            mFolderAdapter.submitList(folderList, mModel.getFolderParentIdList(mParentCategory)
                    , mModel.getSizeParentIdList(mParentCategory), mSort);
        });
    }

    private void sizeLive() {
        if (mSizeLive != null && mSizeLive.hasObservers())
            mSizeLive.removeObservers(getViewLifecycleOwner());

        mSizeLive = mModel.getSizeLive(mParentId);
        mSizeLive.observe(getViewLifecycleOwner(), sizeList -> {
            if (sizeList.size() == 0)
                mBinding.tvListNoDataLayout.setVisibility(View.VISIBLE);
            else
                mBinding.tvListNoDataLayout.setVisibility(View.GONE);

            List<Size> sortSizeList = Sort.sizeSort(mSort, sizeList);

            if (mModel.isFavoriteView())
                sortSizeList.removeIf(size -> !size.isFavorite());

            mSizeAdapterList.submitList(sizeList, mSort);
            mSizeAdapterGrid.submitList(sizeList, mSort);
        });
    }

    private void dialogLive() {
        SelectedItemTreat selectedItemTreat = new SelectedItemTreat(requireContext());

        mDialogViewModel.getBackStackEntryLive().observe(getViewLifecycleOwner(), navBackStackEntry -> {
            nameEditLive(navBackStackEntry);
            itemMoveLive(selectedItemTreat, navBackStackEntry);
            selectedItemDeleteLive(selectedItemTreat, navBackStackEntry);
            sortLive(navBackStackEntry);
        });
    }

    private void nameEditLive(@NotNull androidx.navigation.NavBackStackEntry navBackStackEntry) {
        navBackStackEntry.getSavedStateHandle().getLiveData(NAME_EDIT_CONFIRM).observe(navBackStackEntry, o -> {
            boolean isParentName = o instanceof Boolean && (boolean) o;
            //TODO
            //parent name edit
            if (isParentName) {
                String parentName = mDialogViewModel.getParentName();
                if (mActionBar != null) mActionBar.setTitle(parentName);
                MaterialTextView textView = requireView().findViewWithTag(THIS_FOLDER_TEXT_VIEW);
                if (textView != null) textView.setText(parentName);
                else mBinding.tvListNavigationCategory.setText(parentName);
            }
            if (mActionMode != null) mActionMode.finish();
        });
    }

    private void itemMoveLive(SelectedItemTreat selectedItemTreat, @NotNull androidx.navigation.NavBackStackEntry navBackStackEntry) {
        navBackStackEntry.getSavedStateHandle().getLiveData(ITEM_MOVE_CONFIRM).observe(navBackStackEntry, o -> {
            long folderId = (long) o;
            selectedItemTreat.folderSizeMove(false,folderId, mModel.getSelectedFolderList(), mModel.getSelectedSizeList());
            if (mActionMode != null) mActionMode.finish();
        });
    }

    private void selectedItemDeleteLive(SelectedItemTreat selectedItemTreat, @NotNull androidx.navigation.NavBackStackEntry navBackStackEntry) {
        navBackStackEntry.getSavedStateHandle().getLiveData(SELECTED_ITEM_DELETE_CONFIRM).observe(navBackStackEntry, o -> {
            selectedItemTreat.folderSizeDelete(false, mModel.getSelectedFolderList(), mModel.getSelectedSizeList());
            if (mActionMode != null) mActionMode.finish();
        });
    }

    private void sortLive(@NotNull androidx.navigation.NavBackStackEntry navBackStackEntry) {
        navBackStackEntry.getSavedStateHandle().getLiveData(SORT_CONFIRM).observe(navBackStackEntry, o -> {
            int sort = (int) o;
            if (mSort != sort) {
                mSort = sort;
                SharedPreferences.Editor editor = mSortPreference.edit();
                editor.putInt(SORT_LIST, mSort);
                editor.apply();
                folderLive();
                sizeLive();
            }
        });
    }

    private void actionModeTitleLive() {
        mModel.getSelectedSizeLive().observe(getViewLifecycleOwner(), integer -> {
            String title = integer + getString(R.string.action_mode_title);
            mActionModeTitleBinding.tvActionModeTitle.setText(title);

            if (mActionMode != null) {
                mEditMenu.setVisible(integer == 1 && mModel.getSelectedSizeList().size() == 0);
                mMoveMenu.setVisible(integer > 0);
                mDeletedMenu.setVisible(integer > 0);
            }

            int sizeSize = mSizeAdapterList.getCurrentList().size();
            List<Folder> folderList = new ArrayList<>(mFolderAdapter.getCurrentList());
            folderList.removeIf(folder -> folder.getId() == -1);
            int folderSize = folderList.size();
            mActionModeTitleBinding.cbActionModeTitle.setChecked(sizeSize + folderSize == integer);
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        scrollChangeListener();
        popupMenuClick();
        folderToggleClick();
        activityFabClick();
        textNavigationClick();
        topFabClick();
        folderAdapterClick();
        folderRvTouch();
        sizeRvTouch();

        if (savedInstanceState != null && savedInstanceState.getBoolean(ACTION_MODE)) {
            mFolderAdapter.setSelectedFolderList(mModel.getSelectedFolderList());
            if (mViewType == LISTVIEW)
                mSizeAdapterList.setSelectedSizeList(mModel.getSelectedSizeList());
            else mSizeAdapterGrid.setSelectedSizeList(mModel.getSelectedSizeList());
            ((AppCompatActivity) requireActivity()).startSupportActionMode(mActionModeCallback);
        }
    }

    private void scrollChangeListener() {
        mBinding.svList.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (v.getScrollY() != 0) mTopFab.show();
            else mTopFab.hide();

            if ((isSizeDragSelecting || isFolderDragSelecting || isDragging) && mScrollEnable && scrollY > oldScrollY)
                v.postDelayed(() -> v.scrollBy(0, 1), 50);
            else if ((isSizeDragSelecting || isFolderDragSelecting || isDragging) && mScrollEnable && scrollY < oldScrollY)
                v.postDelayed(() -> v.scrollBy(0, -1), 50);
        });
    }

    private void popupMenuClick() {
        mPopupMenuBinding.tvPopupCreateFolder.setOnClickListener(v -> {
            mNavController.navigate(ListFragmentDirections.actionListFragmentToAddDialog(FOLDER, mParentCategory, mParentId));
            mPopupWindow.dismiss();
        });

        mPopupMenuBinding.tvPopupSortOrder.setOnClickListener(v -> {
            mNavController.navigate(ListFragmentDirections.actionListFragmentToSortDialog(mSort, LIST_FRAGMENT));
            mPopupWindow.dismiss();
        });

        mPopupMenuBinding.tvPopupRecycleBin.setOnClickListener(v -> {
            mNavController.navigate(ListFragmentDirections.actionListFragmentToTrashActivity());
            mPopupWindow.dismiss();
        });
    }

    private void folderToggleClick() {
        if (mFolderToggleOpen) {
            mBinding.iconListToggle.setImageResource(R.drawable.icon_triangle_down);
            mBinding.rvListFolder.setVisibility(View.VISIBLE);
        } else {
            mBinding.iconListToggle.setImageResource(R.drawable.icon_triangle_left);
            mBinding.rvListFolder.setVisibility(View.GONE);
        }

        mBinding.iconListToggle.setOnClickListener(v -> {
            if (mFolderToggleOpen) {//close
                mBinding.iconListToggle.setImageResource(R.drawable.icon_triangle_left);
                mBinding.rvListFolder.setVisibility(View.GONE);
                mFolderToggleOpen = false;
            } else {//open
                mBinding.iconListToggle.setImageResource(R.drawable.icon_triangle_down);
                mBinding.rvListFolder.setVisibility(View.VISIBLE);
                mFolderToggleOpen = true;
            }
            SharedPreferences.Editor editor = mFolderTogglePreference.edit();
            editor.putBoolean(FOLDER_TOGGLE, mFolderToggleOpen);
            editor.apply();
        });
    }

    private void activityFabClick() {
        requireActivity().findViewById(R.id.fab_main).setOnClickListener(v ->
                mNavController.navigate(ListFragmentDirections.actionListFragmentToInputOutputFragment(
                        mParentId, 0, mParentCategory)));
    }

    private void textNavigationClick() {
        mBinding.iconListHome.setOnClickListener(v -> {
            if (isSearchView)
                mNavController.navigate(ListFragmentDirections.actionListFragmentToMainFragment());
            else mNavController.popBackStack(R.id.mainFragment, false);
        });

        mBinding.tvListNavigationCategory.setOnClickListener(v -> {
            if (mThisFolderId != 0)
                mNavController.navigate(ListFragmentDirections.actionListFragmentRefresh(mThisCategoryId, 0, mParentCategory));
        });
    }

    private void topFabClick() {
        mTopFab.setOnClickListener(v -> {
            mBinding.svList.scrollTo(0, 0);
            mBinding.svList.setOnScrollChangeListener((View.OnScrollChangeListener) (v1, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                if (scrollY != 0) mBinding.svList.scrollTo(0, 0);
                else {
                    mBinding.svList.setOnScrollChangeListener((View.OnScrollChangeListener) null);
                    scrollChangeListener();
                }
            });
        });
    }

    private void folderAdapterClick() {
        mFolderAdapter.setOnFolderAdapterListener(new FolderVHListener() {
            @Override
            public void onFolderItemViewClick(Folder folder, MaterialCheckBox checkBox) {
                if (mActionMode == null)
                    mNavController.navigate(ListFragmentDirections.actionListFragmentRefresh(mThisCategoryId, folder.getId(), mParentCategory));
                else {
                    checkBox.setChecked(!checkBox.isChecked());
                    mModel.folderSelected(folder, checkBox.isChecked(), mFolderAdapter);
                }
            }

            @Override
            public void onFolderItemViewLongClick(int position) {
                if (mActionMode == null) {
                    mModel.selectedItemsClear();
                    ((AppCompatActivity) requireActivity()).startSupportActionMode(mActionModeCallback);
                }
                isFolderStart = true;
                isSizeStart = false;
                mFolderDragSelectListener.startDragSelection(position);
            }

            @Override
            public void onFolderDragHandleTouch(RecyclerView.ViewHolder holder) {
                if (!isDragging) {
                    isDragging = true;
                    mTouchHelperFolder.startDrag(holder);
                } else isDragging = false;
            }
        });
    }

    private void folderRvTouch() {
        mBinding.rvListFolder.setOnTouchListener((v, event) -> {
            //folder auto scroll--------------------------------------------------------------------
            if (!isSizeStart && (isFolderDragSelecting || isDragging)) {
                if (event.getRawY() > 2000) {
                    mBinding.svList.scrollBy(0, 1);
                    mScrollEnable = true;
                } else if (event.getRawY() < 250) {
                    mBinding.svList.scrollBy(0, -1);
                    mScrollEnable = true;
                } else if (event.getRawY() < 2000 && event.getRawY() > 250)
                    mScrollEnable = false;
            }
            //--------------------------------------------------------------------------------------

            //folder drag down----------------------------------------------------------------------
            if (isFolderDragSelecting && isFolderStart && event.getY() > v.getBottom() - 50
                    && event.getAction() != MotionEvent.ACTION_UP) {
                //dispatch event
                MotionEvent newEvent = MotionEvent.obtain(event.getDownTime(), event.getEventTime(), event.getAction(), event.getX(),
                        event.getY() - v.getBottom(), event.getMetaState());
                newEvent.recycle();
                mBinding.rvListSize.dispatchTouchEvent(newEvent);
                //folder drag down------------------------------------------------------------------
                if (!isSizeDragSelecting) {
                    folderDragDown();
                    mSizeDragSelectListener.startDragSelection(0);
                    mFolderSelectedState = false;
                    mFolderSelectedPosition1 = -1;
                    mFolderSelectedPosition2 = -1;
                }
            }
            //folder drag up------------------------------------------------------------------------
            else if (isFolderDragSelecting && isFolderStart && event.getY() < v.getBottom()
                    && isSizeDragSelecting && event.getAction() != MotionEvent.ACTION_UP) {
                folderDragUp();

                RecyclerView.ViewHolder viewHolder = mBinding.rvListSize.findViewHolderForLayoutPosition(0);
                if (viewHolder != null) viewHolder.itemView.callOnClick();

                RecyclerView.ViewHolder viewHolder2 = mBinding.rvListSize.findViewHolderForLayoutPosition(1);
                if (mViewType == GRIDVIEW && viewHolder2 != null &&
                        ((mSizeSelectedPosition2 == 1 && mSizeSelectedState) || (mSizeSelectedPosition2 == 3 && !mSizeSelectedState)))
                    viewHolder2.itemView.callOnClick();

                isSizeDragSelecting = false;
                mSizeSelectedState = false;
                mSizeSelectedPosition1 = -1;
                mSizeSelectedPosition2 = -1;
                mSizeStartPosition = -1;
            }
            //--------------------------------------------------------------------------------------
            if (event.getAction() == MotionEvent.ACTION_UP && isFolderStart) {
                mBinding.rvListSize.dispatchTouchEvent(event);
                mFolderSelectedState = false;
                mSizeSelectedState = false;
                mFolderSelectedPosition1 = -1;
                mFolderSelectedPosition2 = -1;
                mSizeSelectedPosition1 = -1;
                mSizeSelectedPosition2 = -1;
                mFolderStartPosition = -1;
                mSizeStartPosition = -1;
            }
            return false;
        });
    }

    private void sizeRvTouch() {
        mBinding.rvListSize.setOnTouchListener((v, event) -> {
            //size auto scroll----------------------------------------------------------------------
            if (!isFolderStart && (isSizeDragSelecting || isDragging)) {
                if (event.getRawY() > 2000) {
                    mBinding.svList.scrollBy(0, 1);
                    mScrollEnable = true;
                } else if (event.getRawY() < 250) {
                    mBinding.svList.scrollBy(0, -1);
                    mScrollEnable = true;
                } else if (event.getRawY() < 2000 && event.getRawY() > 250)
                    mScrollEnable = false;
            }
            //--------------------------------------------------------------------------------------

            // size drag up-------------------------------------------------------------------------
            if (isSizeDragSelecting && isSizeStart && event.getY() < 0 && event.getAction() != MotionEvent.ACTION_UP) {
                MotionEvent newEvent = MotionEvent.obtain(event.getDownTime(), event.getEventTime(), event.getAction(), event.getX(),
                        event.getY() + mBinding.rvListFolder.getBottom(), event.getMetaState());
                newEvent.recycle();
                mBinding.rvListFolder.dispatchTouchEvent(newEvent);

                if (!isFolderDragSelecting) {
                    RecyclerView.ViewHolder viewHolder = mBinding.rvListSize.findViewHolderForLayoutPosition(0);
                    RecyclerView.ViewHolder viewHolder2 = mBinding.rvListSize.findViewHolderForLayoutPosition(1);
                    gridDragUp(viewHolder, viewHolder2);
                    mFolderDragSelectListener.startDragSelection(mFolderAdapter.getItemCount() - 1);
                }
            }
            //size drag down------------------------------------------------------------------------
            else if (isSizeDragSelecting && event.getY() > 0
                    && isFolderDragSelecting && event.getAction() != MotionEvent.ACTION_UP && isSizeStart) {
                gridDragDown();

                if (mFolderSelectedPosition2 == -1)
                    folderHolderCallOnClick(mFolderAdapter.getItemCount() - 1);
                else if (mFolderSelectedState)
                    for (int i = mFolderSelectedPosition1; i < mFolderAdapter.getItemCount(); i++)
                        folderHolderCallOnClick(i);
                else
                    for (int i = mFolderSelectedPosition2 + 1; i < mFolderAdapter.getItemCount(); i++)
                        folderHolderCallOnClick(i);

                isFolderDragSelecting = false;
                mFolderSelectedState = false;
                mFolderStartPosition = -1;
                mFolderSelectedPosition1 = -1;
                mFolderSelectedPosition2 = -1;
            }
            //--------------------------------------------------------------------------------------
            if (event.getAction() == MotionEvent.ACTION_UP && isSizeStart) {
                mBinding.rvListFolder.dispatchTouchEvent(event);
                mFolderSelectedState = false;
                mSizeSelectedState = false;
                mFolderSelectedPosition1 = -1;
                mFolderSelectedPosition2 = -1;
                mSizeSelectedPosition1 = -1;
                mSizeSelectedPosition2 = -1;
                mFolderStartPosition = -1;
                mSizeStartPosition = -1;
            }
            return false;
        });
    }

    private void folderDragDown() {
        int lastPosition = mFolderAdapter.getItemCount();
        if (mFolderSelectedPosition1 == -1) {
            for (int i = mFolderStartPosition + 1; i < lastPosition; i++)
                folderHolderCallOnClick(i);
        } else if (mFolderStartPosition < mFolderSelectedPosition1) {
            if (mFolderSelectedPosition1 == mFolderSelectedPosition2) {
                if (mFolderSelectedState) {
                    for (int i = mFolderSelectedPosition1 + 1; i < lastPosition; i++)
                        folderHolderCallOnClick(i);
                } else {
                    for (int i = mFolderSelectedPosition1; i < lastPosition; i++)
                        folderHolderCallOnClick(i);
                }
            } else if (mFolderSelectedPosition1 < mFolderSelectedPosition2) {
                if (mFolderSelectedState) {
                    for (int i = mFolderSelectedPosition2 + 1; i < lastPosition; i++)
                        folderHolderCallOnClick(i);
                }
            }
        } else if (mFolderStartPosition > mFolderSelectedPosition1) {
            for (int i = mFolderStartPosition + 1; i < lastPosition; i++)
                folderHolderCallOnClick(i);
            if (mFolderSelectedPosition1 == mFolderSelectedPosition2) {
                if (mFolderSelectedState) {
                    for (int i = mFolderSelectedPosition1; i < mFolderStartPosition; i++)
                        folderHolderCallOnClick(i);
                } else {
                    for (int i = mFolderSelectedPosition1 + 1; i < mFolderStartPosition; i++)
                        folderHolderCallOnClick(i);
                }
            } else {
                if (mFolderSelectedState) {
                    for (int i = mFolderSelectedPosition1; i < mFolderStartPosition; i++)
                        folderHolderCallOnClick(i);
                } else {
                    for (int i = mFolderSelectedPosition2 + 1; i < mFolderStartPosition; i++)
                        folderHolderCallOnClick(i);
                }
            }
        }
    }

    private void folderDragUp() {
        for (int i = mFolderStartPosition + 1; i < mFolderAdapter.getItemCount(); i++)
            folderHolderCallOnClick(i);
        mFolderDragSelectListener.startDragSelection(mFolderStartPosition);
    }

    private void gridDragUp(RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder viewHolder2) {
        if (mViewType == GRIDVIEW && viewHolder != null && viewHolder2 != null) {
            if (mSizeStartPosition == 0 && mSizeSelectedPosition2 >= 1
                    && (mSizeSelectedState || mSizeSelectedPosition2 >= 3))
                viewHolder2.itemView.callOnClick();
            else if (mSizeStartPosition == 1) {
                if (mSizeSelectedPosition1 != 0 && mSizeSelectedPosition2 != 2)
                    viewHolder.itemView.callOnClick();
                else if (mSizeSelectedPosition2 == 0 && !mSizeSelectedState) {
                    viewHolder.itemView.callOnClick();
                }
            } else if (mSizeStartPosition > 1) {
                if (mSizeSelectedPosition1 != 0) viewHolder.itemView.callOnClick();
                else if (!mSizeSelectedState) viewHolder.itemView.callOnClick();
            }
        }
    }

    private void gridDragDown() {
        if (mViewType == GRIDVIEW) {
            RecyclerView.ViewHolder viewHolder = mBinding.rvListSize.findViewHolderForLayoutPosition(0);
            RecyclerView.ViewHolder viewHolder2 = mBinding.rvListSize.findViewHolderForLayoutPosition(1);
            if (viewHolder != null && viewHolder2 != null)
                if (mSizeStartPosition == 0) {
                    if ((mSizeSelectedPosition2 == 1 && mSizeSelectedState)
                            || (mSizeSelectedPosition2 == 3 && !mSizeSelectedState))
                        viewHolder2.itemView.callOnClick();
                } else if (mSizeStartPosition == 1) {
                    if (mSizeSelectedPosition2 == -1
                            || (mSizeSelectedPosition2 == 0 && !mSizeSelectedState)
                            || (mSizeSelectedPosition2 == 3 && !mSizeSelectedState))
                        viewHolder.itemView.callOnClick();
                } else if (mSizeStartPosition > 1)
                    if (mSizeSelectedPosition1 != 0 || !mSizeSelectedState)
                        viewHolder.itemView.callOnClick();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mActionMode != null) mActionMode.finish();
        mNavigationSet = false;

    }

    //size adapter click listener-------------------------------------------------------------------
    @Override
    public void onSizeItemViewClick(Size size, MaterialCheckBox checkBox) {
        if (mActionMode == null) {
            mNavController.navigate(ListFragmentDirections.actionListFragmentToInputOutputFragment(
                    size.getParentId(), size.getId(), mParentCategory));
        } else {
            checkBox.setChecked(!checkBox.isChecked());
            mModel.sizeSelected(size, checkBox.isChecked(), mSizeAdapterList, mSizeAdapterGrid, mViewType);
        }
    }

    @Override
    public void onSizeItemViewLongClick(int position) {
        if (mActionMode == null) {
            mModel.selectedItemsClear();
            ((AppCompatActivity) requireActivity()).startSupportActionMode(mActionModeCallback);
        }
        isSizeStart = true;
        isFolderStart = false;
        mSizeDragSelectListener.startDragSelection(position);
    }

    @Override
    public void onSizeDragHandleTouch(RecyclerView.ViewHolder viewHolder) {
        if (!isDragging) {
            isDragging = true;
            if (viewHolder instanceof SizeListVH)
                mTouchHelperList.startDrag(viewHolder);
            else mTouchHelperGrid.startDrag(viewHolder);
        } else isDragging = false;
    }

    @Override
    public void onSizeFavoriteClick(Size size) {
        mModel.sizeFavoriteClick(size);
    }

    //menu------------------------------------------------------------------------------------------
    @Override
    public void onPrepareOptionsMenu(@NonNull @NotNull Menu menu) {
        MenuItem favoriteMenu = menu.getItem(1);
        MenuItem viewTypeMenu = menu.getItem(2);

        favoriteMenu.setIcon(mModel.isFavoriteView() ? R.drawable.icon_favorite : R.drawable.icon_favorite_border);
        viewTypeMenu.setIcon(mViewType == LISTVIEW ? R.drawable.icon_list : R.drawable.icon_grid);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull @NotNull Menu menu, @NonNull @NotNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull @NotNull MenuItem item) {
        if (item.getItemId() == R.id.menu_list_view_type) {
            if (mViewType == LISTVIEW) {
                item.setIcon(R.drawable.icon_grid);
                mViewType = GRIDVIEW;
            } else {
                item.setIcon(R.drawable.icon_list);
                mViewType = LISTVIEW;
            }
            sizeRvInit();
            SharedPreferences.Editor editor = mViewTypePreference.edit();
            editor.putInt(VIEW_TYPE, mViewType);
            editor.apply();
            return true;
        } else if (item.getItemId() == R.id.menu_list_popup) {
            mPopupWindow.showAsDropDown(requireActivity().findViewById(R.id.menu_list_popup));
            return true;
        } else if (item.getItemId() == R.id.menu_list_favorite) {
            mModel.setFavoriteView(!mModel.isFavoriteView());
            item.setIcon(mModel.isFavoriteView() ? R.drawable.icon_favorite : R.drawable.icon_favorite_border);
            sizeLive();
            return true;
        } else if (item.getItemId() == R.id.menu_list_search) {
            mNavController.navigate(ListFragmentDirections.actionListFragmentToSearchActivity());
            return true;
        } else if (isSearchView && getParentFragmentManager().getBackStackEntryCount() == 0)
            requireActivity().finish();
        return false;
    }

    @Override
    public void onSaveInstanceState(@NonNull @NotNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(ACTION_MODE, mActionModeOn);
    }
}