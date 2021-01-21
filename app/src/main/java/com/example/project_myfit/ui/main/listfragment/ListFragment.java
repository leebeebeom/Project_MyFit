package com.example.project_myfit.ui.main.listfragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.DialogFragment;
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
import com.example.project_myfit.dialog.AddFolderDialog;
import com.example.project_myfit.dialog.FolderNameEditDialog;
import com.example.project_myfit.dialog.ItemMoveDialog;
import com.example.project_myfit.dialog.SelectedItemDeleteDialog;
import com.example.project_myfit.dialog.TreeAddFolderDialog;
import com.example.project_myfit.dialog.TreeViewDialog;
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
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textview.MaterialTextView;
import com.michaelflisar.dragselectrecyclerview.DragSelectTouchListener;
import com.unnamed.b.atv.model.TreeNode;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.example.project_myfit.MyFitConstant.ACTION_MODE_OFF;
import static com.example.project_myfit.MyFitConstant.ACTION_MODE_ON;
import static com.example.project_myfit.MyFitConstant.GRIDVIEW;
import static com.example.project_myfit.MyFitConstant.LISTVIEW;
import static com.example.project_myfit.MyFitConstant.VIEW_TYPE;
/*
TODO
트리뷰 다이얼로그 플러스 버튼 키우기(클릭 잘 안됨)
트리뷰 아이템 디자인 수정
트리뷰 현재 위치 표현 방법 구상

리플 이펙트 필요한 곳 정리 후 추가

폴더, 그리드, 리스트 체크박스, 드래그핸들 키우기(터치 잘 안됨)

인풋아웃풋에서 mActivityModel.getSize 모델에 저장하고 null 시키고 전부 변경

폴더 생성, 삭제 어마운트 변경
사이즈 생성 상제 어마운트 변경
 */

