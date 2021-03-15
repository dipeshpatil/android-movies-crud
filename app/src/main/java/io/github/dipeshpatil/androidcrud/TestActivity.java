package io.github.dipeshpatil.androidcrud;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.theophrast.ui.widget.SquareImageView;

public class TestActivity extends AppCompatActivity {
    Button showAlertDialogButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        showAlertDialogButton = findViewById(R.id.alert_Dialog_trigger);

        showAlertDialogButton.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setPositiveButton("Fetch", (dialog, which) -> {

            }).setNeutralButton("Cancel", (dialog, which) -> {

            });
            final AlertDialog dialog = builder.create();
            LayoutInflater inflater = getLayoutInflater();
            View dialogLayout = inflater.inflate(R.layout.add_movie_layout, null);
            dialog.setView(dialogLayout);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

            dialog.setOnShowListener(d -> {
                EditText dialogMovieEditText = dialog.findViewById(R.id.add_movie_title_dialog);
                Button dialogFetchButton = dialog.findViewById(R.id.fetch_button_dialog);
                LinearLayout layout = dialog.findViewById(R.id.resultLayout_dialog);

                TextView movieTitleView = dialog.findViewById(R.id.result_movie_name_dialog);
                TextView moviePlotView = dialog.findViewById(R.id.result_movie_name_dialog);
                SquareImageView moviePosterView = dialog.findViewById(R.id.result_movie_poster_dialog);

                String title = movieTitleView.getText().toString();

                dialogFetchButton.setOnClickListener(v1 -> {

                });
            });

            dialog.show();
        });
    }
}