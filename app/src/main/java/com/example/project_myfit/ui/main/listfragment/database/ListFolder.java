package com.example.project_myfit.ui.main.listfragment.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.List;

@Entity
public class ListFolder {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String folderName;
    private List<Size> sizeList;

    public ListFolder(String folderName, List<Size> sizeList) {
        this.folderName = folderName;
        this.sizeList = sizeList;
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

    public List<Size> getSizeList() {
        return sizeList;
    }

    public void setSizeList(List<Size> sizeList) {
        this.sizeList = sizeList;
    }

    @Override
    public String toString() {
        return "ListFolder{" +
                "id=" + id +
                ", folderName='" + folderName + '\'' +
                '}';
    }
}
