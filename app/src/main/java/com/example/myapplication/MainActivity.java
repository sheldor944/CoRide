package com.example.myapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.myapplication.helper.Callback;
import com.example.myapplication.helper.GetUserNameCallback;
import com.example.myapplication.helper.PermissionCallback;
import com.example.myapplication.ui.introduction.IntroductionActivity;
import com.example.myapplication.ui.login.LoginActivity;
import com.example.myapplication.ui.notifications.UpdateProfileActivity;
import com.example.myapplication.ui.notifications.ViewProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.myapplication.databinding.ActivityMainBinding;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends testerActivity {
    private static final String TAG = "MainActivity";

    private ActivityMainBinding binding;
    private DrawerLayout mDrawerLayout;
    private View mHeaderView;
    private ActionBarDrawerToggle mToggle;
    private ActionBar mActionBar;
    private NavigationView mNavigationView;
    private MenuItem mLogoutItem;
    private MenuItem mBugReportItem;
    private MenuItem mAboutUsItem;

    private ImageView imageView;
    private TextView textView ;
    private String mUserId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mUserId = FirebaseAuth.getInstance().getUid();

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
//        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);



        initNavigationView();

        imageView = findViewById(R.id.leftNavViewImageView);
        textView = findViewById(R.id.leftNavViewNameText);
//        textView.setText("kitare");
//        loadData();
    }
    private void loadData()
    {
        LocationDB locationDB = new LocationDB() ;
        locationDB.getImageURL(new Callback<Uri>() {
            @Override
            public void onComplete(Uri response) {
//                imageView.setImageURI(response);
//                Glide.with(MainActivity.this).load(response).into(imageView); // Using Glide library to load the image

            }
        });
//        String user = FirebaseAuth.getInstance().getUid();
//        locationDB.getUserName(user, new GetUserNameCallback() {
//            @Override
//            public void onUserNameRecieved(String name) {
//                textView.setText(name);
//            }
//        });
    }

    private void initNavigationView() {
        mNavigationView = findViewById(R.id.left_nav_view);
        mHeaderView = mNavigationView.getHeaderView(0);
        mDrawerLayout = findViewById(R.id.drawerLayout);
        mToggle = new ActionBarDrawerToggle(
                this,
                mDrawerLayout,
                R.string.nav_open,
                R.string.nav_close
        );

        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();

        mActionBar = getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);

        TextView nameTextView = mHeaderView.findViewById(R.id.leftNavViewNameText);
        new LocationDB().getUserName(mUserId, new GetUserNameCallback() {
            @Override
            public void onUserNameRecieved(String name, String phone) {
                nameTextView.setText(name);
            }
        });

        ImageView profileImageView = mHeaderView.findViewById(R.id.leftNavViewImageView);
        new LocationDB().getImageURL(new Callback<Uri>() {
            @Override
            public void onComplete(Uri response) {
                Glide.with(MainActivity.this).load(response).into(profileImageView);
            }
        });

        mLogoutItem = mNavigationView.getMenu().findItem(R.id.logout);
        mLogoutItem.setOnMenuItemClickListener(item -> {
            askForConfirmation("Are you sure you want to logout?", new PermissionCallback() {
                @Override
                public void onPermit() {
                    FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            });
            return true;
        });

        mBugReportItem = mNavigationView.getMenu().findItem(R.id.report_a_bug);
        mBugReportItem.setOnMenuItemClickListener(item -> {
            composeEmail();
            return true;
        });

        mAboutUsItem = mNavigationView.getMenu().findItem(R.id.about_us);
        mAboutUsItem.setOnMenuItemClickListener(item -> {
            Intent intent = new Intent(this, IntroductionActivity.class);
            startActivity(intent);
            return true;
        });
    }

    private void composeEmail() {
        String recipient = "abcd@gmail.com";
        String subject = "Bug Report";

        // Create an Intent with the ACTION_SENDTO action and the mailto URI
        Uri uri = Uri.parse("mailto:" + recipient);
        Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
        // Set the email subject
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);

        // Check if there's a Gmail app installed
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            // If Gmail is not installed, handle this case accordingly
            // For example, you can open a web browser with a mailto link
            // or display a message to the user.
            Toast.makeText(
                    this,
                    "No email application found",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    private void askForConfirmation(String query, PermissionCallback callback) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage(query)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked "Yes," handle the action here
                        // For example, delete something or proceed with an action
                        Log.d(TAG, "onClick: clicked on yes");
                        callback.onPermit();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked "No," handle the action here
                        // For example, do nothing or close the dialog
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(mToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed: ");
        askForConfirmation("Are you sure you want to exit?", new PermissionCallback() {
            @Override
            public void onPermit() {
                MainActivity.this.finishAffinity();
            }
        });
    }
}