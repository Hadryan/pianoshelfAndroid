package com.pianoshelf.joey.pianoshelf.shelf;

import java.net.URL;
import java.util.List;

/**
 * Created by joey on 05/09/16.
 * Should be a subset of FullComposition
 * But due to submitted_by having a conflicting type definition this is here as a workaround
 */
public class ShelfSheetInfo {
    int id;
    String title;
    String style;
    String key;
    String date;
    int file_size;
    String composer_name;
    String license;
    String license_name;
    URL license_url;
    List<String> tags;
    int submitted_by;
    URL thumbnail_url;
    int pop;
    int view_count;
    String uniqueurl;
    int difficulty;

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

    public int getFile_size() {
        return file_size;
    }

    public void setFile_size(int file_size) {
        this.file_size = file_size;
    }

    public String getComposer_name() {
        return composer_name;
    }

    public void setComposer_name(String composer_name) {
        this.composer_name = composer_name;
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

    public URL getLicense_url() {
        return license_url;
    }

    public void setLicense_url(URL license_url) {
        this.license_url = license_url;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public int getSubmitted_by() {
        return submitted_by;
    }

    public void setSubmitted_by(int submitted_by) {
        this.submitted_by = submitted_by;
    }

    public URL getThumbnail_url() {
        return thumbnail_url;
    }

    public void setThumbnail_url(URL thumbnail_url) {
        this.thumbnail_url = thumbnail_url;
    }

    public int getPop() {
        return pop;
    }

    public void setPop(int pop) {
        this.pop = pop;
    }

    public int getView_count() {
        return view_count;
    }

    public void setView_count(int view_count) {
        this.view_count = view_count;
    }

    public String getUniqueurl() {
        return uniqueurl;
    }

    public void setUniqueurl(String uniqueurl) {
        this.uniqueurl = uniqueurl;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }
}
