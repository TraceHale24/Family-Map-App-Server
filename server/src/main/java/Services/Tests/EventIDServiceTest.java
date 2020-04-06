package Services.Tests;

import Dao.*;
import Model.AuthorizationToken;
import Model.Event;
import Model.Person;
import Model.User;
import Results.Result;
import Services.ClearService;
import Services.EventIDService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EventIDServiceTest {
    private Database db;
    Event fakeEvent = new Event("fakeEventID", "fakeUsername", "fakeID", 120, 150, "Fake Country", "Fake City", "Fake", 2018);


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
    public void EventIDSuccess() throws DataAccessException {
        boolean success = false;
        try {
            Connection conn = db.openConnection();
            EventDAO eDao = new EventDAO(conn);
            eDao.addEvent(fakeEvent);
            db.closeConnection(true);
            conn.close();
            EventIDService service = new EventIDService();
            Result r = service.run(fakeEvent);

            if(r.isSuccess()) {
                success = true;
            }


        } catch (DataAccessException | SQLException e) {
            db.closeConnection(false);
        }

        assertTrue(success);
    }

    @Test
    public void EventIDFail() throws DataAccessException {
        boolean success = true;
        Event newEvent = new Event("secondFakeID", "fakeUsername", "fakeID", 120, 150, "Fake Country", "Fake City", "Fake", 2018);
        try {
            Connection conn = db.openConnection();
            EventDAO eDao = new EventDAO(conn);
            eDao.addEvent(fakeEvent);
            db.closeConnection(true);
            conn.close();
            EventIDService service = new EventIDService();
            Result result = service.run(newEvent);

            if(!result.isSuccess()) {
                success = false;
            }
        } catch (DataAccessException | SQLException e) {
            db.closeConnection(false);
        }

        assertFalse(success);
    }
}
