package com.example.familymapapp.Extras;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import Model.Event;
import Model.Person;

public class DataCache {
    private Person mainPerson;
    private static DataCache instance;
    private ArrayList<Event> events = null;
    private Map<String, Person> people = null;
    private Map<String, Person> filteredPeople = new TreeMap<>();
    private Map<String, ArrayList<Event>> personToEvent = null;
    public Map<String, ArrayList<Event>> getPersonToEvent() {
        return personToEvent;
    }
    public ArrayList<Event> filteredEvents = null;
    public Set<Event> fatherEvents = null;
    public Set<Event> mothersEvents = null;
    public Set<Event> maleEventsSet = null;
    public Set<Event> femaleEventsSet = null;



    private boolean lifeStoryLines = true;
    private boolean familyTreeLines = true;
    private boolean spouseLines = true;
    private boolean fatherSide = true;
    private boolean motherSide = true;
    private boolean maleEvents = true;
    private boolean femaleEvents = true;



    public Map<String, Person> getFilteredPeople() {
        if(filteredEvents != null) {
            for (Event event : filteredEvents) {
                filteredPeople.put(event.getPersonID(), people.get(event.getPersonID()));
            }
        }
        return filteredPeople;
    }

    public ArrayList<Event> getFilteredEvents() {
        return filteredEvents;
    }

    public Person getMainPerson() {
        return mainPerson;
    }

    public void setMainPerson(Person mainPerson) {
        this.mainPerson = mainPerson;
    }

    public boolean isLifeStoryLines() {
        return lifeStoryLines;
    }

    public void setLifeStoryLines(boolean lifeStoryLines) {
        this.lifeStoryLines = lifeStoryLines;
    }

    public boolean isFamilyTreeLines() {
        return familyTreeLines;
    }

    public void setFamilyTreeLines(boolean familyTreeLines) {
        this.familyTreeLines = familyTreeLines;
    }

    public boolean isSpouseLines() {
        return spouseLines;
    }

    public void setSpouseLines(boolean spouseLines) {
        this.spouseLines = spouseLines;
    }

    public boolean isFatherSide() {
        return fatherSide;
    }

    public void setFatherSide(boolean fatherSide) {
        if(!fatherSide) {
            Set set = new HashSet<Event>(filteredEvents);
            if(mainPerson.getFatherID() != null) {
                for(Event event : personToEvent.get(mainPerson.getFatherID())) {
                    fatherEvents.add(event);
                }
                getEventsFather(mainPerson.getFatherID());
            }
            set.removeAll(fatherEvents);
            filteredEvents = new ArrayList<Event>(set);
        }
        else {
            Set set = new HashSet<Event>(filteredEvents);
            set.addAll(fatherEvents);
            filteredEvents = new ArrayList<Event>(set);
        }
        this.fatherSide = fatherSide;
    }

    public void getEventsFather(String personID) {
        if(personID != null) {
            if(people.get(personID).getFatherID() != null) {
                for(Event event : personToEvent.get(people.get(personID).getFatherID())) {
                    fatherEvents.add(event);
                }
                getEventsFather(people.get(personID).getFatherID());
            }
            if(people.get(personID).getMotherID() != null) {
                for(Event event : personToEvent.get(people.get(personID).getMotherID())) {
                    fatherEvents.add(event);
                }
                getEventsFather(people.get(personID).getMotherID());
            }
        }
        else {
            return;
        }
    }

    public void getEventsMother(String personID) {
        if(personID != null) {
            if(people.get(personID).getFatherID() != null) {
                for(Event event : personToEvent.get(people.get(personID).getFatherID())) {
                    mothersEvents.add(event);
                }
                getEventsFather(people.get(personID).getFatherID());
            }
            if(people.get(personID).getMotherID() != null) {
                for(Event event : personToEvent.get(people.get(personID).getMotherID())) {
                    mothersEvents.add(event);
                }
                getEventsMother(people.get(personID).getMotherID());
            }
        }
        else {
            return;
        }
    }

    public boolean isMotherSide() {
        return motherSide;
    }

