
package com.example.triviaquizjava;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;


import androidx.room.Room;


public class QuizActivity extends AppCompatActivity {

    TextView txtProgress, txtQuestion;
    Button[] buttons = new Button[4];

    JSONArray questions;
    int currentQuestion = 0;
    int score = 0;
    String correctAnswer;

    Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        txtProgress = findViewById(R.id.txtProgress);
        txtQuestion = findViewById(R.id.txtQuestion);

        buttons[0] = findViewById(R.id.btnAnswer1);
        buttons[1] = findViewById(R.id.btnAnswer2);
        buttons[2] = findViewById(R.id.btnAnswer3);
        buttons[3] = findViewById(R.id.btnAnswer4);

        for (Button button : buttons) {
            button.setEnabled(false);
        }

        loadQuestions();
    }

    String decode(String text) {
        return Html.fromHtml(
                text, Html.FROM_HTML_MODE_LEGACY
        ).toString();
    }

    void loadQuestions() {
        new Thread(() -> {
            HttpURLConnection connection = null;

            try {
                URL url = new URL(
                        "https://opentdb.com/api.php?amount=10&type=multiple"
                );

                connection = (HttpURLConnection)
                        url.openConnection();

                connection.setConnectTimeout(10000);
                connection.setReadTimeout(10000);

                BufferedReader reader =
                        new BufferedReader(
                                new InputStreamReader(
                                        connection.getInputStream()
                                )
                        );

                StringBuilder response = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                reader.close();

                JSONObject json =
                        new JSONObject(response.toString());

                if (json.getInt("response_code") != 0) {
                    throw new Exception("API error");
                }

                questions = json.getJSONArray("results");

                runOnUiThread(() -> showQuestion());

            } catch (Exception e) {
                runOnUiThread(() -> {
                    txtQuestion.setText(
                            "Failed to load questions."
                    );
                    Toast.makeText(
                            this,
                            "Check internet connection or try again later.",
                            Toast.LENGTH_LONG
                    ).show();
                });
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }).start();
    }

    void showQuestion() {
        try {
            JSONObject question =
                    questions.getJSONObject(currentQuestion);

            txtProgress.setText(
                    "Question " + (currentQuestion + 1) + "/10"
            );

            txtQuestion.setText(
                    decode(question.getString("question"))
            );

            correctAnswer =
                    decode(question.getString("correct_answer"));

            JSONArray incorrect =
                    question.getJSONArray("incorrect_answers");

            ArrayList<String> answers = new ArrayList<>();

            answers.add(correctAnswer);

            for (int i = 0; i < incorrect.length(); i++) {
                answers.add(decode(incorrect.getString(i)));
            }

            Collections.shuffle(answers);

            for (int i = 0; i < 4; i++) {
                Button button = buttons[i];
                String answer = answers.get(i);

                button.setText(answer);
                button.setEnabled(true);

                button.setOnClickListener(v -> {
                    for (Button b : buttons) {
                        b.setEnabled(false);
                    }

                    if (answer.equals(correctAnswer)) {
                        score++;
                        Toast.makeText(
                                this, "Correct!",
                                Toast.LENGTH_SHORT
                        ).show();
                    } else {
                        Toast.makeText(
                                this,
                                "Wrong! Correct: " + correctAnswer,
                                Toast.LENGTH_SHORT
                        ).show();
                    }

                    handler.postDelayed(() -> {
                        currentQuestion++;

                        if (currentQuestion < 10) {
                            showQuestion();
                        } else {
                            txtProgress.setText("Quiz finished");
                            txtQuestion.setText(
                                    "Your score: " + score + "/10"
                            );
                            QuizResult result = new QuizResult();
                            result.playerName = "Player";
                            result.score = score;

                            new Thread(() -> {
                                AppDatabase db = Room.databaseBuilder(
                                        getApplicationContext(),
                                        AppDatabase.class,
                                        "trivia_database"
                                ).build();

                                db.quizResultDao().insert(result);
                            }).start();
                            for (Button b : buttons) {
                                b.setVisibility(Button.GONE);
                            }
                        }
                    }, 1200);
                });
            }

        } catch (Exception e) {
            txtQuestion.setText("Error displaying question.");
        }
    }
}
