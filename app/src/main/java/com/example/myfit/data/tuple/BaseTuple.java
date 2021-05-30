package com.example.myfit.data.tuple;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public class BaseTuple {
    private long id;
    private int parentIndex, orderNumber;
    private String name;
    private long deletedTime;
}
