package id.ac.astra.polytechnic.trpab.ui.fragment.maintenance;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import id.ac.astra.polytechnic.trpab.data.api.ApiClient;
import id.ac.astra.polytechnic.trpab.data.api.ApiService;
import id.ac.astra.polytechnic.trpab.data.api.DataResponse;
import id.ac.astra.polytechnic.trpab.data.model.HeavyEngine;
import id.ac.astra.polytechnic.trpab.ui.activity.MainActivity;
import id.ac.astra.polytechnic.trpab.R;
import id.ac.astra.polytechnic.trpab.data.adapter.SchaduleAdapter;
import id.ac.astra.polytechnic.trpab.data.model.Schadule;
import id.ac.astra.polytechnic.trpab.databinding.FragmentSchedulelistBinding;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScheduleListFragment extends Fragment implements SchaduleAdapter.OnItemClickListener {
    private FragmentSchedulelistBinding binding;
    private RecyclerView recyclerView;
    private SchaduleAdapter schaduleAdapter;
    private List<Schadule> serviceItemList;
    private String untid;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSchedulelistBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        recyclerView = binding.recyclerViewActionlist;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        TextView titleTextView = binding.textview3;

        // Initialize the service item list
        serviceItemList = new ArrayList<>();

        // Retrieve and set the title if provided
        Bundle args = getArguments();
        if (args != null) {
            String title = args.getString("title", "Default Title");
            String status = args.getString("status");
            untid = args.getString("id"); // Perubahan dari getInt ke getString
            titleTextView.setText(title);

            // Populate the RecyclerView based on the status
            if ("5".equals(status)) {
                // Load and set service items
                loadServiceItems();
            }
        }

        ((MainActivity) getActivity()).showBackButton();

        return view;
    }

    private void loadServiceItems() {
        // Create JSON body
        String json = "{\"unt_id\": \"" + untid + "\"}";
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), json);

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<DataResponse<Schadule>> call = apiService.getDataSchedule(body);

        call.enqueue(new Callback<DataResponse<Schadule>>() {
            @Override
            public void onResponse(Call<DataResponse<Schadule>> call, Response<DataResponse<Schadule>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    serviceItemList = response.body().getResult();
                    schaduleAdapter = new SchaduleAdapter(serviceItemList, ScheduleListFragment.this);
                    recyclerView.setAdapter(schaduleAdapter);
                } else {
                    Toast.makeText(getContext(), "Failed to load data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<DataResponse<Schadule>> call, Throwable t) {
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onItemClick(int position) {
        Schadule clickedItem = serviceItemList.get(position);
        NavController navController = NavHostFragment.findNavController(this);

        // Fetch HeavyEngine data using the unt_id from the clicked Schadule item
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<DataResponse<HeavyEngine>> call = apiService.getDataUnit();

        call.enqueue(new Callback<DataResponse<HeavyEngine>>() {
            @Override
            public void onResponse(Call<DataResponse<HeavyEngine>> call, Response<DataResponse<HeavyEngine>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    HeavyEngine heavyEngine = response.body().getResult().get(0); // Assuming there's only one item returned

                    Bundle args = new Bundle();
                    args.putString("title", heavyEngine.getTitle());
                    args.putString("id", clickedItem.getId());

                    navController.navigate(R.id.action_to_actionlistfragment, args);
                } else {
                    Toast.makeText(getContext(), "Failed to load HeavyEngine data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<DataResponse<HeavyEngine>> call, Throwable t) {
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


}
