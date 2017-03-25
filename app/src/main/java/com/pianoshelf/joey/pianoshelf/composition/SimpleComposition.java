package com.pianoshelf.joey.pianoshelf.composition;

/**
 * Created by Me on 3/25/2017.
 * This class address the problems with different typed fields between
 * profile and normal composition json fields
 * example: http://pianoshelf.com/api/profile/?username=hello
 * <p>
 * <p>
 * // suspected deserialization because of conflict between:
 * // http://pianoshelf.com/api/profile/?username=hello
 * // http://pianoshelf.com/api/sheetmusic/1/
 */

public class SimpleComposition implements Composition {
    int id;
    String title;
    String composer_name;
    int difficulty;
    int view_count;
    String thumbnail_url;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getComposer_name() {
        return composer_name;
    }

    public void setComposer_name(String composer_name) {
        this.composer_name = composer_name;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public int getView_count() {
        return view_count;
    }

    public void setView_count(int view_count) {
        this.view_count = view_count;
    }

    public String getThumbnail_url() {
        return thumbnail_url;
    }

    public void setThumbnail_url(String thumbnail_url) {
        this.thumbnail_url = thumbnail_url;
    }
}
