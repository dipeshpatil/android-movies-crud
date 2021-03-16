package io.github.dipeshpatil.androidcrud;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.imangazaliev.slugify.Slugify;
import com.squareup.picasso.Picasso;
import com.theophrast.ui.widget.SquareImageView;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.dipeshpatil.androidcrud.Adapters.MoviesListAdapter;
import io.github.dipeshpatil.androidcrud.Helpers.DatabaseHelper;
import io.github.dipeshpatil.androidcrud.Movies.Movie;
import io.github.dipeshpatil.androidcrud.Movies.MovieItem;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private TextView threeDots;
    private GoogleSignInClient mGoogleSignInClient;

    private FirebaseFirestore db;
    private DatabaseHelper databaseHelper;

    private static final String API_KEY = "e2716675";
    private final String URL = "https://www.omdbapi.com/?apikey=" + API_KEY + "&t=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();

        FloatingActionButton actionButton = findViewById(R.id.add_floating_btn);
        actionButton.setOnClickListener(v -> startAlertDialogForAddMovie());

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        db = FirebaseFirestore.getInstance();
        databaseHelper = new DatabaseHelper(this);

        if (databaseHelper.getCount() == 0)
            buildUpOfflineBase(db, auth, databaseHelper);

        loadFromSQLite();

        threeDots = findViewById(R.id.three_dots);
        threeDots.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(this, threeDots);
            popupMenu.inflate(R.menu.main_menu);
            popupMenu.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.dashboard:
                        startActivity(new Intent(MainActivity.this, DashboardActivity.class));
                        return true;
                    case R.id.signout:
                        signOutFromAll();
                        return true;
                    default:
                        return false;
                }
            });
            popupMenu.show();
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadFromSQLite();
    }

    private void signOutFromAll() {
        auth.signOut();
        mGoogleSignInClient.signOut().addOnCompleteListener(task -> sendToLogin());
    }

    private void sendToLogin() {
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
        finish();
    }

    private void loadFromSQLite() {
        List<MovieItem> moviesList = new ArrayList<>();
        Cursor data = databaseHelper.getAllData(DatabaseHelper.BY_ID_DESC);

        if (data.getCount() != 0) {
            while (data.moveToNext()) {
                moviesList.add(new MovieItem(
                        data.getString(1),
                        data.getString(2),
                        data.getString(3),
                        data.getString(4),
                        data.getString(5),
                        data.getString(6),
                        data.getString(7),
                        data.getString(8),
                        data.getString(9),
                        data.getString(10)
                ));
            }

            RecyclerView recyclerView = findViewById(R.id.recyclerView);
            MoviesListAdapter adapter = new MoviesListAdapter(moviesList);
            recyclerView.setHasFixedSize(true);
            recyclerView.setAdapter(adapter);
        }
    }

    private void buildUpOfflineBase(
            FirebaseFirestore db,
            FirebaseAuth auth,
            DatabaseHelper databaseHelper) {
        db.collection(auth.getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot snapshots = task.getResult();
                        List<DocumentSnapshot> moviesList = snapshots.getDocuments();
                        boolean flag = true;

                        for (DocumentSnapshot d : moviesList) {
                            String title_slug = d.getString("title_slug");
                            MovieItem item = d.toObject(MovieItem.class);
                            if (!databaseHelper.alreadyExists(title_slug)) {
                                boolean success = databaseHelper.insertData(item);
                                flag = flag && success;
                            }
                        }

                        Toast.makeText(this,
                                flag ? "Backup Success, Restart the Application" : "Backup Failed!",
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
        loadFromSQLite();
    }

    private void startAlertDialogForAddMovie() {
        Map<String, Object> movieMap = new HashMap<>();
        databaseHelper = new DatabaseHelper(this);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setNeutralButton("Cancel", (dialog, which) -> {
        });

        final AlertDialog dialog = builder.create();
        LayoutInflater inflater = getLayoutInflater();
        View dialogLayout = inflater.inflate(R.layout.add_movie_layout, null);
        dialog.setView(dialogLayout);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false);

        dialog.setOnShowListener(d -> {
            EditText dialogMovieEditText = dialog.findViewById(R.id.add_movie_title_dialog);
            Button dialogFetchButton = dialog.findViewById(R.id.fetch_button_dialog);

            dialogFetchButton.setOnClickListener(v1 -> {
                LinearLayout layout = dialog.findViewById(R.id.resultLayout_dialog);

                TextView dialogMovieTitle = dialog.findViewById(R.id.result_movie_name_dialog);
                TextView dialogMoviePlot = dialog.findViewById(R.id.result_movie_plot_dialog);
                SquareImageView dialogPosterView = dialog.findViewById(R.id.result_movie_poster_dialog);

                String title = dialogMovieEditText.getText().toString();

                String requestURL = URL + URLEncoder.encode(title);
                StringRequest request = new StringRequest(requestURL,
                        response -> {
                            Log.d("VOLLEY_RESPONSE", response);
                            GsonBuilder gsonBuilder = new GsonBuilder();
                            Gson gson = gsonBuilder.create();
                            Movie movie = gson.fromJson(response, Movie.class);

                            if (movie.getResponse().equals("True")) {
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

                                dialogMovieTitle.setText(movieTitleText);
                                dialogMoviePlot.setText(moviePlotText);
                                Picasso.get().load(moviePosterURL).into(dialogPosterView);
                                layout.setVisibility(View.VISIBLE);
                            } else
                                Toast.makeText(this, "Not Found!", Toast.LENGTH_SHORT).show();
                        },
                        error -> Log.d("VOLLEY_ERROR", error.toString())
                );
                RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
                queue.add(request);
            });

            Button resultButtonAddDialog = dialog.findViewById(R.id.result_button_add_dialog);
            resultButtonAddDialog.setOnClickListener(v -> {
                String currentUserUID = auth.getCurrentUser().getUid();
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
                                if (success) {
                                    loadFromSQLite();
                                    dialog.dismiss();
                                }
                            }
                        });
            });
        });

        dialog.show();
    }
}