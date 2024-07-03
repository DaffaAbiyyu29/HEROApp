package id.ac.astra.polytechnic.trpab.ui.borrowing;

import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import id.ac.astra.polytechnic.trpab.MainActivity;
import id.ac.astra.polytechnic.trpab.R;
import id.ac.astra.polytechnic.trpab.data.adapter.HeavyEngineAdapter;
import id.ac.astra.polytechnic.trpab.data.model.HeavyEngine;
import id.ac.astra.polytechnic.trpab.data.viewmodel.HeavyEngineViewModel;
import id.ac.astra.polytechnic.trpab.databinding.FragmentBorrowingBinding;
import id.ac.astra.polytechnic.trpab.ui.DetailHeavyEngineDialogFragment;

public class BorrowingFragment extends Fragment implements HeavyEngineAdapter.OnItemClickListener, DetailHeavyEngineDialogFragment.OnPeminjamanAddedListener {

    private HeavyEngineViewModel mViewModel;
    private FragmentBorrowingBinding binding;
    private View underlineOne, underlineTwo, underlineThree;
    Button btnOne, btnTwo, btnThree;

    private RecyclerView recyclerView;
    private HeavyEngineAdapter mHeavyEngineAdapter;
    private List<HeavyEngine> heavyEngineList;
    private String title = "Peminjaman Alat";
    private String currentDataType = "available";
    private int activeBackgroundColor, inactiveBackgroundColor;

    public static BorrowingFragment newInstance() {
        return new BorrowingFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentBorrowingBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        underlineOne = view.findViewById(R.id.underline_one);
        underlineTwo = view.findViewById(R.id.underline_two);
        underlineThree = view.findViewById(R.id.underline_three);

        btnOne = view.findViewById(R.id.btn_one);
        btnTwo = view.findViewById(R.id.btn_two);
        btnThree = view.findViewById(R.id.btn_three);

        recyclerView = view.findViewById(R.id.recycler_view_borrowing);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setVisibility(View.VISIBLE);

        btnOne.setSelected(true);
        underlineOne.setVisibility(View.VISIBLE);

        activeBackgroundColor = ContextCompat.getColor(requireContext(), R.color.red_primary);
        inactiveBackgroundColor = ContextCompat.getColor(requireContext(), R.color.abu2);

        mViewModel = new ViewModelProvider(this).get(HeavyEngineViewModel.class);

        // Observe heavyEngineList from HeavyEngineViewModel
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
                    mViewModel.fetchDataUnitByName(s.toString(), Collections.singletonList(1));
                } else if (btnTwo.isSelected()) {
                    mViewModel.fetchDataUnitByName(s.toString(), Collections.singletonList(4));
                } else if (btnThree.isSelected()) {
                    mViewModel.fetchDataUnitByName(s.toString(), Arrays.asList(2, 3));
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
        mViewModel.fetchDataUnitByName("", Collections.singletonList(1));

        btnOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetButtonStyles(btnOne, btnTwo, btnThree, underlineOne, underlineTwo, underlineThree, inactiveBackgroundColor);
                btnOne.setTextColor(activeBackgroundColor);
                btnOne.setSelected(true);
                underlineOne.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.VISIBLE);
                heavyEngineList = mViewModel.getAvailableList();
                setupRecyclerView(heavyEngineList);
                title = "Peminjaman Alat";

                // Update search listener
                search_view_dashboard.setText("");
                search_view_dashboard.removeTextChangedListener(searchTextWatcher);
                search_view_dashboard.addTextChangedListener(searchTextWatcher);
            }
        });

        btnTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetButtonStyles(btnOne, btnTwo, btnThree, underlineOne, underlineTwo, underlineThree, inactiveBackgroundColor);
                btnTwo.setTextColor(activeBackgroundColor);
                btnTwo.setSelected(true);
                underlineTwo.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.VISIBLE);
                heavyEngineList = mViewModel.getPendingList();
                setupRecyclerView2(heavyEngineList);
                title = "Persetujuan Alat";

                // Update search listener
                search_view_dashboard.setText("");
                search_view_dashboard.removeTextChangedListener(searchTextWatcher);
                search_view_dashboard.addTextChangedListener(searchTextWatcher);
            }
        });

        btnThree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetButtonStyles(btnOne, btnTwo, btnThree, underlineOne, underlineTwo, underlineThree, inactiveBackgroundColor);
                btnThree.setTextColor(activeBackgroundColor);
                btnThree.setSelected(true);
                underlineThree.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.VISIBLE);
                heavyEngineList = mViewModel.getUnavailableList();
                setupRecyclerView(heavyEngineList);
                title = "Pengembalian Alat";

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

        ((MainActivity) requireActivity()).showLogoutButton();

        return view;
    }

    private void setupRecyclerView(List<HeavyEngine> items) {
        mHeavyEngineAdapter = new HeavyEngineAdapter(items, false);
        mHeavyEngineAdapter.setOnItemClickListener(this);
        recyclerView.setAdapter(mHeavyEngineAdapter);
    }

    private void setupRecyclerView2(List<HeavyEngine> items) {
        mHeavyEngineAdapter = new HeavyEngineAdapter(items, true);
        mHeavyEngineAdapter.setOnItemClickListener(this);
        recyclerView.setAdapter(mHeavyEngineAdapter);
    }

    private void resetButtonStyles(Button btnOne, Button btnTwo, Button btnThree, View underlineOne, View underlineTwo, View underlineThree, int inactiveBackgroundColor) {
        btnOne.setTextColor(inactiveBackgroundColor);
        btnOne.setSelected(false);
        btnTwo.setTextColor(inactiveBackgroundColor);
        btnTwo.setSelected(false);
        btnThree.setTextColor(inactiveBackgroundColor);
        btnThree.setSelected(false);
        underlineOne.setVisibility(View.GONE);
        underlineTwo.setVisibility(View.GONE);
        underlineThree.setVisibility(View.GONE);
    }

    @Override
    public void onResume() {
        super.onResume();

        // Atur kembali data default
        btnOne.setSelected(true);
        btnOne.setTextColor(activeBackgroundColor);
        underlineOne.setVisibility(View.VISIBLE);
        mViewModel.fetchDataUnitByName("", Collections.singletonList(1));
        heavyEngineList = mViewModel.getAvailableList();
        setupRecyclerView(heavyEngineList);
    }


    @Override
    public void onItemClick(int position) {
        HeavyEngine clickedItem = heavyEngineList.get(position);
        DetailHeavyEngineDialogFragment dialogFragment = DetailHeavyEngineDialogFragment.newInstance(
                clickedItem.getId(),
                title,
                clickedItem.getTitle(),
                clickedItem.getHours(),
                clickedItem.getImageUrl()
        );
        dialogFragment.setOnPeminjamanAddedListener(this);

        dialogFragment.show(getChildFragmentManager(), "DetailHeavyEngineDialogFragment");
    }

    @Override
    public void onPeminjamanAdded() {
        // Refresh data saat peminjaman berhasil ditambahkan
        // Contoh: Memuat ulang data dari ViewModel
        if (btnOne.isSelected()) {
            mViewModel.fetchDataUnitByName("", Collections.singletonList(1));
        } else if (btnTwo.isSelected()) {
            mViewModel.fetchDataUnitByName("", Collections.singletonList(4));
        } else if (btnThree.isSelected()) {
            mViewModel.fetchDataUnitByName("", Arrays.asList(2, 3));
        }
    }
}
