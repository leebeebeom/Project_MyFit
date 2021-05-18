package com.example.myfit.data.repository;

public abstract class BaseRepository {
    public abstract void deleteOrRestore(long[] ids, boolean isDeleted);
    
    public abstract void changeSort(int sort);
}
