package id.ac.astra.polytechnic.trpab.ui.maintenance;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import id.ac.astra.polytechnic.trpab.R;
import id.ac.astra.polytechnic.trpab.data.adapter.HeavyEngineAdapter;
import id.ac.astra.polytechnic.trpab.data.model.HeavyEngine;

public class MaintenanceFragment extends Fragment {

    private MaintenanceViewModel mViewModel;
    private RecyclerView recyclerView;
    private HeavyEngineAdapter mHeavyEngineAdapter;
    private List<HeavyEngine> dashboardItemList;

    public static MaintenanceFragment newInstance() {
        return new MaintenanceFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_maintenance, container, false);

        Log.d("MaintenanceFragment", "onCreateView: Fragment created successfully");

        // Menginisialisasi RecyclerView dan menambahkan layout manager
        recyclerView = view.findViewById(R.id.recycler_view_maintenance);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Menginisialisasi data dan adapter
        dashboardItemList = new ArrayList<>();
        // Tambahkan data ke dashboardItemList
        dashboardItemList.add(new HeavyEngine("D85ESS-2", "5674 Hours", "Sedang Digunakan", R.drawable.beko1));
        dashboardItemList.add(new HeavyEngine("PC200-8", "4321 Hours", "Sedang Dalam Perawatan", R.drawable.avatar_1));
        dashboardItemList.add(new HeavyEngine("PC200-8", "4321 Hours", "Sedang Dalam Perawatan", R.drawable.avatar_1));
        dashboardItemList.add(new HeavyEngine("CAT320", "7890 Hours", "Tersedia", R.drawable.avatar_2));
        dashboardItemList.add(new HeavyEngine("CAT320", "7890 Hours", "Tersedia", R.drawable.avatar_2));
        dashboardItemList.add(new HeavyEngine("CAT320", "7890 Hours", "Tersedia", R.drawable.avatar_2));

        // Filter daftar item yang tersedia
        List<HeavyEngine> availableItems = filterAvailableItems(dashboardItemList);

        // Menginisialisasi adapter dengan daftar yang difilter
        mHeavyEngineAdapter = new HeavyEngineAdapter(availableItems);
        recyclerView.setAdapter(mHeavyEngineAdapter);

        return view;
    }

    private List<HeavyEngine> filterAvailableItems(List<HeavyEngine> items) {
        return items.stream()
                .filter(item -> "Tersedia".equals(item.getStatus()))
                .collect(Collectors.toList());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(MaintenanceViewModel.class);
        // TODO: Use the ViewModel
    }

}