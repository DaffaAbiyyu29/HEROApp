package id.ac.astra.polytechnic.trpab.ui.maintenance;

import androidx.cardview.widget.CardView;
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
    private CardView statusBarView;

    public static MaintenanceProcessFragment newInstance() {
        return new MaintenanceProcessFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_maintenance_process, container, false);

        Log.d("MaintenanceFragment", "onCreateView: Fragment created successfully");

        recyclerView = view.findViewById(R.id.recycler_view_maintenance_process);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        dashboardItemList = new ArrayList<>();
        dashboardItemList.add(new HeavyEngine("1", "D85ESS-2", "5674", "Tersedia", R.drawable.beko1));
        dashboardItemList.add(new HeavyEngine("2", "PC200-8", "4321", "Sedang Dalam Perawatan", R.drawable.avatar_1));
        dashboardItemList.add(new HeavyEngine("3", "E/g SAA6D140-3", "4321", "Tersedia", R.drawable.avatar_2));
        dashboardItemList.add(new HeavyEngine("4", "PC210LC-10MO", "7890", "Sedang Dalam Perawatan", R.drawable.avatar_3));
        dashboardItemList.add(new HeavyEngine("5", "CAT320", "7890", "Sedang Digunakan", R.drawable.avatar_4));
        dashboardItemList.add(new HeavyEngine("6", "PC500LC-10R", "7890", "Tersedia", R.drawable.avatar_5));

        List<HeavyEngine> availableItems = filterAvailableItems(dashboardItemList);
        mHeavyEngineAdapter = new HeavyEngineAdapter(availableItems, false);
        recyclerView.setAdapter(mHeavyEngineAdapter);

        ((MainActivity) getActivity()).showBackButton();

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
