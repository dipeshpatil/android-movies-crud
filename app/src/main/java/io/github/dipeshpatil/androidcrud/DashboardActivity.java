package io.github.dipeshpatil.androidcrud;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.github.dipeshpatil.androidcrud.Adapters.DashboardAdapter;
import io.github.dipeshpatil.androidcrud.Helpers.DatabaseHelper;
import io.github.dipeshpatil.androidcrud.Movies.MovieItem;

public class DashboardActivity extends AppCompatActivity {
    private DatabaseHelper databaseHelper;
    private RecyclerView dashboardRecyclerView;

    private RadioGroup radioGroupColumn, radioGroupOrder;
    private RadioButton radioButtonColumn, radioButtonOrder;

    private Cursor data;
    private List<MovieItem> list;
    private FirebaseAuth auth;

    private static final HashMap<String, Integer> map = new HashMap<>();

    static {
        map.put("IDASC", DatabaseHelper.BY_ID_ASC);
        map.put("IDDESC", DatabaseHelper.BY_ID_DESC);
        map.put("TitleASC", DatabaseHelper.BY_TITLE_ASC);
        map.put("TitleDESC", DatabaseHelper.BY_TITLE_DESC);
        map.put("RatingASC", DatabaseHelper.BY_RATING_ASC);
        map.put("RatingDESC", DatabaseHelper.BY_RATING_DESC);
        map.put("YearASC", DatabaseHelper.BY_YEAR_ASC);
        map.put("YearDESC", DatabaseHelper.BY_YEAR_DESC);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        auth = FirebaseAuth.getInstance();

        databaseHelper = new DatabaseHelper(this);
        dashboardRecyclerView = findViewById(R.id.dashboard_recycler_view);

        radioGroupColumn = findViewById(R.id.radio_group_column);
        radioGroupOrder = findViewById(R.id.radio_group_order);

        Button fetchButton = findViewById(R.id.dashboard_fetch_btn);

        data = databaseHelper.getAllData(DatabaseHelper.BY_ID_DESC, auth.getCurrentUser().getUid());
        list = new ArrayList<>();

        if (data.getCount() != 0) {
            while (data.moveToNext()) {
                MovieItem item = new MovieItem(
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
                );
                list.add(item);
            }
        }

        DashboardAdapter adapter = new DashboardAdapter(list);
        dashboardRecyclerView.setHasFixedSize(true);
        dashboardRecyclerView.setAdapter(adapter);

        fetchButton.setOnClickListener(v -> {
            int columnID = radioGroupColumn.getCheckedRadioButtonId();
            int orderID = radioGroupOrder.getCheckedRadioButtonId();

            if (columnID == -1 || orderID == -1) {
                Toast.makeText(this, "Select Attributes", Toast.LENGTH_SHORT).show();
            } else {
                radioButtonColumn = findViewById(columnID);
                radioButtonOrder = findViewById(orderID);

                String column = radioButtonColumn.getText().toString();
                String order = radioButtonOrder.getText().toString();

                data = databaseHelper.getAllData(getValue(column + order), auth.getCurrentUser().getUid());
                list.clear();

                if (data.getCount() != 0) {
                    while (data.moveToNext()) {
                        MovieItem item = new MovieItem(
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
                        );
                        list.add(item);
                    }
                }

                dashboardRecyclerView.setAdapter(adapter);
            }
        });
    }

    private int getValue(String key) {
        Integer n = map.get(key);
        return n != null ? n : -1;
    }
}