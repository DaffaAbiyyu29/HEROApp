package id.ac.astra.polytechnic.trpab.ui.fragment.maintenance;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;

import java.util.List;

import id.ac.astra.polytechnic.trpab.R;
import id.ac.astra.polytechnic.trpab.data.api.ApiClient;
import id.ac.astra.polytechnic.trpab.data.api.ApiService;
import id.ac.astra.polytechnic.trpab.data.api.DataResponse;
import id.ac.astra.polytechnic.trpab.data.model.ChecklistItem;
import id.ac.astra.polytechnic.trpab.databinding.FragmentWorkmanshipdetailsBinding;
import id.ac.astra.polytechnic.trpab.ui.activity.MainActivity;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WorkmanshipDetailsFragment extends Fragment {
    private FragmentWorkmanshipdetailsBinding binding;
    private String actid;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentWorkmanshipdetailsBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        Bundle args = getArguments();
        if (args != null) {
            String schaduleId = args.getString("title");
            String foto = args.getString("foto");
            String keterangan = args.getString("keterangan");

            actid = args.getString("id");

            binding.textview1.setText(schaduleId);
            binding.detailKeteranganText.setText(keterangan);

            if (foto != null && !foto.isEmpty()) {
                // Assuming "foto" is a part of the image URL
                String host = ApiClient.BASE_URL;
                String imageUrl = host + "HEROAPP_BE/LoadActionImage.php/" + actid;
                Glide.with(this)
                        .load(imageUrl)
                        .placeholder(R.drawable.image1) // Set a placeholder image if needed
                        .error(R.drawable.image1) // Set an error image if needed
                        .into(binding.selectedImageView);
            } else {
                binding.selectedImageView.setImageResource(R.drawable.image1); // Set a placeholder image if needed
            }


        }

        ((MainActivity) getActivity()).showBackButton();
        return view;
    }

/*    private void fetchChecklistItemDetails() {
        String json = "{\"act_id\":\"" + actid + "\"}";
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), json);
        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        Call<DataResponse<ChecklistItem>> call = apiService.getDataAction(body);

        call.enqueue(new Callback<DataResponse<ChecklistItem>>() {
            @Override
            public void onResponse(Call<DataResponse<ChecklistItem>> call, Response<DataResponse<ChecklistItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<ChecklistItem> checklistItems = response.body().getResult();
                    if (checklistItems != null && !checklistItems.isEmpty()) {
                        ChecklistItem checklistItem = checklistItems.get(0);
                        binding.textview1.setText(checklistItem.getName());
                        binding.standarisasiText.setText(checklistItem.getKeterangan());
                        Glide.with(WorkmanshipDetailsFragment.this)
                                .load(checklistItem.getFoto())
                                .into(binding.selectedImageView);
                    }
                }
            }

            @Override
            public void onFailure(Call<DataResponse<ChecklistItem>> call, Throwable t) {
                // Handle failure
            }
        });
    }

    private RequestBody createRequestBody(String actId) {
        return RequestBody.create(MediaType.parse("application/json"), "{\"act_id\":\"" + actId + "\"}");
    }*/
}
