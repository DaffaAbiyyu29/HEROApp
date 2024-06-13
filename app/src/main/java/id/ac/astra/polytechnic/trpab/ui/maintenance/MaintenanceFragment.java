package id.ac.astra.polytechnic.trpab.ui.maintenance;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import id.ac.astra.polytechnic.trpab.R;

public class MaintenanceFragment extends Fragment {

    private MaintenanceViewModel mViewModel;

    public static MaintenanceFragment newInstance() {
        return new MaintenanceFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_maintenance, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(MaintenanceViewModel.class);
        // TODO: Use the ViewModel
    }

}