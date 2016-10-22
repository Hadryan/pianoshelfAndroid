package com.pianoshelf.joey.pianoshelf.comment;

import com.pianoshelf.joey.pianoshelf.rest_api.SubmittedBy;

import java.util.Date;
import java.util.List;

/**
 * Created by joey on 02/10/16.
 */

public class Comment {
    int id; // id of the comment
    String message;
    int upvotes; // shitpost counter
    String date;
    int user;
    Integer recipient; // comment id of parent
    String recipient_username;
    int sheetmusic;
    String sheetmusic_title;
    String sheetmusic_uniqueurl;
    String status;
    Date last_modified;
    SubmittedBy submitted_by;
    List<Integer> replies; // comment id of replies


}
