package com.example.myfit.data.model.category;

import androidx.room.Entity;

import com.example.myfit.data.model.BaseInfo;
import com.example.myfit.data.model.BaseModel;

import java.util.Objects;

@Entity
public class Category extends BaseModel {
    private String name;
    private int contentsSize;

    public Category(BaseInfo baseInfo, String name) {
        super(baseInfo);
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getContentsSize() {
        return contentsSize;
    }

    public void setContentsSize(int contentsSize) {
        this.contentsSize = contentsSize;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Category)) return false;
        if (!super.equals(o)) return false;
        Category category = (Category) o;
        return getContentsSize() == category.getContentsSize() &&
                getName().equals(category.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getName(), getContentsSize());
    }
}
