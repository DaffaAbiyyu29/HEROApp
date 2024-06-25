package id.ac.astra.polytechnic.trpab.ui.maintenance;

import androidx.lifecycle.ViewModelProvider;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import id.ac.astra.polytechnic.trpab.MainActivity;
import id.ac.astra.polytechnic.trpab.R;
import id.ac.astra.polytechnic.trpab.data.adapter.HeavyEngineAdapter;
import id.ac.astra.polytechnic.trpab.data.model.HeavyEngine;

public class MaintenanceFragment extends Fragment implements HeavyEngineAdapter.OnItemClickListener {

    private MaintenanceViewModel mViewModel;
    private RecyclerView recyclerView;
    private HeavyEngineAdapter mHeavyEngineAdapter;
    private List<HeavyEngine> dashboardItemList;
    private List<HeavyEngine> availableItems;

    public static MaintenanceFragment newInstance() {
        return new MaintenanceFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_maintenance, container, false);

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

        // Menginisialisasi data dan adapter
        dashboardItemList = new ArrayList<>();
        dashboardItemList.add(new HeavyEngine("1", "D85ESS-2", "5674", "Tersedia", R.drawable.beko1));
        dashboardItemList.add(new HeavyEngine("2", "PC200-8", "4321", "Sedang Dalam Perawatan", R.drawable.avatar_1));
        dashboardItemList.add(new HeavyEngine("3", "E/g SAA6D140-3", "4321", "Tersedia", R.drawable.avatar_2));
        dashboardItemList.add(new HeavyEngine("4", "PC210LC-10MO", "7890", "Sedang Dalam Perawatan", R.drawable.avatar_3));
        dashboardItemList.add(new HeavyEngine("5", "CAT320", "7890", "Sedang Digunakan", R.drawable.avatar_4));
        dashboardItemList.add(new HeavyEngine("6", "PC500LC-10R", "7890", "Tersedia", R.drawable.avatar_5));

        // Filter daftar item yang tersedia
        availableItems = filterAvailableItems(dashboardItemList);
        mHeavyEngineAdapter = new HeavyEngineAdapter(availableItems, false);
        mHeavyEngineAdapter.setOnItemClickListener(this);
        recyclerView.setAdapter(mHeavyEngineAdapter);

        ((MainActivity) getActivity()).showLogoutButton();

        return view;
    }

    @Override
    public void onItemClick(int position) {
        // Handle item click here
        HeavyEngine clickedItem = availableItems.get(position);
//        Toast.makeText(getContext(), "Item clicked: " + clickedItem.getId(), Toast.LENGTH_SHORT).show();
        // Show dialog or navigate to another fragment
        DetailHeavyEngineDialogFragment dialogFragment = DetailHeavyEngineDialogFragment.newInstance(
                "Perawatan Alat",
                clickedItem.getTitle(),
                clickedItem.getHours(),
                clickedItem.getImageResId()
        );
        dialogFragment.show(getChildFragmentManager(), "DetailHeavyEngineDialogFragment");
    }

    private List<HeavyEngine> filterAvailableItems(List<HeavyEngine> items) {
        return items.stream()
                .filter(item -> "Tersedia".equals(item.getStatus()))
                .collect(Collectors.toList());
    }
}
