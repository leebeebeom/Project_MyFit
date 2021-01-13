package com.example.project_myfit.ui.main.listfragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

public class ListFragment extends Fragment implements SizeAdapterListener {

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
    private FloatingActionButton mActivityFab;
    private Animation rotateOpen, rotateClose, fromBottom, toBottom;
    private boolean isFabOpened;
    private List<Size> mSelectedItemSize;
    private List<Folder> mSelectedItemFolder;
    private ActionMode mActionMode;
    private MenuItem mEditMenuIcon;
    private long mFolderId;
    //actionModeCallback----------------------------------------------------------------------------
    private final ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
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
            mActionMode = mode;
            mSelectedItemSize = new ArrayList<>();
            mSelectedItemFolder = new ArrayList<>();
            selectAllClick();
            mEditMenuIcon = menu.getItem(0);
            return true;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            //TODO 이동 만들기
            if (item.getItemId() == R.id.list_action_mode_del)
                showDeleteDialog(mode);
            else if (item.getItemId() == R.id.list_action_mode_move)
                Log.d("로그", "onActionItemClicked: 메롱");
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
    private Folder mThisFolder;

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
                if (mViewType == LISTVIEW) mSizeAdapterList.deselectAll();
                else mSizeAdapterGrid.deselectAll();
                mSelectedItemSize.clear();
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

