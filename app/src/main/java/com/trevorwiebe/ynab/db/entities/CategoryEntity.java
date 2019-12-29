package com.trevorwiebe.ynab.db.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.Keep;

@Keep
@Entity(tableName = "category")
public class CategoryEntity {

    @NonNull
    @PrimaryKey()
    @ColumnInfo(name = "id")
    private String id;

    @ColumnInfo(name = "category_group_id")
    private String category_group_id;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "hidden")
    private int hidden;

    @ColumnInfo(name = "deleted")
    private int deleted;

    public CategoryEntity(@NonNull String id, String category_group_id, String name, int hidden, int deleted) {
        this.id = id;
        this.category_group_id = category_group_id;
        this.name = name;
        this.hidden = hidden;
        this.deleted = deleted;
    }

    public @NonNull String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getCategory_group_id() {
        return category_group_id;
    }

    public void setCategory_group_id(String category_group_id) {
        this.category_group_id = category_group_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getHidden() {
        return hidden;
    }

    public void setHidden(int hidden) {
        this.hidden = hidden;
    }

    public int getDeleted() {
        return deleted;
    }

    public void setDeleted(int deleted) {
        this.deleted = deleted;
    }
}

