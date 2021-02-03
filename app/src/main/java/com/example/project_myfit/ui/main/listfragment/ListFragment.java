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
import com.example.project_myfit.dialog.TreeAddFolderDialog;
import com.example.project_myfit.dialog.TreeViewDialog;
import com.example.project_myfit.ui.main.listfragment.adapter.folderdapter.FolderAdapterListener;
import com.example.project_myfit.ui.main.listfragment.adapter.folderdapter.FolderDragCallBack;
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

import static com.example.project_myfit.MyFitConstant.ACTION_MODE_OFF;
import static com.example.project_myfit.MyFitConstant.ACTION_MODE_ON;
import static com.example.project_myfit.MyFitConstant.FOLDER_TOGGLE;
import static com.example.project_myfit.MyFitConstant.GRIDVIEW;
import static com.example.project_myfit.MyFitConstant.LISTVIEW;
import static com.example.project_myfit.MyFitConstant.SORT;
import static com.example.project_myfit.MyFitConstant.SORT_CUSTOM;
import static com.example.project_myfit.MyFitConstant.SORT_LATEST;
import static com.example.project_myfit.MyFitConstant.SORT_LATEST_REVERSE;
import static com.example.project_myfit.MyFitConstant.VIEW_TYPE;

//TODO 써치바
//TODO 전체적으로 글씨 줄이기
//TODO 휴지통 클릭
//TODO 오토 스크를 액션 업 스크롤 정지

