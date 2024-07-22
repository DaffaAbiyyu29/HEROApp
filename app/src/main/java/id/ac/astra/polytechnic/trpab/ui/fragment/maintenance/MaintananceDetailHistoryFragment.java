package id.ac.astra.polytechnic.trpab.ui.fragment.maintenance;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import id.ac.astra.polytechnic.trpab.R;
import id.ac.astra.polytechnic.trpab.data.adapter.ChecklistAdapter;
import id.ac.astra.polytechnic.trpab.data.adapter.RiwayatAdapter;
import id.ac.astra.polytechnic.trpab.data.api.ApiClient;
import id.ac.astra.polytechnic.trpab.data.api.ApiService;
import id.ac.astra.polytechnic.trpab.data.api.DataResponse;
import id.ac.astra.polytechnic.trpab.data.model.ChecklistItem;
import id.ac.astra.polytechnic.trpab.data.model.RiwayatMaintenance;
import id.ac.astra.polytechnic.trpab.databinding.FragmentRiwayatMaintananceBinding;
import id.ac.astra.polytechnic.trpab.ui.activity.MainActivity;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MaintananceDetailHistoryFragment extends Fragment {
    private RecyclerView recyclerView;
    private RiwayatAdapter mRiwayatAdapter;
    private List<RiwayatMaintenance> riwayatMaintenanceList;
    private String untid;
    private FragmentRiwayatMaintananceBinding binding;
    private static final String TAG = "MaintananceDetailHistory";

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentRiwayatMaintananceBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        recyclerView = view.findViewById(R.id.recycler_riwayat_maintenance_history);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        TextView titleTextView = binding.perbaikan;

        Bundle args = getArguments();
        if (args != null) {
            String schaduleId = args.getString("title", "Default Title");
            untid = args.getString("id");

            titleTextView.setText(schaduleId);
        }

        mRiwayatAdapter = new RiwayatAdapter(new ArrayList<>());
        recyclerView.setAdapter(mRiwayatAdapter);

        fetchRiwayatMaintenanceData();

        ((MainActivity) getActivity()).showBackButton();

        return view;
    }

    private void fetchRiwayatMaintenanceData() {
        String json = "{\"unt_id\": \"" + untid + "\"}";
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), json);

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<ResponseBody> call = apiService.getRiwayatMaintanance(body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String responseBody = response.body().string();
                        JsonObject jsonObject = new Gson().fromJson(responseBody, JsonObject.class);
                        JsonArray dataArray = jsonObject.getAsJsonArray("data");

                        Type maintenanceListType = new TypeToken<List<RiwayatMaintenance>>() {}.getType();
                        List<RiwayatMaintenance> maintenanceList = new Gson().fromJson(dataArray, maintenanceListType);

                        if (maintenanceList == null || maintenanceList.isEmpty()) {
                            Log.d(TAG, "No maintenance data found");
                            // Optionally, you can update the adapter with an empty list
                            mRiwayatAdapter.updateData(new ArrayList<>());
                        } else {
                            mRiwayatAdapter.updateData(maintenanceList);
                        }

                        recyclerView.setAdapter(mRiwayatAdapter);
                    } catch (IOException e) {
                        Log.e(TAG, "Error reading response body: " + e.getMessage());
                        Toast.makeText(getContext(), "Error reading response body", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing JSON: " + e.getMessage());
                        Toast.makeText(getContext(), "Error parsing JSON", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e(TAG, "Failed to load data: " + response.errorBody());
                    Toast.makeText(getContext(), "Failed to load data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "Error: " + t.getMessage());
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}