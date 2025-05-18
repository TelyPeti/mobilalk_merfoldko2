package com.example.billiard_idopontfoglalo_app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class TableChoosingActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    private static final String PREF_KEY = Objects.requireNonNull(TableChoosingActivity.class.getPackage()).toString();
    private FirebaseFirestore firestore;
    private CollectionReference items;
    private static final int KEY = 99;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_table_choosing);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        sharedPreferences = getSharedPreferences(PREF_KEY, MODE_PRIVATE);
        firestore = FirebaseFirestore.getInstance();
        items = firestore.collection("UserData");
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
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public void cancel(View view) {
        Intent intent = new Intent(this, HomePageActivity.class);
        intent.putExtra("KEY", KEY);
        startActivity(intent);
    }

    public void table_choosing(View view) {
        Intent intent = new Intent(this, ReservationMakeActivity.class);
        intent.putExtra("KEY", KEY);

        Button clickedButton = (Button) view;
        String buttonText = clickedButton.getText().toString();

        String tableNumber = buttonText.trim();

        intent.putExtra("SELECTED_TABLE", tableNumber);
        startActivity(intent);
    }
}