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

    @ColumnInfo(name = "goal_type")
    private String goal_type;

    @ColumnInfo(name = "goal_target")
    private long goal_target;

    @ColumnInfo(name = "balance")
    private long balance;

    public CategoryEntity(@NonNull String id, String category_group_id, String name, int hidden, int deleted, String goal_type, long goal_target, long balance) {
        this.id = id;
        this.category_group_id = category_group_id;
        this.name = name;
        this.hidden = hidden;
        this.deleted = deleted;
        this.goal_type = goal_type;
        this.goal_target = goal_target;
        this.balance = balance;
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

    public String getGoal_type() {
        return goal_type;
    }

    public void setGoal_type(String goal_type) {
        this.goal_type = goal_type;
    }

    public long getGoal_target() {
        return goal_target;
    }

    public void setGoal_target(long goal_target) {
        this.goal_target = goal_target;
    }

    public long getBalance() {
        return balance;
    }

    public void setBalance(long balance) {
        this.balance = balance;
    }
}

