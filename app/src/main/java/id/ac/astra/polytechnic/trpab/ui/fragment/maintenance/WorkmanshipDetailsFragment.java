package id.ac.astra.polytechnic.trpab.ui.fragment.maintenance;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import id.ac.astra.polytechnic.trpab.ui.activity.MainActivity;
import id.ac.astra.polytechnic.trpab.databinding.FragmentWorkmanshipdetailsBinding;

public class WorkmanshipDetailsFragment extends Fragment {
    private FragmentWorkmanshipdetailsBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentWorkmanshipdetailsBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

//        recyclerView = view.findViewById(R.id.recycler_view_schadulelist);
//        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//
//        TextView titleTextView = binding.textView2;
//
//        // Retrieve the Schadule ID from arguments
//        Bundle args = getArguments();
//        if (args != null) {
//            String schaduleId = args.getString("title", "Default Title");
//            // Use schaduleId to fetch or display relevant data
//
//            titleTextView.setText(schaduleId);
//        }
//
//        schaduleItemList = new ArrayList<>();
//

        ((MainActivity) getActivity()).showBackButton();
        return view;
    }
}
