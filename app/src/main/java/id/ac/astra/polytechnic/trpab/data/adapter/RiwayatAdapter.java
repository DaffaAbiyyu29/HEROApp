package id.ac.astra.polytechnic.trpab.data.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import id.ac.astra.polytechnic.trpab.R;
import id.ac.astra.polytechnic.trpab.data.model.RiwayatMaintenance;

public class RiwayatAdapter extends RecyclerView.Adapter<RiwayatAdapter.ViewHolder> {
    private List<RiwayatMaintenance> mRiwayatMaintenances;
    private OnPrintPdfClickListener onPrintPdfClickListener;

    public RiwayatAdapter(List<RiwayatMaintenance> riwayatMaintenances, OnPrintPdfClickListener listener) {
        this.mRiwayatMaintenances = riwayatMaintenances;
        this.onPrintPdfClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_history_maintanance, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RiwayatMaintenance maintenance = mRiwayatMaintenances.get(position);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String formattedDate = dateFormat.format(maintenance.getPbkTanggalAkhir());

        holder.namaPembuatTextView.setText(maintenance.getNamaPembuat());
        holder.pbkTanggalAkhirTextView.setText(formattedDate);
        holder.pbkJamAkhirTextView.setText(maintenance.getPbkJamAkhir());
        holder.untHoursMeterTextView.setText(String.valueOf(maintenance.getUntHoursMeter()));

        holder.buttonPrint.setOnClickListener(v -> {
            if (onPrintPdfClickListener != null) {
                onPrintPdfClickListener.onPrintPdfClick(
                        position,
                        String.valueOf(maintenance.getUntId()),
                        String.valueOf(maintenance.getPbkId())
                );
            }
        });
    }

    @Override
    public int getItemCount() {
        return (mRiwayatMaintenances != null) ? mRiwayatMaintenances.size() : 0;
    }

    public void updateData(List<RiwayatMaintenance> newData) {
        this.mRiwayatMaintenances.clear();
        this.mRiwayatMaintenances.addAll(newData);
        notifyDataSetChanged();
    }

    public interface OnPrintPdfClickListener {
        void onPrintPdfClick(int position, String untId, String pbkId); // Ensure parameters match here
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView namaPembuatTextView;
        TextView pbkTanggalAkhirTextView;
        TextView pbkJamAkhirTextView;
        TextView untHoursMeterTextView;
        Button buttonPrint;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            namaPembuatTextView = itemView.findViewById(R.id.user);
            pbkTanggalAkhirTextView = itemView.findViewById(R.id.bb);
            pbkJamAkhirTextView = itemView.findViewById(R.id.cc);
            untHoursMeterTextView = itemView.findViewById(R.id.hoursmeter_text);
            buttonPrint = itemView.findViewById(R.id.button_cetak_pdf);
        }
    }
}

