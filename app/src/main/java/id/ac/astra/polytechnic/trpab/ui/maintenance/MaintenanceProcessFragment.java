package id.ac.astra.polytechnic.trpab.ui.maintenance;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
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

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.widget.Button;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import id.ac.astra.polytechnic.trpab.MainActivity;
import id.ac.astra.polytechnic.trpab.R;
import id.ac.astra.polytechnic.trpab.data.adapter.HeavyEngineAdapter;
import id.ac.astra.polytechnic.trpab.data.model.HeavyEngine;
import id.ac.astra.polytechnic.trpab.data.viewmodel.HeavyEngineViewModel;
import id.ac.astra.polytechnic.trpab.ui.borrowing.BorrowingFragment;

public class MaintenanceProcessFragment extends Fragment implements HeavyEngineAdapter.OnItemClickListener {

    private HeavyEngineViewModel mViewModel;
    private View underlineOne, underlineTwo;
    private RecyclerView recyclerView;
    private HeavyEngineAdapter mHeavyEngineAdapter;
    Button btnOne, btnTwo;

    private List<HeavyEngine> heavyEngineList;
    private List<HeavyEngine> dashboardItemList = new ArrayList<>(); // Inisialisasi list kosong
    private List<HeavyEngine> servicelist;
    private List<HeavyEngine> perbaikanlist;

    private CardView statusBarView;
    private int activeBackgroundColor, inactiveBackgroundColor;

    public static MaintenanceProcessFragment newInstance() {
        return new MaintenanceProcessFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_maintenance_process, container, false);

        underlineOne = view.findViewById(R.id.underline_one);
        underlineTwo = view.findViewById(R.id.underline_two);

        btnOne = view.findViewById(R.id.btn_one);
        btnTwo = view.findViewById(R.id.btn_two);

        recyclerView = view.findViewById(R.id.recycler_view_maintenance_process);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setVisibility(View.VISIBLE);

        btnOne.setSelected(true);
        underlineOne.setVisibility(View.VISIBLE);

        activeBackgroundColor = ContextCompat.getColor(requireContext(), R.color.red_primary);
        inactiveBackgroundColor = ContextCompat.getColor(requireContext(), R.color.abu2);

//        servicelist = new ArrayList<>();
//        perbaikanlist = new ArrayList<>();

        mViewModel = new ViewModelProvider(this).get(HeavyEngineViewModel.class);

        mViewModel.getHeavyEngineList().observe(getViewLifecycleOwner(), new Observer<List<HeavyEngine>>() {
            @Override
            public void onChanged(List<HeavyEngine> heavyEngines) {
                if (heavyEngines != null) {
                    heavyEngineList = heavyEngines;
                    setupRecyclerView(heavyEngineList);
                } else {
                    Toast.makeText(getContext(), "Failed to load data", Toast.LENGTH_SHORT).show();
                }
            }
        });

        TextInputEditText search_view_dashboard = view.findViewById(R.id.search_view_dashboard);
        TextWatcher searchTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Tidak perlu melakukan apa pun sebelum teks diubah
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Panggil fetchDataUnitByName dari ViewModel dengan status sesuai tombol yang aktif
                if (btnOne.isSelected()) {
                    mViewModel.fetchDataUnitByName(s.toString(), Collections.singletonList(2));
                } else if (btnTwo.isSelected()) {
                    mViewModel.fetchDataUnitByName(s.toString(), Collections.singletonList(5));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Tidak perlu melakukan apa pun setelah teks diubah
            }
        };

        search_view_dashboard.setText("");
        search_view_dashboard.removeTextChangedListener(searchTextWatcher);
        search_view_dashboard.addTextChangedListener(searchTextWatcher);

        // Set default filter to btnOne
        btnOne.setSelected(true);
        btnOne.setTextColor(activeBackgroundColor);
        underlineOne.setVisibility(View.VISIBLE);
        mViewModel.fetchDataUnitByName("", Collections.singletonList(2));

        btnOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetButtonStyles(btnOne, btnTwo, underlineOne, underlineTwo, inactiveBackgroundColor);
                btnOne.setTextColor(activeBackgroundColor);
                btnOne.setSelected(true);
                underlineOne.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.VISIBLE);
