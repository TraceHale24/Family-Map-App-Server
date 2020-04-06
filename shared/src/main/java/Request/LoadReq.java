package Request;

import Model.Event;
import Model.Person;
import Model.User;

import java.util.ArrayList;

public class LoadReq {
    private ArrayList<User> users;
    private ArrayList<Person> persons;
    private ArrayList<Event> events;

    public ArrayList<Event> getEventArray() {
        return events;
    }

    public ArrayList<User> getUserArray() {
        return users;
    }

    public ArrayList<Person> getPersonArray() {
        return persons;
    }

    public void setUsers(ArrayList<User> users) {
        this.users = users;
    }

    public void setPersons(ArrayList<Person> persons) {
        this.persons = persons;
    }

    public void setEvents(ArrayList<Event> events) {
        this.events = events;
    }
}
