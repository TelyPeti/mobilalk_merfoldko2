package com.example.billiard_idopontfoglalo_app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class ReservationMakeActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    private static final String PREF_KEY = Objects.requireNonNull(ReservationMakeActivity.class.getPackage()).toString();
    private FirebaseFirestore firestore;
    private FirebaseUser user;
    private CollectionReference items;
    private static final int KEY = 99;
    private Spinner dateSp;
    private String selectedDate = "";
    private NotificationHandler notificationHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_reservation_make);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null){
            finish();
        }

        sharedPreferences = getSharedPreferences(PREF_KEY, MODE_PRIVATE);

        dateSp = findViewById(R.id.dateSpinner);
        String[] values = getResources().getStringArray(R.array.available_dates);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, values);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dateSp.setAdapter(adapter);
        String dates = sharedPreferences.getString("Időpont", "14:00–15:00");
        dateSp.setSelection(adapter.getPosition(dates));

        String selectedTable = getIntent().getStringExtra("SELECTED_TABLE");
        TextView tableNumberTextView = findViewById(R.id.UserNameTextview);
        if (selectedTable != null) {
            tableNumberTextView.setText("Asztalszám: " + selectedTable);
        }

        firestore = FirebaseFirestore.getInstance();
        items = firestore.collection("Foglalas");


        CalendarView calendarView = findViewById(R.id.DatePicker);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        selectedDate = sdf.format(new Date());
        calendarView.setDate(System.currentTimeMillis());

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                selectedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, dayOfMonth);
            }
        });

        notificationHandler = new NotificationHandler(this);

    }

    public void make_reservation(View view) {
        TextView tableNumberTextView = findViewById(R.id.UserNameTextview);
        String tableNumberText = tableNumberTextView.getText().toString();
        String tableNumber = tableNumberText.replace("Asztalszám: ", "");

        String selectedTime = dateSp.getSelectedItem().toString();
        String userEmail = user.getEmail();

        if (tableNumber.isEmpty() || selectedDate.isEmpty() || selectedTime.isEmpty() || userEmail == null) {
            Toast.makeText(this, "Kérem válasszon minden opciónál!", Toast.LENGTH_SHORT).show();
            return;
        }

        items.whereEqualTo("tableNumber", tableNumber)
                .whereEqualTo("date", selectedDate)
                .whereEqualTo("time", selectedTime)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (!task.getResult().isEmpty()) {
                            Toast.makeText(ReservationMakeActivity.this,
                                    "Ez az asztal már foglalt, a kiválasztott időpontban!",
                                    Toast.LENGTH_LONG).show();
                        } else {
                            ReservationData reservation = new ReservationData(
                                    userEmail,
                                    tableNumber,
                                    selectedDate,
                                    selectedTime
                            );

                            items.add(reservation)
                                    .addOnSuccessListener(documentReference -> {
                                        Toast.makeText(ReservationMakeActivity.this,
                                                "Sikeres foglalás!",
                                                Toast.LENGTH_SHORT).show();
                                        if (notificationHandler != null) {
                                            notificationHandler.sendTicket("Sikeresen rögzítettük a foglalásod, sok szeretettel várunk a termünkben!");
                                        }
                                        Intent intent = new Intent(ReservationMakeActivity.this, HomePageActivity.class);
                                        intent.putExtra("KEY", KEY);
                                        startActivity(intent);
                                        finish();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(ReservationMakeActivity.this,
                                                "Hiba a foglalás mentésekor: " + e.getMessage(),
                                                Toast.LENGTH_SHORT).show();
                                    });
                        }
                    } else {
                        Toast.makeText(ReservationMakeActivity.this,
                                "Hiba a foglalás ellenörzésekor: " + task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void cancel(View view) {
        Intent intent = new Intent(this, TableChoosingActivity.class);
        intent.putExtra("KEY", KEY);
        startActivity(intent);
    }

    public void toHome(View view) {
        Intent intent = new Intent(this, HomePageActivity.class);
        intent.putExtra("KEY", KEY);
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
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}