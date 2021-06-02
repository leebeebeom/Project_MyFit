package com.example.myfit.data.tuple;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BaseTuple {
    private long id;
    private int parentIndex, sortNumber;
    private String name;
    private long deletedTime;
}
