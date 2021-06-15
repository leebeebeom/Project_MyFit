package com.example.myfit.util;

public class ListenerUtil {
//    public static void recentSearchClick(String word, @NotNull MaterialAutoCompleteTextView autoCompleteTextView, @NotNull TextInputLayout textInputLayout, Context context, int type) {
//        autoCompleteTextView.setText(word);
//        autoCompleteTextView.setSelection(word.length());
//        autoCompleteTextView.dismissDropDown();
//        textInputLayout.setEndIconVisible(false);
//        KeyBoardUtil.hideKeyBoard(autoCompleteTextView);

//        Repository repository = new Repository(context);
//        repository.getRecentSearchRepository().insertRecentSearch(word, type);
//}

//    public static void autoCompleteImeClick(@NotNull MaterialAutoCompleteTextView autoCompleteTextView, int recentSearchType, Context context) {
//        autoCompleteTextView.setOnEditorActionListener((v, actionId, event) -> {
//            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
//                CommonUtil.keyBoardHide(context, autoCompleteTextView);
//
//                autoCompleteTextView.dismissDropDown();
//
//                String word = autoCompleteTextView.getText().toString().trim();
//
//                if (!TextUtils.isEmpty(word)) {
//                    Repository repository = new Repository(context);
//                    repository.getRecentSearchRepository().insertRecentSearch(word, recentSearchType);
//                }
//            }
//            return false;
//        });
//    }

//    public static void autoCompleteEndIconClick(MaterialAutoCompleteTextView autoCompleteTextView, @NotNull TextInputLayout textInputLayout, Context context) {
//        textInputLayout.setEndIconOnClickListener(v -> {
//            textInputLayout.setEndIconVisible(false);
//            autoCompleteTextView.setText("");
//            autoCompleteTextView.postDelayed(() -> KeyBoardUtil.showKeyBoard(autoCompleteTextView), 50);
//        });
//    }

//    public static void autoCompleteItemClick(@NotNull MaterialAutoCompleteTextView autoCompleteTextView, int recentSearchType, Context context) {
//        autoCompleteTextView.setOnItemClickListener((parent, view, position, id) -> {
//            autoCompleteTextView.clearFocus();
//            KeyBoardUtil.hideKeyBoard(autoCompleteTextView);
//
//            String word = String.valueOf(autoCompleteTextView.getText());
//            Repository repository = new Repository(context);
//            repository.getRecentSearchRepository().insertRecentSearch(word, recentSearchType);
//        });
//    }
}

