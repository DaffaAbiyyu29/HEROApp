package id.ac.astra.polytechnic.trpab.ui.maintenance;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
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

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import id.ac.astra.polytechnic.trpab.MainActivity;
import id.ac.astra.polytechnic.trpab.R;
import id.ac.astra.polytechnic.trpab.data.adapter.HeavyEngineAdapter;
import id.ac.astra.polytechnic.trpab.data.model.HeavyEngine;

public class MaintenanceProcessFragment extends Fragment {

    private MaintenanceProcessViewModel mViewModel;
    private RecyclerView recyclerView;
    private HeavyEngineAdapter mHeavyEngineAdapter;
    private List<HeavyEngine> dashboardItemList;

    public static MaintenanceProcessFragment newInstance() {
        return new MaintenanceProcessFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_maintenance_process, container, false);

        Log.d("MaintenanceFragment", "onCreateView: Fragment created successfully");

        ((MainActivity) getActivity()).showBackButton();

//        MaterialButton maintenanceProcessBtn = view.findViewById(R.id.maintenance_process_btn);
//        maintenanceProcessBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main);
//                navController.navigate(R.id.nav_maintenance_process);
//            }
//        });

        recyclerView = view.findViewById(R.id.recycler_view_maintenance_process);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        dashboardItemList = new ArrayList<>();
        dashboardItemList.add(new HeavyEngine("D85ESS-2", "5674 Hours", "Sedang Digunakan", R.drawable.beko1));
        dashboardItemList.add(new HeavyEngine("PC200-8", "4321 Hours", "Sedang Dalam Perawatan", R.drawable.avatar_1));
        dashboardItemList.add(new HeavyEngine("PC200-8", "4321 Hours", "Sedang Dalam Perawatan", R.drawable.avatar_1));
        dashboardItemList.add(new HeavyEngine("CAT320", "7890 Hours", "Tersedia", R.drawable.avatar_2));
        dashboardItemList.add(new HeavyEngine("CAT320", "7890 Hours", "Tersedia", R.drawable.avatar_2));
        dashboardItemList.add(new HeavyEngine("CAT320", "7890 Hours", "Tersedia", R.drawable.avatar_2));

        List<HeavyEngine> availableItems = filterAvailableItems(dashboardItemList);
        mHeavyEngineAdapter = new HeavyEngineAdapter(dashboardItemList);
        recyclerView.setAdapter(mHeavyEngineAdapter);

        return view;
    }

    private List<HeavyEngine> filterAvailableItems(List<HeavyEngine> items) {
        return items.stream()
                .filter(item -> "Sedang Dalam Perawatan".equals(item.getStatus()))
                .collect(Collectors.toList());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(MaintenanceProcessViewModel.class);
    }
}
