package com.example.project_myfit.fragment.list;

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
import com.example.project_myfit.databinding.ActionModeTitleBinding;
import com.example.project_myfit.databinding.FragmentListBinding;
import com.example.project_myfit.databinding.ListPopupMenuBinding;
import com.example.project_myfit.dialog.DialogViewModel;
import com.example.project_myfit.fragment.list.adapter.folderdapter.FolderAdapter;
import com.example.project_myfit.fragment.list.adapter.folderdapter.FolderDragCallBack;
import com.example.project_myfit.fragment.list.adapter.sizeadapter.SizeAdapterGrid;
import com.example.project_myfit.fragment.list.adapter.sizeadapter.SizeAdapterList;
import com.example.project_myfit.fragment.list.adapter.sizeadapter.SizeAdapterListener;
import com.example.project_myfit.util.SelectedItemTreat;
import com.example.project_myfit.util.Sort;
import com.example.project_myfit.util.adapter.DragCallBackGrid;
import com.example.project_myfit.util.adapter.DragCallBackList;
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
import static com.example.project_myfit.util.MyFitConstant.ITEM_MOVE_CONFIRM_CLICK;
import static com.example.project_myfit.util.MyFitConstant.LISTVIEW;
import static com.example.project_myfit.util.MyFitConstant.LIST_FRAGMENT;
import static com.example.project_myfit.util.MyFitConstant.NAME_EDIT_CONFIRM_CLICK;
import static com.example.project_myfit.util.MyFitConstant.SELECTED_ITEM_DELETE_CONFIRM_CLICK;
import static com.example.project_myfit.util.MyFitConstant.SIZE;
import static com.example.project_myfit.util.MyFitConstant.SORT_CONFIRM_CLICK;
import static com.example.project_myfit.util.MyFitConstant.SORT_CUSTOM;
import static com.example.project_myfit.util.MyFitConstant.SORT_LIST;
import static com.example.project_myfit.util.MyFitConstant.VIEW_TYPE;

//TODO 휴지통 클릭

@SuppressLint("ClickableViewAccessibility")
public class ListFragment extends Fragment implements SizeAdapterListener {

