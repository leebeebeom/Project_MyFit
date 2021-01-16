package com.example.project_myfit.ui.main.listfragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_myfit.MainActivityViewModel;
import com.example.project_myfit.MyFitConstant;
import com.example.project_myfit.R;
import com.example.project_myfit.databinding.ActionModeTitleBinding;
import com.example.project_myfit.databinding.FragmentListBinding;
import com.example.project_myfit.databinding.ItemDialogEditTextBinding;
import com.example.project_myfit.ui.main.database.Category;
import com.example.project_myfit.ui.main.listfragment.adapter.folderdapter.FolderAdapter;
import com.example.project_myfit.ui.main.listfragment.adapter.folderdapter.FolderAdapterListener;
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
import com.example.project_myfit.ui.main.listfragment.treeview.TreeViewAddClickListener;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textview.MaterialTextView;
import com.michaelflisar.dragselectrecyclerview.DragSelectTouchListener;
import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.view.AndroidTreeView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.example.project_myfit.MyFitConstant.ACTION_MODE_NONE;
import static com.example.project_myfit.MyFitConstant.ACTION_MODE_OFF;
import static com.example.project_myfit.MyFitConstant.ACTION_MODE_ON;
import static com.example.project_myfit.MyFitConstant.GRIDVIEW;
import static com.example.project_myfit.MyFitConstant.LISTVIEW;
import static com.example.project_myfit.MyFitConstant.VIEW_TYPE;

@SuppressLint("ClickableViewAccessibility")
public class ListFragment extends Fragment implements SizeAdapterListener, TreeViewAddClickListener {
    private MainActivityViewModel mActivityModel;
    private ListViewModel mModel;
    private FragmentListBinding mBinding;
    private ItemDialogEditTextBinding mDialogEditTextBinding;
    private ActionModeTitleBinding mActionModeTitleBinding;
    private FolderAdapter mFolderAdapter;
    private SizeAdapterList mSizeAdapterList;
    private SizeAdapterGrid mSizeAdapterGrid;
    private SharedPreferences mPreference;
    private int mViewType;
    private ItemTouchHelper mTouchHelperList, mTouchHelperGrid, mTouchHelperFolder;
    private FloatingActionButton mActivityFab, mActivityFabAdd, mActivityFabAddFolder;
    private Animation rotateOpen, rotateClose, fromBottom, toBottom;
    private boolean isFabOpened, isFolderDragSelectEnable, isSizeDragSelectEnable, mScrollEnable;
    private List<Size> mSelectedItemSize;
    private List<Folder> mSelectedItemFolder;
    private ActionMode mActionMode;
    private MenuItem mEditMenu;
    private long mFolderId;
    private ActionMode.Callback mActionModeCallback;
    private DragSelectTouchListener mSelectListenerSize, mSelectListenerFolder;
    private AlertDialog mTreeViewDialog;

