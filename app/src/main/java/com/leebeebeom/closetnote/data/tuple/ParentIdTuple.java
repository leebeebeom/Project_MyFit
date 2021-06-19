package com.leebeebeom.closetnote.data.tuple;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public class ParentIdTuple {
    private long id, parentId;
}
