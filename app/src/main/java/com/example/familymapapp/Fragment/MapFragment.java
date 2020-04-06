package com.example.familymapapp.Fragment;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.fonts.Font;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toolbar;

import com.example.familymap.R;
import com.example.familymapapp.Activity.EventActivity;
import com.example.familymapapp.Activity.MainActivity;
import com.example.familymapapp.Activity.SearchActivity;
import com.example.familymapapp.Activity.SettingsActivity;
import com.example.familymapapp.Extras.DataCache;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import Model.Event;
import Model.Person;


public class MapFragment extends SupportMapFragment implements OnMapReadyCallback {
    private GoogleMap mMap;
    private MapView mapView;
    private DataCache dataCache = DataCache.getInstance();
    private String personID;
    private boolean fromMain = true;
    private ImageView mGenderImageView;
    private LinearLayout mInfoWindow;
    private TextView mWindowText;
    private ArrayList<Polyline> polyLines = new ArrayList<>();
    private Polyline line = null;
    private ArrayList<Marker> markers = new ArrayList<>();
    private ArrayList<Float> colors = new ArrayList();
    private int colorCounter = 0;
    private Map<String, Float> typeToColor = new TreeMap<>();

    public static MapFragment newInstance() {
        return new MapFragment();
    }

    public boolean isFromMain() {
        return fromMain;
    }

    public void setFromMain(boolean fromMain) {
        this.fromMain = fromMain;
    }

