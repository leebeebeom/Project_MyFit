package com.example.myfit.data.model.size;

import androidx.room.Entity;

import com.example.myfit.data.model.DefaultInfo;

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
}

