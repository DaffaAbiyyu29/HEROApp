package id.ac.astra.polytechnic.trpab.ui.home;

import androidx.lifecycle.ViewModelProvider;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.List;
import id.ac.astra.polytechnic.trpab.R;

public class HomeFragment extends Fragment {

    private HomeViewModel mViewModel;
    private RecyclerView recyclerView;
    private HomeAdapter dashboardAdapter;
    private List<HomeItem> dashboardItemList;

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        Log.d("HomeFragment", "onCreateView: Fragment created successfully");

        // Menginisialisasi RecyclerView dan menambahkan layout manager
        recyclerView = view.findViewById(R.id.recycler_view_dashboard);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Menginisialisasi data dan adapter
        dashboardItemList = new ArrayList<>();
        // Tambahkan data ke dashboardItemList
        dashboardItemList.add(new HomeItem("D85ESS-2", "5674 Hours", "Sedang Digunakan", R.drawable.beko1));
        dashboardItemList.add(new HomeItem("PC200-8", "4321 Hours", "Sedang Dalam Perawatan", R.drawable.avatar_1));
        dashboardItemList.add(new HomeItem("PC200-8", "4321 Hours", "Sedang Dalam Perawatan", R.drawable.avatar_1));
        dashboardItemList.add(new HomeItem("CAT320", "7890 Hours", "Tersedia", R.drawable.avatar_2));
        dashboardItemList.add(new HomeItem("CAT320", "7890 Hours", "Tersedia", R.drawable.avatar_2));
        dashboardItemList.add(new HomeItem("CAT320", "7890 Hours", "Tersedia", R.drawable.avatar_2));

        dashboardAdapter = new HomeAdapter(dashboardItemList);
        recyclerView.setAdapter(dashboardAdapter);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        // TODO: Use the ViewModel
    }
}
