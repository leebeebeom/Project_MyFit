package com.leebeebeom.closetnote.data.tuple.tuple;

import com.leebeebeom.closetnote.data.model.model.Folder;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class FolderTuple extends CategoryTuple {
    private long parentId;

    public FolderTuple(Folder folder) {
        super(folder);
        this.parentId = folder.getParentId();
    }
}
