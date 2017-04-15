package com.pianoshelf.joey.pianoshelf.comment;

/**
 * Created by Me on 4/11/2017.
 */

public class CommentPost {
    String commentText;
    int sheetmusicId;
    int recipientId;

    public CommentPost(String commentText, int sheetmusicId, int recipientId) {
        this.commentText = commentText;
        this.sheetmusicId = sheetmusicId;
        this.recipientId = recipientId;
    }

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    public int getSheetmusicId() {
        return sheetmusicId;
    }

    public void setSheetmusicId(int sheetmusicId) {
        this.sheetmusicId = sheetmusicId;
    }

    public int getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(int recipientId) {
        this.recipientId = recipientId;
    }
}
