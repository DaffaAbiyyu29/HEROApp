package id.ac.astra.polytechnic.trpab.ui.maintenance;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import id.ac.astra.polytechnic.trpab.R;

public class DetailHeavyEngineDialogFragment extends DialogFragment {

    private static final String ARG_TITLE = "title";
    private static final String ARG_NAME = "name";
    private static final String ARG_HM = "description";
    private static final String ARG_IMAGE_RES_ID = "image_res_id";

    private Calendar calendar;
    private Calendar calendar2;
    private EditText startDate;
    private EditText endDate;
    private Spinner spinner;
    private TextView detail_keterangan_text;
    private TextView detail_pj_text;
    private TextView detail_jenis_perawatan_text;
    private EditText detail_keterangan_value;
    private MaterialCardView note_card;
    private TextView detail_diajukan_date_end_text;
    private TextView detail_catatan_text;
    TextInputEditText hm1, hm2, hm3, hm4, hm5, hm6, hm7, hm8, hm9, hm10, hm11;
    public DetailHeavyEngineDialogFragment() {
        // Required empty public constructor
    }

    public static DetailHeavyEngineDialogFragment newInstance(String title, String name, String description, int imageResId) {
        DetailHeavyEngineDialogFragment fragment = new DetailHeavyEngineDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putString(ARG_NAME, name);
        args.putString(ARG_HM, description);
        args.putInt(ARG_IMAGE_RES_ID, imageResId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.detail_heavy_engine_card, container, false);

        spinner = view.findViewById(R.id.detail_jenis_perawatan_spinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                getContext(),
                R.array.jenis_perawatan_array,
                android.R.layout.simple_spinner_item
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        // Mendapatkan referensi ke elemen di dalam dialog
        TextView detailTitle = view.findViewById(R.id.detail_title);
        TextView detailHeavyName = view.findViewById(R.id.detail_heavy_name);
        ImageView detailImage = view.findViewById(R.id.detail_image);
        detail_keterangan_text = view.findViewById(R.id.detail_keterangan_text);
        detail_pj_text = view.findViewById(R.id.detail_pj_text);
        detail_jenis_perawatan_text = view.findViewById(R.id.detail_jenis_perawatan_text);
        detail_keterangan_value = view.findViewById(R.id.detail_keterangan_value);
        detail_diajukan_date_end_text = view.findViewById(R.id.detail_diajukan_date_end_text);
        detail_catatan_text = view.findViewById(R.id.detail_catatan_text);
        note_card = view.findViewById(R.id.note_card);
        hm1 = view.findViewById(R.id.hm1);
        hm2 = view.findViewById(R.id.hm2);
        hm3 = view.findViewById(R.id.hm3);
        hm4 = view.findViewById(R.id.hm4);
        hm5 = view.findViewById(R.id.hm5);
        hm6 = view.findViewById(R.id.hm6);
        hm7 = view.findViewById(R.id.hm7);
        hm8 = view.findViewById(R.id.hm8);
        hm9 = view.findViewById(R.id.hm9);
        hm10 = view.findViewById(R.id.hm10);
        hm11 = view.findViewById(R.id.hm11);
        startDate = view.findViewById(R.id.detail_diajukan_date_start);
        endDate = view.findViewById(R.id.detail_diajukan_date_end);

        // Inisialisasi kalender
        calendar = Calendar.getInstance();
        calendar2 = Calendar.getInstance();

        // Mengambil data dari argumen
        Bundle args = getArguments();
        if (args != null) {
            String title = args.getString(ARG_TITLE);
            String name = args.getString(ARG_NAME);
            String hoursMeter = args.getString(ARG_HM);
            int imageResId = args.getInt(ARG_IMAGE_RES_ID);

            if (title.equals("Perawatan Alat")){
                perawatanAlatDialog();
            } else if (title.equals("Peminjaman Alat")) {
                peminjamanAlatDialog();
            } else if (title.equals("Persetujuan Alat")) {
                persetujuanAlatDialog();
            } else if (title.equals("Pengembalian Alat")) {
                pengembalianAlatDialog();
            }

            // Pisahkan string menjadi array
            String[] hmArray = hoursMeter.split("");

            // Mengatur teks berdasarkan elemen array
            if (hmArray.length > 1) hm1.setText(hmArray[1]);
            if (hmArray.length > 2) hm2.setText(hmArray[2]);
            if (hmArray.length > 3) hm3.setText(hmArray[3]);
            if (hmArray.length > 4) hm4.setText(hmArray[4]);
            if (hmArray.length > 5) hm5.setText(hmArray[5]);
            if (hmArray.length > 6) hm6.setText(hmArray[6]);
            if (hmArray.length > 7) hm7.setText(hmArray[7]);
            if (hmArray.length > 8) hm8.setText(hmArray[8]);
            if (hmArray.length > 9) hm9.setText(hmArray[9]);
            if (hmArray.length > 10) hm10.setText(hmArray[10]);
            if (hmArray.length > 11) hm11.setText(hmArray[11]);

            // Set data dari argumen ke dalam dialog
            detailTitle.setText(title);
            detailHeavyName.setText(name); // Menampilkan name
            detailImage.setImageResource(imageResId);
        }

        // Mendapatkan tanggal dan waktu saat ini
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, d MMMM yyyy", new Locale("id", "ID"));
        String currentDateTime = dateFormat.format(calendar.getTime());
        startDate.setText(currentDateTime);

        // Menampilkan DatePicker dan TimePicker saat startDate diklik
        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateTimePicker();
            }
        });

        endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateTimePicker2();
            }
        });

        // Pengaturan button cancel
        Button cancelButton = view.findViewById(R.id.detail_button_batal);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Menutup dialog ketika tombol cancel diklik
                dismiss();
            }
        });

        return view;
    }

    private void perawatanAlatDialog() {
        detail_keterangan_text.setVisibility(View.GONE);
        detail_keterangan_value.setVisibility(View.GONE);
        detail_diajukan_date_end_text.setVisibility(View.GONE);
        startDate.setEnabled(false);
        endDate.setVisibility(View.GONE);
        detail_catatan_text.setVisibility(View.GONE);
        note_card.setVisibility(View.GONE);
        hm1.setBackgroundResource(R.color.cream);
        hm2.setBackgroundResource(R.color.cream);
        hm3.setBackgroundResource(R.color.cream);
        hm4.setBackgroundResource(R.color.cream);
        hm5.setBackgroundResource(R.color.cream);
        hm6.setBackgroundResource(R.color.cream);
        hm7.setBackgroundResource(R.color.cream);
        hm8.setBackgroundResource(R.color.cream);
        hm9.setBackgroundResource(R.color.cream);
        hm10.setBackgroundResource(R.color.cream);
        hm11.setBackgroundResource(R.color.cream);
    }

    private void peminjamanAlatDialog() {

    }

    private void persetujuanAlatDialog() {

    }

    private void pengembalianAlatDialog() {
        detail_jenis_perawatan_text.setVisibility(View.GONE);
        spinner.setVisibility(View.GONE);
        detail_pj_text.setText("Diajukan Oleh");
    }

    private void showDateTimePicker() {
        // Membuat DatePickerDialog untuk memilih tanggal
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), R.style.CustomDatePickerDialog, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                // Set tanggal yang dipilih ke kalender
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                // Setelah memilih tanggal, tampilkan TimePickerDialog untuk memilih waktu
                showTimePicker();
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        // Tampilkan DatePickerDialog
        datePickerDialog.show();
    }

    private void showTimePicker() {
        // Membuat TimePickerDialog untuk memilih waktu
        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), R.style.CustomTimePickerDialog, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                // Set waktu yang dipilih ke kalender
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);

                // Format tanggal dan waktu yang dipilih
                SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, d MMMM yyyy", new Locale("id", "ID"));
                String selectedDateTime = dateFormat.format(calendar.getTime());

                // Set tanggal dan waktu yang dipilih ke startDate
                startDate.setText(selectedDateTime);
            }
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);

        // Tampilkan TimePickerDialog
        timePickerDialog.show();
    }

    private void showDateTimePicker2() {
        // Membuat DatePickerDialog untuk memilih tanggal
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), R.style.CustomDatePickerDialog, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                // Set tanggal yang dipilih ke kalender
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                // Setelah memilih tanggal, tampilkan TimePickerDialog untuk memilih waktu
                showTimePicker2();
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        // Tampilkan DatePickerDialog
        datePickerDialog.show();
    }

    private void showTimePicker2() {
        // Membuat TimePickerDialog untuk memilih waktu
        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), R.style.CustomTimePickerDialog, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                // Set waktu yang dipilih ke kalender
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);

                // Format tanggal dan waktu yang dipilih
                SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, d MMMM yyyy", new Locale("id", "ID"));
                String selectedDateTime = dateFormat.format(calendar.getTime());

                // Set tanggal dan waktu yang dipilih ke startDate
                endDate.setText(selectedDateTime);
            }
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);

        // Tampilkan TimePickerDialog
        timePickerDialog.show();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Mengatur lebar dan tinggi dialog (misalnya 80% dari lebar layar)
        if (getDialog() != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            getDialog().getWindow().setLayout(width, height);
        }
    }
}
