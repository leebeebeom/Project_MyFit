package com.leebeebeom.closetnote.data.tuple;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ContentSizeTuple {
    private long parentId;
    private int size;

    public void plusSize(int size){
        this.size = this.size + size;
    }
}
