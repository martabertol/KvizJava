package com.example.triviaquizjava;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.widget.Button;
import android.widget.Toast;

import android.content.Intent;

public class MainActivity extends AppCompatActivity {

    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        db = Room.databaseBuilder(
                getApplicationContext(),
                AppDatabase.class,
                "trivia_database"
        ).allowMainThreadQueries().build();
        Button btnStart = findViewById(R.id.btnStart);
        Button btnResults = findViewById(R.id.btnResults);


        btnStart.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, QuizActivity.class);
            startActivity(intent);
        });


        btnResults.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ResultsActivity.class);
            startActivity(intent);
        });

    }
}
