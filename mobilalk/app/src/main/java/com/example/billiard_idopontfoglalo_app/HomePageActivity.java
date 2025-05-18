package com.example.billiard_idopontfoglalo_app;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.app.AlarmManager;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import android.Manifest;

import java.util.Objects;

public class HomePageActivity extends AppCompatActivity {
    private static final String LOG_TAG = HomePageActivity.class.getName();
    private static final String PREF_KEY = Objects.requireNonNull(HomePageActivity.class.getPackage()).toString();
    private static final int SECRET_KEY = 99;
    private AlarmManager mAlarmManager;
    private FirebaseFirestore firestore;
    private CollectionReference items;
    private SharedPreferences sharedPreferences;
    Button reserveButton;
    TextView welcomeTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        sharedPreferences = getSharedPreferences(PREF_KEY, MODE_PRIVATE);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            Log.d(LOG_TAG, "Authenticated user");
        } else {
            Log.d(LOG_TAG, "Not authenticated user");
            finish();
        }

        firestore = FirebaseFirestore.getInstance();
        items = firestore.collection("UserData");

        reserveButton = findViewById(R.id.reserveButton);
        welcomeTextView = findViewById(R.id.WelcomeTextView);

        mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        setAlarmManager();
    }

    public void logout(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("SECRET_KEY", SECRET_KEY);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() { super.onPause(); }

    @Override
    protected void onResume() {
        super.onResume();
        Animation anim_one = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.animation_one);
        reserveButton.startAnimation(anim_one);
        Animation anim_two = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.animation_two);
        welcomeTextView.startAnimation(anim_two);
    }

    public void make_reservation_homepage(View view) {
        Intent intent = new Intent(this, TableChoosingActivity.class);
        intent.putExtra("SECRET_KEY", SECRET_KEY);
        startActivity(intent);
    }

    public void profile(View view) {
        Intent intent = new Intent(this, ProfileActivity.class);
        intent.putExtra("SECRET_KEY", SECRET_KEY);
        startActivity(intent);
    }


    public void check_reservations(View view) {
        Intent intent = new Intent(this, ReservationDoneActivity.class);
        intent.putExtra("SECRET_KEY", SECRET_KEY);
        startActivity(intent);
    }

    private void setAlarmManager() {
        long repeatInterval = 30000;
        long triggerTime = SystemClock.elapsedRealtime() + repeatInterval;

        Intent intent = new Intent(this, AlarmReceiver.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        android.app.AlarmManager alarmManager = (android.app.AlarmManager) getSystemService(ALARM_SERVICE);

        alarmManager.setInexactRepeating(
                android.app.AlarmManager.ELAPSED_REALTIME_WAKEUP,
                triggerTime,
                repeatInterval,
                pendingIntent
        );
    }

}
