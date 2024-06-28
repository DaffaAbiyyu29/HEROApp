package id.ac.astra.polytechnic.trpab.data.adapter;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import id.ac.astra.polytechnic.trpab.R;
import id.ac.astra.polytechnic.trpab.data.model.HeavyEngine;

public class HeavyEngineAdapter extends RecyclerView.Adapter<HeavyEngineAdapter.ViewHolder> {

    private List<HeavyEngine> mHeavyEngineList;
    private boolean isMaintenanceProcessFragment;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void updateData(List<HeavyEngine> newItems) {
        this.mHeavyEngineList = newItems;
        notifyDataSetChanged();
    }


    public HeavyEngine getItem(int position) {
        return mHeavyEngineList.get(position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public HeavyEngineAdapter(List<HeavyEngine> heavyEngineList, boolean isMaintenanceProcessFragment) {
        this.mHeavyEngineList = heavyEngineList;
        this.isMaintenanceProcessFragment = isMaintenanceProcessFragment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_dashboard, parent, false);
        return new ViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HeavyEngine item = mHeavyEngineList.get(position);
        holder.titleTextView.setText(item.getTitle());
        holder.hoursTextView.setText(item.getHours() + " Hours");
        holder.statusTextView.setText(item.getStatus());
        holder.imageView.setImageResource(item.getImageResId());

        if (item.getStatus().equals("Tersedia")) {
            holder.statusBarView.setCardBackgroundColor(Color.parseColor("#038100"));
            holder.icoonStatus.setImageResource(R.drawable.ic_task);
            holder.icoonStatus.setImageTintList(ColorStateList.valueOf(Color.parseColor("#038100")));
        } else if (item.getStatus().equals("Sedang Dalam Perawatan")) {
            holder.statusBarView.setCardBackgroundColor(Color.parseColor("#CC9239"));
            holder.icoonStatus.setImageResource(R.drawable.ic_tool);
            holder.icoonStatus.setImageTintList(ColorStateList.valueOf(Color.parseColor("#CC9239")));
        } else if (item.getStatus().equals("Sedang Digunakan")) {
            holder.statusBarView.setCardBackgroundColor(Color.parseColor("#600D08"));
            holder.icoonStatus.setImageResource(R.drawable.ic_bag);
            holder.icoonStatus.setImageTintList(ColorStateList.valueOf(Color.parseColor("#600D08")));
        }

        // Mengatur visibilitas statusBarView berdasarkan nilai isMaintenanceProcessFragment
        if (isMaintenanceProcessFragment) {
            holder.statusBarView.setVisibility(View.GONE);
            holder.icoonStatus.setVisibility(View.GONE);
        } else {
            holder.statusBarView.setVisibility(View.VISIBLE);
            holder.icoonStatus.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return mHeavyEngineList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView titleTextView;
        public TextView hoursTextView;
        public TextView statusTextView;
        public ImageView imageView;
        public ImageView icoonStatus;
        public CardView statusBarView;

        public ViewHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.heavyname_text);
            hoursTextView = itemView.findViewById(R.id.hoursmeter_text);
            statusTextView = itemView.findViewById(R.id.status_text);
            imageView = itemView.findViewById(R.id.image_view);
            icoonStatus = itemView.findViewById(R.id.icon_status);
            statusBarView = itemView.findViewById(R.id.status_bar);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }
}
