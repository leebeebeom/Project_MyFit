package com.example.project_myfit.ui.main.listfragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
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
import com.example.project_myfit.ui.main.listfragment.adapter.FolderAdapter;
import com.example.project_myfit.ui.main.listfragment.adapter.FolderAdapterListener;
import com.example.project_myfit.ui.main.listfragment.adapter.FolderDragCallBack;
import com.example.project_myfit.ui.main.listfragment.adapter.SizeAdapterGrid;
import com.example.project_myfit.ui.main.listfragment.adapter.SizeAdapterList;
import com.example.project_myfit.ui.main.listfragment.adapter.SizeAdapterListener;
import com.example.project_myfit.ui.main.listfragment.adapter.SizeDragCallBackGrid;
import com.example.project_myfit.ui.main.listfragment.adapter.SizeDragCallBackList;
import com.example.project_myfit.ui.main.listfragment.database.Folder;
import com.example.project_myfit.ui.main.listfragment.database.Size;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.michaelflisar.dragselectrecyclerview.DragSelectTouchListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
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
    //TODO 폴더 삭제 후 폴더 없으면 리사이클러뷰 숨기기

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
            mSelectedItemSize = new ArrayList<>();
            mSelectedItemFolder = new ArrayList<>();
            selectAllClick();
            return true;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            //TODO 이동 만들기
            if (item.getItemId() == R.id.list_action_mode_del) {
                showDeleteDialog(mode);
            } else if (item.getItemId() == R.id.list_action_mode_move) {
                Log.d("로그", "onActionItemClicked: 메롱");
            }
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mActionModeTitleBinding.actionModeTitle.setText("");
            mActionModeTitleBinding.actionModeSelectAll.setChecked(false);
            ((ViewGroup) mActionModeTitleBinding.getRoot().getParent()).removeAllViews();
            mFolderAdapter.setActionModeState(ACTION_MODE_OFF);
            if (mViewType == LISTVIEW)
                mSizeAdapterList.setActionModeState(MyFitConstant.ACTION_MODE_OFF);
            else mSizeAdapterGrid.setActionModeState(MyFitConstant.ACTION_MODE_OFF);
        }
    };

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
        mActivityModel = new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);
        mBinding.setCategory(mActivityModel.getCategory());
        mActivityModel.setListFolder(null);
        mActivityModel.setSize(null);
        setActionBarTitle();
    }

    private void setActionBarTitle() {
        mModel.setActionBarTitle(mActivityModel.getCategory().getCategory());
        ActionBar actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
        if (actionBar != null) actionBar.setTitle(mModel.getActionBarTitle());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();

        if (mViewType == LISTVIEW) setListLayout();
        else setGridLayout();

        setClickListener();

        //folder Renew
        mModel.getAllFolder(mActivityModel.getCategory().getId()).observe(getViewLifecycleOwner(), folderList -> {
            mModel.setFolderLargestOrder();
            mFolderAdapter.setItem(folderList);
            if (folderList.size() == 0) {
                mBinding.recyclerFolder.setVisibility(View.GONE);
            } else mBinding.recyclerFolder.setVisibility(View.VISIBLE);
        });

        //size Renew
        mModel.getAllSize(mActivityModel.getCategory().getId()).observe(getViewLifecycleOwner(), sizeList -> {
            mSizeAdapterList.setItem(sizeList);
            mSizeAdapterGrid.setItem(sizeList);
        });

    }

    @Override
    public void onStop() {
        super.onStop();
        if (isFabOpened) {
            mActivityFab.startAnimation(rotateClose);
        }
        SharedPreferences.Editor editor = mPreference.edit();
        editor.putInt(VIEW_TYPE, mViewType);
        editor.apply();
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

        //fab animation
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
                    mActivityModel.setListFolder(folder);
                    Navigation.findNavController(requireView()).navigate(R.id.action_listFragment_to_inFolderFragment);
                } else {
                    actionModeOnClickFolder(folder, position, checkBox);
                }
            }

            @Override
            public void onCardViewLongClick(Folder folder, RecyclerView.ViewHolder holder, MaterialCheckBox checkBox, int position) {
                if (mFolderAdapter.getActionModeState() == ACTION_MODE_NONE || mFolderAdapter.getActionModeState() == ACTION_MODE_OFF) {
                    ((AppCompatActivity) requireActivity()).startSupportActionMode(mActionModeCallback);
                }
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
        } else {
            actionModeOnClickSize(size, checkBox, position, false);
        }
    }

    @Override
    public void onCardViewLongClick(Size size, MaterialCheckBox checkBox, int position) {
        if ((mSizeAdapterList.getActionModeState() == ACTION_MODE_NONE || mSizeAdapterList.getActionModeState() == ACTION_MODE_OFF) && mViewType == LISTVIEW) {
            ((AppCompatActivity) requireActivity()).startSupportActionMode(mActionModeCallback);
        } else if ((mSizeAdapterGrid.getActionModeState() == ACTION_MODE_NONE || mSizeAdapterGrid.getActionModeState() == ACTION_MODE_OFF) && mViewType == GRIDVIEW) {
            ((AppCompatActivity) requireActivity()).startSupportActionMode(mActionModeCallback);
        }
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
    }

    private void actionModeOnClickFolder(Folder folder, int position, MaterialCheckBox checkBox) {
        checkBox.setChecked(!checkBox.isChecked());
        if (checkBox.isChecked()) mSelectedItemFolder.add(folder);
        else mSelectedItemFolder.remove(folder);
        mFolderAdapter.setSelectedPosition(position);
        setActionModeTitle();
        mActionModeTitleBinding.actionModeSelectAll.setChecked(mSizeAdapterGrid.getCurrentList().size() == mSelectedItemSize.size() &&
                mFolderAdapter.getCurrentList().size() == mSelectedItemFolder.size());
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
                viewHolder.itemView.findViewById(R.id.grid_card_view).callOnClick();
            }
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
                            mActivityModel.getCategory().getId(),
                            largestOrder,
                            "0"));
                    Toast.makeText(requireContext(), "추가됨", Toast.LENGTH_SHORT).show();//TODO 위치 조정
                });
        builder.show();
    }

    private void showDeleteDialog(ActionMode mode) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext(), R.style.myAlertDialog)
                .setTitle("확인")
                .setMessage(getSelectedAmount() + "개의 아이템을 삭제하시겠습니까?\n삭제 후 복구할 수 없습니다.")
                .setNegativeButton("취소", null)
                .setPositiveButton("확인", (dialog, which) -> {
                    mModel.deleteFolder(mSelectedItemFolder);
                    mModel.deleteSize(mSelectedItemSize);
                    mode.finish();
                });
        builder.show();
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
        if (mViewType == LISTVIEW) {
            icon.setIcon(R.drawable.icon_list);
        } else {
            icon.setIcon(R.drawable.icon_grid);
        }
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
            return true;
        }
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

