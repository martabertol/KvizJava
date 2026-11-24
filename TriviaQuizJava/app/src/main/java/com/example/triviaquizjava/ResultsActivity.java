package com.example.triviaquizjava;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;

import androidx.room.Room;

public class ResultsActivity extends AppCompatActivity {

    private RecyclerView recyclerResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        recyclerResults = findViewById(R.id.recyclerResults);

        recyclerResults.setLayoutManager(
                new LinearLayoutManager(this)
        );

        recyclerResults = findViewById(R.id.recyclerResults);
        recyclerResults.setLayoutManager(
                new LinearLayoutManager(this)
        );

        new Thread(() -> {
            AppDatabase db = Room.databaseBuilder(
                    getApplicationContext(),
                    AppDatabase.class,
                    "trivia_database"
            ).build();

            java.util.List<QuizResult> results =
                    db.quizResultDao().getAllResults();

            runOnUiThread(() -> {
                ResultsAdapter adapter = new ResultsAdapter(results, db);
                recyclerResults.setAdapter(adapter);
            });
        }).start();

    }
}

