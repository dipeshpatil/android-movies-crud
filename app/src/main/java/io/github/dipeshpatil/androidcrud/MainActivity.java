package io.github.dipeshpatil.androidcrud;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.theophrast.ui.widget.SquareImageView;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import io.github.dipeshpatil.androidcrud.Movies.Movie;

public class MainActivity extends AppCompatActivity {
    private static final String API_KEY = "e2716675";
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = FirebaseFirestore.getInstance();

        EditText movieTitle = findViewById(R.id.movie_title);

        Button fetchButton = findViewById(R.id.fetch_button);

        TextView movieName = findViewById(R.id.movie_name);
        SquareImageView moviePoster = findViewById(R.id.movie_poster);

        ProgressDialog dataDialog = new ProgressDialog(this);
        dataDialog.setMessage("Loading");
        dataDialog.setCanceledOnTouchOutside(false);

        String URL = "https://www.omdbapi.com/?apikey=" + API_KEY + "&t=";

        fetchButton.setOnClickListener(v -> {
            dataDialog.show();
            String title = movieTitle.getText().toString();
            String requestURL = URL + URLEncoder.encode(title);
            Toast.makeText(this, requestURL, Toast.LENGTH_SHORT).show();
            StringRequest request = new StringRequest(requestURL,
                    response -> {
                        Log.d("VOLLEY_RESPONSE", response);
                        GsonBuilder builder = new GsonBuilder();
                        Gson gson = builder.create();
                        Movie movie = gson.fromJson(response, Movie.class);

                        String movieNameText = movie.getTitle();
                        String moviePosterURL = movie.getPoster();
                        String movieRating = movie.getImdbRating();

                        Map<String, Object> movieMap = new HashMap<>();
                        movieMap.put("title", movieNameText);
                        movieMap.put("poster", moviePosterURL);
                        movieMap.put("rating", movieRating);

                        movieName.setText(movieNameText);
                        Picasso.get().load(moviePosterURL).into(moviePoster, new Callback() {
                            @Override
                            public void onSuccess() {
                                dataDialog.dismiss();
                                db.collection("movies")
                                        .add(movieMap)
                                        .addOnSuccessListener(
                                                documentReference -> Toast.makeText(MainActivity.this, "Success!", Toast.LENGTH_SHORT).show())
                                        .addOnFailureListener(e -> Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_SHORT).show());
                            }

                            @Override
                            public void onError(Exception e) {
                                Toast.makeText(MainActivity.this, "Poster Doesn't Exist", Toast.LENGTH_LONG)
                                        .show();
                                dataDialog.hide();
                            }
                        });
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