@SuppressLint("ClickableViewAccessibility")
public class ListFragment extends Fragment implements SizeAdapterListener,
        AddFolderDialog.AddFolderConfirmClick, TreeAddFolderDialog.TreeAddFolderConfirmClick,
        TreeNode.TreeNodeClickListener, TreeViewDialog.TreeViewAddClick,
        SelectedItemDeleteDialog.SelectedItemDeleteConfirmClick, FolderNameEditDialog.FolderNameEditConfirmClick,
        ItemMoveDialog.ItemMoveConfirmClick {

    private MainActivityViewModel mActivityModel;
    private ListViewModel mModel;

    private FragmentListBinding mBinding;
    private ActionModeTitleBinding mActionModeTitleBinding;

    private FolderAdapter mFolderAdapter;
    private SizeAdapterList mSizeAdapterList;
    private SizeAdapterGrid mSizeAdapterGrid;

    private ItemTouchHelper mTouchHelperList, mTouchHelperGrid, mTouchHelperFolder;

    private FloatingActionButton mActivityFab;

    private boolean isFolderDragSelectEnable, isSizeDragSelectEnable, mScrollEnable;

    private MenuItem mEditMenu, mMoveMenu, mDeletedMenu;
    private ActionMode.Callback mActionModeCallback;

    private DragSelectTouchListener mSelectListenerSize, mSelectListenerFolder;
    private int mViewType;
    private SharedPreferences mPreference;
    private ActionMode mActionMode;
    private boolean mActionModeOn;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mModel = new ViewModelProvider(this).get(ListViewModel.class);
        mActivityModel = new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);

        if (mModel.getThisFolder() == null) {
            Folder thisFolder = mActivityModel.getFolder() == null ? null : mActivityModel.getFolder();
            long folderId = mActivityModel.getFolder() == null ? mActivityModel.getCategory().getId() : mActivityModel.getFolder().getId();
            mModel.setThisFolder(thisFolder, folderId);
        }

        dragSelectListenerInit();

        mFolderAdapter = new FolderAdapter(mModel);
        mSizeAdapterList = new SizeAdapterList(mModel);
        mSizeAdapterGrid = new SizeAdapterGrid(mModel);

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

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mActivityModel.setSize(null);

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

        actionModeCallBackInit();

        if (savedInstanceState != null && savedInstanceState.getBoolean("action mode")) {//액션모드 리스토어
            ((AppCompatActivity) requireActivity()).startSupportActionMode(mActionModeCallback);
            mFolderAdapter.setSelectedPosition(mModel.getSelectedPositionFolder());
            if (mViewType == LISTVIEW)
                mSizeAdapterList.setSelectedPosition(mModel.getSelectedPositionSize());
            else mSizeAdapterGrid.setSelectedPosition(mModel.getSelectedPositionSize());
        }


        return mBinding.getRoot();
    }

    private void setActionBarTitle() {
        ActionBar actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
        if (actionBar != null) {
            String actionBarTitle = mModel.getThisFolder() == null ? mActivityModel.getCategory().getCategory() : mModel.getThisFolder().getFolderName();
            actionBar.setTitle(actionBarTitle);
        }
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
        textView.setMaxLines(1);
        textView.setEllipsize(TextUtils.TruncateAt.END);
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

    private void actionModeCallBackInit() {
        mActionModeCallback = new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                mActionModeOn = true;
                mActionMode = mode;

                mode.getMenuInflater().inflate(R.menu.list_action_mode, menu);
                mode.setCustomView(mActionModeTitleBinding.getRoot());

                mFolderAdapter.setActionModeState(ACTION_MODE_ON);
                if (mViewType == LISTVIEW)
                    mSizeAdapterList.setActionModeState(ACTION_MODE_ON);
                else mSizeAdapterGrid.setActionModeState(ACTION_MODE_ON);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                selectAllClick();
                mEditMenu = menu.getItem(0);
                mMoveMenu = menu.getItem(1);
                mDeletedMenu = menu.getItem(2);
                return true;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                if (item.getItemId() == R.id.list_action_mode_del) {
                    if (mModel.getSelectedAmount().getValue() != null)
                        showDialog(SelectedItemDeleteDialog.newInstance(mModel.getSelectedAmount().getValue()), "delete");
                } else if (item.getItemId() == R.id.list_action_mode_move) {
                    mActivityModel.setSelectedFolder(mModel.getSelectedItemFolder());
                    showDialog(new TreeViewDialog(), "tree");
                } else if (item.getItemId() == R.id.list_action_mode_edit) {
                    showDialog(FolderNameEditDialog.newInstance(mModel.getSelectedItemFolder().get(0).getFolderName()), "edit");
                }
                return true;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                mActionModeOn = false;
                mActionMode = null;

                mActionModeTitleBinding.actionModeTitle.setText("");
                mActionModeTitleBinding.actionModeSelectAll.setChecked(false);
                ((ViewGroup) mActionModeTitleBinding.getRoot().getParent()).removeAllViews();
                mModel.setSelectedPositionFolder(mFolderAdapter.getSelectedPosition());
                mFolderAdapter.setActionModeState(ACTION_MODE_OFF);
                if (mViewType == LISTVIEW) {
                    mModel.setSelectedPositionSize(mSizeAdapterList.getSelectedPosition());
                    mSizeAdapterList.setActionModeState(MyFitConstant.ACTION_MODE_OFF);
                } else {
                    mModel.setSelectedPositionSize(mSizeAdapterGrid.getSelectedPosition());
                    mSizeAdapterGrid.setActionModeState(MyFitConstant.ACTION_MODE_OFF);
                }

            }
        };
    }

    private void selectAllClick() {
        mActionModeTitleBinding.actionModeSelectAll.setOnClickListener(v -> {
            mModel.selectAllClick(mActionModeTitleBinding.actionModeSelectAll.isChecked(), mFolderAdapter.getCurrentList(), mSizeAdapterList.getCurrentList());
            if (mActionModeTitleBinding.actionModeSelectAll.isChecked()) {
                mFolderAdapter.selectAll();
                mSizeAdapterList.selectAll();
                mSizeAdapterGrid.selectAll();
            } else {
                mFolderAdapter.deselectAll();
                mSizeAdapterList.deselectAll();
                mSizeAdapterGrid.deselectAll();
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivityFab = requireActivity().findViewById(R.id.activity_fab);

        recyclerViewInit();

        autoScroll();

        itemTouchHelperInit();

        setSizeRecyclerLayout();

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

        mModel.getSelectedAmount().observe(getViewLifecycleOwner(), integer -> {
            String title = integer + getString(R.string.item_selected);
            mActionModeTitleBinding.actionModeTitle.setText(title);
            if (mActionMode != null) {
                mEditMenu.setVisible(integer == 1 && mModel.getSelectedItemSize().size() == 0);
                mMoveMenu.setVisible(integer > 0);
                mDeletedMenu.setVisible(integer > 0);
            }
            if (mViewType == LISTVIEW)//모든 아이템 선택되면 전체 선택 체크
                mActionModeTitleBinding.actionModeSelectAll.setChecked(
                        mSizeAdapterList.getCurrentList().size() + mFolderAdapter.getCurrentList().size() == integer);
            else
                mActionModeTitleBinding.actionModeSelectAll.setChecked(
                        mSizeAdapterGrid.getCurrentList().size() + mFolderAdapter.getCurrentList().size() == integer);
        });
    }

    private void recyclerViewInit() {
        mBinding.recyclerFolder.setAdapter(mFolderAdapter);
        mBinding.recyclerFolder.addOnItemTouchListener(mSelectListenerFolder);
        mBinding.recyclerSize.addOnItemTouchListener(mSelectListenerSize);
    }

    private void autoScroll() {
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
        if (mActionMode != null) mActionMode.finish();
    }

    //클릭&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&
    private void setClickListener() {
        categoryTextClick();

        homeIconClick();

        fabClick();

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

    private void fabClick() {
        mActivityFab.setOnClickListener(v -> Navigation.findNavController(requireActivity(), R.id.host_fragment).navigate(R.id.action_listFragment_to_inputOutputFragment));
    }

    //폴더 클릭-------------------------------------------------------------------------------------
    private void folderAdapterClick() {
        mFolderAdapter.setOnFolderAdapterListener(new FolderAdapterListener() {
            @Override
            public void onFolderCardViewClick(Folder folder, MaterialCheckBox checkBox, int position) {
                if (mActionMode == null) {
                    mActivityModel.setFolder(folder);
                    Navigation.findNavController(requireActivity(), R.id.host_fragment).navigate(R.id.action_listFragment_refresh);
                } else actionModeOnClickFolder(folder, position, checkBox);
            }

            @Override
            public void onFolderCardViewLongClick(MaterialCardView cardView, int position) {
                if (mActionMode == null) {
                    mModel.selectedItemListInit();
                    ((AppCompatActivity) requireActivity()).startSupportActionMode(mActionModeCallback);
                }
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
        mModel.folderSelected(folder, checkBox.isChecked());
        mFolderAdapter.setSelectedPosition(position);
    }

    //사이즈 클릭-----------------------------------------------------------------------------------
    @Override
    public void onSizeCardViewClick(Size size, MaterialCheckBox checkBox, int position) {
        if (mActionMode == null) {
            mActivityModel.setSize(size);
            Navigation.findNavController(requireActivity(), R.id.host_fragment).navigate(R.id.action_listFragment_to_inputOutputFragment);
        } else actionModeOnClickSize(size, checkBox, position, false);
    }

    private void actionModeOnClickSize(Size size, MaterialCheckBox checkBox, int position, boolean areYouCheckBox) {
        if (!areYouCheckBox) checkBox.setChecked(!checkBox.isChecked());
        mModel.sizeSelected(size, checkBox.isChecked());
        if (mViewType == LISTVIEW) mSizeAdapterList.setSelectedPosition(position);
        else mSizeAdapterGrid.setSelectedPosition(position);
    }

    @Override
    public void onSizeCardViewLongClick(int position) {
        if (mActionMode == null) {
            mModel.selectedItemListInit();
            ((AppCompatActivity) requireActivity()).startSupportActionMode(mActionModeCallback);
        }
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
    public void onSizeDragHandTouch(RecyclerView.ViewHolder viewHolder) {
        if (mActionMode == null && mViewType == LISTVIEW)
            mTouchHelperList.startDrag(viewHolder);
        else if (mActionMode == null && mViewType == GRIDVIEW)
            mTouchHelperGrid.startDrag(viewHolder);
    }

    @Override
    public void onSizeCheckBoxClick(Size size, MaterialCheckBox checkBox, int position) {
        actionModeOnClickSize(size, checkBox, position, true);
    }

    @Override
    public void onSizeCheckBoxLongCLick(int position) {
        cardViewCallOnClick(position);
        mSelectListenerSize.startDragSelection(position);
    }
    //&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&

    //메뉴------------------------------------------------------------------------------------------
    @Override
    public void onPrepareOptionsMenu(@NonNull @NotNull Menu menu) {
        MenuItem icon = menu.getItem(1);
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
        } else if (item.getItemId() == R.id.menu_list_add_folder) {
            showDialog(new AddFolderDialog(), "add");
        }
        return false;
    }

    //다이얼로그 클릭-------------------------------------------------------------------------------
    private void showDialog(DialogFragment dialog, String tag) {
        dialog.setTargetFragment(this, 0);
        dialog.show(getParentFragmentManager(), tag);
    }

    @Override
    public void addFolderConfirmClick(String folderName) {//폴더 추가 클릭
        int largestOrder = mModel.getFolderLargestOrder() + 1;
        mModel.insertFolder(new Folder(mModel.getCurrentTime(), folderName, mModel.getFolderId(), largestOrder, "0"));
    }

    @Override//카테고리 노드 폴더 추가 클릭
    public void treeViewCategoryAddClick(TreeNode node, TreeHolderCategory.CategoryTreeHolder value) {
        mModel.nodeAddClick(node, value);
        showDialog(new TreeAddFolderDialog(), "tree add");
    }

    @Override//카테고리 노드 폴더 추가 클릭
    public void treeViewFolderAddClick(TreeNode node, TreeHolderFolder.FolderTreeHolder value) {
        mModel.nodeAddClick(node, value);
        showDialog(new TreeAddFolderDialog(), "tree add");
    }

    @Override
    public void treeAddFolderConfirmClick(String folderName) {
        TreeNode oldNode = mModel.getAddNode();
        if (mModel.getCategoryAddValue() != null) {//카테고리 노드 폴더 추가 확인 클릭
            TreeHolderCategory.CategoryTreeHolder oldViewHolder = mModel.getCategoryAddValue();

            int largestOrder = mModel.getFolderLargestOrder() + 1;
            Folder folder = new Folder(mModel.getCurrentTime(), folderName, oldViewHolder.category.getId(), largestOrder, "0");
            mModel.insertFolder(folder);

            //바뀐 노드 찾기
            List<TreeNode> categoryNodeList = mActivityModel.getRootTreeNode().getChildren();
            for (TreeNode newNode : categoryNodeList) {
                TreeHolderCategory.CategoryTreeHolder newViewHolder = (TreeHolderCategory.CategoryTreeHolder) newNode.getValue();
                if (oldViewHolder.category.getId() == newViewHolder.category.getId()) {
                    oldNode = newNode;
                    break;
                }
            }

            TreeNode treeNode = new TreeNode(new TreeHolderFolder.FolderTreeHolder(folder, mModel.getAllFolder(),
                    40, this, mModel.getSelectedItemFolder())).setViewHolder(new TreeHolderFolder(requireContext()));
            oldNode.getViewHolder().getTreeView().addNode(oldNode, treeNode);
            oldNode.getViewHolder().getTreeView().expandNode(oldNode);
            ((TreeHolderCategory) oldNode.getViewHolder()).setIconClickable();
        } else {//폴더 노드 폴더 추가 확인 클릭
            TreeHolderFolder.FolderTreeHolder oldViewHolder = mModel.getFolderAddValue();

            int largestOrder = mModel.getFolderLargestOrder() + 1;
            Folder folder = new Folder(mModel.getCurrentTime(), folderName, oldViewHolder.folder.getId(), largestOrder, "0");
            mModel.insertFolder(folder);

            //모든 폴더 노드 리스트화
            List<TreeNode> categoryNodeList = mActivityModel.getRootTreeNode().getChildren();
            List<TreeNode> folderNodeList = new ArrayList<>();
            for (TreeNode categoryNode : categoryNodeList) {
                if (categoryNode.getChildren().size() != 0)
                    folderNodeList.addAll(categoryNode.getChildren());
            }
            List<TreeNode> newFolderList = new ArrayList<>(folderNodeList);
            List<TreeNode> newFolderList2 = listingFolderNode(folderNodeList, newFolderList);

            //바뀐 노드 찾기
            for (TreeNode folderNode : newFolderList2) {
                TreeHolderFolder.FolderTreeHolder newViewHolder = (TreeHolderFolder.FolderTreeHolder) folderNode.getValue();
                if (oldViewHolder.folder.getId() == newViewHolder.folder.getId()) {
                    oldNode = folderNode;
                    break;
                }
            }

            TreeNode treeNode = new TreeNode(new TreeHolderFolder.FolderTreeHolder(folder, mModel.getAllFolder(),
                    oldViewHolder.margin + 20, this, mModel.getSelectedItemFolder())).setViewHolder(new TreeHolderFolder(requireContext()));
            oldNode.getViewHolder().getTreeView().addNode(oldNode, treeNode);
            oldNode.getViewHolder().getTreeView().expandNode(oldNode);
            ((TreeHolderFolder) oldNode.getViewHolder()).setIconClickable();
        }

    }

    private List<TreeNode> listingFolderNode(List<TreeNode> folderNodeList, List<TreeNode> newFolderList) {
        for (TreeNode folderNode : folderNodeList) {
            if (folderNode.getChildren().size() != 0) {
                newFolderList.addAll(folderNode.getChildren());
                listingFolderNode(folderNode.getChildren(), newFolderList);
            }
        }
        return newFolderList;
    }

    @Override
    public void onClick(TreeNode node, Object value) {//트리뷰 노드 클릭
        if (value instanceof TreeHolderCategory.CategoryTreeHolder) {
            TreeHolderCategory.CategoryTreeHolder categoryHolder = (TreeHolderCategory.CategoryTreeHolder) value;
            if (mModel.getSelectedAmount().getValue() != null)
                showDialog(ItemMoveDialog.getInstance(mModel.getSelectedAmount().getValue(), categoryHolder.category.getId()), "move");
        } else if (value instanceof TreeHolderFolder.FolderTreeHolder && !((TreeHolderFolder) node.getViewHolder()).isSelected()) {
            TreeHolderFolder.FolderTreeHolder folderHolder = (TreeHolderFolder.FolderTreeHolder) value;
            if (mModel.getSelectedAmount().getValue() != null)
                showDialog(ItemMoveDialog.getInstance(mModel.getSelectedAmount().getValue(), folderHolder.folder.getId()), "move");
        }
    }

    @Override
    public void selectedItemDeleteConfirmClick() {//삭제 다이얼로그 클릭
        mModel.selectedItemDelete();
        mActionMode.finish();
    }

    @Override
    public void folderNameEditConfirmClick(String folderName) {//폴더 이름 변경 다이얼로그 클릭
        Folder folder = mModel.getSelectedItemFolder().get(0);
        folder.setFolderName(folderName);
        mModel.updateFolder(folder);
        mActionMode.finish();
    }

    @Override
    public void itemMoveConfirmClick(long folderId) {//아이템 이동 다이얼로그 확인 클릭
        mModel.selectedItemMove(folderId);
        //트리뷰 다이얼로그 제거
        TreeViewDialog treeViewDialog = ((TreeViewDialog) getParentFragmentManager().findFragmentByTag("tree"));
        if (treeViewDialog != null) treeViewDialog.dismiss();
        mActionMode.finish();
    }

    @Override
    public void onSaveInstanceState(@NonNull @NotNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("action mode", mActionModeOn);
    }
}
