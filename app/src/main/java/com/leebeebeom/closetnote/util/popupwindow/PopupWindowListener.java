package com.leebeebeom.closetnote.util.popupwindow;

public interface PopupWindowListener {
    void sortClick();

    void recycleBinClick();

    interface MainPopupWindowListener extends PopupWindowListener {
        void addCategoryClick();
    }

    interface ListPopupWindowListener extends PopupWindowListener {
        void createFolderClick();
    }
}
