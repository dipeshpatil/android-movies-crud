package io.github.dipeshpatil.androidcrud;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import io.github.dipeshpatil.androidcrud.Movies.MovieItem;
import io.github.dipeshpatil.androidcrud.MoviesList.MoviesListAdapter;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private TextView threeDots;
    private GoogleSignInClient mGoogleSignInClient;

    private FirebaseFirestore db;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();

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
        else restoreBackups();

        restoreBackups();

        threeDots = findViewById(R.id.three_dots);
        threeDots.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(this, threeDots);
            popupMenu.inflate(R.menu.main_menu);
            popupMenu.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.add_movie:
                        startActivity(new Intent(MainActivity.this, AddMovieActivity.class));
                        return true;
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
        restoreBackups();
    }

    @Override
    protected void onResume() {
        super.onResume();
        restoreBackups();
    }

    private void signOutFromAll() {
        auth.signOut();
        mGoogleSignInClient.signOut().addOnCompleteListener(task -> sendToLogin());
    }

    private void sendToLogin() {
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
        finish();
    }

    private void restoreBackups() {
        List<MovieItem> moviesList = new ArrayList<>();
        Cursor data = databaseHelper.getAllData();

        if (data.getCount() != 0) {
            while (data.moveToNext()) {
                moviesList.add(new MovieItem(
                        data.getString(1),
                        data.getString(4),
                        data.getString(2)
                ));
            }

            RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
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
                        QuerySnapshot document = task.getResult();
                        List<DocumentSnapshot> moviesList = document.getDocuments();
                        boolean flag = true;
                        for (DocumentSnapshot d : moviesList) {
                            String title_slug = d.getString("title_slug");

                            if (!databaseHelper.alreadyExists(title_slug)) {
                                String title = d.getString("title");
                                String plot = d.getString("plot");
                                String rating = d.getString("rating");
                                String poster = d.getString("poster");
                                String genre = d.getString("genre");
                                String year = d.getString("year");
                                String released = d.getString("released");
                                String actors = d.getString("actors");
                                String directors = d.getString("directors");

                                boolean success = databaseHelper.insertData(
                                        title,
                                        plot,
                                        rating,
                                        poster,
                                        genre,
                                        year,
                                        released,
                                        actors,
                                        directors,
                                        title_slug
                                );

                                flag = flag && success;
                            }
                        }

                        if (flag)
                            Toast.makeText(
                                    this,
                                    "Backup Success!",
                                    Toast.LENGTH_LONG
                            ).show();
                        else
                            Toast.makeText(
                                    this,
                                    "Backup Failed!",
                                    Toast.LENGTH_LONG
                            ).show();
                    }
                });
        restoreBackups();
    }
}