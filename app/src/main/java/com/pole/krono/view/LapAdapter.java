package com.pole.krono.view;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.pole.krono.MyChronometer;
import com.pole.krono.R;
import com.pole.krono.model.Lap;

import java.util.ArrayList;
import java.util.List;

public class LapAdapter extends RecyclerView.Adapter<LapAdapter.ViewHolder> {

    private static final String TAG = "Pole: LapAdapter";

    private LayoutInflater layoutInflater;
    private List<Lap> laps;

    LapAdapter(Context context) {
        layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a new view
        // set the view's size, margins, paddings and layout parameters
        return new ViewHolder(layoutInflater.inflate(R.layout.recycler_item_lap, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.setLap(position);
    }

    void setLaps(List<Lap> laps){
        this.laps = laps;
        Log.v(TAG, "ChronoFragment: added " + laps.size() + " laps");
        notifyDataSetChanged();
    }

    void clear() {
        laps = new ArrayList<>();
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (laps != null)
            return laps.size();
        else return 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView lapTimeTextView;
        private TextView gapTextView;
        private TextView lapNumberTextView;

        ViewHolder(View itemView) {
            super(itemView);

            lapTimeTextView = itemView.findViewById(R.id.lapTimeTextView);
            gapTextView = itemView.findViewById(R.id.gapTextView);
            lapNumberTextView = itemView.findViewById(R.id.lapNumberTextView);

        }

        void setLap(int pos) {
            Lap lap = laps.get(pos);
            if(lap != null) {
                lapTimeTextView.setText(MyChronometer.getTimeString(lap.time));
                lapNumberTextView.setText(String.valueOf(lap.lapNumber));
                if(pos < laps.size() - 1) {
                    long gap = lap.time - laps.get(pos + 1).time;
                    gapTextView.setText(MyChronometer.getGapString(gap));
                    gapTextView.setTextColor(gap > 0 ? Color.RED : Color.GREEN);
                } else
                    gapTextView.setText("");
            } else {
                lapTimeTextView.setText(R.string.loading);
                gapTextView.setText(R.string.loading);
                lapNumberTextView.setText(R.string.loading);
            }
        }
    }
}
