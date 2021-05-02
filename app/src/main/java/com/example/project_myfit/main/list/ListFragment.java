package com.example.project_myfit.main.list;

import android.annotation.SuppressLint;
import android.content.Context;
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

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_myfit.MainActivity;
import com.example.project_myfit.R;
import com.example.project_myfit.data.model.Folder;
import com.example.project_myfit.data.model.Size;
import com.example.project_myfit.databinding.ActivityMainBinding;
import com.example.project_myfit.databinding.FragmentListBinding;
import com.example.project_myfit.databinding.LayoutPopupBinding;
import com.example.project_myfit.dialog.DialogViewModel;
import com.example.project_myfit.main.list.adapter.folderadapter.FolderAdapter;
import com.example.project_myfit.main.list.adapter.folderadapter.FolderDragCallBack;
import com.example.project_myfit.main.list.adapter.sizeadapter.SizeAdapterGrid;
import com.example.project_myfit.main.list.adapter.sizeadapter.SizeAdapterList;
import com.example.project_myfit.util.ActionModeImpl;
import com.example.project_myfit.util.ListenerUtil;
import com.example.project_myfit.util.MyFitVariable;
import com.example.project_myfit.util.PopupWindowImpl;
import com.example.project_myfit.util.SelectedItemTreat;
import com.example.project_myfit.util.Sort;
import com.example.project_myfit.util.adapter.DragCallBackGrid;
import com.example.project_myfit.util.adapter.DragCallBackList;
import com.example.project_myfit.util.adapter.viewholder.FolderVHListener;
import com.example.project_myfit.util.adapter.viewholder.SizeListVH;
import com.example.project_myfit.util.adapter.viewholder.SizeVHListener;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textview.MaterialTextView;
import com.michaelflisar.dragselectrecyclerview.DragSelectTouchListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.example.project_myfit.util.MyFitConstant.ACTION_MODE;
import static com.example.project_myfit.util.MyFitConstant.FOLDER;
import static com.example.project_myfit.util.MyFitConstant.GRIDVIEW;
import static com.example.project_myfit.util.MyFitConstant.ITEM_MOVE_CONFIRM;
import static com.example.project_myfit.util.MyFitConstant.LISTVIEW;
import static com.example.project_myfit.util.MyFitConstant.LIST_FRAGMENT;
import static com.example.project_myfit.util.MyFitConstant.NAME_EDIT_CONFIRM;
import static com.example.project_myfit.util.MyFitConstant.SELECTED_ITEM_DELETE_CONFIRM;
import static com.example.project_myfit.util.MyFitConstant.SORT_CONFIRM;
import static com.example.project_myfit.util.MyFitConstant.THIS_FOLDER_TEXT_VIEW;

@SuppressLint("ClickableViewAccessibility")
public class ListFragment extends Fragment implements SizeVHListener, ActionModeImpl.ActionModeListener, PopupWindowImpl.PopupWindowClickListener, FolderVHListener {

    private ListViewModel mModel;
    private FragmentListBinding mBinding;
    private ActivityMainBinding mActivityBinding;
    private ItemTouchHelper mTouchHelperList, mTouchHelperGrid, mTouchHelperFolder;
    private boolean isFolderDragSelecting, isSizeDragSelecting, isFolderStart, isSizeStart, mSizeSelectedState,
            mFolderSelectedState, mNavigationSet, isSearchView;
    private DragSelectTouchListener mSizeDragSelectListener, mFolderDragSelectListener;
    private int mPadding8dp, mPaddingBottom;
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
    private float mNavigationTextViewSize;
    private ActionBar mActionBar;
    private long mThisCategoryId, mThisFolderId, mParentId;
    private String mParentCategory;
    private ActionModeImpl mActionMode;
    private PopupWindowImpl mPopupWindow;

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
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = FragmentListBinding.inflate(inflater);
        mActivityBinding = ((MainActivity) requireActivity()).mBinding;

        adapterInit();
        itemTouchHelperInit();
        rvInit();

        View view = mBinding.getRoot();

