package io.github.dipeshpatil.androidcrud.Adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.theophrast.ui.widget.SquareImageView;

import java.util.List;

import io.github.dipeshpatil.androidcrud.DetailActivity;
import io.github.dipeshpatil.androidcrud.Movies.MovieItem;
import io.github.dipeshpatil.androidcrud.R;

public class MoviesListAdapter extends RecyclerView.Adapter<MoviesListAdapter.ViewHolder> {
    private List<MovieItem> list;

    public MoviesListAdapter(List<MovieItem> list) {
        this.list = list;
    }

    @Override
    public MoviesListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View listItem = inflater.inflate(R.layout.grid_movie_layout, parent, false);
        ViewHolder holder = new ViewHolder(listItem);
        return holder;
    }

    @Override
    public void onBindViewHolder(MoviesListAdapter.ViewHolder holder, int position) {
        Picasso.get().load(list.get(position).getPoster()).into(holder.posterView);
        holder.posterView.setOnClickListener(v -> {
            Intent detailsIntent = new Intent(holder.posterView.getContext(), DetailActivity.class);
            detailsIntent.putExtra("title", list.get(position).getTitle());
            holder.posterView.getContext().startActivity(detailsIntent);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        SquareImageView posterView;

        public ViewHolder(View itemView) {
            super(itemView);
            posterView = itemView.findViewById(R.id.grid_movie_poster);
        }
    }
}
