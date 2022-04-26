package com.example.arisedeck;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;


public class SocialFragment extends Fragment {

    FirebaseFirestore db;
    TextView[] Ranks = new TextView[10];
    TextView[] Usernames = new TextView[10];
    TextView[] Locations = new TextView[10];

    public SocialFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_social, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();

        Ranks[0] = (TextView) view.findViewById(R.id.rank1);
        Ranks[1] = (TextView) view.findViewById(R.id.rank2);
        Ranks[2] = (TextView) view.findViewById(R.id.rank3);
        Ranks[3] = (TextView) view.findViewById(R.id.rank4);
        Ranks[4] = (TextView) view.findViewById(R.id.rank5);
        Ranks[5] = (TextView) view.findViewById(R.id.rank6);
        Ranks[6] = (TextView) view.findViewById(R.id.rank7);
        Ranks[7] = (TextView) view.findViewById(R.id.rank8);
        Ranks[8] = (TextView) view.findViewById(R.id.rank9);
        Ranks[9] = (TextView) view.findViewById(R.id.rank10);

        Usernames[0] = (TextView) view.findViewById(R.id.username1);
        Usernames[1] = (TextView) view.findViewById(R.id.username2);
        Usernames[2] = (TextView) view.findViewById(R.id.username3);
        Usernames[3] = (TextView) view.findViewById(R.id.username4);
        Usernames[4] = (TextView) view.findViewById(R.id.username5);
        Usernames[5] = (TextView) view.findViewById(R.id.username6);
        Usernames[6] = (TextView) view.findViewById(R.id.username7);
        Usernames[7] = (TextView) view.findViewById(R.id.username8);
        Usernames[8] = (TextView) view.findViewById(R.id.username9);
        Usernames[9] = (TextView) view.findViewById(R.id.username10);

        Locations[0] = (TextView) view.findViewById(R.id.completed1);
        Locations[1] = (TextView) view.findViewById(R.id.completed2);
        Locations[2] = (TextView) view.findViewById(R.id.completed3);
        Locations[3] = (TextView) view.findViewById(R.id.completed4);
        Locations[4] = (TextView) view.findViewById(R.id.completed5);
        Locations[5] = (TextView) view.findViewById(R.id.completed6);
        Locations[6] = (TextView) view.findViewById(R.id.completed7);
        Locations[7] = (TextView) view.findViewById(R.id.completed8);
        Locations[8] = (TextView) view.findViewById(R.id.completed9);
        Locations[9] = (TextView) view.findViewById(R.id.completed10);

        CollectionReference usersRef = db.collection("users");
        Query query = usersRef.orderBy("Rank", Query.Direction.DESCENDING).limit(10);
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && !task.getResult().isEmpty()){

                int counter = 0;
                for (DocumentSnapshot document : task.getResult()) {

                    String CCompleted = document.getString("Location").toString();
                    String userName = (String) document.getString("Username");
                    String Rank = document.getLong("Rank").toString();

                    Ranks[counter].setText(Rank);
                    Usernames[counter].setText(userName);
                    Locations[counter].setText(CCompleted);

                    counter++;
                }
            }
        });
    }
}