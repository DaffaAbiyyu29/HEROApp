package id.ac.astra.polytechnic.trpab.ui.fragment.others;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import id.ac.astra.polytechnic.trpab.R;
import id.ac.astra.polytechnic.trpab.data.api.DataResponse;
import id.ac.astra.polytechnic.trpab.data.model.Penggunaan;
import id.ac.astra.polytechnic.trpab.data.model.Penggunaan;
import id.ac.astra.polytechnic.trpab.data.model.Perbaikan;
import id.ac.astra.polytechnic.trpab.data.model.User;
import id.ac.astra.polytechnic.trpab.data.viewmodel.LoginViewModel;
import id.ac.astra.polytechnic.trpab.data.viewmodel.PenggunaanViewModel;
import id.ac.astra.polytechnic.trpab.data.viewmodel.PerbaikanViewModel;
import id.ac.astra.polytechnic.trpab.ui.activity.LoginActivity;
import id.ac.astra.polytechnic.trpab.ui.activity.MainActivity;

public class DetailHeavyEngineDialogFragment extends DialogFragment {

    private static final String ARG_ID = "id";
    private static final String ARG_TITLE = "title";
    private static final String ARG_NAME = "name";
    private static final String ARG_HM = "description";
    private static final String ARG_IMAGE_RES_ID = "image_res_id";
    private static final String ARG_ID_USER = "id_pgn";
    private PenggunaanViewModel mViewModel;
    private PerbaikanViewModel mViewModel2;
    private LoginViewModel mViewModel3;
    private Calendar calendar;
    private Calendar calendar2;
    private EditText startDate;
    private EditText endDate;
    private Spinner spinner;
    private TextView detail_keterangan_text;
    private TextView detail_diajukan_date_start_text;
    private TextView detail_pj_text;
    private TextView detail_jenis_perawatan_text;
    private EditText detail_keterangan_value;
    private EditText detail_pj_value, detail_diajukan_date_start, detail_diajukan_date_end;
    private MaterialCardView note_card;
    private TextView detail_diajukan_date_end_text;
    private TextView detail_catatan_text;
    private MaterialButton detail_button_setuju;
    TextInputEditText hm1, hm2, hm3, hm4, hm5, hm6, hm7, hm8, hm9, hm10, hm11;
    static String idUnt, idUser;
    String selectedItemText;

    Bundle args;
    private OnPeminjamanAddedListener mListener;

    public void setOnPeminjamanAddedListener(OnPeminjamanAddedListener listener) {
        mListener = listener;
    }

    public DetailHeavyEngineDialogFragment() {
        // Required empty public constructor
    }

