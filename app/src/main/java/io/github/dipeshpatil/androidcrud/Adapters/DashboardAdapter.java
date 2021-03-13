package io.github.dipeshpatil.androidcrud.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import io.github.dipeshpatil.androidcrud.DatabaseHelper;
import io.github.dipeshpatil.androidcrud.Movies.MovieItem;
import io.github.dipeshpatil.androidcrud.R;

public class DashboardAdapter extends RecyclerView.Adapter<DashboardAdapter.ViewHolder> {
    private List<MovieItem> list;
    private DatabaseHelper databaseHelper;

    public DashboardAdapter(List<MovieItem> list) {
        this.list = list;
    }

    @Override
    public DashboardAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View listItem = inflater.inflate(R.layout.dashboard_layout, parent, false);
        return new ViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(DashboardAdapter.ViewHolder holder, int position) {
        holder.dashboardTitleView.setText(trimTextBySize(list.get(position).getTitle(), 20));

        holder.moreButton.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(holder.moreButton.getContext(), holder.moreButton);
            popupMenu.inflate(R.menu.dashboard_list_menu);
            popupMenu.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.dashboard_view:
                        Toast.makeText(holder.moreButton.getContext(), list.get(position).getTitle(), Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.dashboard_share:
                        Toast.makeText(holder.moreButton.getContext(), list.get(position).getTitle() + " Shared", Toast.LENGTH_SHORT).show();
                        return true;
                    default:
                        return false;
                }
            });
            popupMenu.show();
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView dashboardTitleView;
        Button moreButton, deleteButton;

        public ViewHolder(View itemView) {
            super(itemView);
            dashboardTitleView = itemView.findViewById(R.id.dashboard_movie_title);
            moreButton = itemView.findViewById(R.id.dashboard_more_button);
            deleteButton = itemView.findViewById(R.id.dashboard_delete_button);
        }
    }

    private String trimTextBySize(String text, int size) {
        if (text.length() > size) {
            return text.substring(0, size) + "..";
        }
        return text;
    }
}
