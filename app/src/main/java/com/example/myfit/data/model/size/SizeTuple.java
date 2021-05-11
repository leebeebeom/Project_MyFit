package com.example.myfit.data.model.size;

import com.example.myfit.data.model.tuple.BaseTuple;

public class SizeTuple extends BaseTuple {
    private String imageUri, brand;
    private boolean isFavorite;

    public SizeTuple(long id, byte parentCategoryIndex) {
        super(id, parentCategoryIndex);
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }
}
