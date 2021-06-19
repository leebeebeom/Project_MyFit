package com.leebeebeom.closetnote.data.tuple.tuple;

import com.leebeebeom.closetnote.data.model.model.Size;
import com.leebeebeom.closetnote.data.tuple.BaseTuple;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class SizeTuple extends BaseTuple {
    private String brand, imageUri;
    private boolean favorite;

    public SizeTuple(Size size) {
        super(size);
        this.brand = size.getBrand();
        this.imageUri = size.getImageUri() != null ? size.getImageUri() : null;
        this.favorite = size.isFavorite();
    }
}
