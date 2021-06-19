package com.leebeebeom.closetnote.ui.recyclebin.search.adapter;

//public class RecycleBinCategoryAdapter extends BaseAdapter<Category, RecyclerView.ViewHolder> implements Filterable {
//    private final CategoryVH.CategoryVHListener mListener;
//    private final RecycleBinSearchViewModel mModel;
//    private List<Category> mAllCategoryList;
//
//    public RecycleBinCategoryAdapter(Context context, CategoryVH.CategoryVHListener listener, RecycleBinSearchViewModel model) {
//        super(new DiffUtil.ItemCallback<Category>() {
//            @Override
//            public boolean areItemsTheSame(@NonNull @NotNull Category oldItem, @NonNull @NotNull Category newItem) {
//                return oldItem.getId() == newItem.getId();
//            }
//
//            @Override
//            public boolean areContentsTheSame(@NonNull @NotNull Category oldItem, @NonNull @NotNull Category newItem) {
//                return true;
//            }
//        }, context);
//        this.mListener = listener;
//        this.mModel = model;
//    }
//
//    public void setItem(List<Category> list, List<Long> folderParentIdList, List<Long> sizeParentIdList, CharSequence word) {
//        super.setItems(folderParentIdList, sizeParentIdList);
//        this.mAllCategoryList = list;
//        setWord(word);
//    }
//
//    public void setWord(CharSequence word) {
//        getFilter().filter(word, count -> {
//            if (mModel.getFilteredListSizeLive().getValue() != null) {
//                Integer[] countArray = mModel.getFilteredListSizeLive().getValue();
//                countArray[0] = count;
//                mModel.getFilteredListSizeLive().setValue(countArray);
//            }
//        });
//    }
//
//    @Override
//    public int getItemViewType(int position) {
//        if (getItemId(position) != -1) return 0;
//        else return 1;
//    }
//
//    @NonNull
//    @NotNull
//    @Override
//    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
//        if (viewType == 0) {
//            ItemCategoryBinding binding = ItemCategoryBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
//            return new CategoryVH(binding, mListener);
//        } else {
//            ItemHeaderBinding binding = ItemHeaderBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
//            return new HeaderVH(binding);
//        }
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder holder, int position) {
//        Category category = getItem(position);
//        if (holder instanceof CategoryVH) {
//            CategoryVH categoryVH = (CategoryVH) holder;
//            categoryVH.bind(category);
//
//            restoreSelectedHashSet();
//            setActionMode(LISTVIEW, categoryVH.getBinding().cardView, categoryVH.getBinding().cb, category.getId(), position);
//            categoryVH.getBinding().iconDragHandle.setVisibility(View.GONE);
//        } else if (holder instanceof HeaderVH)
//            ((HeaderVH) holder).bind(Constant.ParentCategory.values()[category.getParentIndex()].name());
//    }
//
//    @Override
//    public void moveItem(int from, int to) {
//
//    }
//
//    private class RecycleBinSearchCategoryFilter extends Filter {
//
//        @Override
//        protected FilterResults performFiltering(@NotNull CharSequence constraint) {
//            String keyWord = constraint.toString().toLowerCase(Locale.getDefault()).replaceAll("\\p{Z}", "");
//
//            List<List<Category>> filteredList = Arrays.asList(new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
//
//            if (!TextUtils.isEmpty(keyWord)) {
//                for (Category category : mAllCategoryList) {
//                    String categoryName = category.getName().toLowerCase(Locale.getDefault()).replaceAll("\\p{Z}", "");
//                    if (KoreanTextMatcher.isMatch(categoryName, keyWord))
//                        FilterUtil.classify(category, filteredList);
//                }
//            }
//            FilterResults filterResults = new FilterResults();
//            for (int i = 0; i < filteredList.size(); i++)
//                filterResults.count += filteredList.get(i).size();
//
//            for (int i = 0; i < filteredList.size(); i++)
//                if (!filteredList.get(i).isEmpty())
//                    filteredList.get(i).add(0, new Category(-1, null, i, 0));
//
//            submitList(FilterUtil.getCombineList(filteredList));
//            return filterResults;
//        }
//
//        @Override
//        protected void publishResults(CharSequence constraint, FilterResults results) {
//        }
//    }
//
//    @Override
//    public Filter getFilter() {
//        return new RecycleBinSearchCategoryFilter();
//    }
//}
