package com.pianoshelf.joey.pianoshelf.composition;

import com.pianoshelf.joey.pianoshelf.rest_api.SubmittedBy;

/**
 * Created by Me on 3/25/2017.
 * This class address the problems with different typed fields between
 * profile and normal composition json fields
 * example: http://pianoshelf.com/api/profile/?username=hello
 *
 *
 // suspected deserialization because of conflict between:
 // http://pianoshelf.com/api/profile/?username=hello
 // http://pianoshelf.com/api/sheetmusic/1/
 */

public interface Composition {
    public int getId();

    public void setId(int id);

    public String getTitle();

    public void setTitle(String title);

    public String getComposer_name();

    public void setComposer_name(String composer_name);

    public int getDifficulty();

    public void setDifficulty(int difficulty);

    public int getView_count();

    public void setView_count(int view_count);


    public String getThumbnail_url();

    public void setThumbnail_url(String thumbnail_url);

}
