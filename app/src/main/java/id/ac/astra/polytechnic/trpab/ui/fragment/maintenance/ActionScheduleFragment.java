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
import id.ac.astra.polytechnic.trpab.data.adapter.ChecklistAdapter;
import id.ac.astra.polytechnic.trpab.data.model.ChecklistItem;
import id.ac.astra.polytechnic.trpab.databinding.FragmentActionlistBinding;

public class ActionScheduleFragment extends Fragment implements  ChecklistAdapter.OnItemClickListener{
    private FragmentActionlistBinding binding;
    private RecyclerView recyclerView;
    private ChecklistAdapter checklistAdapter;
    private List<ChecklistItem> schaduleItemList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentActionlistBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        recyclerView = view.findViewById(R.id.recycler_view_schadulelist);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        TextView titleTextView = binding.textView2;

        // Retrieve the Schadule ID from arguments
        Bundle args = getArguments();
        if (args != null) {
            String schaduleId = args.getString("title", "Default Title");
            // Use schaduleId to fetch or display relevant data

            titleTextView.setText(schaduleId);
        }

        schaduleItemList = new ArrayList<>();

        loadServiceItems();

        ((MainActivity) getActivity()).showBackButton();
        return view;
    }

    private void loadServiceItems() {
        // Add some dummy data to the list for testing
        schaduleItemList.add(new ChecklistItem("1","Check, Clean and Replace Air Cleaner Element", "1","1", false));
        schaduleItemList.add(new ChecklistItem("2","Clean Inside of Cooling System","1","1", false));

        checklistAdapter = new ChecklistAdapter(schaduleItemList, this);
        recyclerView.setAdapter(checklistAdapter);
    }

    @Override
    public void onItemClick(int position) {
        ChecklistItem clickedItem = schaduleItemList.get(position);
        NavController navController = NavHostFragment.findNavController(this);

        Bundle args = new Bundle();
        args.putString("title", clickedItem.getName());

        navController.navigate(R.id.action_to_workmanshipDetailsFragment, args);
    }
}
