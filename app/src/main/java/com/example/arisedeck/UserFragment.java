package com.example.arisedeck;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;


public class UserFragment extends Fragment {

    TextView rank, FullName, username;
    ProgressBar pb;
    FirebaseFirestore db;
    Button myProfileBtn; //settingsBtn, helpBtn;

    public UserFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_user, container, false);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String Username = this.getArguments().getString("username");

        FullName = (TextView) view.findViewById(R.id.fullname);
        username = (TextView) view.findViewById(R.id.username);
        rank = (TextView) view.findViewById(R.id.rank);
        pb = (ProgressBar) view.findViewById(R.id.xpb);

        myProfileBtn = (Button) view.findViewById(R.id.myProfile);
        //settingsBtn = (Button) view.findViewById(R.id.settings);
        //helpBtn = (Button) view.findViewById(R.id.help);

        db = FirebaseFirestore.getInstance();

        myProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                MyProfileFragment myProfileFragment= new MyProfileFragment();
                Bundle bundle = new Bundle();
                bundle.putString("Username", Username);
                myProfileFragment.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, myProfileFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        CollectionReference usersRef = db.collection("users");
        Query query = usersRef.whereEqualTo("Username", Username);
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DocumentSnapshot document : task.getResult()) {

                    String fullName = document.getString("First Name") + " " + document.getString("Last Name");
                    String userName = (String) document.getString("Username");
                    String Rank = document.getLong("Rank").toString();
                    String xp = document.getLong("XP").toString();

                    FullName.setText(fullName);
                    username.setText(userName);
                    rank.setText(Rank);

                    int temp = Integer.parseInt(xp);
                    int progress = temp * 100 / 5000;
                    pb.setProgress(progress);
                }
            }
        });
    }
}