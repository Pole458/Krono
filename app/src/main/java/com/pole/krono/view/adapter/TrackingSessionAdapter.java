package com.pole.krono.view.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.pole.krono.R;
import com.pole.krono.model.TrackingSession;
import com.pole.krono.view.TrackingSessionActivity;

import java.text.DateFormat;
import java.util.List;

public class TrackingSessionAdapter extends RecyclerView.Adapter<TrackingSessionAdapter.ViewHolder> {

    private LayoutInflater layoutInflater;
    private List<TrackingSession> trackingSessions;
    private Context context;

    public TrackingSessionAdapter(Context context) {
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a new view
        // set the view's size, margins, paddings and layout parameters
        return new ViewHolder(layoutInflater.inflate(R.layout.recycler_view_tracking_session_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.setTrackingSession(trackingSessions.get(position));
    }

    public void setTrackingSession(List<TrackingSession> trackingSession){
        this.trackingSessions = trackingSession;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (trackingSessions != null)
            return trackingSessions.size();
        else return 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView sportTextView;
        private TextView activityTypeTextView;
        private TextView startDateTextView;
        private Long id = -1L;

        ViewHolder(View itemView) {
            super(itemView);

            sportTextView = itemView.findViewById(R.id.sportTextView);
            activityTypeTextView = itemView.findViewById(R.id.activityTypeTextView);
            startDateTextView = itemView.findViewById(R.id.startDateTextView);

            itemView.setOnClickListener(v -> {
                Intent intent = new Intent();
                intent.putExtra("id", id);
                intent.setClass(context, TrackingSessionActivity.class);
                context.startActivity(intent);
            });

        }

        void setTrackingSession(TrackingSession trackingSession) {
            if(trackingSession != null) {
                id = trackingSession.id;
                sportTextView.setText(trackingSession.sport);
                activityTypeTextView.setText(trackingSession.activityType);
                startDateTextView.setText(DateFormat.getDateInstance().format(trackingSession.startTime));
            } else {
                sportTextView.setText(R.string.loading);
                id = -1L;
            }
        }
    }
}
