package com.example.project_myfit.ui.main.listfragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

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
import com.example.project_myfit.MyFitDialog;
import com.example.project_myfit.R;
import com.example.project_myfit.databinding.ActionModeTitleBinding;
import com.example.project_myfit.databinding.FragmentListBinding;
import com.example.project_myfit.databinding.ItemDialogEditTextBinding;
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
import com.example.project_myfit.ui.main.listfragment.treeview.TreeViewAddClick;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textview.MaterialTextView;
import com.michaelflisar.dragselectrecyclerview.DragSelectTouchListener;
import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.view.AndroidTreeView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.example.project_myfit.MyFitConstant.ACTION_MODE_NONE;
import static com.example.project_myfit.MyFitConstant.ACTION_MODE_OFF;
import static com.example.project_myfit.MyFitConstant.ACTION_MODE_ON;
import static com.example.project_myfit.MyFitConstant.GRIDVIEW;
import static com.example.project_myfit.MyFitConstant.LISTVIEW;
import static com.example.project_myfit.MyFitConstant.VIEW_TYPE;

@SuppressLint("ClickableViewAccessibility")
public class ListFragment extends Fragment implements SizeAdapterListener, TreeViewAddClick {
    private MainActivityViewModel mActivityModel;
    private ListViewModel mModel;

    private FragmentListBinding mBinding;
    private ActionModeTitleBinding mActionModeTitleBinding;

    private FolderAdapter mFolderAdapter;
    private SizeAdapterList mSizeAdapterList;
    private SizeAdapterGrid mSizeAdapterGrid;

    private ItemTouchHelper mTouchHelperList, mTouchHelperGrid, mTouchHelperFolder;

    private FloatingActionButton mActivityFab, mActivityFabAdd, mActivityFabAddFolder;
    private Animation rotateOpen, rotateClose, fromBottom, toBottom;

    private boolean isFabOpened, isFolderDragSelectEnable, isSizeDragSelectEnable, mScrollEnable;

    private List<Size> mSelectedItemSize;
    private List<Folder> mSelectedItemFolder;

    private ActionMode mActionMode;
    private MenuItem mEditMenu;
    private ActionMode.Callback mActionModeCallback;

