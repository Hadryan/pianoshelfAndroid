package com.pianoshelf.joey.pianoshelf.comment;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.pianoshelf.joey.pianoshelf.rest_api.SubmittedBy;

import java.util.Date;
import java.util.List;

/**
 * Created by joey on 02/10/16.
 */

public class Comment {

    /* JSON FIELDS */
    int id; // id of the comment
    String message;
    int upvotes; // shitpost counter
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    Date date;
    int user;
    Integer recipient; // comment id of parent
    String recipient_username;
    int sheetmusic;
    String sheetmusic_title;
    String sheetmusic_uniqueurl;
    String status;
    String last_modified;
    SubmittedBy submitted_by;
    List<Integer> replies; // comment id of replies

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getUpvotes() {
        return upvotes;
    }

    public void setUpvotes(int upvotes) {
        this.upvotes = upvotes;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getUser() {
        return user;
    }

    public void setUser(int user) {
        this.user = user;
    }

    public Integer getRecipient() {
        return recipient;
    }

    public void setRecipient(Integer recipient) {
        this.recipient = recipient;
    }

    public String getRecipient_username() {
        return recipient_username;
    }

    public void setRecipient_username(String recipient_username) {
        this.recipient_username = recipient_username;
    }

    public int getSheetmusic() {
        return sheetmusic;
    }

    public void setSheetmusic(int sheetmusic) {
        this.sheetmusic = sheetmusic;
    }

    public String getSheetmusic_title() {
        return sheetmusic_title;
    }

    public void setSheetmusic_title(String sheetmusic_title) {
        this.sheetmusic_title = sheetmusic_title;
    }

    public String getSheetmusic_uniqueurl() {
        return sheetmusic_uniqueurl;
    }

    public void setSheetmusic_uniqueurl(String sheetmusic_uniqueurl) {
        this.sheetmusic_uniqueurl = sheetmusic_uniqueurl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLast_modified() {
        return last_modified;
    }

    public void setLast_modified(String last_modified) {
        this.last_modified = last_modified;
    }

    public SubmittedBy getSubmitted_by() {
        return submitted_by;
    }

    public void setSubmitted_by(SubmittedBy submitted_by) {
        this.submitted_by = submitted_by;
    }

    public List<Integer> getReplies() {
        return replies;
    }

    public void setReplies(List<Integer> replies) {
        this.replies = replies;
    }
}
