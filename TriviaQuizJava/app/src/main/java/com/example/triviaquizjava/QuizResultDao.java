
package com.example.triviaquizjava;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;

import java.util.List;

@Dao
public interface QuizResultDao {

    @Insert
    void insert(QuizResult result);

    @Query("SELECT * FROM results")
    List<QuizResult> getAllResults();

    @Update
    void update(QuizResult result);

    @Delete
    void delete(QuizResult result);
}

