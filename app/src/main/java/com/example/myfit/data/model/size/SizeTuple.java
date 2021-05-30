package com.example.myfit.data.model.size;

import com.example.myfit.data.model.tuple.BaseTuple;

import java.util.Objects;

public class SizeTuple extends BaseTuple {
    private String brand, imageUri;
    private boolean favorite;

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SizeTuple)) return false;
        if (!super.equals(o)) return false;
        SizeTuple sizeTuple = (SizeTuple) o;
        return isFavorite() == sizeTuple.isFavorite() &&
                getBrand().equals(sizeTuple.getBrand()) &&
                Objects.equals(getImageUri(), sizeTuple.getImageUri());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getBrand(), getImageUri(), isFavorite());
    }
}
