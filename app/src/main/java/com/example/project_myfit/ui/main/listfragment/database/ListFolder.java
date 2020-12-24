package com.example.project_myfit.ui.main.listfragment.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.chad.library.adapter.base.entity.node.BaseExpandNode;
import com.chad.library.adapter.base.entity.node.BaseNode;

import org.jetbrains.annotations.Nullable;

import java.util.List;

@Entity
public class ListFolder extends BaseExpandNode {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String folderName;
    private List<BaseNode> sizeList;

    public ListFolder(String folderName) {
        this.folderName = folderName;

        setExpanded(false);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public List<BaseNode> getSizeList() {
        return sizeList;
    }

    public void setSizeList(List<BaseNode> sizeList) {
        this.sizeList = sizeList;
    }

    @Override
    public String toString() {
        return "ListFolder{" +
                "id=" + id +
                ", folderName='" + folderName + '\'' +
                '}';
    }


    @Nullable
    @Override
    public List<BaseNode> getChildNode() {
        return getSizeList();
    }
}
