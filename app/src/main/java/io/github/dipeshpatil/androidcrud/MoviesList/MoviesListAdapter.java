package io.github.dipeshpatil.androidcrud.MoviesList;

import android.content.Intent;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.squareup.picasso.Picasso;
import com.theophrast.ui.widget.SquareImageView;

import java.util.List;

import io.github.dipeshpatil.androidcrud.DatabaseHelper;
import io.github.dipeshpatil.androidcrud.DetailActivity;
import io.github.dipeshpatil.androidcrud.Movies.MovieItem;
import io.github.dipeshpatil.androidcrud.R;

public class MoviesListAdapter extends RecyclerView.Adapter<MoviesListAdapter.ViewHolder> {
    private List<MovieItem> list;
//    private DatabaseHelper databaseHelper;

    public MoviesListAdapter(List<MovieItem> list) {
        this.list = list;
    }

    @Override
    public MoviesListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View listItem = inflater.inflate(R.layout.movie_item_layout, parent, false);
        ViewHolder holder = new ViewHolder(listItem);
        return holder;
    }

    @Override
    public void onBindViewHolder(MoviesListAdapter.ViewHolder holder, int position) {
        holder.titleView.setText(list.get(position).getTitle());
        holder.plotView.setText(list.get(position).getPlot());
        Picasso.get().load(list.get(position).getPoster()).into(holder.posterView);

        holder.viewButton.setOnClickListener(v -> {
            Intent detailsIntent = new Intent(holder.titleView.getContext(), DetailActivity.class);
            detailsIntent.putExtra("title", list.get(position).getTitle());
            holder.titleView.getContext().startActivity(detailsIntent);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleView, plotView;
        SquareImageView posterView;
        Button viewButton, favButton;

        public ViewHolder(View itemView) {
            super(itemView);
            titleView = itemView.findViewById(R.id.movie_name);
            plotView = itemView.findViewById(R.id.movie_plot);
            posterView = itemView.findViewById(R.id.movie_poster);
            viewButton = itemView.findViewById(R.id.button_view);
            favButton = itemView.findViewById(R.id.button_add_fav);
        }
    }
}
