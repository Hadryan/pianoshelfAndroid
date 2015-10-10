package com.pianoshelf.joey.pianoshelf.rest_api;

import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;
import com.pianoshelf.joey.pianoshelf.C;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;

/**
 * Created by joey on 20/09/15.
 */
public class RemoveSheetFromShelfRequest extends SpringAndroidSpiceRequest<Void> {
    private final String shelfUrl = C.SERVER_ADDR + "api/shelf/";
    private final String LOG_TAG = "Remove Sheet From Shelf";
    private ShelfSheetMusic shelfSheetMusic;
    private String authToken;

    public RemoveSheetFromShelfRequest(int sheetID, String authToken) {
        super(Void.class);
        shelfSheetMusic = new ShelfSheetMusic(sheetID);
        this.authToken = authToken;
    }

    @Override
    public Void loadDataFromNetwork() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.add(C.AUTHORIZATION_HEADER, C.TOKEN_PREFIX + authToken);
        HttpEntity<ShelfSheetMusic> request = new HttpEntity<>(shelfSheetMusic, headers);
        getRestTemplate().delete(shelfUrl, request);
        return null;
    }
}
