package com.evaneos.android.watchme.helper;

import android.content.Context;
import android.database.Cursor;
import android.provider.CalendarContract;
import com.evaneos.android.watchme.EventValues;
import com.evaneos.android.watchme.UserInformation;
import com.evaneos.android.watchme.rest.model.Room;

import java.util.Calendar;

/**
 * Created by Guillaume 'DarzuL' Bourderye on 07/11/15.
 *
 * Help to fetch data from calendar CP
 */
public final class CalendarHelper {
    private CalendarHelper() {
    }

    public static String getUserAccountName(Context context) {
        return context.getSharedPreferences(UserInformation.PREF_FILE, Context.MODE_PRIVATE) //
                .getString(UserInformation.KEY_USER_ACCOUNT_NAME, null);
    }

    public static boolean isUserBusy(Context context) {
        long now = Calendar.getInstance().getTimeInMillis();
        long end = now + EventValues.DURATION;
        String userAccountName = getUserAccountName(context);

        if (userAccountName == null) {
            return false;
        }

        Cursor cursor = context.getContentResolver().query( //
                CalendarContract.Events.CONTENT_URI, //
                null, //
                CalendarContract.Events.ACCOUNT_NAME + "=?" +
                        " AND ((" + //
                        CalendarContract.Events.DTSTART + "<? AND " + //
                        CalendarContract.Events.DTEND + ">?)" +
                        " OR (" + //
                        CalendarContract.Events.DTSTART + ">? AND " + //
                        CalendarContract.Events.DTSTART + "<?)" + //
                        " OR (" + //
                        CalendarContract.Events.DTEND + ">? AND " + //
                        CalendarContract.Events.DTEND + "<?))" + //
                        // TODO hack
                        " AND " + //
                        CalendarContract.Events.CALENDAR_ID + " IN (" + Room.printCalendarIdForSql() + ")",
                new String[]{ //
                        String.valueOf(userAccountName), //
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

    /**
     * Return the calendar id where the user has an event
     *
     * @param context
     * @return
     */
    public static Integer getUserCurrentRoomId(Context context) {
        String userAccountName = getUserAccountName(context);
        if (userAccountName == null) {
            return null;
        }

        long now = Calendar.getInstance().getTimeInMillis();

        Cursor cursor = context.getContentResolver().query( //
                CalendarContract.Events.CONTENT_URI, //
                new String[]{CalendarContract.Events.CALENDAR_ID}, //
                CalendarContract.Events.ACCOUNT_NAME + "=? AND " +
                        CalendarContract.Events.DTSTART + "<=? AND " +
                        CalendarContract.Events.DTEND + ">?", //
                new String[]{ //
                        userAccountName, //
                        String.valueOf(now), //
                        String.valueOf(now) //
                }, //
                null //
        );

        if (cursor == null) {
            return null;
        }

        Integer userCurrentRoomId = null;
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            userCurrentRoomId = cursor.getInt(0);
        }

        cursor.close();
        return userCurrentRoomId;
    }
}
