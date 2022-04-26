package com.example.arisedeck;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
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


public class MonthlyFragment extends Fragment {

    ProgressBar[] cPBars = new ProgressBar[5];
    TextView[] chal = new TextView[5];
    Button[] cBtns = new Button[5];

    FirebaseFirestore db;

    public MonthlyFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_challenge, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String Username = this.getArguments().getString("username");
        final int GAINED_XP = 1500;

        chal[0] = (TextView) view.findViewById(R.id.Challenge1);
        chal[1] = (TextView) view.findViewById(R.id.Challenge2);
        chal[2] = (TextView) view.findViewById(R.id.Challenge3);
        chal[3] = (TextView) view.findViewById(R.id.Challenge4);
        chal[4] = (TextView) view.findViewById(R.id.Challenge5);

        cPBars[0] = (ProgressBar) view.findViewById(R.id.challenge1PB);
        cPBars[1] = (ProgressBar) view.findViewById(R.id.challenge2PB);
        cPBars[2] = (ProgressBar) view.findViewById(R.id.challenge3PB);
        cPBars[3] = (ProgressBar) view.findViewById(R.id.challenge4PB);
        cPBars[4] = (ProgressBar) view.findViewById(R.id.challenge5PB);

        cBtns[0] = (Button) view.findViewById(R.id.Complete1);
        cBtns[1] = (Button) view.findViewById(R.id.Complete2);
        cBtns[2] = (Button) view.findViewById(R.id.Complete3);
        cBtns[3] = (Button) view.findViewById(R.id.Complete4);
        cBtns[4] = (Button) view.findViewById(R.id.Complete5);

        db = FirebaseFirestore.getInstance();

