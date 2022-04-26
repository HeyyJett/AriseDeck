package com.example.arisedeck;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class CreateAccount extends AppCompatActivity {

    Button createAccBtn;
    EditText firstName, lastName, username, email, password;
    FirebaseFirestore db;
    String StoreLocation;

    // Locations
    FusedLocationProviderClient fusedLocationProviderClient;

    // Need to store what user already put in the boxes
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        String firstName = ((EditText) findViewById(R.id.firstNameET)).getText().toString();
        String lastName = ((EditText) findViewById(R.id.lastNameET)).getText().toString();
        String username = ((EditText) findViewById(R.id.usernameET)).getText().toString();
        String email = ((EditText) findViewById(R.id.emailET)).getText().toString();
        String password = ((EditText) findViewById(R.id.passwordET)).getText().toString();

        outState.putString("firstName", firstName);
        outState.putString("lastName", lastName);
        outState.putString("username", username);
        outState.putString("email", email);
        outState.putString("password", password);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        firstName = (EditText) findViewById(R.id.firstNameET);
        lastName = (EditText) findViewById(R.id.lastNameET);
        username = (EditText) findViewById(R.id.usernameET);
        email = (EditText) findViewById(R.id.emailET);
        password = (EditText) findViewById(R.id.passwordET);
        createAccBtn = (Button) findViewById(R.id.register_btn);

        if (savedInstanceState != null) {
            firstName.setText(savedInstanceState.getString("firstName"));
            lastName.setText(savedInstanceState.getString("lastName"));
            username.setText(savedInstanceState.getString("username"));
            email.setText(savedInstanceState.getString("email"));
            password.setText(savedInstanceState.getString("password"));
        }

        // Locations Services
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        // On Click - on create
        getLastLocation();

        // Send all this information to database and log in the user or send them to the login screen
        createAccBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Firestore DB
                db = FirebaseFirestore.getInstance();

                String fName = firstName.getText().toString();
                String lName = lastName.getText().toString();
                String userName = username.getText().toString();
                String eMail = email.getText().toString();
                String passWord = password.getText().toString();

                Map<String, Object> user = new HashMap<>();
                user.put("First Name", fName);
                user.put("Last Name", lName);
                user.put("Username", userName);
                user.put("Email", eMail);
                user.put("Password", passWord);
                user.put("Rank", 1);
                user.put("XP", 0);
                user.put("Completed", 0);

                // Challenges
                user.put("DChal1", false);
                user.put("DChal1Desc", "Do 30 push-ups");
                user.put("DChal2", false);
                user.put("DChal2Desc", "Do 30 pull-ups");
                user.put("DChal3", false);
                user.put("DChal3Desc", "Run 3 miles");
                user.put("DChal4", false);
                user.put("DChal4Desc", "Do 30 squats");
                user.put("DChal5", false);
                user.put("DChal5Desc", "Do planks for 3 minutes");

                user.put("WChal1", false);
                user.put("WChal1Desc", "Do 150 push-ups");
                user.put("WChal2", false);
                user.put("WChal2Desc", "Do 150 squats");
                user.put("WChal3", false);
                user.put("WChal3Desc", "Do 100 sit-ups");
                user.put("WChal4", false);
                user.put("WChal4Desc", "Run 10 miles");
                user.put("WChal5", false);
                user.put("WChal5Desc", "Do 100 burpees");

                user.put("MChal1", false);
                user.put("MChal1Desc", "Do 400 dips");
                user.put("MChal2", false);
                user.put("MChal2Desc", "Do 400 squats");
                user.put("MChal3", false);
                user.put("MChal3Desc", "Do 400 pull-ups");
                user.put("MChal4", false);
                user.put("MChal4Desc", "Do 400 sit-ups");
                user.put("MChal5", false);
                user.put("MChal5Desc", "Run 40 miles");

                if(!StoreLocation.isEmpty())
                    user.put("Location", StoreLocation);
                else{
                    user.put("Location", "Unknown");
                }

                /// Check if username already exists
                CollectionReference usersRef = db.collection("users");
                Query query = usersRef.whereEqualTo("Username", userName);
                query.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot documentSnapshot : task.getResult()) {
                            String user1 = documentSnapshot.getString("Username");

                            if (user1.equals(userName)) {
                                Toast.makeText(CreateAccount.this, "Username already exists", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    if (task.getResult().size() == 0) {

                        db.collection("users").add(user)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {

                                        Toast.makeText(CreateAccount.this, "Account Successfully Created", Toast.LENGTH_SHORT).show();
                                        onBackPressed();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(CreateAccount.this, "Something went wrong while creating your account", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        });
    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(this, Login.class);
        this.startActivity(intent);

    }

    private void getLastLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            fusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                Geocoder geocoder = new Geocoder(CreateAccount.this, Locale.getDefault());
                                List<Address> addresses = null;
                                try {
                                    addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                                    String state = addresses.get(0).getAdminArea();
                                    String country = addresses.get(0).getCountryName();
                                    StoreLocation = state + ", " + country;
                                    Log.d("TAG", "Location Stored");

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }


                            }
                        }
                    });

        } else {

            requestPermission();

        }

    }

    private void requestPermission() {

        ActivityCompat.requestPermissions(CreateAccount.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == 100) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

}