    public void setColors() {
        colors.add(30.0f);
        colors.add(60.0f);
        colors.add(180.0f);
        colors.add(210.0f);
        colors.add(270.0f);
        colors.add(300.0f);
        colors.add(330.0f);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        getMapAsync(this);
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        super.onCreateView(layoutInflater, viewGroup, bundle);
        View v = layoutInflater.inflate(R.layout.fragment_map, viewGroup, false);

        mapView = v.findViewById(R.id.mapview);
        mapView.onCreate(bundle);
        mapView.getMapAsync(MapFragment.this);

        mGenderImageView = (ImageView) v.findViewById(R.id.mapGenderIcon);
        Drawable androidGenderIcon = new IconDrawable(getActivity(), FontAwesomeIcons.fa_android).colorRes(R.color.androidColor).sizeDp(40);
        mGenderImageView.setImageDrawable(androidGenderIcon);
        mInfoWindow = v.findViewById(R.id.infoWindow);
        mWindowText = v.findViewById(R.id.infoWindowText);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mMap != null) {
            for(Marker marker:markers) {
                marker.remove();
            }
            markers.clear();

            for(Polyline lines:polyLines) {
                lines.remove();
            }
            polyLines.clear();
            setColors();
            //redraw everything
            final ArrayList<Event> eventsList = dataCache.getFilteredEvents();
            final Map<String, Person> people = dataCache.getPeople();

            ArrayList<Event> events = new ArrayList<>();
            if(!dataCache.isMaleEvents()) {
                for(Event temp:eventsList) {
                    String test = temp.getPersonID();
                    if(people.get(temp.getPersonID()).getGender().toLowerCase().equals("m")) {
                        System.out.println("Just for testing");
                    }
                    else {
                        events.add(temp);
                    }
                }
            }
            else {
                events = eventsList;
            }


            for (int i = 0; i < events.size(); i++) {
                Marker mEvent = null;
                if (events.get(i).getEventType().toLowerCase().equals("birth")) {
                    LatLng eventPos = new LatLng(events.get(i).getLatitude(), events.get(i).getLongitude());
                    mEvent = mMap.addMarker(new MarkerOptions()
                            .position(eventPos)
                            .snippet(people.get(events.get(i).getPersonID()).getFirstName() + " " + people.get(events.get(i).getPersonID()).getLastName() + "\n" + "BIRTH: " + events.get(i).getCity() + ", " + events.get(i).getCountry() + " (" + events.get(i).getYear() + ")")
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                } else if (events.get(i).getEventType().toLowerCase().equals("death")) {
                    LatLng eventPos = new LatLng(events.get(i).getLatitude(), events.get(i).getLongitude());
                    mEvent = mMap.addMarker(new MarkerOptions()
                            .position(eventPos)
                            .snippet(people.get(events.get(i).getPersonID()).getFirstName() + " " + people.get(events.get(i).getPersonID()).getLastName() + "\n" + "DEATH: " + events.get(i).getCity() + ", " + events.get(i).getCountry() + " (" + events.get(i).getYear() + ")")
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                } else if (events.get(i).getEventType().toLowerCase().equals("marriage")) {
                    LatLng eventPos = new LatLng(events.get(i).getLatitude(), events.get(i).getLongitude());
                    mEvent = mMap.addMarker(new MarkerOptions()
                            .position(eventPos)
                            .snippet(people.get(events.get(i).getPersonID()).getFirstName() + " " + people.get(events.get(i).getPersonID()).getLastName() + "\n" + "MARRIAGE: " + events.get(i).getCity() + ", " + events.get(i).getCountry() + " (" + events.get(i).getYear() + ")")
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                } else if(typeToColor.get(events.get(i).getEventType().toLowerCase()) == null) {
                    typeToColor.put(events.get(i).getEventType().toLowerCase(), colors.get(colorCounter));
                    colorCounter++;
                    LatLng eventPos = new LatLng(events.get(i).getLatitude(), events.get(i).getLongitude());
                    mEvent = mMap.addMarker(new MarkerOptions()
                            .position(eventPos)
                            .snippet(people.get(events.get(i).getPersonID()).getFirstName() + " " + people.get(events.get(i).getPersonID()).getLastName() + "\n" + events.get(i).getEventType().toUpperCase() + ": " + events.get(i).getCity() + ", " + events.get(i).getCountry() + " (" + events.get(i).getYear() + ")")
                            .icon(BitmapDescriptorFactory.defaultMarker(typeToColor.get(events.get(i).getEventType().toLowerCase()))));
                } else if(typeToColor.get(events.get(i).getEventType().toLowerCase()) != null) {
                    LatLng eventPos = new LatLng(events.get(i).getLatitude(), events.get(i).getLongitude());
                    mEvent = mMap.addMarker(new MarkerOptions()
                            .position(eventPos)
                            .snippet(people.get(events.get(i).getPersonID()).getFirstName() + " " + people.get(events.get(i).getPersonID()).getLastName() + "\n" + events.get(i).getEventType().toUpperCase() + ": " + events.get(i).getCity() + ", " + events.get(i).getCountry() + " (" + events.get(i).getYear() + ")")
                            .icon(BitmapDescriptorFactory.defaultMarker(typeToColor.get(events.get(i).getEventType().toLowerCase()))));
                }
                mEvent.setTag(events.get(i));
                markers.add(mEvent);
            }
        }
        else {
            mapView.onResume();
        }
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;
        final ArrayList<Event> events = dataCache.getFilteredEvents();
        final Map<String, Person> people = dataCache.getFilteredPeople();
        setColors();
        for (int i = 0; i < events.size(); i++) {
            Marker mEvent = null;
            if (events.get(i).getEventType().toLowerCase().equals("birth")) {
                LatLng eventPos = new LatLng(events.get(i).getLatitude(), events.get(i).getLongitude());
                mEvent = mMap.addMarker(new MarkerOptions()
                        .position(eventPos)
                        .snippet(people.get(events.get(i).getPersonID()).getFirstName() + " " + people.get(events.get(i).getPersonID()).getLastName() + "\n" + "BIRTH: " + events.get(i).getCity() + ", " + events.get(i).getCountry() + " (" + events.get(i).getYear() + ")")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
            } else if (events.get(i).getEventType().toLowerCase().equals("death")) {
                LatLng eventPos = new LatLng(events.get(i).getLatitude(), events.get(i).getLongitude());
                mEvent = mMap.addMarker(new MarkerOptions()
                        .position(eventPos)
                        .snippet(people.get(events.get(i).getPersonID()).getFirstName() + " " + people.get(events.get(i).getPersonID()).getLastName() + "\n" + "DEATH: " + events.get(i).getCity() + ", " + events.get(i).getCountry() + " (" + events.get(i).getYear() + ")")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
            } else if (events.get(i).getEventType().toLowerCase().equals("marriage")) {
                LatLng eventPos = new LatLng(events.get(i).getLatitude(), events.get(i).getLongitude());
                mEvent = mMap.addMarker(new MarkerOptions()
                        .position(eventPos)
                        .snippet(people.get(events.get(i).getPersonID()).getFirstName() + " " + people.get(events.get(i).getPersonID()).getLastName() + "\n" + "MARRIAGE: " + events.get(i).getCity() + ", " + events.get(i).getCountry() + " (" + events.get(i).getYear() + ")")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
            } else if(typeToColor.get(events.get(i).getEventType().toLowerCase()) == null) {
                typeToColor.put(events.get(i).getEventType().toLowerCase(), colors.get(colorCounter));
                colorCounter++;
                LatLng eventPos = new LatLng(events.get(i).getLatitude(), events.get(i).getLongitude());
                mEvent = mMap.addMarker(new MarkerOptions()
                        .position(eventPos)
                        .snippet(people.get(events.get(i).getPersonID()).getFirstName() + " " + people.get(events.get(i).getPersonID()).getLastName() + "\n" + events.get(i).getEventType().toUpperCase() + ": " + events.get(i).getCity() + ", " + events.get(i).getCountry() + " (" + events.get(i).getYear() + ")")
                        .icon(BitmapDescriptorFactory.defaultMarker(typeToColor.get(events.get(i).getEventType().toLowerCase()))));
            } else if(typeToColor.get(events.get(i).getEventType().toLowerCase()) != null) {
                LatLng eventPos = new LatLng(events.get(i).getLatitude(), events.get(i).getLongitude());
                mEvent = mMap.addMarker(new MarkerOptions()
                        .position(eventPos)
                        .snippet(people.get(events.get(i).getPersonID()).getFirstName() + " " + people.get(events.get(i).getPersonID()).getLastName() + "\n" + events.get(i).getEventType().toUpperCase() + ": " + events.get(i).getCity() + ", " + events.get(i).getCountry() + " (" + events.get(i).getYear() + ")")
                        .icon(BitmapDescriptorFactory.defaultMarker(typeToColor.get(events.get(i).getEventType().toLowerCase()))));
            }
            mEvent.setTag(events.get(i));
            markers.add(mEvent);
        }
        //Create Marker Listener!


        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                for (Polyline polyTemp : polyLines) {
                    polyTemp.remove();
                }
                polyLines.clear();
                String temp = marker.getSnippet();
                Event e = (Event) marker.getTag();
                personID = e.getPersonID();
                mWindowText.setText(temp);

                if (dataCache.getPeople().get(personID).getGender().toLowerCase().equals("m")) {
                    Drawable maleGenderIcon = new IconDrawable(getActivity(), FontAwesomeIcons.fa_male).colorRes(R.color.maleColor).sizeDp(40);
                    mGenderImageView.setImageDrawable(maleGenderIcon);
                } else {
                    Drawable femaleGenderIcon = new IconDrawable(getActivity(), FontAwesomeIcons.fa_female).colorRes(R.color.femaleColor).sizeDp(40);
                    mGenderImageView.setImageDrawable(femaleGenderIcon);
                }

                if (dataCache.isLifeStoryLines()) {
                    ArrayList<Event> lifeStory = dataCache.getPersonToEvent().get(personID);
                    for (int i = 0; i < lifeStory.size() - 1; i++) {
                        if(dataCache.getFilteredEvents().contains(lifeStory.get(i))) {
                            LatLng latSource = new LatLng(lifeStory.get(i).getLatitude(), lifeStory.get(i).getLongitude());
                            LatLng latDestination = new LatLng(lifeStory.get(i + 1).getLatitude(), lifeStory.get(i + 1).getLongitude());
                            line = googleMap.addPolyline(new PolylineOptions().add(latSource, latDestination).color(getContext().getResources().getColor(R.color.deathColor)));
                            polyLines.add(line);
                        }
                    }
                }

                if (dataCache.isSpouseLines()) {
                    Person personTemp = dataCache.getPeople().get(e.getPersonID());
                    if(personTemp.getGender().toLowerCase().equals("m") && dataCache.isFemaleEvents() || personTemp.getGender().toLowerCase().equals("f") && dataCache.isMaleEvents()) {
                        if (personTemp.getSpouseID() != null) {
                            Person spouse = dataCache.getPeople().get(personTemp.getSpouseID());
                            ArrayList<Event> spouseEvents = dataCache.getPersonToEvent().get(spouse.getPersonID());
                            Collections.sort(spouseEvents);
                            LatLng latSource = new LatLng(e.getLatitude(), e.getLongitude());
                            LatLng latDestination = new LatLng(spouseEvents.get(0).getLatitude(), spouseEvents.get(0).getLongitude());
                            line = googleMap.addPolyline(new PolylineOptions().add(latSource, latDestination).color(getContext().getResources().getColor(R.color.femaleColor)));
                            polyLines.add(line);
                        }
                    }
                }

                if (dataCache.isFamilyTreeLines()) {
                    Person personTemp = dataCache.getPeople().get(e.getPersonID());
                    String fatherID = personTemp.getFatherID();
                    String motherID = personTemp.getMotherID();
                    float lineSize = 20;

                    if (dataCache.isFatherSide()) {
                        mapFamilyLines(e, fatherID, lineSize, googleMap);
                    }
                    if (dataCache.isMotherSide()) {
                        mapFamilyLines(e, motherID, lineSize, googleMap);
                    }

                }
                return false;
            }
        });


        mInfoWindow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mWindowText.getText().equals("Click on a marker to Display info!")) {
                    //Create Event Activity
                    MainActivity main = (MainActivity) getActivity();
                    main.startPersonActivity(personID);
                }
            }
        });


        if (!fromMain) {
            EventActivity activity = (EventActivity) getActivity();
            Event event = activity.getEvent();

            LatLng eventPos = new LatLng(event.getLatitude(), event.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLng(eventPos));
            for (Marker marker : markers) {
                Event temp;
                temp = (Event) marker.getTag();
                if (temp.getPersonID().equals(event.getPersonID()) && temp.getEventName().equals(event.getEventName())) {
                    markerClicked(marker);
                    if (dataCache.getPeople().get(personID).getGender().toLowerCase().equals("m")) {
                        Drawable maleGenderIcon = new IconDrawable(getActivity(), FontAwesomeIcons.fa_male).colorRes(R.color.maleColor).sizeDp(40);
                        mGenderImageView.setImageDrawable(maleGenderIcon);
                    } else {
                        Drawable femaleGenderIcon = new IconDrawable(getActivity(), FontAwesomeIcons.fa_female).colorRes(R.color.femaleColor).sizeDp(40);
                        mGenderImageView.setImageDrawable(femaleGenderIcon);
                    }

                }
            }
        }

    }


    private void mapFamilyLines(Event event, String personID, float lineWidth, GoogleMap googleMap) {
        if (personID != null) {
            ArrayList<Event> temp = dataCache.getPersonToEvent().get(personID);
            if (!temp.isEmpty() && dataCache.getFilteredEvents().contains(temp.get(0))) {
                Collections.sort(temp);
                Event parentEvent = temp.get(0);
                LatLng latSource = new LatLng(event.getLatitude(), event.getLongitude());
                LatLng latDestination = new LatLng(parentEvent.getLatitude(), parentEvent.getLongitude());
                line = googleMap.addPolyline(new PolylineOptions().
                        add(latSource, latDestination).color(getContext().
                        getResources().getColor(R.color.maleColor))
                        .width(lineWidth));
                polyLines.add(line);
                Person personTemp = dataCache.getPeople().get(personID);
                String fatherID = personTemp.getFatherID();
                String motherID = personTemp.getMotherID();
                float percent = .6f;

                if(dataCache.isFatherSide()) {
                    mapFamilyLines(temp.get(0), fatherID, lineWidth * percent, googleMap);
                }
                if(dataCache.isMotherSide()) {
                    mapFamilyLines(temp.get(0), motherID, lineWidth * percent, googleMap);
                }

            }
        }
        return;
    }

    private void markerClicked(Marker marker) {
        for (Polyline polyTemp : polyLines) {
            polyTemp.remove();
        }
        polyLines.clear();
        String temp = marker.getSnippet();
        Event e = (Event) marker.getTag();
        personID = e.getPersonID();
        mWindowText.setText(temp);

        mInfoWindow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mWindowText.getText().equals("Click on a marker to Display info!")) {
                    //Create Event Activity
                    EventActivity event = (EventActivity) getActivity();
                    event.startPersonActivity(personID);
                }
            }
        });
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        if (fromMain) {
            inflater.inflate(R.menu.fragment_maps, menu);

            //ActionBar icon(s)
            menu.findItem(R.id.searchMenuItem).setIcon(
                    new IconDrawable(getActivity(), FontAwesomeIcons.fa_search)
                            .colorRes(R.color.toolbarIcon)
                            .actionBarSize());

            menu.findItem(R.id.settingsMenuItem).setIcon(
                    new IconDrawable(getActivity(), FontAwesomeIcons.fa_gear)
                            .colorRes(R.color.toolbarIcon)
                            .actionBarSize());
        } else {
            inflater.inflate(R.menu.fragment_maps, menu);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = null;

        if (item.getItemId() == R.id.searchMenuItem) {
            intent = new Intent(getActivity(), SearchActivity.class);
            startActivity(intent);
            return true;

        } else if (item.getItemId() == R.id.settingsMenuItem) {
            intent = new Intent(getActivity(), SettingsActivity.class);
            startActivity(intent);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}
