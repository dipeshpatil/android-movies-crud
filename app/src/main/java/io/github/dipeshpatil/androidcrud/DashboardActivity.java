package io.github.dipeshpatil.androidcrud;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.github.dipeshpatil.androidcrud.Adapters.DashboardAdapter;
import io.github.dipeshpatil.androidcrud.Movies.MovieItem;

public class DashboardActivity extends AppCompatActivity {
    private DatabaseHelper databaseHelper;
    private RecyclerView dashboardRecyclerView;

    private RadioGroup radioGroupColumn, radioGroupOrder;
    private RadioButton radioButtonColumn, radioButtonOrder;

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

        databaseHelper = new DatabaseHelper(this);
        dashboardRecyclerView = findViewById(R.id.dashboard_recycler_view);

        radioGroupColumn = findViewById(R.id.radio_group_column);
        radioGroupOrder = findViewById(R.id.radio_group_order);

        Button fetchButton = findViewById(R.id.dashboard_fetch_btn);

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

                Cursor data = databaseHelper.getAllData(getValue(column + order));

                List<MovieItem> list = new ArrayList<>();

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
        });
    }

    private int getValue(String key) {
        Integer n = map.get(key);
        return n != null ? n : -1;
    }
}