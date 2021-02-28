package com.example.project_myfit.ui.main.listfragment;

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
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_myfit.MainActivityViewModel;
import com.example.project_myfit.R;
import com.example.project_myfit.databinding.ActionModeTitleBinding;
import com.example.project_myfit.databinding.FragmentListBinding;
import com.example.project_myfit.databinding.ListPopupMenuBinding;
import com.example.project_myfit.dialog.AddFolderDialog;
import com.example.project_myfit.dialog.FolderNameEditDialog;
import com.example.project_myfit.dialog.ItemMoveDialog;
import com.example.project_myfit.dialog.SelectedItemDeleteDialog;
import com.example.project_myfit.dialog.SortDialog;
import com.example.project_myfit.dialog.TreeViewDialog;
import com.example.project_myfit.ui.main.database.Category;
import com.example.project_myfit.ui.main.listfragment.adapter.folderdapter.FolderAdapter;
import com.example.project_myfit.ui.main.listfragment.adapter.folderdapter.FolderDragCallBack;
import com.example.project_myfit.ui.main.listfragment.adapter.sizeadapter.SizeAdapterGrid;
import com.example.project_myfit.ui.main.listfragment.adapter.sizeadapter.SizeAdapterList;
import com.example.project_myfit.ui.main.listfragment.adapter.sizeadapter.SizeAdapterListener;
import com.example.project_myfit.ui.main.listfragment.adapter.sizeadapter.SizeDragCallBackGrid;
import com.example.project_myfit.ui.main.listfragment.adapter.sizeadapter.SizeDragCallBackList;
import com.example.project_myfit.ui.main.listfragment.database.Folder;
import com.example.project_myfit.ui.main.listfragment.database.Size;
import com.example.project_myfit.ui.main.listfragment.treeview.TreeHolderCategory;
import com.example.project_myfit.ui.main.listfragment.treeview.TreeHolderFolder;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textview.MaterialTextView;
import com.michaelflisar.dragselectrecyclerview.DragSelectTouchListener;
import com.unnamed.b.atv.model.TreeNode;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static com.example.project_myfit.MyFitConstant.ACTION_MODE;
import static com.example.project_myfit.MyFitConstant.ACTION_MODE_OFF;
import static com.example.project_myfit.MyFitConstant.ACTION_MODE_ON;
import static com.example.project_myfit.MyFitConstant.DELETE_DIALOG;
import static com.example.project_myfit.MyFitConstant.FOLDER_ADD_DIALOG;
import static com.example.project_myfit.MyFitConstant.FOLDER_EDIT_DIALOG;
import static com.example.project_myfit.MyFitConstant.FOLDER_ID;
import static com.example.project_myfit.MyFitConstant.FOLDER_TOGGLE;
import static com.example.project_myfit.MyFitConstant.GRIDVIEW;
import static com.example.project_myfit.MyFitConstant.LISTVIEW;
import static com.example.project_myfit.MyFitConstant.MOVE_DIALOG;
import static com.example.project_myfit.MyFitConstant.SORT_BRAND;
import static com.example.project_myfit.MyFitConstant.SORT_BRAND_REVERSE;
import static com.example.project_myfit.MyFitConstant.SORT_CREATE;
import static com.example.project_myfit.MyFitConstant.SORT_CREATE_REVERSE;
import static com.example.project_myfit.MyFitConstant.SORT_CUSTOM;
import static com.example.project_myfit.MyFitConstant.SORT_DIALOG;
import static com.example.project_myfit.MyFitConstant.SORT_LIST;
import static com.example.project_myfit.MyFitConstant.SORT_NAME;
import static com.example.project_myfit.MyFitConstant.SORT_NAME_REVERSE;
import static com.example.project_myfit.MyFitConstant.TREE_ADD_FOLDER;
import static com.example.project_myfit.MyFitConstant.TREE_DIALOG;
import static com.example.project_myfit.MyFitConstant.VIEW_TYPE;

//TODO 휴지통 클릭

