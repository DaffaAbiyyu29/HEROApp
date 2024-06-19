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

public class MaintenanceHistoryFragment extends Fragment {

    private MaintenanceHistoryViewModel mViewModel;
    private RecyclerView recyclerView;
    private HeavyEngineAdapter mHeavyEngineAdapter;
    private List<HeavyEngine> dashboardItemList;
    private CardView statusBarView;

    public static MaintenanceHistoryFragment newInstance() {
        return new MaintenanceHistoryFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_maintenance_history, container, false);

        Log.d("MaintenanceFragment", "onCreateView: Fragment created successfully");

        recyclerView = view.findViewById(R.id.recycler_view_maintenance_history);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        dashboardItemList = new ArrayList<>();
        dashboardItemList.add(new HeavyEngine("1", "D85ESS-2", "5674 Hours", "Sedang Digunakan", R.drawable.beko1));
        dashboardItemList.add(new HeavyEngine("2", "PC200-8", "4321 Hours", "Sedang Dalam Perawatan", R.drawable.avatar_1));
        dashboardItemList.add(new HeavyEngine("3", "PC200-8", "4321 Hours", "Sedang Dalam Perawatan", R.drawable.avatar_1));
        dashboardItemList.add(new HeavyEngine("4", "CAT320", "7890 Hours", "Tersedia", R.drawable.avatar_2));
        dashboardItemList.add(new HeavyEngine("5", "CAT320", "7890 Hours", "Tersedia", R.drawable.avatar_2));
        dashboardItemList.add(new HeavyEngine("6", "CAT320", "7890 Hours", "Tersedia", R.drawable.avatar_2));

        mHeavyEngineAdapter = new HeavyEngineAdapter(dashboardItemList, true);
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
        mViewModel = new ViewModelProvider(this).get(MaintenanceHistoryViewModel.class);
    }
}
