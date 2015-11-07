package com.evaneos.android.watchme.activity;

import android.Manifest;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;
import com.crashlytics.android.Crashlytics;
import com.evaneos.android.watchme.EventValues;
import com.evaneos.android.watchme.R;
import com.evaneos.android.watchme.UserInformation;
import com.evaneos.android.watchme.fragment.InfoDialog;
import com.evaneos.android.watchme.fragment.RoomListFragment;
import com.evaneos.android.watchme.helper.CalendarHelper;
import com.evaneos.android.watchme.rest.model.Room;
import io.fabric.sdk.android.Fabric;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by Guillaume 'DarzuL' Bourderye on 07/11/15.
 *
 * First activity in hierarchy
 */
public class MainActivity extends AppCompatActivity implements RoomListFragment.Listener, InfoDialog.Callback {

    private static final String CALENDAR_LIST_FRAGMENT_TAG = "RoomListFragment";
    private static final int READ_CALENDAR_PERMISSION_REQUEST = 100;
    private static final int WRITE_CALENDAR_PERMISSION_REQUEST = 101;
    private static final String READ_CALENDAR_DIALOG = "ReadCalendarDialog";
    private static final String WRITE_CALENDAR_DIALOG = "WriteCalendarDialog";
    private static final int READ_CALENDAR_PERMISSION_DIALOG_ID = 100;
    private static final int WRITE_CALENDAR_PERMISSION_DIALOG_ID = 101;
    private boolean mReadCalendarPermissionGranted = false;
    private boolean mWriteCalendarPermissionGranted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);

        checkReadCalendarPermission();
        checkWriteCalendarPermission();
    }

    private void setupFragment() {
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentByTag(CALENDAR_LIST_FRAGMENT_TAG);
        if (fragment == null) {
            fm.beginTransaction() //
                    .add(R.id.container, RoomListFragment.newInstance(), CALENDAR_LIST_FRAGMENT_TAG) //
                    .commit();
        }
    }

    private boolean checkReadCalendarPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR) !=
                PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_CALENDAR)) {

                InfoDialog.newInstance( //
                        READ_CALENDAR_PERMISSION_DIALOG_ID, //
                        getString(R.string.dialogTitle_readCalendar_permision), //
                        getString(R.string.dialogContent_readCalendar_permision), //
                        R.string.action_understood //
                ).show(getSupportFragmentManager(), READ_CALENDAR_DIALOG);

            } else {
                askReadCalendarPermission();
            }

            return false;
        } else {
            mReadCalendarPermissionGranted = true;
            checkIfAllPermissionsAreGranted();
            return true;
        }
    }

    private void askReadCalendarPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CALENDAR},
                READ_CALENDAR_PERMISSION_REQUEST);
    }

    private boolean checkWriteCalendarPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR) !=
                PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_CALENDAR)) {

                InfoDialog.newInstance( //
                        WRITE_CALENDAR_PERMISSION_DIALOG_ID, //
                        getString(R.string.dialogTitle_writeCalendar_permision), //
                        getString(R.string.dialogContent_writeCalendar_permision), //
                        R.string.action_understood //
                ).show(getSupportFragmentManager(), WRITE_CALENDAR_DIALOG);

            } else {
                askWriteCalendarPermission();
            }

            return false;
        } else {
            mWriteCalendarPermissionGranted = true;
            checkIfAllPermissionsAreGranted();
            return true;
        }
    }

    private void askWriteCalendarPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_CALENDAR},
                WRITE_CALENDAR_PERMISSION_REQUEST);
    }

    private void checkIfAllPermissionsAreGranted() {
        if (mReadCalendarPermissionGranted && mWriteCalendarPermissionGranted) {
            setupFragment();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case READ_CALENDAR_PERMISSION_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    onPermissionRefused();
                }
                return;
            }
            case WRITE_CALENDAR_PERMISSION_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    onPermissionRefused();
                }
                return;
            }
        }
    }

    private void onPermissionRefused() {
        Toast.makeText(this, R.string.text_useless_app_without_permissions, Toast.LENGTH_LONG).show();
        finish();
    }

    @Override
    public void onAction(int dialogId, int actionRes) {
        switch (dialogId) {

            case READ_CALENDAR_PERMISSION_DIALOG_ID:
                if (actionRes == R.string.action_understood) {
                    askReadCalendarPermission();
                }
                break;

            case WRITE_CALENDAR_PERMISSION_DIALOG_ID:
                if (actionRes == R.string.action_understood) {
                    askWriteCalendarPermission();
                }
                break;
        }
    }

    @Override
    public void onReserveRoomRequest(Room room) {
        if (!checkReadCalendarPermission() || !checkWriteCalendarPermission()) {
            return;
        }

        if (CalendarHelper.isUserBusy(this)) {
            Toast.makeText(this, R.string.text_cannot_be_in_two_at_the_same_time, Toast.LENGTH_SHORT).show();
            return;
        }

        long now = Calendar.getInstance().getTimeInMillis();
        ContentValues cv = new ContentValues();
        cv.put(CalendarContract.Events.CALENDAR_ID, room.getCalendarId());
        cv.put(CalendarContract.Events.TITLE, getString(R.string.title_defaultReservation));
        cv.put(CalendarContract.Events.DTSTART, now);
        cv.put(CalendarContract.Events.DTEND, now + EventValues.DURATION);
        cv.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().getID());

        if (!checkWriteCalendarPermission()) {
            return;
        }

        Uri uri = getContentResolver().insert( //
                CalendarContract.Events.CONTENT_URI, //
                cv //
        );

        long eventId = Long.parseLong(uri.getLastPathSegment());

        Cursor c = getContentResolver().query( //
                CalendarContract.Events.CONTENT_URI, //
                new String[]{ //
                        CalendarContract.Events.ACCOUNT_NAME //
                }, //
                CalendarContract.Events._ID + "=" + eventId, //
                null, //
                null //
        );

        if (c == null || c.getCount() == 0) {
            return;
        }

        Toast.makeText(this, R.string.action_is_booking, Toast.LENGTH_SHORT).show();

        c.moveToFirst();
        saveUserInformation(c.getString(0));
    }

    private void saveUserInformation(String userCalendarName) {
        getSharedPreferences(UserInformation.PREF_FILE, MODE_PRIVATE).edit()//
                .putString(UserInformation.KEY_USER_ACCOUNT_NAME, userCalendarName) //
                .apply();
    }
}
