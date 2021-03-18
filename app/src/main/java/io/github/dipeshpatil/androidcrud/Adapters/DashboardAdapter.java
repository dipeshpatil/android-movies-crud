package io.github.dipeshpatil.androidcrud.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.imangazaliev.slugify.Slugify;

import java.util.List;

import io.github.dipeshpatil.androidcrud.Helpers.DatabaseHelper;
import io.github.dipeshpatil.androidcrud.DetailActivity;
import io.github.dipeshpatil.androidcrud.Movies.MovieItem;
import io.github.dipeshpatil.androidcrud.R;

public class DashboardAdapter extends RecyclerView.Adapter<DashboardAdapter.ViewHolder> {
    private List<MovieItem> list;
    private DatabaseHelper databaseHelper;

    private FirebaseAuth auth;
    private FirebaseFirestore db;

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
            Context context = holder.moreButton.getContext();
            String title = list.get(position).getTitle();
            String rating = list.get(position).getRating();
            String year = list.get(position).getYear();
            PopupMenu popupMenu = new PopupMenu(context, holder.moreButton);
            popupMenu.inflate(R.menu.dashboard_list_menu);
            popupMenu.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.dashboard_view:
                        Intent detailsIntent = new Intent(context, DetailActivity.class);
                        detailsIntent.putExtra("title", title);
                        context.startActivity(detailsIntent);
                        return true;
                    case R.id.dashboard_share:
                        share(context, title, rating, year);
                        return true;
                    default:
                        return false;
                }
            });
            popupMenu.show();
        });

        holder.deleteButton.setOnClickListener(v -> {
            Context context = holder.deleteButton.getContext();
            String title = list.get(position).getTitle();
            databaseHelper = new DatabaseHelper(context);
            auth = FirebaseAuth.getInstance();
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
            builder.setTitle("Delete")
                    .setMessage("Are you sure you want to delete " + title + " ?")
                    .setNeutralButton(
                            "Cancel",
                            (dialog, which) -> {
                            }
                    )
                    .setPositiveButton(
                            "Delete",
                            (dialog, which) -> {
                                if (databaseHelper.deleteDataByTitle(title, auth.getCurrentUser().getUid())) {
                                    list.remove(position);
                                    notifyItemRemoved(position);
                                    notifyItemRangeChanged(position, list.size());
                                    if (deleteFromFirestore(title)) {
                                        Toast.makeText(context, title + " Deleted", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                    ).show();
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
        if (text.length() > size)
            return text.substring(0, size) + "..";
        return text;
    }

    private String getYTSLink(String domain, String title, String year) {
        Slugify slugify = new Slugify();
        return "https://yts." + domain + "/movie/" + slugify.slugify(title) + "-" + year;
    }

    private void share(Context context, String title, String rating, String year) {
        String link = getYTSLink("one", title, year);
        title = "Here's A Movie Worth Watching\n"
                + "*" + title + "*\n"
                + "Rating: _" + rating + "_\n"
                + link;
        Intent wi = new Intent(Intent.ACTION_SEND);
        wi.setType("text/plain");
        wi.setPackage("com.whatsapp");
        wi.putExtra(Intent.EXTRA_TEXT, title);
        try {
            context.startActivity(wi);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(context, "Whatsapp not installed.", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean deleteFromFirestore(String title) {
        final boolean[] deleted = {false};
        Slugify slugify = new Slugify();

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        String currentUser = auth.getCurrentUser().getUid();

        db.collection(currentUser)
                .document(slugify.slugify(title))
                .delete()
                .addOnSuccessListener(aVoid -> deleted[0] = true)
                .addOnFailureListener(e -> deleted[0] = false);

        return deleted[0];
    }
}