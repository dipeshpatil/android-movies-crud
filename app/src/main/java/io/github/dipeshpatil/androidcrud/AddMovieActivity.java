package io.github.dipeshpatil.androidcrud;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.imangazaliev.slugify.Slugify;
import com.squareup.picasso.Picasso;
import com.theophrast.ui.widget.SquareImageView;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import io.github.dipeshpatil.androidcrud.Movies.Movie;

public class AddMovieActivity extends AppCompatActivity {
    private EditText addMovieTitle;
    private Button fetchButton, resultButtonAdd;
    private LinearLayout layout;
    private TextView resultMovieName, resultMoviePlot;
    private SquareImageView resultMoviePoster;

    private FirebaseAuth auth;
    private FirebaseUser user;

    private FirebaseFirestore db;
    private DatabaseHelper databaseHelper;

    private static final String API_KEY = "e2716675";
    private final String URL = "https://www.omdbapi.com/?apikey=" + API_KEY + "&t=";

    private Map<String, Object> movieMap;

    private String currentUserUID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_movie);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        currentUserUID = user.getUid();

        db = FirebaseFirestore.getInstance();
        databaseHelper = new DatabaseHelper(this);

        addMovieTitle = findViewById(R.id.add_movie_title);

        fetchButton = findViewById(R.id.fetch_button);
        resultButtonAdd = findViewById(R.id.result_button_add);

        layout = findViewById(R.id.resultLayout);

        resultMovieName = findViewById(R.id.result_movie_name);
        resultMoviePlot = findViewById(R.id.result_movie_plot);
        resultMoviePoster = findViewById(R.id.result_movie_poster);

        ProgressDialog dataDialog = new ProgressDialog(this);
        dataDialog.setMessage("Fetching");
        dataDialog.setCanceledOnTouchOutside(false);

        movieMap = new HashMap<>();

        fetchButton.setOnClickListener(v -> {
            dataDialog.show();
            String title = addMovieTitle.getText().toString();
            String requestURL = URL + URLEncoder.encode(title);
            StringRequest request = new StringRequest(requestURL,
                    response -> {
                        Log.d("VOLLEY_RESPONSE", response);
                        GsonBuilder builder = new GsonBuilder();
                        Gson gson = builder.create();
                        Movie movie = gson.fromJson(response, Movie.class);

                        if (movie.getResponse().equals("True")) {
                            dataDialog.dismiss();

                            String movieTitleText = movie.getTitle();
                            String moviePlotText = movie.getPlot();
                            String movieRatingText = movie.getImdbRating();
                            String moviePosterURL = movie.getPoster();
                            String movieGenreText = movie.getGenre();
                            String movieYearText = movie.getYear();
                            String movieReleased = movie.getReleased();
                            String movieActors = movie.getActors();
                            String movieDirector = movie.getDirector();

                            resultMovieName.setText(movieTitleText);
                            resultMoviePlot.setText(moviePlotText);
                            Picasso.get().load(moviePosterURL).into(resultMoviePoster);
                            layout.setVisibility(View.VISIBLE);

                            Slugify slugify = new Slugify();
                            String title_slug = slugify.slugify(movieTitleText.toLowerCase());

                            movieMap.put("title_slug", title_slug);
                            movieMap.put("title", movieTitleText);
                            movieMap.put("plot", moviePlotText);
                            movieMap.put("rating", movieRatingText);
                            movieMap.put("poster", moviePosterURL);
                            movieMap.put("genre", movieGenreText);
                            movieMap.put("year", movieYearText);
                            movieMap.put("released", movieReleased);
                            movieMap.put("actors", movieActors);
                            movieMap.put("directors", movieDirector);

                        } else {
                            Toast.makeText(this, "Not Found!", Toast.LENGTH_SHORT).show();
                            dataDialog.dismiss();
                        }
                    },
                    error -> {
                        Log.d("VOLLEY_ERROR", error.toString());
                        dataDialog.hide();
                    }
            );

            RequestQueue queue = Volley.newRequestQueue(AddMovieActivity.this);
            queue.add(request);
        });

        resultButtonAdd.setOnClickListener(v -> {
            dataDialog.setMessage("Adding");
            dataDialog.setCanceledOnTouchOutside(false);

            db.collection(currentUserUID)
                    .document(movieMap.get("title_slug").toString())
                    .set(movieMap)
                    .addOnCompleteListener(task -> {

                        if (!databaseHelper.alreadyExists(movieMap.get("title_slug").toString())) {
                            boolean success = databaseHelper.insertData(
                                    movieMap.get("title").toString(),
                                    movieMap.get("plot").toString(),
                                    movieMap.get("rating").toString(),
                                    movieMap.get("poster").toString(),
                                    movieMap.get("genre").toString(),
                                    movieMap.get("year").toString(),
                                    movieMap.get("released").toString(),
                                    movieMap.get("actors").toString(),
                                    movieMap.get("directors").toString(),
                                    movieMap.get("title_slug").toString()
                            );
                            if (success)
                                Toast.makeText(AddMovieActivity.this, "Success!", Toast.LENGTH_SHORT).show();
                            else
                                Toast.makeText(AddMovieActivity.this, "SQLite Failed!", Toast.LENGTH_LONG).show();
                        } else
                            Toast.makeText(AddMovieActivity.this, "Already Exists!", Toast.LENGTH_SHORT).show();
                        dataDialog.dismiss();
                    }).addOnFailureListener(e -> {
                Toast.makeText(this, "Failed to Add!", Toast.LENGTH_SHORT).show();
                dataDialog.hide();
            });
        });
    }
}