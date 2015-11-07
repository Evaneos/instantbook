package com.evaneos.android.watchme.fragment;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.evaneos.android.watchme.R;
import com.evaneos.android.watchme.adapter.RoomAdapter;
import com.evaneos.android.watchme.helper.CalendarHelper;
import com.evaneos.android.watchme.rest.model.Room;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Guillaume 'DarzuL' Bourderye on 07/11/15.
 *
 * Screen to display the rooms
 */
public class RoomListFragment extends Fragment implements RoomAdapter.Listener, LoaderManager.LoaderCallbacks<Cursor> {

    private static final int LOADER_ID = 1;
    private Listener mListener;
    private RoomAdapter mAdapter;
    private List<Room> mRooms = new LinkedList<>();

    public static RoomListFragment newInstance() {
        Bundle args = new Bundle();
        RoomListFragment fragment = new RoomListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        getLoaderManager().initLoader(LOADER_ID, null, this);

        mAdapter = new RoomAdapter(mRooms, this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_room_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAdapter.setContext(getContext());
        setupList(view);
    }

    private void setupList(View rootView) {
        final RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.list);
        recyclerView.setHasFixedSize(true);
        recyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop,
                                       int oldRight, int oldBottom) {
                v.removeOnLayoutChangeListener(this);

                mAdapter.setItemSize(right - left, getResources().getDimensionPixelSize(R.dimen.roomItemHeight));
                RecyclerView recyclerView = (RecyclerView) v;
                recyclerView.setAdapter(mAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            }
        });
    }

    @Override
    public void onReserveRoomRequest(Room room) {
        mListener.onReserveRoomRequest(room);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mListener = (Listener) activity;

    }

    @Override
    public void onDetach() {
        mListener = null;
        super.onDetach();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader( //
                getContext(), //
                CalendarQuery.URI, //
                CalendarQuery.PROJECTION, //
                null, //
                null, //
                CalendarQuery.SORT //
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.e("Toto", "onLoadFinished()");
        mRooms.clear();
        while (data.moveToNext()) {
            String id = data.getString(CalendarQuery.ID);
            String name = data.getString(CalendarQuery.NAME);
            boolean isDirty = data.getInt(CalendarQuery.DIRTY) == 1;
            mRooms.add(new Room(id, name, isDirty, 56594));
        }

        filterRooms();
        mAdapter.notifyDataSetChanged();
    }

    /**
     * TODO hack - Because this is weleanit, we have to take shortcuts !
     */
    private void filterRooms() {
        Room.clearCalendarIdInWhiteList();
        List<Room> roomToRemove = new LinkedList<>();
        for (Room room : mRooms) {
            if (!Room.WHITE_LIST_CALENDAR_NAME.contains(room.getName())) {
                roomToRemove.add(room);
            } else {
                Room.addCalendarIdInWhiteList(room);
            }
        }
        mRooms.removeAll(roomToRemove);
    }

    private void moveOurRoomAtTop() {
        Integer currentRoomId = CalendarHelper.getUserCurrentRoomId(getContext());
        if (currentRoomId == null) {
            return;
        }

        //        mRooms.
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mRooms.clear();
        mAdapter.notifyDataSetChanged();
    }

    public interface CalendarQuery {
        int ID = 0;
        int NAME = 1;
        int DIRTY = 2;

        Uri URI = CalendarContract.Calendars.CONTENT_URI;
        String[] PROJECTION = new String[]{ //
                CalendarContract.Calendars._ID, //
                CalendarContract.Calendars.CALENDAR_DISPLAY_NAME, //
                CalendarContract.Calendars.DIRTY //
        };
        String SORT = CalendarContract.Calendars.CALENDAR_DISPLAY_NAME + " ASC";
    }

    public interface Listener {
        void onReserveRoomRequest(Room room);
    }
}
