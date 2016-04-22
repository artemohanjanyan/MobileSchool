package com.artemohanjanyan.mobileschool;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
    private List<Artist> artists;

    public class ViewHolder extends RecyclerView.ViewHolder {

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
                    Context context = getContext();
                    Intent intent = new Intent(context, DescriptionActivity.class);
                    intent.putExtra(DescriptionActivity.ARTIST_EXTRA, artist);
                    context.startActivity(intent);
                }
            };
        }

        public Context getContext() {
            return cover.getContext();
        }

        public void downloadCover() {
            Picasso.with(getContext())
                    .load(artist.smallCover)
                    .into(cover);
        }
    }

    public Adapter() {
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
        holder.genres.setText(artist.getGenres());
        holder.published.setText(artist.getPublished(holder.getContext()));

        holder.downloadCover();
    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        holder.cover.setImageBitmap(null);
        Picasso.with(holder.getContext()).cancelRequest(holder.cover);
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

    @Override
    public void registerAdapterDataObserver(RecyclerView.AdapterDataObserver observer) {
        super.registerAdapterDataObserver(observer);
    }
}
