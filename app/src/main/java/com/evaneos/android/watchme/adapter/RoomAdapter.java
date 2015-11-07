package com.evaneos.android.watchme.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.evaneos.android.watchme.R;
import com.evaneos.android.watchme.helper.PictureUrlHelper;
import com.evaneos.android.watchme.rest.model.Room;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Guillaume 'DarzuL' Bourderye on 07/11/15.
 *
 * Adapter which manage room items
 */
public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.RoomViewHolder> {

    private final Listener mListener;
    private final List<Room> mRooms;
    private int mAvailableColor;
    private int mDirtyColor;
    private int mBusyColor;
    private Context mContext;
    private int mItemWidth;
    private int mItemHeight;

    public RoomAdapter(List<Room> rooms, Listener listener) {
        mRooms = rooms;
        mListener = listener;
    }

    public void setContext(Context context) {
        mContext = context;
        mAvailableColor = ContextCompat.getColor(mContext, R.color.roomState_available);
        mDirtyColor = ContextCompat.getColor(mContext, R.color.roomState_dirty);
        mBusyColor = ContextCompat.getColor(mContext, R.color.roomState_busy);
    }

    @Override
    public RoomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new RoomViewHolder(this, LayoutInflater.from(mContext).inflate(R.layout.item_room, parent, false));
    }

    @Override
    public void onBindViewHolder(RoomViewHolder holder, int position) {
        Room room = mRooms.get(position);
        boolean isBusy = room.isBusy(mContext);
        boolean isDirty = room.isDirty();

        holder.mTitle.setText(room.getName());
        Picasso.with(mContext) //
                .load(PictureUrlHelper.buildUrl(room.getPictureId(), mItemWidth, mItemHeight)) //
                .into(holder.mImageView);

        holder.mFab.setVisibility(isBusy ? View.GONE : View.VISIBLE);
        holder.mTitle.setBackgroundColor( //
                isDirty ? mDirtyColor : //
                        isBusy ? mBusyColor : mAvailableColor //
        );
    }

    @Override
    public int getItemCount() {
        return mRooms.size();
    }

    public void setItemSize(int itemWidth, int itemHeight) {
        mItemWidth = itemWidth;
        mItemHeight = itemHeight;
    }

    private void onReserveRoomRequest(int itemPosition) {
        mListener.onReserveRoomRequest(mRooms.get(itemPosition));
    }

    public interface Listener {
        void onReserveRoomRequest(Room room);
    }

    public static class RoomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private RoomAdapter mRoomAdapter;
        private TextView mTitle;
        private ImageView mImageView;
        private View mFab;

        public RoomViewHolder(RoomAdapter roomAdapter, View itemView) {
            super(itemView);
            mRoomAdapter = roomAdapter;
            mTitle = (TextView) itemView.findViewById(R.id.title);
            mImageView = (ImageView) itemView.findViewById(R.id.image);
            mFab = itemView.findViewById(R.id.fab);
            mFab.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mRoomAdapter.onReserveRoomRequest(getAdapterPosition());
        }
    }
}
