package com.example.myfit.data.model;

import androidx.room.Entity;

@Entity
public class RecentSearch extends BaseModel {
    private final String word, date;
    private final int type;

    public RecentSearch(String word, String date, int type) {
        super(new DefaultInfo((byte) -1, -1));
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


}
