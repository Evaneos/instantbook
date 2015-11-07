package com.evaneos.android.watchme.helper;

/**
 * Created by Guillaume 'DarzuL' Bourderye on 07/11/15.
 *
 * Helper to build picture url from API
 */
public final class PictureUrlHelper {

    private static final String BASE_PICTURE_URL = "http://static1.evaneos.com/images/reduction/";

    private PictureUrlHelper() {
    }

    public static String buildUrl(int pictureId, int width, int height) {
        return BASE_PICTURE_URL + //
                pictureId + "_w-" + width + //
                "_h-" + height + //
                "_m-crop.jpg";
    }
}