    private DragSelectTouchListener mSelectListenerSize, mSelectListenerFolder;
    private AlertDialog mTreeViewDialog;
    private AndroidTreeView mTreeView;
    private int mViewType;
    private SharedPreferences mPreference;
    private ActionBar mActionBar;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mModel = new ViewModelProvider(this).get(ListViewModel.class);
        mActivityModel = new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);

        mModel.setThisFolder(mActivityModel);

        dragSelectListenerInit();

        mFolderAdapter = new FolderAdapter(mModel);
        mSizeAdapterList = new SizeAdapterList(mModel);
        mSizeAdapterGrid = new SizeAdapterGrid(mModel);

        actionModeCallBackInit();

        setHasOptionsMenu(true);
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

    private void actionModeCallBackInit() {
        mActionModeCallback = new ActionMode.Callback() {
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
                if (item.getItemId() == R.id.list_action_mode_del)
                    showSelectedItemDeleteDialog(mode);
                else if (item.getItemId() == R.id.list_action_mode_move)
                    showTreeViewDialog();
                else showFolderNameEditDialog(mode);
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

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = FragmentListBinding.inflate(inflater);
        mBinding.setCategory(mActivityModel.getCategory());
        setActionBarTitle();

        mPreference = requireActivity().getSharedPreferences(VIEW_TYPE, Context.MODE_PRIVATE);
        mViewType = mPreference.getInt(VIEW_TYPE, LISTVIEW);

        if (mModel.getThisFolder() != null) {
            setNavigation(mModel.getFolderHistory());
            mActivityModel.setFolder(null);
        }

        mActionModeTitleBinding = ActionModeTitleBinding.inflate(inflater);

        return mBinding.getRoot();
    }

    private void setActionBarTitle() {
        if (mActionBar == null)
            mActionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
        if (mActionBar != null) mActionBar.setTitle(mModel.getActionBarTitle());

    }

    private void setNavigation(List<Folder> folderHistory) {
        for (Folder folder : folderHistory) {
            mBinding.listNavigation.addView(getArrowIcon());
            mBinding.listNavigation.addView(getFolderNameView(folder));
        }
    }

    @NotNull
    public MaterialTextView getFolderNameView(Folder folder) {
        MaterialTextView textView = new MaterialTextView(requireContext());
        textView.setAlpha(0.8f);
        textView.setTextSize(18f);
        textView.setText(folder.getFolderName());
        textView.setOnClickListener(v -> {
            mActivityModel.setFolder(folder);
            Navigation.findNavController(requireActivity(), R.id.host_fragment).navigate(R.id.action_listFragment_refresh);
        });
        return textView;
    }

    @NotNull
    public ImageView getArrowIcon() {
        Drawable arrowIcon = ResourcesCompat.getDrawable(requireActivity().getResources(), R.drawable.icon_arrow_forward, requireActivity().getTheme());
        ImageView imageView = new ImageView(requireContext());
        imageView.setImageDrawable(arrowIcon);
        imageView.setAlpha(0.7f);
        return imageView;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        recyclerViewInit();

        autoScroll();

        itemTouchHelperInit();

        setSizeRecyclerLayout();

        fabInit();

        setClickListener();

        mModel.getFolderLive().observe(getViewLifecycleOwner(), folderList -> {
            mFolderAdapter.setItem(folderList);
            if (folderList.size() == 0) mBinding.recyclerFolder.setVisibility(View.GONE);
            else mBinding.recyclerFolder.setVisibility(View.VISIBLE);
        });

        mModel.getSizeLive().observe(getViewLifecycleOwner(), sizeList -> {
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
            showAddFolderDialog();
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
            public void onFolderCardViewLongClick(Folder folder, MaterialCardView cardView, MaterialCheckBox checkBox, int position) {
                if (mFolderAdapter.getActionModeState() == ACTION_MODE_NONE || mFolderAdapter.getActionModeState() == ACTION_MODE_OFF)
                    ((AppCompatActivity) requireActivity()).startSupportActionMode(mActionModeCallback);
                cardView.callOnClick();
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
    //&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&

    //dialog----------------------------------------------------------------------------------------
    private void showAddFolderDialog() {
        ItemDialogEditTextBinding binding = MyFitDialog.getFolderEditText(requireContext(), "");
        MyFitDialog.getAddFolderDialog(requireContext())
                .setView(binding.getRoot())
                .setPositiveButton("확인", (dialog, which) -> {
                    int largestOrder = mModel.getFolderLargestOrder() + 1;
                    mModel.insertFolder(new Folder(mModel.getCurrentTime(), String.valueOf(binding.editTextDialog.getText()), mModel.getFolderId(), largestOrder, "0"));
                }).show();
    }

    private void showAddFolderDialog(TreeNode node, TreeHolderCategory.IconTreeHolder value) {
        ItemDialogEditTextBinding binding = MyFitDialog.getFolderEditText(requireContext(), "");
        MyFitDialog.getAddFolderDialog(requireContext())
                .setView(binding.getRoot())
                .setPositiveButton("확인", (dialog, which) -> {
                    int largestOrder = mModel.getFolderLargestOrder() + 1;
                    Folder folder = new Folder(mModel.getCurrentTime(), String.valueOf(binding.editTextDialog.getText()), value.category.getId(), largestOrder, "0");
                    mModel.insertFolder(folder);
                    TreeNode treeNode = new TreeNode(new TreeHolderFolder.IconTreeHolder(folder, mModel.getAllFolder(), 40, this, mSelectedItemFolder)).setViewHolder(new TreeHolderFolder(requireContext()));
                    mTreeView.addNode(node, treeNode);
                    ((TreeHolderCategory) node.getViewHolder()).setClickListener();
                }).show();
    }

    private void showAddFolderDialog(TreeNode node, TreeHolderFolder.IconTreeHolder value) {
        ItemDialogEditTextBinding binding = MyFitDialog.getFolderEditText(requireContext(), "");
        MyFitDialog.getAddFolderDialog(requireContext())
                .setView(binding.getRoot())
                .setPositiveButton("확인", (dialog, which) -> {
                    int largestOrder = mModel.getFolderLargestOrder();
                    largestOrder++;
                    Folder folder = new Folder(mModel.getCurrentTime(), String.valueOf(binding.editTextDialog.getText()), value.folder.getId(), largestOrder, "0");
                    mModel.insertFolder(folder);
                    TreeNode treeNode = new TreeNode(new TreeHolderFolder.IconTreeHolder(folder, mModel.getAllFolder(), value.margin + 20, this, mSelectedItemFolder)).setViewHolder(new TreeHolderFolder(requireContext()));
                    mTreeView.addNode(node, treeNode);
                    ((TreeHolderFolder) node.getViewHolder()).setClickListener();
                }).show();
    }

    private void showSelectedItemDeleteDialog(ActionMode mode) {
        MyFitDialog.getSelectedItemDeleteDialog(requireContext(), getSelectedAmount())
                .setPositiveButton("확인", (dialog, which) -> {
                    mModel.deleteFolder(mSelectedItemFolder);
                    mModel.deleteSize(mSelectedItemSize);
                    mode.finish();
                }).show();
    }

    private void showTreeViewDialog() {
        mTreeViewDialog = MyFitDialog.getTreeViewDialog(requireContext(), mActivityModel.getCategory().getParentCategory(), getTreeView());
        mTreeViewDialog.show();
    }

    private void showFolderNameEditDialog(ActionMode mode) {
        Folder folder = mSelectedItemFolder.get(0);
        ItemDialogEditTextBinding binding = MyFitDialog.getFolderEditText(requireContext(), folder.getFolderName());

        MyFitDialog.getEditFolderNameDialog(requireContext())
                .setView(binding.getRoot())
                .setPositiveButton("확인", (dialog, which) -> {
                    folder.setFolderName(String.valueOf(binding.editTextDialog.getText()));
                    mModel.updateFolder(folder);
                    mode.finish();
                }).show();
    }

    private void showItemMoveDialog(long folderId) {
        MyFitDialog.getItemMoveDialog(requireContext(), getSelectedAmount())
                .setPositiveButton("확인", (dialog, which) -> {
                    for (Folder f : mSelectedItemFolder) f.setFolderId(folderId);
                    for (Size s : mSelectedItemSize) s.setFolderId(folderId);
                    mModel.updateFolder(mSelectedItemFolder);
                    mModel.updateSizeList(mSelectedItemSize);
                    mTreeViewDialog.dismiss();
                    mActionMode.finish();
                }).show();
    }

    private View getTreeView() {
        mTreeView = MyFitDialog.getTreeView(requireContext(), mModel.getCategoryList(), mModel.getAllFolder(), this, mSelectedItemFolder);
        mTreeView.setDefaultNodeClickListener((node, value) -> {
            if (value instanceof TreeHolderCategory.IconTreeHolder) {
                TreeHolderCategory.IconTreeHolder categoryHolder = (TreeHolderCategory.IconTreeHolder) value;
                showItemMoveDialog(categoryHolder.category.getId());
            } else if (value instanceof TreeHolderFolder.IconTreeHolder && !((TreeHolderFolder) node.getViewHolder()).isSelected()) {
                TreeHolderFolder.IconTreeHolder folderHolder = (TreeHolderFolder.IconTreeHolder) value;
                showItemMoveDialog(folderHolder.folder.getId());
            }
        });
        return mTreeView.getView();
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
            } else {
                item.setIcon(R.drawable.icon_list);
                mViewType = LISTVIEW;
            }
            setSizeRecyclerLayout();
            SharedPreferences.Editor editor = mPreference.edit();
            editor.putInt(VIEW_TYPE, mViewType);
            editor.apply();
            return true;
        }
        return false;
    }

    @Override
    public void OnCategoryAddClick(TreeNode node, TreeHolderCategory.IconTreeHolder value) {
        showAddFolderDialog(node, value);
    }

    @Override
    public void OnFolderAddClick(TreeNode node, TreeHolderFolder.IconTreeHolder value) {
        showAddFolderDialog(node, value);
    }
}

