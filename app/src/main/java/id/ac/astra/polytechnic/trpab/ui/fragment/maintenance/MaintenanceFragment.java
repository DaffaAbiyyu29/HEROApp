package id.ac.astra.polytechnic.trpab.ui.fragment.maintenance;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import id.ac.astra.polytechnic.trpab.ui.activity.MainActivity;
import id.ac.astra.polytechnic.trpab.R;
import id.ac.astra.polytechnic.trpab.data.adapter.HeavyEngineAdapter;
import id.ac.astra.polytechnic.trpab.data.model.HeavyEngine;
import id.ac.astra.polytechnic.trpab.data.viewmodel.HeavyEngineViewModel;
import id.ac.astra.polytechnic.trpab.databinding.FragmentMaintenanceBinding;
import id.ac.astra.polytechnic.trpab.ui.fragment.others.DetailHeavyEngineDialogFragment;

public class MaintenanceFragment extends Fragment implements HeavyEngineAdapter.OnItemClickListener {

    private HeavyEngineViewModel mViewModel;
    private RecyclerView recyclerView;
    private HeavyEngineAdapter mHeavyEngineAdapter;
    private FragmentMaintenanceBinding binding;
    private List<HeavyEngine> dashboardItemList;
    private List<HeavyEngine> availableItems = new ArrayList<>(); // Inisialisasi list kosong
    private List<HeavyEngine> heavyEngineList;

    public static MaintenanceFragment newInstance() {
        return new MaintenanceFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMaintenanceBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        Log.d("MaintenanceFragment", "onCreateView: Fragment created successfully");

        MaterialButton maintenanceProcessBtn = view.findViewById(R.id.maintenance_process_btn);
        MaterialButton maintenanceHistoryBtn = view.findViewById(R.id.maintenance_history_btn);
        maintenanceProcessBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Gunakan NavController untuk navigasi ke fragment lain
                NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main);
                navController.navigate(R.id.nav_maintenance_process);
            }
        });

        maintenanceHistoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Gunakan NavController untuk navigasi ke fragment lain
                NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main);
                navController.navigate(R.id.nav_maintenance_history);
            }
        });

        // Menginisialisasi RecyclerView dan menambahkan layout manager
        recyclerView = view.findViewById(R.id.recycler_view_maintenance);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mViewModel = new ViewModelProvider(this).get(HeavyEngineViewModel.class);

        mViewModel.getHeavyEngineList().observe(getViewLifecycleOwner(), new Observer<List<HeavyEngine>>() {
            @Override
            public void onChanged(List<HeavyEngine> heavyEngines) {
                if (heavyEngines != null) {
                    // Filter data hanya untuk status "1" (Sedang Dalam Perawatan)
                    availableItems = filterAvailableItems(heavyEngines); // Inisialisasi availableItems

                    // Set adapter untuk RecyclerView
                    setupRecyclerView(availableItems);
                } else {
                    Toast.makeText(getContext(), "Failed to load data", Toast.LENGTH_SHORT).show();
                }
            }
        });

        TextInputEditText search_view_dashboard = view.findViewById(R.id.search_view_dashboard);
        search_view_dashboard.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Tidak perlu melakukan apa pun sebelum teks diubah
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Panggil searchUnitByName dari ViewModel
                mViewModel.fetchDataUnitByName(s.toString(), Collections.singletonList(1));
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Tidak perlu melakukan apa pun setelah teks diubah
            }
        });

        MaterialButton delBtn = view.findViewById(R.id.btn_search_delete);
        delBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search_view_dashboard.setText("");
            }
        });

        ((MainActivity) getActivity()).showLogoutButton();

        return view;
    }

    private void setupRecyclerView(List<HeavyEngine> items) {
        mHeavyEngineAdapter = new HeavyEngineAdapter(items, false);
        mHeavyEngineAdapter.setOnItemClickListener(this);
        recyclerView.setAdapter(mHeavyEngineAdapter);
    }

    @Override
    public void onItemClick(int position) {
        HeavyEngine clickedItem = availableItems.get(position); // Pastikan availableItems tidak null
        // Show dialog or navigate to another fragment
        DetailHeavyEngineDialogFragment dialogFragment = DetailHeavyEngineDialogFragment.newInstance(
                clickedItem.getId(),
                "Perawatan Alat",
                clickedItem.getTitle(),
                clickedItem.getHours(),
                clickedItem.getImageUrl()
        );
        dialogFragment.show(getChildFragmentManager(), "DetailHeavyEngineDialogFragment");
    }

    private List<HeavyEngine> filterAvailableItems(List<HeavyEngine> items) {
        return items.stream()
                .filter(item -> "1".equals(item.getStatus()))
                .collect(Collectors.toList());
    }
}
