package com.example.myfit.data.tuple.tuple;

import com.example.myfit.data.tuple.BaseTuple;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class CategoryTuple extends BaseTuple {
    private int contentSize;
}
