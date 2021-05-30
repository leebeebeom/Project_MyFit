package com.example.myfit.data.model.model.size;

import androidx.room.Embedded;
import androidx.room.Entity;

import com.example.myfit.data.model.BaseInfo;
import com.example.myfit.data.model.BaseModel;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@EqualsAndHashCode
public class Size extends BaseModel {
    @Embedded
    private BaseSizeInfo baseSizeInfo;
    @Embedded
    private DetailSizeInfo detailSizeInfo;
    private long parentId;
    private boolean parentDeleted;

    public Size(BaseInfo baseInfo, long parentId) {
        super(baseInfo);
        this.parentId = parentId;
        this.baseSizeInfo = new BaseSizeInfo();
        this.detailSizeInfo = new DetailSizeInfo();
    }
}
