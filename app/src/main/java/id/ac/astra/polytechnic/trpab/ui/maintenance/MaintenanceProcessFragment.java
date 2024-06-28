package id.ac.astra.polytechnic.trpab.ui.maintenance;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import id.ac.astra.polytechnic.trpab.MainActivity;
import id.ac.astra.polytechnic.trpab.R;
import id.ac.astra.polytechnic.trpab.data.adapter.HeavyEngineAdapter;
import id.ac.astra.polytechnic.trpab.data.model.HeavyEngine;
import id.ac.astra.polytechnic.trpab.ui.borrowing.BorrowingFragment;

public class MaintenanceProcessFragment extends Fragment implements HeavyEngineAdapter.OnItemClickListener{

    private MaintenanceProcessViewModel mViewModel;
    private View underlineOne, underlineTwo;
    private RecyclerView recyclerView;
    private HeavyEngineAdapter mHeavyEngineAdapter;
    private List<HeavyEngine> dashboardItemList;
//    private List<HeavyEngine> heavyEngineList;
    private List<HeavyEngine> servicelist;
    private List<HeavyEngine> perbaikanlist;

    private CardView statusBarView;

    public static MaintenanceProcessFragment newInstance() {
        return new MaintenanceProcessFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_maintenance_process, container, false);

        underlineOne = view.findViewById(R.id.underline_one);
        underlineTwo = view.findViewById(R.id.underline_two);

        Button btnOne = view.findViewById(R.id.btn_one);
        Button btnTwo = view.findViewById(R.id.btn_two);

        recyclerView = view.findViewById(R.id.recycler_view_maintenance_process);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setVisibility(View.VISIBLE);

        int activeBackgroundColor = ContextCompat.getColor(requireContext(), R.color.red_primary);
        int inactiveBackgroundColor = ContextCompat.getColor(requireContext(), R.color.abu2);

        servicelist = new ArrayList<>();
        perbaikanlist = new ArrayList<>();
//        heavyEngineList = new ArrayList<>();

        Log.d("MaintenanceFragment", "onCreateView: Fragment created successfully");

//        recyclerView = view.findViewById(R.id.recycler_view_maintenance_process);
//        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        dashboardItemList = new ArrayList<>();
        dashboardItemList.add(new HeavyEngine("1", "D85ESS-2", "5674", "Tersedia", R.drawable.beko1));
        dashboardItemList.add(new HeavyEngine("2", "PC200-8", "4321", "Sedang Dalam Perawatan", R.drawable.avatar_1));
        dashboardItemList.add(new HeavyEngine("3", "E/g SAA6D140-3", "4321", "Tersedia", R.drawable.avatar_2));
        dashboardItemList.add(new HeavyEngine("4", "PC210LC-10MO", "7890", "Sedang Dalam Perawatan", R.drawable.avatar_3));
        dashboardItemList.add(new HeavyEngine("5", "CAT320", "7890", "Sedang Digunakan", R.drawable.avatar_4));
        dashboardItemList.add(new HeavyEngine("6", "PC500LC-10R", "7890", "Tersedia", R.drawable.avatar_5));

//        heavyEngineList = new ArrayList<>(dashboardItemList);
        perbaikanlist = filterAvailableItems(dashboardItemList);
        servicelist = filterAvailableItems(dashboardItemList);

        dashboardItemList.clear();
        dashboardItemList = perbaikanlist;
        underlineOne.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.VISIBLE);

//        List<HeavyEngine> availableItems = filterAvailableItems(dashboardItemList);
        mHeavyEngineAdapter = new HeavyEngineAdapter(perbaikanlist, false);
        recyclerView.setAdapter(mHeavyEngineAdapter);

        mHeavyEngineAdapter.setOnItemClickListener(MaintenanceProcessFragment.this);

        ((MainActivity) getActivity()).showBackButton();

        btnOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetButtonStyles(btnOne, btnTwo, underlineOne, underlineTwo, inactiveBackgroundColor);
                btnOne.setTextColor(activeBackgroundColor);
                btnOne.setTextColor(activeBackgroundColor);
                underlineOne.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.VISIBLE);
                mHeavyEngineAdapter.updateData(perbaikanlist);
            }
        });
        btnTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetButtonStyles(btnOne, btnTwo, underlineOne, underlineTwo, inactiveBackgroundColor);
                btnTwo.setTextColor(activeBackgroundColor);
                underlineTwo.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.VISIBLE);
                mHeavyEngineAdapter.updateData(servicelist);
            }
        });

        return view;
    }

    private List<HeavyEngine> filterAvailableItems(List<HeavyEngine> items) {
        return items.stream()
                .filter(item -> "Sedang Dalam Perawatan".equals(item.getStatus()))
                .collect(Collectors.toList());
    }

    @Override
    public void onItemClick(int position) {
        HeavyEngine clickedItem = mHeavyEngineAdapter.getItem(position);

        Bundle bundle = new Bundle();
        bundle.putString("title", clickedItem.getTitle());

        NavController navController = NavHostFragment.findNavController(this);
        navController.navigate(R.id.action_to_improvementFragment, bundle);
    }

    private void resetButtonStyles(Button btnOne, Button btnTwo, View underlineOne, View underlineTwo, int inactiveBackgroundColor) {
        btnOne.setTextColor(inactiveBackgroundColor);
        btnTwo.setTextColor(inactiveBackgroundColor);
        underlineOne.setVisibility(View.GONE);
        underlineTwo.setVisibility(View.GONE);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(MaintenanceProcessViewModel.class);
    }
}
