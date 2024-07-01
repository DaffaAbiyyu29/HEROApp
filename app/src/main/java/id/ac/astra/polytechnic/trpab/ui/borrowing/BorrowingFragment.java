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

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.util.List;
import java.util.stream.Collectors;

import id.ac.astra.polytechnic.trpab.MainActivity;
import id.ac.astra.polytechnic.trpab.R;
import id.ac.astra.polytechnic.trpab.data.adapter.HeavyEngineAdapter;
import id.ac.astra.polytechnic.trpab.data.model.HeavyEngine;
import id.ac.astra.polytechnic.trpab.data.viewmodel.HeavyEngineViewModel;
import id.ac.astra.polytechnic.trpab.databinding.FragmentBorrowingBinding;
import id.ac.astra.polytechnic.trpab.ui.DetailHeavyEngineDialogFragment;

public class BorrowingFragment extends Fragment implements HeavyEngineAdapter.OnItemClickListener {

    private HeavyEngineViewModel mViewModel;
    private FragmentBorrowingBinding binding;
    private View underlineOne, underlineTwo, underlineThree;

    private RecyclerView recyclerView;
    private HeavyEngineAdapter mHeavyEngineAdapter;
    private List<HeavyEngine> heavyEngineList;
    private String title = "Peminjaman Alat";

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

        Button btnOne = view.findViewById(R.id.btn_one);
        Button btnTwo = view.findViewById(R.id.btn_two);
        Button btnThree = view.findViewById(R.id.btn_three);

        recyclerView = view.findViewById(R.id.recycler_view_borrowing);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setVisibility(View.VISIBLE);

        underlineOne.setVisibility(View.VISIBLE);;

        int activeBackgroundColor = ContextCompat.getColor(requireContext(), R.color.red_primary);
        int inactiveBackgroundColor = ContextCompat.getColor(requireContext(), R.color.abu2);

        mViewModel = new ViewModelProvider(this).get(HeavyEngineViewModel.class);

        // Observe heavyEngineList from HeavyEngineViewModel
        mViewModel.getHeavyEngineList().observe(getViewLifecycleOwner(), new Observer<List<HeavyEngine>>() {
            @Override
            public void onChanged(List<HeavyEngine> heavyEngines) {
                if (heavyEngines != null) {
                    heavyEngineList = heavyEngines;
                    heavyEngineList = mViewModel.getAvailableList();
                    setupRecyclerView(heavyEngineList);
                } else {
                    // Handle case where heavyEngines is null
                }
            }
        });

        ((MainActivity) requireActivity()).showLogoutButton();

        btnOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetButtonStyles(btnOne, btnTwo, btnThree, underlineOne, underlineTwo, underlineThree, inactiveBackgroundColor);
                btnOne.setTextColor(activeBackgroundColor);
                underlineOne.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.VISIBLE);
                heavyEngineList = mViewModel.getAvailableList();
                setupRecyclerView(heavyEngineList);
//                mHeavyEngineAdapter = new HeavyEngineAdapter(heavyEngineList, false);
//                mHeavyEngineAdapter.setItems(heavyEngineList);
//                recyclerView.setAdapter(mHeavyEngineAdapter);
                title = "Peminjaman Alat";
            }
        });

        btnTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetButtonStyles(btnOne, btnTwo, btnThree, underlineOne, underlineTwo, underlineThree, inactiveBackgroundColor);
                btnTwo.setTextColor(activeBackgroundColor);
                underlineTwo.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.VISIBLE);
                heavyEngineList = mViewModel.getPendingList();
                setupRecyclerView2(heavyEngineList);
//                mHeavyEngineAdapter = new HeavyEngineAdapter(heavyEngineList, true);
//                mHeavyEngineAdapter.setItems(heavyEngineList);
//                recyclerView.setAdapter(mHeavyEngineAdapter);
                title = "Persetujuan Alat";
            }
        });

        btnThree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetButtonStyles(btnOne, btnTwo, btnThree, underlineOne, underlineTwo, underlineThree, inactiveBackgroundColor);
                btnThree.setTextColor(activeBackgroundColor);
                underlineThree.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.VISIBLE);
                heavyEngineList = mViewModel.getUnavailableList();
                setupRecyclerView(heavyEngineList);
//                mHeavyEngineAdapter = new HeavyEngineAdapter(heavyEngineList, false);
//                mHeavyEngineAdapter.setItems(heavyEngineList);
//                recyclerView.setAdapter(mHeavyEngineAdapter);
                title = "Pengembalian Alat";
            }
        });

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
        btnTwo.setTextColor(inactiveBackgroundColor);
        btnThree.setTextColor(inactiveBackgroundColor);
        underlineOne.setVisibility(View.GONE);
        underlineTwo.setVisibility(View.GONE);
        underlineThree.setVisibility(View.GONE);
    }

    @Override
    public void onItemClick(int position) {
        HeavyEngine clickedItem = heavyEngineList.get(position);
        DetailHeavyEngineDialogFragment dialogFragment = DetailHeavyEngineDialogFragment.newInstance(
                title,
                clickedItem.getTitle(),
                clickedItem.getHours(),
                clickedItem.getImageUrl()
        );
        dialogFragment.show(getChildFragmentManager(), "DetailHeavyEngineDialogFragment");
    }
}
