package io.github.dipeshpatil.androidcrud;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.squareup.picasso.Picasso;
import com.theophrast.ui.widget.SquareImageView;

import io.github.dipeshpatil.androidcrud.Helpers.DatabaseHelper;

public class DetailActivity extends AppCompatActivity {

    private TextView detailMovieTitle, detailMoviePlot;
    private SquareImageView detailMoviePoster;

    private DatabaseHelper databaseHelper;
    private ChipGroup chipGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        databaseHelper = new DatabaseHelper(this);

        detailMoviePlot = findViewById(R.id.detail_movie_plot);
        detailMovieTitle = findViewById(R.id.detail_movie_title);
        detailMoviePoster = findViewById(R.id.detail_movie_poster);

        chipGroup = findViewById(R.id.detail_chip_group);

        String title = getIntent().getStringExtra("title");

        Cursor data = databaseHelper.getDataByTitle(title);

        data.moveToFirst();
        String moviePlot = data.getString(2);
        String moviePoster = data.getString(4);
        String movieYear = data.getString(6);
        String movieRating = data.getString(3);
        String[] genreArray = data.getString(5).split(",");

        detailMovieTitle.setText(title);
        detailMoviePlot.setText(moviePlot);
        Picasso.get().load(moviePoster).into(detailMoviePoster);

        Chip chip = new Chip(this);
        chip.setText(movieYear);
        chip.setChipBackgroundColorResource(R.color.colorDanger);
        chip.setTextColor(getResources().getColor(R.color.colorLight));
        chipGroup.addView(chip);

        chip = new Chip(this);
        chip.setText(movieRating);
        chip.setChipBackgroundColorResource(R.color.colorSuccess);
        chip.setTextColor(getResources().getColor(R.color.colorLight));
        chipGroup.addView(chip);

        for (String genre : genreArray) {
            Chip genreChip = new Chip(this);
            genreChip.setText(genre.trim());
            genreChip.setChipBackgroundColorResource(R.color.colorWarning);
            genreChip.setTextColor(getResources().getColor(R.color.colorLight));
            chipGroup.addView(genreChip);
        }
    }
}