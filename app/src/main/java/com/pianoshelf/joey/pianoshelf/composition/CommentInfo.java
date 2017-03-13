package com.pianoshelf.joey.pianoshelf.composition;

/**
 * Created by joey on 27/04/16.
 */
public class CommentInfo {
    //  https://www.pianoshelf.com/api/sheetmusic/1/
    private int id; // comment unique id
    private String message; // comment
    private int upvotes;
    private String date;
    private int user; // author userid
    private int recipient;
    private String recipient_username;
    private int sheetmusic;
    private String sheetmusic_title;
    private String sheetmusic_uniqueurl;
    private String profile_picture; // defer image url resolution to a later stage
    private String status; // TODO enum
    private String last_modified; // date
    private String name; // user name of author
    private int[] replies; // comment id of replies

    // Getters and Setters

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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getUser() {
        return user;
    }

    public void setUser(int user) {
        this.user = user;
    }

    public int getRecipient() {
        return recipient;
    }

    public void setRecipient(int recipient) {
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

    public String getProfile_picture() {
        return profile_picture;
    }

    public void setProfile_picture(String profile_picture) {
        this.profile_picture = profile_picture;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int[] getReplies() {
        return replies;
    }

    public void setReplies(int[] replies) {
        this.replies = replies;
    }
}
