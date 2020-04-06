package Results;

import Model.Event;

import java.util.ArrayList;

public class EventResult extends Result {
    ArrayList<Event> data;

    public EventResult(ArrayList<Event> data) {
        this.data= data;
    }

    public ArrayList<Event> getData() {
        return data;
    }
}
