package com.pianoshelf.joey.pianoshelf.profile;

import com.pianoshelf.joey.pianoshelf.composition.Composition;

import java.util.List;

/**
 * Created by joey on 12/29/14.
 */
public class Profile {
    private String username;
    private String full_name;
    private ProfileShelf shelf;
    private String description;
    private String small_profile_picture;
    private String large_profile_picture;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public ProfileShelf getShelf() {
        return shelf;
    }

    public void setShelf(ProfileShelf shelf) {
        this.shelf = shelf;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLarge_profile_picture() {
        return large_profile_picture;
    }

    public void setLarge_profile_picture(String large_profile_picture) {
        this.large_profile_picture = large_profile_picture;
    }

    public String getSmall_profile_picture() {
        return small_profile_picture;
    }

    public void setSmall_profile_picture(String small_profile_picture) {
        this.small_profile_picture = small_profile_picture;
    }


    public class ProfileShelf {
        private List<Composition> sheetmusic;

        public List<Composition> getSheetmusic() {
            return sheetmusic;
        }

        public void setSheetmusic(List<Composition> sheetmusic) {
            this.sheetmusic = sheetmusic;
        }
    }
}
