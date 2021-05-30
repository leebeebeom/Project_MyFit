package com.example.myfit.data.model.model.size;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public class BaseSizeInfo {
    private String createdTime, modifiedTime, imageUri, brand, name, size, link, memo;
    private boolean isFavorite;
}
