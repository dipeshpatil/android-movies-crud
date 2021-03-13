package io.github.dipeshpatil.androidcrud.MoviesList;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.theophrast.ui.widget.SquareImageView;

import java.util.List;

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
        View listItem = inflater.inflate(R.layout.movie_item_layout, parent, false);
        ViewHolder holder = new ViewHolder(listItem);
        return holder;
    }

    @Override
    public void onBindViewHolder(MoviesListAdapter.ViewHolder holder, int position) {
        holder.titleView.setText(list.get(position).getTitle());
        holder.plotView.setText(list.get(position).getPlot());
        Picasso.get().load(list.get(position).getPoster()).into(holder.posterView);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleView, plotView;
        SquareImageView posterView;

        public ViewHolder(View itemView) {
            super(itemView);
            titleView = itemView.findViewById(R.id.movie_name);
            plotView = itemView.findViewById(R.id.movie_plot);
            posterView = itemView.findViewById(R.id.movie_poster);
        }
    }
}
