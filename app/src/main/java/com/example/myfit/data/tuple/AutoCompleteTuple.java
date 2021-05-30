package com.example.myfit.data.tuple;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public class AutoCompleteTuple {
    private long id;
    private String word;
    private boolean deleted;
}
