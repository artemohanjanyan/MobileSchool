package com.artemohanjanyan.mobileschool;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import butterknife.BindView;

/**
 * Adapter for displaying artists' preview.
 */
class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

    interface OnArtistSelectListener {
        void onArtistSelected(Artist artist);
    }

    private static final String TAG = Adapter.class.getSimpleName();

    private Cursor cursor;
    private int lastPosition = -1;
    private int lastFetched = -1;

    static class ViewHolder extends RecyclerView.ViewHolder {
        OnArtistSelectListener listener;
        Context context;

        Artist artist;

        View view;
        @BindView(R.id.item_cover) ImageView cover;
        @BindView(R.id.item_name) TextView name;
        @BindView(R.id.item_genres) TextView genres;
        @BindView(R.id.item_published) TextView published;

        ViewHolder(View view) {
            super(view);
            this.view = view;
            context = view.getContext();
            listener = (OnArtistSelectListener) view.getContext();

//            ButterKnife.setDebug(true);
//            ButterKnife.bind(this, view); вот с этим и без следующих строчек не пашет
            cover = (ImageView) view.findViewById(R.id.item_cover);
            name = (TextView) view.findViewById(R.id.item_name);
            genres = (TextView) view.findViewById(R.id.item_genres);
            published = (TextView) view.findViewById(R.id.item_published);
        }
    }

    /**
     * Creates adapter with empty list of artists.
     */
    Adapter() {
        Log.d(TAG, "adapter created");
        this.cursor = null;
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
                // Launch DescriptionFragment
                viewHolder.listener.onArtistSelected(viewHolder.artist);
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        cursor.moveToPosition(position);
        Artist artist = new Artist(cursor);

        holder.artist = artist;

        holder.name.setText(artist.name);
        holder.genres.setText(artist.getGenres());
        holder.published.setText(artist.getPublished(holder.cover.getContext()));


        Picasso.with(holder.context)
                .load(holder.artist.smallCover)
                .into(holder.cover);
        lastFetched = Math.max(lastFetched, position);

        // Fetch next 10 covers to minimize loading visible by user.
        // Start from last not cached.
        for (int i = lastFetched - position + 1; i <= 10 && position + i < getItemCount(); ++i) {
            cursor.moveToPosition(position + i);
            Picasso.with(holder.context)
                    .load(Artist.getSmallCover(cursor)).fetch();
            lastFetched = position + i;
        }

        // Fetch big cover
        // UPD Slows everything down
        // ApplicationContext.getInstance().getPicasso()
        //        .load(Artist.getBigCover(cursor))
        //        .tag(holder.artist.bigCover)
        //        .fetch();
    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        holder.cover.setImageBitmap(null);
        // May stop unnecessary image loading, but let's let them cache.
        //ApplicationContext.getInstance().getPicasso()
        //        .cancelRequest(holder.cover);

        // Disable big cover fetching
        // UPD Slows everything down
        // ApplicationContext.getInstance().getPicasso()
        //        .cancelTag(holder.artist.bigCover);
    }

    @Override
    public void onViewAttachedToWindow(ViewHolder holder) {
        // Start animation if this item hasn't been showed yet.
        if (holder.getAdapterPosition() > lastPosition) {
            AnimatorSet set = (AnimatorSet) AnimatorInflater
                    .loadAnimator(holder.view.getContext(), R.animator.list_item_appearance);
            set.setTarget(holder.view);
            set.start();
            lastPosition = holder.getAdapterPosition();
        }
    }

    @Override
    public void onViewDetachedFromWindow(ViewHolder holder) {
        // Stop animation
        holder.view.clearAnimation();
    }

    @Override
    public int getItemCount() {
        return cursor != null ? cursor.getCount() : 0;
    }

    /**
     * Set cursor pointing to artists' information.
     * @param cursor cursor pointing to the information to be displayed.
     *                Adapter keeps the reference to this cursor
     *                until {@link Adapter#dropCursor()} is called.
     */
    void setCursor(Cursor cursor) {
        dropCursor();
        this.cursor = cursor;
        notifyDataSetChanged();
    }

    /**
     * Removes the reference to the cursor.
     */
    void dropCursor() {
        notifyDataSetChanged();
        cursor = null;
        lastPosition = -1;
        lastFetched = -1;
    }

    /**
     * Returns position of last displayed item.
     * Used for animation.
     */
    int getLastPosition() {
        return lastPosition;
    }

    /**
     * Override the position of the last displayed item.
     * Used for animation.
     * @param lastPosition index of the item before the first item to be animated.
     */
    void setLastPosition(int lastPosition) {
        this.lastPosition = lastPosition;
    }
}
