package com.pianoshelf.joey.pianoshelf.REST_API;

import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;
import com.pianoshelf.joey.pianoshelf.Constants;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;

/**
 * Created by joey on 05/07/15.
 */
public class AddSheetToShelfRequest extends SpringAndroidSpiceRequest<Void> {
    private final String shelfUrl = Constants.SERVER_ADDR + "api/shelf/";
    private ShelfSheetMusic shelfSheetMusic;
    private String authToken;

    public AddSheetToShelfRequest(int sheetID, String authToken) {
        super(Void.class);
        this.authToken = authToken;
        this.shelfSheetMusic = new ShelfSheetMusic(sheetID);
    }

    @Override
    public Void loadDataFromNetwork() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.add(Constants.AUTHORIZATION_HEADER, Constants.TOKEN_PREFIX + authToken);
        HttpEntity<ShelfSheetMusic> request = new HttpEntity<>(shelfSheetMusic, headers);
        getRestTemplate().postForEntity(shelfUrl, request, Void.class);
        return null;
    }
}
