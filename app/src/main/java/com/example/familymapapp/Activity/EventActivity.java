package com.example.familymapapp.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;

import com.example.familymap.R;
import com.example.familymapapp.Extras.DataCache;
import com.example.familymapapp.Fragment.MapFragment;

import java.util.ArrayList;

import Model.Event;

public class EventActivity extends AppCompatActivity {
    DataCache dataCache = DataCache.getInstance();
    private Event event;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        Intent i = getIntent();
        ArrayList<Event> events = dataCache.getEvents();
        String eventID = i.getExtras().getString("EventID");

        for(Event temp: events) {
            if(temp.getEventName().equals(eventID)) {
                event = temp;
                break;
            }
        }
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        MapFragment mapFragment = new MapFragment();
        mapFragment.setFromMain(false);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.event_container, mapFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void startPersonActivity(String personID) {
        Intent i = new Intent(this, PersonActivity.class);
        i.putExtra("PersonID", personID);
        startActivity(i);
    }
}
