package com.trevorwiebe.ynab.db.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.trevorwiebe.ynab.db.entities.CategoryEntity;

import java.util.List;

@Dao
public interface CategoryDao {

    @Insert
    void insertCategory(CategoryEntity categoryEntity);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCategoryList(List<CategoryEntity> categoryEntities);

    @Query("SELECT * FROM category WHERE deleted = 0 AND hidden = 0 ORDER BY name")
    List<CategoryEntity> getCategoryList();

    @Query("SELECT * FROM category WHERE id = :id")
    CategoryEntity getCategoryById(String id);

    @Update
    void updateCategory(CategoryEntity categoryEntity);

    @Delete
    void deleteCategory(CategoryEntity categoryEntity);
}