    private ListViewModel mModel;
    private FragmentListBinding mBinding;
    private ActionModeTitleBinding mActionModeTitleBinding;
    private ItemTouchHelper mTouchHelperList, mTouchHelperGrid, mTouchHelperFolder;
    private boolean mIsFolderDragSelecting, mIsSizeDragSelecting, mScrollEnable, mActionModeOn, mIsFolderToggleOpen, isDragging, isFolderStart, isSizeStart, mSizeSelectedState, mFolderSelectedState;
    private MenuItem mEditMenu, mMoveMenu, mDeletedMenu;
    private DragSelectTouchListener mSizeDragSelectListener, mFolderDragSelectListener;
    private SharedPreferences mViewTypePreference, mSortPreference, mFolderTogglePreference;
    private int mViewType, mSort;
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
    private ListPopupMenuBinding mPopupMenuBinding;
    private int mPadding8dp, mPaddingBottom;
    private float mTextViewSize;
    private ActionBar mActionBar;
    private long mThisCategoryId;
    private long mThisFolderId;
    private long mParentId;
    private String mParentCategory;
    private final ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(@NotNull ActionMode mode, Menu menu) {
            mActionMode = mode;
            mActionModeOn = true;

            mode.getMenuInflater().inflate(R.menu.action_mode, menu);
            mode.setCustomView(mActionModeTitleBinding.getRoot());

            mFolderAdapter.setActionModeState(ACTION_MODE_ON);
            if (mViewType == LISTVIEW) mSizeAdapterList.setActionModeState(ACTION_MODE_ON);
            else mSizeAdapterGrid.setActionModeState(ACTION_MODE_ON);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, @NotNull Menu menu) {
            mActionModeTitleBinding.actionModeSelectAll.setOnClickListener(v ->
                    mModel.selectAllClick(((MaterialCheckBox) v).isChecked(), mFolderAdapter,
                            mViewType, mSizeAdapterList, mSizeAdapterGrid));

            mEditMenu = menu.getItem(0);
            mMoveMenu = menu.getItem(1);
            mDeletedMenu = menu.getItem(2);
            return true;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, @NotNull MenuItem item) {
            if (item.getItemId() == R.id.action_mode_edit)
                mNavController.navigate(ListFragmentDirections.actionListFragmentToNameEditDialog(mModel.getSelectedFolderId(), FOLDER, false));
                // -> dialogLive
            else if (item.getItemId() == R.id.action_mode_move) {
                mDialogViewModel.forTreeView(mModel.getSelectedFolderList(), mModel.getSelectedSizeList(), mModel.getFolderHistory3());
                mNavController.navigate(ListFragmentDirections.actionListFragmentToTreeViewDialog(mParentCategory, mThisCategoryId, mThisFolderId));
                // -> dialogLive
            } else if (item.getItemId() == R.id.action_mode_del)
                mNavController.navigate(ListFragmentDirections.actionListFragmentToSelectedItemDeleteDialog(mModel.getSelectedItemSize()));
            // -> dialogLive
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mActionMode = null;
            mActionModeOn = false;

            mFolderAdapter.setActionModeState(ACTION_MODE_OFF);
            if (mViewType == LISTVIEW) mSizeAdapterList.setActionModeState(ACTION_MODE_OFF);
            else mSizeAdapterGrid.setActionModeState(ACTION_MODE_OFF);

            mActionModeTitleBinding.actionModeSelectAll.setChecked(false);
            ((ViewGroup) mActionModeTitleBinding.getRoot().getParent()).removeAllViews();
        }
    };
    private boolean mIsNavigationSet;

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
        mThisCategoryId = ListFragmentArgs.fromBundle(getArguments()).getCategoryId();
        mThisFolderId = ListFragmentArgs.fromBundle(getArguments()).getFolderId();
        mParentId = mThisFolderId == 0 ? mThisCategoryId : mThisFolderId;
        mParentCategory = ListFragmentArgs.fromBundle(getArguments()).getParentCategory();

        mModel = new ViewModelProvider(this).get(ListViewModel.class);
        mNavController = NavHostFragment.findNavController(this);
        mDialogViewModel = new ViewModelProvider(mNavController.getViewModelStoreOwner(R.id.main_nav_graph)).get(DialogViewModel.class);
        preferenceInit();
        setHasOptionsMenu(true);
    }

    private void preferenceInit() {
        mViewTypePreference = requireActivity().getSharedPreferences(VIEW_TYPE, Context.MODE_PRIVATE);
        mViewType = mViewTypePreference.getInt(VIEW_TYPE, LISTVIEW);

        mSortPreference = requireActivity().getSharedPreferences(SORT_LIST, Context.MODE_PRIVATE);
        mSort = mSortPreference.getInt(SORT_LIST, SORT_CUSTOM);

        mFolderTogglePreference = requireActivity().getSharedPreferences(FOLDER_TOGGLE, Context.MODE_PRIVATE);
        mIsFolderToggleOpen = mFolderTogglePreference.getBoolean(FOLDER_TOGGLE, true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = FragmentListBinding.inflate(inflater);
        View view = mBinding.getRoot();
        mActionModeTitleBinding = ActionModeTitleBinding.inflate(inflater);
        mPopupMenuBinding = ListPopupMenuBinding.inflate(inflater);
        mPopupWindow = new PopupWindow(mPopupMenuBinding.getRoot(), ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mPopupWindow.setOutsideTouchable(true);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mTopFab = requireActivity().findViewById(R.id.top_fab);

        adapterAndItemTouchHelperInit();
        recyclerViewInit();

        setThisCategoryLive();
        setThisFolderLive();
        setFolderLive();
        setSizeLive();
        setDialogLive();
        setActionModeTitleLive();
    }

    private void adapterAndItemTouchHelperInit() {
        mFolderAdapter = new FolderAdapter(mModel);
        mSizeAdapterList = new SizeAdapterList(mModel, this);
        mSizeAdapterGrid = new SizeAdapterGrid(mModel, this);

        mTouchHelperFolder = new ItemTouchHelper(new FolderDragCallBack(mFolderAdapter));
        mTouchHelperFolder.attachToRecyclerView(mBinding.recyclerFolder);

        mTouchHelperList = new ItemTouchHelper(new DragCallBackList(mSizeAdapterList, SIZE));
        mTouchHelperGrid = new ItemTouchHelper(new DragCallBackGrid(mSizeAdapterGrid));
    }

    private void recyclerViewInit() {
        mBinding.recyclerFolder.setAdapter(mFolderAdapter);
        mBinding.recyclerFolder.addOnItemTouchListener(getFolderDragSelectListener());

        mBinding.recyclerSize.setHasFixedSize(true);
        mBinding.recyclerSize.addOnItemTouchListener(getSizeDragSelectListener());
        setSizeRecyclerLayout();
    }

    private void setSizeRecyclerLayout() {
        if (mPadding8dp == 0) mPadding8dp = (int) getResources().getDimension(R.dimen._8sdp);
        if (mPaddingBottom == 0) mPaddingBottom = (int) getResources().getDimension(R.dimen._60sdp);

        if (mViewType == LISTVIEW) {
            mBinding.recyclerSize.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false));
            mBinding.recyclerSize.setAdapter(mSizeAdapterList);
            mBinding.recyclerSize.setPadding(0, mPadding8dp, 0, mPaddingBottom);
            mTouchHelperList.attachToRecyclerView(mBinding.recyclerSize);
        } else {
            mBinding.recyclerSize.setLayoutManager(new GridLayoutManager(requireContext(), 2, RecyclerView.VERTICAL, false));
            mBinding.recyclerSize.setAdapter(mSizeAdapterGrid);
            mBinding.recyclerSize.setPadding(mPadding8dp, mPadding8dp, mPadding8dp, mPaddingBottom);
            mTouchHelperGrid.attachToRecyclerView(mBinding.recyclerSize);
        }
    }

    private DragSelectTouchListener getFolderDragSelectListener() {
        if (mFolderDragSelectListener == null) {
            DragSelectTouchListener.OnAdvancedDragSelectListener dragSelectListenerFolder = new DragSelectTouchListener.OnAdvancedDragSelectListener() {
                @Override
                public void onSelectionStarted(int i) {
                    mIsFolderDragSelecting = true;
                    mBinding.listScrollView.setScrollable(false);
                    if (mFolderStartPosition == -1) folderHolderCallOnClick(i);
                    mFolderStartPosition = i;
                }

                @Override
                public void onSelectionFinished(int i) {
                    isFolderStart = false;
                    mIsFolderDragSelecting = false;
                    mBinding.listScrollView.setScrollable(true);
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
        }
        return mFolderDragSelectListener;
    }

    private DragSelectTouchListener getSizeDragSelectListener() {
        if (mSizeDragSelectListener == null) {
            DragSelectTouchListener.OnAdvancedDragSelectListener dragSelectListenerSize = new DragSelectTouchListener.OnAdvancedDragSelectListener() {
                @Override
                public void onSelectionStarted(int i) {
                    mIsSizeDragSelecting = true;
                    mBinding.listScrollView.setScrollable(false);
                    if (mSizeStartPosition == -1) sizeHolderCallOnClick(i);
                    mSizeStartPosition = i;
                }

                @Override
                public void onSelectionFinished(int i) {
                    isSizeStart = false;
                    mIsSizeDragSelecting = false;
                    mBinding.listScrollView.setScrollable(true);
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
        }
        return mSizeDragSelectListener;
    }

    private void folderHolderCallOnClick(int i) {
        RecyclerView.ViewHolder viewHolder = mBinding.recyclerFolder.findViewHolderForLayoutPosition(i);
        if (viewHolder != null) viewHolder.itemView.callOnClick();
    }

    private void sizeHolderCallOnClick(int i) {
        RecyclerView.ViewHolder viewHolder = mBinding.recyclerSize.findViewHolderForLayoutPosition(i);
        if (viewHolder != null) viewHolder.itemView.callOnClick();
    }

    private void setThisCategoryLive() {
        mModel.getThisCategoryLive(mThisCategoryId).observe(getViewLifecycleOwner(), category -> {
            mBinding.setCategory(category);
            if (mThisFolderId == 0) setActionBarTitle(category.getCategoryName());
        });
    }

    private void setThisFolderLive() {
        mModel.getThisFolderLive(mThisFolderId).observe(getViewLifecycleOwner(), folder -> {
            if (folder != null) {
                if (folder.getIsDeleted() || folder.getParentIsDeleted())
                    mNavController.popBackStack();
                setActionBarTitle(folder.getFolderName());
                if (!mIsNavigationSet) {
                    setNavigation(folder);
                    mIsNavigationSet = true;
                }
            }
        });
    }

    private void setNavigation(Folder thisFolder) {
        for (Folder folder : mModel.getFolderHistory(mSort, thisFolder)) {
            mBinding.listNavigation.addView(getArrowIcon());
            mBinding.listNavigation.addView(getFolderNameTextView(folder));
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
    public MaterialTextView getFolderNameTextView(@NotNull Folder folder) {
        MaterialTextView textView = new MaterialTextView(requireContext());
        textView.setAlpha(0.9f);
        if (mTextViewSize == 0) mTextViewSize = getResources().getDimension(R.dimen._4sdp);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, mTextViewSize);
        textView.setText(folder.getFolderName());
        if (mThisFolderId != folder.getId()) {
            textView.setOnClickListener(v -> mNavController.navigate(ListFragmentDirections.actionListFragmentRefresh(mThisCategoryId, folder.getId(), mParentCategory)));
        } else textView.setTag("this_folder_text_view");
        textView.setMaxLines(1);
        return textView;
    }

    private void setActionBarTitle(String title) {
        if (mActionBar == null)
            mActionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
        if (mActionBar != null) mActionBar.setTitle(title);
    }

    private void setFolderLive() {
        if (mFolderLive != null && mFolderLive.hasObservers())
            mFolderLive.removeObservers(getViewLifecycleOwner());

        mFolderLive = mModel.getFolderLiveByParentId(mParentId);
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

            mBinding.recyclerFolderLayout.jumpDrawablesToCurrentState();
            mBinding.recyclerFolderLayout.setVisibility(folderList.size() == 0 ? View.GONE : View.VISIBLE);
            mFolderAdapter.submitList(folderList, mModel.getFolderParentIdListByParentCategory(mParentCategory)
                    , mModel.getSizeParentIdListByParentCategory(mParentCategory), mSort);
        });
    }

    private void setSizeLive() {
        if (mSizeLive != null && mSizeLive.hasObservers())
            mSizeLive.removeObservers(getViewLifecycleOwner());

        mSizeLive = mModel.getSizeLiveByParentId(mParentId);
        mSizeLive.observe(getViewLifecycleOwner(), sizeList -> {
            if (sizeList.size() == 0)
                mBinding.noData.setVisibility(View.VISIBLE);
            else
                mBinding.noData.setVisibility(View.GONE);

            List<Size> sortSizeList = Sort.sizeSort(mSort, sizeList);

            if (mModel.isFavoriteView())
                sortSizeList.removeIf(size -> !size.isFavorite());

            mSizeAdapterList.submitList(sizeList, mSort);
            mSizeAdapterGrid.submitList(sizeList, mSort);
        });
    }

    private void setDialogLive() {
        SelectedItemTreat selectedItemTreat = new SelectedItemTreat(requireContext());

        mDialogViewModel.getBackStackEntryLive().observe(getViewLifecycleOwner(), navBackStackEntry -> {
            nameEditDialogLive(navBackStackEntry);
            itemMoveDialogLive(selectedItemTreat, navBackStackEntry);
            selectedItemDeleteLive(selectedItemTreat, navBackStackEntry);
            sortDialogLive(navBackStackEntry);
        });
    }

    private void nameEditDialogLive(@NotNull androidx.navigation.NavBackStackEntry navBackStackEntry) {
        navBackStackEntry.getSavedStateHandle().getLiveData(NAME_EDIT_CONFIRM_CLICK).observe(navBackStackEntry, o -> {
            boolean isParentName = o instanceof Boolean && (boolean) o;
            //parent name edit
            if (isParentName) {
                String parentName = mDialogViewModel.getParentName();
                if (mActionBar != null) mActionBar.setTitle(parentName);
                MaterialTextView textView = requireView().findViewWithTag("this_folder_text_view");
                if (textView != null) textView.setText(parentName);
                else mBinding.listTextCategory.setText(parentName);
            }
            if (mActionMode != null) mActionMode.finish();
        });
    }

    private void itemMoveDialogLive(SelectedItemTreat selectedItemTreat, @NotNull androidx.navigation.NavBackStackEntry navBackStackEntry) {
        navBackStackEntry.getSavedStateHandle().getLiveData(ITEM_MOVE_CONFIRM_CLICK).observe(navBackStackEntry, o -> {
            long folderId = (long) o;
            selectedItemTreat.folderSizeMove(false,folderId, mModel.getSelectedFolderList(), mModel.getSelectedSizeList());
            if (mActionMode != null) mActionMode.finish();
        });
    }

    private void selectedItemDeleteLive(SelectedItemTreat selectedItemTreat, @NotNull androidx.navigation.NavBackStackEntry navBackStackEntry) {
        navBackStackEntry.getSavedStateHandle().getLiveData(SELECTED_ITEM_DELETE_CONFIRM_CLICK).observe(navBackStackEntry, o -> {
            selectedItemTreat.folderSizeDelete(false, mModel.getSelectedFolderList(), mModel.getSelectedSizeList());
            if (mActionMode != null) mActionMode.finish();
        });
    }

    private void sortDialogLive(@NotNull androidx.navigation.NavBackStackEntry navBackStackEntry) {
        navBackStackEntry.getSavedStateHandle().getLiveData(SORT_CONFIRM_CLICK).observe(navBackStackEntry, o -> {
            int sort = (int) o;
            if (mSort != sort) {
                mSort = sort;
                SharedPreferences.Editor editor = mSortPreference.edit();
                editor.putInt(SORT_LIST, mSort);
                editor.apply();
                setFolderLive();
                setSizeLive();
            }
        });
    }

    private void setActionModeTitleLive() {
        mModel.getSelectedSizeLive().observe(getViewLifecycleOwner(), integer -> {
            String title = integer + getString(R.string.item_selected);
            mActionModeTitleBinding.actionModeTitle.setText(title);

            if (mActionMode != null) {
                mEditMenu.setVisible(integer == 1 && mModel.getSelectedSizeList().size() == 0);
                mMoveMenu.setVisible(integer > 0);
                mDeletedMenu.setVisible(integer > 0);
            }

            int sizeSize = mSizeAdapterList.getCurrentList().size();
            List<Folder> folderList = new ArrayList<>(mFolderAdapter.getCurrentList());
            folderList.removeIf(folder -> folder.getId() == -1);
            int folderSize = folderList.size();
            mActionModeTitleBinding.actionModeSelectAll.setChecked(sizeSize + folderSize == integer);
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setScrollChangeListener();
        setPopupMenuClickListener();
        setFolderToggleClickListener();
        setActivityFabClickListener();
        setTextNavigationClickListener();
        setTopFabClickListener();
        folderAdapterClick();
        setFolderRecyclerTouchListener();
        setSizeRecyclerTouchListener();

        //restore actionMode
        if (savedInstanceState != null && savedInstanceState.getBoolean(ACTION_MODE)) {
            if (mViewType == LISTVIEW)
                mSizeAdapterList.setSelectedSizeList(mModel.getSelectedSizeList());
            else mSizeAdapterGrid.setSelectedSizeList(mModel.getSelectedSizeList());
            mFolderAdapter.setSelectedFolderList(mModel.getSelectedFolderList());
            ((AppCompatActivity) requireActivity()).startSupportActionMode(mActionModeCallback);
        }
    }

    private void setScrollChangeListener() {
        mBinding.listScrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (v.getScrollY() != 0) mTopFab.show();
            else mTopFab.hide();

            if ((mIsSizeDragSelecting || mIsFolderDragSelecting || isDragging) && mScrollEnable && scrollY > oldScrollY)
                v.postDelayed(() -> v.scrollBy(0, 1), 50);
            else if ((mIsSizeDragSelecting || mIsFolderDragSelecting || isDragging) && mScrollEnable && scrollY < oldScrollY)
                v.postDelayed(() -> v.scrollBy(0, -1), 50);
        });
    }

    private void setPopupMenuClickListener() {
        mPopupMenuBinding.addFolder.setOnClickListener(v -> {
            mNavController.navigate(ListFragmentDirections.actionListFragmentToAddDialog(FOLDER, mParentCategory, mParentId));
            mPopupWindow.dismiss();
        });

        mPopupMenuBinding.sort.setOnClickListener(v -> {
            mNavController.navigate(ListFragmentDirections.actionListFragmentToSortDialog(mSort, LIST_FRAGMENT));
            mPopupWindow.dismiss();
        });
    }

    private void setFolderToggleClickListener() {
        if (mIsFolderToggleOpen) {
            mBinding.folderToggleIcon.setImageResource(R.drawable.icon_triangle_down);
            mBinding.recyclerFolder.setVisibility(View.VISIBLE);
        } else {
            mBinding.folderToggleIcon.setImageResource(R.drawable.icon_triangle_left);
            mBinding.recyclerFolder.setVisibility(View.GONE);
        }

        mBinding.folderToggleIcon.setOnClickListener(v -> {
            if (mIsFolderToggleOpen) {//close
                mBinding.folderToggleIcon.setImageResource(R.drawable.icon_triangle_left);
                mBinding.recyclerFolder.setVisibility(View.GONE);
                mIsFolderToggleOpen = false;
            } else {//open
                mBinding.folderToggleIcon.setImageResource(R.drawable.icon_triangle_down);
                mBinding.recyclerFolder.setVisibility(View.VISIBLE);
                mIsFolderToggleOpen = true;
            }
            SharedPreferences.Editor editor = mFolderTogglePreference.edit();
            editor.putBoolean(FOLDER_TOGGLE, mIsFolderToggleOpen);
            editor.apply();
        });
    }

    private void setActivityFabClickListener() {
        requireActivity().findViewById(R.id.activity_fab).setOnClickListener(v ->
                mNavController.navigate(ListFragmentDirections.actionListFragmentToInputOutputFragment(
                        mThisCategoryId, mThisFolderId, 0, mParentCategory)));
    }

    private void setTextNavigationClickListener() {
        //home icon click
        mBinding.listIconHome.setOnClickListener(v ->
                mNavController.popBackStack(R.id.mainFragment, false));

        //category text click
        mBinding.listTextCategory.setOnClickListener(v -> {
            if (mThisFolderId != 0)
                mNavController.navigate(ListFragmentDirections.actionListFragmentRefresh(mThisCategoryId, 0, mParentCategory));
        });
    }

    private void setTopFabClickListener() {
        mTopFab.setOnClickListener(v -> {
            mBinding.listScrollView.scrollTo(0, 0);
            mBinding.listScrollView.setOnScrollChangeListener((View.OnScrollChangeListener) (v1, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                if (scrollY != 0) mBinding.listScrollView.scrollTo(0, 0);
                else {
                    mBinding.listScrollView.setOnScrollChangeListener((View.OnScrollChangeListener) null);
                    setScrollChangeListener();
                }
            });
        });
    }

    private void folderAdapterClick() {
        mFolderAdapter.setOnFolderAdapterListener(new FolderAdapter.FolderAdapterListener() {
            @Override
            public void onFolderItemViewClick(Folder folder, MaterialCheckBox checkBox) {
                if (mActionMode == null) {
                    mTopFab.hide();
                    mNavController.navigate(ListFragmentDirections.actionListFragmentRefresh(mThisCategoryId, folder.getId(), mParentCategory));
                } else {
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

    private void setFolderRecyclerTouchListener() {
        mBinding.recyclerFolder.setOnTouchListener((v, event) -> {
            //folder auto scroll--------------------------------------------------------------------
            if (!isSizeStart && (mIsFolderDragSelecting || isDragging)) {
                if (event.getRawY() > 2000) {
                    mBinding.listScrollView.scrollBy(0, 1);
                    mScrollEnable = true;
                } else if (event.getRawY() < 250) {
                    mBinding.listScrollView.scrollBy(0, -1);
                    mScrollEnable = true;
                } else if (event.getRawY() < 2000 && event.getRawY() > 250)
                    mScrollEnable = false;
            }
            //--------------------------------------------------------------------------------------

            //folder drag down----------------------------------------------------------------------
            if (mIsFolderDragSelecting && isFolderStart && event.getY() > v.getBottom() - 50
                    && event.getAction() != MotionEvent.ACTION_UP) {
                //dispatch event
                MotionEvent newEvent = MotionEvent.obtain(event.getDownTime(), event.getEventTime(), event.getAction(), event.getX(),
                        event.getY() - v.getBottom(), event.getMetaState());
                newEvent.recycle();
                mBinding.recyclerSize.dispatchTouchEvent(newEvent);
                //folder drag down------------------------------------------------------------------
                if (!mIsSizeDragSelecting) {
                    folderToSizeDragDown();
                    mSizeDragSelectListener.startDragSelection(0);
                    mFolderSelectedState = false;
                    mFolderSelectedPosition1 = -1;
                    mFolderSelectedPosition2 = -1;
                }
            }
            //folder drag up------------------------------------------------------------------------
            else if (mIsFolderDragSelecting && isFolderStart && event.getY() < v.getBottom()
                    && mIsSizeDragSelecting && event.getAction() != MotionEvent.ACTION_UP) {
                folderToSizeDragUp();

                RecyclerView.ViewHolder viewHolder = mBinding.recyclerSize.findViewHolderForLayoutPosition(0);
                if (viewHolder != null) viewHolder.itemView.callOnClick();

                RecyclerView.ViewHolder viewHolder2 = mBinding.recyclerSize.findViewHolderForLayoutPosition(1);
                if (mViewType == GRIDVIEW && viewHolder2 != null &&
                        ((mSizeSelectedPosition2 == 1 && mSizeSelectedState) || (mSizeSelectedPosition2 == 3 && !mSizeSelectedState)))
                    viewHolder2.itemView.callOnClick();

                mIsSizeDragSelecting = false;
                mSizeSelectedState = false;
                mSizeSelectedPosition1 = -1;
                mSizeSelectedPosition2 = -1;
                mSizeStartPosition = -1;
            }
            //--------------------------------------------------------------------------------------
            if (event.getAction() == MotionEvent.ACTION_UP && isFolderStart) {
                mBinding.recyclerSize.dispatchTouchEvent(event);
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

    private void setSizeRecyclerTouchListener() {
        mBinding.recyclerSize.setOnTouchListener((v, event) -> {
            //size auto scroll----------------------------------------------------------------------
            if (!isFolderStart && (mIsSizeDragSelecting || isDragging)) {
                if (event.getRawY() > 2000) {
                    mBinding.listScrollView.scrollBy(0, 1);
                    mScrollEnable = true;
                } else if (event.getRawY() < 250) {
                    mBinding.listScrollView.scrollBy(0, -1);
                    mScrollEnable = true;
                } else if (event.getRawY() < 2000 && event.getRawY() > 250)
                    mScrollEnable = false;
            }
            //--------------------------------------------------------------------------------------

            // size drag up-------------------------------------------------------------------------
            if (mIsSizeDragSelecting && isSizeStart && event.getY() < 0 && event.getAction() != MotionEvent.ACTION_UP ) {
                MotionEvent newEvent = MotionEvent.obtain(event.getDownTime(), event.getEventTime(), event.getAction(), event.getX(),
                        event.getY() + mBinding.recyclerFolder.getBottom(), event.getMetaState());
                newEvent.recycle();
                mBinding.recyclerFolder.dispatchTouchEvent(newEvent);

                if (!mIsFolderDragSelecting) {
                    RecyclerView.ViewHolder viewHolder = mBinding.recyclerSize.findViewHolderForLayoutPosition(0);
                    RecyclerView.ViewHolder viewHolder2 = mBinding.recyclerSize.findViewHolderForLayoutPosition(1);
                    gridDragUp(viewHolder, viewHolder2);
                    mFolderDragSelectListener.startDragSelection(mFolderAdapter.getItemCount() - 1);
                }
            }
            //size drag down------------------------------------------------------------------------
            else if (mIsSizeDragSelecting && event.getY() > 0
                    && mIsFolderDragSelecting && event.getAction() != MotionEvent.ACTION_UP && isSizeStart) {
                gridDragDown();

                if (mFolderSelectedPosition2 == -1)
                    folderHolderCallOnClick(mFolderAdapter.getItemCount() - 1);
                else if (mFolderSelectedState)
                    for (int i = mFolderSelectedPosition1; i < mFolderAdapter.getItemCount(); i++)
                        folderHolderCallOnClick(i);
                else
                    for (int i = mFolderSelectedPosition2 + 1; i < mFolderAdapter.getItemCount(); i++)
                        folderHolderCallOnClick(i);

                mIsFolderDragSelecting = false;
                mFolderSelectedState = false;
                mFolderStartPosition = -1;
                mFolderSelectedPosition1 = -1;
                mFolderSelectedPosition2 = -1;
            }
            //--------------------------------------------------------------------------------------
            if (event.getAction() == MotionEvent.ACTION_UP && isSizeStart) {
                mBinding.recyclerFolder.dispatchTouchEvent(event);
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

    private void folderToSizeDragDown() {
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

    private void folderToSizeDragUp() {
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
            RecyclerView.ViewHolder viewHolder = mBinding.recyclerSize.findViewHolderForLayoutPosition(0);
            RecyclerView.ViewHolder viewHolder2 = mBinding.recyclerSize.findViewHolderForLayoutPosition(1);
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
        mIsNavigationSet = false;
    }

    //size adapter click listener-------------------------------------------------------------------
    @Override
    public void onSizeItemViewClick(Size size, MaterialCheckBox checkBox) {
        if (mActionMode == null) {
            mNavController.navigate(ListFragmentDirections.actionListFragmentToInputOutputFragment(
                    mThisCategoryId, mThisFolderId, size.getId(), mParentCategory));
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
            if (viewHolder instanceof SizeAdapterList.SizeListVH)
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
        MenuItem favoriteIcon = menu.getItem(1);
        MenuItem viewTypeIcon = menu.getItem(2);

        favoriteIcon.setIcon(mModel.isFavoriteView() ? R.drawable.icon_favorite : R.drawable.icon_favorite_border);
        viewTypeIcon.setIcon(mViewType == LISTVIEW ? R.drawable.icon_list : R.drawable.icon_grid);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull @NotNull Menu menu, @NonNull @NotNull MenuInflater inflater) {
        inflater.inflate(R.menu.list_menu, menu);
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
            setSizeRecyclerLayout();
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
            setSizeLive();
            return true;
        } else if (item.getItemId() == R.id.menu_list_search) {
            mNavController.navigate(ListFragmentDirections.actionListFragmentToSearchActivity());
            return true;
        }
        return false;
    }

    @Override
    public void onSaveInstanceState(@NonNull @NotNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(ACTION_MODE, mActionModeOn);
    }
}