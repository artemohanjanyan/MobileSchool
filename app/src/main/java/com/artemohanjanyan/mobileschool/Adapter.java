package com.artemohanjanyan.mobileschool;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
    private List<Artist> artists;
    private Context context;
    private Resources resources;

    public class ViewHolder extends RecyclerView.ViewHolder
            implements DownloadCallback<Bitmap> {

        public DownloadImageTask coverDownloader;

        public Artist artist;

        public ImageView cover;
        public TextView name;
        public TextView genres;
        public TextView published;

        public ViewHolder(View view) {
            super(view);
            cover = (ImageView) view.findViewById(R.id.item_cover);
            name = (TextView) view.findViewById(R.id.item_name);
            genres = (TextView) view.findViewById(R.id.item_genres);
            published = (TextView) view.findViewById(R.id.item_published);
        }

        public View.OnClickListener getOnClickListener() {
            return new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, DescriptionActivity.class);
                    intent.putExtra(DescriptionActivity.NAME_EXTRA, artist.name);
                    intent.putExtra(DescriptionActivity.COVER_EXTRA, artist.bigCover);
                    intent.putExtra(DescriptionActivity.GENRES_EXTRA, getGenres());
                    intent.putExtra(DescriptionActivity.PUBLISHED_EXTRA, getPublished());
                    intent.putExtra(DescriptionActivity.DESCRIPTION_EXTRA, artist.description);
                    context.startActivity(intent);
                }
            };
        }

        @Override
        public void onDownloaded(Bitmap bitmap) {
            cover.setImageBitmap(bitmap);
            AnimatorSet set = (AnimatorSet) AnimatorInflater
                    .loadAnimator(context, R.animator.cover_animation);
            set.setTarget(cover);
            set.start();
        }

        public String getGenres() {
            return TextUtils.join(", ", artist.genres);
        }

        public String getPublished() {
            return context.getString(R.string.published,
                    resources.getQuantityString(R.plurals.albums, artist.albums, artist.albums),
                    resources.getQuantityString(R.plurals.tracks, artist.tracks, artist.tracks));
        }
    }

    public Adapter(Context context) {
        this.context = context;
        this.resources = context.getResources();
        this.artists = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.list_item_layout, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setOnClickListener(viewHolder.getOnClickListener());
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Artist artist = artists.get(position);

        holder.artist = artist;

        holder.name.setText(artist.name);
        holder.genres.setText(holder.getGenres());
        holder.published.setText(holder.getPublished());

        holder.coverDownloader = new DownloadImageTask(holder);
        holder.coverDownloader.execute(artist.smallCover);
    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        if (holder.coverDownloader != null) {
            holder.coverDownloader.cancel(true);
            holder.coverDownloader = null;
        }
        holder.cover.setImageBitmap(null);
    }

    @Override
    public int getItemCount() {
        return artists.size();
    }

    public void addArtists(List<Artist> artists) {
        int positionStart = this.artists.size();
        this.artists.addAll(artists);
        notifyItemRangeInserted(positionStart, artists.size());
    }
}
