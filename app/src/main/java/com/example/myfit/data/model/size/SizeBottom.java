package com.example.myfit.data.model.size;

import androidx.room.Entity;

import com.example.myfit.data.model.DefaultInfo;

@Entity
public class SizeBottom extends BaseSize {
    private String length, waist, thigh, rise, hem;

    public SizeBottom(DefaultInfo defaultInfo, long parentId) {
        super(defaultInfo, parentId);
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public String getWaist() {
        return waist;
    }

    public void setWaist(String waist) {
        this.waist = waist;
    }

    public String getThigh() {
        return thigh;
    }

    public void setThigh(String thigh) {
        this.thigh = thigh;
    }

    public String getRise() {
        return rise;
    }

    public void setRise(String rise) {
        this.rise = rise;
    }

    public String getHem() {
        return hem;
    }

    public void setHem(String hem) {
        this.hem = hem;
    }
}

