package com.example.project_myfit.ui.main.database;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.chad.library.adapter.base.entity.node.BaseNode;
import com.example.project_myfit.R;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

//엔티티
@Entity
public class ChildCategory extends BaseNode {
    @Ignore
    public static final String SHORT_SLEEVE = "SHORT SLEEVE";
    @Ignore
    public static final String LONG_SLEEVE = "LONG SLEEVE";
    @Ignore
    public static final String SHORT_PANTS = "SHORT PANTS";
    @Ignore
    public static final String LONG_PANTS = "LONG PANTS";
    @Ignore
    public static final String SKIRT = "SKIRT";
    @Ignore
    public static final String ONE_PIECE = "ONE PIECE";
    @Ignore
    public static final String OUTER = "OUTER";
    @Ignore
    public static final String COAT = "COAT";

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String childCategory;
    private String parentCategory;

    @Ignore
    private Map<String, Integer> map;

    public ChildCategory(String childCategory, String parentCategory) {
        this.childCategory = childCategory;
        this.parentCategory = parentCategory;
    }

    public Integer getImage() {
        map = new HashMap<>();
        //상의
        map.put(SHORT_SLEEVE, R.drawable.category_shortsleeve);
        map.put(LONG_SLEEVE, R.drawable.category_longsleeve);
        //하의
        map.put(SHORT_PANTS, R.drawable.category_shortpants);
        map.put(LONG_PANTS, R.drawable.category_longpants);
        map.put(SKIRT, R.drawable.category_skirt);
        map.put(ONE_PIECE, R.drawable.category_onepiece);
        //아우터
        map.put(OUTER, R.drawable.category_outer);
        map.put(COAT, R.drawable.category_coat);
        return map.get(childCategory);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getChildCategory() {
        return childCategory;
    }

    public void setChildCategory(String childCategory) {
        this.childCategory = childCategory;
    }

    public String getParentCategory() {
        return parentCategory;
    }

    public void setParentCategory(String parentCategory) {
        this.parentCategory = parentCategory;
    }

    @NotNull
    @Override
    public String toString() {
        return "Clothing{" +
                "clothing='" + childCategory + '\'' +
                '}';
    }

    @Nullable
    @Override
    public List<BaseNode> getChildNode() {
        return null;
    }
}