        mActionMode = new ActionModeImpl(inflater, R.menu.menu_action_mode, this, mFolderAdapter, mSizeAdapterGrid, mSizeAdapterGrid);

        LayoutPopupBinding binding = LayoutPopupBinding.inflate(inflater);
        binding.tvAddCategory.setVisibility(View.GONE);
        mPopupWindow = new PopupWindowImpl(binding, this);
        return view;
    }

    private void adapterInit() {
        mFolderAdapter = new FolderAdapter(requireContext(), mModel, this);
        mSizeAdapterList = new SizeAdapterList(requireContext(), mModel, this);
        mSizeAdapterGrid = new SizeAdapterGrid(requireContext(), mModel, this);
    }

    private void itemTouchHelperInit() {
        mTouchHelperFolder = new ItemTouchHelper(new FolderDragCallBack(mFolderAdapter));
        mTouchHelperFolder.attachToRecyclerView(mBinding.rvFolder);

        mTouchHelperList = new ItemTouchHelper(new DragCallBackList(mSizeAdapterList));
        mTouchHelperGrid = new ItemTouchHelper(new DragCallBackGrid(mSizeAdapterGrid));
    }

    private void rvInit() {
        mBinding.rvFolder.setAdapter(mFolderAdapter);
        mBinding.rvFolder.addOnItemTouchListener(getFolderDragSelectListener());

        mBinding.rvSize.setHasFixedSize(true);
        mBinding.rvSize.addOnItemTouchListener(getSizeDragSelectListener());
        sizeRvInit();
    }

    private DragSelectTouchListener getFolderDragSelectListener() {
        DragSelectTouchListener.OnAdvancedDragSelectListener dragSelectListenerFolder = new DragSelectTouchListener.OnAdvancedDragSelectListener() {
            @Override
            public void onSelectionStarted(int i) {
                MyFitVariable.isDragSelecting = true;
                isFolderDragSelecting = true;
                mBinding.sv.setScrollable(false);
                if (mFolderStartPosition == -1) folderHolderCallOnClick(i);
                mFolderStartPosition = i;
            }

            @Override
            public void onSelectionFinished(int i) {
                MyFitVariable.isDragSelecting = false;
                isFolderStart = false;
                isFolderDragSelecting = false;
                mBinding.sv.setScrollable(true);
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
                MyFitVariable.isDragSelecting = true;
                isSizeDragSelecting = true;
                mBinding.sv.setScrollable(false);
                if (mSizeStartPosition == -1) sizeHolderCallOnClick(i);
                mSizeStartPosition = i;
            }

            @Override
            public void onSelectionFinished(int i) {
                MyFitVariable.isDragSelecting = false;
                isSizeStart = false;
                isSizeDragSelecting = false;
                mBinding.sv.setScrollable(true);
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
        RecyclerView.ViewHolder viewHolder = mBinding.rvFolder.findViewHolderForLayoutPosition(i);
        if (viewHolder != null) viewHolder.itemView.callOnClick();
    }

    private void sizeHolderCallOnClick(int i) {
        RecyclerView.ViewHolder viewHolder = mBinding.rvSize.findViewHolderForLayoutPosition(i);
        if (viewHolder != null) viewHolder.itemView.callOnClick();
    }

    private void sizeRvInit() {
        if (mPadding8dp == 0) mPadding8dp = (int) getResources().getDimension(R.dimen._8sdp);
        if (mPaddingBottom == 0) mPaddingBottom = (int) getResources().getDimension(R.dimen._60sdp);

        if (mModel.getViewType() == LISTVIEW) sizeRvListInit();
        else sizeRvGridInit();

    }

    private void sizeRvListInit() {
        mBinding.rvSize.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false));
        mBinding.rvSize.setAdapter(mSizeAdapterList);
        mBinding.rvSize.setPadding(0, mPadding8dp, 0, mPaddingBottom);
        mTouchHelperList.attachToRecyclerView(mBinding.rvSize);
    }

    private void sizeRvGridInit() {
        mBinding.rvSize.setLayoutManager(new GridLayoutManager(requireContext(), 2, RecyclerView.VERTICAL, false));
        mBinding.rvSize.setAdapter(mSizeAdapterGrid);
        mBinding.rvSize.setPadding(mPadding8dp, mPadding8dp, mPadding8dp, mPaddingBottom);
        mTouchHelperGrid.attachToRecyclerView(mBinding.rvSize);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (mBinding.sv.getScrollY() == 0) mActivityBinding.fabTop.hide();
        else mActivityBinding.fabTop.show();

        thisCategoryLive();
        thisFolderLive();
        folderLive();
        sizeLive();
        dialogLive();
        actionModeTitleLive();
    }

    private void thisCategoryLive() {
        mModel.getThisCategoryLive(mThisCategoryId).observe(getViewLifecycleOwner(), category -> {
            mBinding.tvCategory.setText(category.getCategoryName());
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
        for (Folder folder : mModel.getFolderHistory(thisFolder)) {
            mBinding.layoutTextNavigation.addView(getArrowIcon());
            mBinding.layoutTextNavigation.addView(getFolderNameTv(folder));
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
            List<Folder> sortFolderList = Sort.folderSort(mModel.getSort(), folderList);

            int size = sortFolderList.size();
            if (size % 4 == 1)
                addFolderDummy(folderList, 3);
            else if (size % 4 == 2)
                addFolderDummy(folderList, 2);
            else if (size % 4 == 3)
                addFolderDummy(folderList, 1);

            mBinding.layoutRvFolder.setVisibility(folderList.size() == 0 ? View.GONE : View.VISIBLE);
            mFolderAdapter.setItem(mModel.getSort(), folderList, mModel.getFolderParentIdList(mParentCategory)
                    , mModel.getSizeParentIdList(mParentCategory));
        });
    }

    private void addFolderDummy(List<Folder> folderList, int count) {
        for (int i = 0; i < count; i++) {
            Folder dummy = new Folder(-1, "dummy", 0, 0, "dummy");
            folderList.add(dummy);
        }
    }

    private void sizeLive() {
        if (mSizeLive != null && mSizeLive.hasObservers())
            mSizeLive.removeObservers(getViewLifecycleOwner());

        mSizeLive = mModel.getSizeLive(mParentId);
        mSizeLive.observe(getViewLifecycleOwner(), sizeList -> {
            if (sizeList.size() == 0) mBinding.layoutNoData.setVisibility(View.VISIBLE);
            else mBinding.layoutNoData.setVisibility(View.GONE);

            List<Size> sortSizeList = Sort.sizeSort(mModel.getSort(), sizeList);

            if (mModel.isFavoriteView())
                sortSizeList.removeIf(size -> !size.isFavorite());

            mSizeAdapterList.setItem(mModel.getSort(), sizeList);
            mSizeAdapterGrid.setItem(mModel.getSort(), sizeList);
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
                else mBinding.tvCategory.setText(parentName);
            }
            if (MyFitVariable.actionMode != null) MyFitVariable.actionMode.finish();
        });
    }

    private void itemMoveLive(SelectedItemTreat selectedItemTreat, @NotNull androidx.navigation.NavBackStackEntry navBackStackEntry) {
        navBackStackEntry.getSavedStateHandle().getLiveData(ITEM_MOVE_CONFIRM).observe(navBackStackEntry, o -> {
            long folderId = (long) o;
            selectedItemTreat.folderSizeMove(false,folderId, mModel.getSelectedFolderList(), mModel.getSelectedSizeList());
            if (MyFitVariable.actionMode != null) MyFitVariable.actionMode.finish();
        });
    }

    private void selectedItemDeleteLive(SelectedItemTreat selectedItemTreat, @NotNull androidx.navigation.NavBackStackEntry navBackStackEntry) {
        navBackStackEntry.getSavedStateHandle().getLiveData(SELECTED_ITEM_DELETE_CONFIRM).observe(navBackStackEntry, o -> {
            selectedItemTreat.folderSizeDelete(false, mModel.getSelectedFolderList(), mModel.getSelectedSizeList());
            if (MyFitVariable.actionMode != null) MyFitVariable.actionMode.finish();
        });
    }

    private void sortLive(@NotNull androidx.navigation.NavBackStackEntry navBackStackEntry) {
        navBackStackEntry.getSavedStateHandle().getLiveData(SORT_CONFIRM).observe(navBackStackEntry, o -> {
            if (o instanceof Integer && mModel.sortChanged((Integer) o)) {
                folderLive();
                sizeLive();
            }
        });
    }

    private void actionModeTitleLive() {
        mModel.getSelectedSizeLive().observe(getViewLifecycleOwner(), integer -> {
            if (MyFitVariable.actionMode != null) {
                mActionMode.getBinding().tvTitle.setText(getString(R.string.action_mode_title, integer));

                int sizeSize = mSizeAdapterList.getCurrentList().size();
                List<Folder> folderList = new ArrayList<>(mFolderAdapter.getCurrentList());
                folderList.removeIf(folder -> folder.getId() == -1);
                int folderSize = folderList.size();
                mActionMode.getBinding().cb.setChecked(sizeSize + folderSize == integer);

                mActionMode.getMenuItemList().get(0).setVisible(integer == 1 && mModel.getSelectedSizeList().size() == 0);
                mActionMode.getMenuItemList().get(1).setVisible(integer > 0);
                mActionMode.getMenuItemList().get(2).setVisible(integer > 0);
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ListenerUtil listenerUtil = new ListenerUtil();
        listenerUtil.scrollChangeListener(mBinding.sv, mActivityBinding.fabTop);
        listenerUtil.fabTopClick(mBinding.sv, mActivityBinding.fabTop);
        folderLayoutToggle();
        folderToggleClick();
        fabClick();
        textNavigationClick();
        folderRvTouch(listenerUtil);
        sizeRvTouch(listenerUtil);

        actionModeRestore(savedInstanceState);
    }

    private void folderLayoutToggle() {
        if (mModel.isFolderToggleOpen()) {
            mBinding.iconToggle.setImageResource(R.drawable.icon_triangle_down);
            mBinding.rvFolder.setVisibility(View.VISIBLE);
        } else {
            mBinding.iconToggle.setImageResource(R.drawable.icon_triangle_left);
            mBinding.rvFolder.setVisibility(View.GONE);
        }
    }

    private void folderToggleClick() {
        mBinding.iconToggle.setOnClickListener(v -> {
            mModel.folderToggleClick();
            folderLayoutToggle();
        });
    }

    private void fabClick() {
        mActivityBinding.fab.setOnClickListener(v ->
                mNavController.navigate(
                        ListFragmentDirections.actionListFragmentToInputOutputFragment(mParentId, 0, mParentCategory)));
    }

    private void textNavigationClick() {
        mBinding.iconHome.setOnClickListener(v -> {
            if (isSearchView)
                mNavController.navigate(ListFragmentDirections.actionListFragmentToMainFragment());
            else mNavController.popBackStack(R.id.mainFragment, false);
        });

        mBinding.tvCategory.setOnClickListener(v -> {
            if (mThisFolderId != 0)
                mNavController.navigate(ListFragmentDirections.actionListFragmentRefresh(mThisCategoryId, 0, mParentCategory));
        });
    }

    private void folderRvTouch(ListenerUtil listenerUtil) {
        mBinding.rvFolder.setOnTouchListener((v, event) -> {
            //folder auto scroll--------------------------------------------------------------------
            if (!isSizeStart && (isFolderDragSelecting || MyFitVariable.isDragging))
                listenerUtil.autoScroll(mBinding.sv, event);
            //folder drag down----------------------------------------------------------------------
            if (isFolderDragSelecting && isFolderStart && event.getY() > v.getBottom() - 50
                    && event.getAction() != MotionEvent.ACTION_UP) {
                //dispatch event
                MotionEvent newEvent = MotionEvent.obtain(event.getDownTime(), event.getEventTime(), event.getAction(), event.getX(),
                        event.getY() - v.getBottom(), event.getMetaState());
                newEvent.recycle();
                mBinding.rvSize.dispatchTouchEvent(newEvent);
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

                RecyclerView.ViewHolder viewHolder = mBinding.rvSize.findViewHolderForLayoutPosition(0);
                if (viewHolder != null) viewHolder.itemView.callOnClick();

                RecyclerView.ViewHolder viewHolder2 = mBinding.rvSize.findViewHolderForLayoutPosition(1);
                if (mModel.getViewType() == GRIDVIEW && viewHolder2 != null &&
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
                mBinding.rvSize.dispatchTouchEvent(event);
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

    private void sizeRvTouch(ListenerUtil listenerUtil) {
        mBinding.rvSize.setOnTouchListener((v, event) -> {
            //size auto scroll----------------------------------------------------------------------
            if (!isFolderStart && (isSizeDragSelecting || MyFitVariable.isDragging))
                listenerUtil.autoScroll(mBinding.sv, event);
            //--------------------------------------------------------------------------------------

            // size drag up-------------------------------------------------------------------------
            if (isSizeDragSelecting && isSizeStart && event.getY() < 0 && event.getAction() != MotionEvent.ACTION_UP) {
                MotionEvent newEvent = MotionEvent.obtain(event.getDownTime(), event.getEventTime(), event.getAction(), event.getX(),
                        event.getY() + mBinding.rvFolder.getBottom(), event.getMetaState());
                newEvent.recycle();
                mBinding.rvFolder.dispatchTouchEvent(newEvent);

                if (!isFolderDragSelecting) {
                    RecyclerView.ViewHolder viewHolder = mBinding.rvSize.findViewHolderForLayoutPosition(0);
                    RecyclerView.ViewHolder viewHolder2 = mBinding.rvSize.findViewHolderForLayoutPosition(1);
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
                mBinding.rvFolder.dispatchTouchEvent(event);
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
        if (mModel.getViewType() == GRIDVIEW && viewHolder != null && viewHolder2 != null) {
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
        if (mModel.getViewType() == GRIDVIEW) {
            RecyclerView.ViewHolder viewHolder = mBinding.rvSize.findViewHolderForLayoutPosition(0);
            RecyclerView.ViewHolder viewHolder2 = mBinding.rvSize.findViewHolderForLayoutPosition(1);
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

    private void actionModeRestore(@org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null && savedInstanceState.getBoolean(ACTION_MODE)) {
            mFolderAdapter.setSelectedItemList(mModel.getSelectedFolderList());
            if (mModel.getViewType() == LISTVIEW)
                mSizeAdapterList.setSelectedItemList(mModel.getSelectedSizeList());
            else mSizeAdapterGrid.setSelectedItemList(mModel.getSelectedSizeList());
            ((AppCompatActivity) requireActivity()).startSupportActionMode(mActionMode);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (MyFitVariable.actionMode != null) MyFitVariable.actionMode.finish();
        mNavigationSet = false;
    }

    //size adapter listener-------------------------------------------------------------------------
    @Override
    public void onSizeItemViewClick(Size size, MaterialCheckBox checkBox) {
        if (MyFitVariable.actionMode == null) {
            mNavController.navigate(ListFragmentDirections.actionListFragmentToInputOutputFragment(
                    size.getParentId(), size.getId(), mParentCategory));
        } else {
            checkBox.setChecked(!checkBox.isChecked());
            if (mModel.getViewType() == LISTVIEW) mSizeAdapterList.itemSelected(size.getId());
            else mSizeAdapterGrid.itemSelected(size.getId());
            mModel.itemSelected(size, checkBox.isChecked());
        }
    }

    @Override
    public void onSizeItemViewLongClick(int position) {
        actionModeStart();
        isSizeStart = true;
        isFolderStart = false;
        mSizeDragSelectListener.startDragSelection(position);
    }

    @Override
    public void onSizeDragHandleTouch(RecyclerView.ViewHolder viewHolder) {
        if (viewHolder instanceof SizeListVH)
            mTouchHelperList.startDrag(viewHolder);
        else mTouchHelperGrid.startDrag(viewHolder);
    }

    @Override
    public void onSizeFavoriteClick(Size size) {
        mModel.sizeFavoriteClick(size);
    }

    //folder adapter click--------------------------------------------------------------------------
    @Override
    public void onFolderItemViewClick(Folder folder, MaterialCheckBox checkBox) {
        if (MyFitVariable.actionMode == null)
            mNavController.navigate(ListFragmentDirections.actionListFragmentRefresh(mThisCategoryId, folder.getId(), mParentCategory));
        else {
            checkBox.setChecked(!checkBox.isChecked());
            mFolderAdapter.itemSelected(folder.getId());
            mModel.itemSelected(folder, checkBox.isChecked());
        }
    }

    @Override
    public void onFolderItemViewLongClick(int position) {
        actionModeStart();
        isFolderStart = true;
        isSizeStart = false;
        mFolderDragSelectListener.startDragSelection(position);
    }

    @Override
    public void onFolderDragHandleTouch(RecyclerView.ViewHolder holder) {
        mTouchHelperFolder.startDrag(holder);
    }

    private void actionModeStart() {
        if (MyFitVariable.actionMode == null) {
            mModel.selectedItemsClear();
            ((AppCompatActivity) requireActivity()).startSupportActionMode(mActionMode);
        }
    }

    //menu------------------------------------------------------------------------------------------
    @Override
    public void onPrepareOptionsMenu(@NonNull @NotNull Menu menu) {
        MenuItem favoriteMenu = menu.getItem(1);
        MenuItem viewTypeMenu = menu.getItem(2);

        favoriteMenu.setIcon(mModel.isFavoriteView() ? R.drawable.icon_favorite : R.drawable.icon_favorite_border);
        viewTypeMenu.setIcon(mModel.getViewType() == LISTVIEW ? R.drawable.icon_list : R.drawable.icon_grid);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull @NotNull Menu menu, @NonNull @NotNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull @NotNull MenuItem item) {
        if (item.getItemId() == R.id.menu_list_view_type) {
            if (mModel.viewTypeClick() == LISTVIEW)
                item.setIcon(R.drawable.icon_grid);
            else item.setIcon(R.drawable.icon_list);
            sizeRvInit();
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
        outState.putBoolean(ACTION_MODE, MyFitVariable.isActionModeOn);
    }

    //action mode-----------------------------------------------------------------------------------
    @Override
    public void selectAllClick(boolean isChecked) {
        mModel.selectAllClick(isChecked, mFolderAdapter, mSizeAdapterList, mSizeAdapterGrid);
    }

    @Override
    public void actionItemClick(int itemId) {
        if (itemId == R.id.menu_action_mode_edit)
            mNavController.navigate(ListFragmentDirections.actionListFragmentToNameEditDialog(mModel.getSelectedFolderId(), FOLDER, false));
        else if (itemId == R.id.menu_action_mode_move) {
            mDialogViewModel.forTreeView(mModel.getSelectedFolderList(), mModel.getSelectedSizeList(), mModel.getFolderHistory3());
            mNavController.navigate(ListFragmentDirections.actionListFragmentToTreeViewDialog(mParentCategory, mThisCategoryId, mThisFolderId));
        } else if (itemId == R.id.menu_action_mode_delete)
            mNavController.navigate(ListFragmentDirections.actionListFragmentToSelectedItemDeleteDialog(mModel.getSelectedItemSize()));
    }

    //popup menu click------------------------------------------------------------------------------
    @Override
    public void addCategoryClick() {
    }

    @Override
    public void createFolderClick() {
        mNavController.navigate(ListFragmentDirections.actionListFragmentToAddDialog(FOLDER, mParentCategory, mParentId));
    }

    @Override
    public void sortClick() {
        mNavController.navigate(ListFragmentDirections.actionListFragmentToSortDialog(mModel.getSort(), LIST_FRAGMENT));
    }

    @Override
    public void recycleBinClick() {
        mNavController.navigate(ListFragmentDirections.actionListFragmentToRecycleBinActivity());
    }
}