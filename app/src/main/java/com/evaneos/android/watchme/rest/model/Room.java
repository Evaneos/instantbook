package com.evaneos.android.watchme.rest.model;

import android.content.Context;
import android.database.Cursor;
import android.provider.CalendarContract;
import android.text.TextUtils;
import com.evaneos.android.watchme.EventValues;

import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by Guillaume 'DarzuL' Bourderye on 07/11/15.
 *
 * A room is always associated with a calendar
 */
public class Room {

    // TODO hack
    public static final Map<String, Integer> COVER_PICTURE_MAP = new HashMap<>();
    public static final List<String> WHITE_LIST_CALENDAR_NAME = new LinkedList<>();
    public static final List<String> WHITE_LIST_CALENDAR_ID = new LinkedList<>();
    //
    private String mCalendarId;
    private String mName;
    private boolean mIsDirty;
    private int mPictureId;

    public Room(String calendarId, String name, boolean isDirty, int pictureId) {
        mCalendarId = calendarId;
        mName = name;
        mIsDirty = isDirty;
        mPictureId = pictureId;
    }

    /**
     * TODO This is so hacky :(
     */
    public static void clearCalendarIdInWhiteList() {
        WHITE_LIST_CALENDAR_ID.clear();
    }

    public static void addCalendarIdInWhiteList(Room room) {
        WHITE_LIST_CALENDAR_ID.add(room.getCalendarId());
        room.setPictureId(COVER_PICTURE_MAP.get(room.getName()));
    }

    public static String printCalendarIdForSql() {
        return TextUtils.join(",", WHITE_LIST_CALENDAR_ID);
    }

    public String getCalendarId() {
        return mCalendarId;
    }
    // End of hack

    public String getName() {
        return mName;
    }

    public boolean isDirty() {
        return mIsDirty;
    }

    public int getPictureId() {
        return mPictureId;
    }

    public void setPictureId(int pictureId) {
        mPictureId = pictureId;
    }

    public boolean isBusy(Context context) {
        long now = Calendar.getInstance().getTimeInMillis();
        long end = now + EventValues.DURATION;
        Cursor cursor = context.getContentResolver().query( //
                CalendarContract.Events.CONTENT_URI, //
                new String[]{CalendarContract.Events._ID}, //
                CalendarContract.Events.CALENDAR_ID + "=?" +
                        " AND ((" + //
                        CalendarContract.Events.DTSTART + "<? AND " + //
                        CalendarContract.Events.DTEND + ">?)" +
                        " OR (" + //
                        CalendarContract.Events.DTSTART + ">? AND " + //
                        CalendarContract.Events.DTSTART + "<?)" + //
                        " OR (" + //
                        CalendarContract.Events.DTEND + ">? AND " + //
                        CalendarContract.Events.DTEND + "<?))", //
                new String[]{ //
                        String.valueOf(mCalendarId), //
                        String.valueOf(now), //
                        String.valueOf(end), //
                        String.valueOf(now), //
                        String.valueOf(end), //
                        String.valueOf(now), //
                        String.valueOf(end) //
                }, //
                null //
        );

        boolean isBusy = cursor.getCount() != 0;
        cursor.close();

        return isBusy;
    }

    // TODO hack
    static {
        WHITE_LIST_CALENDAR_NAME.add("Delhi");
        WHITE_LIST_CALENDAR_NAME.add("Pékin");
        WHITE_LIST_CALENDAR_NAME.add("Rio");
        WHITE_LIST_CALENDAR_NAME.add("Lisbonne");
        WHITE_LIST_CALENDAR_NAME.add("Buenos Aires");
        WHITE_LIST_CALENDAR_NAME.add("San Francisco");
        WHITE_LIST_CALENDAR_NAME.add("Salle Détente");
        WHITE_LIST_CALENDAR_NAME.add("Le Loft");

        COVER_PICTURE_MAP.put("Delhi", 131804);
        COVER_PICTURE_MAP.put("Pékin", 168297);
        COVER_PICTURE_MAP.put("Rio", 172230);
        COVER_PICTURE_MAP.put("Lisbonne", 134883);
        COVER_PICTURE_MAP.put("Buenos Aires", 127951);
        COVER_PICTURE_MAP.put("San Francisco", 131708);
        COVER_PICTURE_MAP.put("Salle Détente", 125457);
        COVER_PICTURE_MAP.put("Le Loft", 197451);
    }
}
