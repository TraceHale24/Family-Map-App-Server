package com.example.familymapapp.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.familymap.R;
import com.example.familymapapp.Extras.DataCache;

import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import Model.Event;
import Model.Person;

public class PersonActivity extends AppCompatActivity {

    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, ArrayList<Event>> listDataChild;
    HashMap<String, Person> listDataPersonChild;
    DataCache cache = null;
    String personID;
    ArrayList<Event> events = new ArrayList<>();
    ArrayList<Person> people = new ArrayList<>();
    Person person;
    ImageView mGenderImageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);
        Intent intent = getIntent();
        // get the listview
        expListView = (ExpandableListView) findViewById(R.id.lvExp);
        // preparing list data
        // setting list adapter
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        personID = intent.getExtras().getString("PersonID");

        prepareListData();

        TextView firstName = (TextView) findViewById(R.id.firstNamePlaceHolder);
        firstName.setText(person.getFirstName());
        TextView lastName = (TextView) findViewById(R.id.lastNamePlaceHolder);
        lastName.setText(person.getLastName());
        TextView gender = (TextView) findViewById(R.id.genderPlaceHolder);
        if (person.getGender().toLowerCase().equals("m")) {
            gender.setText("Male");

        } else {
            gender.setText("Female");
        }

        listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild, listDataPersonChild);
        expListView.setAdapter(listAdapter);


        // Listview on child click listener
        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                if (groupPosition == 0) {

                    Intent i = new Intent(PersonActivity.this, EventActivity.class);
                    Event eventToPass = (Event) listAdapter.getChild(groupPosition, childPosition);
                    i.putExtra("EventID", eventToPass.getEventName());
                    startActivity(i);
                } else {
                    Map<String, Person> map = (Map<String, Person>) listAdapter.getChild(groupPosition, childPosition);
                    for (String temp : map.keySet()) {
                        Intent i = new Intent(PersonActivity.this, PersonActivity.class);
                        i.putExtra("PersonID", map.get(temp).getPersonID());
                        startActivity(i);
                    }

                }
                return false;
            }
        });
    }

    /*
     * Preparing the list data
     */
    private void prepareListData() {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, ArrayList<Event>>();
        listDataPersonChild = new HashMap<>();


        // Adding child data
        listDataHeader.add("LIFE EVENTS");
        listDataHeader.add("FAMILY");

        cache = DataCache.getInstance();
        person = cache.getPeople().get(personID);
        if (person.getFatherID() != null) {
            listDataPersonChild.put("Dad", cache.getPeople().get(person.getFatherID()));
        }
        if (person.getMotherID() != null) {
            listDataPersonChild.put("Mom", cache.getPeople().get(person.getMotherID()));
        }
        if (person.getSpouseID() != null) {
            listDataPersonChild.put("Spouse", cache.getPeople().get(person.getSpouseID()));
        }
        if (person.getGender().toLowerCase().equals("m")) {
            for (Person temp : cache.getPeople().values()) {
                if (personID.equals(temp.getFatherID())) {
                    listDataPersonChild.put("Child", temp);
                    break;
                }
            }
        } else if (person.getGender().toLowerCase().equals("f")) {
            for (Person temp : cache.getPeople().values()) {
                if (personID.equals(temp.getMotherID())) {
                    listDataPersonChild.put("Child", temp);
                    break;
                }
            }
        }

        events = cache.getPersonToEvent().get(personID);
        // Adding child data
        if(!cache.isMaleEvents() && person.getGender().toLowerCase().equals("m")) {
            events.clear();
        }
        if(!cache.isFemaleEvents() && person.getGender().toLowerCase().equals("f")) {
            events.clear();
        }
        if(!events.isEmpty()) {
            Collections.sort(events);
        }


        listDataChild.put(listDataHeader.get(0), events);
        // Header, Child data
        //listDataChild.put(listDataHeader.get(1), families);
    }


    public class ExpandableListAdapter extends BaseExpandableListAdapter {

        private Context _context;
        private List<String> _listDataHeader; // header titles
        // child data in format of header title, child title
        private HashMap<String, ArrayList<Event>> _listDataChild;
        private HashMap<String, Person> _listChildPersonData;

        public ExpandableListAdapter(Context context, List<String> listDataHeader,
                                     HashMap<String, ArrayList<Event>> listChildData, HashMap<String, Person> listChildPersonData) {
            this._context = context;
            this._listDataHeader = listDataHeader;
            this._listDataChild = listChildData;
            this._listChildPersonData = listChildPersonData;
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            if (groupPosition == 0) {
                return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                        .get(childPosition);
            } else {
                int i = 0;
                for (String temp : _listChildPersonData.keySet()) {
                    if (i == childPosition) {
                        Map<String, Person> returnThis = new TreeMap<>();
                        returnThis.put(temp, this._listChildPersonData.get(temp));
                        return returnThis;
                    }
                    i++;
                }
            }
            return null;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public View getChildView(int groupPosition, final int childPosition,
                                 boolean isLastChild, View convertView, ViewGroup parent) {

            String childText = null;
            Drawable icon = null;

            if (groupPosition == 0) {
                Event event = (Event) getChild(groupPosition, childPosition);
                childText = getChild(groupPosition, childPosition).toString();

                if (event.getEventType().toLowerCase().equals("birth")) {
                    icon = new IconDrawable(PersonActivity.this, FontAwesomeIcons.fa_map_marker).colorRes(R.color.androidColor).sizeDp(35);
                } else if (event.getEventType().toLowerCase().equals("death")) {
                    icon = new IconDrawable(PersonActivity.this, FontAwesomeIcons.fa_map_marker).colorRes(R.color.deathColor).sizeDp(35);
                } else if (event.getEventType().toLowerCase().equals("marriage")) {
                    icon = new IconDrawable(PersonActivity.this, FontAwesomeIcons.fa_map_marker).colorRes(R.color.maleColor).sizeDp(35);
                } else {
                    icon = new IconDrawable(PersonActivity.this, FontAwesomeIcons.fa_map_marker).colorRes(R.color.colorPrimary).sizeDp(35);
                }

            } else {
                Map<String, Person> map = (Map<String, Person>) getChild(groupPosition, childPosition);
                Person person = null;
                for (String temp : map.keySet()) {
                    childText = map.get(temp).toString() + "\n" + temp;
                    person = map.get(temp);
                }

                if (person.getGender().toLowerCase().equals("m")) {
                    icon = new IconDrawable(PersonActivity.this, FontAwesomeIcons.fa_male).colorRes(R.color.maleColor).sizeDp(35);
                } else {
                    icon = new IconDrawable(PersonActivity.this, FontAwesomeIcons.fa_female).colorRes(R.color.femaleColor).sizeDp(35);
                }
            }


            if (convertView == null) {
                LayoutInflater infalInflater = (LayoutInflater) this._context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = infalInflater.inflate(R.layout.list_item, null);
            }

            TextView txtListChild = (TextView) convertView
                    .findViewById(R.id.lblListItem);

            //icon = new IconDrawable(PersonActivity.this, FontAwesomeIcons.fa_map_marker).colorRes(R.color.androidColor).sizeDp(20);
            ImageView mIcon = (ImageView) convertView.findViewById(R.id.icon);
            mIcon.setImageDrawable(icon);

            txtListChild.setText(childText);
            return convertView;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            if (groupPosition == 0) {
                return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                        .size();
            } else {
                return this._listChildPersonData.size();
            }

        }

        @Override
        public Object getGroup(int groupPosition) {
            if (groupPosition == 0) {
                return this._listDataHeader.get(groupPosition);
            } else {
                return this._listChildPersonData;
            }
        }

        @Override
        public int getGroupCount() {
            return this._listDataHeader.size();
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded,
                                 View convertView, ViewGroup parent) {
            String headerTitle = null;
            if (groupPosition == 0) {
                headerTitle = (String) getGroup(groupPosition);
            } else {
                headerTitle = "FAMILY";
            }
            if (convertView == null) {
                LayoutInflater infalInflater = (LayoutInflater) this._context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = infalInflater.inflate(R.layout.list_group, null);
            }

            TextView lblListHeader = (TextView) convertView
                    .findViewById(R.id.lblListHeader);
            lblListHeader.setTypeface(null, Typeface.BOLD);
            lblListHeader.setText(headerTitle);

            return convertView;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
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
}
