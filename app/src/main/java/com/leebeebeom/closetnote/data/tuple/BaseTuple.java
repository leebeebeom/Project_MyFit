package com.leebeebeom.closetnote.data.tuple;

import com.leebeebeom.closetnote.data.model.BaseModel;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BaseTuple {
    private long id;
    private int parentIndex, sortNumber;
    private String name;
    private long deletedTime;

    public BaseTuple(BaseModel baseModel) {
        this.id = baseModel.getId();
        this.parentIndex = baseModel.getParentIndex();
        this.sortNumber = baseModel.getSortNumber();
        this.name = baseModel.getName();
        this.deletedTime = baseModel.getDeletedTime();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BaseTuple)) return false;

        BaseTuple baseTuple = (BaseTuple) o;

        if (getId() != baseTuple.getId()) return false;
        if (getParentIndex() != baseTuple.getParentIndex()) return false;
        if (getDeletedTime() != baseTuple.getDeletedTime()) return false;
        return getName() != null ? getName().equals(baseTuple.getName()) : baseTuple.getName() == null;
    }

    @Override
    public int hashCode() {
        int result = (int) (getId() ^ (getId() >>> 32));
        result = 31 * result + getParentIndex();
        result = 31 * result + (getName() != null ? getName().hashCode() : 0);
        result = 31 * result + (int) (getDeletedTime() ^ (getDeletedTime() >>> 32));
        return result;
    }
}
