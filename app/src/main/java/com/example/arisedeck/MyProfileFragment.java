package com.example.arisedeck;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class MyProfileFragment extends Fragment {

    EditText FirstName, LastName, Username, Email, Password;
    TextView Rank;
    FirebaseFirestore db;
    Button UpdateBtn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_profile, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String SUsername = this.getArguments().getString("Username");

        FirstName = (EditText) view.findViewById(R.id.firstName);
        LastName = (EditText) view.findViewById(R.id.lastName);
        Username = (EditText) view.findViewById(R.id.username);
        Username.setEnabled(false);
        Rank = (TextView) view.findViewById(R.id.rank);

        Email = (EditText) view.findViewById(R.id.email);
        Password = (EditText) view.findViewById(R.id.password);
        UpdateBtn = (Button) view.findViewById(R.id.update);

        db = FirebaseFirestore.getInstance();

        CollectionReference usersRef = db.collection("users");
        Query query = usersRef.whereEqualTo("Username", SUsername);
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DocumentSnapshot document : task.getResult()) {

                    String fName = document.getString("First Name");
                    String lName = document.getString("Last Name");
                    String userName = (String) document.getString("Username");
                    String email = document.getString("Email");
                    String rank = document.getLong("Rank").toString();
                    String password = document.getLong("Rank").toString();

                    FirstName.setText(fName);
                    LastName.setText(lName);
                    Username.setText(userName);
                    Email.setText(email);
                    Rank.setText(rank);
                    Password.setText(password);
                }
            }
        });

        UpdateBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {

                String fName = FirstName.getText().toString();
                String lName = LastName.getText().toString();
                String userName = Username.getText().toString();
                String email = Email.getText().toString();
                String password = Password.getText().toString();

                Map<String, Object> user = new HashMap<>();
                user.put("First Name", fName);
                user.put("Last Name", lName);
                user.put("Username", userName);
                user.put("Email", email);
                user.put("Password", password);

                db.collection("users").whereEqualTo("Username", SUsername)
                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if(task.isSuccessful() && !task.getResult().isEmpty()){

                            DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                            String docID = documentSnapshot.getId();

                            db.collection("users").document(docID).update(user)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(getActivity(), "Profile Information updated", Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getActivity(), "Something went wrong while updating your profile", Toast.LENGTH_SHORT).show();

                                }
                            });

                        }else{
                            Toast.makeText(getActivity(), "Error occurred while updating your profile", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}