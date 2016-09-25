package com.pianoshelf.joey.pianoshelf.composition;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

/**
 * Created by joey on 12/13/14.
 * Querying the API through a search/filter no longer returns a fully complete Composition object
 * Always check null condition before accessing fields
 * Needs a util that automatically queries the full Composition object given an id/composition
 * <p/>
 * Cannot use containers with Gson, defer to Java arrays instead
 *
 * Example JSON:
 * C.SERVER_ADDR/api/sheetmusic/1/
 */

public class Composition {

    // sheet info
    private String title;
    private String style;
    private String key;
    private String date;
    private String composer_name;
    private String description;
    private int difficulty;
    private List<String> tags;

    // techical
    private int id;
    private String file_size;
    // suspected deserialization because of conflict between:
    // http://pianoshelf.com/api/profile/?username=hello
    // http://pianoshelf.com/api/sheetmusic/1/
    @JsonIgnore
    private SubmittedBy submitted_by; // userid of submission
    private boolean upload_complete;

    // API
    private String thumbnail_url;
    private String uniqueurl;

    // formatting
    private String original_format;
    private String lilypond_file;
    private String midi_file;

    // statistics
    private int pop;
    private int view_count;

    // user info
    private boolean in_shelf;

    // descriptions
    private String short_description;
    private String long_description;

    // licensing
    private String license;
    private String license_name;
    private String license_url;

    // arrays
    private List<String> images;
    private List<VideoInfo> videos;
    @Deprecated
    private List<CommentInfo> comments;

    // Custom fields
    private List<String> offline_images;

    // Getters and Setters, auto-generated by android studio
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

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getFile_size() {
        return file_size;
    }

    public void setFile_size(String file_size) {
        this.file_size = file_size;
    }

    public String getComposer_name() {
        return composer_name;
    }

    public void setComposer_name(String composer_name) {
        this.composer_name = composer_name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPop() {
        return pop;
    }

    public void setPop(int pop) {
        this.pop = pop;
    }

    public String getUniqueurl() {
        return uniqueurl;
    }

    public void setUniqueurl(String uniqueurl) {
        this.uniqueurl = uniqueurl;
    }

    public String getThumbnail_url() {
        return thumbnail_url;
    }

    public void setThumbnail_url(String thumbnail_url) {
        this.thumbnail_url = thumbnail_url;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public List<String> getOffline_Images() {
        return offline_images;
    }

    public void setOffline_images(List<String> offline_images) {
        this.offline_images = offline_images;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public List<VideoInfo> getVideos() {
        return videos;
    }

    public void setVideos(List<VideoInfo> videos) {
        this.videos = videos;
    }

    @Deprecated
    public List<CommentInfo> getComments() {
        return comments;
    }

    @Deprecated
    public void setComments(List<CommentInfo> comments) {
        this.comments = comments;
    }

    public List<String> getOffline_images() {
        return offline_images;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    @JsonIgnore
    public SubmittedBy getSubmitted_by() {
        return submitted_by;
    }

    @JsonIgnore
    public void setSubmitted_by(SubmittedBy submitted_by) {
        this.submitted_by = submitted_by;
    }

    public boolean isUpload_complete() {
        return upload_complete;
    }

    public void setUpload_complete(boolean upload_complete) {
        this.upload_complete = upload_complete;
    }

    public String getOriginal_format() {
        return original_format;
    }

    public void setOriginal_format(String original_format) {
        this.original_format = original_format;
    }

    public String getLilypond_file() {
        return lilypond_file;
    }

    public void setLilypond_file(String lilypond_file) {
        this.lilypond_file = lilypond_file;
    }

    public String getMidi_file() {
        return midi_file;
    }

    public void setMidi_file(String midi_file) {
        this.midi_file = midi_file;
    }

    public int getView_count() {
        return view_count;
    }

    public void setView_count(int view_count) {
        this.view_count = view_count;
    }

    public boolean isIn_shelf() {
        return in_shelf;
    }

    public void setIn_shelf(boolean in_shelf) {
        this.in_shelf = in_shelf;
    }

    public String getShort_description() {
        return short_description;
    }

    public void setShort_description(String short_description) {
        this.short_description = short_description;
    }

    public String getLong_description() {
        return long_description;
    }

    public void setLong_description(String long_description) {
        this.long_description = long_description;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public String getLicense_name() {
        return license_name;
    }

    public void setLicense_name(String license_name) {
        this.license_name = license_name;
    }

    public String getLicense_url() {
        return license_url;
    }

    public void setLicense_url(String license_url) {
        this.license_url = license_url;
    }
}
