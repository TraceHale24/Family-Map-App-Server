package com.example.familymapapp.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toolbar;

import com.example.familymap.R;
import com.example.familymapapp.Extras.DataCache;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import Model.Event;
import Model.Person;

public class SearchActivity extends AppCompatActivity {

    SearchView searchView;

    DataCache data = DataCache.getInstance();

    RecyclerView mFamilyRV;
    RecyclerView mEventsRV;

    LinearLayoutManager mFamilyLM;
    LinearLayoutManager mEventLM;

    private ArrayList<Person> mPeople = new ArrayList<>();
    private ArrayList<Event> mEvents = new ArrayList<>();

    ImageView mImageView;

    FamilyItemAdapter mFamilyAdapter;
    ItemAdapter mEventsAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);



        searchView = (SearchView) findViewById(R.id.search_query_view);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                refreshAdapters(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                refreshAdapters(newText);
                return true;
            }
        });

        mFamilyRV = (RecyclerView) findViewById(R.id.search_recycler_view_people);
        mEventsRV = (RecyclerView) findViewById(R.id.search_recycler_view_events);

        mFamilyRV.setNestedScrollingEnabled(false);
        mEventsRV.setNestedScrollingEnabled(false);

        mFamilyLM = new LinearLayoutManager(getApplicationContext());
        mFamilyLM.setOrientation(LinearLayoutManager.VERTICAL);

        mEventLM = new LinearLayoutManager(getApplicationContext());
        mEventLM.setOrientation(LinearLayoutManager.VERTICAL);

        mFamilyRV.setLayoutManager(mFamilyLM);
        mEventsRV.setLayoutManager(mEventLM);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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


    private void refreshAdapters(String query) {
        getMatching(query);
        mFamilyAdapter = new FamilyItemAdapter(mPeople);
        mEventsAdapter = new ItemAdapter(mEvents);

        mFamilyRV.setAdapter(mFamilyAdapter);
        mEventsRV.setAdapter(mEventsAdapter);
    }

    private void getMatching(String query) {
        Map<String, Person> people = data.getFilteredPeople();
        ArrayList<Event> eventsList = data.getFilteredEvents();
        ArrayList<Event> events = new ArrayList<>();
        if(!data.isMaleEvents()) {
            for(Event temp : eventsList) {
                if(people.get(temp.getPersonID()).getGender().toLowerCase().equals("m")) {
                    System.out.println("Just for kicks");
                }
                else {
                    events.add(temp);
                }
            }
        }
        else {
            events = eventsList;
        }
        mPeople.clear();
        mEvents.clear();

        for(Person person:people.values()) {
            String string = person.getFirstName() + person.getLastName();
            if(string.toLowerCase().contains(query.toLowerCase())) {
                mPeople.add(person);
            }
        }
        for(Event e :events) {
            String key = e.getCountry() + e.getCity() + e.getEventType() + e.getYear();
            if(key.toLowerCase().contains(query.toLowerCase())) {
                mEvents.add(e);
            }
        }
    }


    private class FamilyItemAdapter extends RecyclerView.Adapter<FamilyItemAdapter.FamilyListItemViewHolder> {
        private ArrayList<Person> matchingPeople;

        public FamilyItemAdapter(ArrayList<Person> matchingPeople) {
            this.matchingPeople = matchingPeople;
        }

        @NonNull
        @Override
        public FamilyItemAdapter.FamilyListItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            final View itemView = LayoutInflater.from(
                    parent.getContext()).inflate(R.layout.search_people_holder, parent, false);

            return new FamilyListItemViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull FamilyItemAdapter.FamilyListItemViewHolder holder, int position) {
            Person person = matchingPeople.get(position);


            holder.nameText.setText(person.toString());

            if (person.getGender().toLowerCase().equals("m")) {
                Drawable male = new IconDrawable(SearchActivity.this, FontAwesomeIcons.fa_male).colorRes(R.color.maleColor).sizeDp(20);
                holder.icon.setImageDrawable(male);
            } else {
                Drawable female = new IconDrawable(SearchActivity.this, FontAwesomeIcons.fa_female).colorRes(R.color.femaleColor).sizeDp(20);
                holder.icon.setImageDrawable(female);
            }
        }

        @Override
        public int getItemCount() {
            return matchingPeople.size();
        }


        public class FamilyListItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            public ImageView icon;
            public TextView nameText;

            public FamilyListItemViewHolder(View itemView) {
                super(itemView);

                icon = (ImageView) itemView.findViewById(R.id.search_person_icon);
                nameText = (TextView) itemView.findViewById(R.id.search_person_name);

                itemView.setOnClickListener(this);
            }

            /**
             * Called when a view has been clicked.
             *
             * @param v The view that was clicked.
             */
            @Override
            public void onClick(View v) {
                Person clickedPerson = matchingPeople.get(getAdapterPosition());
                Intent intent = new Intent(SearchActivity.this, PersonActivity.class);
                intent.putExtra("PersonID", clickedPerson.getPersonID());
                startActivity(intent);
            }
        }
    }

    private class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ListItemViewHolder> {
        private ArrayList<Event> events = new ArrayList<>();

        public ItemAdapter(ArrayList<Event> events) {
            this.events = events;
        }

        @NonNull
        @Override
        public ItemAdapter.ListItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            final View itemView = LayoutInflater.from(
                    parent.getContext()).inflate(R.layout.search_event_holder, parent, false);

            return new ListItemViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull ItemAdapter.ListItemViewHolder holder, int position) {
            Event event = events.get(position);
            Person person = data.getPeople().get(event.getPersonID());

            Drawable icon;

            if (event.getEventType().toLowerCase().equals("birth")) {
                icon = new IconDrawable(SearchActivity.this, FontAwesomeIcons.fa_map_marker).colorRes(R.color.androidColor).sizeDp(20);
            } else if (event.getEventType().toLowerCase().equals("death")) {
                icon = new IconDrawable(SearchActivity.this, FontAwesomeIcons.fa_map_marker).colorRes(R.color.deathColor).sizeDp(20);
            } else if (event.getEventType().toLowerCase().equals("marriage")) {
                icon = new IconDrawable(SearchActivity.this, FontAwesomeIcons.fa_map_marker).colorRes(R.color.maleColor).sizeDp(20);
            } else {
                icon = new IconDrawable(SearchActivity.this, FontAwesomeIcons.fa_map_marker).colorRes(R.color.colorPrimary).sizeDp(20);
            }

            holder.icon.setImageDrawable(icon);

            String topText = event.toString();
            holder.topText.setText(topText);

            holder.bottomText.setText(person.toString());
        }

        @Override
        public int getItemCount() {
            return events.size();
        }

        public class ListItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            public ImageView icon;
            public TextView topText;
            public TextView bottomText;

            public ListItemViewHolder(View itemView) {
                super(itemView);

                icon = (ImageView) itemView.findViewById(R.id.search_event_icon);
                topText = (TextView) itemView.findViewById(R.id.search_event_info);
                bottomText = (TextView) itemView.findViewById(R.id.search_event_owner_name);

                itemView.setOnClickListener(this);
            }

            /**
             * Called when a view has been clicked.
             *
             * @param v The view that was clicked.
             */
            @Override
            public void onClick(View v) {
                Event clickedEvent = events.get(getAdapterPosition());
                //Model model = Model.getModel();
                //model.setSelectedEvent(clickedEvent);

                Intent intent = new Intent(SearchActivity.this, EventActivity.class);
                intent.putExtra("EventID", clickedEvent.getEventName());
                startActivity(intent);
            }
        }
    }
}
