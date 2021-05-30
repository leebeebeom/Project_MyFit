package com.example.myfit.data.model.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Objects;

@Entity
public class RecentSearch {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private final String word, date;
    private final int type;

    public RecentSearch(String word, String date, int type) {
        this.word = word;
        this.date = date;
        this.type = type;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RecentSearch)) return false;
        RecentSearch that = (RecentSearch) o;
        return getId() == that.getId() &&
                getType() == that.getType() &&
                getWord().equals(that.getWord()) &&
                getDate().equals(that.getDate());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getWord(), getDate(), getType());
    }
}
