package com.example.myfit.data.tuple.tuple;

import com.example.myfit.data.tuple.BaseTuple;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public class SizeTuple extends BaseTuple {
    private String brand, imageUri;
    private boolean favorite;
}
