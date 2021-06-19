package com.leebeebeom.closetnote.data.tuple;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public class DeletedTuple {
    private long id;
    private boolean deleted;
    private long deletedTime;
}
