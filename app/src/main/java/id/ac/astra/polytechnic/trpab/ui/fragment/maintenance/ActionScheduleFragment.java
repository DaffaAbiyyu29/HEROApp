package id.ac.astra.polytechnic.trpab.ui.fragment.maintenance;

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
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import id.ac.astra.polytechnic.trpab.data.adapter.ChecklistAdapter;
import id.ac.astra.polytechnic.trpab.data.api.ApiClient;
import id.ac.astra.polytechnic.trpab.data.api.ApiService;
import id.ac.astra.polytechnic.trpab.data.api.DataResponse;
import id.ac.astra.polytechnic.trpab.data.model.ChecklistItem;
import id.ac.astra.polytechnic.trpab.ui.activity.MainActivity;
import id.ac.astra.polytechnic.trpab.R;
import id.ac.astra.polytechnic.trpab.databinding.FragmentActionlistBinding;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActionScheduleFragment extends Fragment implements ChecklistAdapter.OnItemClickListener {

    private FragmentActionlistBinding binding;
    private RecyclerView recyclerView;
    private ChecklistAdapter checklistAdapter;
    private List<ChecklistItem> schaduleItemList;
    private String schid;
    private static final String TAG = "ActionScheduleFragment";


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentActionlistBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        recyclerView = view.findViewById(R.id.recycler_view_schadulelist);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        TextView titleTextView = binding.textView2;

        // Retrieve the Schadule ID from arguments
        Bundle args = getArguments();
        if (args != null) {
            String schaduleId = args.getString("title", "Default Title");
            schid = args.getString("id");

            titleTextView.setText(schaduleId);
        }

        schaduleItemList = new ArrayList<>();

        loadServiceItems();

        ((MainActivity) getActivity()).showBackButton();
        return view;
    }

    private void loadServiceItems() {
        String json = "{\"sch_id\": \"" + schid + "\"}";
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), json);

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<DataResponse<ChecklistItem>> call = apiService.getDataAction(body);

        call.enqueue(new Callback<DataResponse<ChecklistItem>>() {
            @Override
            public void onResponse(Call<DataResponse<ChecklistItem>> call, Response<DataResponse<ChecklistItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Response JSON: " + new Gson().toJson(response.body())); // Tambahkan log response
                    schaduleItemList = response.body().getResult();
                    checklistAdapter = new ChecklistAdapter(schaduleItemList, ActionScheduleFragment.this);
                    recyclerView.setAdapter(checklistAdapter);
                } else {
                    Log.e(TAG, "Failed to load data: " + response.errorBody());
                    Toast.makeText(getContext(), "Failed to load data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<DataResponse<ChecklistItem>> call, Throwable t) {
                Log.e(TAG, "Error: " + t.getMessage());
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onItemClick(int position) {
        ChecklistItem clickedItem = schaduleItemList.get(position);
        NavController navController = NavHostFragment.findNavController(this);

        Bundle args = new Bundle();
        args.putString("title", clickedItem.getName());

        navController.navigate(R.id.action_to_workmanshipDetailsFragment, args);
    }
}