        // Set up Challenges & buttons & Bars
        db.collection("users").whereEqualTo("Username", Username)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if(task.isSuccessful() && !task.getResult().isEmpty()){
                    for (DocumentSnapshot document : task.getResult()) {

                        String description1 = document.getString("MChal1Desc");
                        chal[0].setText(description1);

                        String description2 = document.getString("MChal2Desc");
                        chal[1].setText(description2);

                        String description3 = document.getString("MChal3Desc");
                        chal[2].setText(description3);

                        String description4 = document.getString("MChal4Desc");
                        chal[3].setText(description4);

                        String description5 = document.getString("MChal5Desc");
                        chal[4].setText(description5);

                        Boolean alreadyDone1 = document.getBoolean("MChal1");

                        if(alreadyDone1) {
                            cPBars[0].setProgress(100);
                            cBtns[0].setEnabled(false);
                        }

                        Boolean alreadyDone2 = document.getBoolean("MChal2");

                        if(alreadyDone2) {
                            cPBars[1].setProgress(100);
                            cBtns[1].setEnabled(false);
                        }

                        Boolean alreadyDone3 = document.getBoolean("MChal3");

                        if(alreadyDone3) {
                            cPBars[2].setProgress(100);
                            cBtns[2].setEnabled(false);
                        }

                        Boolean alreadyDone4 = document.getBoolean("MChal4");

                        if(alreadyDone4) {
                            cPBars[3].setProgress(100);
                            cBtns[3].setEnabled(false);
                        }

                        Boolean alreadyDone5 = document.getBoolean("MChal5");

                        if(alreadyDone5) {
                            cPBars[4].setProgress(100);
                            cBtns[4].setEnabled(false);
                        }
                    }
                }else{
                    Toast.makeText(getActivity(), "Error occurred while updating your profile", Toast.LENGTH_SHORT).show();
                }
            }
        });

        cBtns[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                cBtns[0].setEnabled(false);
                cPBars[0].setProgress(100);

                // Get info
                CollectionReference usersRef = db.collection("users");
                Query query = usersRef.whereEqualTo("Username", Username);
                query.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                        String docID = documentSnapshot.getId();

                        for (DocumentSnapshot document : task.getResult()) {

                            Map<String, Object> user = new HashMap<>();

                            String Completed = document.getLong("Completed").toString();
                            String Rank = document.getLong("Rank").toString();
                            String XP = document.getLong("XP").toString();

                            // Do Calculations
                            int total_completed = Integer.parseInt(Completed) + 1;
                            int total_xp = Integer.parseInt(XP) + GAINED_XP;
                            int total_rank = Integer.parseInt(Rank);

                            if (total_xp >= 5000) {

                                total_xp -= 5000;
                                total_rank += 1;

                                Toast.makeText(getActivity(), "You've Ranked Up!", Toast.LENGTH_SHORT).show();
                                user.put("Rank", total_rank);
                            }

                            user.put("XP", total_xp);
                            user.put("Completed", total_completed);
                            user.put("MChal1", true);

                            db.collection("users").document(docID).update(user)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(getActivity(), "Challenge Completed", Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getActivity(), "Can not complete this request", Toast.LENGTH_SHORT).show();

                                }
                            });
                        }
                    }
                });
            }
        });

        cBtns[1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                cBtns[1].setEnabled(false);
                cPBars[1].setProgress(100);

                // Get info
                CollectionReference usersRef = db.collection("users");
                Query query = usersRef.whereEqualTo("Username", Username);
                query.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                        String docID = documentSnapshot.getId();

                        for (DocumentSnapshot document : task.getResult()) {

                            Map<String, Object> user = new HashMap<>();

                            String Completed = document.getLong("Completed").toString();
                            String Rank = document.getLong("Rank").toString();
                            String XP = document.getLong("XP").toString();

                            // Do Calculations
                            int total_completed = Integer.parseInt(Completed) + 1;
                            int total_xp = Integer.parseInt(XP) + GAINED_XP;
                            int total_rank = Integer.parseInt(Rank);

                            if (total_xp >= 5000) {

                                total_xp -= 5000;
                                total_rank += 1;

                                Toast.makeText(getActivity(), "You've Ranked Up!", Toast.LENGTH_SHORT).show();
                                user.put("Rank", total_rank);
                            }

                            user.put("XP", total_xp);
                            user.put("Completed", total_completed);
                            user.put("MChal2", true);

                            db.collection("users").document(docID).update(user)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(getActivity(), "Challenge Completed", Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getActivity(), "Can not complete this request", Toast.LENGTH_SHORT).show();

                                }
                            });
                        }
                    }
                });
            }
        });

        cBtns[2].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                cBtns[2].setEnabled(false);
                cPBars[2].setProgress(100);

                // Get info
                CollectionReference usersRef = db.collection("users");
                Query query = usersRef.whereEqualTo("Username", Username);
                query.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                        String docID = documentSnapshot.getId();

                        for (DocumentSnapshot document : task.getResult()) {

                            Map<String, Object> user = new HashMap<>();

                            String Completed = document.getLong("Completed").toString();
                            String Rank = document.getLong("Rank").toString();
                            String XP = document.getLong("XP").toString();

                            // Do Calculations
                            int total_completed = Integer.parseInt(Completed) + 1;
                            int total_xp = Integer.parseInt(XP) + GAINED_XP;
                            int total_rank = Integer.parseInt(Rank);

                            if (total_xp >= 5000) {

                                total_xp -= 5000;
                                total_rank += 1;

                                Toast.makeText(getActivity(), "You've Ranked Up!", Toast.LENGTH_SHORT).show();
                                user.put("Rank", total_rank);
                            }

                            user.put("XP", total_xp);
                            user.put("Completed", total_completed);
                            user.put("MChal3", true);

                            db.collection("users").document(docID).update(user)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(getActivity(), "Challenge Completed", Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getActivity(), "Can not complete this request", Toast.LENGTH_SHORT).show();

                                }
                            });
                        }
                    }
                });
            }
        });

        cBtns[3].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                cBtns[3].setEnabled(false);
                cPBars[3].setProgress(100);

                // Get info
                CollectionReference usersRef = db.collection("users");
                Query query = usersRef.whereEqualTo("Username", Username);
                query.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                        String docID = documentSnapshot.getId();

                        for (DocumentSnapshot document : task.getResult()) {

                            Map<String, Object> user = new HashMap<>();

                            String Completed = document.getLong("Completed").toString();
                            String Rank = document.getLong("Rank").toString();
                            String XP = document.getLong("XP").toString();

                            // Do Calculations
                            int total_completed = Integer.parseInt(Completed) + 1;
                            int total_xp = Integer.parseInt(XP) + GAINED_XP;
                            int total_rank = Integer.parseInt(Rank);

                            if (total_xp >= 5000) {

                                total_xp -= 5000;
                                total_rank += 1;

                                Toast.makeText(getActivity(), "You've Ranked Up!", Toast.LENGTH_SHORT).show();
                                user.put("Rank", total_rank);
                            }

                            user.put("XP", total_xp);
                            user.put("Completed", total_completed);
                            user.put("MChal4", true);

                            db.collection("users").document(docID).update(user)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(getActivity(), "Challenge Completed", Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getActivity(), "Can not complete this request", Toast.LENGTH_SHORT).show();

                                }
                            });
                        }
                    }
                });
            }
        });

        cBtns[4].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                cBtns[4].setEnabled(false);
                cPBars[4].setProgress(100);

                // Get info
                CollectionReference usersRef = db.collection("users");
                Query query = usersRef.whereEqualTo("Username", Username);
                query.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                        String docID = documentSnapshot.getId();

                        for (DocumentSnapshot document : task.getResult()) {

                            Map<String, Object> user = new HashMap<>();

                            String Completed = document.getLong("Completed").toString();
                            String Rank = document.getLong("Rank").toString();
                            String XP = document.getLong("XP").toString();

                            // Do Calculations
                            int total_completed = Integer.parseInt(Completed) + 1;
                            int total_xp = Integer.parseInt(XP) + GAINED_XP;
                            int total_rank = Integer.parseInt(Rank);

                            if (total_xp >= 5000) {

                                total_xp -= 5000;
                                total_rank += 1;

                                Toast.makeText(getActivity(), "You've Ranked Up!", Toast.LENGTH_SHORT).show();
                                user.put("Rank", total_rank);
                            }

                            user.put("XP", total_xp);
                            user.put("Completed", total_completed);
                            user.put("MChal5", true);

                            db.collection("users").document(docID).update(user)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(getActivity(), "Challenge Completed", Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getActivity(), "Can not complete this request", Toast.LENGTH_SHORT).show();

                                }
                            });
                        }
                    }
                });
            }
        });
    }
}