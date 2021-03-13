package io.github.dipeshpatil.androidcrud;

import android.database.Cursor;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import io.github.dipeshpatil.androidcrud.Adapters.DashboardAdapter;
import io.github.dipeshpatil.androidcrud.Movies.MovieItem;

public class DashboardActivity extends AppCompatActivity {
    private DatabaseHelper databaseHelper;
    private RecyclerView dashboardRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        databaseHelper = new DatabaseHelper(this);
        Cursor data = databaseHelper.getAllData();

        List<MovieItem> list = new ArrayList<>();
        dashboardRecyclerView = findViewById(R.id.dashboard_recycler_view);

        if (data.getCount() != 0) {
            while (data.moveToNext()) {
                MovieItem item = new MovieItem(
                        data.getString(1),
                        data.getString(4),
                        data.getString(2)
                );
                list.add(item);
            }
        }

        DashboardAdapter adapter = new DashboardAdapter(list);
        dashboardRecyclerView.setAdapter(adapter);
        dashboardRecyclerView.setHasFixedSize(true);
        dashboardRecyclerView.setAdapter(adapter);
    }
}