 package com.example.arisedeck;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

 public class Login extends AppCompatActivity {

     FirebaseFirestore db;
     TextView Username;
     TextView Password;

     // Need to store what user already put in the boxes
     @Override
     public void onSaveInstanceState(Bundle outState) {
         super.onSaveInstanceState(outState);

         String username = ((EditText) findViewById(R.id.username)).getText().toString();
         String password = ((EditText) findViewById(R.id.password)).getText().toString();

         outState.putString("username", username);
         outState.putString("password", password);
     }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Username = (TextView) findViewById(R.id.username);
        Password = (TextView) findViewById(R.id.password);

        if (savedInstanceState != null) {
            Username.setText(savedInstanceState.getString("username"));
            Password.setText(savedInstanceState.getString("password"));
        }
    }

     @Override
     public void onBackPressed() {

        // Do nothing
     }

    public void login(View v){

        db = FirebaseFirestore.getInstance();

        String SUsername = this.Username.getText().toString();
        String SPassword = this.Password.getText().toString();

        // Check if username and password exist in the database

        CollectionReference usersRef = db.collection("users");
        Query query = usersRef.whereEqualTo("Username", SUsername).whereEqualTo("Password", SPassword);
        query.get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                for(DocumentSnapshot documentSnapshot : task.getResult()){
                    String user1 = documentSnapshot.getString("Username");
                    String password1 = documentSnapshot.getString("Password");

                    if(user1.equals(SUsername) && password1.equals(SPassword)){
                        Toast.makeText(Login.this, "Welcome, " + SUsername, Toast.LENGTH_SHORT).show();
                        StartApp(this.Username.getText().toString());
                    }
                }
            }

            if(task.getResult().size() == 0 ){

                Toast.makeText(Login.this, "Invalid username or password", Toast.LENGTH_SHORT).show();
            }
        });
    }

     public void createAccount(View v) {

         Intent intent = new Intent(this, CreateAccount.class);
         this.startActivity(intent);

     }

     private void StartApp(String username){
         Intent intent = new Intent(this, MainActivity.class);
         intent.putExtra("username", username);
         intent.putExtra("loggedIn", true);
         this.startActivity(intent);
     }



}