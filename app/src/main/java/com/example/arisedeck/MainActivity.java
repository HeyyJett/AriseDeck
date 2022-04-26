package com.example.arisedeck;

import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;


public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    Boolean LoggedIn;

    HomeFragment homeFragment = new HomeFragment();
    ChallengeFragment challengeFragment = new ChallengeFragment();
    SocialFragment socialFragment = new SocialFragment();
    UserFragment userFragment = new UserFragment();

    public void daily(View view){

        String username = getIntent().getStringExtra("username");
        ChallengeFragment challFragment = new ChallengeFragment();

        Bundle bundle = new Bundle();
        bundle.putString("username", username);
        challFragment.setArguments(bundle);

        this.getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, challFragment)
                .addToBackStack(null)
                .commit();
    }

    public void weekly(View view){

            String username = getIntent().getStringExtra("username");
            WeeklyFragment weeklyFragment= new WeeklyFragment();

            Bundle bundle = new Bundle();
            bundle.putString("username", username);
            weeklyFragment.setArguments(bundle);

            this.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, weeklyFragment)
                    .addToBackStack(null)
                    .commit();
    }

    public void monthly(View view){

        String username = getIntent().getStringExtra("username");
        MonthlyFragment monthlyFragment= new MonthlyFragment();

        Bundle bundle = new Bundle();
        bundle.putString("username", username);
        monthlyFragment.setArguments(bundle);

        this.getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, monthlyFragment)
                .addToBackStack(null)
                .commit();
    }


    // Need to check if user logged in and save it
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean("loggedIn", LoggedIn);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(savedInstanceState != null)
            LoggedIn = savedInstanceState.getBoolean("loggedIn");
        else
            LoggedIn = getIntent().getBooleanExtra("loggedIn", false);

        // Take user to login screen.
        if(!LoggedIn) {
            Intent intent = new Intent(this, Login.class);
            this.startActivity(intent);
        }

        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        getSupportFragmentManager().beginTransaction().replace(R.id.container, homeFragment).commit();

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected( MenuItem item) {

                String username = getIntent().getStringExtra("username");
                Bundle bundle = new Bundle();

                switch (item.getItemId()){
                    case R.id.homeFragment:

                        bundle.putString("username", username);
                        homeFragment.setArguments(bundle);

                        getSupportFragmentManager().beginTransaction().replace(R.id.container, homeFragment).commit();
                        return true;

                    case R.id.challengeFragment:


                        bundle.putString("username", username);
                        challengeFragment.setArguments(bundle);

                        getSupportFragmentManager().beginTransaction().replace(R.id.container, challengeFragment).commit();
                        return true;

                    case R.id.socialFragment:

                        bundle.putString("username", username);
                        socialFragment.setArguments(bundle);

                        getSupportFragmentManager().beginTransaction().replace(R.id.container, socialFragment).commit();
                        return true;

                    case R.id.userFragment:

                        bundle.putString("username", username);
                        userFragment.setArguments(bundle);

                        getSupportFragmentManager().beginTransaction().replace(R.id.container, userFragment).commit();
                        return true;
                }

                return false;
            }
        });
    }
}