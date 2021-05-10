package com.example.myfit.data.model.size;

import androidx.room.Entity;

import com.example.myfit.data.model.DefaultInfo;

import java.util.Objects;

@Entity
public class SizeTop extends BaseSize {
    private String length, shoulder, chest, sleeve;

    public SizeTop(DefaultInfo defaultInfo, long parentId) {
        super(defaultInfo, parentId);
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public String getShoulder() {
        return shoulder;
    }

    public void setShoulder(String shoulder) {
        this.shoulder = shoulder;
    }

    public String getChest() {
        return chest;
    }

    public void setChest(String chest) {
        this.chest = chest;
    }

    public String getSleeve() {
        return sleeve;
    }

    public void setSleeve(String sleeve) {
        this.sleeve = sleeve;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SizeTop)) return false;
        SizeTop sizeTop = (SizeTop) o;
        return getId() == sizeTop.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}