    public void setMotherSide(boolean motherSide) {
        if(!motherSide) {
            Set set = new HashSet<Event>(filteredEvents);
            if(mainPerson.getMotherID() != null) {
                for(Event event : personToEvent.get(mainPerson.getMotherID())) {
                    mothersEvents.add(event);
                }
                getEventsMother(mainPerson.getMotherID());
            }
            set.removeAll(mothersEvents);
            filteredEvents = new ArrayList<Event>(set);
        }
        else {
            Set set = new HashSet<Event>(filteredEvents);
            set.addAll(mothersEvents);
            filteredEvents = new ArrayList<Event>(set);
        }
        this.motherSide = motherSide;
    }

    public boolean isMaleEvents() {
        return maleEvents;
    }

    public void setMaleEvents(boolean maleEvents) {
        if(!maleEvents) {
            Set set = new HashSet<Event>(filteredEvents);
            if(mainPerson.getGender().toLowerCase().equals("m")) {
                for(Event event : personToEvent.get(mainPerson.getPersonID())) {
                    maleEventsSet.add(event);
                }
            }
            recurseGenderMale(mainPerson.getPersonID());
            set.removeAll(maleEventsSet);
            filteredEvents = new ArrayList<Event>(set);
        }
        else {
            Set set = new HashSet<Event>(filteredEvents);
            set.addAll(maleEventsSet);
            filteredEvents = new ArrayList<Event>(set);
        }
        this.maleEvents = maleEvents;
    }

    public void recurseGenderMale(String personID) {
        if(personID != null) {
            if(people.get(personID).getFatherID() != null) {
                for(Event event : personToEvent.get(people.get(personID).getFatherID())) {
                    maleEventsSet.add(event);
                }
                recurseGenderMale(people.get(personID).getFatherID());
            }
            if(people.get(personID).getMotherID() != null) {
                recurseGenderMale(people.get(personID).getMotherID());
            }
        }
    }

    public boolean isFemaleEvents() {
        return femaleEvents;
    }

    public void setFemaleEvents(boolean femaleEvents) {
        if(!femaleEvents) {
            Set set = new HashSet<Event>(filteredEvents);
            if(mainPerson.getGender().toLowerCase().equals("f")) {
                for(Event event : personToEvent.get(mainPerson.getPersonID())) {
                    femaleEventsSet.add(event);
                }
            }
            recurseGenderFemale(mainPerson.getPersonID());

            set.removeAll(femaleEventsSet);
            filteredEvents = new ArrayList<Event>(set);
        }
        else {
            Set set = new HashSet<Event>(filteredEvents);
            set.addAll(femaleEventsSet);
            filteredEvents = new ArrayList<Event>(set);
        }
        this.femaleEvents = femaleEvents;
    }

    public void recurseGenderFemale(String personID) {
        if(personID != null) {
            if(people.get(personID).getMotherID() != null) {
                for(Event event : personToEvent.get(people.get(personID).getMotherID())) {
                    femaleEventsSet.add(event);
                }
                recurseGenderFemale(people.get(personID).getMotherID());
            }
            if(people.get(personID).getFatherID() != null) {
                recurseGenderFemale(people.get(personID).getFatherID());
            }
        }
    }

    public static DataCache getInstance() {
        if (instance == null) {
            instance = new DataCache();
        }
        return instance;
    }

    private DataCache() {
        people = new TreeMap<>();
        events = new ArrayList<>();
        personToEvent = new TreeMap<>();
        filteredEvents = new ArrayList<>();
        fatherEvents = new HashSet<>();
        mothersEvents = new HashSet<>();
        maleEventsSet = new HashSet<>();
        femaleEventsSet = new HashSet<>();
    }

    public ArrayList<Event> getEvents() {
        return events;
    }

    public void setEvents(ArrayList<Event> events) {

        for (int i = 0; i < events.size(); i++) {
            ArrayList<Event> temp = new ArrayList<>();
            if(personToEvent.get(events.get(i).getPersonID()) != null) {
                temp = personToEvent.get(events.get(i).getPersonID());
            }
            temp.add(events.get(i));
            personToEvent.put(events.get(i).getPersonID(), temp);
        }
        this.events = events;
        this.filteredEvents = events;
    }

    public Map<String, Person> getPeople() {
        return people;
    }

    public void setPeople(ArrayList<Person> eventList) {
        for (int i = 0; i < eventList.size(); i++) {
            people.put(eventList.get(i).getPersonID(), eventList.get(i));
        }
    }

    public void clear() {
        personToEvent.clear();
        events.clear();
        people.clear();

    }

}
