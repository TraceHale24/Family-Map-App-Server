package Results;

import Model.Person;

import java.util.ArrayList;

public class PersonResult extends Result {
    ArrayList<Person> data;

    public PersonResult(ArrayList<Person> data) {
        this.data = data;
    }

    public ArrayList<Person> getData() {
        return data;
    }
//public void setPersonArray(ArrayList<Person> personArray) {
        //this.personArray = personArray;
    //}
}
