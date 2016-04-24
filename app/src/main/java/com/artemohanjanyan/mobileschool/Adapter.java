package com.artemohanjanyan.mobileschool;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter for displaying artists' preview.
 * Stores artists in a {@link List}.
 */
public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

    public static final String TAG = Adapter.class.getSimpleName();

    private List<Artist> artists;
    private int lastPosition = -1;

    public class ViewHolder extends RecyclerView.ViewHolder {
        public Artist artist;

        public View view;
        public ImageView cover;
        public TextView name;
        public TextView genres;
        public TextView published;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            cover = (ImageView) view.findViewById(R.id.item_cover);
            name = (TextView) view.findViewById(R.id.item_name);
            genres = (TextView) view.findViewById(R.id.item_genres);
            published = (TextView) view.findViewById(R.id.item_published);
        }
    }

    /**
     * Creates adapter with empty list of artists.
     */
    public Adapter() {
        this.artists = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);
        final ViewHolder viewHolder = new ViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Launch DescriptionActivity
                Log.d(TAG, "launching " + viewHolder.artist.name + " description");
                Context context = viewHolder.cover.getContext();
                Intent intent = new Intent(context, DescriptionActivity.class);
                intent.putExtra(DescriptionActivity.ARTIST_EXTRA, viewHolder.artist);
                context.startActivity(intent);
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Artist artist = artists.get(position);

        holder.artist = artist;

        holder.name.setText(artist.name);
        holder.genres.setText(artist.getGenres());
        holder.published.setText(artist.getPublished(holder.cover.getContext()));

        ApplicationContext.getInstance().getPicasso()
                .load(holder.artist.smallCover)
                .into(holder.cover);
    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        holder.cover.setImageBitmap(null);
        ApplicationContext.getInstance().getPicasso()
                .cancelRequest(holder.cover);
    }

    @Override
    public void onViewAttachedToWindow(ViewHolder holder) {
        // Start animation if this item hasn't been showed yet.
        if (holder.getAdapterPosition() > lastPosition) {
            holder.view.startAnimation(AnimationUtils
                    .loadAnimation(holder.view.getContext(), R.anim.item_animation));
            lastPosition = holder.getAdapterPosition();
        }
    }

    @Override
    public void onViewDetachedFromWindow(ViewHolder holder) {
        holder.view.clearAnimation();
    }

    @Override
    public int getItemCount() {
        return artists.size();
    }

    /**
     * Sets list of displayed artists.
     * @param artists list of artists to be displayed. Keeps the reference to this list
     *                until {@link Adapter#dropArtists()} is called.
     */
    public void setArtists(List<Artist> artists) {
        dropArtists();
        this.artists = artists;
        notifyItemRangeInserted(0, this.artists.size());
    }

    /**
     * Drops the list of displayed artists.
     */
    public void dropArtists() {
        notifyItemRangeRemoved(0, artists.size());
        artists = new ArrayList<>();
        lastPosition = -1;
    }
}
