package com.example.billiard_idopontfoglalo_app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;


import java.util.Objects;


public class RegistrationActivity extends AppCompatActivity {
    private static final String LOG_TAG = RegistrationActivity.class.getName();
    private static final int SECRET_KEY = 99;
    private static final String PREF_KEY = Objects.requireNonNull(RegistrationActivity.class.getPackage()).toString();
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    private CollectionReference items;
    EditText userNameET;
    EditText userEmailET;
    EditText phoneNumberET;
    EditText passwordET;
    EditText passwordAgainET;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registration);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Bundle bundle = getIntent().getExtras();
        assert bundle != null;
        int secret_key = bundle.getInt("SECRET_KEY");

        if (secret_key != 99) {
            finish();
        }

        userNameET = findViewById(R.id.UserNameEditText);
        userEmailET = findViewById(R.id.EmailEditText);
        phoneNumberET = findViewById(R.id.PhoneNumberEditText);
        passwordET = findViewById(R.id.PasswordEditText);
        passwordAgainET = findViewById(R.id.PasswordAgainEditText);
        SharedPreferences sharedPreferences = getSharedPreferences(PREF_KEY, MODE_PRIVATE);
        String email = sharedPreferences.getString("email", "");
        userEmailET.setText(email);
        auth = FirebaseAuth.getInstance();

        firestore = FirebaseFirestore.getInstance();
        items = firestore.collection("UserData");
    }

    public void registration(View view) {
        String userNameString = userNameET.getText().toString();
        String userEmailString = userEmailET.getText().toString();
        String phoneNumberString = phoneNumberET.getText().toString();
        String userPasswordString = passwordET.getText().toString();
        String userPasswordAgainString = passwordAgainET.getText().toString();

        if (userNameString.isEmpty() || userEmailString.isEmpty() || phoneNumberString.isEmpty() || userPasswordString.isEmpty()) {
            Toast.makeText(this, "Minden mező kitöltése kötelező!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!userEmailString.contains("@")){
            Toast.makeText(this, "Nem megfelelő email formátum!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!userPasswordString.equals(userPasswordAgainString)) {
            Toast.makeText(this, "A jelszavak nem egyeznek!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (userPasswordString.length() < 6) {
            Toast.makeText(this, "A jelszónak legalább 6 karakter hosszúnak kell lennie!", Toast.LENGTH_SHORT).show();
            return;
        }

        auth.createUserWithEmailAndPassword(userEmailString, userPasswordString)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(LOG_TAG, "Sikeres regisztráció!");
                            UserData ud = new UserData(userNameString, userEmailString, phoneNumberString);

                            SharedPreferences sharedPreferences = getSharedPreferences(PREF_KEY, MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("email", userEmailString);
                            editor.putString("nev", userNameString);
                            editor.putString("phoneNumber", phoneNumberString);
                            editor.apply();

                            items.add(ud)
                                    .addOnSuccessListener(documentReference -> {
                                        Log.d(LOG_TAG, "Felhasználói adatok sikeresen elmentve: " + documentReference.getId());
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e(LOG_TAG, "Hiba történt a felhasználói adatok mentésekor", e);
                                    });

                            startApp();

                        } else {
                            Log.w(LOG_TAG, "Sikertelen regisztráció!", task.getException());
                            Toast.makeText(RegistrationActivity.this, "Sikertelen regisztráció!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }


    private void startApp() {
        Intent intent = new Intent(this, HomePageActivity.class);
        intent.putExtra("SECRET_KEY", SECRET_KEY);
        startActivity(intent);
    }

    public void cancel(View view) {
        finish();
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