package Services.Tests;

import Dao.*;
import Model.AuthorizationToken;
import Model.Event;
import Model.Person;
import Model.User;
import Results.Result;
import Services.ClearService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ClearServiceTest {
    private Database db;

    Person fakePerson = new Person("fakeID", "fakeUsername", "Fake", "LastName", "M", "Fake-Dad", "Fake-Mom", "No Spouse");
    User fakeUser = new User("fakeUsername", "password", "email@fake.com", "Trace", "Hale", "M", "bad");
    Event fakeEvent = new Event("fakeEventID", "fakeUsername", "fakeID", 120, 150, "Fake Country", "Fake City", "Fake", 2018);
    AuthorizationToken fakeToken = new AuthorizationToken("This is a fake token", "fakeUsername");


    @BeforeEach
    public void setUp() throws Exception {

        //lets create a new database
        db = new Database();
        Connection conn = db.openConnection();

        //Create Fake Data here

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
    public void clearSuccess() throws DataAccessException {
        boolean success = false;
        try {
            Connection conn = db.openConnection();
            EventDAO eDao = new EventDAO(conn);
            PersonDAO pDao = new PersonDAO(conn);
            AuthorizationTokenDAO aDao = new AuthorizationTokenDAO(conn);
            UserDAO uDao = new UserDAO(conn);
            eDao.addEvent(fakeEvent);
            pDao.addPerson(fakePerson);
            uDao.addUser(fakeUser);
            aDao.addAuthToken(fakeToken);
            db.closeConnection(true);

            ClearService clear = new ClearService();
            Result r = clear.clear();

            if(r.isSuccess()) {
                success = true;
            }


        } catch (DataAccessException | SQLException e) {
            db.closeConnection(false);
        }

        assertTrue(success);
    }

    @Test
    public void clearFail() throws DataAccessException {
        boolean success = true;
        try {
            Connection conn = db.openConnection();
            EventDAO eDao = new EventDAO(conn);
            PersonDAO pDao = new PersonDAO(conn);
            AuthorizationTokenDAO aDao = new AuthorizationTokenDAO(conn);
            UserDAO uDao = new UserDAO(conn);
            eDao.addEvent(fakeEvent);
            pDao.addPerson(fakePerson);
            uDao.addUser(fakeUser);
            aDao.addAuthToken(fakeToken);
            db.closeConnection(true);

            ClearService clear = new ClearService();
            clear.clear();

            conn = db.openConnection();
            EventDAO newEDao = new EventDAO(conn);
            Event newEvent = newEDao.getEvent("fakeEventID");
            if(newEvent == null) {
                success = false;
            }
            db.closeConnection(true);

        } catch (DataAccessException | SQLException e) {
            db.closeConnection(false);
        }

        assertFalse(success);
    }
}
