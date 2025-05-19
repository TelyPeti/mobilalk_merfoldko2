package com.example.billiard_idopontfoglalo_app;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.usb.UsbEndpoint;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class ProfileActivity extends AppCompatActivity {
    private static final String LOG_TAG = ProfileActivity.class.getName();
    private SharedPreferences sharedPreferences;
    private static final String PREF_KEY = Objects.requireNonNull(ProfileActivity.class.getPackage()).toString();
    private FirebaseFirestore firestore;
    private CollectionReference items;
    private static final int KEY = 99;
    private String emailOfUsers;
    EditText UsernameET;
    EditText phoneNumberET;
    NotificationHandler mNotificationHandler;
    private static final int RECORD_AUDIO_PERMISSION_CODE = 101;
    private SpeechRecognizer speechRecognizer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mNotificationHandler = new NotificationHandler(this);

        UsernameET = findViewById(R.id.UserNameEditText);
        phoneNumberET = findViewById(R.id.PhoneNumberEditText);

        firestore = FirebaseFirestore.getInstance();
        items = firestore.collection("UserData");

        sharedPreferences = getSharedPreferences(PREF_KEY, MODE_PRIVATE);
        emailOfUsers = sharedPreferences.getString("email", "email");

        userDataGiver(sharedPreferences.getString("email", "Senki"));

        requestRecordAudioPermission();

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.RECORD_AUDIO},
                    RECORD_AUDIO_PERMISSION_CODE);
        } else {
            initSpeechRecognizer();
        }
    }


    public void cancel(View view) {
        Intent intent = new Intent(this, HomePageActivity.class);
        intent.putExtra("KEY", KEY);
        startActivity(intent);
    }

    public void DeleteProfile(View view) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            items.whereEqualTo("email", emailOfUsers)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            if (!task.getResult().isEmpty()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    String documentId = document.getId();
                                    items.document(documentId).delete()
                                            .addOnSuccessListener(aVoid -> {
                                                Log.d(LOG_TAG, "Felhasználó sikeresen törölve: " + documentId);

                                                deleteAuthAccount(user);
                                            })
                                            .addOnFailureListener(e -> {
                                                Log.w(LOG_TAG, "Felhasználó törlése sikertelen: " + documentId, e);
                                                Toast.makeText(ProfileActivity.this, "Hiba történt az adatok törlésekor", Toast.LENGTH_SHORT).show();
                                            });
                                }
                            } else {
                                Log.d(LOG_TAG, "Nincs törlendő adat ehhez az emailhez: " + emailOfUsers);
                                deleteAuthAccount(user);
                            }
                        } else {
                            Log.d(LOG_TAG, "Sikertelen lekérdezés: ", task.getException());
                            Toast.makeText(ProfileActivity.this, "Hiba történt az adatok törlésekor", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void deleteAuthAccount(FirebaseUser user) {
        user.delete()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(LOG_TAG, "A felhasználó fiókja sikeresen törölve.");

                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.clear();
                        editor.apply();

                        Toast.makeText(ProfileActivity.this, "Profil törölve", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                        intent.putExtra("KEY", KEY);
                        startActivity(intent);
                        finish();
                    } else {
                        Log.e(LOG_TAG, "Hiba történt a fiók törlésekor.", task.getException());
                        Toast.makeText(ProfileActivity.this, "Hiba történt a fiók törlésekor", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void userDataGiver(String email) {
        items.whereEqualTo("email", email).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String felhasznalonev = document.getString("nev");
                        String phone = document.getString("phoneNumber");

                        UsernameET.setText(felhasznalonev);
                        phoneNumberET.setText(phone);

                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("nev", felhasznalonev);
                        editor.putString("phoneNumber", phone);
                        editor.apply();

                        Log.d(LOG_TAG, "Felhasználói adatok frissítve: " + felhasznalonev + ", " + phone);
                    }
                } else {
                    Log.e(LOG_TAG, "Hiba történt az adatbázis lekérdezése során", task.getException());
                }
            }
        });
    }
    public void Modify(View view) {
        try {
            String newName = UsernameET.getText().toString().trim();
            String newPhone = phoneNumberET.getText().toString().trim();

            if (newName.isEmpty()) {
                Toast.makeText(this, "Nincs megadva felhasználónév!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (emailOfUsers == null || emailOfUsers.isEmpty()) {
                Toast.makeText(this, "Hiba: Érvénytelen felhasználói azonosító", Toast.LENGTH_SHORT).show();
                return;
            }

            Toast.makeText(this, "Adatok frissítése...", Toast.LENGTH_SHORT).show();

            items.whereEqualTo("email", emailOfUsers)
                    .get()
                    .addOnCompleteListener(task -> {
                        try {
                            if (task.isSuccessful()) {
                                QuerySnapshot querySnapshot = task.getResult();
                                if (querySnapshot != null && !querySnapshot.isEmpty()) {
                                    for (QueryDocumentSnapshot document : querySnapshot) {
                                        updateDocument(document, newName, newPhone);
                                    }
                                } else {
                                    Log.w(LOG_TAG, "Nincs találat a megadott emaillel: " + emailOfUsers);
                                    runOnUiThread(() ->
                                            Toast.makeText(this, "Nem található felhasználói adat!", Toast.LENGTH_SHORT).show()
                                    );
                                }
                            } else {
                                Log.e(LOG_TAG, "Sikertelen lekérdezés", task.getException());
                                runOnUiThread(() ->
                                        Toast.makeText(this, "Hiba történt az adatok frissítésekor", Toast.LENGTH_SHORT).show()
                                );
                            }
                        } catch (Exception e) {
                            Log.e(LOG_TAG, "Váratlan hiba a módosítás közben", e);
                            runOnUiThread(() ->
                                    Toast.makeText(this, "Váratlan hiba történt", Toast.LENGTH_SHORT).show()
                            );
                        }
                    });
        } catch (Exception e) {
            Log.e(LOG_TAG, "Váratlan hiba a módosítás kezdetén", e);
            Toast.makeText(this, "Váratlan hiba történt", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateDocument(QueryDocumentSnapshot document, String newName, String newPhone) {
        String documentId = document.getId();
        Map<String, Object> updates = new HashMap<>();
        updates.put("nev", newName);
        updates.put("phoneNumber", newPhone);

        items.document(documentId).update(updates)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(LOG_TAG, "Sikeres módosítás: " + documentId);

                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("nev", newName);
                        editor.putString("phoneNumber", newPhone);
                        editor.apply();

                        Toast.makeText(ProfileActivity.this, "Sikeres módosítás!", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e(LOG_TAG, "Sikertelen módosítás: " + documentId, task.getException());
                        Toast.makeText(ProfileActivity.this, "Sikertelen módosítás: " +
                                        (task.getException() != null ? task.getException().getMessage() : ""),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void initSpeechRecognizer() {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {}

            @Override
            public void onBeginningOfSpeech() {}

            @Override
            public void onRmsChanged(float rmsdB) {}

            @Override
            public void onBufferReceived(byte[] buffer) {}

            @Override
            public void onEndOfSpeech() {}

            @Override
            public void onError(int error) {}

            @Override
            public void onResults(Bundle results) {
                ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (matches != null && matches.size() > 0) {
                    String text = matches.get(0);
                    UsernameET.setText(text);
                }
            }

            @Override
            public void onPartialResults(Bundle partialResults) {}

            @Override
            public void onEvent(int eventType, Bundle params) {}
        });
    }

    private void startSpeechRecognition() {
        if (speechRecognizer != null) {
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Beszéljen most...");
            speechRecognizer.startListening(intent);
        } else {
            Toast.makeText(this, "A hangfelismerő még nincs inicializálva", Toast.LENGTH_SHORT).show();
        }
    }

    private void requestRecordAudioPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.RECORD_AUDIO)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Engedélykérés");
            builder.setMessage("Az alkalmazásnak szüksége van mikrofonhoz való hozzáférésre a hangfelismeréshez.");
            builder.setPositiveButton("Engedély megadása", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ActivityCompat.requestPermissions(ProfileActivity.this,
                            new String[]{android.Manifest.permission.RECORD_AUDIO},
                            RECORD_AUDIO_PERMISSION_CODE);
                }
            });
            builder.setNegativeButton("Elutasítás", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(ProfileActivity.this, "Kérjük, engedélyezze a mikrofonhoz való hozzáférést a beállításokban", Toast.LENGTH_LONG).show();
                }
            });
            builder.show();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.RECORD_AUDIO},
                    RECORD_AUDIO_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RECORD_AUDIO_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initSpeechRecognizer();
            } else {
                Toast.makeText(this, "Mikrofonhoz való hozzáférés megtagadva", Toast.LENGTH_SHORT).show();
            }
        }
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
        if (speechRecognizer != null) {
            speechRecognizer.destroy();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void hangfelismer(View view) {
        startSpeechRecognition();
    }
}