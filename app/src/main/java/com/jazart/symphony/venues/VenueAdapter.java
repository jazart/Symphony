package com.jazart.symphony.venues;

import androidx.annotation.NonNull;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.jazart.symphony.R;
import com.jazart.symphony.model.venues.Venue;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class VenueAdapter extends RecyclerView.Adapter<VenueAdapter.VenueHolder> {

    private List<Venue> mVenueList;
    private final RequestManager mGlide;

    VenueAdapter(RequestManager glide) {
        mGlide = glide;
    }

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

        public static final String TAG = "VenueHolder";
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

        void bind(final Venue venue) {
            CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(itemView.getContext());
            circularProgressDrawable.setStrokeWidth(5f);
            circularProgressDrawable.setCenterRadius(30f);
            circularProgressDrawable.start();
            mGlide.load(venue.getImageUri())
                    .apply(new RequestOptions()
                            .centerCrop()
                            .placeholder(circularProgressDrawable)
                            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC))
                    .into(mVenuePicIv);

            mVenueNameTv.setText(venue.getName());
            mVenueAddressTv.setText(venue.getLocation().getAddress());
        }

        @Override
        public void onClick(View v) {
        }
    }
}
