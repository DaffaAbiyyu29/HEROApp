package id.ac.astra.polytechnic.trpab.ui.fragment.home;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

import id.ac.astra.polytechnic.trpab.ui.activity.MainActivity;
import id.ac.astra.polytechnic.trpab.R;
import id.ac.astra.polytechnic.trpab.data.adapter.HeavyEngineAdapter;
import id.ac.astra.polytechnic.trpab.data.model.HeavyEngine;
import id.ac.astra.polytechnic.trpab.data.viewmodel.HeavyEngineViewModel;

public class HomeFragment extends Fragment {

//    private HomeViewModel mViewModel;
    private RecyclerView recyclerView;
    private HeavyEngineAdapter mHeavyEngineAdapter;
    private List<HeavyEngine> dashboardItemList;
    private HeavyEngineViewModel mViewModel;
    String id, nama, role, npk, nim;

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        Log.d("MaintenanceFragment", "onCreateView: Fragment created successfully");

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.recycler_view_dashboard);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize ViewModel
        mViewModel = new ViewModelProvider(this).get(HeavyEngineViewModel.class);

        TextView nomorUser = view.findViewById(R.id.id_text);
        TextView namaUser = view.findViewById(R.id.name_text);
        TextView roleUser = view.findViewById(R.id.role_text);
        Bundle bundle = getArguments();
        if (bundle != null) {
            id = bundle.getString("ID_USER");
            nama = bundle.getString("NAMA_USER");
            role = bundle.getString("ROLE_USER");
            npk = bundle.getString("NPK");
            nim = bundle.getString("NIM");

            // Tampilkan nama di TextView
            if (npk != null) {
                nomorUser.setText(npk);
            } else if (nim != null) {
                nomorUser.setText(nim);
            }

            namaUser.setText(nama);
            roleUser.setText(role);
        }

        // Observe LiveData from ViewModel
        mViewModel.getHeavyEngineList().observe(getViewLifecycleOwner(), new Observer<List<HeavyEngine>>() {
            @Override
            public void onChanged(List<HeavyEngine> heavyEngineList) {
                if (heavyEngineList != null) {
                    mHeavyEngineAdapter = new HeavyEngineAdapter(heavyEngineList, false);
                    recyclerView.setAdapter(mHeavyEngineAdapter);
                } else {
                    Log.e("HomeFragment", "Failed to load data");
                    Toast.makeText(getContext(), "Failed to load data", Toast.LENGTH_SHORT).show();
                }
            }
        });

        TextInputEditText search_view_dashboard = view.findViewById(R.id.search_view_dashboard);
        search_view_dashboard.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Tidak perlu melakukan apa pun sebelum teks diubah
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Panggil searchUnitByName dari ViewModel
                mViewModel.fetchDataUnitByName(s.toString(), null);
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Tidak perlu melakukan apa pun setelah teks diubah
            }
        });

        MaterialButton delBtn = view.findViewById(R.id.btn_search_delete);
        delBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search_view_dashboard.setText("");
            }
        });

        ((MainActivity) getActivity()).showLogoutButton();

        return view;
    }

}