    @Override

    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mModel = new ViewModelProvider(this).get(ListViewModel.class);
        mActivityModel = new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);

        if (mModel.getThisFolder() == null) {
            Folder thisFolder = mActivityModel.getFolder() != null ? mActivityModel.getFolder() : null;
            mModel.setThisFolder(thisFolder);
        }

        mFolderId = mModel.getThisFolder() == null ? mActivityModel.getCategory().getId() :
                mModel.getThisFolder().getId();

        mPreference = requireContext().getSharedPreferences(VIEW_TYPE, Context.MODE_PRIVATE);
        mViewType = mPreference.getInt(VIEW_TYPE, LISTVIEW);

        dragSelectListenerInit();

        mFolderAdapter = new FolderAdapter(mModel);
        mSizeAdapterList = new SizeAdapterList(mModel);
        mSizeAdapterGrid = new SizeAdapterGrid(mModel);

        mActionModeCallback = actionModeCallBackInit();

        setHasOptionsMenu(true);
    }

    private ActionMode.Callback actionModeCallBackInit() {
        return new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                mode.getMenuInflater().inflate(R.menu.list_action_mode, menu);
                mode.setCustomView(mActionModeTitleBinding.getRoot());

                mSelectedItemSize = new ArrayList<>();
                mSelectedItemFolder = new ArrayList<>();

                mFolderAdapter.setActionModeState(ACTION_MODE_ON);
                if (mViewType == LISTVIEW)
                    mSizeAdapterList.setActionModeState(ACTION_MODE_ON);
                else mSizeAdapterGrid.setActionModeState(ACTION_MODE_ON);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                mActionMode = mode;
                selectAllClick();
                mEditMenu = menu.getItem(0);
                return true;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                //TODO 이동 만들기
                if (item.getItemId() == R.id.list_action_mode_del)
                    showDeleteDialog(mode);
                else if (item.getItemId() == R.id.list_action_mode_move)
                    showTreeDialog();
                else showEditDialog(mode);
                return true;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                mActionMode = null;
                mActionModeTitleBinding.actionModeTitle.setText("");
                mActionModeTitleBinding.actionModeSelectAll.setChecked(false);
                ((ViewGroup) mActionModeTitleBinding.getRoot().getParent()).removeAllViews();

                mFolderAdapter.setActionModeState(ACTION_MODE_OFF);
                if (mViewType == LISTVIEW)
                    mSizeAdapterList.setActionModeState(MyFitConstant.ACTION_MODE_OFF);
                else mSizeAdapterGrid.setActionModeState(MyFitConstant.ACTION_MODE_OFF);
            }
        };
    }

    private void selectAllClick() {
        mActionModeTitleBinding.actionModeSelectAll.setOnClickListener(v -> {
            if (mActionModeTitleBinding.actionModeSelectAll.isChecked()) {
                mSelectedItemFolder.clear();
                mSelectedItemSize.clear();
                mFolderAdapter.selectAll();
                mSelectedItemFolder.addAll(mFolderAdapter.getCurrentList());
                if (mViewType == LISTVIEW) {
                    mSizeAdapterList.selectAll();
                    mSelectedItemSize.addAll(mSizeAdapterList.getCurrentList());
                } else {
                    mSizeAdapterGrid.selectAll();
                    mSelectedItemSize.addAll(mSizeAdapterGrid.getCurrentList());
                }
            } else {
                mFolderAdapter.deselectAll();
                mSelectedItemFolder.clear();
                mSelectedItemSize.clear();
                if (mViewType == LISTVIEW) mSizeAdapterList.deselectAll();
                else mSizeAdapterGrid.deselectAll();
            }
            setActionModeTitle();
        });
    }

    private void setActionModeTitle() {
        String title = getSelectedAmount() + getString(R.string.item_selected);
        mActionModeTitleBinding.actionModeTitle.setText(title);
    }

    private int getSelectedAmount() {
        return mSelectedItemFolder.size() + mSelectedItemSize.size();
    }

    private void dragSelectListenerInit() {
        DragSelectTouchListener.OnAdvancedDragSelectListener dragSelectListenerSize = new DragSelectTouchListener.OnAdvancedDragSelectListener() {
            @Override
            public void onSelectionStarted(int i) {
                isSizeDragSelectEnable = true;
                mBinding.listScrollView.setScrollable(false);
            }

            @Override
            public void onSelectionFinished(int i) {
                isSizeDragSelectEnable = false;
                mBinding.listScrollView.setScrollable(true);
            }

            @Override
            public void onSelectChange(int i, int i1, boolean b) {
                cardViewCallOnClick(i, i1);
            }
        };
        DragSelectTouchListener.OnAdvancedDragSelectListener dragSelectListenerFolder = new DragSelectTouchListener.OnAdvancedDragSelectListener() {
            @Override
            public void onSelectionStarted(int i) {
                isFolderDragSelectEnable = true;
                mBinding.listScrollView.setScrollable(false);
            }

            @Override
            public void onSelectionFinished(int i) {
                isFolderDragSelectEnable = false;
                mBinding.listScrollView.setScrollable(true);
            }

            @Override
            public void onSelectChange(int i, int i1, boolean b) {
                for (int j = i; j <= i1; j++) {
                    RecyclerView.ViewHolder viewHolder = mBinding.recyclerFolder.findViewHolderForLayoutPosition(j);
                    if (viewHolder != null)
                        viewHolder.itemView.findViewById(R.id.folder_card_view).callOnClick();
                }
            }
        };
        mSelectListenerSize = new DragSelectTouchListener().withSelectListener(dragSelectListenerSize);
        mSelectListenerFolder = new DragSelectTouchListener().withSelectListener(dragSelectListenerFolder);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = FragmentListBinding.inflate(inflater);
        mBinding.setCategory(mActivityModel.getCategory());
        setActionBarTitle();

        if (mModel.getThisFolder() != null) {
            setNavigation(getFolderHistory());
            mActivityModel.setFolder(null);
        }

        mDialogEditTextBinding = ItemDialogEditTextBinding.inflate(inflater);
        mActionModeTitleBinding = ActionModeTitleBinding.inflate(inflater);

        return mBinding.getRoot();
    }

    private void setActionBarTitle() {
        ActionBar actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
        if (actionBar != null)
            if (mModel.getThisFolder() == null)
                actionBar.setTitle(mActivityModel.getCategory().getCategory());
            else
                actionBar.setTitle(mModel.getThisFolder().getFolderName());
    }

    private void setNavigation(List<Folder> folderHistory2) {
        for (Folder f : folderHistory2) {
            ImageView arrowIcon = getArrowIcon();
            MaterialTextView folderNameView = getFolderNameView();

            folderNameView.setText(f.getFolderName());
            folderNameView.setOnClickListener(v -> {
                mActivityModel.setFolder(f);
                Navigation.findNavController(requireActivity(), R.id.host_fragment).navigate(R.id.action_listFragment_refresh);
            });

            mBinding.listNavigation.addView(arrowIcon);
            mBinding.listNavigation.addView(folderNameView);
        }
    }

    @NotNull
    private List<Folder> getFolderHistory() {
        List<Folder> allFolderList = mModel.getAllFolder();
        List<Folder> folderHistory = new ArrayList<>();
        folderHistory.add(mModel.getThisFolder());
        List<Folder> folderHistory2 = getFolderHistory(allFolderList, folderHistory, mModel.getThisFolder());
        Collections.reverse(folderHistory2);
        return folderHistory2;
    }

    private List<Folder> getFolderHistory(List<Folder> allFolderList, List<Folder> folderHistory, Folder thisFolder) {
        for (Folder parentFolder : allFolderList) {
            if (parentFolder.getId() == thisFolder.getFolderId()) {
                folderHistory.add(parentFolder);
                getFolderHistory(allFolderList, folderHistory, parentFolder);
                break;
            }
        }
        return folderHistory;
    }

    @NotNull
    private ImageView getArrowIcon() {
        Drawable arrowIcon = ResourcesCompat.getDrawable(getResources(), R.drawable.icon_arrow_forward, requireActivity().getTheme());
        ImageView imageView = new ImageView(requireContext());
        imageView.setImageDrawable(arrowIcon);
        imageView.setAlpha(0.7f);
        return imageView;
    }

    @NotNull
    private MaterialTextView getFolderNameView() {
        MaterialTextView textView = new MaterialTextView(requireContext());
        textView.setAlpha(0.8f);
        textView.setTextSize(18f);
        return textView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        recyclerViewInit();

        autoScroll();

        itemTouchHelperInit();

        fabInit();

        if (mViewType == LISTVIEW) setListLayout();
        else setGridLayout();

        setClickListener();

        //folder Renew
        mModel.getFolderListLive(mFolderId).observe(getViewLifecycleOwner(), folderList -> {
            mFolderAdapter.setItem(folderList);
            if (folderList.size() == 0) mBinding.recyclerFolder.setVisibility(View.GONE);
            else mBinding.recyclerFolder.setVisibility(View.VISIBLE);
        });
        //size Renew
        mModel.getAllSize(mFolderId).observe(getViewLifecycleOwner(), sizeList -> {
            mSizeAdapterList.setItem(sizeList);
            mSizeAdapterGrid.setItem(sizeList);
        });
    }

    private void recyclerViewInit() {
        mBinding.recyclerFolder.setAdapter(mFolderAdapter);
        mBinding.recyclerFolder.addOnItemTouchListener(mSelectListenerFolder);
        mBinding.recyclerSize.addOnItemTouchListener(mSelectListenerSize);
    }

    private void autoScroll() {
        mBinding.recyclerSize.setOnTouchListener((v, event) -> {
            if (isSizeDragSelectEnable && event.getRawY() > 2000) {
                mBinding.listScrollView.scrollBy(0, 1);
                mScrollEnable = true;
            } else if (isSizeDragSelectEnable && event.getRawY() < 250) {
                mBinding.listScrollView.scrollBy(0, -1);
                mScrollEnable = true;
            } else mScrollEnable = false;
            return false;
        });

        mBinding.recyclerFolder.setOnTouchListener((v, event) -> {
            if (isFolderDragSelectEnable && event.getRawY() > 2000) {
                mBinding.listScrollView.scrollBy(0, 1);
                mScrollEnable = true;
            } else if (isFolderDragSelectEnable && event.getRawY() < 250) {
                mBinding.listScrollView.scrollBy(0, -1);
                mScrollEnable = true;
            } else if (event.getAction() == MotionEvent.ACTION_UP) mScrollEnable = false;
            else mScrollEnable = false;
            return false;
        });

        mBinding.listScrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if ((isSizeDragSelectEnable || isFolderDragSelectEnable) && mScrollEnable && scrollY > oldScrollY)
                v.postDelayed(() -> v.scrollBy(0, 1), 50);
            else if ((isSizeDragSelectEnable || isFolderDragSelectEnable) && mScrollEnable && scrollY < oldScrollY)
                v.postDelayed(() -> v.scrollBy(0, -1), 50);
        });
    }

    private void itemTouchHelperInit() {
        mTouchHelperFolder = new ItemTouchHelper(new FolderDragCallBack(mFolderAdapter));
        mTouchHelperFolder.attachToRecyclerView(mBinding.recyclerFolder);
        mTouchHelperList = new ItemTouchHelper(new SizeDragCallBackList(mSizeAdapterList));
        mTouchHelperGrid = new ItemTouchHelper(new SizeDragCallBackGrid(mSizeAdapterGrid));
    }

    private void fabInit() {
        mActivityFab = requireActivity().findViewById(R.id.activity_fab);
        mActivityFabAdd = requireActivity().findViewById(R.id.activity_fab_add);
        mActivityFabAddFolder = requireActivity().findViewById(R.id.activity_fab_add_folder);
        rotateOpen = AnimationUtils.loadAnimation(requireContext(), R.anim.rotate_open);
        rotateClose = AnimationUtils.loadAnimation(requireContext(), R.anim.rotate_close);
        fromBottom = AnimationUtils.loadAnimation(requireContext(), R.anim.folder_from_bottom);
        toBottom = AnimationUtils.loadAnimation(requireContext(), R.anim.folder_to_bottom);
    }

    private void setListLayout() {
        int paddingTop = (int) getResources().getDimension(R.dimen._8sdp);
        int paddingBottom = (int) getResources().getDimension(R.dimen._60sdp);
        mBinding.recyclerSize.setPadding(0, paddingTop, 0, paddingBottom);
        mBinding.recyclerSize.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false));
        mBinding.recyclerSize.setAdapter(mSizeAdapterList);
        mTouchHelperList.attachToRecyclerView(mBinding.recyclerSize);
    }

    private void setGridLayout() {
        int padding = (int) getResources().getDimension(R.dimen._8sdp);
        int paddingBottom = (int) getResources().getDimension(R.dimen._60sdp);
        mBinding.recyclerSize.setPadding(padding, padding, padding, paddingBottom);
        mBinding.recyclerSize.setLayoutManager(new GridLayoutManager(requireContext(), 2, RecyclerView.VERTICAL, false));
        mBinding.recyclerSize.setAdapter(mSizeAdapterGrid);
        mTouchHelperGrid.attachToRecyclerView(mBinding.recyclerSize);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (isFabOpened) {
            isFabOpened = false;
            setVisibility(isFabOpened);
            setAnimation(isFabOpened);
            setClickable(isFabOpened);
        }
        if (mActionMode != null) mActionMode.finish();
    }

    //click&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&
    private void setClickListener() {
        categoryTextClick();

        homeIconClick();

        fabClick();
        addFabClick();
        addFolderFabClick();

        folderAdapterClick();
        mSizeAdapterList.setOnSizeAdapterListener(this);
        mSizeAdapterGrid.setOnSizeAdapterListener(this);
    }

    private void categoryTextClick() {
        mBinding.listTextCategory.setOnClickListener(v -> Navigation.findNavController(requireActivity(), R.id.host_fragment).navigate(R.id.action_listFragment_refresh));
    }

    private void homeIconClick() {
        mBinding.listIconHome.setOnClickListener(v -> Navigation.findNavController(requireActivity(), R.id.host_fragment).navigate(R.id.action_listFragment_to_mainFragment));
    }

    //fab click-------------------------------------------------------------------------------------
    private void fabClick() {
        mActivityFab.setOnClickListener(v -> {
            isFabOpened = !isFabOpened;
            setVisibility(isFabOpened);
            setAnimation(isFabOpened);
            setClickable(isFabOpened);
        });
    }

    private void addFabClick() {
        mActivityFabAdd.setOnClickListener(v -> {
            mActivityFab.callOnClick();
            mActivityModel.setFolder(mModel.getThisFolder());
            mActivityModel.setSize(null);
            Navigation.findNavController(requireActivity(), R.id.host_fragment).navigate(R.id.action_listFragment_to_inputOutputFragment);
        });
    }

    private void addFolderFabClick() {
        mActivityFabAddFolder.setOnClickListener(v -> {
            mActivityFab.callOnClick();
            showAddFolderDialog(mFolderId);
        });
    }

    private void setVisibility(boolean isFabOpened) {
        if (isFabOpened) {
            mActivityFabAdd.setVisibility(View.VISIBLE);
            mActivityFabAddFolder.setVisibility(View.VISIBLE);
        } else {
            mActivityFabAdd.setVisibility(View.INVISIBLE);
            mActivityFabAddFolder.setVisibility(View.INVISIBLE);
        }
    }

    private void setAnimation(boolean isFabOpened) {
        if (isFabOpened) {
            mActivityFab.startAnimation(rotateOpen);
            mActivityFabAdd.startAnimation(fromBottom);
            mActivityFabAddFolder.startAnimation(fromBottom);
        } else {
            mActivityFab.startAnimation(rotateClose);
            mActivityFabAdd.startAnimation(toBottom);
            mActivityFabAddFolder.startAnimation(toBottom);
        }
    }

    private void setClickable(boolean isFabOpened) {
        mActivityFabAdd.setClickable(isFabOpened);
        mActivityFabAddFolder.setClickable(isFabOpened);
    }
    //----------------------------------------------------------------------------------------------

    //folder click---------------------------------------------------------------------------------
    private void folderAdapterClick() {
        mFolderAdapter.setOnFolderAdapterListener(new FolderAdapterListener() {
            @Override
            public void onFolderCardViewClick(Folder folder, MaterialCheckBox checkBox, int position) {
                if (mFolderAdapter.getActionModeState() == ACTION_MODE_NONE || mFolderAdapter.getActionModeState() == ACTION_MODE_OFF) {
                    mActivityModel.setFolder(folder);
                    Navigation.findNavController(requireActivity(), R.id.host_fragment).navigate(R.id.action_listFragment_refresh);
                } else actionModeOnClickFolder(folder, position, checkBox);
            }

            @Override
            public void onFolderCardViewLongClick(Folder folder, RecyclerView.ViewHolder holder, MaterialCheckBox checkBox, int position) {
                if (mFolderAdapter.getActionModeState() == ACTION_MODE_NONE || mFolderAdapter.getActionModeState() == ACTION_MODE_OFF)
                    ((AppCompatActivity) requireActivity()).startSupportActionMode(mActionModeCallback);
                holder.itemView.findViewById(R.id.folder_card_view).callOnClick();
                mSelectListenerFolder.startDragSelection(position);
            }

            @Override
            public void onFolderDragHandTouch(RecyclerView.ViewHolder holder) {
                mTouchHelperFolder.startDrag(holder);
            }
        });
    }

    private void actionModeOnClickFolder(Folder folder, int position, MaterialCheckBox checkBox) {
        checkBox.setChecked(!checkBox.isChecked());
        if (checkBox.isChecked()) mSelectedItemFolder.add(folder);
        else mSelectedItemFolder.remove(folder);
        mFolderAdapter.setSelectedPosition(position);
        setActionModeTitle();
        actionModeSelectAllCheck();
        mEditMenu.setVisible(mSelectedItemSize.size() == 0 && mSelectedItemFolder.size() == 1);
    }

    private void actionModeSelectAllCheck() {
        if (mViewType == LISTVIEW)
            mActionModeTitleBinding.actionModeSelectAll.setChecked(mSizeAdapterList.getCurrentList().size() == mSelectedItemSize.size()
                    && mFolderAdapter.getCurrentList().size() == mSelectedItemFolder.size());
        else
            mActionModeTitleBinding.actionModeSelectAll.setChecked(mSizeAdapterGrid.getCurrentList().size() == mSelectedItemSize.size()
                    && mFolderAdapter.getCurrentList().size() == mSelectedItemFolder.size());
    }
    //----------------------------------------------------------------------------------------------

    //size click------------------------------------------------------------------------------------
    @Override
    public void onListCardViewClick(Size size, MaterialCheckBox checkBox, int position) {
        if (mViewType == LISTVIEW && (mSizeAdapterList.getActionModeState() == ACTION_MODE_NONE || mSizeAdapterList.getActionModeState() == ACTION_MODE_OFF) ||
                mViewType == GRIDVIEW && (mSizeAdapterGrid.getActionModeState() == ACTION_MODE_NONE || mSizeAdapterGrid.getActionModeState() == ACTION_MODE_OFF)) {
            mActivityModel.setSize(size);
            Navigation.findNavController(requireActivity(), R.id.host_fragment).navigate(R.id.action_listFragment_to_inputOutputFragment);
        } else actionModeOnClickSize(size, checkBox, position, false);
    }

    private void actionModeOnClickSize(Size size, MaterialCheckBox checkBox, int position, boolean areYouCheckBox) {
        if (!areYouCheckBox) checkBox.setChecked(!checkBox.isChecked());
        if (checkBox.isChecked()) mSelectedItemSize.add(size);
        else mSelectedItemSize.remove(size);
        if (mViewType == LISTVIEW) mSizeAdapterList.setSelectedPosition(position);
        else mSizeAdapterGrid.setSelectedPosition(position);
        setActionModeTitle();
        actionModeSelectAllCheck();
        mEditMenu.setVisible(mSelectedItemSize.size() == 0 && mSelectedItemFolder.size() == 1);
    }

    @Override
    public void onListCardViewLongClick(Size size, MaterialCheckBox checkBox, int position) {
        if (mViewType == LISTVIEW && (mSizeAdapterList.getActionModeState() == ACTION_MODE_NONE || mSizeAdapterList.getActionModeState() == ACTION_MODE_OFF) ||
                mViewType == GRIDVIEW && (mSizeAdapterGrid.getActionModeState() == ACTION_MODE_NONE || mSizeAdapterGrid.getActionModeState() == ACTION_MODE_OFF))
            ((AppCompatActivity) requireActivity()).startSupportActionMode(mActionModeCallback);
        cardViewCallOnClick(position);
        mSelectListenerSize.startDragSelection(position);
    }

    private void cardViewCallOnClick(int position) {
        RecyclerView.ViewHolder viewHolder = mBinding.recyclerSize.findViewHolderForLayoutPosition(position);
        if (viewHolder != null) {
            if (mViewType == LISTVIEW)
                viewHolder.itemView.findViewById(R.id.list_card_view).callOnClick();
            else viewHolder.itemView.findViewById(R.id.grid_card_view).callOnClick();
        }
    }

    private void cardViewCallOnClick(int position, int position2) {
        for (int i = position; i <= position2; i++) {
            RecyclerView.ViewHolder viewHolder = mBinding.recyclerSize.findViewHolderForLayoutPosition(i);
            if (viewHolder != null) {
                if (mViewType == LISTVIEW)
                    viewHolder.itemView.findViewById(R.id.list_card_view).callOnClick();
                else viewHolder.itemView.findViewById(R.id.grid_card_view).callOnClick();
            }
        }
    }

    @Override
    public void onListDragHandTouch(RecyclerView.ViewHolder viewHolder) {
        if ((mSizeAdapterList.getActionModeState() == ACTION_MODE_NONE || mSizeAdapterList.getActionModeState() == ACTION_MODE_OFF) && mViewType == LISTVIEW)
            mTouchHelperList.startDrag(viewHolder);
        else if ((mSizeAdapterGrid.getActionModeState() == ACTION_MODE_NONE || mSizeAdapterGrid.getActionModeState() == ACTION_MODE_OFF) && mViewType == GRIDVIEW)
            mTouchHelperGrid.startDrag(viewHolder);
    }

    @Override
    public void onListCheckBoxClick(Size size, MaterialCheckBox checkBox, int position) {
        actionModeOnClickSize(size, checkBox, position, true);
    }

    @Override
    public void onListCheckBoxLongCLick(int position) {
        cardViewCallOnClick(position);
        mSelectListenerSize.startDragSelection(position);
    }

    @Override
    public void onCardViewTouch(int position) {
        if (isFolderDragSelectEnable) {
            mSelectListenerSize.startDragSelection(position);
            cardViewCallOnClick(position);
        }
    }
    //&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&

    //dialog----------------------------------------------------------------------------------------
    @SuppressLint("ShowToast")
    private void showAddFolderDialog(long folderId) {
        setDialogEditText("");
        MaterialAlertDialogBuilder builder = getDialogBuilder();
        builder.setTitle("폴더 생성")
                .setPositiveButton("확인", (dialog, which) -> {
                    int largestOrder = mModel.getFolderLargestOrder();
                    largestOrder++;
                    mModel.insertFolder(new Folder(mModel.getCurrentTime(), String.valueOf(mDialogEditTextBinding.editTextDialog.getText()),
                            folderId,
                            largestOrder,
                            "0"));
                });
        builder.show();
    }

    private void showAddFolderDialog(TreeNode node, long folderId) {
        setDialogEditText("");
        MaterialAlertDialogBuilder builder = getDialogBuilder();
        builder.setTitle("폴더 생성")
                .setPositiveButton("확인", (dialog, which) -> {
                    int largestOrder = mModel.getFolderLargestOrder();
                    largestOrder++;
                    Folder folder = new Folder(mModel.getCurrentTime(), String.valueOf(mDialogEditTextBinding.editTextDialog.getText()),
                            folderId,
                            largestOrder,
                            "0");
                    mModel.insertFolder(folder);
                    node.addChild(new TreeNode(new TreeHolderFolder.IconTreeHolder(folder, mModel.getAllFolder(), 0, this))).setViewHolder(new TreeHolderFolder(requireContext()));
                    //TODO 안됨 씨발
                });
        builder.show();
    }

    private void showDeleteDialog(ActionMode mode) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext(), R.style.myAlertDialog)
                .setTitle("확인")
                .setMessage(getSelectedAmount() + "개의 아이템을 휴지통으로 이동하시겠습니까?")
                .setNegativeButton("취소", null)
                .setPositiveButton("확인", (dialog, which) -> {
                    mModel.deleteFolder(mSelectedItemFolder);
                    mModel.deleteSize(mSelectedItemSize);
                    mode.finish();
                });
        builder.show();
    }

    private void showEditDialog(ActionMode mode) {
        Folder folder = mSelectedItemFolder.get(0);
        setDialogEditText(folder.getFolderName());
        getDialogBuilder()
                .setTitle("폴더 이름 변경")
                .setPositiveButton("확인", (dialog, which) -> {
                    mode.finish();
                    folder.setFolderName(String.valueOf(mDialogEditTextBinding.editTextDialog.getText()));
                    mModel.updateFolder(folder);
                })
                .show();
    }

    private void showTreeDialog() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext(), R.style.myAlertDialog)
                .setMessage(mActivityModel.getCategory().getParentCategory())
                .setView(getTreeView());
        mTreeViewDialog = builder.show();
        TextView textView = mTreeViewDialog.findViewById(android.R.id.message);
        if (textView != null)
            textView.setTextSize(requireContext().getResources().getDimension(R.dimen._6sdp));
        Window window = mTreeViewDialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawableResource(R.drawable.tree_view_dialog_background);
    }

    private void showItemMoveDialog(long folderId) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext(), R.style.myAlertDialog)
                .setTitle("확인")
                .setMessage(getSelectedAmount() + "개의 아이템을 이동하시겠습니까?")
                .setPositiveButton("확인", (dialog, which) -> {
                    for (Folder f : mSelectedItemFolder) f.setFolderId(folderId);
                    for (Size s : mSelectedItemSize) s.setFolderId(folderId);
                    mModel.updateFolderList(mSelectedItemFolder);
                    mModel.updateSizeList(mSelectedItemSize);
                    mTreeViewDialog.dismiss();
                    mActionMode.finish();
                });
        builder.show();
    }

    private View getTreeView() {
        TreeNode root = TreeNode.root();
        List<Category> categoryList = mModel.getAllCategoryList(mActivityModel.getCategory().getParentCategory());
        List<Folder> allFolderList = mModel.getAllFolder();
        for (Category category : categoryList) {
            if (mActivityModel.getCategory().getParentCategory().equals(category.getParentCategory())) {
                TreeNode categoryTreeNode = new TreeNode(new TreeHolderCategory.IconTreeHolder(category, this)).setViewHolder(new TreeHolderCategory(requireContext()));
                root.addChild(categoryTreeNode);
                for (Folder folder : allFolderList) {
                    if (category.getId() == folder.getFolderId()) {
                        TreeNode folderTreeNode = new TreeNode(new TreeHolderFolder.IconTreeHolder(folder, mModel.getAllFolder(), 40, this)).setViewHolder(new TreeHolderFolder(requireContext()));
                        categoryTreeNode.addChild(folderTreeNode);
                    }
                }
            }
        }
        AndroidTreeView treeView = new AndroidTreeView(requireContext(), root);
        treeView.setDefaultAnimation(false);
        treeView.setUseAutoToggle(false);
        treeView.setDefaultNodeClickListener((node, value) -> {
            if (value instanceof TreeHolderCategory.IconTreeHolder) {
                TreeHolderCategory.IconTreeHolder categoryHolder = (TreeHolderCategory.IconTreeHolder) value;
                showItemMoveDialog(categoryHolder.category.getId());
                Toast.makeText(requireContext(), "카테고리 노드", Toast.LENGTH_SHORT).show();
            } else {
                TreeHolderFolder.IconTreeHolder folderHolder = (TreeHolderFolder.IconTreeHolder) value;
                showItemMoveDialog(folderHolder.folder.getId());
                Toast.makeText(requireContext(), "폴더 노드", Toast.LENGTH_SHORT).show();
            }
        });
        return treeView.getView();
    }

    private void setDialogEditText(String setText) {
        mDialogEditTextBinding.setHint("Folder Name");
        mDialogEditTextBinding.setPlaceHolder("ex)Nike, Adidas");
        mDialogEditTextBinding.setSetText(setText);
        mDialogEditTextBinding.editTextLayoutDialog.setCounterEnabled(false);
        InputFilter inputFilter = new InputFilter.LengthFilter(1000);
        mDialogEditTextBinding.editTextDialog.setFilters(new InputFilter[]{inputFilter});
        mDialogEditTextBinding.editTextDialog.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
    }

    @NotNull
    private MaterialAlertDialogBuilder getDialogBuilder() {
        return new MaterialAlertDialogBuilder(requireContext(), R.style.myAlertDialog)
                .setView(mDialogEditTextBinding.getRoot())
                .setNegativeButton("취소", null)
                .setOnDismissListener(dialog -> ((ViewGroup) mDialogEditTextBinding.getRoot().getParent()).removeAllViews());
    }
    //----------------------------------------------------------------------------------------------

    //menu------------------------------------------------------------------------------------------
    @Override
    public void onPrepareOptionsMenu(@NonNull @NotNull Menu menu) {
        MenuItem icon = menu.getItem(0);
        if (mViewType == LISTVIEW) icon.setIcon(R.drawable.icon_list);
        else icon.setIcon(R.drawable.icon_grid);
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
                setGridLayout();
            } else {
                item.setIcon(R.drawable.icon_list);
                mViewType = LISTVIEW;
                setListLayout();
            }
            SharedPreferences.Editor editor = mPreference.edit();
            editor.putInt(VIEW_TYPE, mViewType);
            editor.apply();
            return true;
        }
        return false;
    }

    @Override
    public void onAddClick(TreeNode node, long folderId) {
        showAddFolderDialog(folderId);
    }
}

