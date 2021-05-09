package com.example.project_myfit.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Objects;

@Entity
public class RecentSearch implements Model {
    @PrimaryKey
    private final long id;
    private final String word, date;
    private int type;

    public RecentSearch(long id, String word, String date, int type) {
        this.id = id;
        this.word = word;
        this.date = date;
        this.type = type;
    }

    @Override
    public long getId() {
        return id;
    }

    public String getWord() {
        return word;
    }

    public String getDate() {
        return date;
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
    public long getParentId() {
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
