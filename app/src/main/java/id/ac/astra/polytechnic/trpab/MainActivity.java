package id.ac.astra.polytechnic.trpab;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import id.ac.astra.polytechnic.trpab.databinding.ActivityMainBinding;
import id.ac.astra.polytechnic.trpab.ui.login.LoginActivity;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private MaterialButton logoutButton;
    private MaterialButton backToMaintenanceDashboard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);

        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        logoutButton = findViewById(R.id.logout);
        backToMaintenanceDashboard = findViewById(R.id.back_to_maintenance);


        // Tambahkan listener klik ke tombol logout
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        backToMaintenanceDashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                NavController navController = Navigation.findNavController(MainActivity.this, R.id.nav_host_fragment_content_main);
//                navController.navigate(R.id.nav_maintenance);
                NavController navController = Navigation.findNavController(MainActivity.this, R.id.nav_host_fragment_content_main);
                navController.popBackStack();
            }
        });

        updateDateTime();

        // NavHostFragment dan NavController untuk navigasi dengan fragment
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_content_main);
        assert navHostFragment != null;
        NavController navController = navHostFragment.getNavController();

        // Menghubungkan BottomNavigationView dengan NavController
//        BottomNavigationView bottomNavigationView = binding.bottomNavView;
//        if (bottomNavigationView != null) {
//            mAppBarConfiguration = new AppBarConfiguration.Builder(
//                    R.id.nav_home, R.id.nav_borrowing, R.id.nav_maintenance, R.id.nav_maintenance_process, R.id.nav_maintenance_history)
//                    .build();
//            NavigationUI.setupWithNavController(bottomNavigationView, navController);
//        }

        @SuppressLint({"MissingInflatedId", "LocalSuppress"})
        MeowBottomNavigation bottomNavigationView = findViewById(R.id.bottom_nav_view);
        bottomNavigationView.add(new MeowBottomNavigation.Model(1, R.drawable.ic_home));
        bottomNavigationView.add(new MeowBottomNavigation.Model(2, R.drawable.ic_bar));
        bottomNavigationView.add(new MeowBottomNavigation.Model(3, R.drawable.ic_tool));

        bottomNavigationView.setOnClickMenuListener(new Function1<MeowBottomNavigation.Model, Unit>() {
            @Override
            public Unit invoke(MeowBottomNavigation.Model model) {
                switch (model.getId()) {
                    case 1:
                        navController.navigate(R.id.nav_home);
                        break;
                    case 2:
                        navController.navigate(R.id.nav_borrowing);
                        break;
                    case 3:
                        navController.navigate(R.id.nav_maintenance);
                        break;
                }
                return null;
            }
        });

        bottomNavigationView.setOnShowListener(new Function1<MeowBottomNavigation.Model, Unit>() {
            @Override
            public Unit invoke(MeowBottomNavigation.Model model) {
                String name;
                switch (model.getId()) {
                    case 1:
                        name = "Home";
                        break;
                    case 2:
                        name = "Borrowing";
                        break;
                    case 3:
                        name = "Maintenance";
                        break;
                    default:
                        name = "";
                        break;
                }
//                bottomNavigationView.setCount(1, "9");
                return null;
            }
        });
        bottomNavigationView.show(1, true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean result = super.onCreateOptionsMenu(menu);
        NavigationView navView = findViewById(R.id.nav_view);
        if (navView == null) {
            getMenuInflater().inflate(R.menu.overflow, menu);
        }

        updateDateTime();

        return result;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.nav_settings) {
            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
            navController.navigate(R.id.nav_settings);
            updateDateTime();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        updateDateTime();
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private void updateDateTime() {
//        TextView textView = findViewById(R.id.text_date);
//        TextView textView2 = findViewById(R.id.text_time);
        SimpleDateFormat outputFormat = new SimpleDateFormat("EEEE, d MMMM yyyy", new Locale("id", "ID"));
        SimpleDateFormat outputFormat2 = new SimpleDateFormat("HH:mm 'WIB'", new Locale("id", "ID"));

        try {
            Date dateNow = new Date();
            String formattedDate = outputFormat.format(dateNow);
            String formattedDate2 = outputFormat2.format(dateNow);
//            textView.setText(formattedDate);
//            textView2.setText(formattedDate2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showBackButton() {
        if (logoutButton != null && backToMaintenanceDashboard != null) {
            logoutButton.setVisibility(View.GONE);
            backToMaintenanceDashboard.setVisibility(View.VISIBLE);
        }
    }

    public void showLogoutButton() {
        if (logoutButton != null && backToMaintenanceDashboard != null) {
            logoutButton.setVisibility(View.VISIBLE);
            backToMaintenanceDashboard.setVisibility(View.GONE);
        }
    }
}
