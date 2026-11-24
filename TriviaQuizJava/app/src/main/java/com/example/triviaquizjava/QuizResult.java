package com.example.triviaquizjava;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "results")
public class QuizResult {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String playerName;
    public int score;
}

