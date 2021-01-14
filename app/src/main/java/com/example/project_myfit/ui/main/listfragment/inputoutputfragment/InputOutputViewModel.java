package com.example.project_myfit.ui.main.listfragment.inputoutputfragment;

import android.app.Application;
import android.net.Uri;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.databinding.BindingAdapter;
import androidx.lifecycle.AndroidViewModel;

import com.example.project_myfit.ui.main.database.AppDataBase;
import com.example.project_myfit.ui.main.listfragment.database.Size;
import com.example.project_myfit.ui.main.listfragment.database.SizeDao;
import com.google.android.material.textfield.TextInputEditText;

public class InputOutputViewModel extends AndroidViewModel {
    private final SizeDao mSizeDao;
    private int mLargestOrder;
    private Size mOldSize;

    public InputOutputViewModel(@NonNull Application application) {
        super(application);
        mSizeDao = AppDataBase.getsInstance(application).sizeDao();
    }

    //dao-------------------------------------------------------------------------------------------
    public Size getOldSize(int id) {
        Thread thread = new Thread(() -> mOldSize = mSizeDao.getSizeById(id));
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return mOldSize;
    }

    public int getLargestOrder() {
        Thread thread = new Thread(() -> mLargestOrder = mSizeDao.getLargestOrder());
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return mLargestOrder;
    }

    public void insert(Size size) {
        new Thread(() -> mSizeDao.insert(size)).start();
    }

    public void update(Size size) {
        new Thread(() -> mSizeDao.update(size)).start();
    }

    public void delete(Size size) {
        size.setDeleted(true);
        new Thread(() -> mSizeDao.update(size)).start();
    }
    //----------------------------------------------------------------------------------------------

    //bindingAdapter--------------------------------------------------------------------------------
    @BindingAdapter("setUriOutput")
    public static void setUriOutput(ImageView imageView, String uriString) {
        if (uriString != null) {
            Uri uri = Uri.parse(uriString);
            imageView.setImageURI(uri);
        }
    }

    @BindingAdapter("android:text")
    public static void setSelection(TextInputEditText editText, String text) {
        if (text != null) {
            editText.setText(text);
            editText.setSelection(text.length());
        }
    }
    //----------------------------------------------------------------------------------------------
}