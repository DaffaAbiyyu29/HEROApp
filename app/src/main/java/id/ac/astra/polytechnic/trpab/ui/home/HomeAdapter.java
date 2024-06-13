package id.ac.astra.polytechnic.trpab.ui.home;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.util.List;
import id.ac.astra.polytechnic.trpab.R;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ViewHolder> {

    private List<HomeItem> homeItemList;

    public HomeAdapter(List<HomeItem> homeItemList) {
        this.homeItemList = homeItemList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_dashboard, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HomeItem item = homeItemList.get(position);
        holder.titleTextView.setText(item.getTitle());
        holder.hoursTextView.setText(item.getHours());
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
    }

    @Override
    public int getItemCount() {
        return homeItemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView titleTextView;
        public TextView hoursTextView;
        public TextView statusTextView;
        public ImageView imageView;
        public ImageView icoonStatus;
        public CardView statusBarView;

        public ViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.heavyname_text);
            hoursTextView = itemView.findViewById(R.id.hoursmeter_text);
            statusTextView = itemView.findViewById(R.id.status_text);
            imageView = itemView.findViewById(R.id.image_view);
            icoonStatus = itemView.findViewById(R.id.icon_status);
            statusBarView = itemView.findViewById(R.id.status_bar);
        }
    }

}
