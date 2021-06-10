package com.example.myfit.data.tuple.tuple;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class FolderTuple extends CategoryTuple {
    private long parentId;
}