    public static DetailHeavyEngineDialogFragment newInstance(String id, String title, String name, String hm, String imageResId, String idPenggunaan) {
        DetailHeavyEngineDialogFragment fragment = new DetailHeavyEngineDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ID, id);
        args.putString(ARG_TITLE, title);
        args.putString(ARG_NAME, name);
        args.putString(ARG_HM, hm);
        args.putString(ARG_IMAGE_RES_ID, imageResId);
        args.putString(ARG_ID_USER, idPenggunaan);
        fragment.setArguments(args);
        idUnt = id;
        idUser = idPenggunaan;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.detail_heavy_engine_card, container, false);

        mViewModel = new ViewModelProvider(this).get(PenggunaanViewModel.class);
        mViewModel2 = new ViewModelProvider(this).get(PerbaikanViewModel.class);
        mViewModel3 = new ViewModelProvider(this).get(LoginViewModel.class);

        spinner = view.findViewById(R.id.detail_jenis_perawatan_spinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                getContext(),
                R.array.jenis_perawatan_array,
                android.R.layout.simple_spinner_item
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Get the selected item text
                selectedItemText = (String) parent.getItemAtPosition(position);
                Log.d("oooo3", selectedItemText);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        // Mendapatkan referensi ke elemen di dalam dialog
        TextView detailTitle = view.findViewById(R.id.detail_title);
        TextView detailHeavyName = view.findViewById(R.id.detail_heavy_name);
        ImageView detailImage = view.findViewById(R.id.detail_image);
        detail_keterangan_text = view.findViewById(R.id.detail_keterangan_text);
        detail_diajukan_date_start_text = view.findViewById(R.id.detail_diajukan_date_start_text);
        detail_pj_text = view.findViewById(R.id.detail_pj_text);
        detail_pj_value = view.findViewById(R.id.detail_pj_value);
        detail_jenis_perawatan_text = view.findViewById(R.id.detail_jenis_perawatan_text);
        detail_keterangan_value = view.findViewById(R.id.detail_keterangan_value);
        detail_diajukan_date_end_text = view.findViewById(R.id.detail_diajukan_date_end_text);
        detail_catatan_text = view.findViewById(R.id.detail_catatan_text);
        detail_diajukan_date_start = view.findViewById(R.id.detail_diajukan_date_start);
        detail_diajukan_date_end = view.findViewById(R.id.detail_diajukan_date_end);
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
        detail_button_setuju = view.findViewById(R.id.detail_button_setuju);

        // Inisialisasi kalender
        calendar = Calendar.getInstance();
        calendar2 = Calendar.getInstance();

        // Mengambil data dari argumen
        args = getArguments();
        if (args != null) {
            String title = args.getString(ARG_TITLE);
            String name = args.getString(ARG_NAME);
            String hoursMeter = args.getString(ARG_HM);
            String imageResId = args.getString(ARG_IMAGE_RES_ID);

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
            Glide.with(detailImage.getContext()).load(imageResId).into(detailImage);
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
        getDataPenggunaan(idUnt);

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

        detail_button_setuju.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int jns = 0;
                if (selectedItemText.equals("Perbaikan")){
                    jns = 1;
                } else if (selectedItemText.equals("Service")){
                    jns = 2;
                }
                Perbaikan perbaikan = new Perbaikan();
                perbaikan.setUntId(args.getString(ARG_ID));
                perbaikan.setJenis(jns);
                perbaikan.setCreatedBy(Integer.valueOf(args.getString(ARG_ID_USER)));

                mViewModel2.createPerbaikan(perbaikan, new PerbaikanViewModel.PerbaikanCallback() {
                    @Override
                    public void onSuccess(String result) {
                        if (mListener != null) {
                            mListener.onPeminjamanAdded();
                        }

                        // Menutup dialog saat ini
                        dismiss();

                        // Menampilkan PopupResponseDialog setelah dialog saat ini ditutup
                        PopupResponseDialog dialogFragment = PopupResponseDialog.newInstance(
                                "Berhasil !",
                                "Selamat perbaikan alat telah berhasil dilakukan",
                                R.drawable.ic_success
                        );
                        dialogFragment.show(getParentFragmentManager(), "PopupResponseDialog");
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        // Handle failure
                        Log.e("oooo", "Failed to create pengajuan: " + t.getMessage());
                    }
                });
            }
        });
    }

    private void peminjamanAlatDialog() {
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
        detail_keterangan_value.setEnabled(true);
        detail_keterangan_value.setTextColor(getResources().getColor(R.color.red_primary));
        detail_jenis_perawatan_text.setVisibility(View.GONE);
        spinner.setVisibility(View.GONE);
        detail_pj_text.setText("Diajukan Oleh");
        startDate.setEnabled(false);
        detail_diajukan_date_end_text.setVisibility(View.GONE);
        endDate.setVisibility(View.GONE);
        detail_catatan_text.setVisibility(View.GONE);
        note_card.setVisibility(View.GONE);
        detail_diajukan_date_start_text.setVisibility(View.GONE);
        startDate.setVisibility(View.GONE);
        detail_pj_text.setVisibility(View.GONE);
        detail_pj_value.setVisibility(View.GONE);
        detail_button_setuju.setText("Pinjam");

        detail_button_setuju.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Penggunaan penggunaan = new Penggunaan();
                penggunaan.setId(args.getString(ARG_ID));
                penggunaan.setKeterangan(String.valueOf(detail_keterangan_value.getText()));
                penggunaan.setCreaby(args.getString(ARG_ID_USER));

                mViewModel.createPengajuan(penggunaan, new PenggunaanViewModel.PenggunaanCallback() {
                    @Override
                    public void onSuccess(String result) {
                        if (mListener != null) {
                            mListener.onPeminjamanAdded();
                        }

                        // Menutup dialog saat ini
                        dismiss();

                        // Menampilkan PopupResponseDialog setelah dialog saat ini ditutup
                        PopupResponseDialog dialogFragment = PopupResponseDialog.newInstance(
                                "Berhasil !",
                                "Selamat peminjaman alat telah berhasil dilakukan",
                                R.drawable.ic_success
                        );
                        dialogFragment.show(getParentFragmentManager(), "PopupResponseDialog");
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        // Handle failure
                        Log.e("oooo", "Failed to create pengajuan: " + t.getMessage());
                    }
                });
            }
        });
    }

    private void persetujuanAlatDialog() {
        getDataPenggunaan(idUnt);

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
        detail_keterangan_value.setEnabled(false);
        detail_keterangan_value.setTextColor(getResources().getColor(R.color.cream));
        detail_jenis_perawatan_text.setVisibility(View.GONE);
        spinner.setVisibility(View.GONE);
        detail_pj_text.setText("Diajukan Oleh");
        startDate.setEnabled(false);
        detail_diajukan_date_end_text.setVisibility(View.GONE);
        endDate.setVisibility(View.GONE);
        detail_catatan_text.setVisibility(View.GONE);
        note_card.setVisibility(View.GONE);
        detail_button_setuju.setText("Setujui");

        detail_button_setuju.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Penggunaan penggunaan = new Penggunaan();
                penggunaan.setId(args.getString(ARG_ID));
                penggunaan.setModiby(args.getString(ARG_ID_USER));

                mViewModel.CreatePersetujuan(penggunaan, new PenggunaanViewModel.PenggunaanCallback() {
                    @Override
                    public void onSuccess(String result) {
                        if (mListener != null) {
                            mListener.onPeminjamanAdded();
                        }

                        // Menutup dialog saat ini
                        dismiss();

                        // Menampilkan PopupResponseDialog setelah dialog saat ini ditutup
                        PopupResponseDialog dialogFragment = PopupResponseDialog.newInstance(
                                "Berhasil !",
                                "Pengajuan telah disetujui.",
                                R.drawable.ic_success
                        );
                        dialogFragment.show(getParentFragmentManager(), "PopupResponseDialog");
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        // Handle failure
                        Log.e("oooo", "Failed to create pengajuan: " + t.getMessage());
                    }
                });
            }
        });
    }

    private void pengembalianAlatDialog() {
        getDataPenggunaan(idUnt);

        detail_jenis_perawatan_text.setVisibility(View.GONE);
        hm1.setEnabled(true);
        hm2.setEnabled(true);
        hm3.setEnabled(true);
        hm4.setEnabled(true);
        hm5.setEnabled(true);
        hm6.setEnabled(true);
        hm7.setEnabled(true);
        hm8.setEnabled(true);
        hm9.setEnabled(true);
        hm10.setEnabled(true);
        hm11.setEnabled(true);
        detail_keterangan_value.setEnabled(false);
        detail_keterangan_value.setTextColor(getResources().getColor(R.color.cream));
        spinner.setVisibility(View.GONE);
        detail_pj_text.setText("Diajukan Oleh");
        startDate.setEnabled(false);
        detail_diajukan_date_start_text.setText("Diajukan Pada");
        endDate.setEnabled(false);
        detail_button_setuju.setText("Kembalikan");

        detail_button_setuju.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringBuilder hmStringBuilder = new StringBuilder();

                hmStringBuilder.append(hm1.getText().toString());
                hmStringBuilder.append(hm2.getText().toString());
                hmStringBuilder.append(hm3.getText().toString());
                hmStringBuilder.append(hm4.getText().toString());
                hmStringBuilder.append(hm5.getText().toString());
                hmStringBuilder.append(hm6.getText().toString());
                hmStringBuilder.append(hm7.getText().toString());
                hmStringBuilder.append(hm8.getText().toString());
                hmStringBuilder.append(hm9.getText().toString());
                hmStringBuilder.append(hm10.getText().toString());
                hmStringBuilder.append(hm11.getText().toString());

                String hm = hmStringBuilder.toString();

                Penggunaan penggunaan = new Penggunaan();
                penggunaan.setId(args.getString(ARG_ID));
                penggunaan.setHoursMeterAkhir(hm);
                penggunaan.setModiby(args.getString(ARG_ID_USER));

                mViewModel.createPengembalian(penggunaan, new PenggunaanViewModel.PenggunaanCallback() {
                    @Override
                    public void onSuccess(String result) {
                        if (mListener != null) {
                            mListener.onPeminjamanAdded();
                        }

                        // Menutup dialog saat ini
                        dismiss();

                        // Menampilkan PopupResponseDialog setelah dialog saat ini ditutup
                        PopupResponseDialog dialogFragment = PopupResponseDialog.newInstance(
                                "Berhasil !",
                                "Unit berhasil dikembalikan",
                                R.drawable.ic_success
                        );
                        dialogFragment.show(getParentFragmentManager(), "PopupResponseDialog");
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        // Handle failure
                        Log.e("oooo", "Failed to create pengajuan: " + t.getMessage());
                    }
                });
            }
        });
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
            // Mendapatkan lebar layar
            int width = getResources().getDisplayMetrics().widthPixels;
            int margin = (int) (40 * getResources().getDisplayMetrics().density);
            getDialog().getWindow().setLayout(width - 2 * margin, ViewGroup.LayoutParams.WRAP_CONTENT);
            getDialog().getWindow().setBackgroundDrawableResource(R.drawable.bottom_nav_background);
        }
    }

    private void getDataPenggunaan(String untId){
        mViewModel.getDataPenggunaan(untId, new PenggunaanViewModel.PenggunaanCallback2() {
            @Override
            public void onSuccess(DataResponse<Penggunaan> result) {
                // Handle success
                String message = String.valueOf(result.getMessage());
                Gson gson = new Gson();
                Log.d("oooologin", gson.toJson(result));
                String jsonResponse = gson.toJson(result.getResult());

                Penggunaan[] pgns = gson.fromJson(jsonResponse, Penggunaan[].class);
                // Buat objek SimpleDateFormat untuk input dan output
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.forLanguageTag("id-ID"));
                SimpleDateFormat outputFormat = new SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.forLanguageTag("id-ID"));

                // Parse tanggal dari string ke objek Date
                Date dateStart = null;
                Date dateEnd = new Date();
                try {
                    dateStart = inputFormat.parse(pgns[0].getCreadate());
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }

                // Format objek Date ke string dalam format yang diinginkan
                String outputDateStart = outputFormat.format(dateStart);
                String outputDate = outputFormat.format(dateEnd);

                detail_keterangan_value.setText(pgns[0].getKeterangan());
                detail_diajukan_date_start.setText(outputDateStart);
                detail_diajukan_date_end.setText(outputDate);

                getUserById(pgns[0].getCreaby());
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("ooo44", "Failed to create : " +t.getMessage());
            }
        });
    }

    private void getUserById(String idUsr){
        mViewModel3.getUserById(idUsr, new LoginViewModel.UserCallback() {
            @Override
            public void onSuccess(DataResponse<User> result) {
                // Handle success
                String message = String.valueOf(result.getMessage());
                Gson gson = new Gson();
                Log.d("oooologin", gson.toJson(result));
                String jsonResponse = gson.toJson(result.getResult());

                User[] users = gson.fromJson(jsonResponse, User[].class);
                detail_pj_value.setText(users[0].getNama());
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("ooo44", "Failed to create : " +t.getMessage());
            }
        });
    }

    public interface OnPeminjamanAddedListener {
        void onPeminjamanAdded();
    }
}
