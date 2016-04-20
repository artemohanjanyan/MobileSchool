package com.artemohanjanyan.mobileschool;

import java.net.URL;
import java.util.List;

/**
 * Stores data about one artist.
 */
public class Artist {
    public int id;
    public String name;
    public List<String> genres;
    public int tracks, albums;
    public URL link;
    public String description;
    public URL smallCover, bigCover;
}
