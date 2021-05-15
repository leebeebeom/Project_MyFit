package com.example.myfit.data.model;

import androidx.room.Entity;

import java.util.Objects;

@Entity
public class RecentSearch extends BaseModel {
    private final String word, date;
    private final int type;

    public RecentSearch(String word, String date, int type) {
        super(new BaseInfo((byte) -1, -1));
        this.word = word;
        this.date = date;
        this.type = type;
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
        if (!super.equals(o)) return false;
        RecentSearch that = (RecentSearch) o;
        return getType() == that.getType() &&
                getWord().equals(that.getWord()) &&
                getDate().equals(that.getDate());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getWord(), getDate(), getType());
    }
}
