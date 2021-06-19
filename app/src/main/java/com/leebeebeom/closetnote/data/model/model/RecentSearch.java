package com.leebeebeom.closetnote.data.model.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@EqualsAndHashCode
public class RecentSearch {
    private final String word, date;
    private final int type;
    @PrimaryKey(autoGenerate = true)
    private long id;

    public RecentSearch(String word, String date, int type) {
        this.word = word;
        this.date = date;
        this.type = type;
    }
}
