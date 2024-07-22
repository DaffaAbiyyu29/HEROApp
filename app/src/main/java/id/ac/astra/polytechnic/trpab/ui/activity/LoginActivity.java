package id.ac.astra.polytechnic.trpab.ui.activity;

import androidx.lifecycle.ViewModelProvider;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;

import id.ac.astra.polytechnic.trpab.R;
import id.ac.astra.polytechnic.trpab.data.api.DataResponse;
import id.ac.astra.polytechnic.trpab.data.model.User;
import id.ac.astra.polytechnic.trpab.data.viewmodel.LoginViewModel;
import id.ac.astra.polytechnic.trpab.databinding.ActivityLoginBinding;
import id.ac.astra.polytechnic.trpab.ui.fragment.others.PopupResponseDialog;

public class LoginActivity extends AppCompatActivity {

    private LoginViewModel mViewModel;
    private ActivityLoginBinding binding;
    Bundle args;
    String id, nama, role, npk, nim;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mViewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        final EditText usernameEditText = (EditText) binding.username;
        final EditText passwordEditText = (EditText) binding.password;
        final MaterialButton loginButton = (MaterialButton) binding.login;
        final ProgressBar loadingProgressBar = binding.loading;

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = String.valueOf(usernameEditText.getText());
                String password = String.valueOf(passwordEditText.getText());

                if (username.isEmpty()) {
                    usernameEditText.setError("Username tidak boleh kosong!");
                    return;
                }

                if (password.isEmpty()) {
                    passwordEditText.setError("Password tidak boleh kosong!");
                    return;
                }

                loadingProgressBar.setVisibility(View.VISIBLE);

                mViewModel.login(username, password, new LoginViewModel.UserCallback() {
                    @Override
                    public void onSuccess(DataResponse<User> result) {
                        // Handle success
                        String message = String.valueOf(result.getMessage());
                        Gson gson = new Gson();
                        Log.d("oooologin", gson.toJson(result.getResult()));
                        String jsonResponse = gson.toJson(result.getResult());

                        User[] users = gson.fromJson(jsonResponse, User[].class);
                        for (User user : users) {
                            id = user.getId();
                            npk = user.getNpk();
                            nim = user.getNim();
                            nama = user.getNama();
                            role = user.getRole();
                        }

                        if (result.getResult() != null){
                            if (message.equals("Login Berhasil")){
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        loadingProgressBar.setVisibility(View.GONE);
                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                        intent.putExtra("ID_USER", id);
                                        intent.putExtra("NAMA_USER", nama);
                                        intent.putExtra("ROLE_USER", role);
                                        intent.putExtra("NPK", npk);
                                        intent.putExtra("NIM", nim);
                                        startActivity(intent);
                                    }
                                }, 1500);
                            } else {
                                loadingProgressBar.setVisibility(View.GONE);
                                PopupResponseDialog dialogFragment = PopupResponseDialog.newInstance(
                                        "Gagal !",
                                        message,
                                        R.drawable.ic_warning
                                );
                                dialogFragment.show(getSupportFragmentManager(), "PopupResponseDialog");
                            }
                        } else {
                            loadingProgressBar.setVisibility(View.GONE);
                            PopupResponseDialog dialogFragment = PopupResponseDialog.newInstance(
                                    "Gagal !",
                                    message,
                                    R.drawable.ic_warning
                            );
                            dialogFragment.show(getSupportFragmentManager(), "PopupResponseDialog");
                        }
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        loadingProgressBar.setVisibility(View.GONE);
                        PopupResponseDialog dialogFragment = PopupResponseDialog.newInstance(
                                "Gagal !",
                                t.getMessage(),
                                R.drawable.ic_warning
                        );
                        dialogFragment.show(getSupportFragmentManager(), "PopupResponseDialog");
                    }
                });
            }
        });
    }
}