@SuppressLint("ClickableViewAccessibility")
public class ListFragment extends Fragment implements SizeAdapterListener,
        AddFolderDialog.AddFolderConfirmClick, TreeAddFolderDialog.TreeAddFolderConfirmClick,
        TreeNode.TreeNodeClickListener, TreeViewDialog.TreeViewAddClick,
        SelectedItemDeleteDialog.SelectedItemDeleteConfirmClick, FolderNameEditDialog.FolderNameEditConfirmClick,
        ItemMoveDialog.ItemMoveConfirmClick, SortDialog.SortConfirmClick {

    private MainActivityViewModel mActivityModel;
    private ListViewModel mModel;
    private FragmentListBinding mBinding;
    private ActionModeTitleBinding mActionModeTitleBinding;
    private ItemTouchHelper mTouchHelperList, mTouchHelperGrid, mTouchHelperFolder;
    private FloatingActionButton mActivityFab;
    private boolean isFolderDragSelectEnable, isSizeDragSelectEnable, mScrollEnable, mActionModeOn, mFolderToggle, isDragging, isFolderStart, isSizeStart, mGridLastSelectedState, mFolderSelectedState;
    private MenuItem mEditMenu, mMoveMenu, mDeletedMenu;
    private ActionMode.Callback mActionModeCallback;
    private DragSelectTouchListener mSelectListenerSize, mSelectListenerFolder;
    private SharedPreferences mViewTypePreference, mSortPreference, mFolderPreference;
    private int mViewType, mSort;
    private ActionMode mActionMode;
    private ListPopupMenuBinding mPopupMenuBinding;
    private PopupWindow mPopupWindow;
    private LiveData<List<Folder>> mFolderLive;
    private LiveData<List<Size>> mSizeLive;
    private int mFolderStartPosition = -1;
    private int mGridStartPosition = -1;
    private int mFolderSelectedPosition2 = -1;
    private int mFolderSelectedPosition1 = -1;
    private int mGridSelectedPosition2 = -1;
    private int mGridSelectedPosition1 = -1;
    private OnBackPressedCallback mOnBackPress;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mModel = new ViewModelProvider(this).get(ListViewModel.class);
        mActivityModel = new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);

        mModel.setThisFolder(mActivityModel);

        dragSelectListenerInit();

        setHasOptionsMenu(true);
    }

    private void dragSelectListenerInit() {
        DragSelectTouchListener.OnAdvancedDragSelectListener dragSelectListenerFolder = new DragSelectTouchListener.OnAdvancedDragSelectListener() {
            @Override
            public void onSelectionStarted(int i) {
                isFolderDragSelectEnable = true;
                mBinding.listScrollView.setScrollable(false);
                if (mFolderStartPosition == -1)
                    folderHolderCallOnClick(i);
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
                if (mGridStartPosition == -1) {
                    RecyclerView.ViewHolder viewHolder = mBinding.recyclerSize.findViewHolderForLayoutPosition(i);
                    if (viewHolder != null) viewHolder.itemView.callOnClick();
                }
                mGridStartPosition = i;
            }

            @Override
            public void onSelectionFinished(int i) {
                mBinding.listScrollView.setScrollable(true);
            }

            @Override
            public void onSelectChange(int i, int i1, boolean b) {
                mGridSelectedPosition1 = i;
                mGridSelectedPosition2 = i1;
                mGridLastSelectedState = b;
                for (int j = i; j <= i1; j++) {
                    RecyclerView.ViewHolder viewHolder = mBinding.recyclerSize.findViewHolderForLayoutPosition(j);
                    if (viewHolder != null) viewHolder.itemView.callOnClick();
                }
            }
        };

        mSelectListenerFolder = new DragSelectTouchListener().withSelectListener(dragSelectListenerFolder);
        mSelectListenerSize = new DragSelectTouchListener().withSelectListener(dragSelectListenerSize);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mActivityModel.setSize(null);
        mActivityModel.setFolder(null);

        mBinding = FragmentListBinding.inflate(inflater);
        mBinding.setCategory(mActivityModel.getCategory());

        mActionModeTitleBinding = ActionModeTitleBinding.inflate(inflater);
        mPopupMenuBinding = ListPopupMenuBinding.inflate(inflater);

        mPopupWindow = new PopupWindow(mPopupMenuBinding.getRoot(), ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mPopupWindow.setOutsideTouchable(true);

        onBackPressInit();

        mViewTypePreference = requireActivity().getSharedPreferences(VIEW_TYPE, Context.MODE_PRIVATE);
        mViewType = mViewTypePreference.getInt(VIEW_TYPE, LISTVIEW);

        mSortPreference = requireActivity().getSharedPreferences(SORT, Context.MODE_PRIVATE);
        mSort = mSortPreference.getInt(SORT, SORT_CUSTOM);
        mModel.setSort(mSort);

        mFolderPreference = requireActivity().getSharedPreferences(FOLDER_TOGGLE, Context.MODE_PRIVATE);
        mFolderToggle = mFolderPreference.getBoolean(FOLDER_TOGGLE, true);

        //actionBar title
        ActionBar actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
        String title = mModel.getThisFolder() == null ? mActivityModel.getCategory().getCategory() : mModel.getThisFolder().getFolderName();
        if (actionBar != null) actionBar.setTitle(title);

        //home icon click
        mBinding.listIconHome.setOnClickListener(v -> Navigation.findNavController(requireActivity(), R.id.host_fragment).navigate(R.id.action_listFragment_to_mainFragment));

        //category text click
        mBinding.listTextCategory.setOnClickListener(v -> {
            if (mModel.getThisFolder() != null)
                Navigation.findNavController(requireActivity(), R.id.host_fragment).navigate(R.id.action_listFragment_refresh);
        });

        if (mModel.getThisFolder() != null) setNavigation(mModel.getFolderHistory());

        actionModeCallBackInit();

        //restore actionMode
        if (savedInstanceState != null && savedInstanceState.getBoolean("action mode")) {
            ((AppCompatActivity) requireActivity()).startSupportActionMode(mActionModeCallback);
            mModel.restoreAdapterSelectedPosition();
        }

        return mBinding.getRoot();
    }

    private void onBackPressInit() {
        mOnBackPress = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (mPopupWindow.isShowing()) mPopupWindow.dismiss();
                else {
                    mOnBackPress.remove();
                    requireActivity().onBackPressed();
                }
            }
        };
    }

    //text navigation-------------------------------------------------------------------------------
    private void setNavigation(List<Folder> folderHistory) {
        for (Folder folder : folderHistory) {
            mBinding.listNavigation.addView(getArrowIcon());
            mBinding.listNavigation.addView(getFolderNameView(folder));
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
    public MaterialTextView getFolderNameView(Folder folder) {
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

    //action mode-----------------------------------------------------------------------------------
    private void actionModeCallBackInit() {
        mActionModeCallback = new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                mActionModeOn = true;
                mActionMode = mode;

                mode.getMenuInflater().inflate(R.menu.list_action_mode, menu);
                mode.setCustomView(mActionModeTitleBinding.getRoot());

                mModel.setAdapterActionModeState(ACTION_MODE_ON);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                mActionModeTitleBinding.actionModeSelectAll.setOnClickListener(v -> mModel.selectAllClick(mActionModeTitleBinding.actionModeSelectAll.isChecked()));
                mEditMenu = menu.getItem(0);
                mMoveMenu = menu.getItem(1);
                mDeletedMenu = menu.getItem(2);
                return true;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                if (item.getItemId() == R.id.list_action_mode_del) {
                    if (mModel.getSelectedAmount().getValue() != null)
                        showDialog(SelectedItemDeleteDialog.getInstance(mModel.getSelectedAmount().getValue()), "delete");
                } else if (item.getItemId() == R.id.list_action_mode_move)
                    showDialog(new TreeViewDialog(), "tree");
                else
                    showDialog(FolderNameEditDialog.getInstance(mModel.getSelectedItemFolder().get(0).getFolderName()), "edit");
                return true;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                mActionMode = null;
                mActionModeOn = false;

                mModel.setSelectedPosition();
                mModel.setAdapterActionModeState(ACTION_MODE_OFF);

                mActionModeTitleBinding.actionModeSelectAll.setChecked(false);
                ((ViewGroup) mActionModeTitleBinding.getRoot().getParent()).removeAllViews();
            }
        };
    }
    //----------------------------------------------------------------------------------------------

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), mOnBackPress);

        recyclerViewInit();

        itemTouchHelperInit();

        setSizeRecyclerLayout();

        autoScroll();

        setLiveData();

        setClickListener();

        mModel.getSelectedAmount().observe(getViewLifecycleOwner(), integer -> {
            String title = integer + getString(R.string.item_selected);
            mActionModeTitleBinding.actionModeTitle.setText(title);

            if (mActionMode != null) {
                mEditMenu.setVisible(integer == 1 && mModel.getSelectedItemSize().size() == 0);
                mMoveMenu.setVisible(integer > 0);
                mDeletedMenu.setVisible(integer > 0);
            }

            int sizeSize = mModel.getSizeAdapterList().getCurrentList().size();
            List<Folder> folderList = new ArrayList<>(mModel.getFolderAdapter().getFolderList());
            folderList.removeIf(folder -> folder.getId() == -1);
            int folderSize = folderList.size();
            mActionModeTitleBinding.actionModeSelectAll.setChecked(sizeSize + folderSize == integer);
        });
    }

    private void recyclerViewInit() {
        mBinding.recyclerSize.setHasFixedSize(true);
        mBinding.recyclerFolder.setAdapter(mModel.getFolderAdapter());
        mBinding.recyclerFolder.addOnItemTouchListener(mSelectListenerFolder);
        mBinding.recyclerSize.addOnItemTouchListener(mSelectListenerSize);
    }

    private void itemTouchHelperInit() {
        mTouchHelperFolder = new ItemTouchHelper(new FolderDragCallBack(mModel.getFolderAdapter()));
        mTouchHelperFolder.attachToRecyclerView(mBinding.recyclerFolder);
        mTouchHelperList = new ItemTouchHelper(new SizeDragCallBackList(mModel.getSizeAdapterList()));
        mTouchHelperGrid = new ItemTouchHelper(new SizeDragCallBackGrid(mModel.getSizeAdapterGrid()));
    }

    private void setSizeRecyclerLayout() {
        int padding8dp = (int) getResources().getDimension(R.dimen._8sdp);
        int paddingBottom = (int) getResources().getDimension(R.dimen._60sdp);
        if (mViewType == LISTVIEW) {
            mBinding.recyclerSize.setPadding(0, padding8dp, 0, paddingBottom);
            mBinding.recyclerSize.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false));
            mBinding.recyclerSize.setAdapter(mModel.getSizeAdapterList());
            mTouchHelperList.attachToRecyclerView(mBinding.recyclerSize);
        } else {
            mBinding.recyclerSize.setPadding(padding8dp, padding8dp, padding8dp, paddingBottom);
            mBinding.recyclerSize.setLayoutManager(new GridLayoutManager(requireContext(), 2, RecyclerView.VERTICAL, false));
            mBinding.recyclerSize.setAdapter(mModel.getSizeAdapterGrid());
            mTouchHelperGrid.attachToRecyclerView(mBinding.recyclerSize);
        }
    }

    private void autoScroll() {
        mBinding.recyclerFolder.setOnTouchListener((v, event) -> {
            //folder auto scroll--------------------------------------------------------------------
            if (!isSizeStart && (isFolderDragSelectEnable || isDragging) && event.getRawY() > 2000) {
                mBinding.listScrollView.scrollBy(0, 1);
                mScrollEnable = true;
            } else if ((isFolderDragSelectEnable || isDragging) && event.getRawY() < 250) {
                mBinding.listScrollView.scrollBy(0, -1);
                mScrollEnable = true;
            } else if (!isSizeStart && (isFolderDragSelectEnable || isDragging) && event.getRawY() < 2000 && event.getRawY() > 250)
                mScrollEnable = false;
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
                RecyclerView.ViewHolder viewHolder2 = mBinding.recyclerSize.findViewHolderForLayoutPosition(1);
                if (viewHolder != null) viewHolder.itemView.callOnClick();

                if (mViewType == GRIDVIEW && viewHolder2 != null &&
                        ((mGridSelectedPosition2 == 1 && mGridLastSelectedState) || (mGridSelectedPosition2 == 3 && !mGridLastSelectedState)))
                    viewHolder2.itemView.callOnClick();

                isSizeDragSelectEnable = false;
                mGridLastSelectedState = false;
                mGridSelectedPosition1 = -1;
                mGridSelectedPosition2 = -1;
                mGridStartPosition = -1;
            }
            //--------------------------------------------------------------------------------------

            if (event.getAction() == MotionEvent.ACTION_UP && isFolderStart) {
                mBinding.recyclerSize.dispatchTouchEvent(event);
                isFolderStart = false;
                isSizeStart = false;
                isFolderDragSelectEnable = false;
                isSizeDragSelectEnable = false;
                mFolderSelectedState = false;
                mGridLastSelectedState = false;
                mFolderSelectedPosition1 = -1;
                mFolderSelectedPosition2 = -1;
                mGridSelectedPosition1 = -1;
                mGridSelectedPosition2 = -1;
                mFolderStartPosition = -1;
                mGridStartPosition = -1;
            }
            return false;
        });

        mBinding.recyclerSize.setOnTouchListener((v, event) -> {
            //size auto scroll----------------------------------------------------------------------
            if ((isSizeDragSelectEnable || isDragging) && event.getRawY() > 2000) {
                mBinding.listScrollView.scrollBy(0, 1);
                mScrollEnable = true;
            } else if (!isFolderStart && (isSizeDragSelectEnable || isDragging) && event.getRawY() < 250) {
                mBinding.listScrollView.scrollBy(0, -1);
                mScrollEnable = true;
            } else if (!isFolderStart && (isSizeDragSelectEnable || isDragging) && event.getRawY() < 2000 && event.getRawY() > 250)
                mScrollEnable = false;
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
                    mSelectListenerFolder.startDragSelection(mModel.getFolderAdapter().getItemCount() - 1);
                }
            }
            //size drag down------------------------------------------------------------------------
            else if (isSizeDragSelectEnable && !isDragging && event.getY() > 0
                    && isFolderDragSelectEnable && event.getAction() != MotionEvent.ACTION_UP && isSizeStart) {

                gridDragDown();

                if (mFolderSelectedPosition2 == -1)
                    folderHolderCallOnClick(mModel.getFolderAdapter().getItemCount() - 1);
                else if (mFolderSelectedState)
                    for (int i = mFolderSelectedPosition1; i < mModel.getFolderAdapter().getItemCount(); i++)
                        folderHolderCallOnClick(i);
                else
                    for (int i = mFolderSelectedPosition2 + 1; i < mModel.getFolderAdapter().getItemCount(); i++)
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
                mGridLastSelectedState = false;
                mFolderSelectedPosition1 = -1;
                mFolderSelectedPosition2 = -1;
                mGridSelectedPosition1 = -1;
                mGridSelectedPosition2 = -1;
                mFolderStartPosition = -1;
                mGridStartPosition = -1;
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

    private void folderToSizeDragDown() {
        int lastPosition = mModel.getFolderAdapter().getItemCount();
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
        for (int i = mFolderStartPosition + 1; i < mModel.getFolderAdapter().getItemCount(); i++)
            folderHolderCallOnClick(i);
        mSelectListenerFolder.startDragSelection(mFolderStartPosition);
    }

    private void gridDragUp(RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder viewHolder2) {
        if (mViewType == GRIDVIEW && viewHolder != null && viewHolder2 != null) {
            if (mGridStartPosition == 0 && mGridSelectedPosition2 >= 1
                    && (mGridLastSelectedState || mGridSelectedPosition2 >= 3))
                viewHolder2.itemView.callOnClick();
            else if (mGridStartPosition == 1) {
                if (mGridSelectedPosition1 != 0 && mGridSelectedPosition2 != 2)
                    viewHolder.itemView.callOnClick();
                else if (mGridSelectedPosition2 == 0 && !mGridLastSelectedState) {
                    viewHolder.itemView.callOnClick();
                }
            } else if (mGridStartPosition > 1) {
                if (mGridSelectedPosition1 != 0) viewHolder.itemView.callOnClick();
                else if (!mGridLastSelectedState) viewHolder.itemView.callOnClick();
            }
        }
    }

    private void gridDragDown() {
        if (mViewType == GRIDVIEW) {
            RecyclerView.ViewHolder viewHolder = mBinding.recyclerSize.findViewHolderForLayoutPosition(0);
            RecyclerView.ViewHolder viewHolder2 = mBinding.recyclerSize.findViewHolderForLayoutPosition(1);
            if (viewHolder != null && viewHolder2 != null)
                if (mGridStartPosition == 0) {
                    if ((mGridSelectedPosition2 == 1 && mGridLastSelectedState)
                            || (mGridSelectedPosition2 == 3 && !mGridLastSelectedState))
                        viewHolder2.itemView.callOnClick();
                } else if (mGridStartPosition == 1) {
                    if (mGridSelectedPosition2 == -1
                            || (mGridSelectedPosition2 == 0 && !mGridLastSelectedState)
                            || (mGridSelectedPosition2 == 3 && !mGridLastSelectedState))
                        viewHolder.itemView.callOnClick();
                } else if (mGridStartPosition > 1)
                    if (mGridSelectedPosition1 != 0 || !mGridLastSelectedState)
                        viewHolder.itemView.callOnClick();
        }
    }

    private void folderHolderCallOnClick(int i) {
        RecyclerView.ViewHolder viewHolder = mBinding.recyclerFolder.findViewHolderForLayoutPosition(i);
        if (viewHolder != null) viewHolder.itemView.callOnClick();
    }

    private void setLiveData() {
        //folder
        if (mFolderLive != null && mFolderLive.hasObservers())
            mFolderLive.removeObservers(getViewLifecycleOwner());

        mFolderLive = mModel.getFolderLive();
        mFolderLive.observe(getViewLifecycleOwner(), folderList -> {
            if (mSort == SORT_LATEST)
                folderList.sort((o1, o2) -> Long.compare(o2.getId(), o1.getId()));
            else if (mSort == SORT_LATEST_REVERSE)
                folderList.sort((o1, o2) -> Long.compare(o1.getId(), o2.getId()));

            int size = folderList.size();
            if (size % 4 == 1) {
                for (int i = 0; i < 3; i++) {
                    Folder dummy = new Folder(-1, "dummy", 0, 0, "");
                    folderList.add(dummy);
                }
            } else if (size % 4 == 2) {
                for (int i = 0; i < 2; i++) {
                    Folder dummy = new Folder(-1, "dummy", 0, 0, "");
                    folderList.add(dummy);
                }
            } else if (size % 4 == 3) {
                Folder dummy = new Folder(-1, "dummy", 0, 0, "");
                folderList.add(dummy);
            }

            mModel.getFolderAdapter().setItem(folderList);
            mModel.getFolderAdapter().setSort(mSort);

            if (folderList.size() == 0) mBinding.recyclerFolderLayout.setVisibility(View.GONE);
            else mBinding.recyclerFolderLayout.setVisibility(View.VISIBLE);
        });

        //size
        if (mSizeLive != null && mSizeLive.hasObservers())
            mSizeLive.removeObservers(getViewLifecycleOwner());

        mSizeLive = mModel.getSizeLive();
        mSizeLive.observe(getViewLifecycleOwner(), sizeList -> {
            if (sizeList.size() == 0) {
                mBinding.noData.setVisibility(View.VISIBLE);
                mBinding.recyclerSize.setVisibility(View.GONE);
            } else {
                mBinding.noData.setVisibility(View.GONE);
                mBinding.recyclerSize.setVisibility(View.VISIBLE);
            }

            if (mSort == SORT_LATEST)
                sizeList.sort((o1, o2) -> Integer.compare(o2.getId(), o1.getId()));
            else if (mSort == SORT_LATEST_REVERSE)
                sizeList.sort((o1, o2) -> Integer.compare(o1.getId(), o2.getId()));

            if (mModel.isFavoriteView()) {
                List<Size> favoriteList = new ArrayList<>();
                for (Size size : sizeList) if (size.isFavorite()) favoriteList.add(size);
                sizeList = favoriteList;
            }

            mModel.getSizeAdapterList().setItem(sizeList);
            mModel.getSizeAdapterList().setSort(mSort);

            mModel.getSizeAdapterGrid().setItem(sizeList);
            mModel.getSizeAdapterGrid().setSort(mSort);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mActionMode != null) mActionMode.finish();
    }

    //click&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&
    private void setClickListener() {
        goTopFabClick();

        folderToggleClick();

        popupMenuClick();

        mActivityFab = requireActivity().findViewById(R.id.activity_fab);
        fabClick();

        folderAdapterClick();

        mModel.getSizeAdapterList().setOnSizeAdapterListener(this);
        mModel.getSizeAdapterGrid().setOnSizeAdapterListener(this);
    }

    private void goTopFabClick() {
        mBinding.listFab.setOnClickListener(v -> mBinding.listScrollView.scrollTo(0, 0));
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
            SharedPreferences.Editor editor = mFolderPreference.edit();
            editor.putBoolean(FOLDER_TOGGLE, mFolderToggle);
            editor.apply();
        });

    }

    private void popupMenuClick() {
        mPopupMenuBinding.addFolder.setOnClickListener(v -> {
            showDialog(new AddFolderDialog(), "add");
            mPopupWindow.dismiss();
        });

        mPopupMenuBinding.sort.setOnClickListener(v -> {
            showDialog(SortDialog.getInstance(mSort), "sort");
            mPopupWindow.dismiss();
        });
    }

    private void fabClick() {
        mActivityFab.setOnClickListener(v -> {
            mActivityModel.setFolder(mModel.getThisFolder());
            Navigation.findNavController(requireActivity(), R.id.host_fragment).navigate(R.id.action_listFragment_to_inputOutputFragment);
        });
    }

    //folder click----------------------------------------------------------------------------------
    private void folderAdapterClick() {
        mModel.getFolderAdapter().setOnFolderAdapterListener(new FolderAdapterListener() {
            @Override
            public void onFolderItemViewClick(Folder folder, MaterialCheckBox checkBox, int position) {
                if (mActionMode == null) {
                    mActivityModel.setFolder(folder);
                    Navigation.findNavController(requireActivity(), R.id.host_fragment).navigate(R.id.action_listFragment_refresh);
                } else {
                    checkBox.setChecked(!checkBox.isChecked());
                    mModel.folderSelected(folder, checkBox.isChecked(), position);
                }
            }

            @Override
            public void onFolderItemViewLongClick(MaterialCardView cardView, int position) {
                if (mActionMode == null) {
                    mModel.selectedItemListInit();
                    ((AppCompatActivity) requireActivity()).startSupportActionMode(mActionModeCallback);
                }
                isFolderStart = true;
                isSizeStart = false;
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

    //size click------------------------------------------------------------------------------------
    @Override
    public void onSizeCardViewClick(Size size, MaterialCheckBox checkBox, int position) {
        if (mActionMode == null) {
            mActivityModel.setSize(size);
            Navigation.findNavController(requireActivity(), R.id.host_fragment).navigate(R.id.action_listFragment_to_inputOutputFragment);
        } else {
            checkBox.setChecked(!checkBox.isChecked());
            mModel.sizeSelected(size, checkBox.isChecked(), position);
        }
    }

    @Override
    public void onSizeCardViewLongClick(MaterialCardView cardView, int position) {
        if (mActionMode == null) {
            mModel.selectedItemListInit();
            ((AppCompatActivity) requireActivity()).startSupportActionMode(mActionModeCallback);
        }
        isFolderStart = false;
        isSizeStart = true;
        mSelectListenerSize.startDragSelection(position);
    }

    @Override
    public void onSizeDragHandleTouch(RecyclerView.ViewHolder viewHolder) {
        if (mActionMode == null && !isDragging) {
            isDragging = true;
            if (mViewType == LISTVIEW) mTouchHelperList.startDrag(viewHolder);
            else mTouchHelperGrid.startDrag(viewHolder);
        } else if (isDragging) isDragging = false;
    }
    //&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&

    //menu------------------------------------------------------------------------------------------
    @Override
    public void onPrepareOptionsMenu(@NonNull @NotNull Menu menu) {
        MenuItem viewTypeIcon = menu.getItem(1);
        MenuItem favoriteIcon = menu.getItem(0);
        if (mViewType == LISTVIEW) viewTypeIcon.setIcon(R.drawable.icon_list);
        else viewTypeIcon.setIcon(R.drawable.icon_grid);

        if (mModel.isFavoriteView()) favoriteIcon.setIcon(R.drawable.icon_favorite);
        else favoriteIcon.setIcon(R.drawable.icon_favorite_border);
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
                mModel.getSizeAdapterList().setSelectedPosition(new HashSet<>());
                mModel.getSizeAdapterList().setActionModeState(0);
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
            if (mModel.isFavoriteView()) item.setIcon(R.drawable.icon_favorite);
            else item.setIcon(R.drawable.icon_favorite_border);
            setLiveData();
            return true;
        }
        return false;
    }

    //dialog click----------------------------------------------------------------------------------
    private void showDialog(DialogFragment dialog, String tag) {
        dialog.setTargetFragment(this, 0);
        dialog.show(getParentFragmentManager(), tag);
    }

    @Override//add folder confirm click
    public void addFolderConfirmClick(String folderName) {
        mModel.addFolder(folderName);
    }

    @Override//category node add folder icon click
    public void treeViewCategoryAddClick(TreeNode node, TreeHolderCategory.CategoryTreeHolder value) {
        mModel.nodeAddClick(node, value);
        showDialog(new TreeAddFolderDialog(), "tree add");
    }

    @Override//folder node add folder icon click
    public void treeViewFolderAddClick(TreeNode node, TreeHolderFolder.FolderTreeHolder value) {
        mModel.nodeAddClick(node, value);
        showDialog(new TreeAddFolderDialog(), "tree add");
    }

    @Override//node add folder confirm click
    public void treeAddFolderConfirmClick(String folderName) {
        TreeNode oldNode = mModel.getAddNode();

        if (mModel.getCategoryAddValue() != null) {//category node
            TreeHolderCategory.CategoryTreeHolder oldViewHolder = mModel.getCategoryAddValue();

            int largestOrder = mModel.getFolderLargestOrder() + 1;
            Folder folder = new Folder(mModel.getCurrentTime(), folderName, oldViewHolder.category.getId(), largestOrder, "0");
            mModel.insertFolder(folder);

            //find node(recreate)
            List<TreeNode> categoryNodeList = mModel.getTreeNodeRoot().getChildren();
            for (TreeNode newNode : categoryNodeList) {
                TreeHolderCategory.CategoryTreeHolder newViewHolder = (TreeHolderCategory.CategoryTreeHolder) newNode.getValue();
                if (oldViewHolder.category.getId() == newViewHolder.category.getId()) {
                    oldNode = newNode;
                    break;
                }
            }

            TreeNode treeNode = new TreeNode(new TreeHolderFolder.FolderTreeHolder(folder, mModel.getAllFolder(),
                    40, this, mModel.getSelectedItemFolder(), mModel.getSelectedItemSize())).setViewHolder(new TreeHolderFolder(requireContext()));
            oldNode.getViewHolder().getTreeView().addNode(oldNode, treeNode);
            oldNode.getViewHolder().getTreeView().expandNode(oldNode);
            ((TreeHolderCategory) oldNode.getViewHolder()).setIconClickable();

            TreeHolderCategory.CategoryTreeHolder newViewHolder = (TreeHolderCategory.CategoryTreeHolder) oldNode.getValue();
            int amount = Integer.parseInt(newViewHolder.category.getItemAmount()) + 1;
            newViewHolder.category.setItemAmount(String.valueOf(amount));
            mModel.updateCategory(newViewHolder.category);

            TreeHolderCategory newViewHolder2 = (TreeHolderCategory) oldNode.getViewHolder();
            String string = String.valueOf(Integer.parseInt(newViewHolder2.getBinding().amount.getText().toString()) + 1);
            newViewHolder2.getBinding().amount.setText(string);
        } else {//folder node
            TreeHolderFolder.FolderTreeHolder oldViewHolder = mModel.getFolderAddValue();

            int largestOrder = mModel.getFolderLargestOrder() + 1;
            Folder folder = new Folder(mModel.getCurrentTime(), folderName, oldViewHolder.folder.getId(), largestOrder, "0");
            mModel.insertFolder(folder);

            //find node(recreate)
            for (TreeNode folderNode : getAllFolderNode()) {
                TreeHolderFolder.FolderTreeHolder newViewHolder = (TreeHolderFolder.FolderTreeHolder) folderNode.getValue();
                if (oldViewHolder.folder.getId() == newViewHolder.folder.getId()) {
                    oldNode = folderNode;
                    break;
                }
            }

            int margin = (int) getResources().getDimension(R.dimen._8sdp);
            TreeNode treeNode = new TreeNode(new TreeHolderFolder.FolderTreeHolder(folder, mModel.getAllFolder(),
                    oldViewHolder.margin + margin, this, mModel.getSelectedItemFolder(), mModel.getSelectedItemSize())).setViewHolder(new TreeHolderFolder(requireContext()));
            oldNode.getViewHolder().getTreeView().addNode(oldNode, treeNode);
            oldNode.getViewHolder().getTreeView().expandNode(oldNode);
            ((TreeHolderFolder) oldNode.getViewHolder()).setIconClickable();

            TreeHolderFolder.FolderTreeHolder newViewHolder = (TreeHolderFolder.FolderTreeHolder) oldNode.getValue();
            int amount = Integer.parseInt(newViewHolder.folder.getItemAmount()) + 1;
            newViewHolder.folder.setItemAmount(String.valueOf(amount));
            mModel.updateFolder(newViewHolder.folder);

            TreeHolderFolder newViewHolder2 = (TreeHolderFolder) oldNode.getViewHolder();
            String string = String.valueOf(Integer.parseInt(newViewHolder2.getBinding().amount.getText().toString()) + 1);
            newViewHolder2.getBinding().amount.setText(string);
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

    private void listingFolderNode(List<TreeNode> folderNodeList, List<TreeNode> newFolderList) {
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
                showDialog(ItemMoveDialog.getInstance(mModel.getSelectedAmount().getValue(), categoryHolder.category.getId()), "move");
                //current position is folder
            else if (mModel.getThisFolder() != null)
                showDialog(ItemMoveDialog.getInstance(mModel.getSelectedAmount().getValue(), categoryHolder.category.getId()), "move");
        } else if (value instanceof TreeHolderFolder.FolderTreeHolder && !((TreeHolderFolder) node.getViewHolder()).isSelected() && mModel.getSelectedAmount().getValue() != null) {
            TreeHolderFolder.FolderTreeHolder folderHolder = (TreeHolderFolder.FolderTreeHolder) node.getValue();
            //current position is category
            if (mModel.getThisFolder() == null)
                showDialog(ItemMoveDialog.getInstance(mModel.getSelectedAmount().getValue(), folderHolder.folder.getId()), "move");
                //current position is folder & clicked folder is not this folder
            else if (mModel.getThisFolder() != null && folderHolder.folder.getId() != mModel.getThisFolder().getId())
                showDialog(ItemMoveDialog.getInstance(mModel.getSelectedAmount().getValue(), folderHolder.folder.getId()), "move");
        }
    }

    @Override//item move confirm click
    public void itemMoveConfirmClick(long folderId) {
        mModel.selectedItemMove(folderId);
        TreeViewDialog treeViewDialog = ((TreeViewDialog) getParentFragmentManager().findFragmentByTag("tree"));
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
        mModel.updateFolder(folder);
        mActionMode.finish();
    }

    @Override//sort confirm click
    public void sortConfirmClick(int sort) {
        if (sort == SORT_CUSTOM) mSort = SORT_CUSTOM;
        else if (sort == SORT_LATEST) mSort = SORT_LATEST;
        else mSort = SORT_LATEST_REVERSE;
        mModel.setSort(mSort);
        setLiveData();
        SharedPreferences.Editor editor = mSortPreference.edit();
        editor.putInt(SORT, mSort);
        editor.apply();
    }


    @Override
    public void onSaveInstanceState(@NonNull @NotNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("action mode", mActionModeOn);
    }
}