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

import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import id.ac.astra.polytechnic.trpab.data.adapter.ChecklistAdapter;
import id.ac.astra.polytechnic.trpab.data.api.ApiClient;
import id.ac.astra.polytechnic.trpab.data.api.ApiService;
import id.ac.astra.polytechnic.trpab.data.api.DataResponse;
import id.ac.astra.polytechnic.trpab.data.model.ChecklistItem;
import id.ac.astra.polytechnic.trpab.data.model.SaveAction;
import id.ac.astra.polytechnic.trpab.ui.activity.MainActivity;
import id.ac.astra.polytechnic.trpab.R;
import id.ac.astra.polytechnic.trpab.databinding.FragmentActionlistBinding;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActionScheduleFragment extends Fragment implements ChecklistAdapter.OnItemClickListener {

    // Existing fields
    private FragmentActionlistBinding binding;
    private RecyclerView recyclerView;
    private ChecklistAdapter checklistAdapter;
    private List<ChecklistItem> schaduleItemList;
    private String schid;
    private static final String TAG = "ActionScheduleFragment";
    private MaterialButton btnSave;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentActionlistBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        recyclerView = view.findViewById(R.id.recycler_view_schadulelist);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        btnSave = view.findViewById(R.id.btn_save);

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

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveActionItems();
            }
        });

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
                    Log.d(TAG, "Response JSON: " + new Gson().toJson(response.body()));
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

    private void saveActionItems() {
        List<SaveAction> actions = new ArrayList<>();
        boolean isAnyItemChecked = false;

        for (ChecklistItem item : schaduleItemList) {
            Log.d(TAG, "Item ID: " + item.getId() + " isChecked: " + item.isChecked()); // Debug log
            SaveAction saveAction = new SaveAction();
            saveAction.setActid(item.getId());
            saveAction.setResultcheck(item.isChecked() ? "1" : "0");

            if (item.isChecked()) {
                isAnyItemChecked = true;
            }

            actions.add(saveAction);
        }

        if (!isAnyItemChecked) {
            Toast.makeText(getContext(), "Please select at least one item to save.", Toast.LENGTH_SHORT).show();
            return;
        }

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("sch_id", schid);
        JsonArray jsonArray = new JsonArray();

        for (SaveAction action : actions) {
            JsonObject actionObject = new JsonObject();
            actionObject.addProperty("act_id", action.getActid());
            actionObject.addProperty("result_check", action.getResultcheck());
            jsonArray.add(actionObject);
        }

        jsonObject.add("actions", jsonArray);
        String json = new Gson().toJson(jsonObject);

        RequestBody body = RequestBody.create(MediaType.parse("application/json"), json);

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<JsonObject> call = apiService.saveAction(body);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Response JSON: " + response.body().toString());
                    Toast.makeText(getContext(), "Data saved successfully", Toast.LENGTH_SHORT).show();

                    // Navigate to MaintenanceFragment
                    NavController navController = NavHostFragment.findNavController(ActionScheduleFragment.this);
                    navController.navigate(R.id.nav_maintenance);

                } else {
                    Log.e(TAG, "Failed to save data: " + response.errorBody());
                    Toast.makeText(getContext(), "Failed to save data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
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
        args.putString("id", clickedItem.getId());
        args.putString("title", clickedItem.getName());
        args.putString("foto", clickedItem.getFoto());
        args.putString("keterangan", clickedItem.getKeterangan());

        navController.navigate(R.id.action_to_workmanshipDetailsFragment, args);
    }
}

