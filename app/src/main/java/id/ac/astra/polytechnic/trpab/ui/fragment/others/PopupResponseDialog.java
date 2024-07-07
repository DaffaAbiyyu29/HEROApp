package id.ac.astra.polytechnic.trpab.ui.fragment.others;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import id.ac.astra.polytechnic.trpab.R;


public class PopupResponseDialog extends DialogFragment {
    private static final String ARG_STATUS = "status";
    private static final String ARG_DESC = "desc";
    private static final String ARD_ICON = "icon";
    private TextView response_status, response_message;
    private ImageView response_icon;

    public static PopupResponseDialog newInstance(String status, String desc, int icon) {
        PopupResponseDialog fragment = new PopupResponseDialog();
        Bundle args = new Bundle();
        args.putString(ARG_STATUS, status);
        args.putString(ARG_DESC, desc);
        args.putInt(ARD_ICON, icon);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.popup_response, container, false);
        Button okButton = view.findViewById(R.id.btn_ok);
        Button cancelButton = view.findViewById(R.id.btn_batal);

        response_status = view.findViewById(R.id.response_status);
        response_message = view.findViewById(R.id.response_message);
        response_icon = view.findViewById(R.id.imageView2);

        Bundle args = getArguments();
        if (args != null) {
            String status = args.getString(ARG_STATUS);
            String desc = args.getString(ARG_DESC);
            int icon = args.getInt(ARD_ICON);

            if (status.equals("Berhasil !")){
                response_status.setText(status);
                response_message.setText(desc);
                response_icon.setImageResource(icon);
                cancelButton.setVisibility(View.GONE);
            } else if (status.equals("Perhatian !")){
                response_status.setText(status);
                response_message.setText(desc);
                response_icon.setImageResource(icon);
            }
        }

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Menutup dialog ketika tombol cancel diklik
                dismiss();
            }
        });

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Menutup dialog ketika tombol cancel diklik
                dismiss();
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        // Mengatur lebar dan tinggi dialog (misalnya 80% dari lebar layar)
        if (getDialog() != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            getDialog().getWindow().setLayout(width, height);
        }
    }
}
