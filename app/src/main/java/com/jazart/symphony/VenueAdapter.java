package com.jazart.symphony;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jazart.symphony.venues.Venue;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class VenueAdapter extends RecyclerView.Adapter<VenueAdapter.VenueHolder> {

    private List<Venue> mVenueList;

    @NonNull
    @Override
    public VenueHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_venue,
                parent, false);
        return new VenueHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VenueHolder holder, int position) {
        holder.bind(mVenueList.get(position));
    }

    @Override
    public int getItemCount() {
        return mVenueList == null ? 0 : mVenueList.size();
    }

    public void setVenueList(List<Venue> venues) {
        mVenueList = venues;
    }

    class VenueHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.venue_pic_iv)
        ImageView mVenuePicIv;
        @BindView(R.id.venue_name_tv)
        TextView mVenueNameTv;
        @BindView(R.id.venue_event2_tv)
        TextView mVenueEvent2Tv;
        @BindView(R.id.venue_address_tv)
        TextView mVenueAddressTv;
        @BindView(R.id.venue_event_tv)
        TextView mVenueEventTv;

        VenueHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            ButterKnife.bind(this, itemView);
        }

        void bind(Venue venue) {
            mVenueNameTv.setText(venue.getName());
            mVenueAddressTv.setText(venue.getLocation().getAddress());
        }

        @Override
        public void onClick(View v) {

        }
    }
}
