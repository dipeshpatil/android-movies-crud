package io.github.dipeshpatil.androidcrud;

import android.app.ProgressDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.imangazaliev.slugify.Slugify;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.github.dipeshpatil.androidcrud.Movies.Movie;

public class MainActivity extends AppCompatActivity {
    private static final String API_KEY = "e2716675";
    private FirebaseFirestore db;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = FirebaseFirestore.getInstance();
        databaseHelper = new DatabaseHelper(this);

        EditText movieTitle = findViewById(R.id.movie_title);
        ListView listView = findViewById(R.id.listview);
        Button fetchButton = findViewById(R.id.fetch_button);

        ProgressDialog dataDialog = new ProgressDialog(this);
        dataDialog.setMessage("Fetching");
        dataDialog.setCanceledOnTouchOutside(false);

        String URL = "https://www.omdbapi.com/?apikey=" + API_KEY + "&t=";

        ArrayList<String> arrayList = new ArrayList<>();
        Cursor data = databaseHelper.getAllData();

        if (data.getCount() != 0) {
            while (data.moveToNext()) {
                arrayList.add(
                        data.getString(1)
                );
            }
            ListAdapter listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, arrayList);
            listView.setAdapter(listAdapter);
        }

        fetchButton.setOnClickListener(v -> {
            dataDialog.show();
            String title = movieTitle.getText().toString();
            String requestURL = URL + URLEncoder.encode(title);
            StringRequest request = new StringRequest(requestURL,
                    response -> {
                        Log.d("VOLLEY_RESPONSE", response);
                        GsonBuilder builder = new GsonBuilder();
                        Gson gson = builder.create();
                        Movie movie = gson.fromJson(response, Movie.class);

                        if(movie.getResponse().equals("True")){
                            String movieTitleText = movie.getTitle();
                            String moviePlotText = movie.getPlot();
                            String movieRatingText = movie.getImdbRating();
                            String moviePosterURL = movie.getPoster();
                            String movieGenreText = movie.getGenre();
                            String movieYearText = movie.getYear();
                            String movieReleased = movie.getReleased();
                            String movieActors = movie.getActors();
                            String movieDirector = movie.getDirector();

                            Slugify slugify = new Slugify();
                            String title_slug = slugify.slugify(movieTitleText.toLowerCase());

                            Map<String, Object> movieMap = new HashMap<>();
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

                            db.collection("movies")
                                    .document(title_slug)
                                    .set(movieMap)
                                    .addOnSuccessListener(
                                            documentReference -> {
                                                if (!databaseHelper.alreadyExists(title_slug)) {
                                                    boolean success = databaseHelper.insertData(
                                                            movieTitleText,
                                                            moviePlotText,
                                                            movieRatingText,
                                                            moviePosterURL,
                                                            movieGenreText,
                                                            movieYearText,
                                                            movieReleased,
                                                            movieActors,
                                                            movieDirector,
                                                            title_slug
                                                    );
                                                    if (success)
                                                        Toast.makeText(MainActivity.this, "Success!", Toast.LENGTH_SHORT).show();
                                                    else
                                                        Toast.makeText(MainActivity.this, "SQLite Failed!", Toast.LENGTH_LONG).show();
                                                } else
                                                    Toast.makeText(MainActivity.this, "Already Exists!", Toast.LENGTH_SHORT).show();
                                                dataDialog.dismiss();
                                                movieTitle.setText("");
                                            }
                                    )
                                    .addOnFailureListener(
                                            e -> Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_SHORT).show()
                                    );
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

            RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
            queue.add(request);
        });
    }
}