package com.example.myfit.data.tuple;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public class ParentDeletedTuple {
    private long id;
    private boolean deleted, parentDeleted;
}