//                mHeavyEngineAdapter.updateData(perbaikanlist);
                heavyEngineList = mViewModel.getProcessMaintenancePerbaikan();
                setupRecyclerView(heavyEngineList);

                // Update search listener
                search_view_dashboard.setText("");
                search_view_dashboard.removeTextChangedListener(searchTextWatcher);
                search_view_dashboard.addTextChangedListener(searchTextWatcher);
            }
        });

        btnTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetButtonStyles(btnOne, btnTwo, underlineOne, underlineTwo, inactiveBackgroundColor);
                btnTwo.setTextColor(activeBackgroundColor);
                btnTwo.setSelected(true);
                underlineTwo.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.VISIBLE);
//                mHeavyEngineAdapter.updateData(servicelist);
                heavyEngineList = mViewModel.getProcessMaintenanceService();
                setupRecyclerView(heavyEngineList);

                // Update search listener
                search_view_dashboard.setText("");
                search_view_dashboard.removeTextChangedListener(searchTextWatcher);
                search_view_dashboard.addTextChangedListener(searchTextWatcher);
            }
        });

        MaterialButton delBtn = view.findViewById(R.id.btn_search_delete);
        delBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search_view_dashboard.setText("");
            }
        });

        ((MainActivity) getActivity()).showBackButton();

        return view;
    }

    private List<HeavyEngine> filterPerbaikanItems(List<HeavyEngine> items) {
        return items.stream()
                .filter(item -> "2".equals(item.getStatus()))
                .collect(Collectors.toList());
    }

    private List<HeavyEngine> filterServiceItems(List<HeavyEngine> items) {
        return items.stream()
                .filter(item -> "5".equals(item.getStatus()))
                .collect(Collectors.toList());
    }

    private void setupRecyclerView(List<HeavyEngine> items) {
        mHeavyEngineAdapter = new HeavyEngineAdapter(items, false);
        mHeavyEngineAdapter.setOnItemClickListener(this);
        recyclerView.setAdapter(mHeavyEngineAdapter);
    }

    @Override
    public void onItemClick(int position) {
        HeavyEngine clickedItem = mHeavyEngineAdapter.getItem(position);

        Bundle bundle = new Bundle();
        bundle.putString("title", clickedItem.getTitle());
        bundle.putString("status", clickedItem.getStatus());

        NavController navController = NavHostFragment.findNavController(this);
        if ("5".equals(clickedItem.getStatus())) {
            Log.d("MaintenanceProcessFragment", "Navigating to SchaduleListFragment");
            navController.navigate(R.id.action_to_schadulefragment, bundle);
        } else {
            Log.d("MaintenanceProcessFragment", "Navigating to ImprovementFragment");
            navController.navigate(R.id.action_to_improvementFragment, bundle);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        // Atur kembali data default
        btnOne.setSelected(true);
        btnOne.setTextColor(activeBackgroundColor);
        underlineOne.setVisibility(View.VISIBLE);
        mViewModel.fetchDataUnitByName("", Collections.singletonList(2));
        heavyEngineList = mViewModel.getAvailableList();
        setupRecyclerView(heavyEngineList);
    }


    private void resetButtonStyles(Button btnOne, Button btnTwo, View underlineOne, View underlineTwo, int inactiveBackgroundColor) {
        btnOne.setTextColor(inactiveBackgroundColor);
        btnOne.setSelected(false);
        btnTwo.setTextColor(inactiveBackgroundColor);
        btnTwo.setSelected(false);
        underlineOne.setVisibility(View.GONE);
        underlineTwo.setVisibility(View.GONE);
    }
}
