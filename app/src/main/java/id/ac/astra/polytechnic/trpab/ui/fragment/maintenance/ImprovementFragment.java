package id.ac.astra.polytechnic.trpab.ui.fragment.maintenance;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.button.MaterialButton;
import com.google.gson.JsonObject;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import id.ac.astra.polytechnic.trpab.R;
import id.ac.astra.polytechnic.trpab.data.api.ApiClient;
import id.ac.astra.polytechnic.trpab.data.api.ApiService;
import id.ac.astra.polytechnic.trpab.data.model.Perbaikan;
import id.ac.astra.polytechnic.trpab.data.model.SavePerbaikan;
import id.ac.astra.polytechnic.trpab.databinding.FragmentPerbaikanBinding;
import id.ac.astra.polytechnic.trpab.ui.activity.MainActivity;
import id.ac.astra.polytechnic.trpab.ui.fragment.others.PopupResponseDialog;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ImprovementFragment extends Fragment {

    private static final String TAG = "ImprovementFragment";

    private FragmentPerbaikanBinding binding;
    private MaterialButton btnSave;
    private EditText detailCatatanEditText;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPerbaikanBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        TextView titleTextView3 = view.findViewById(R.id.textView3);
        titleTextView3.setText("Data Perbaikan");

        TextView titleTextView4 = view.findViewById(R.id.textView4);

        // Retrieve Perbaikan object from Bundle
        Bundle args = getArguments();
        if (args != null) {
            String title = args.getString("title", "Default Title");
            titleTextView4.setText(title);
        }

        detailCatatanEditText = view.findViewById(R.id.detail_catatan);

        btnSave = view.findViewById(R.id.simpan);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savePerbaikan();
            }
        });

        ((MainActivity) getActivity()).showBackButton();

        return view;
    }

    private void savePerbaikan() {
        // Mendapatkan data yang diperlukan
        String pbkId = getArguments() != null ? getArguments().getString("pbk_id", "") : "";
        String keterangan = detailCatatanEditText.getText().toString();

        // Membuat objek SavePerbaikan
        SavePerbaikan savePerbaikan = new SavePerbaikan();
        savePerbaikan.setPbk_id(pbkId);
        savePerbaikan.setKeterangan(keterangan);
        savePerbaikan.setCreadate(new Date());

        // Konversi ke RequestBody
        Gson gson = new Gson();
        String json = gson.toJson(savePerbaikan);
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), json);

        // Memanggil API
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<JsonObject> call = apiService.savePerbaikan(body);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Tanggapan berhasil
                    Toast.makeText(getContext(), "Data berhasil disimpan", Toast.LENGTH_SHORT).show();

                    PopupResponseDialog dialogFragment = PopupResponseDialog.newInstance(
                            "Berhasil !",
                            "Unit berhasil dikembalikan",
                            R.drawable.ic_success
                    );
                    dialogFragment.show(getParentFragmentManager(), "PopupResponseDialog");

                    NavController navController = NavHostFragment.findNavController(ImprovementFragment.this);
                    navController.navigate(R.id.nav_maintenance);
                } else {
                    // Tanggapan gagal
                    Toast.makeText(getContext(),"Gagal menyimpan data" , Toast.LENGTH_SHORT).show();
                    PopupResponseDialog dialogFragment = PopupResponseDialog.newInstance(
                            "Gagal !",
                            "Gagal menyimpan data Unit",
                            R.drawable.ic_warning
                    );
                    dialogFragment.show(getParentFragmentManager(), "PopupResponseDialog");
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                // Gagal koneksi atau kesalahan lainnya
                Toast.makeText(getContext(), "Terjadi kesalahan: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
