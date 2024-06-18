package id.ac.astra.polytechnic.trpab.ui.borrowing;

import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

import id.ac.astra.polytechnic.trpab.MainActivity;
import id.ac.astra.polytechnic.trpab.R;
import id.ac.astra.polytechnic.trpab.data.adapter.HeavyEngineAdapter;
import id.ac.astra.polytechnic.trpab.data.model.HeavyEngine;
import id.ac.astra.polytechnic.trpab.databinding.FragmentBorrowingBinding;

public class BorrowingFragment extends Fragment {

    private BorrowingViewModel mViewModel;
    private FragmentBorrowingBinding binding;
    private View underlineOne, underlineTwo, underlineThree;

    private RecyclerView recyclerView;
    private HeavyEngineAdapter mHeavyEngineAdapter;
    private List<HeavyEngine> availableList;
    private List<HeavyEngine> pendingList;
    private List<HeavyEngine> unavailableList;

    public static BorrowingFragment newInstance() {
        return new BorrowingFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentBorrowingBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        Button btnOne = view.findViewById(R.id.btn_one);
        Button btnTwo = view.findViewById(R.id.btn_two);
        Button btnThree = view.findViewById(R.id.btn_three);

        recyclerView = view.findViewById(R.id.recycler_view_borrowing);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setVisibility(View.VISIBLE);

        int activeBackgroundColor = ContextCompat.getColor(requireContext(), R.color.red_primary);
        int inactiveBackgroundColor = ContextCompat.getColor(requireContext(), R.color.abu2);

        availableList = new ArrayList<>();
        pendingList = new ArrayList<>();
        unavailableList = new ArrayList<>();

        // Initialize the lists with their respective items
        availableList.add(new HeavyEngine("1", "D85ESS-2", "5674 Hours", "Tersedia", R.drawable.beko1));
        availableList.add(new HeavyEngine("2", "PC200-8", "4321 Hours", "Tersedia", R.drawable.avatar_1));
        availableList.add(new HeavyEngine("3", "PC200-8", "4321 Hours", "Tersedia", R.drawable.avatar_1));
        availableList.add(new HeavyEngine("4", "CAT320", "7890 Hours", "Tersedia", R.drawable.avatar_2));
        availableList.add(new HeavyEngine("5", "CAT320", "7890 Hours", "Tersedia", R.drawable.avatar_2));

        pendingList.add(new HeavyEngine("1", "D85ESS-2", "5674 Hours", "Dalam Pengajuan", R.drawable.beko1));
        pendingList.add(new HeavyEngine("2", "PC200-8", "4321 Hours", "Dalam Pengajuan", R.drawable.avatar_1));
        pendingList.add(new HeavyEngine("3", "PC200-8", "4321 Hours", "Dalam Pengajuan", R.drawable.avatar_1));
        pendingList.add(new HeavyEngine("4", "CAT320", "7890 Hours", "Dalam Pengajuan", R.drawable.avatar_2));
        pendingList.add(new HeavyEngine("5", "CAT320", "7890 Hours", "Dalam Pengajuan", R.drawable.avatar_2));

        unavailableList.add(new HeavyEngine("1", "PC200-8", "4321 Hours", "Sedang Digunakan", R.drawable.avatar_1));
        unavailableList.add(new HeavyEngine("2", "CAT320", "7890 Hours", "Sedang Dalam Perawatan", R.drawable.avatar_2));
        unavailableList.add(new HeavyEngine("3", "CAT320", "7890 Hours", "Sedang Dalam Perawatan", R.drawable.avatar_2));

        // Set the initial adapter with the available list
        mHeavyEngineAdapter = new HeavyEngineAdapter(availableList, false);
        recyclerView.setAdapter(mHeavyEngineAdapter);

        ((MainActivity) getActivity()).showLogoutButton();

        underlineOne = view.findViewById(R.id.underline_one);
        underlineTwo = view.findViewById(R.id.underline_two);
        underlineThree = view.findViewById(R.id.underline_three);


        btnOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetButtonStyles(btnOne, btnTwo, btnThree, underlineOne, underlineTwo, underlineThree, inactiveBackgroundColor);
                btnOne.setTextColor(activeBackgroundColor);
                underlineOne.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.VISIBLE);
                mHeavyEngineAdapter = new HeavyEngineAdapter(availableList, false);
                recyclerView.setAdapter(mHeavyEngineAdapter);
            }
        });

        btnTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetButtonStyles(btnOne, btnTwo, btnThree, underlineOne, underlineTwo, underlineThree, inactiveBackgroundColor);
                btnTwo.setTextColor(activeBackgroundColor);
                underlineTwo.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.VISIBLE);
                mHeavyEngineAdapter = new HeavyEngineAdapter(pendingList, true);
                recyclerView.setAdapter(mHeavyEngineAdapter);
            }
        });

        btnThree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetButtonStyles(btnOne, btnTwo, btnThree, underlineOne, underlineTwo, underlineThree, inactiveBackgroundColor);
                btnThree.setTextColor(activeBackgroundColor);
                underlineThree.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.VISIBLE);
                mHeavyEngineAdapter = new HeavyEngineAdapter(unavailableList, false);
                recyclerView.setAdapter(mHeavyEngineAdapter);
            }
        });

        return view;
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
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(BorrowingViewModel.class);
        // TODO: Use the ViewModel
    }
}
