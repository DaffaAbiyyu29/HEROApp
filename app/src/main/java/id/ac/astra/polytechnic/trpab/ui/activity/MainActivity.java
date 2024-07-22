package id.ac.astra.polytechnic.trpab.ui.activity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import id.ac.astra.polytechnic.trpab.R;
import id.ac.astra.polytechnic.trpab.databinding.ActivityMainBinding;
import id.ac.astra.polytechnic.trpab.ui.fragment.others.NotificationDialog;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private MaterialButton logoutButton, backToMaintenanceDashboard, notificationButton;
    String id, nama, role, npk, nim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        id = getIntent().getStringExtra("ID_USER");
        nama = getIntent().getStringExtra("NAMA_USER");
        role = getIntent().getStringExtra("ROLE_USER");
        npk = getIntent().getStringExtra("NPK");
        nim = getIntent().getStringExtra("NIM");

        Bundle bundle = new Bundle();
        bundle.putString("ID_USER", id);
        bundle.putString("NAMA_USER", nama);
        bundle.putString("ROLE_USER", role);
        bundle.putString("NPK", npk);
        bundle.putString("NIM", nim);

        logoutButton = findViewById(R.id.logout);
        backToMaintenanceDashboard = findViewById(R.id.back_to_maintenance);
        notificationButton = findViewById(R.id.notification);

        if (role.equals("Admin")) {
            notificationButton.setVisibility(View.GONE);
        }

        // Tambahkan listener klik ke tombol logout
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        notificationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NotificationDialog dialogFragment = NotificationDialog.newInstance();
                dialogFragment.show(getSupportFragmentManager(), "NotificationDialog");
            }
        });

        backToMaintenanceDashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavController navController = Navigation.findNavController(MainActivity.this, R.id.nav_host_fragment_content_main);
                navController.popBackStack();
            }
        });

        updateDateTime();

        // NavHostFragment dan NavController untuk navigasi dengan fragment
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_content_main);

        NavController navController = navHostFragment.getNavController();
        navController.setGraph(R.navigation.mobile_navigation, bundle);
//        navController.navigate(R.id.nav_home, bundle);

        MeowBottomNavigation bottomNavigationView = findViewById(R.id.bottom_nav_view);
       if (bottomNavigationView != null){
           bottomNavigationView.add(new MeowBottomNavigation.Model(1, R.drawable.ic_home));
           bottomNavigationView.add(new MeowBottomNavigation.Model(2, R.drawable.ic_bar));
           bottomNavigationView.add(new MeowBottomNavigation.Model(3, R.drawable.ic_tool));

           bottomNavigationView.setOnClickMenuListener(new Function1<MeowBottomNavigation.Model, Unit>() {
               @Override
               public Unit invoke(MeowBottomNavigation.Model model) {
                   switch (model.getId()) {
                       case 1:
                           navController.navigate(R.id.nav_home, bundle);
                           break;
                       case 2:
                           navController.navigate(R.id.nav_borrowing, bundle);
                           break;
                       case 3:
                           navController.navigate(R.id.nav_maintenance, bundle);
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
                   return null;
               }
           });
           bottomNavigationView.setHovered(true);
           bottomNavigationView.show(1, true);
       } else {
           DrawerLayout drawer = binding.activityContainer;
           NavigationView navigationView = findViewById(R.id.nav_view);

           View headerView = navigationView.getHeaderView(0);
           TextView nameTx = headerView.findViewById(R.id.textViewName);
           TextView roleTx = headerView.findViewById(R.id.textViewRole);
           nameTx.setText(nama);
           roleTx.setText(role);

           mAppBarConfiguration = new AppBarConfiguration.Builder(
                   R.id.nav_home, R.id.nav_borrowing, R.id.nav_maintenance)
                   .setOpenableLayout(drawer)
                   .build();

           NavigationUI.setupWithNavController(navigationView, navController);

//           navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
//               @Override
//               public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//                   boolean handled = NavigationUI.onNavDestinationSelected(item, navController);
//                   if (handled) {
//                       drawer.closeDrawer(GravityCompat.START);
//                   }
//                   return handled;
//               }
//           });
       }
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

//    @Override
//    public boolean onSupportNavigateUp() {
//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
//        updateDateTime();
//        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
//                || super.onSupportNavigateUp();
//    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
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
