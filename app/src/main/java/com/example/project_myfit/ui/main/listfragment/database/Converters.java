package com.example.project_myfit.ui.main.listfragment.database;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class Converters {
    @TypeConverter
    public static String fromSizeList(List<Size> sizeList) {
        if (sizeList == null) {
            return null;
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<Size>>() {
        }.getType();
        String json = gson.toJson(sizeList, type);
        return json;
    }

    @TypeConverter
    public static List<Size> toSizeList(String sizeString) {
        if (sizeString == null) {
            return null;
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<Size>>() {
        }.getType();
        List<Size> sizeList = gson.fromJson(sizeString, type);
        return sizeList;
    }
}
