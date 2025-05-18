package com.example.billiard_idopontfoglalo_app;

import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.Objects;

public class ReservationDoneActivity extends AppCompatActivity {
    private static final String PREF_KEY = Objects.requireNonNull(ReservationDoneActivity.class.getPackage()).toString();
    private static final int KEY = 99;
    private FirebaseFirestore firestore;
    private CollectionReference items;
    private FirebaseUser user;
    private LinearLayout reservationsContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_reservation_done);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        firestore = FirebaseFirestore.getInstance();
        items = firestore.collection("Foglalas");
        user = FirebaseAuth.getInstance().getCurrentUser();
        reservationsContainer = findViewById(R.id.reservationsContainer);

        if (user == null) {
            finish();
        } else {
            loadUserReservations();
        }
    }

    private void loadUserReservations() {
        items.whereEqualTo("email", user.getEmail())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        reservationsContainer.removeAllViews();
                        if (task.getResult().isEmpty()) {
                            TextView noReservations = new TextView(this);
                            noReservations.setText("Nincsenek foglalásai");
                            noReservations.setTextSize(18);
                            noReservations.setGravity(Gravity.CENTER);
                            reservationsContainer.addView(noReservations);
                        } else {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                ReservationData reservation = document.toObject(ReservationData.class);
                                addReservationCard(reservation, document.getId());
                            }
                        }
                    } else {
                        Toast.makeText(this, "Hiba a foglalások betöltésekor: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void addReservationCard(ReservationData reservation, String documentId) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setBackgroundResource(R.drawable.rounded_card_background);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(16, 8, 16, 8);
        card.setLayoutParams(params);
        card.setPadding(16, 16, 16, 16);

        TextView emailText = new TextView(this);
        emailText.setText("Email címed: " + reservation.getEmail());
        emailText.setTextSize(16);
        emailText.setTextColor(Color.BLACK);
        card.addView(emailText);

        TextView tableText = new TextView(this);
        tableText.setText("Asztal: " + reservation.getTableNumber());
        tableText.setTextSize(16);
        tableText.setTextColor(Color.BLACK);
        card.addView(tableText);

        TextView dateText = new TextView(this);
        dateText.setText("Dátum: " + reservation.getDate());
        dateText.setTextSize(16);
        dateText.setTextColor(Color.BLACK);
        card.addView(dateText);

        TextView timeText = new TextView(this);
        timeText.setText("Időpont: " + reservation.getTime());
        timeText.setTextSize(16);
        timeText.setTextColor(Color.BLACK);
        card.addView(timeText);

        Button deleteButton = new Button(this);
        deleteButton.setText("Törlés");
        deleteButton.setBackgroundResource(R.drawable.rounded_button);
        deleteButton.setTextColor(Color.WHITE);
        deleteButton.setOnClickListener(v -> deleteReservation(documentId, card));

        LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, getResources().getDisplayMetrics()),
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 45, getResources().getDisplayMetrics())
        );
        buttonParams.gravity = Gravity.END;
        buttonParams.setMargins(0, 8, 0, 0); // Add some top margin
        deleteButton.setLayoutParams(buttonParams);

        card.addView(deleteButton);

        reservationsContainer.addView(card);
    }

    private void deleteReservation(String documentId, View cardView) {
        items.document(documentId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    reservationsContainer.removeView(cardView);
                    Toast.makeText(this, "Foglalás sikeresen törölve", Toast.LENGTH_SHORT).show();

                    if (reservationsContainer.getChildCount() == 0) {
                        TextView noReservations = new TextView(this);
                        noReservations.setText("Nincsenek foglalásaid");
                        noReservations.setTextSize(18);
                        noReservations.setGravity(Gravity.CENTER);
                        reservationsContainer.addView(noReservations);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Hiba a foglalás törlésekor: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    public void cancel(View view) {
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (user != null) {
            loadUserReservations();
        }
    }
}