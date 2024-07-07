package id.ac.astra.polytechnic.trpab.ui.fragment.maintenance;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import id.ac.astra.polytechnic.trpab.ui.activity.MainActivity;
import id.ac.astra.polytechnic.trpab.R;
import id.ac.astra.polytechnic.trpab.data.adapter.SchaduleAdapter;
import id.ac.astra.polytechnic.trpab.data.model.Schadule;
import id.ac.astra.polytechnic.trpab.databinding.FragmentSchedulelistBinding;

public class ScheduleListFragment extends Fragment implements SchaduleAdapter.OnItemClickListener {
    private FragmentSchedulelistBinding binding;
    private RecyclerView recyclerView;
    private SchaduleAdapter schaduleAdapter;
    private List<Schadule> serviceItemList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSchedulelistBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        recyclerView = binding.recyclerViewActionlist;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        TextView titleTextView = binding.textview3;

        // Initialize the service item list
        serviceItemList = new ArrayList<>();

        // Retrieve and set the title if provided
        Bundle args = getArguments();
        if (args != null) {
            String title = args.getString("title", "Default Title");
            String status = args.getString("status");

            titleTextView.setText(title);

            // Populate the RecyclerView based on the status
            if ("5".equals(status)) {
                // Load and set service items
                loadServiceItems();
            }
        }

        ((MainActivity) getActivity()).showBackButton();

        return view;
    }

    private void loadServiceItems() {
        // Add some dummy data to the list for testing
        serviceItemList.add(new Schadule("1", "Every 50 Hours Service", "50", "3", "1"));
        serviceItemList.add(new Schadule("2", "Every 150 Hours Service", "150", "3", "1"));

        schaduleAdapter = new SchaduleAdapter(serviceItemList, this);
        recyclerView.setAdapter(schaduleAdapter);
    }

    @Override
    public void onItemClick(int position) {
        Schadule clickedItem = serviceItemList.get(position);
        NavController navController = NavHostFragment.findNavController(this);

        Bundle args = new Bundle();
        args.putString("title", clickedItem.getName());

        navController.navigate(R.id.action_to_actionlistfragment, args);
    }
}
