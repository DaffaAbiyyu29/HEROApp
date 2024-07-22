package id.ac.astra.polytechnic.trpab.ui.fragment.maintenance;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

import id.ac.astra.polytechnic.trpab.R;
import id.ac.astra.polytechnic.trpab.data.adapter.HeavyEngineAdapter;
import id.ac.astra.polytechnic.trpab.data.model.HeavyEngine;
import id.ac.astra.polytechnic.trpab.data.viewmodel.HeavyEngineViewModel;
import id.ac.astra.polytechnic.trpab.databinding.FragmentRiwayatMaintananceBinding;
import id.ac.astra.polytechnic.trpab.databinding.FragmentWorkmanshipdetailsBinding;
import id.ac.astra.polytechnic.trpab.ui.activity.MainActivity;

public class MaintananceDetailHistoryFragment extends Fragment {
    private HeavyEngineViewModel mViewModel;
    private RecyclerView recyclerView;
    private HeavyEngineAdapter mHeavyEngineAdapter;
    private List<HeavyEngine> dashboardItemList;
    private CardView statusBarView;
    private String schid;
    private FragmentRiwayatMaintananceBinding binding;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentRiwayatMaintananceBinding.inflate(inflater,container, false);
        View view = binding.getRoot();

//        Log.d("MaintenanceFragment", "onCreateView: Fragment created successfully");
        TextView titleTextView = binding.perbaikan;

        Bundle args = getArguments();
        if (args != null) {
            String schaduleId = args.getString("title", "Default Title");
            schid = args.getString("id");

            titleTextView.setText(schaduleId);
        }

        recyclerView = view.findViewById(R.id.recycler_riwayat_maintenance_history);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//
//        mViewModel = new ViewModelProvider(this).get(HeavyEngineViewModel.class);
//
//        mViewModel.getHeavyEngineList().observe(getViewLifecycleOwner(), new Observer<List<HeavyEngine>>() {
//            @Override
//            public void onChanged(List<HeavyEngine> heavyEngineList) {
//                if (heavyEngineList != null) {
//                    mHeavyEngineAdapter = new HeavyEngineAdapter(heavyEngineList, true);
//                    recyclerView.setAdapter(mHeavyEngineAdapter);
//                } else {
//                    Toast.makeText(getContext(), "Failed to load data", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });

        ((MainActivity) getActivity()).showBackButton();

        return view;
    }
}