@SuppressLint("ClickableViewAccessibility")
public class ListFragment extends Fragment implements SizeAdapterListener,
        AddFolderDialog.AddFolderConfirmClick, AddFolderDialog.TreeAddFolderConfirmClick,
        TreeNode.TreeNodeClickListener, TreeViewDialog.TreeViewAddClick,
        SelectedItemDeleteDialog.SelectedItemDeleteConfirmClick, FolderNameEditDialog.FolderNameEditConfirmClick,
        ItemMoveDialog.ItemMoveConfirmClick, SortDialog.SortConfirmClick {

    private MainActivityViewModel mActivityModel;
    private ListViewModel mModel;
    private FragmentListBinding mBinding;
    private ActionModeTitleBinding mActionModeTitleBinding;
    private ItemTouchHelper mTouchHelperList, mTouchHelperGrid, mTouchHelperFolder;
    private boolean isFolderDragSelectEnable, isSizeDragSelectEnable, mScrollEnable, mActionModeOn, mFolderToggle, isDragging, isFolderStart, isSizeStart, mSizeLastSelectedState, mFolderSelectedState;
    private MenuItem mEditMenu, mMoveMenu, mDeletedMenu;
    private DragSelectTouchListener mSelectListenerSize, mSelectListenerFolder;
    private SharedPreferences mViewTypePreference, mSortPreference, mFolderTogglePreference;
    private int mViewType, mSort;
    private ActionMode mActionMode;
    private ListPopupMenuBinding mPopupMenuBinding;
    private PopupWindow mPopupWindow;
    private LiveData<List<Folder>> mFolderLive;
    private LiveData<List<Size>> mSizeLive;
    private int mFolderStartPosition = -1;
    private int mSizeStartPosition = -1;
    private int mFolderSelectedPosition2 = -1;
    private int mFolderSelectedPosition1 = -1;
    private int mSizeSelectedPosition2 = -1;
    private int mSizeSelectedPosition1 = -1;
    private OnBackPressedCallback mOnBackPress;
    private FolderAdapter mFolderAdapter;
    private SizeAdapterList mSizeAdapterList;
    private SizeAdapterGrid mSizeAdapterGrid;
    private FloatingActionButton mActivityFab;
    private final ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(@NotNull ActionMode mode, Menu menu) {
            mActionMode = mode;
            mActionModeOn = true;

            mode.getMenuInflater().inflate(R.menu.list_action_mode, menu);
            mode.setCustomView(mActionModeTitleBinding.getRoot());

            mFolderAdapter.setActionModeState(ACTION_MODE_ON);
            if (mViewType == LISTVIEW) mSizeAdapterList.setActionModeState(ACTION_MODE_ON);
            else mSizeAdapterGrid.setActionModeState(ACTION_MODE_ON);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, @NotNull Menu menu) {
            mActionModeTitleBinding.actionModeSelectAll.setOnClickListener(v -> {
                mModel.getSelectedItemSize().clear();
                mModel.getSelectedItemFolder().clear();
                if (mActionModeTitleBinding.actionModeSelectAll.isChecked()) {
                    mModel.getSelectedItemFolder().addAll(mFolderAdapter.getCurrentList());
                    mModel.getSelectedItemFolder().removeIf(folder -> folder.getId() == -1);
                    mModel.getSelectedItemSize().addAll(mSizeAdapterList.getCurrentList());
                    mFolderAdapter.selectAll();
                    if (mViewType == LISTVIEW) mSizeAdapterList.selectAll();
                    else mSizeAdapterGrid.selectAll();
                } else {
                    mFolderAdapter.deselectAll();
                    if (mViewType == LISTVIEW) mSizeAdapterList.deselectAll();
                    else mSizeAdapterGrid.deselectAll();
                }
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
                showDialog(FolderNameEditDialog.getInstance(mModel.getSelectedItemFolder().get(0).getFolderName()), FOLDER_EDIT_DIALOG);
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mActionMode = null;
            mActionModeOn = false;

            mModel.setSelectedPositionFolder(mFolderAdapter.getSelectedPosition());
            if (mViewType == LISTVIEW)
                mModel.setSelectedPositionSizeList(mSizeAdapterList.getSelectedPosition());
            else mModel.setSelectedPositionSizeGrid(mSizeAdapterGrid.getSelectedPosition());

            mFolderAdapter.setActionModeState(ACTION_MODE_OFF);
            if (mViewType == LISTVIEW) mSizeAdapterList.setActionModeState(ACTION_MODE_OFF);
            else mSizeAdapterGrid.setActionModeState(ACTION_MODE_OFF);

            mActionModeTitleBinding.actionModeSelectAll.setChecked(false);
            ((ViewGroup) mActionModeTitleBinding.getRoot().getParent()).removeAllViews();
        }
    };

    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        super.onAttach(context);
        mOnBackPress = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (requireActivity().getIntent().getLongExtra(FOLDER_ID, 0) != 0 && !mPopupWindow.isShowing())
                    requireActivity().finish();
                else if (mPopupWindow.isShowing()) mPopupWindow.dismiss();
                else {
                    mOnBackPress.remove();
                    requireActivity().onBackPressed();
                }
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, mOnBackPress);
    }

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mModel = new ViewModelProvider(this).get(ListViewModel.class);
        mActivityModel = new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);
        mModel.setThisFolder(mActivityModel);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mActivityModel.setSize(null);
        mActivityModel.setFolder(null);

        dragSelectListenerInit();
        preferenceInit();

        mFolderAdapter = new FolderAdapter(mModel);
        mSizeAdapterList = new SizeAdapterList(mModel);
        mSizeAdapterGrid = new SizeAdapterGrid(mModel);
        folderAdapterClick();
        mSizeAdapterList.setOnSizeAdapterListener(this);
        mSizeAdapterGrid.setOnSizeAdapterListener(this);

        mBinding = FragmentListBinding.inflate(inflater);
        View view = mBinding.getRoot();
        mBinding.setCategory(mActivityModel.getCategory());
        recyclerViewInit();
        folderToggleClick();
        autoScroll();

        //home icon click
        mBinding.listIconHome.setOnClickListener(v -> Navigation.findNavController(requireActivity(), R.id.host_fragment).navigate(R.id.action_listFragment_to_mainFragment));

        //category text click
        mBinding.listTextCategory.setOnClickListener(v -> {
            if (mModel.getThisFolder() != null)
                Navigation.findNavController(requireActivity(), R.id.host_fragment).navigate(R.id.action_listFragment_refresh);
        });

        //go top fab click
        mBinding.listFab.setOnClickListener(v -> {
            mBinding.listScrollView.scrollTo(0, 0);
            mBinding.listScrollView.smoothScrollBy(0, 0);
        });

        mActionModeTitleBinding = ActionModeTitleBinding.inflate(inflater);

        mPopupMenuBinding = ListPopupMenuBinding.inflate(inflater);
        mPopupWindow = new PopupWindow(mPopupMenuBinding.getRoot(), ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mPopupWindow.setOutsideTouchable(true);
        popupMenuClick();

        if (mModel.getThisFolder() != null) setNavigation(mModel.getFolderHistory());

        //restore actionMode
        if (savedInstanceState != null && savedInstanceState.getBoolean(ACTION_MODE)) {
            ((AppCompatActivity) requireActivity()).startSupportActionMode(mActionModeCallback);
            mFolderAdapter.setSelectedPosition(mModel.getSelectedPositionFolder());
            if (mViewType == LISTVIEW)
                mSizeAdapterList.setSelectedPosition(mModel.getSelectedPositionSizeList());
            else mSizeAdapterGrid.setSelectedPosition(mModel.getSelectedPositionSizeGrid());
        }

        return view;
    }

    private void dragSelectListenerInit() {
        DragSelectTouchListener.OnAdvancedDragSelectListener dragSelectListenerFolder = new DragSelectTouchListener.OnAdvancedDragSelectListener() {
            @Override
            public void onSelectionStarted(int i) {
                isFolderDragSelectEnable = true;
                mBinding.listScrollView.setScrollable(false);
                if (mFolderStartPosition == -1) folderHolderCallOnClick(i);
                mFolderStartPosition = i;
            }

            @Override
            public void onSelectionFinished(int i) {
                mBinding.listScrollView.setScrollable(true);
            }

            @Override
            public void onSelectChange(int i, int i1, boolean b) {
                mFolderSelectedPosition1 = i;
                mFolderSelectedPosition2 = i1;
                mFolderSelectedState = b;
                for (int j = i; j <= i1; j++) folderHolderCallOnClick(j);
            }
        };
        DragSelectTouchListener.OnAdvancedDragSelectListener dragSelectListenerSize = new DragSelectTouchListener.OnAdvancedDragSelectListener() {
            @Override
            public void onSelectionStarted(int i) {
                isSizeDragSelectEnable = true;
                mBinding.listScrollView.setScrollable(false);
                if (mSizeStartPosition == -1) sizeHolderCallOnClick(i);
                mSizeStartPosition = i;
            }

            @Override
            public void onSelectionFinished(int i) {
                mBinding.listScrollView.setScrollable(true);
            }

            @Override
            public void onSelectChange(int i, int i1, boolean b) {
                mSizeSelectedPosition1 = i;
                mSizeSelectedPosition2 = i1;
                mSizeLastSelectedState = b;
                for (int j = i; j <= i1; j++) sizeHolderCallOnClick(j);
            }
        };

        mSelectListenerFolder = new DragSelectTouchListener().withSelectListener(dragSelectListenerFolder);
        mSelectListenerSize = new DragSelectTouchListener().withSelectListener(dragSelectListenerSize);
    }

    private void preferenceInit() {
        mViewTypePreference = requireActivity().getSharedPreferences(VIEW_TYPE, Context.MODE_PRIVATE);
        mViewType = mViewTypePreference.getInt(VIEW_TYPE, LISTVIEW);

        mSortPreference = requireActivity().getSharedPreferences(SORT_LIST, Context.MODE_PRIVATE);
        mSort = mSortPreference.getInt(SORT_LIST, SORT_CUSTOM);
        mModel.setSort(mSort);

        mFolderTogglePreference = requireActivity().getSharedPreferences(FOLDER_TOGGLE, Context.MODE_PRIVATE);
        mFolderToggle = mFolderTogglePreference.getBoolean(FOLDER_TOGGLE, true);
    }

    private void folderAdapterClick() {
        mFolderAdapter.setOnFolderAdapterListener(new FolderAdapter.FolderAdapterListener() {
            @Override
            public void onFolderItemViewClick(Folder folder, MaterialCheckBox checkBox, int position) {
                if (mActionMode == null) {
                    mActivityModel.setFolder(folder);
                    Navigation.findNavController(requireActivity(), R.id.host_fragment).navigate(R.id.action_listFragment_refresh);
                } else {
                    checkBox.setChecked(!checkBox.isChecked());
                    mModel.folderSelected(folder, checkBox.isChecked(), position, mFolderAdapter);
                }
            }

            @Override
            public void onFolderItemViewLongClick(MaterialCardView cardView, int position) {
                if (mActionMode == null) {
                    mModel.selectedItemListInit();
                    ((AppCompatActivity) requireActivity()).startSupportActionMode(mActionModeCallback);
                }
                isFolderStart = true;
                mSelectListenerFolder.startDragSelection(position);
            }

            @Override
            public void onFolderDragHandleTouch(RecyclerView.ViewHolder holder, LinearLayoutCompat folderAmountLayout) {
                if (mActionMode == null && !isDragging) {
                    isDragging = true;
                    folderAmountLayout.setVisibility(View.GONE);
                    mTouchHelperFolder.startDrag(holder);
                } else if (isDragging) isDragging = false;
            }
        });
    }

    private void recyclerViewInit() {
        mBinding.recyclerSize.setHasFixedSize(true);
        mBinding.recyclerFolder.setAdapter(mFolderAdapter);
        mBinding.recyclerFolder.addOnItemTouchListener(mSelectListenerFolder);
        mBinding.recyclerSize.addOnItemTouchListener(mSelectListenerSize);
    }

    private void folderToggleClick() {
        if (mFolderToggle) {
            mBinding.folderToggleIcon.setImageResource(R.drawable.icon_triangle_down);
            mBinding.recyclerFolder.setVisibility(View.VISIBLE);
        } else {
            mBinding.folderToggleIcon.setImageResource(R.drawable.icon_triangle_left);
            mBinding.recyclerFolder.setVisibility(View.GONE);
        }

        mBinding.folderToggleIcon.setOnClickListener(v -> {
            if (mBinding.recyclerFolder.getVisibility() == View.VISIBLE) {
                mBinding.folderToggleIcon.setImageResource(R.drawable.icon_triangle_left);
                mBinding.recyclerFolder.setVisibility(View.GONE);
                mFolderToggle = false;
            } else {
                mBinding.folderToggleIcon.setImageResource(R.drawable.icon_triangle_down);
                mBinding.recyclerFolder.setVisibility(View.VISIBLE);
                mFolderToggle = true;
            }
            SharedPreferences.Editor editor = mFolderTogglePreference.edit();
            editor.putBoolean(FOLDER_TOGGLE, mFolderToggle);
            editor.apply();
        });

    }

    private void autoScroll() {
        mBinding.recyclerFolder.setOnTouchListener((v, event) -> {
            //folder auto scroll--------------------------------------------------------------------
            if (!isSizeStart && (isFolderDragSelectEnable || isDragging)) {
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
            if (isFolderDragSelectEnable && !isDragging && event.getY() > v.getBottom() - 50
                    && event.getAction() != MotionEvent.ACTION_UP && isFolderStart) {
                //dispatch event
                MotionEvent newEvent = MotionEvent.obtain(event.getDownTime(), event.getEventTime(), event.getAction(), event.getX(),
                        event.getY() - v.getBottom(), event.getMetaState());
                newEvent.recycle();
                mBinding.recyclerSize.dispatchTouchEvent(newEvent);

                //folder drag down------------------------------------------------------------------
                if (!isSizeDragSelectEnable) {
                    folderToSizeDragDown();
                    mSelectListenerSize.startDragSelection(0);
                    mFolderSelectedState = false;
                    mFolderSelectedPosition1 = -1;
                    mFolderSelectedPosition2 = -1;
                }
            }
            //folder drag up------------------------------------------------------------------------
            else if (isFolderDragSelectEnable && !isDragging && event.getY() < v.getBottom()
                    && isSizeDragSelectEnable && event.getAction() != MotionEvent.ACTION_UP && isFolderStart) {
                folderToSizeDragUp();

                RecyclerView.ViewHolder viewHolder = mBinding.recyclerSize.findViewHolderForLayoutPosition(0);
                if (viewHolder != null) viewHolder.itemView.callOnClick();

                RecyclerView.ViewHolder viewHolder2 = mBinding.recyclerSize.findViewHolderForLayoutPosition(1);
                if (mViewType == GRIDVIEW && viewHolder2 != null &&
                        ((mSizeSelectedPosition2 == 1 && mSizeLastSelectedState) || (mSizeSelectedPosition2 == 3 && !mSizeLastSelectedState)))
                    viewHolder2.itemView.callOnClick();

                isSizeDragSelectEnable = false;
                mSizeLastSelectedState = false;
                mSizeSelectedPosition1 = -1;
                mSizeSelectedPosition2 = -1;
                mSizeStartPosition = -1;
            }
            //--------------------------------------------------------------------------------------
            if (event.getAction() == MotionEvent.ACTION_UP && isFolderStart) {
                mBinding.recyclerSize.dispatchTouchEvent(event);
                isFolderStart = false;
                isSizeStart = false;
                isFolderDragSelectEnable = false;
                isSizeDragSelectEnable = false;
                mFolderSelectedState = false;
                mSizeLastSelectedState = false;
                mFolderSelectedPosition1 = -1;
                mFolderSelectedPosition2 = -1;
                mSizeSelectedPosition1 = -1;
                mSizeSelectedPosition2 = -1;
                mFolderStartPosition = -1;
                mSizeStartPosition = -1;
            }
            return false;
        });

        mBinding.recyclerSize.setOnTouchListener((v, event) -> {
            //size auto scroll----------------------------------------------------------------------
            if (!isFolderStart && (isSizeDragSelectEnable || isDragging)) {
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
            if (isSizeDragSelectEnable && !isDragging && event.getY() < 0
                    && event.getAction() != MotionEvent.ACTION_UP && isSizeStart) {
                MotionEvent newEvent = MotionEvent.obtain(event.getDownTime(), event.getEventTime(), event.getAction(), event.getX(),
                        event.getY() + mBinding.recyclerFolder.getBottom(), event.getMetaState());
                newEvent.recycle();
                mBinding.recyclerFolder.dispatchTouchEvent(newEvent);

                if (!isFolderDragSelectEnable) {
                    RecyclerView.ViewHolder viewHolder = mBinding.recyclerSize.findViewHolderForLayoutPosition(0);
                    RecyclerView.ViewHolder viewHolder2 = mBinding.recyclerSize.findViewHolderForLayoutPosition(1);
                    gridDragUp(viewHolder, viewHolder2);
                    mSelectListenerFolder.startDragSelection(mFolderAdapter.getItemCount() - 1);
                }
            }
            //size drag down------------------------------------------------------------------------
            else if (isSizeDragSelectEnable && !isDragging && event.getY() > 0
                    && isFolderDragSelectEnable && event.getAction() != MotionEvent.ACTION_UP && isSizeStart) {
                gridDragDown();

                if (mFolderSelectedPosition2 == -1)
                    folderHolderCallOnClick(mFolderAdapter.getItemCount() - 1);
                else if (mFolderSelectedState)
                    for (int i = mFolderSelectedPosition1; i < mFolderAdapter.getItemCount(); i++)
                        folderHolderCallOnClick(i);
                else
                    for (int i = mFolderSelectedPosition2 + 1; i < mFolderAdapter.getItemCount(); i++)
                        folderHolderCallOnClick(i);

                isFolderDragSelectEnable = false;
                mFolderSelectedState = false;
                mFolderStartPosition = -1;
                mFolderSelectedPosition1 = -1;
                mFolderSelectedPosition2 = -1;
            }
            //--------------------------------------------------------------------------------------
            if (event.getAction() == MotionEvent.ACTION_UP && isSizeStart) {
                mBinding.recyclerFolder.dispatchTouchEvent(event);
                isFolderStart = false;
                isSizeStart = false;
                isSizeDragSelectEnable = false;
                isFolderDragSelectEnable = false;
                mFolderSelectedState = false;
                mSizeLastSelectedState = false;
                mFolderSelectedPosition1 = -1;
                mFolderSelectedPosition2 = -1;
                mSizeSelectedPosition1 = -1;
                mSizeSelectedPosition2 = -1;
                mFolderStartPosition = -1;
                mSizeStartPosition = -1;
            }
            return false;
        });

        mBinding.listScrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if ((isSizeDragSelectEnable || isFolderDragSelectEnable || isDragging) && mScrollEnable && scrollY > oldScrollY)
                v.postDelayed(() -> v.scrollBy(0, 1), 50);
            else if ((isSizeDragSelectEnable || isFolderDragSelectEnable || isDragging) && mScrollEnable && scrollY < oldScrollY)
                v.postDelayed(() -> v.scrollBy(0, -1), 50);
            if (oldScrollY < scrollY) mBinding.listFab.show();
            else if (scrollY == 0) mBinding.listFab.hide();
        });
    }

    private void popupMenuClick() {
        mPopupMenuBinding.addFolder.setOnClickListener(v -> {
            showDialog(AddFolderDialog.getInstance(false), FOLDER_ADD_DIALOG);
            mPopupWindow.dismiss();
        });

        mPopupMenuBinding.sort.setOnClickListener(v -> {
            showDialog(SortDialog.getInstance(mSort), SORT_DIALOG);
            mPopupWindow.dismiss();
        });
    }

    private void folderToSizeDragDown() {
        int lastPosition = mFolderAdapter.getItemCount();
        if (mFolderSelectedPosition1 == -1)
            for (int i = mFolderStartPosition + 1; i < lastPosition; i++)
                folderHolderCallOnClick(i);
        else if (mFolderStartPosition < mFolderSelectedPosition1) {
            if (mFolderSelectedPosition1 == mFolderSelectedPosition2)
                if (mFolderSelectedState)
                    for (int i = mFolderSelectedPosition1 + 1; i < lastPosition; i++)
                        folderHolderCallOnClick(i);
                else
                    for (int i = mFolderSelectedPosition1; i < lastPosition; i++)
                        folderHolderCallOnClick(i);
            else if (mFolderSelectedPosition1 < mFolderSelectedPosition2)
                if (mFolderSelectedState)
                    for (int i = mFolderSelectedPosition2 + 1; i < lastPosition; i++)
                        folderHolderCallOnClick(i);
                else
                    for (int i = mFolderSelectedPosition1; i < lastPosition; i++)
                        folderHolderCallOnClick(i);
        } else if (mFolderStartPosition > mFolderSelectedPosition1) {
            for (int i = mFolderStartPosition + 1; i < lastPosition; i++)
                folderHolderCallOnClick(i);
            if (mFolderSelectedPosition1 == mFolderSelectedPosition2) {
                if (mFolderSelectedState)
                    for (int i = mFolderSelectedPosition1; i < mFolderStartPosition; i++)
                        folderHolderCallOnClick(i);
                else for (int i = mFolderSelectedPosition1 + 1; i < mFolderStartPosition; i++)
                    folderHolderCallOnClick(i);
            } else {
                if (mFolderSelectedState)
                    for (int i = mFolderSelectedPosition1; i < mFolderStartPosition; i++)
                        folderHolderCallOnClick(i);
                else for (int i = mFolderSelectedPosition2 + 1; i < mFolderStartPosition; i++)
                    folderHolderCallOnClick(i);
            }
        }
    }

    private void folderToSizeDragUp() {
        for (int i = mFolderStartPosition + 1; i < mFolderAdapter.getItemCount(); i++)
            folderHolderCallOnClick(i);
        mSelectListenerFolder.startDragSelection(mFolderStartPosition);
    }

    private void gridDragUp(RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder viewHolder2) {
        if (mViewType == GRIDVIEW && viewHolder != null && viewHolder2 != null) {
            if (mSizeStartPosition == 0 && mSizeSelectedPosition2 >= 1
                    && (mSizeLastSelectedState || mSizeSelectedPosition2 >= 3))
                viewHolder2.itemView.callOnClick();
            else if (mSizeStartPosition == 1) {
                if (mSizeSelectedPosition1 != 0 && mSizeSelectedPosition2 != 2)
                    viewHolder.itemView.callOnClick();
                else if (mSizeSelectedPosition2 == 0 && !mSizeLastSelectedState) {
                    viewHolder.itemView.callOnClick();
                }
            } else if (mSizeStartPosition > 1) {
                if (mSizeSelectedPosition1 != 0) viewHolder.itemView.callOnClick();
                else if (!mSizeLastSelectedState) viewHolder.itemView.callOnClick();
            }
        }
    }

    private void gridDragDown() {
        if (mViewType == GRIDVIEW) {
            RecyclerView.ViewHolder viewHolder = mBinding.recyclerSize.findViewHolderForLayoutPosition(0);
            RecyclerView.ViewHolder viewHolder2 = mBinding.recyclerSize.findViewHolderForLayoutPosition(1);
            if (viewHolder != null && viewHolder2 != null)
                if (mSizeStartPosition == 0) {
                    if ((mSizeSelectedPosition2 == 1 && mSizeLastSelectedState)
                            || (mSizeSelectedPosition2 == 3 && !mSizeLastSelectedState))
                        viewHolder2.itemView.callOnClick();
                } else if (mSizeStartPosition == 1) {
                    if (mSizeSelectedPosition2 == -1
                            || (mSizeSelectedPosition2 == 0 && !mSizeLastSelectedState)
                            || (mSizeSelectedPosition2 == 3 && !mSizeLastSelectedState))
                        viewHolder.itemView.callOnClick();
                } else if (mSizeStartPosition > 1)
                    if (mSizeSelectedPosition1 != 0 || !mSizeLastSelectedState)
                        viewHolder.itemView.callOnClick();
        }
    }

    private void folderHolderCallOnClick(int i) {
        RecyclerView.ViewHolder viewHolder = mBinding.recyclerFolder.findViewHolderForLayoutPosition(i);
        if (viewHolder != null) viewHolder.itemView.callOnClick();
    }

    private void sizeHolderCallOnClick(int i) {
        RecyclerView.ViewHolder viewHolder = mBinding.recyclerSize.findViewHolderForLayoutPosition(i);
        if (viewHolder != null) viewHolder.itemView.callOnClick();
    }

    //text navigation-------------------------------------------------------------------------------
    private void setNavigation(@NotNull List<Folder> folderHistory) {
        for (Folder folder : folderHistory) {
            mBinding.listNavigation.addView(getArrowIcon());
            mBinding.listNavigation.addView(getFolderNameTextView(folder));
        }
    }

    @NotNull
    public ImageView getArrowIcon() {
        Drawable arrowIcon = ResourcesCompat.getDrawable(requireActivity().getResources(), R.drawable.icon_arrow_forward, requireActivity().getTheme());
        ImageView imageView = new ImageView(requireContext());
        imageView.setImageDrawable(arrowIcon);
        imageView.setAlpha(0.7f);
        return imageView;
    }

    @NotNull
    public MaterialTextView getFolderNameTextView(@NotNull Folder folder) {
        MaterialTextView textView = new MaterialTextView(requireContext());
        textView.setAlpha(0.8f);
        float size = getResources().getDimension(R.dimen._4sdp);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size);
        textView.setText(folder.getFolderName());
        textView.setOnClickListener(v -> {
            if (mModel.getThisFolder().getId() != folder.getId()) {
                mActivityModel.setFolder(folder);
                Navigation.findNavController(requireActivity(), R.id.host_fragment).navigate(R.id.action_listFragment_refresh);
            }
        });
        textView.setMaxLines(1);
        return textView;
    }

    //----------------------------------------------------------------------------------------------
    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ActionBar actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
        String title = mModel.getThisFolder() == null ? mActivityModel.getCategory().getCategory() : mModel.getThisFolder().getFolderName();
        if (actionBar != null) actionBar.setTitle(title);
        fabClick();
    }

    private void fabClick() {
        mActivityFab = requireActivity().findViewById(R.id.activity_fab);
        mActivityFab.setOnClickListener(v -> {
            mActivityModel.setFolder(mModel.getThisFolder());
            Navigation.findNavController(requireActivity(), R.id.host_fragment).navigate(R.id.action_listFragment_to_inputOutputFragment);
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        itemTouchHelperInit();

        setSizeRecyclerLayout();

        setLiveData();

        actionModeLiveData();
    }

    private void itemTouchHelperInit() {
        mTouchHelperFolder = new ItemTouchHelper(new FolderDragCallBack(mFolderAdapter));
        mTouchHelperFolder.attachToRecyclerView(mBinding.recyclerFolder);
        mTouchHelperList = new ItemTouchHelper(new SizeDragCallBackList(mSizeAdapterList));
        mTouchHelperGrid = new ItemTouchHelper(new SizeDragCallBackGrid(mSizeAdapterGrid));
    }

    private void setSizeRecyclerLayout() {
        int padding8dp = (int) getResources().getDimension(R.dimen._8sdp);
        int paddingBottom = (int) getResources().getDimension(R.dimen._60sdp);
        if (mViewType == LISTVIEW) {
            mBinding.recyclerSize.setPadding(0, padding8dp, 0, paddingBottom);
            mBinding.recyclerSize.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false));
            mBinding.recyclerSize.setAdapter(mSizeAdapterList);
            mTouchHelperList.attachToRecyclerView(mBinding.recyclerSize);
        } else {
            mBinding.recyclerSize.setPadding(padding8dp, padding8dp, padding8dp, paddingBottom);
            mBinding.recyclerSize.setLayoutManager(new GridLayoutManager(requireContext(), 2, RecyclerView.VERTICAL, false));
            mBinding.recyclerSize.setAdapter(mSizeAdapterGrid);
            mTouchHelperGrid.attachToRecyclerView(mBinding.recyclerSize);
        }
    }

    private void setLiveData() {
        //folder
        if (mFolderLive != null && mFolderLive.hasObservers())
            mFolderLive.removeObservers(getViewLifecycleOwner());

        mFolderLive = mModel.getRepository().getFolderLiveByFolder(mModel.getFolderId());
        mFolderLive.observe(getViewLifecycleOwner(), folderList -> {
            if (mSort == SORT_CREATE)
                folderList.sort((o1, o2) -> Long.compare(o2.getId(), o1.getId()));
            else if (mSort == SORT_CREATE_REVERSE)
                folderList.sort((o1, o2) -> Long.compare(o1.getId(), o2.getId()));
            else if (mSort == SORT_BRAND || mSort == SORT_NAME)
                folderList.sort((o1, o2) -> o1.getFolderName().compareTo(o2.getFolderName()));
            else if (mSort == SORT_BRAND_REVERSE || mSort == SORT_NAME_REVERSE)
                folderList.sort((o1, o2) -> o2.getFolderName().compareTo(o1.getFolderName()));

            int size = folderList.size();
            if (size % 4 == 1) {
                for (int i = 0; i < 3; i++) {
                    Folder dummy = new Folder(-1, "dummy", 0, 0, mActivityModel.getCategory().getParentCategory());
                    folderList.add(dummy);
                }
            } else if (size % 4 == 2) {
                for (int i = 0; i < 2; i++) {
                    Folder dummy = new Folder(-1, "dummy", 0, 0, mActivityModel.getCategory().getParentCategory());
                    folderList.add(dummy);
                }
            } else if (size % 4 == 3) {
                Folder dummy = new Folder(-1, "dummy", 0, 0, mActivityModel.getCategory().getParentCategory());
                folderList.add(dummy);
            }

            mFolderAdapter.submitList(folderList, mModel.getRepository().getFolderFolderIdByParent(mModel.getThisCategory().getParentCategory())
                    , mModel.getRepository().getSizeFolderIdByParent(mModel.getThisCategory().getParentCategory()));
            mFolderAdapter.setSort(mSort);

            mBinding.recyclerFolderLayout.setVisibility(folderList.size() == 0 ? View.GONE : View.VISIBLE);
        });

        //size
        if (mSizeLive != null && mSizeLive.hasObservers())
            mSizeLive.removeObservers(getViewLifecycleOwner());

        mSizeLive = mModel.getRepository().getAllSizeLiveByFolder(mModel.getFolderId());
        mSizeLive.observe(getViewLifecycleOwner(), sizeList -> {
            if (sizeList.size() == 0) {
                mBinding.noData.setVisibility(View.VISIBLE);
                mBinding.recyclerSize.setVisibility(View.GONE);
            } else {
                mBinding.noData.setVisibility(View.GONE);
                mBinding.recyclerSize.setVisibility(View.VISIBLE);
            }

            if (mSort == SORT_CREATE)
                sizeList.sort((o1, o2) -> Integer.compare(o2.getId(), o1.getId()));
            else if (mSort == SORT_CREATE_REVERSE)
                sizeList.sort((o1, o2) -> Integer.compare(o1.getId(), o2.getId()));
            else if (mSort == SORT_BRAND)
                sizeList.sort((o1, o2) -> o1.getBrand().compareTo(o2.getBrand()));
            else if (mSort == SORT_BRAND_REVERSE)
                sizeList.sort((o1, o2) -> o2.getBrand().compareTo(o1.getBrand()));
            else if (mSort == SORT_NAME)
                sizeList.sort((o1, o2) -> o1.getName().compareTo(o2.getName()));
            else if (mSort == SORT_NAME_REVERSE)
                sizeList.sort((o1, o2) -> o2.getName().compareTo(o1.getName()));

            if (mModel.isFavoriteView()) {
                List<Size> favoriteList = new ArrayList<>();
                for (Size size : sizeList) if (size.isFavorite()) favoriteList.add(size);
                sizeList = favoriteList;
            }
            mSizeAdapterList.submitList(sizeList);
            mSizeAdapterList.setSort(mSort);

            mSizeAdapterGrid.submitList(sizeList);
            mSizeAdapterGrid.setSort(mSort);
        });
    }

    private void actionModeLiveData() {
        mModel.getSelectedAmount().observe(getViewLifecycleOwner(), integer -> {
            String title = integer + getString(R.string.item_selected);
            mActionModeTitleBinding.actionModeTitle.setText(title);

            if (mActionMode != null) {
                mEditMenu.setVisible(integer == 1 && mModel.getSelectedItemSize().size() == 0);
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
    public void onDestroyView() {
        super.onDestroyView();
        if (mActionMode != null) mActionMode.finish();
        mBinding = null;
        mFolderAdapter = null;
        mSizeAdapterList = null;
        mSizeAdapterGrid = null;
        mTouchHelperFolder = null;
        mTouchHelperList = null;
        mTouchHelperGrid = null;
        mActivityFab = null;
        mSelectListenerFolder = null;
        mSelectListenerSize = null;
    }

    @Override
    public void onSizeItemViewClick(Size size, MaterialCheckBox checkBox, int position) {
        if (mActionMode == null) {
            mActivityModel.setSize(size);
            Navigation.findNavController(requireActivity(), R.id.host_fragment).navigate(R.id.action_listFragment_to_inputOutputFragment);
        } else {
            checkBox.setChecked(!checkBox.isChecked());
            mModel.sizeSelected(size, checkBox.isChecked(), position, mSizeAdapterList, mSizeAdapterGrid, mViewType);
        }
    }

    @Override
    public void onSizeItemViewLongClick(MaterialCardView cardView, int position) {
        if (mActionMode == null) {
            mModel.selectedItemListInit();
            ((AppCompatActivity) requireActivity()).startSupportActionMode(mActionModeCallback);
        }
        isSizeStart = true;
        mSelectListenerSize.startDragSelection(position);
    }

    @Override
    public void onSizeDragHandleTouch(RecyclerView.ViewHolder viewHolder) {
        if (mActionMode != null && !isDragging) {
            isDragging = true;
            if (mViewType == LISTVIEW) mTouchHelperList.startDrag(viewHolder);
            else mTouchHelperGrid.startDrag(viewHolder);
        } else if (isDragging) isDragging = false;
    }

    @Override
    public void onSizeFavoriteClick(Size size) {
        mModel.getRepository().sizeUpdate(size);
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
        if (item.getItemId() == R.id.menu_list_list_grid) {
            if (mViewType == LISTVIEW) {
                item.setIcon(R.drawable.icon_grid);
                mViewType = GRIDVIEW;
            } else {
                mSizeAdapterList.setSelectedPosition(new HashSet<>());
                mSizeAdapterList.setActionModeState(0);
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
            setLiveData();
            return true;
        } else if (item.getItemId() == R.id.menu_list_search) {
            Navigation.findNavController(requireActivity(), R.id.host_fragment).navigate(R.id.action_listFragment_to_searchActivity);
            return true;
        }
        return false;
    }

    //dialog click----------------------------------------------------------------------------------
    private void showDialog(@NotNull DialogFragment dialog, String tag) {
        dialog.setTargetFragment(this, 0);
        dialog.show(getParentFragmentManager(), tag);
    }

    @Override//add folder confirm click
    public void addFolderConfirmClick(@NotNull String folderName) {
        Folder folder = new Folder(mModel.getCurrentTime(),
                folderName.trim(),
                mModel.getFolderId(),
                mModel.getRepository().getFolderLargestOrder() + 1,
                mModel.getThisCategory().getParentCategory());
        mModel.getRepository().folderInsert(folder);
    }

    @Override//category node add folder icon click
    public void treeViewCategoryAddClick(TreeNode node, TreeHolderCategory.CategoryTreeHolder value) {
        mModel.nodeAddClick(node, value);
        showDialog(AddFolderDialog.getInstance(true), TREE_ADD_FOLDER);
    }

    @Override//folder node add folder icon click
    public void treeViewFolderAddClick(TreeNode node, TreeHolderFolder.FolderTreeHolder value) {
        mModel.nodeAddClick(node, value);
        showDialog(AddFolderDialog.getInstance(true), TREE_ADD_FOLDER);
    }

    @Override//node add folder confirm click
    public void treeAddFolderConfirmClick(String folderName) {
        TreeNode oldNode = mModel.getAddNode();

        if (mModel.getCategoryAddValue() != null) {//category node
            TreeHolderCategory.CategoryTreeHolder oldViewHolder = mModel.getCategoryAddValue();

            int largestOrder = mModel.getRepository().getFolderLargestOrder() + 1;
            Folder folder = new Folder(mModel.getCurrentTime(), folderName.trim(), oldViewHolder.category.getId(), largestOrder, mActivityModel.getCategory().getParentCategory());
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

            TreeNode treeNode = new TreeNode(new TreeHolderFolder.FolderTreeHolder(folder, 40, this,
                    mModel.getSelectedItemFolder(), mModel.getSelectedItemSize(), mModel.getAllFolderByParent(),
                    mModel.getRepository().getFolderFolderIdByParent(mModel.getThisCategory().getParentCategory()),
                    mModel.getRepository().getSizeFolderIdByParent(mModel.getThisCategory().getParentCategory())))
                    .setViewHolder(new TreeHolderFolder(requireContext()));
            oldNode.getViewHolder().getTreeView().addNode(oldNode, treeNode);
            oldNode.getViewHolder().getTreeView().expandNode(oldNode);
            ((TreeHolderCategory) oldNode.getViewHolder()).setIconClickable();

            List<Long> folderFolderIdList = mModel.getRepository().getFolderFolderIdByParent(mModel.getThisCategory().getParentCategory());
            List<Long> sizeFolderIdList = mModel.getRepository().getSizeFolderIdByParent(mModel.getThisCategory().getParentCategory());

            TreeHolderCategory.CategoryTreeHolder newViewHolder = (TreeHolderCategory.CategoryTreeHolder) oldNode.getValue();
            TreeHolderCategory newViewHolder2 = (TreeHolderCategory) oldNode.getViewHolder();
            newViewHolder2.getBinding().amount.setText(getItemAmount(newViewHolder.category, folderFolderIdList, sizeFolderIdList));
        } else {//folder node
            TreeHolderFolder.FolderTreeHolder oldViewHolder = mModel.getFolderAddValue();

            int largestOrder = mModel.getRepository().getFolderLargestOrder() + 1;
            Folder folder = new Folder(mModel.getCurrentTime(), folderName.trim(), oldViewHolder.getFolder().getId(), largestOrder, mActivityModel.getCategory().getParentCategory());
            mModel.getRepository().folderInsert(folder);

            //find node(recreate)
            for (TreeNode folderNode : getAllFolderNode()) {
                TreeHolderFolder.FolderTreeHolder newViewHolder = (TreeHolderFolder.FolderTreeHolder) folderNode.getValue();
                if (oldViewHolder.getFolder().getId() == newViewHolder.getFolder().getId()) {
                    oldNode = folderNode;
                    break;
                }
            }

            int margin = (int) getResources().getDimension(R.dimen._8sdp);
            TreeNode treeNode = new TreeNode(new TreeHolderFolder.FolderTreeHolder(folder,
                    oldViewHolder.getMargin() + margin, this,
                    mModel.getSelectedItemFolder(), mModel.getSelectedItemSize(), mModel.getAllFolderByParent(),
                    mModel.getRepository().getFolderFolderIdByParent(mModel.getThisCategory().getParentCategory()),
                    mModel.getRepository().getSizeFolderIdByParent(mModel.getThisCategory().getParentCategory())))
                    .setViewHolder(new TreeHolderFolder(requireContext()));
            oldNode.getViewHolder().getTreeView().addNode(oldNode, treeNode);
            oldNode.getViewHolder().getTreeView().expandNode(oldNode);
            ((TreeHolderFolder) oldNode.getViewHolder()).setIconClickable();

            List<Long> folderFolderIdList = mModel.getRepository().getFolderFolderIdByParent(mModel.getThisCategory().getParentCategory());
            List<Long> sizeFolderIdList = mModel.getRepository().getSizeFolderIdByParent(mModel.getThisCategory().getParentCategory());

            TreeHolderFolder.FolderTreeHolder newViewHolder = (TreeHolderFolder.FolderTreeHolder) oldNode.getValue();
            TreeHolderFolder newViewHolder2 = (TreeHolderFolder) oldNode.getViewHolder();
            newViewHolder2.getBinding().amount.setText(getItemAmount(newViewHolder.getFolder(), folderFolderIdList, sizeFolderIdList));

            Folder dummy = newViewHolder.getFolder();
            if (dummy.getDummy() == 0) dummy.setDummy(1);
            else dummy.setDummy(0);
            mModel.getRepository().folderUpdate(dummy);
        }
    }

    @NotNull
    private String getItemAmount(Category category, @NotNull List<Long> folderFolderIdList, List<Long> sizeFolderIdList) {
        int amount = 0;
        for (Long f : folderFolderIdList) if (f == category.getId()) amount++;
        for (Long s : sizeFolderIdList) if (s == category.getId()) amount++;
        return String.valueOf(amount);
    }

    @NotNull
    private String getItemAmount(Folder folder, @NotNull List<Long> folderFolderIdList, List<Long> sizeFolderIdList) {
        int amount = 0;
        for (Long f : folderFolderIdList) if (f == folder.getId()) amount++;
        for (Long s : sizeFolderIdList) if (s == folder.getId()) amount++;
        return String.valueOf(amount);
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

    private void listingFolderNode(@NotNull List<TreeNode> folderNodeList, List<TreeNode> newFolderList) {
        for (TreeNode folderNode : folderNodeList) {
            if (folderNode.getChildren().size() != 0) {
                newFolderList.addAll(folderNode.getChildren());
                listingFolderNode(folderNode.getChildren(), newFolderList);
            }
        }
    }

    @Override//node click
    public void onClick(TreeNode node, Object value) {
        if (value instanceof TreeHolderCategory.CategoryTreeHolder && mModel.getSelectedAmount().getValue() != null) {//category node click
            TreeHolderCategory.CategoryTreeHolder categoryHolder = (TreeHolderCategory.CategoryTreeHolder) node.getValue();
            //current position is category & clicked category is not this category
            if (mModel.getThisFolder() == null && categoryHolder.category.getId() != mActivityModel.getCategory().getId())
                showDialog(ItemMoveDialog.getInstance(mModel.getSelectedAmount().getValue(), categoryHolder.category.getId()), MOVE_DIALOG);
                //current position is folder
            else if (mModel.getThisFolder() != null)
                showDialog(ItemMoveDialog.getInstance(mModel.getSelectedAmount().getValue(), categoryHolder.category.getId()), MOVE_DIALOG);
        } else if (value instanceof TreeHolderFolder.FolderTreeHolder && ((TreeHolderFolder) node.getViewHolder()).isNotSelected() && mModel.getSelectedAmount().getValue() != null) {
            TreeHolderFolder.FolderTreeHolder folderHolder = (TreeHolderFolder.FolderTreeHolder) node.getValue();
            //current position is category
            if (mModel.getThisFolder() == null)
                showDialog(ItemMoveDialog.getInstance(mModel.getSelectedAmount().getValue(), folderHolder.getFolder().getId()), MOVE_DIALOG);
                //current position is folder & clicked folder is not this folder
            else if (mModel.getThisFolder() != null && folderHolder.getFolder().getId() != mModel.getThisFolder().getId())
                showDialog(ItemMoveDialog.getInstance(mModel.getSelectedAmount().getValue(), folderHolder.getFolder().getId()), MOVE_DIALOG);
        }
    }

    @Override//item move confirm click
    public void itemMoveConfirmClick(long folderId) {
        mModel.selectedItemMove(folderId);
        TreeViewDialog treeViewDialog = ((TreeViewDialog) getParentFragmentManager().findFragmentByTag(TREE_DIALOG));
        if (treeViewDialog != null) treeViewDialog.dismiss();
        mActionMode.finish();
    }

    @Override//selected item delete confirm click
    public void selectedItemDeleteConfirmClick() {
        mModel.selectedItemDelete();
        mActionMode.finish();
    }

    @Override//folder name edit confirm click
    public void folderNameEditConfirmClick(String folderName) {
        Folder folder = mModel.getSelectedItemFolder().get(0);
        folder.setFolderName(folderName);
        mModel.getRepository().folderUpdate(folder);
        mActionMode.finish();
    }

    @Override//sort confirm click
    public void sortConfirmClick(int sort) {
        mSort = sort;
        mModel.setSort(mSort);
        setLiveData();
        SharedPreferences.Editor editor = mSortPreference.edit();
        editor.putInt(SORT_LIST, mSort);
        editor.apply();
    }

    @Override
    public void onSaveInstanceState(@NonNull @NotNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(ACTION_MODE, mActionModeOn);
    }
}