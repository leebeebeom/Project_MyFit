package com.leebeebeom.closetnote.data.tuple.tuple;

import androidx.room.Ignore;

import com.leebeebeom.closetnote.data.model.model.Category;
import com.leebeebeom.closetnote.data.tuple.BaseTuple;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class CategoryTuple extends BaseTuple {
    @Ignore
    private int contentSize;

    public CategoryTuple(Category category) {
        super(category);
        this.contentSize = category.getContentSize();
    }
}
