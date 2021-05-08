package com.example.project_myfit.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Objects;

@Entity
public class RecentSearch extends ParentModel{
    @PrimaryKey
    private long id;
    private String word, date;
    private int type;

    public RecentSearch(long id, String word, String date, int type) {
        super(id,-1, -1);
        this.id = id;
        this.word = word;
        this.date = date;
        this.type = type;
    }

    public long getId() {
        return id;
    }

    public void setId(long   id) {
        this.id = id;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public int getParentCategoryIndex() {
        return -1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RecentSearch)) return false;
        RecentSearch that = (RecentSearch) o;
        return getId() == that.getId() &&
                Objects.equals(getWord(), that.getWord());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getWord());
    }
}
