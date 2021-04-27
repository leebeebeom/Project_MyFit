package com.example.project_myfit.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Objects;

@Entity
public class RecentSearch {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String word, date;
    private boolean isRecycleBin;

    public RecentSearch(String word, String date, boolean isRecycleBin) {
        this.word = word;
        this.date = date;
        this.isRecycleBin = isRecycleBin;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public boolean isRecycleBin() {
        return isRecycleBin;
    }

    public void setRecycleBin(boolean recycleBin) {
        isRecycleBin = recycleBin;
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
