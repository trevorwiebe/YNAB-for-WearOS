package com.trevorwiebe.ynab.db.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.trevorwiebe.ynab.db.entities.CategoryEntity;

import java.util.List;

@Dao
public interface CategoryDao {

    @Insert
    void insertCategory(CategoryEntity categoryEntity);

    @Insert
    void insertCategoryList(List<CategoryEntity> categoryEntities);

    @Query("SELECT * FROM category WHERE deleted = 0 AND hidden = 0")
    List<CategoryEntity> getCategoryList();

    @Query("SELECT * FROM category WHERE id = :id")
    CategoryEntity getCategoryById(String id);

    @Update
    void updateCategory(CategoryEntity categoryEntity);

    @Delete
    void deleteCategory(CategoryEntity categoryEntity);
}
