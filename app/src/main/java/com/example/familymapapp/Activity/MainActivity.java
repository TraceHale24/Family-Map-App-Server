package com.example.familymapapp.Activity;
import com.example.familymap.R;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;

import com.example.familymapapp.Fragment.LoginFragment;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Iconify.with(new FontAwesomeModule());

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        ft.add(R.id.container, new LoginFragment());
        ft.commit();

    }

    public void startPersonActivity(String personID) {
        Intent i = new Intent(this, PersonActivity.class);
        i.putExtra("PersonID", personID);
        startActivity(i);
    }

    @Override
    public void onBackPressed() {
        //do nothing
    }
}
