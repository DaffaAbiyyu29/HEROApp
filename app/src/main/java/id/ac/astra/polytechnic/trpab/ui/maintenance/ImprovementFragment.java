package id.ac.astra.polytechnic.trpab.ui.maintenance;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import id.ac.astra.polytechnic.trpab.MainActivity;
import id.ac.astra.polytechnic.trpab.R;
import id.ac.astra.polytechnic.trpab.databinding.FragmentPerbaikanBinding;

public class ImprovementFragment extends Fragment {

    private static final String ARG_TITLE = "title";
    private static final String ARG_NAME = "name";

    private FragmentPerbaikanBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPerbaikanBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        TextView titleTextView3 = view.findViewById(R.id.textView3);
        titleTextView3.setText("Data Perbaikan");

        TextView titleTextView4 = view.findViewById(R.id.textView4);

        // Retrieve and set the name if provided
        Bundle args = getArguments();
        if (args != null) {
            String title = args.getString("title", "Default Title");
            titleTextView4.setText(title);
        }

        ((MainActivity) getActivity()).showBackButton();

        return view;
    }

    public static ImprovementFragment newInstance(String title, String name) {
        ImprovementFragment improvementFragment = new ImprovementFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putString(ARG_NAME, name);

        improvementFragment.setArguments(args);
        return improvementFragment;
    }
}
