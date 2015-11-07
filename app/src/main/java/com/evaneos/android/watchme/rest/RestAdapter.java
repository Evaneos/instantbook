package com.evaneos.android.watchme.rest;

import com.evaneos.android.watchme.rest.service.RoomService;
import retrofit.Retrofit;

/**
 * Created by Guillaume 'DarzuL' Bourderye on 07/11/15.
 *
 * Generate services
 */
public class RestAdapter {
    private static RestAdapter ourInstance = new RestAdapter();

    private final RoomService mRoomService;

    private RestAdapter() {
        Retrofit retrofit = new Retrofit.Builder().baseUrl("https://api.github.com").build();

        mRoomService = retrofit.create(RoomService.class);
    }

    public static RestAdapter getInstance() {
        return ourInstance;
    }

    public RoomService getRoomService() {
        return mRoomService;
    }
}
