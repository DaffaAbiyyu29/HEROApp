package id.ac.astra.polytechnic.trpab.ui.fragment.others;

import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import id.ac.astra.polytechnic.trpab.R;

public class NotificationDialog extends DialogFragment {
    public NotificationDialog() {
        // Required empty public constructor
    }

    public static NotificationDialog newInstance() {
        NotificationDialog fragment = new NotificationDialog();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.popup_notification, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null) {
            // Mendapatkan lebar layar
            int width = getResources().getDisplayMetrics().widthPixels;
            int margin = (int) (40 * getResources().getDisplayMetrics().density);
            getDialog().getWindow().setLayout(width - 2 * margin, ViewGroup.LayoutParams.WRAP_CONTENT);
            getDialog().getWindow().setGravity(Gravity.TOP);
            getDialog().getWindow().setBackgroundDrawableResource(R.drawable.bottom_nav_background);
        }
    }
}