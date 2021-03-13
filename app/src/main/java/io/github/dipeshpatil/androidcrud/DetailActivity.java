

package io.github.dipeshpatil.androidcrud;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;
import com.theophrast.ui.widget.SquareImageView;

public class DetailActivity extends AppCompatActivity {

    private TextView detailMovieTitle;
    private TextView detailMoviePlot;
    private SquareImageView detailMoviePoster;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        detailMoviePlot = findViewById(R.id.detail_movie_plot);
        detailMovieTitle = findViewById(R.id.detail_movie_title);
        detailMoviePoster = findViewById(R.id.detail_movie_poster);

        String title = getIntent().getStringExtra("title");
        String plot = getIntent().getStringExtra("plot");
        String poster = getIntent().getStringExtra("poster");

        detailMovieTitle.setText(title);
        detailMoviePlot.setText(plot);

        Picasso.get().load(poster).into(detailMoviePoster);
    }
}