    //----------------------------------------------------------------------------------------------
    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mModel = new ViewModelProvider(this).get(ListViewModel.class);
        mActivityModel = new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);
        mPreference = requireContext().getSharedPreferences(VIEW_TYPE, Context.MODE_PRIVATE);
        mViewType = mPreference.getInt(VIEW_TYPE, LISTVIEW);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = FragmentListBinding.inflate(inflater);
        mDialogEditTextBinding = ItemDialogEditTextBinding.inflate(inflater);
        mActionModeTitleBinding = ActionModeTitleBinding.inflate(inflater);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBinding.setCategory(mActivityModel.getCategory());
        mBinding.listTextCategory.setOnClickListener(v -> {
            mActivityModel.getFolderList().clear();
            Navigation.findNavController(requireView()).navigate(R.id.action_listFragment_refresh);
        });

        if (mActivityModel.getFolderList().size() != 0) {
            for (Folder f : mActivityModel.getFolderList()) {

            }
        }
        //------------------------------------------------------------------------------------------
        mModel.getAllFolderLive().observe(getViewLifecycleOwner(), allFolderList -> {
            if (mThisFolder == null)
                mThisFolder = mActivityModel.getFolderList().size() != 0 ? mActivityModel.getFolderList().get(mActivityModel.getFolderList().size() - 1) : null;
            if (mThisFolder != null) {
                List<Folder> folderHistory = new ArrayList<>();
                folderHistory.add(mThisFolder);
                List<Folder> folderHistory2 = getFolderHistory(allFolderList, folderHistory, mThisFolder);
                Collections.reverse(folderHistory2);
                for (Folder f : folderHistory2) {
                    ImageView arrowIcon = getArrowIcon();
                    MaterialTextView folderNameView = getFolderNameView();
                    mBinding.listNavigation.addView(arrowIcon);
                    folderNameView.setText(f.getFolderName());
                    mBinding.listNavigation.addView(folderNameView);
                    folderNameView.setOnClickListener(v -> {
                        mActivityModel.setListFolder(f);
                        Navigation.findNavController(requireView()).navigate(R.id.action_listFragment_refresh);
                    });
                }
            }
        });

        //------------------------------------------------------------------------------------------
        mFolderId = mActivityModel.getFolderList().size() == 0 ? mActivityModel.getCategory().getId() :
                mActivityModel.getFolderList().get(mActivityModel.getFolderList().size() - 1).getId();
        mActivityModel.setSize(null);
        setActionBarTitle();
    }

    private List<Folder> getFolderHistory(List<Folder> allFolderList, List<Folder> folderHistory, Folder thisFolder) {
        for (Folder parentFolder : allFolderList) {
            if (parentFolder.getId() == thisFolder.getFolderId()) {
                folderHistory.add(parentFolder);
                getFolderHistory(allFolderList, folderHistory, parentFolder);
            }
        }
        return folderHistory;
    }

    @NotNull
    private MaterialTextView getFolderNameView() {
        MaterialTextView textView = new MaterialTextView(requireContext());
        textView.setAlpha(0.8f);
        textView.setTextSize(18f);
        return textView;
    }

    @NotNull
    private ImageView getArrowIcon() {
        Drawable arrowIcon = ResourcesCompat.getDrawable(getResources(), R.drawable.icon_arrow_forward, requireActivity().getTheme());
        ImageView imageView = new ImageView(requireContext());
        imageView.setImageDrawable(arrowIcon);
        imageView.setAlpha(0.7f);
        return imageView;
    }

    private void setActionBarTitle() {
        ActionBar actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
        if (actionBar != null)
            if (mActivityModel.getFolderList().size() == 0)
                actionBar.setTitle(mActivityModel.getCategory().getCategory());
            else
                actionBar.setTitle(mActivityModel.getFolderList().get(mActivityModel.getFolderList().size() - 1).getFolderName());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
        mModel.setAllCategoryList(mActivityModel.getCategory().getParentCategory()); // for TreeView

        if (mViewType == LISTVIEW) setListLayout();
        else setGridLayout();

        setClickListener();

        //folder Renew
        mModel.getAllFolder(mFolderId).observe(getViewLifecycleOwner(), folderList -> {
            mModel.setAllFolderList(); // for TreeView
            mModel.setFolderLargestOrder();
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (isFabOpened) mActivityFab.startAnimation(rotateClose);
        if (mActionMode != null) mActionMode.finish();
    }


    private void init() {
        mFolderAdapter = new FolderAdapter(mModel);
        mSizeAdapterList = new SizeAdapterList(mModel);
        mSizeAdapterGrid = new SizeAdapterGrid(mModel);

        mBinding.recyclerFolder.setHasFixedSize(true);
        mBinding.recyclerFolder.setAdapter(mFolderAdapter);
        mBinding.recyclerFolder.addOnItemTouchListener(mSelectListenerFolder);
        mBinding.recyclerSize.addOnItemTouchListener(mSelectListenerSize);

        mTouchHelperFolder = new ItemTouchHelper(new FolderDragCallBack(mFolderAdapter));
        mTouchHelperFolder.attachToRecyclerView(mBinding.recyclerFolder);
        mTouchHelperList = new ItemTouchHelper(new SizeDragCallBackList(mSizeAdapterList));
        mTouchHelperGrid = new ItemTouchHelper(new SizeDragCallBackGrid(mSizeAdapterGrid));

        mActivityFab = requireActivity().findViewById(R.id.activity_fab);
        rotateOpen = AnimationUtils.loadAnimation(requireContext(), R.anim.rotate_open);
        rotateClose = AnimationUtils.loadAnimation(requireContext(), R.anim.rotate_close);
        fromBottom = AnimationUtils.loadAnimation(requireContext(), R.anim.folder_from_bottom);
        toBottom = AnimationUtils.loadAnimation(requireContext(), R.anim.folder_to_bottom);
    }

    //size recyclerView layout----------------------------------------------------------------------
    private void setListLayout() {
        int paddingTop = (int) getResources().getDimension(R.dimen._8sdp);
        int paddingBottom = (int) getResources().getDimension(R.dimen._60sdp);
        mBinding.recyclerSize.setPadding(0, paddingTop, 0, paddingBottom);
        mBinding.recyclerSize.setLayoutManager(new LinearLayoutManager(requireContext()));
        mBinding.recyclerSize.setAdapter(mSizeAdapterList);
        mTouchHelperList.attachToRecyclerView(mBinding.recyclerSize);
    }

    private void setGridLayout() {
        int padding = (int) getResources().getDimension(R.dimen._8sdp);
        int paddingBottom = (int) getResources().getDimension(R.dimen._60sdp);
        mBinding.recyclerSize.setPadding(padding, padding, padding, paddingBottom);
        mBinding.recyclerSize.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        mBinding.recyclerSize.setAdapter(mSizeAdapterGrid);
        mTouchHelperGrid.attachToRecyclerView(mBinding.recyclerSize);
    }
    //----------------------------------------------------------------------------------------------

    //click&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&
    private void setClickListener() {
        honIconClick();

        fabClick();
        addFabClick();
        addFolderFabClick();

        folderAdapterClick();
        mSizeAdapterList.setOnSizeAdapterListener(this);
        mSizeAdapterGrid.setOnSizeAdapterListener(this);
    }

    private void honIconClick() {
        mBinding.listIconHome.setOnClickListener(v -> Navigation.findNavController(requireView()).navigate(R.id.action_listFragment_to_mainFragment));
    }

    //fab click-------------------------------------------------------------------------------------
    private void fabClick() {
        mActivityFab.setOnClickListener(v -> {
            isFabOpened = !isFabOpened;
            setVisibility(isFabOpened);
            setAnimation(isFabOpened);
            setClickable(isFabOpened);
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext(), R.style.myAlertDialog)
                    .setMessage("아이템 이동")
                    .setView(asd());
            AlertDialog dialogs = builder.show();
            TextView textView = dialogs.findViewById(android.R.id.message);
            if (textView != null)
                textView.setTextSize(requireContext().getResources().getDimension(R.dimen._6sdp));
            Window window = dialogs.getWindow();
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            window.setBackgroundDrawableResource(R.drawable.tree_view_dialog_background);
        });

    }

    private void addFabClick() {
        mBinding.listFabAdd.setOnClickListener(v -> {
            mActivityFab.performClick();
            goInputOutputFragment();
        });
    }

    private void addFolderFabClick() {
        mBinding.listFabAddFolder.setOnClickListener(v -> {
            mActivityFab.performClick();
            showAddFolderDialog();
        });
    }

    private void setVisibility(boolean isFabOpened) {
        if (isFabOpened) {
            mBinding.listFabAdd.setVisibility(View.VISIBLE);
            mBinding.listFabAddFolder.setVisibility(View.VISIBLE);
        } else {
            mBinding.listFabAdd.setVisibility(View.INVISIBLE);
            mBinding.listFabAddFolder.setVisibility(View.INVISIBLE);
        }
    }

    private void setAnimation(boolean isFabOpened) {
        if (isFabOpened) {
            mActivityFab.startAnimation(rotateOpen);
            mBinding.listFabAdd.startAnimation(fromBottom);
            mBinding.listFabAddFolder.startAnimation(fromBottom);
        } else {
            mActivityFab.startAnimation(rotateClose);
            mBinding.listFabAdd.startAnimation(toBottom);
            mBinding.listFabAddFolder.startAnimation(toBottom);
        }
    }

    private void setClickable(boolean isFabOpened) {
        mBinding.listFabAdd.setClickable(isFabOpened);
        mBinding.listFabAddFolder.setClickable(isFabOpened);
    }
    //----------------------------------------------------------------------------------------------

    //adapter click---------------------------------------------------------------------------------
    private void folderAdapterClick() {
        mFolderAdapter.setOnFolderAdapterListener(new FolderAdapterListener() {
            @Override
            public void onCardViewClick(Folder folder, MaterialCheckBox checkBox, int position) {
                if (mFolderAdapter.getActionModeState() == ACTION_MODE_NONE || mFolderAdapter.getActionModeState() == ACTION_MODE_OFF) {
                    if (!mActivityModel.getFolderList().contains(folder))
                        mActivityModel.setListFolder(folder);
                    Navigation.findNavController(requireView()).navigate(R.id.action_listFragment_refresh);
                } else actionModeOnClickFolder(folder, position, checkBox);
            }

            @Override
            public void onCardViewLongClick(Folder folder, RecyclerView.ViewHolder holder, MaterialCheckBox checkBox, int position) {
                if (mFolderAdapter.getActionModeState() == ACTION_MODE_NONE || mFolderAdapter.getActionModeState() == ACTION_MODE_OFF)
                    ((AppCompatActivity) requireActivity()).startSupportActionMode(mActionModeCallback);
                holder.itemView.findViewById(R.id.folder_card_view).callOnClick();
                mSelectListenerFolder.startDragSelection(position);
            }

            @Override
            public void onDragHandTouch(RecyclerView.ViewHolder holder) {
                mTouchHelperFolder.startDrag(holder);
            }

        });
    }

    @Override
    public void onCardViewClick(Size size, MaterialCheckBox checkBox, int position) {
        if ((mSizeAdapterList.getActionModeState() == ACTION_MODE_NONE || mSizeAdapterList.getActionModeState() == ACTION_MODE_OFF) && mViewType == LISTVIEW) {
            mActivityModel.setSize(size);
            goInputOutputFragment();
        } else if ((mSizeAdapterGrid.getActionModeState() == ACTION_MODE_NONE || mSizeAdapterGrid.getActionModeState() == ACTION_MODE_OFF) && mViewType == GRIDVIEW) {
            mActivityModel.setSize(size);
            goInputOutputFragment();
        } else actionModeOnClickSize(size, checkBox, position, false);
    }

    @Override
    public void onCardViewLongClick(Size size, MaterialCheckBox checkBox, int position) {
        if ((mSizeAdapterList.getActionModeState() == ACTION_MODE_NONE || mSizeAdapterList.getActionModeState() == ACTION_MODE_OFF) && mViewType == LISTVIEW)
            ((AppCompatActivity) requireActivity()).startSupportActionMode(mActionModeCallback);
        else if ((mSizeAdapterGrid.getActionModeState() == ACTION_MODE_NONE || mSizeAdapterGrid.getActionModeState() == ACTION_MODE_OFF) && mViewType == GRIDVIEW)
            ((AppCompatActivity) requireActivity()).startSupportActionMode(mActionModeCallback);
        cardViewCallOnClick(position);
        mSelectListenerSize.startDragSelection(position);
    }

    @Override
    public void onDragHandTouch(RecyclerView.ViewHolder viewHolder) {
        if ((mSizeAdapterList.getActionModeState() == ACTION_MODE_NONE || mSizeAdapterList.getActionModeState() == ACTION_MODE_OFF) && mViewType == LISTVIEW)
            mTouchHelperList.startDrag(viewHolder);
        else if ((mSizeAdapterGrid.getActionModeState() == ACTION_MODE_NONE || mSizeAdapterGrid.getActionModeState() == ACTION_MODE_OFF) && mViewType == GRIDVIEW)
            mTouchHelperGrid.startDrag(viewHolder);
    }

    @Override
    public void onCheckBoxClick(Size size, MaterialCheckBox checkBox, int position) {
        actionModeOnClickSize(size, checkBox, position, true);
    }

    @Override
    public void onCheckBoxLongCLick(int position) {
        cardViewCallOnClick(position);
        mSelectListenerSize.startDragSelection(position);
    }

    //click-----------------------------------------------------------------------------------------
    private void actionModeOnClickSize(Size size, MaterialCheckBox checkBox, int position, boolean areYouCheckBox) {
        if (!areYouCheckBox) checkBox.setChecked(!checkBox.isChecked());
        if (checkBox.isChecked()) mSelectedItemSize.add(size);
        else mSelectedItemSize.remove(size);
        if (mViewType == LISTVIEW) mSizeAdapterList.setSelectedPosition(position);
        else mSizeAdapterGrid.setSelectedPosition(position);
        setActionModeTitle();
        if (mViewType == LISTVIEW)
            mActionModeTitleBinding.actionModeSelectAll.setChecked(mSizeAdapterList.getCurrentList().size() == mSelectedItemSize.size()
                    && mFolderAdapter.getCurrentList().size() == mSelectedItemFolder.size());
        else
            mActionModeTitleBinding.actionModeSelectAll.setChecked(mSizeAdapterGrid.getCurrentList().size() == mSelectedItemSize.size()
                    && mFolderAdapter.getCurrentList().size() == mSelectedItemFolder.size());
        mEditMenuIcon.setVisible(mSelectedItemSize.size() == 0 && mSelectedItemFolder.size() == 1);
    }

    private void actionModeOnClickFolder(Folder folder, int position, MaterialCheckBox checkBox) {
        checkBox.setChecked(!checkBox.isChecked());
        if (checkBox.isChecked()) mSelectedItemFolder.add(folder);
        else mSelectedItemFolder.remove(folder);
        mFolderAdapter.setSelectedPosition(position);
        setActionModeTitle();
        mActionModeTitleBinding.actionModeSelectAll.setChecked(mSizeAdapterGrid.getCurrentList().size() == mSelectedItemSize.size() &&
                mFolderAdapter.getCurrentList().size() == mSelectedItemFolder.size());
        mEditMenuIcon.setVisible(mSelectedItemSize.size() == 0 && mSelectedItemFolder.size() == 1);
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
            if (viewHolder != null)
                viewHolder.itemView.findViewById(R.id.grid_card_view).callOnClick();
        }
    }

    //&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&


    private void goInputOutputFragment() {
        Navigation.findNavController(requireView()).navigate(R.id.action_listFragment_to_inputOutputFragment);
    }

    //dialog----------------------------------------------------------------------------------------
    @SuppressLint("ShowToast")
    private void showAddFolderDialog() {
        setDialogEditText("");
        MaterialAlertDialogBuilder builder = getDialogBuilder();
        builder.setTitle("폴더 생성")
                .setPositiveButton("확인", (dialog, which) -> {
                    int largestOrder = mModel.getFolderLargestOrder();
                    largestOrder++;
                    mModel.insertFolder(new Folder(mModel.getCurrentTime(), String.valueOf(mDialogEditTextBinding.editTextDialog.getText()),
                            mFolderId,
                            largestOrder,
                            "0"));
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

    private View asd() {
        TreeNode root = TreeNode.root();
        List<Category> categoryList = mModel.getAllCategoryList();
        List<Folder> folderList = mModel.getAllFolderList();
        for (Category category : categoryList) {
            if (mActivityModel.getCategory().getParentCategory().equals(category.getParentCategory())) {
                TreeNode categoryTreeNode = new TreeNode(new TreeHolderCategory.IconTreeHolder(category.getCategory())).setViewHolder(new TreeHolderCategory(requireContext()));
                root.addChild(categoryTreeNode);
                for (Folder folder : folderList) {
                    if (category.getId() == folder.getFolderId()) {
                        TreeNode folderTreeNode = new TreeNode(new TreeHolderFolder.IconTreeHolder(folder.getFolderName(), folder, mModel, 40)).setViewHolder(new TreeHolderFolder(requireContext()));
                        categoryTreeNode.addChild(folderTreeNode);
                    }
                }
            }
        }
        AndroidTreeView treeView = new AndroidTreeView(requireContext(), root);
        treeView.setDefaultAnimation(false);
        treeView.setUseAutoToggle(false);
        treeView.setDefaultNodeClickListener((node, value) -> {
            Toast.makeText(requireContext(), "노드 클릭됨", Toast.LENGTH_SHORT).show();
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
        if (mActivityModel.getFolderList().size() != 0)
            mActivityModel.getFolderList().remove(mActivityModel.getFolderList().size() - 1);
        return false;
    }
    //----------------------------------------------------------------------------------------------

    //dragSelect------------------------------------------------------------------------------------
    private final DragSelectTouchListener.OnDragSelectListener mDragSelectListenerSize = (i, i1, b) -> {
        if (mViewType == LISTVIEW) cardViewCallOnClick(i);
        else cardViewCallOnClick(i, i1);
    };
    private final DragSelectTouchListener.OnDragSelectListener mDragSelectListenerFolder = (i, i1, b) -> {
        RecyclerView.ViewHolder viewHolder = mBinding.recyclerFolder.findViewHolderForLayoutPosition(i);
        if (viewHolder != null)
            viewHolder.itemView.findViewById(R.id.folder_card_view).callOnClick();
    };

    private final DragSelectTouchListener mSelectListenerSize = new DragSelectTouchListener().withSelectListener(mDragSelectListenerSize);
    private final DragSelectTouchListener mSelectListenerFolder = new DragSelectTouchListener().withSelectListener(mDragSelectListenerFolder);
    //----------------------------------------------------------------------------------------------
}

