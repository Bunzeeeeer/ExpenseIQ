package com.bunzeeeeer.expenseiq.core.domain.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

/**
 *
 * @Author: Lance Joshua Corcega
 * @Date: 03-15-2026
 *
 */
@Entity(
        tableName = "budgets",
        foreignKeys = @ForeignKey(
                entity = Category.class,
                parentColumns = "id",
                childColumns = "categoryId",
                onDelete = ForeignKey.CASCADE
        ),
        indices = {@Index("categoryId")}
)
public class Budget {

    @PrimaryKey(autoGenerate = true)
    private long id;

    private String userId;
    private Long categoryId;
    private double limitAmount;
    private int month;
    private int year;

    public Budget(String userId, Long categoryId, double limitAmount, int month, int year) {
        this.userId = userId;
        this.categoryId = categoryId;
        this.limitAmount = limitAmount;
        this.month = month;
        this.year = year;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }

    public double getLimitAmount() { return limitAmount; }
    public void setLimitAmount(double limitAmount) { this.limitAmount = limitAmount; }

    public int getMonth() { return month; }
    public void setMonth(int month) { this.month = month; }

    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }
}