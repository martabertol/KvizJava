package com.example.triviaquizjava;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import android.widget.TextView;

import android.app.AlertDialog;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.text.InputType;

public class ResultsAdapter
        extends RecyclerView.Adapter<ResultsAdapter.ViewHolder> {

    private final List<QuizResult> results;

    private final AppDatabase db;

    public ResultsAdapter(List<QuizResult> results, AppDatabase db) {
        this.results = results;
        this.db = db;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_result, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder, int position) {

        QuizResult result = results.get(position);

        holder.txtPlayerName.setText(result.playerName);
        holder.txtScore.setText(result.score + "/10");
        holder.itemView.setOnClickListener(v -> {

            EditText inputName = new EditText(v.getContext());
            inputName.setText(result.playerName);
            inputName.setHint("Ime igrača");

            EditText inputScore = new EditText(v.getContext());
            inputScore.setText(String.valueOf(result.score));
            inputScore.setHint("Broj bodova");
            inputScore.setInputType(InputType.TYPE_CLASS_NUMBER);

            LinearLayout layout = new LinearLayout(v.getContext());
            layout.setOrientation(LinearLayout.VERTICAL);

            int padding = (int) (20 * v.getResources()
                    .getDisplayMetrics().density);
            layout.setPadding(padding, 0, padding, 0);

            layout.addView(inputName);
            layout.addView(inputScore);

            AlertDialog dialog = new AlertDialog.Builder(v.getContext())
                    .setTitle("Uredi rezultat")
                    .setView(layout)
                    .setPositiveButton("Spremi", null)
                    .setNegativeButton("Odustani", null)
                    .create();

            dialog.setOnShowListener(d ->
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                            .setOnClickListener(button -> {

                                String name = inputName.getText().toString().trim();
                                String scoreText = inputScore.getText().toString().trim();

                                if (name.isEmpty()) {
                                    inputName.setError("Unesi ime");
                                    return;
                                }

                                if (scoreText.isEmpty()) {
                                    inputScore.setError("Unesi bodove");
                                    return;
                                }

                                int score = Integer.parseInt(scoreText);

                                if (score < 0 || score > 10) {
                                    inputScore.setError("Bodovi moraju biti od 0 do 10");
                                    return;
                                }

                                result.playerName = name;
                                result.score = score;

                                new Thread(() -> {
                                    db.quizResultDao().update(result);

                                    holder.itemView.post(() -> {
                                        notifyItemChanged(holder.getBindingAdapterPosition());
                                    });
                                }).start();

                                dialog.dismiss();
                            }));

            dialog.show();
        });
        holder.itemView.setOnLongClickListener(v -> {
            new androidx.appcompat.app.AlertDialog.Builder(v.getContext())
                    .setTitle("Brisanje rezultata")
                    .setMessage("Želite li obrisati ovaj rezultat?")
                    .setPositiveButton("Obriši", (dialog, which) -> {
                        new Thread(() -> {
                            AppDatabase db = androidx.room.Room.databaseBuilder(
                                    v.getContext().getApplicationContext(),
                                    AppDatabase.class,
                                    "trivia_database"
                            ).build();

                            db.quizResultDao().delete(result);
                        }).start();
                    })
                    .setNegativeButton("Odustani", null)
                    .show();

            return true;
        });
    }

    @Override
    public int getItemCount() {
        return results.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtPlayerName;
        TextView txtScore;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtPlayerName =
                    itemView.findViewById(R.id.txtPlayerName);

            txtScore =
                    itemView.findViewById(R.id.txtScore);
        }
    }
}

