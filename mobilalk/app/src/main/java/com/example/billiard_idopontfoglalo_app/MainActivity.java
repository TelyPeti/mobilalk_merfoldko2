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

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = MainActivity.class.getName();
    private static final String PREF_KEY = Objects.requireNonNull(MainActivity.class.getPackage()).toString();
    private static final int SECRET_KEY = 99;
    private static final int RC_SIGNIN = 9001;
    EditText userEmailET;
    EditText passwordET;
    private SharedPreferences sharedPreferences;
    private FirebaseAuth auth;
    private GoogleSignInClient googleSignInClient;
    private FirebaseFirestore firestore;
    private CollectionReference items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        userEmailET = findViewById(R.id.EmailEditText);
        passwordET = findViewById(R.id.PasswordEditText);
        sharedPreferences = getSharedPreferences(PREF_KEY, MODE_PRIVATE);
        auth = FirebaseAuth.getInstance();

        firestore = FirebaseFirestore.getInstance();
        items = firestore.collection("UserData");

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    public void login(View view) {
        String userEmailString = userEmailET.getText().toString();
        String userPasswordString = passwordET.getText().toString();

        if (userEmailString.isEmpty() || userPasswordString.isEmpty()) {
            Toast.makeText(MainActivity.this, "Hibás felhasználó név vagy jelszó!", Toast.LENGTH_LONG).show();
        }else{
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("email", userEmailString);
            editor.apply();
            Log.i(LOG_TAG, "onPause");
            userDataGiver(userEmailString);

            auth.signInWithEmailAndPassword(userEmailString, userPasswordString).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        Log.d(LOG_TAG, "Sikeres bejelentkezés!");
                        startApp();
                    }else {
                        Log.d(LOG_TAG, "Sikertelen bejelentkezés: ", task.getException());
                        Toast.makeText(MainActivity.this, "Hibás adatok!", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    private void userDataGiver(String email) {
        items.whereEqualTo("email", email).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String nev = document.getString("nev");
                        String phone = document.getString("phoneNumber");

                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("email", email);
                        if (nev != null) editor.putString("nev", nev);
                        if (phone != null) editor.putString("phoneNumber", phone);
                        editor.apply();

                        Log.d(LOG_TAG, "Felhasználói adatok elmentve SharedPreferences-be");
                    }
                } else {
                    Log.e(LOG_TAG, "Hiba történt az adatbázis lekérdezése során", task.getException());
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGNIN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(LOG_TAG, "Sikeres bejelentkezés!" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                Log.w(LOG_TAG, "Sikertelen bejelentkezés! StatusCode: " + e.getStatusCode(), e);
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        auth.signInWithCredential(credential).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                Log.w(LOG_TAG, "Sikeres bejelentkezés!");
                String email = auth.getCurrentUser().getEmail();
                if (email != null) {
                    userDataGiver(email);
                }
                startApp();
            } else {
                Log.w(LOG_TAG, "Sikertelen bejelentkezés!", task.getException());
                Toast.makeText(MainActivity.this, "Sikertelen bejelentkezés: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void loginWithGoogle(View view) {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGNIN);
    }

    private void startApp() {
        Intent intent = new Intent(this, HomePageActivity.class);
        startActivity(intent);
    }

    public void regist(View view) {
        Intent intent = new Intent(this, RegistrationActivity.class);
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
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("email", userEmailET.getText().toString());
        editor.apply();
    }
}