package com.pianoshelf.joey.pianoshelf.shelf;

import com.pianoshelf.joey.pianoshelf.composition.FullComposition;

import java.util.List;

/**
 * Created by joey on 07/08/16.
 */
public class ShelfUpdateResponse {
    List<FullComposition> sheetmusic;

    public List<FullComposition> getSheetmusic() {
        return sheetmusic;
    }

    public void setSheetmusic(List<FullComposition> sheetmusic) {
        this.sheetmusic = sheetmusic;
    }
}
