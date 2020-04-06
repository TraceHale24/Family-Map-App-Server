package Services.Tests;

import Dao.*;

import Model.Event;
import Model.Person;
import Model.User;

import Request.LoadReq;
import Results.Result;
import Services.LoadService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertTrue;


public class LoadServiceTest {
    private Database db;
    private User user = new User("fakeUsername", "fakeUsername", "T", "H", "M", "Dad", "fakeID");
    private Event fakeEvent = new Event("fakeEventID", "fakeUsername", "fakeID", 120, 150, "Fake Country", "Fake City", "Fake", 2018);
    private Event fakeEvent2 = new Event("fakeEventID2", "fakeUsername", "fakeID", 120, 150, "Fake Country", "Fake City", "Fake", 2018);
    private Event fakeEvent3 = new Event("fakeEventID3", "fakeUsername", "fakeID", 120, 150, "Fake Country", "Fake City", "Fake", 2018);
    Person fakePerson = new Person("fakeID", "fakeUsername", "Fake", "LastName", "M", "Fake-Dad", "Fake-Mom", "No Spouse");
    Person fakePerson2 = new Person("fakeIdTwo", "fakeOne", "Fake", "LastName", "M", "Fake-Dad", "Fake-Mom", "No Spouse");
    Person fakePerson3 = new Person("fakeIdThree", "fakeTwo", "Fake", "LastName", "M", "Fake-Dad", "Fake-Mom", "No Spouse");


    @BeforeEach
    public void setUp() throws Exception {

        //lets create a new database
        db = new Database();
        Connection conn = db.openConnection();
    }

    @AfterEach
    public void tearDown() throws Exception {
        //here we can get rid of anything from our tests we don't want to affect the rest of our program
        //lets clear the tables so that any data we entered for testing doesn't linger in our files
        db.openConnection();
        db.clearTables();
        db.closeConnection(true);
    }

    @Test
    public void LoadTestPass() {
        boolean success = false;
        try {
            Connection conn = db.openConnection();
            EventDAO eDao = new EventDAO(conn);
            PersonDAO pDao = new PersonDAO(conn);
            UserDAO uDao = new UserDAO(conn);
            LoadService service = new LoadService();
            ArrayList<User> users = new ArrayList<>();
            users.add(user);
            ArrayList<Person> people = new ArrayList<>();
            people.add(fakePerson);
            people.add(fakePerson2);
            people.add(fakePerson3);
            ArrayList<Event> events = new ArrayList<>();
            events.add(fakeEvent);
            events.add(fakeEvent2);
            events.add(fakeEvent3);
            LoadReq request = new LoadReq();
            request.setEvents(events);
            request.setPersons(people);
            request.setUsers(users);

            Result r = service.load(request);

            if(r.isSuccess() && eDao.getEvent(fakeEvent.getEventName()) != null && pDao.getPerson(fakePerson2.getPersonID()) != null && uDao.getUser(user.getUserName()) != null) {
                success = true;
            }
            eDao.clear();
            pDao.clear();
            uDao.clear();
            db.closeConnection(true);
            conn.close();
        } catch (DataAccessException | SQLException e) {
            e.printStackTrace();
        }
        assertTrue(success);
    }

    @Test
    public void LoadTestFail() {
        boolean success = true;


    }
}
